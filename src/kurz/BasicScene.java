package kurz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class BasicScene extends JComponent {

    private static String imageRootName = "sprites";
    private static String backgroundFileName = "sky_background.png";
    private static double axisScale = Double.NaN;
    private static Color backgroundColor = Color.LIGHT_GRAY;
    private static Color axisColor = Color.DARK_GRAY;
    private static Color tickColor = Color.GRAY;

    public static void main(String[] args) {
        show(new BasicScene());
    }

    protected static void show(BasicScene scene) {
        SwingUtilities.invokeLater(() -> createAndShowFrame(scene));
    }

    protected static JFrame createAndShowFrame(BasicScene scene) {
        JFrame frame = new JFrame(scene.getClass().getSimpleName());
        frame.getContentPane().setBackground(backgroundColor);
        frame.getContentPane().add(scene);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        return frame;
    }

    protected static void setAxis() {
        setAxis(32);
    }

    protected static void setAxis(double scale) {
        BasicScene.axisScale = scale;
    }

    protected static BufferedImage readImage(String fileName) {
        try {
            java.net.URL resource = BasicScene.class.getResource("/kurz/" + imageRootName + "/" + fileName);
            if (resource != null) {
                return ImageIO.read(resource);
            }
        } catch (IOException ex) {
            // Fallback
        }
        return readImage(new File(imageRootName, fileName));
    }

    protected static BufferedImage readImage(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException ex) {
            return null;
        }
    }

    protected static BufferedImage createNaiveImageCopy(BufferedImage source) {
        if (source == null) {
            return null;
        }
        int width = source.getWidth(null);
        int height = source.getHeight(null);
        BufferedImage destination = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                destination.setRGB(x, y, source.getRGB(x, y));
            }
        }
        return destination;
    }

    protected static void setImageRootName(String imageRootName) {
        BasicScene.imageRootName = imageRootName;
    }

    protected static void setBackgroundFileName(String backgroundFileName) {
        BasicScene.backgroundFileName = backgroundFileName;
    }

    public static void setBackgroundColor(Color backgroundColor) {
        BasicScene.backgroundColor = backgroundColor;
    }

    private final BufferedImage background;
    private final Dimension dimension;
    private final int xAxis;
    private final int yAxis;
    private final boolean showAxis;

    public BasicScene() {
        background = readImage(backgroundFileName);
        dimension = background == null ? new Dimension(600, 400) : new Dimension(
                background.getWidth(null),
                background.getHeight(null));
        showAxis = !Double.isNaN(axisScale);
        xAxis = showAxis ? dimension.height / 2 : 0;
        yAxis = showAxis ? dimension.width / 2 : 0;
        setBackground(backgroundColor);
    }

    public int pointToX(double x) {
        if (!showAxis) {
            throw new IllegalStateException("axis not shown");
        }
        return yAxis + (int) (x * axisScale);
    }

    public int pointToY(double y) {
        if (!showAxis) {
            throw new IllegalStateException("axis not shown");
        }
        return xAxis - (int) (y * axisScale);
    }

    @Override
    public Dimension getPreferredSize() {
        return dimension == null ? super.getPreferredSize() : dimension;
    }

    @Override
    public Dimension getMinimumSize() {
        return dimension == null ? super.getMinimumSize() : dimension;
    }

    @Override
    public Dimension getMaximumSize() {
        return dimension == null ? super.getMaximumSize() : dimension;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, null);
        }
        if (showAxis) {
            Color c = g.getColor();
            try {
                g.setColor(axisColor);
                g.drawLine(0, xAxis, dimension.width, xAxis);
                g.drawLine(yAxis, 0, yAxis, dimension.height);
                g.setColor(tickColor);
                AtomicBoolean p = new AtomicBoolean();
                for (int i = 0;; i++) {
                    p.set(false);
                    int t = (int) (i * axisScale);
                    xTick(g, p, t);
                    xTick(g, p, -t);
                    yTick(g, p, t);
                    yTick(g, p, -t);
                    if (!p.get()) {
                        break;
                    }
                }
            } finally {
                g.setColor(c);
            }
        }
    }

    private void xTick(Graphics g, AtomicBoolean p, int t) {
        int x = yAxis + t;
        if ((x >= 0) && (x < dimension.width)) {
            p.set(true);
            g.drawLine(x, xAxis - 5, x, xAxis + 5);
        }
    }

    private void yTick(Graphics g, AtomicBoolean p, int t) {
        int y = xAxis + t;
        if ((y >= 0) && (y < dimension.height)) {
            p.set(true);
            g.drawLine(yAxis - 5, y, yAxis + 5, y);
        }
    }
}