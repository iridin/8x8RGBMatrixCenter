package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.AbstractQueue;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixActivity.LED_COLOR_KEY;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixActivity.LED_INDEX_KEY;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class TimedColorService extends IntentService {

    public final static String COLOR_CHAIN_KEY = "COLOR_CHAIN";
    public final static String COLOR_TIMEUP = "COLOR_TIMEUP";

    public final static long QUIT_DELAY = 10000; // Milliseconds of inactivity until quit

    private static final Boolean debugWorker = false;

    private Thread worker;
    private final AbstractQueue<TimedColor> timedColors;
    private long lastActive;
    private boolean quitWorker = false;

    public TimedColorService() {
        super("TimedColorService");

        timedColors = new PriorityBlockingQueue<>();

        worker = null; // Indicates worker not started
    }

    private synchronized void startWorker() {
        if(worker != null){
            // Worker already started
            return;
        }

        quitWorker = false;
        lastActive = Calendar.getInstance().getTimeInMillis();
        worker = new Thread() {

            @Override
            public void run() {
                if (debugWorker) Log.i("WORKER", "start " + this);
                while (true) {
                    if (debugWorker) Log.i("WORKER", "step " + this);
                    if (Calendar.getInstance().getTimeInMillis() - lastActive > QUIT_DELAY) {
                        worker = null;
                        break;
                    }

                    if (timedColors.isEmpty()) {
                        try {
                            if (debugWorker) Log.i("WORKER", "wait " + this);
                            synchronized (this) {
                                wait(QUIT_DELAY);
                                if (debugWorker) Log.i("WORKER", "woke " + this);
                            }
                        } catch (InterruptedException e) {
                            if (debugWorker) Log.i("WORKER", "interrupted " + this);
                        }
                        continue;
                    }

                    long nextTime = timedColors.peek().time;
                    long now = Calendar.getInstance().getTimeInMillis();
                    if (now >= nextTime) {
                        if (debugWorker) Log.i("WORKER", "broadcast " + this);
                        TimedColor nextColor = timedColors.poll();

                        Intent intent = new Intent(COLOR_TIMEUP);
                        intent.putExtra(LED_INDEX_KEY, nextColor.ledIndex);
                        intent.putExtra(LED_COLOR_KEY, nextColor.color);

                        LocalBroadcastManager.getInstance(TimedColorService.this).sendBroadcast(intent);
                    } else {
                        try {
                            if (debugWorker) Log.i("WORKER", "sleep " + this + " for " + (nextTime - now));
                            synchronized (worker) {
                                wait(nextTime - now);
                            }
                        } catch (InterruptedException e) {
                        }
                    }
                }
                if (debugWorker) Log.i("WORKER", "quit " + this);
            }
        };

        worker.start();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        startWorker();

        List<TimedColor> colorChain = (List<TimedColor>) intent.getSerializableExtra(COLOR_CHAIN_KEY);

        timedColors.addAll(colorChain);
        synchronized (worker) {
            if (debugWorker) Log.i("WORKER", "notified " + worker);
            worker.notify();
        }
    }

}
