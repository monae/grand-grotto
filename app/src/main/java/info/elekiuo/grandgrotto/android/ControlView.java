package info.elekiuo.grandgrotto.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import info.elekiuo.grandgrotto.geometry.Direction;

public class ControlView extends View {
    public ControlView(Context context) {
        super(context);
    }
    public ControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ControlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        setBackgroundResource(R.drawable.control);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*
        setMeasuredDimension(
                resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));
                */
        float d = getResources().getDisplayMetrics().density;
        setMeasuredDimension((int) (d * 168), (int) (d * 168));
    }

    boolean pressed;
    float currentX;
    float currentY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
        case MotionEvent.ACTION_DOWN:
            pressed = true;
            currentX = event.getX();
            currentY = event.getY();
            checkMove();
            break;
        case MotionEvent.ACTION_MOVE:
            currentX = event.getX();
            currentY = event.getY();
            checkMove();
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            pressed = false;
            break;
        }
        return true;
    }

    public void checkMove() {
        MainView mainView = (MainView) getParent();
        if (!pressed || mainView.isAnimating()) {
            return;
        }
        float dx = currentX - getWidth() / 2f;
        float dy = currentY - getHeight() / 2f;
        if (Math.hypot(dx, dy) > 50) {
            Direction d = Direction.EAST.rotateLeft((int) Math.round(Math.atan2(-dy, dx) * 4 / Math.PI));
            mainView.move(d);
        } else {
            mainView.rest();
        }
    }
}
