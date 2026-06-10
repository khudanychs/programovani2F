package kurz;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

public class AdvancedScene extends BasicScene {
    private final BufferedImage layer0Sky;
    private final BufferedImage layer1Mount;
    private final BufferedImage layer2Fields;
    private final BufferedImage layer3Grass;

    private final BufferedImage boarSrc;
    private final BufferedImage insectSrc;
    private final BufferedImage wheelSrc;
    private final BufferedImage flowerSrc;

    private final BufferedImage friedrichSrc;
    private final BufferedImage walterSrc;
    private final BufferedImage sagradaSrc;

    private final BufferedImage friedrichMirrored;
    private final BufferedImage friedrichRotated180;
    private final BufferedImage insectRotated90;
    private final BufferedImage wheelScaledDown;
    private final BufferedImage wheelScaledDownBright;
    private final BufferedImage flowerScaledUp;
    private final BufferedImage blurredGrass;

    private final BufferedImage walterSmall;
    private final BufferedImage walterMirrored;
    private final BufferedImage walterRotated90;
    private final BufferedImage sagradaSmall;
    private final BufferedImage boarSmall;
    private final BufferedImage boarMirrored;
    private final int fw = 200, ww = 250;
    private final int fh, wh;
    private final int sagW, sagH;

    // Pole pro animace
    private final BufferedImage[] ghostFrames;
    private final BufferedImage[] flashingFlowerFrames;

    // Pomocný bod
    private final Matrix staticOriginPoint = Matrix.point2D(0, 0);

    // Fonty
    private final Font speechFont = new Font("Arial", Font.BOLD, 14);
    private final Font actionFont = new Font("Impact", Font.BOLD, 80);

    private double time = 0.0;
    private float globalScroll = 0.0f;
    private final long appStartTime = System.currentTimeMillis();

    public static void main(String[] args) {
        show(new AdvancedScene());
    }

