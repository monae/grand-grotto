package info.elekiuo.grandgrotto.android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class MainView extends FrameLayout {
    private final GLSurfaceView glView;
    private final MainViewRenderer renderer = new MainViewRenderer();

    public MainView(Context context) {
        super(context);
    }
    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    {
        glView = new GLSurfaceView(getContext());
        glView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);
        glView.setEGLContextClientVersion(2);
        glView.setRenderer(renderer);
        glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        addView(glView);
    }

    public void onResume() {
        glView.onResume();
    }

    public void onPause() {
        glView.onPause();
    }
}
