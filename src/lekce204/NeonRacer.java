package lekce204;

import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * NEON RACER: DESTRUCTION (FIXED)
 * - Opraven NullPointerException u policejních majáků.
 * - Plně funkční fyzika a destrukce.
 */
public class NeonRacer extends Application {

    // --- KONFIGURACE ---
    private static final int INITIAL_WIDTH = 1200;
    private static final int INITIAL_HEIGHT = 800;
    private static final double TRACK_WIDTH = 1200;
    private static final double TRACK_LENGTH = 40000;
    
    private enum GameState { MENU, PLAYING, BUSTED, WRECKED, FINISHED }
    private enum ObstacleType { ROAD, STONE, BARREL, WALL }

    private GameState currentState = GameState.MENU;

    // --- DATA HRÁČE ---
    private double speed = 0;
    private double maxSpeed = 190;
    private boolean left, right, up, down;
    
    private int damagePercent = 0;

    // --- POLICIE ---
    private Group policeCarGroup;
    private double policeSpeed = 0;
    private static final double POLICE_START_DIST = 2000; 
    
    // --- GRAFIKA ---
    // Tyto proměnné způsobovaly chybu, teď jsou správně inicializovány
    private Box lightRed, lightBlue; 
    private PhongMaterial matRedOn, matRedOff, matBlueOn, matBlueOff;
    private Group worldRoot = new Group();
    private Group playerCarGroup;
    private Box chassis; 
    private PerspectiveCamera camera;
    private List<PhysicsObject> obstacles = new ArrayList<>();
    private SubScene subScene;
    
    private long raceStartTime = 0;
    private long lastDamageTime = 0;

    // --- UI ---
    private Label lblSpeed, lblStatus;
    private ProgressBar healthBar, policeBar;
    private VBox menuOverlay, gameOverOverlay;
    private Label lblGameOverTitle;
    private Button btnRestart;

    private class PhysicsObject {
        Node node;
        ObstacleType type;
        double originalX;
        boolean isMoving;
        boolean isHit = false;
        double flyVelX, flyVelY, flyVelZ, rotVel;

        PhysicsObject(Node node, ObstacleType type, double x, boolean move) {
            this.node = node; this.type = type; this.originalX = x; this.isMoving = move;
        }
    }

    @Override
    public void start(Stage stage) {
        initMaterials(); // Důležité: Inicializace barev
        create3DWorld();
        BorderPane uiLayer = createInterface();

        StackPane root = new StackPane(subScene, uiLayer);
        root.setStyle("-fx-background-color: black;");

        subScene.widthProperty().bind(root.widthProperty());
        subScene.heightProperty().bind(root.heightProperty());

        Scene scene = new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT);
        setupControls(scene);

        stage.setTitle("Neon Racer: Destruction Fixed");
        stage.setScene(scene);
        stage.show();

