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

    private static final Boolean debugWorker = false;

    private Thread worker;
    private final AbstractQueue<TimedColor> timedColors;
    private boolean quitWorker = false; // TODO: break the cycle when the app closes

    public TimedColorService() {
        super("TimedColorService");

        timedColors = new PriorityBlockingQueue<>();

        worker = new Thread(){

            @Override
            public void run() {
                if(debugWorker) Log.i("WORKER", "start");
                while(true){
                    if(debugWorker) Log.i("WORKER", "step");
                    if(quitWorker){
                        if(debugWorker) Log.i("WORKER", "quit");
                        break;
                    }

                    if(timedColors.isEmpty()){
                        try {
                            if(debugWorker) Log.i("WORKER", "wait");
                            synchronized (this) {
                                wait();
                            }
                        } catch(InterruptedException e){}
                        continue;
                    }

                    long nextTime = timedColors.peek().time;
                    long now = Calendar.getInstance().getTimeInMillis();
                    if(now >= nextTime){
                        if(debugWorker) Log.i("WORKER", "broadcast");
                        TimedColor nextColor = timedColors.poll();

                        Intent intent = new Intent(COLOR_TIMEUP);
                        intent.putExtra(LED_INDEX_KEY, nextColor.ledIndex);
                        intent.putExtra(LED_COLOR_KEY, nextColor.color);

                        LocalBroadcastManager.getInstance(TimedColorService.this).sendBroadcast(intent);
                    } else {
                        try{
                            if(debugWorker) Log.i("WORKER", "sleep " + (nextTime - now));
                            sleep(nextTime - now);
                        } catch (InterruptedException e) {}
                    }
                }
            }
        };

        worker.start();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<TimedColor> colorChain = (List<TimedColor>) intent.getSerializableExtra(COLOR_CHAIN_KEY);

        timedColors.addAll(colorChain);
        synchronized(worker) {
            worker.notify();
        }
    }

}
