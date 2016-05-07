package cz.cuni.mff.a8x8rgbmatrixcenter;

/**
 * Created by Dominik Skoda on 07.05.2016.
 */
public final class ColorHelper {

    private ColorHelper(){}

    public static final int RED_SHIFT = 16;
    public static final int GREEN_SHIFT = 8;
    public static final int BLUE_SHIFT = 0;
    public static final int LED_INTENSITY_LEVEL_COUNT = 4;

    public static int led2Color(int red, int green, int blue){
        int color = 0xFF000000;

        int newRed = (int) Math.round(0xFF * ((double)red / (double)(LED_INTENSITY_LEVEL_COUNT-1)));
        int newGreen = (int) Math.round(0xFF * ((double)green / (double)(LED_INTENSITY_LEVEL_COUNT-1)));
        int newBlue = (int) Math.round(0xFF * ((double)blue / (double)(LED_INTENSITY_LEVEL_COUNT-1)));

        color |= (newRed << RED_SHIFT);
        color |= (newGreen << GREEN_SHIFT);
        color |= (newBlue << BLUE_SHIFT);

        return color;
    }

    public static int color2LedRed(int color){
        int red = (color >> RED_SHIFT) & 0xFF;
        int newRed = (int) Math.round(red * (LED_INTENSITY_LEVEL_COUNT-1) / (double) 0xFF);

        return newRed;
    }

    public static int color2LedGreen(int color){
        int green = (color >> GREEN_SHIFT) & 0xFF;
        int newGreen = (int) Math.round(green * (LED_INTENSITY_LEVEL_COUNT-1) / (double) 0xFF);

        return newGreen;
    }

    public static int color2LedBlue(int color){
        int blue = (color >> BLUE_SHIFT) & 0xFF;
        int newBlue = (int) Math.round(blue * (LED_INTENSITY_LEVEL_COUNT-1) / (double) 0xFF);

        return newBlue;
    }


}
