package kurz;

public class RGBA {

    public static final int BITS_PER_CHANNEL = 8;
    public static final int CHANNEL_MASK = (0x1 << BITS_PER_CHANNEL) - 1; // 0xff
    public static final int SHIFT_BLUE = 0 * BITS_PER_CHANNEL;
    public static final int SHIFT_GREEN = 1 * BITS_PER_CHANNEL;
    public static final int SHIFT_RED = 2 * BITS_PER_CHANNEL;
    public static final int SHIFT_ALPHA = 3 * BITS_PER_CHANNEL;

    public static int getAlpha(int rgba) {
        return (rgba >> SHIFT_ALPHA) & CHANNEL_MASK;
    }

    public static int getRed(int rgba) {
        return (rgba >> SHIFT_RED) & CHANNEL_MASK;
    }

    public static int getGreen(int rgba) {
        return (rgba >> SHIFT_GREEN) & CHANNEL_MASK;
    }

    public static int getBlue(int rgba) {
        return (rgba >> SHIFT_BLUE) & CHANNEL_MASK;
    }

    public static int getRGBA(int red, int green, int blue, int alpha) {
        checkRGBA(red, green, blue, alpha);
        return ((alpha & CHANNEL_MASK) << SHIFT_ALPHA)
                | ((red & CHANNEL_MASK) << SHIFT_RED)
                | ((green & CHANNEL_MASK) << SHIFT_GREEN)
                | ((blue & CHANNEL_MASK) << SHIFT_BLUE);
    }

    public static void checkRGBA(int red, int green, int blue, int alpha) {
        boolean rangeError = false;
        String badComponentString = "";

        if (alpha < 0 || alpha > CHANNEL_MASK) {
            rangeError = true;
            badComponentString += " Alpha";
        }
        if (red < 0 || red > CHANNEL_MASK) {
            rangeError = true;
            badComponentString += " Red";
        }
        if (green < 0 || green > CHANNEL_MASK) {
            rangeError = true;
            badComponentString += " Green";
        }
        if (blue < 0 || blue > CHANNEL_MASK) {
            rangeError = true;
            badComponentString += " Blue";
        }
        if (rangeError == true) {
            throw new IllegalArgumentException("Color parameter outside of expected range:"
                    + badComponentString);
        }
    }
}