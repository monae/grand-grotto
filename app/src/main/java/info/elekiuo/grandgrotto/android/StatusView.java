package info.elekiuo.grandgrotto.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

public class StatusView extends FrameLayout {
    private final Paint paint = new Paint();
    {
        paint.setColor(0xffa51d1d);
    }

    private final Animator animator = new Animator();
    private final TextView lifeTextView;
    private float displayLife = -1;
    private int maxLife;

    public StatusView(Context context) {
        super(context);
    }
    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public StatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        setBackgroundResource(R.drawable.status);

        lifeTextView = new TextView(getContext());
        lifeTextView.setTextColor(ColorStateList.valueOf(0xffffffff));
        lifeTextView.setTypeface(Typeface.DEFAULT_BOLD);
        lifeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
        lifeTextView.setGravity(Gravity.CENTER);
        float d = getResources().getDisplayMetrics().density;
        addView(lifeTextView, new LayoutParams((int) (80 * d), (int) (66 * d), Gravity.LEFT | Gravity.TOP));
    }

    public void setLife(int life) {
        if (displayLife < 0 || displayLife == life) {
            animator.cancel();
            lifeTextView.clearAnimation();
            setDisplayLife(life);
        } else {
            final float start = displayLife;
            final float end = life;
            animator.start(new Animator.Callback() {
                @Override
                public void onAnimationStart() {
                    setDisplayLife(start);
                }
                @Override
                public void onAnimationUpdate(float fraction) {
                    setDisplayLife(start + (end - start) * fraction);
                }
                @Override
                public void onAnimationEnd() {
                    setDisplayLife(end);
                }
            }, 300, new DecelerateInterpolator());

            ScaleAnimation animation = new ScaleAnimation(1.25f, 1f, 1.125f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(300);
            animation.setInterpolator(new DecelerateInterpolator());
            lifeTextView.startAnimation(animation);
        }
    }

    private void setDisplayLife(float displayLife) {
        if (this.displayLife != displayLife) {
            this.displayLife = displayLife;
            String text = "" + Math.round(displayLife);
            lifeTextView.setText(text);
            lifeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 240 / (4 + text.length()));
            invalidate();
        }
    }

    public void setMaxLife(int maxLife) {
        if (this.maxLife != maxLife) {
            this.maxLife = maxLife;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (displayLife > 0 && maxLife > 0) {
            float d = getResources().getDisplayMetrics().density;
            canvas.drawRect(76 * d, 15 * d, (76 + 212 * displayLife / maxLife) * d, 35 * d, paint);
        }
    }
}
