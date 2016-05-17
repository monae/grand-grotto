package info.elekiuo.grandgrotto.android;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.animation.Interpolator;

public class Animator {
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public interface Callback {
        void onAnimationStart();
        void onAnimationUpdate(float fraction);
        void onAnimationEnd();
    }

    public static abstract class CallbackAdapter implements Callback {
        @Override
        public void onAnimationStart() {
        }
        @Override
        public void onAnimationUpdate(float fraction) {
        }
        @Override
        public void onAnimationEnd() {
        }
    }

    private Object lock;
    private Callback callback;
    private long duration = -1;
    private long startTime = -1;
    private Interpolator interpolator;

    private final Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            update();
        }
    };

    public void start(Callback callback, long duration) {
        start(callback, duration, null);
    }

    public void start(Callback callback, long duration, Interpolator interpolator) {
        cancel();

        this.lock = new Object();
        this.callback = callback;
        this.duration = duration;
        this.startTime = SystemClock.uptimeMillis();
        this.interpolator = interpolator;

        Object lock = this.lock;
        callback.onAnimationStart();
        if (lock == this.lock) {
            postUpdate();
        }
    }

    private void postUpdate() {
        handler.postDelayed(updateTask, 16);
    }

    public void cancel() {
        if (isRunning()) {
            Object lock = this.lock;
            callback.onAnimationEnd();
            if (lock == this.lock) {
                reset();
            }
        }
        cancelUpdate();
    }

    private void cancelUpdate() {
        handler.removeCallbacks(updateTask);
    }

    public boolean isRunning() {
        checkThread();
        return startTime >= 0;
    }

    private void update() {
        long time = SystemClock.uptimeMillis();
        if (time < startTime + duration) {
            float fraction = (float) (time - startTime) / duration;
            Object lock = this.lock;
            callback.onAnimationUpdate(interpolator == null ? fraction : interpolator.getInterpolation(fraction));
            if (lock == this.lock) {
                postUpdate();
            }
        } else {
            Object lock = this.lock;
            callback.onAnimationEnd();
            if (lock == this.lock) {
                reset();
            }
        }
    }

    private void reset() {
        lock = null;
        callback = null;
        startTime = -1;
        duration = -1;
        interpolator = null;
    }

    private static void checkThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("Called from non-main thread");
        }
    }
}
