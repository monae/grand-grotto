package info.elekiuo.grandgrotto.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import info.elekiuo.grandgrotto.core.Command;
import info.elekiuo.grandgrotto.core.Monster;
import info.elekiuo.grandgrotto.core.Shell;
import info.elekiuo.grandgrotto.geometry.Direction;
import info.elekiuo.grandgrotto.geometry.Position;
import info.elekiuo.grandgrotto.geometry.Vector;

public class ControlView extends View {
    private final Bitmap overlay;

    private Shell shell;

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
        overlay = BitmapFactory.decodeResource(getResources(), R.drawable.control2);
    }

    public void setShell(Shell shell) {
        this.shell = shell;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (shell == null) {
            return;
        }
        Position position = shell.getPlayer().getPosition();

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                Direction d = Direction.fromVector(dx, dy);
                if (d != null && shell.getMonster(position.plus(d)) != null) {
                    int uw = getWidth() / 3;
                    int uh = getHeight() / 3;
                    Rect rect = new Rect(uw * (dx + 1), uh * (dy + 1), uw * (dx + 2), uh * (dy + 2));
                    canvas.drawBitmap(overlay, rect, rect, null);
                }
            }
        }
    }

    int state;
    float currentX;
    float currentY;
    Direction lastD;
    Direction lastDD;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        currentX = event.getX();
        currentY = event.getY();
        int dx = Math.round(currentX * 3 / getWidth() - 1.5f);
        int dy = Math.round(currentY * 3 / getHeight() - 1.5f);
        Direction d = Direction.fromVector(dx, dy);
        MainView mainView = (MainView) getParent();

        switch (event.getActionMasked()) {
        case MotionEvent.ACTION_DOWN:
            if (d == null) {
                state = 1;
                checkMove();
            } else if (shell.getMonster(shell.getPlayer().getPosition().plus(d)) != null) {
                mainView.attack(d);
            } else {
                state = 2;
                checkMove();
            }
            break;
        case MotionEvent.ACTION_MOVE:
            checkMove();
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            state = 0;
            lastD = null;
            lastDD = null;
            break;
        }
        return true;
    }

    public void checkMove() {
        invalidate();
        MainView mainView = (MainView) getParent();
        if (state == 0 || mainView.isAnimating()) {
            return;
        }
        if (state == 1) {
            mainView.rest();
            return;
        }
        int dx = Math.round(currentX * 3 / getWidth() - 1.5f);
        int dy = Math.round(currentY * 3 / getHeight() - 1.5f);
        Direction d = Direction.fromVector(dx, dy);
        if (d == null) {
            return;
        }
        Monster player = shell.getPlayer();
        if (player.isMovableTo(d)) {
            mainView.move(d);
        } else if (d.isOrdinal()) {
            Vector v = d.vector;
            Direction d1 = Direction.fromVector(v.dx, 0);
            Direction d2 = Direction.fromVector(0, v.dy);
            if (player.isMovableTo(d1)) {
                if (player.isMovableTo(d2)) {
                    if (d == lastD) {
                        if (d1 == lastDD) {
                            lastD = d;
                            lastDD = d2;
                            mainView.move(d2);
                        } else if (d2 == lastDD) {
                            lastD = d;
                            lastDD = d1;
                            mainView.move(d1);
                        }
                    }
                } else {
                    lastD = d;
                    lastDD = d1;
                    mainView.move(d1);
                }
            } else {
                if (player.isMovableTo(d2)) {
                    lastD = d;
                    lastDD = d2;
                    mainView.move(d2);
                }
            }
        }
    }
}