    public AdvancedScene() {
        super();
        setImageRootName("sprites");

        layer0Sky = toCompatibleImage(readImage("sky_background.png"));
        layer1Mount = toCompatibleImage(readImage("e_1_3_wall_3.png"));
        layer2Fields = toCompatibleImage(readImage("e_1_3_ground_7.png"));
        layer3Grass = readImage("e_1_foreground_flowers2.png");

        boarSrc = readImage("boar_ER.png");
        insectSrc = readImage("insect_RE.png");
        wheelSrc = readImage("E_1_1_object_wheel.png");
        flowerSrc = readImage("E_1_2_object_flower1.png");

        friedrichSrc = readImage("friedrich.png");
        walterSrc = readImage("walter.png");
        sagradaSrc = readImage("sagrada.png");

        fh = (friedrichSrc != null) ? (int) (200.0 * friedrichSrc.getHeight() / friedrichSrc.getWidth()) : 200;
        wh = (walterSrc != null) ? (int) (250.0 * walterSrc.getHeight() / walterSrc.getWidth()) : 250;
        walterSmall = toCompatibleImage(scaleNearest(walterSrc, (double) ww / walterSrc.getWidth(), (double) wh / walterSrc.getHeight()));
        walterMirrored = toCompatibleImage(flipHorizontal(walterSmall));
        walterRotated90 = toCompatibleImage(rotatePixel90CW(walterSmall));

        BufferedImage boarS = scaleNearest(boarSrc, 0.6, 0.6);
        boarSmall = toCompatibleImage(boarS);
        boarMirrored = toCompatibleImage(flipHorizontal(boarS));

        int skyH = (layer0Sky != null) ? layer0Sky.getHeight() : 512;
        sagH = (int) (skyH * 1.5);
        double sagAspect = (sagradaSrc != null) ? (double) sagradaSrc.getWidth() / sagradaSrc.getHeight() : 1.83;
        sagW = (int) (sagH * sagAspect);
        sagradaSmall = toCompatibleImage(scaleNearest(sagradaSrc, (double) sagW / sagradaSrc.getWidth(), (double) sagH / sagradaSrc.getHeight()));

        BufferedImage friedrichSmall = scaleNearest(friedrichSrc, (double) fw / friedrichSrc.getWidth(), (double) fh / friedrichSrc.getHeight());
        friedrichMirrored = toCompatibleImage(flipHorizontal(friedrichSmall));
        friedrichRotated180 = toCompatibleImage(rotatePixel180(friedrichSmall));

        insectRotated90 = toCompatibleImage(rotatePixel90CW(insectSrc));

        wheelScaledDown = toCompatibleImage(scaleNearest(wheelSrc, 0.5, 0.5));
        wheelScaledDownBright = toCompatibleImage(adjustColor(wheelScaledDown, 1.0, 1.8));
        flowerScaledUp = toCompatibleImage(scaleNearest(flowerSrc, 2.0, 2.0));

        BufferedImage boarHalf = scaleNearest(boarSrc, 0.5, 0.5);
        BufferedImage insectHalf = scaleNearest(insectSrc, 0.5, 0.5);
        ghostFrames = new BufferedImage[20];
        for (int i = 0; i < 20; i++) {
            double t = i / 19.0;
            ghostFrames[i] = toCompatibleImage(crossFade(boarHalf, insectHalf, t));
        }

        // Příprava 20 fází jasu květiny
        flashingFlowerFrames = new BufferedImage[20];
        for (int i = 0; i < 20; i++) {
            double progress = i / 19.0;
            flashingFlowerFrames[i] = toCompatibleImage(adjustColor(flowerSrc, 1.0, 1.0 + progress * 1.5));
        }

        blurredGrass = toCompatibleImage(boxBlur(layer3Grass, 4));

        // Časovač pro překreslování obrazovky
        Timer timer = new Timer(6, e -> {
            long now = System.currentTimeMillis();
            time = (now - appStartTime) / 1000.0;
            globalScroll = (float) (time * 62.5);
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (layer0Sky == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int viewWidth = getWidth();
        int viewHeight = getHeight();

        g.drawImage(layer0Sky, 0, 0, viewWidth, viewHeight, null);

        // Celý příběh trvá 25 sekund
        double loopTime = time % 25.0;

        // Slunce a Měsíc
        double sunAngle = (loopTime / 25.0) * Math.PI * 2.0; 
        double dayCycleStr = (Math.sin(sunAngle) + 1.0) / 2.0; // 1.0 = High Noon, 0.0 = Midnight
        
        int cx = viewWidth / 2;
        int cy = viewHeight - 200;
        double orbitRadius = viewWidth * 0.28;
        
        int sunX = cx - (int)(Math.cos(sunAngle) * orbitRadius);
        int sunY = cy - (int)(Math.sin(sunAngle) * orbitRadius);
        
        g.setColor(Color.YELLOW);
        g.fillOval(sunX - 60, sunY - 60, 120, 120);
        
        double moonAngle = sunAngle + Math.PI;
        int moonX = cx - (int)(Math.cos(moonAngle) * orbitRadius);
        int moonY = cy - (int)(Math.sin(moonAngle) * orbitRadius);
        
        g.setColor(new Color(220, 220, 255));
        g.fillOval(moonX - 40, moonY - 40, 80, 80);

        int mountOffset = (int) (globalScroll * 0.1) % layer1Mount.getWidth();
        drawParallaxLayer(g, layer1Mount, -mountOffset, viewHeight - layer1Mount.getHeight());

        int fieldsOffset = (int) (globalScroll * 0.5) % layer2Fields.getWidth();
        drawParallaxLayer(g, layer2Fields, -fieldsOffset, viewHeight - layer2Fields.getHeight() + 135);

        if (sagradaSmall != null) {
            int sx = (viewWidth - sagW) / 2;
            int sy = viewHeight - sagH - 30;
            g.drawImage(sagradaSmall, sx, sy, null);
        }

        g.drawImage(flowerScaledUp, viewWidth / 2 - 60, viewHeight - 180, null);
        for (int i = 0; i < 6; i++) {
            g.drawImage(flowerScaledUp, 200 + i * 180, viewHeight - 150 + (i % 3) * 15, null);
        }

        int grassOffset = (int) (globalScroll * 1.5) % layer3Grass.getWidth();
        drawParallaxLayer(g, blurredGrass, -grassOffset, viewHeight - layer3Grass.getHeight());

        // --- Day/Night Darkness Overlay ---
        double nightDarkness = 1.0 - dayCycleStr; // 0 = Day, 1 = Night
        if (nightDarkness > 0.05) {
            int alpha = (int)(nightDarkness * 170);
            alpha = Math.max(0, Math.min(255, alpha));
            g.setColor(new Color(0, 0, 30, alpha));
            g.fillRect(0, 0, viewWidth, viewHeight);
        }

        int groundY = viewHeight - Math.max(fh, wh) - 40;
        int friedrichBaseY = groundY + Math.max(0, wh - fh);
        int walterBaseY = groundY + Math.max(0, fh - wh);

        Matrix friedrichOrigin = Matrix.point2D(-fw, friedrichBaseY);
        Matrix walterOrigin = Matrix.point2D(viewWidth - ww - 50, walterBaseY);

        Matrix friedrichPos = friedrichOrigin;
        Matrix walterPos = walterOrigin;

        String speech = null;
        String walterSpeech = null;
        String boarSpeech = null;
        String actionText = null;

        double enterTime = 4.0;
        double speakTime = 10.0; // Sunset
        double punchTime = 11.0;
        double fFlyTime = 15.0; 
        double boarEnterTime = 12.0;
        double walterTurnTime = 14.0;
        double boarSpeakTime = 16.0;
        double boarKickTime = 20.0;
        double walterFlyTime = 20.5;
        double resetTime = 23.0;

        boolean walterTurns = false;
        boolean walterFlies = false;
        Matrix boarPos = Matrix.translate2D(viewWidth + 500, groundY + 20).multiply(staticOriginPoint);

        if (loopTime < enterTime) {
            double moveX = (loopTime / enterTime) * (viewWidth / 3.0 + fw);
            friedrichPos = Matrix.translate2D(moveX, 0).multiply(friedrichOrigin);
        } else if (loopTime < speakTime) {
            friedrichPos = Matrix.translate2D(viewWidth / 3.0 + fw, 0).multiply(friedrichOrigin);
            speech = "God is dead! And we have killed him!";
        } else if (loopTime < punchTime) {
            friedrichPos = Matrix.translate2D(viewWidth / 3.0 + fw, 0).multiply(friedrichOrigin);
            double progress = (loopTime - speakTime) / (punchTime - speakTime);
            double targetX = (int)(viewWidth / 3.0) + fw;
            double dx = -(viewWidth - ww - 50 - targetX) * progress;
            double dy = -Math.sin(progress * Math.PI) * 100;
            walterPos = Matrix.translate2D(dx, dy).multiply(walterOrigin);
        } else if (loopTime < walterFlyTime) {
            double targetX = (int)(viewWidth / 3.0) + fw;
            walterPos = Matrix.translate2D(-(viewWidth - ww - 50 - targetX), 0).multiply(walterOrigin);

            if (loopTime < fFlyTime) {
                double flight = loopTime - punchTime;
                double flyX = -flight * 800;
                double flyY = -(flight * 600 - 0.5 * 2000 * flight * flight);
                Matrix toOrigin = Matrix.translate2D(-fw / 2.0, -fh / 2.0);
                Matrix rotate = Matrix.rotate2D(flight * 15);
                Matrix fromOrigin = Matrix.translate2D(fw / 2.0, fh / 2.0);
                Matrix fly = Matrix.translate2D(viewWidth / 3.0 + fw + flyX, friedrichBaseY + flyY);
                friedrichPos = fly.multiply(fromOrigin).multiply(rotate).multiply(toOrigin).multiply(staticOriginPoint);
            } else {
                friedrichPos = Matrix.translate2D(-1000, -1000).multiply(staticOriginPoint);
            }
            if (loopTime < punchTime + 1.0) {
                actionText = "POW!!!";
            }
        } else {
            friedrichPos = Matrix.translate2D(-1000, -1000).multiply(staticOriginPoint);
            double flight = loopTime - walterFlyTime;
            double flyX = -flight * 1200;
            double flyY = -(flight * 400 - 0.5 * 1500 * flight * flight);
            double targetX = (int)(viewWidth / 3.0) + fw;
            Matrix wStand = Matrix.translate2D(-(viewWidth - ww - 50 - targetX), 0);
            Matrix wOriginPos = wStand.multiply(walterOrigin);
            walterPos = Matrix.translate2D(flyX, flyY).multiply(wOriginPos);
            walterFlies = true;

            if (loopTime < walterFlyTime + 1.0) {
                actionText = "BAM!!!";
            }
        }

        if (loopTime >= boarEnterTime && loopTime < resetTime) {
            double bTargetX = (viewWidth / 3.0) + fw + ww + 50;
            double bStartX = viewWidth + 200;
            
            if (loopTime < walterTurnTime) {
                double progress = (loopTime - boarEnterTime) / (walterTurnTime - boarEnterTime);
                double bx = bStartX - (bStartX - bTargetX) * progress;
                // Category 10: Pohyb po křivce (běh prasete skákáním)
                double by = (groundY + 20) - Math.abs(Math.sin(progress * Math.PI * 6)) * 40;
                boarPos = Matrix.translate2D(bx, by).multiply(staticOriginPoint);
            } else if (loopTime < boarKickTime) {
                boarPos = Matrix.translate2D(bTargetX, groundY + 20).multiply(staticOriginPoint);
                walterTurns = true;
                if (loopTime < boarSpeakTime) {
                    walterSpeech = "Say my name!";
                } else {
                    boarSpeech = "Hei... Heisenberg?... I dont care!";
                }
            } else if (loopTime < walterFlyTime) {
                double progress = (loopTime - boarKickTime) / (walterFlyTime - boarKickTime);
                double bx = bTargetX - (bTargetX - (bTargetX - ww/2)) * progress;
                boarPos = Matrix.translate2D(bx, groundY + 20).multiply(staticOriginPoint);
                walterTurns = true;
            } else {
                boarPos = Matrix.translate2D(bTargetX - ww/2, groundY + 20).multiply(staticOriginPoint);
            }
        }

        int fx = (int) friedrichPos.get(0, 0);
        int fy = (int) friedrichPos.get(1, 0);
        int wx = (int) walterPos.get(0, 0);
        int wy = (int) walterPos.get(1, 0);
        int bx = (int) boarPos.get(0, 0);
        int by = (int) boarPos.get(1, 0);

        if (walterSmall != null) {
            if (walterFlies) {
                g.drawImage(walterRotated90, wx, wy, null); // Category 6: Sprajt otočený o 90
            } else if (walterTurns) {
                g.drawImage(walterMirrored, wx, wy, null);
            } else {
                g.drawImage(walterSmall, wx, wy, null);
            }
        }

        if (loopTime < resetTime && fx > -500) {
            if (loopTime < punchTime) {
                g.drawImage(friedrichMirrored, fx, fy, null); // Category 4
            } else {
                g.drawImage(friedrichRotated180, fx, fy, null); // Category 5
            }
        }

        if (boarSmall != null && bx < viewWidth + 100) {
            g.drawImage(boarMirrored, bx, by, null);
        }

        // Friedrich's morphing soul (Category 2, 7, 12, 13)
        if (loopTime >= punchTime && loopTime < fFlyTime) {
            double soulProgress = (loopTime - punchTime) / (fFlyTime - punchTime);
            double blendRatio = (Math.sin(loopTime * 5.0) + 1.0) / 2.0;
            int frameIdx = Math.min(19, (int)(blendRatio * 19));
            
            double ghostAlpha = Math.max(0, 1.0 - soulProgress);
            if (ghostAlpha > 0) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, (float) ghostAlpha));
                int sx = (int)(viewWidth / 3.0) + fw; 
                int sy = groundY - (int)(soulProgress * 200);
                g2.drawImage(ghostFrames[frameIdx], sx, sy, null);
                g2.dispose();
            }
        }



        // Získání správného snímku blikající květiny
        double flashCycle = (Math.sin(time * 4.0) + 1.0) / 2.0;
        int flowerFrameIdx = Math.min(19, (int)(flashCycle * 19));

        boolean isFlashPhase = (loopTime >= punchTime && loopTime < punchTime + 1.0);
        BufferedImage wheelToDraw = wheelScaledDown;
        if (isFlashPhase) {
            int flashState = (int)(time * 15) % 2;
            wheelToDraw = (flashState == 0) ? wheelScaledDownBright : wheelScaledDown;
        }
        if (wheelToDraw != null) {
            g.drawImage(wheelToDraw, 150, viewHeight - 120, null);
        }

        // Vykreslení plynulé blikající květiny bez lagů
        g.drawImage(flashingFlowerFrames[flowerFrameIdx], 50, viewHeight - 180, null);

        if (speech != null) drawSpeechBubble(g, speech, fx - 20, fy - 60, 280);
        if (walterSpeech != null) drawSpeechBubble(g, walterSpeech, wx + 40, wy - 60, 150);
        if (boarSpeech != null) drawSpeechBubble(g, boarSpeech, bx - 100, by - 60, 320);

        if (actionText != null && (loopTime < punchTime + 1.0 || (loopTime >= walterFlyTime && loopTime < walterFlyTime + 1.0))) {
            g.setColor(Color.RED);
            g.setFont(actionFont);
            if (actionText.equals("POW!!!")) {
                g.drawString(actionText, wx - 100, wy + wh / 2);
            } else {
                g.drawString(actionText, bx - 150, by + wh / 2);
            }
        }



        java.awt.Toolkit.getDefaultToolkit().sync();
    }

