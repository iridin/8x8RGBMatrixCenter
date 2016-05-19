package cz.cuni.mff.a8x8rgbmatrixcenter;

import java.io.Serializable;

/**
 * Created by Dominik Skoda on 19.05.2016.
 */
public class TimedColor implements Serializable, Comparable<TimedColor> {

    public final int ledIndex;
    public final int color;
    public final long time; /* in milliseconds */

    public TimedColor(int ledIndex, int color, long time){
        this.ledIndex = ledIndex;
        this.color = color;
        this.time = time;
    }

    @Override
    public int compareTo(TimedColor another) {
        return Long.compare(this.time, another.time);
    }
}
