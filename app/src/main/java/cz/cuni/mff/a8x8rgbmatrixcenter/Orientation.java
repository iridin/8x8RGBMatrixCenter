package cz.cuni.mff.a8x8rgbmatrixcenter;

/**
 * Created by Dominik Skoda on 07.05.2016.
 */
public enum Orientation {
    horizontal, vertical;

    public static Orientation parse(int value){
        switch (value){
            case 0:
                return horizontal;
            case 1:
                return vertical;
            default:
                throw new IllegalStateException(String.format("The value %d cannot be casted to Orientation.", value));
        }
    }
}