        new GameLoop().start();
    }

    private void initMaterials() {
        matRedOn = new PhongMaterial(Color.RED); matRedOn.setSpecularColor(Color.WHITE);
        matRedOff = new PhongMaterial(Color.DARKRED);
        matBlueOn = new PhongMaterial(Color.BLUE); matBlueOn.setSpecularColor(Color.WHITE);
        matBlueOff = new PhongMaterial(Color.DARKBLUE);
    }

    private void setupControls(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (currentState != GameState.PLAYING) return;
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) left = true;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) right = true;
            if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) up = true;
            if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) down = true;
        });
        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) left = false;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) right = false;
            if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) up = false;
            if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) down = false;
        });
    }

    private void create3DWorld() {
        camera = new PerspectiveCamera(true);
        camera.setNearClip(1);
        camera.setFarClip(15000);
        camera.setFieldOfView(60);

        // Hráč
        playerCarGroup = createCarModel(Color.CYAN, false);
        playerCarGroup.setTranslateY(-15);
        worldRoot.getChildren().add(playerCarGroup);

        // Policie
        policeCarGroup = createCarModel(Color.BLACK, true);
        policeCarGroup.setTranslateY(-15);
        policeCarGroup.setTranslateZ(-POLICE_START_DIST);
        worldRoot.getChildren().add(policeCarGroup);
        
        AmbientLight ambient = new AmbientLight(Color.rgb(140, 140, 140));
        PointLight playerLight = new PointLight(Color.WHITE);
        playerLight.translateXProperty().bind(playerCarGroup.translateXProperty());
        playerLight.translateYProperty().bind(playerCarGroup.translateYProperty().subtract(200));
        playerLight.translateZProperty().bind(playerCarGroup.translateZProperty());

        worldRoot.getChildren().addAll(ambient, playerLight);

        subScene = new SubScene(worldRoot, INITIAL_WIDTH, INITIAL_HEIGHT, true, SceneAntialiasing.DISABLED);
        subScene.setFill(Color.rgb(10, 10, 15));
        subScene.setCamera(camera);
    }

    private Group createCarModel(Color color, boolean isPolice) {
        Group car = new Group();
        PhongMaterial bodyMat = new PhongMaterial(color);
        bodyMat.setSpecularColor(Color.WHITE);

        Box podvozek = new Box(60, 15, 110);
        podvozek.setMaterial(bodyMat);
        podvozek.setTranslateY(-8);
        if(!isPolice) this.chassis = podvozek; 

        Box kabina = new Box(50, 12, 60);
        kabina.setMaterial(new PhongMaterial(Color.rgb(20, 20, 20)));
        kabina.setTranslateY(-22);
        kabina.setTranslateZ(-5);

        PhongMaterial wheelMat = new PhongMaterial(Color.DARKGRAY);
        Cylinder w1 = createWheel(wheelMat); w1.setTranslateX(-32); w1.setTranslateZ(35);
        Cylinder w2 = createWheel(wheelMat); w2.setTranslateX(32); w2.setTranslateZ(35);
        Cylinder w3 = createWheel(wheelMat); w3.setTranslateX(-32); w3.setTranslateZ(-35);
        Cylinder w4 = createWheel(wheelMat); w4.setTranslateX(32); w4.setTranslateZ(-35);

        car.getChildren().addAll(podvozek, kabina, w1, w2, w3, w4);

        if (isPolice) {
            Box lb = new Box(40, 5, 10); 
            lb.setMaterial(new PhongMaterial(Color.WHITE)); 
            lb.setTranslateY(-30);
            
            // --- ZDE BYLA CHYBA (OPRAVENO) ---
            // Musíme přiřadit vytvořené boxy do třídních proměnných lightRed a lightBlue
            lightRed = new Box(15, 8, 8); 
            lightRed.setMaterial(matRedOff); 
            lightRed.setTranslateX(-15); 
            lightRed.setTranslateY(-30);
            
            lightBlue = new Box(15, 8, 8); 
            lightBlue.setMaterial(matBlueOff); 
            lightBlue.setTranslateX(15); 
            lightBlue.setTranslateY(-30);
            
            car.getChildren().addAll(lb, lightRed, lightBlue);
        } else {
            Box neon = new Box(50, 2, 90);
            neon.setMaterial(new PhongMaterial(color));
            neon.setEffect(new Glow(0.8));
            neon.setTranslateY(2);
            car.getChildren().add(neon);
        }
        return car;
    }

    private Cylinder createWheel(PhongMaterial mat) {
        Cylinder wheel = new Cylinder(16, 14);
        wheel.setMaterial(mat);
        wheel.setRotationAxis(Rotate.Z_AXIS);
        wheel.setRotate(90);
        return wheel;
    }

    private BorderPane createInterface() {
        BorderPane layout = new BorderPane();
        layout.setPickOnBounds(false);

        VBox topHud = new VBox(5);
        topHud.setAlignment(Pos.CENTER);
        topHud.setPadding(new javafx.geometry.Insets(20));

        lblSpeed = new Label("0 km/h");
        lblSpeed.setStyle("-fx-text-fill: cyan; -fx-font-size: 32px; -fx-font-weight: bold;");

        HBox healthBox = new HBox(10);
        healthBox.setAlignment(Pos.CENTER);
        Label lblH = new Label("HEALTH:"); lblH.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        healthBar = new ProgressBar(1.0);
        healthBar.setPrefWidth(250);
        healthBar.setStyle("-fx-accent: cyan;");
        healthBox.getChildren().addAll(lblH, healthBar);

        HBox policeBox = new HBox(10);
        policeBox.setAlignment(Pos.CENTER);
        Label lblP = new Label("POLICE:"); lblP.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        policeBar = new ProgressBar(1.0);
        policeBar.setPrefWidth(250);
        policeBar.setStyle("-fx-accent: green;");
        policeBox.getChildren().addAll(lblP, policeBar);
        
        lblStatus = new Label("");
        lblStatus.setStyle("-fx-text-fill: red; -fx-font-size: 24px; -fx-font-weight: bold;");

        topHud.getChildren().addAll(lblSpeed, healthBox, policeBox, lblStatus);
        layout.setTop(topHud);

        menuOverlay = createMenuBox();
        gameOverOverlay = createGameOverBox();
        gameOverOverlay.setVisible(false);

        StackPane centerStack = new StackPane(menuOverlay, gameOverOverlay);
        centerStack.setPickOnBounds(false);
        layout.setCenter(centerStack);

        return layout;
    }

    private VBox createMenuBox() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(0,0,0,0.85);");
        
        Label title = new Label("NEON RACER");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 60px; -fx-font-weight: bold;");
        Label sub = new Label("SMASH & RUN");
        sub.setStyle("-fx-text-fill: orange; -fx-font-size: 24px;");
        
        Button btn = createStyledButton("START ENGINE", Color.CYAN);
        btn.setOnAction(e -> startGame());
        
        box.getChildren().addAll(title, sub, btn);
        return box;
    }

    private VBox createGameOverBox() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(0,0,0,0.9);");
        
        lblGameOverTitle = new Label("");
        lblGameOverTitle.setStyle("-fx-text-fill: white; -fx-font-size: 60px; -fx-font-weight: bold;");
        
        btnRestart = createStyledButton("RESTART", Color.WHITE);
        btnRestart.setOnAction(e -> resetToMenu());
        
        box.getChildren().addAll(lblGameOverTitle, btnRestart);
        return box;
    }

    private Button createStyledButton(String text, Color c) {
        Button btn = new Button(text);
        String hex = String.format("#%02x%02x%02x", (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
        btn.setStyle("-fx-background-color: transparent; -fx-border-color: " + hex + "; -fx-border-width: 3px; -fx-text-fill: " + hex + "; -fx-font-size: 22px; -fx-padding: 10 40; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + hex + "; -fx-text-fill: black; -fx-border-color: " + hex + "; -fx-border-width: 3px; -fx-font-size: 22px; -fx-padding: 10 40;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-border-color: " + hex + "; -fx-border-width: 3px; -fx-text-fill: " + hex + "; -fx-font-size: 22px; -fx-padding: 10 40;"));
        return btn;
    }

    private void startGame() {
        currentState = GameState.PLAYING;
        menuOverlay.setVisible(false);
        gameOverOverlay.setVisible(false);
        
        worldRoot.getChildren().removeAll(obstacles.stream().map(o -> o.node).toArray(Node[]::new));
        obstacles.clear();
        
        worldRoot.getChildren().remove(playerCarGroup);
        playerCarGroup = createCarModel(Color.CYAN, false);
        playerCarGroup.setTranslateY(-15);
        worldRoot.getChildren().add(playerCarGroup);
        
        playerCarGroup.setTranslateZ(0);
        playerCarGroup.setTranslateX(0);
        playerCarGroup.setRotate(0);
        speed = 0;
        damagePercent = 0;
        healthBar.setProgress(1.0);
        healthBar.setStyle("-fx-accent: cyan;");
        lblStatus.setText("");
        
        policeCarGroup.setTranslateZ(-POLICE_START_DIST);
        policeCarGroup.setTranslateX(0);
        policeCarGroup.setRotate(0);
        policeSpeed = 0;
        
        raceStartTime = System.nanoTime();
        generateLevel();
    }

    private void resetToMenu() {
        currentState = GameState.MENU;
        menuOverlay.setVisible(true);
        gameOverOverlay.setVisible(false);
        speed = 0;
        camera.setTranslateZ(-500);
        camera.setTranslateX(0);
    }

    private void generateLevel() {
        Random rand = new Random();
        
        Box road = new Box(TRACK_WIDTH, 5, TRACK_LENGTH + 5000);
        road.setMaterial(new PhongMaterial(Color.rgb(20, 20, 25)));
        road.setTranslateY(20);
        road.setTranslateZ(TRACK_LENGTH / 2);
        
        worldRoot.getChildren().add(road);
        obstacles.add(new PhysicsObject(road, ObstacleType.ROAD, 0, false)); 

        for (int i = 0; i < 70; i++) {
            Node obsNode;
            ObstacleType type;
            boolean moving = false;
            double r = rand.nextDouble();
            
            if (r < 0.4) { 
                type = ObstacleType.STONE;
                obsNode = new Sphere(60);
                ((Sphere)obsNode).setMaterial(new PhongMaterial(Color.GRAY));
                obsNode.setTranslateY(-25);
            } else if (r < 0.75) { 
                type = ObstacleType.BARREL;
                obsNode = new Cylinder(30, 80);
                ((Cylinder)obsNode).setMaterial(new PhongMaterial(Color.YELLOW));
                obsNode.setTranslateY(-20);
                moving = true;
            } else { 
                type = ObstacleType.WALL;
                obsNode = new Box(300, 80, 20);
                ((Box)obsNode).setMaterial(new PhongMaterial(Color.MAGENTA));
                obsNode.setEffect(new Glow(0.3));
                obsNode.setTranslateY(-30);
            }

            double x = (rand.nextDouble() * (TRACK_WIDTH - 250)) - (TRACK_WIDTH / 2) + 125;
            double z = 2000 + (rand.nextDouble() * (TRACK_LENGTH - 3000));
            
            obsNode.setTranslateX(x);
            obsNode.setTranslateZ(z);
            
            worldRoot.getChildren().add(obsNode);
            obstacles.add(new PhysicsObject(obsNode, type, x, moving));
        }
    }

    private void explodeCar() {
        Random r = new Random();
        for (Node part : playerCarGroup.getChildren()) {
            TranslateTransition tt = new TranslateTransition(Duration.seconds(1.5), part);
            tt.setByX(r.nextInt(600) - 300);
            tt.setByY(-r.nextInt(500));
            tt.setByZ(r.nextInt(600) - 300);
            tt.setCycleCount(1);
            tt.play();
        }
    }

    private class GameLoop extends AnimationTimer {
        private long lastUpdate = 0;
        private boolean lightsPhase = false;

        @Override
        public void handle(long now) {
            if (currentState != GameState.PLAYING) return;

            // IMUNITA (START)
            boolean isImmune = (now - raceStartTime) < 4_000_000_000L;
            if (isImmune) lblStatus.setText("🛡️ START BOOST");
            else if (damagePercent < 100) lblStatus.setText("");

            // FYZIKA
            if (up && speed < maxSpeed) speed += 0.8;
            else if (down && speed > 0) speed -= 2.0;
            else speed *= 0.99;

            double turn = 3.0 + (speed / 30.0);
            if (left) { playerCarGroup.setTranslateX(playerCarGroup.getTranslateX() - turn); playerCarGroup.setRotate(-12); }
            else if (right) { playerCarGroup.setTranslateX(playerCarGroup.getTranslateX() + turn); playerCarGroup.setRotate(12); }
            else { playerCarGroup.setRotate(0); }

            if (playerCarGroup.getTranslateX() < -TRACK_WIDTH/2 + 60) {
                playerCarGroup.setTranslateX(-TRACK_WIDTH/2 + 60);
                if(!isImmune) { speed *= 0.9; }
            }
            if (playerCarGroup.getTranslateX() > TRACK_WIDTH/2 - 60) {
                playerCarGroup.setTranslateX(TRACK_WIDTH/2 - 60);
                if(!isImmune) { speed *= 0.9; }
            }

            playerCarGroup.setTranslateZ(playerCarGroup.getTranslateZ() + speed);

            // POLICIE
            double targetPoliceSpeed = isImmune ? speed * 0.5 : maxSpeed * 0.92; 
            if (playerCarGroup.getTranslateZ() - policeCarGroup.getTranslateZ() > 2000) targetPoliceSpeed = maxSpeed * 1.05;
            if (policeSpeed < targetPoliceSpeed) policeSpeed += 0.1;
            else policeSpeed -= 0.2;
            policeCarGroup.setTranslateZ(policeCarGroup.getTranslateZ() + policeSpeed);
            policeCarGroup.setTranslateX(policeCarGroup.getTranslateX() + (playerCarGroup.getTranslateX() - policeCarGroup.getTranslateX()) * 0.015);

            // UI
            double dist = playerCarGroup.getTranslateZ() - policeCarGroup.getTranslateZ();
            double progress = Math.max(0, Math.min(1.0, dist / 2000.0));
            policeBar.setProgress(progress);
            if (progress < 0.2) policeBar.setStyle("-fx-accent: red;");
            else policeBar.setStyle("-fx-accent: green;");

            if (!isImmune && dist <= 120) gameOver("🚨 BUSTED! 🚨", Color.RED, GameState.BUSTED);

            // OBJEKTY
            for (PhysicsObject obj : obstacles) {
                if (obj.type == ObstacleType.ROAD) continue;

                if (obj.isMoving && !obj.isHit) {
                    obj.node.setTranslateX(obj.originalX + Math.sin(now / 500_000_000.0) * 200);
                }

                if (obj.isHit && obj.type == ObstacleType.STONE) {
                    obj.node.setTranslateX(obj.node.getTranslateX() + obj.flyVelX);
                    obj.node.setTranslateY(obj.node.getTranslateY() + obj.flyVelY);
                    obj.node.setTranslateZ(obj.node.getTranslateZ() + obj.flyVelZ);
                    obj.node.setRotate(obj.node.getRotate() + obj.rotVel);
                    obj.flyVelY += 0.5; 
                    if(obj.node.getTranslateY() > 200) obj.node.setVisible(false);
                }

                if ((!obj.isHit || obj.type != ObstacleType.STONE) && 
                    Math.abs(obj.node.getTranslateZ() - playerCarGroup.getTranslateZ()) < 130) {
                    
                     if (playerCarGroup.getBoundsInParent().intersects(obj.node.getBoundsInParent())) {
                         handleCollision(obj, now);
                     }
                }
            }

            camera.setTranslateZ(playerCarGroup.getTranslateZ() - 600);
            camera.setTranslateY(playerCarGroup.getTranslateY() - 350);
            camera.setTranslateX(playerCarGroup.getTranslateX() * 0.5);
            camera.setRotationAxis(Rotate.X_AXIS);
            camera.setRotate(-22);
            lblSpeed.setText((int)speed * 2 + " km/h");

            if (playerCarGroup.getTranslateZ() >= TRACK_LENGTH) gameOver("🏆 ESCAPED! 🏆", Color.LIME, GameState.FINISHED);

            if (now - lastUpdate > 150_000_000) {
                lightsPhase = !lightsPhase;
                if (lightsPhase) { 
                    if(lightRed != null) lightRed.setMaterial(matRedOn); 
                    if(lightBlue != null) lightBlue.setMaterial(matBlueOff); 
                } else { 
                    if(lightRed != null) lightRed.setMaterial(matRedOff); 
                    if(lightBlue != null) lightBlue.setMaterial(matBlueOn); 
                }
                lastUpdate = now;
            }
        }

        private void handleCollision(PhysicsObject obj, long now) {
            if (obj.type == ObstacleType.STONE) {
                obj.isHit = true;
                obj.flyVelZ = speed * 1.5; 
                obj.flyVelY = -30; 
                obj.flyVelX = (obj.node.getTranslateX() - playerCarGroup.getTranslateX()) * 0.8;
                obj.rotVel = 20;
                return;
            }

            if (now - lastDamageTime < 500_000_000L) return;
            lastDamageTime = now;

            if (obj.type == ObstacleType.BARREL) {
                takeDamage(5);
                speed *= 0.8; 
                playerCarGroup.setTranslateZ(playerCarGroup.getTranslateZ() - 10);
                lblStatus.setText("⚠️ HIT! -5%");
            }
            else if (obj.type == ObstacleType.WALL) {
                takeDamage(50);
                speed = 0; 
                playerCarGroup.setTranslateZ(playerCarGroup.getTranslateZ() - 50);
                lblStatus.setText("⛔ CRASH! -50%");
            }
        }

        private void takeDamage(int amount) {
            damagePercent += amount;
            double health = 1.0 - (damagePercent / 100.0);
            healthBar.setProgress(Math.max(0, health));
            
            if (chassis != null) {
                Color c = Color.CYAN.interpolate(Color.RED, damagePercent / 100.0);
                ((PhongMaterial)chassis.getMaterial()).setDiffuseColor(c);
            }
            if (health < 0.5) healthBar.setStyle("-fx-accent: orange;");
            if (health < 0.25) healthBar.setStyle("-fx-accent: red;");

            if (damagePercent >= 100) {
                explodeCar();
                gameOver("🔥 WRECKED! 🔥", Color.RED, GameState.WRECKED);
            }
        }

        private void gameOver(String text, Color color, GameState state) {
            currentState = state;
            lblGameOverTitle.setText(text);
            lblGameOverTitle.setTextFill(color);
            gameOverOverlay.setVisible(true);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}