    private void drawSpeechBubble(Graphics g, String text, int x, int y, int w) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, w, 40, 15, 15);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, w, 40, 15, 15);
        g.setFont(speechFont);
        g.drawString(text, x + 10, y + 25);
    }

    private void drawParallaxLayer(Graphics g, BufferedImage layer, int startX, int y) {
        if (layer == null) return;
        int x = startX;
        while (x < getWidth()) {
            g.drawImage(layer, x, y, null);
            x += layer.getWidth();
        }
    }

    public static BufferedImage adjustColor(BufferedImage src, double alphaScale, double brightnessFactor) {
        if (src == null) return null;
        int w = src.getWidth();
        int h = src.getHeight();
        int[] pixels = new int[w * h];
        src.getRGB(0, 0, w, h, pixels, 0, w);

        for (int i = 0; i < pixels.length; i++) {
            int argb = pixels[i];
            int a = (int) (RGBA.getAlpha(argb) * alphaScale);
            int r = (int) (RGBA.getRed(argb) * brightnessFactor);
            int g = (int) (RGBA.getGreen(argb) * brightnessFactor);
            int b = (int) (RGBA.getBlue(argb) * brightnessFactor);

            a = Math.max(0, Math.min(255, a));
            r = Math.max(0, Math.min(255, r));
            g = Math.max(0, Math.min(255, g));
            b = Math.max(0, Math.min(255, b));

            pixels[i] = RGBA.getRGBA(r, g, b, a);
        }
        BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        dest.setRGB(0, 0, w, h, pixels, 0, w);
        return dest;
    }

    public static BufferedImage flipHorizontal(BufferedImage src) {
        if (src == null) return null;
        int w = src.getWidth();
        int h = src.getHeight();
        int[] srcPixels = new int[w * h];
        int[] destPixels = new int[w * h];
        src.getRGB(0, 0, w, h, srcPixels, 0, w);

        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                destPixels[offset + (w - 1 - x)] = srcPixels[offset + x];
            }
        }
        BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        dest.setRGB(0, 0, w, h, destPixels, 0, w);
        return dest;
    }

    public static BufferedImage rotatePixel180(BufferedImage src) {
        if (src == null) return null;
        int w = src.getWidth();
        int h = src.getHeight();
        int[] srcPixels = new int[w * h];
        int[] destPixels = new int[w * h];
        src.getRGB(0, 0, w, h, srcPixels, 0, w);

        for (int y = 0; y < h; y++) {
            int srcOffset = y * w;
            int destOffset = (h - 1 - y) * w;
            for (int x = 0; x < w; x++) {
                destPixels[destOffset + (w - 1 - x)] = srcPixels[srcOffset + x];
            }
        }
        BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        dest.setRGB(0, 0, w, h, destPixels, 0, w);
        return dest;
    }

    public static BufferedImage rotatePixel90CW(BufferedImage src) {
        if (src == null) return null;
        int w = src.getWidth();
        int h = src.getHeight();
        int[] srcPixels = new int[w * h];
        int[] destPixels = new int[h * w]; // transposed
        src.getRGB(0, 0, w, h, srcPixels, 0, w);

        for (int y = 0; y < h; y++) {
            int srcOffset = y * w;
            for (int x = 0; x < w; x++) {
                destPixels[x * h + (h - 1 - y)] = srcPixels[srcOffset + x];
            }
        }
        BufferedImage dest = new BufferedImage(h, w, BufferedImage.TYPE_INT_ARGB);
        dest.setRGB(0, 0, h, w, destPixels, 0, h);
        return dest;
    }

    public static BufferedImage crossFade(BufferedImage srcA, BufferedImage srcB, double t) {
        if (srcA == null || srcB == null) return null;
        int w = Math.min(srcA.getWidth(), srcB.getWidth());
        int h = Math.min(srcA.getHeight(), srcB.getHeight());
        int[] pixelsA = new int[w * h];
        int[] pixelsB = new int[w * h];
        int[] destPixels = new int[w * h];
        srcA.getRGB(0, 0, w, h, pixelsA, 0, w);
        srcB.getRGB(0, 0, w, h, pixelsB, 0, w);

        for (int i = 0; i < destPixels.length; i++) {
            int rgbA = pixelsA[i];
            int rgbB = pixelsB[i];

            int a = (int) (RGBA.getAlpha(rgbA) * (1.0 - t) + RGBA.getAlpha(rgbB) * t);
            int r = (int) (RGBA.getRed(rgbA) * (1.0 - t) + RGBA.getRed(rgbB) * t);
            int g = (int) (RGBA.getGreen(rgbA) * (1.0 - t) + RGBA.getGreen(rgbB) * t);
            int b = (int) (RGBA.getBlue(rgbA) * (1.0 - t) + RGBA.getBlue(rgbB) * t);

            destPixels[i] = RGBA.getRGBA(r, g, b, a);
        }
        BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        dest.setRGB(0, 0, w, h, destPixels, 0, w);
        return dest;
    }

    public static BufferedImage scaleNearest(BufferedImage src, double scaleX, double scaleY) {
        if (src == null) return null;
        int newW = (int) (src.getWidth() * scaleX);
        int newH = (int) (src.getHeight() * scaleY);
        if (newW <= 0 || newH <= 0) return null;

        int srcW = src.getWidth();
        int srcH = src.getHeight();
        int[] srcPixels = new int[srcW * srcH];
        int[] destPixels = new int[newW * newH];
        src.getRGB(0, 0, srcW, srcH, srcPixels, 0, srcW);

        for (int y = 0; y < newH; y++) {
            int srcY = Math.min((int) (y / scaleY), srcH - 1);
            int srcYOffset = srcY * srcW;
            int destYOffset = y * newW;
            for (int x = 0; x < newW; x++) {
                int srcX = Math.min((int) (x / scaleX), srcW - 1);
                destPixels[destYOffset + x] = srcPixels[srcYOffset + srcX];
            }
        }
        BufferedImage dest = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        dest.setRGB(0, 0, newW, newH, destPixels, 0, newW);
        return dest;
    }

    public static BufferedImage boxBlur(BufferedImage src, int radius) {
        if (src == null) return null;
        int w = src.getWidth();
        int h = src.getHeight();

        int[] srcPixels = new int[w * h];
        int[] tempPixels = new int[w * h];
        int[] destPixels = new int[w * h];
        src.getRGB(0, 0, w, h, srcPixels, 0, w);

        for (int y = 0; y < h; y++) {
            int yOffset = y * w;
            for (int x = 0; x < w; x++) {
                int hits = 0;
                long aSum = 0, rSum = 0, gSum = 0, bSum = 0;
                for (int k = -radius; k <= radius; k++) {
                    int kx = x + k;
                    if (kx >= 0 && kx < w) {
                        int rgb = srcPixels[yOffset + kx];
                        aSum += RGBA.getAlpha(rgb);
                        rSum += RGBA.getRed(rgb);
                        gSum += RGBA.getGreen(rgb);
                        bSum += RGBA.getBlue(rgb);
                        hits++;
                    }
                }
                tempPixels[yOffset + x] = RGBA.getRGBA((int)(rSum/hits), (int)(gSum/hits), (int)(bSum/hits), (int)(aSum/hits));
            }
        }

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int hits = 0;
                long aSum = 0, rSum = 0, gSum = 0, bSum = 0;
                for (int k = -radius; k <= radius; k++) {
                    int ky = y + k;
                    if (ky >= 0 && ky < h) {
                        int rgb = tempPixels[ky * w + x];
                        aSum += RGBA.getAlpha(rgb);
                        rSum += RGBA.getRed(rgb);
                        gSum += RGBA.getGreen(rgb);
                        bSum += RGBA.getBlue(rgb);
                        hits++;
                    }
                }
                destPixels[y * w + x] = RGBA.getRGBA((int)(rSum/hits), (int)(gSum/hits), (int)(bSum/hits), (int)(aSum/hits));
            }
        }
        
        BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        dest.setRGB(0, 0, w, h, destPixels, 0, w);
        return dest;
    }

    // Převede obrázek pro rychlejší vykreslování
    public static BufferedImage toCompatibleImage(BufferedImage src) {
        if (src == null) return null;
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            return src;
        }
        try {
            java.awt.GraphicsConfiguration gc = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration();
            BufferedImage dest = gc.createCompatibleImage(src.getWidth(), src.getHeight(), src.getTransparency());
            java.awt.Graphics2D g2dest = dest.createGraphics();
            g2dest.drawImage(src, 0, 0, null);
            g2dest.dispose();
            return dest;
        } catch (Exception e) {
            return src;
        }
    }
}