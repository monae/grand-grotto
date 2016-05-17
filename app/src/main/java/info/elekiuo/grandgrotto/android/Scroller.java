package info.elekiuo.grandgrotto.android;

import android.view.MotionEvent;

public class Scroller {

    public static class State {
        public float x;
        public float y;
        public float scale;

        public State() {
            this(0, 0, 1);
        }

        public State(State state) {
            this(state.x, state.y, state.scale);
        }

        public State(float x, float y, float scale) {
            this.x = x;
            this.y = y;
            this.scale = scale;
        }

        public void set(State state) {
            this.x = state.x;
            this.y = state.y;
            this.scale = state.scale;
        }
    }

    public interface Listener {
        void onStateChanged(State state);
    }

    private Listener listener;

    private final State state = new State();
    private final State oldState = new State();

    private float touchX;
    private float touchY;
    private int activePointerId = -1;
    private float touchX2;
    private float touchY2;
    private int activePointerId2 = -1;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void updateState(State state) {
        this.state.set(state);
        onStateChanged();
    }

    public boolean handleTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
        case MotionEvent.ACTION_DOWN:
            activePointerId = event.getPointerId(0);
            saveState(event);
            return true;
        case MotionEvent.ACTION_UP:
            activePointerId = -1;
            return true;
        case MotionEvent.ACTION_MOVE:
            if (event.getPointerCount() == 1) {
                int activePointerIndex = event.findPointerIndex(activePointerId);
                if (activePointerIndex >= 0) {
                    state.x = oldState.x - (event.getX(activePointerIndex) - touchX) / state.scale;
                    state.y = oldState.y - (event.getY(activePointerIndex) - touchY) / state.scale;
                    onStateChanged();
                }
            } else {
                int activePointerIndex = event.findPointerIndex(activePointerId);
                int activePointerIndex2 = event.findPointerIndex(activePointerId2);
                if (activePointerIndex >= 0 && activePointerIndex2 >= 0) {
                    float dx = event.getX(activePointerIndex) - event.getX(activePointerIndex2);
                    float dy = event.getY(activePointerIndex) - event.getY(activePointerIndex2);
                    float oldDx = touchX - touchX2;
                    float oldDy = touchY - touchY2;
                    state.scale = oldState.scale * (float) Math.hypot(dx, dy) / (float) Math.hypot(oldDx, oldDy);
                    onStateChanged();
                }
            }
            return false;
        case MotionEvent.ACTION_POINTER_DOWN:
            if (event.getPointerCount() == 2) {
                activePointerId2 = event.getPointerId(event.getActionIndex());
                saveState(event);
            }
            return true;
        case MotionEvent.ACTION_POINTER_UP:
            int index = event.getActionIndex();
            int pointerId = event.getPointerId(index);
            if (activePointerId == pointerId) {
                activePointerId = activePointerId2;
                activePointerId2 = pointerId;
            }
            if (event.getPointerCount() == 2) {
                activePointerId2 = -1;
            } else if (activePointerId2 == pointerId) {
                int newIndex;
                if (index != 0 && activePointerId != event.getPointerId(0)) {
                    newIndex = 0;
                } else if (index != 1 && activePointerId != event.getPointerId(1)) {
                    newIndex = 1;
                } else {
                    newIndex = 2;
                }
                activePointerId2 = event.getPointerId(newIndex);
            }
            saveState(event);
            return true;
        default:
            return false;
        }
    }

    private void saveState(MotionEvent event) {
        oldState.set(state);
        int activePointerIndex = event.findPointerIndex(activePointerId);
        if (activePointerIndex >= 0) {
            touchX = event.getX(activePointerIndex);
            touchY = event.getY(activePointerIndex);
        }
        int activePointerIndex2 = event.findPointerIndex(activePointerId2);
        if (activePointerIndex2 >= 0) {
            touchX2 = event.getX(activePointerIndex2);
            touchY2 = event.getY(activePointerIndex2);
        }
    }

    private void onStateChanged() {
        if (listener != null) {
            listener.onStateChanged(new State(state));
        }
    }
}
