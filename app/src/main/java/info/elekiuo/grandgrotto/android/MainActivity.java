package info.elekiuo.grandgrotto.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import info.elekiuo.grandgrotto.geometry.Direction;

public class MainActivity extends Activity {
    private MainView mainView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final MainView mainView = new MainView(this);
        this.mainView = mainView;
        addContentView(mainView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);

        TextView refreshButton = new Button(this);
        refreshButton.setText("Refresh");
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.refresh();
            }
        });
        buttons.addView(refreshButton);

        buttons.addView(new View(this), new LinearLayout.LayoutParams(0, 0, 1));

        TextView restButton = new Button(this);
        restButton.setText("…");
        restButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.rest();
            }
        });
        buttons.addView(restButton);

        TextView westButton = new Button(this);
        westButton.setText("←");
        westButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.move(Direction.WEST);
            }
        });
        buttons.addView(westButton);

        TextView southButton = new Button(this);
        southButton.setText("↓");
        southButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.move(Direction.SOUTH);
            }
        });
        buttons.addView(southButton);

        TextView northButton = new Button(this);
        northButton.setText("↑");
        northButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.move(Direction.NORTH);
            }
        });
        buttons.addView(northButton);

        TextView eastButton = new Button(this);
        eastButton.setText("→");
        eastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainView.move(Direction.EAST);
            }
        });
        buttons.addView(eastButton);

        addContentView(buttons, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));

        // avoid clipping
        addContentView(new View(this), new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainView.onPause();
    }
}
