package info.elekiuo.grandgrotto.android;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.elekiuo.grandgrotto.core.Command;
import info.elekiuo.grandgrotto.core.Message;
import info.elekiuo.grandgrotto.core.Monster;
import info.elekiuo.grandgrotto.core.Random;
import info.elekiuo.grandgrotto.core.Shell;
import info.elekiuo.grandgrotto.core.Terrain;
import info.elekiuo.grandgrotto.geometry.Direction;
import info.elekiuo.grandgrotto.geometry.Matrix;
import info.elekiuo.grandgrotto.geometry.Position;
import info.elekiuo.grandgrotto.geometry.Region;

public class MainView extends FrameLayout {

    private final GLAnimator moveAnimator = new GLAnimator();
    private final GLAnimator cameraAnimator = new GLAnimator();

    private Shell shell;

    private final TextureAtlas textureAtlas = TextureAtlas.generate(getResources(), new int[]{
            R.drawable.b1,
            R.drawable.b2,
            R.drawable.b3,
            R.drawable.b4,
            R.drawable.b5,
            R.drawable.c1,
            R.drawable.c2
    });

    private final GLSurfaceView glView;
    private final StatusView statusView;
    private final ControlView controlView;
    private final MainViewRenderer renderer = new MainViewRenderer(textureAtlas);

    private final VertexTexBuffer playerVertexBuffer = createPlayerVertexBuffer();
    private final VertexTexBuffer monsterVertexBuffer = createMonsterVertexBuffer();

    private Scroller.State scrollState = new Scroller.State(
            0, 0, 48 * getResources().getDisplayMetrics().density);
    private final Scroller scroller = new Scroller();

    public MainView(Context context) {
        super(context);
    }
    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    {
        LayoutInflater.from(getContext()).inflate(R.layout.main_view, this);
        glView = (GLSurfaceView) getChildAt(0);
        glView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);
        glView.setEGLContextClientVersion(2);
        glView.setRenderer(renderer);
        glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        statusView = (StatusView) getChildAt(1);

        controlView = (ControlView) getChildAt(2);

        scroller.setListener(new Scroller.Listener() {
            @Override
            public void onStateChanged(Scroller.State state) {
                MainView.this.scrollState = state;
                renderer.setWorldMatrix(getWorldMatrix());
                glView.requestRender();
            }
        });

        refresh();
    }

    public void onResume() {
        glView.onResume();
    }

    public void onPause() {
        glView.onPause();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        renderer.setWorldMatrix(getWorldMatrix());
    }

    private float[] getWorldMatrix() {
        Scroller.State scrollState = this.scrollState;
        int width = getWidth();
        int height = getHeight();

        float[] m = new float[] {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1,
        };

        float tx = scrollState.x;
        float ty = scrollState.y;

        float c = 15/16f;
        float s = (float) Math.sqrt(1 - c * c);

        float sx = 2 * scrollState.scale / width;
        float sy = -2 * scrollState.scale / height;
        float sz = -scrollState.scale / 1024f;
        float f = 0.4f;

        for (int i = 0; i < 16; i += 4) {
            m[i + 0] -= tx * m[i + 3];
            m[i + 1] -= ty * m[i + 3];

            float t = m[i + 1] * c - m[i + 2] * s;
            m[i + 2] = m[i + 1] * s + m[i + 2] * c;
            m[i + 1] = t;

            m[i + 0] *= sx;
            m[i + 1] *= sy;
            m[i + 2] *= sz;

            m[i + 3] += m[i + 2] * f;
        }

        return m;
    }

    private PointF getScreenPoint(float x, float y, float z) {
        return getScreenPoint(x, y, z, 1);
    }

    private PointF getScreenPoint(float x, float y, float z, float w) {
        float[] m = getWorldMatrix();
        float x2 = m[0] * x + m[4] * y + m[8] * z + m[12] * w;
        float y2 = m[1] * x + m[5] * y + m[9] * z + m[13] * w;
        float w2 = m[3] * x + m[7] * y + m[11] * z + m[15] * w;
        return new PointF(x2 * getWidth() / (2 * w2), y2 * getHeight() / (-2 * w2));
    }

    public Sprite createSprite(Monster monster) {
        Position c = monster.getPosition();
        return createSprite(monster, c.x, c.y);
    }

    public Sprite createSprite(Monster monster, float x, float y) {
        return new Sprite(monster == shell.getPlayer() ? playerVertexBuffer : monsterVertexBuffer, x, y, monster.getDirection().ordinal() >= 4);
    }

    public void refresh() {
        moveAnimator.cancel();
        cameraAnimator.cancel();

        shell = new Shell(new Random());
        controlView.setShell(shell);

        Monster player = shell.getPlayer();

        //float d = getResources().getDisplayMetrics().density;
        Position position = player.getPosition();
        //float scale = Math.max(4 * d, Math.min(96 * d, scrollState.scale));
        scroller.updateState(new Scroller.State(position.x, position.y, detectScale()));

        updateMonsterSprites();
        updateStage();
        updateMask();
        glView.requestRender();

        updateStatus();
    }

    private float detectScale() {
        Region sight = shell.getSight();
        int max = Math.max(sight.getCols(), sight.getRows());
        float d = getResources().getDisplayMetrics().density;
        if (max < 15) {
            return 36 * d;
        } else if (max < 25) {
            return 32 * d;
        } else if (max < 35) {
            return 28 * d;
        } else {
            return 24 * d;
        }
    }

    private static Mesh createStageMesh(Matrix<Terrain> cells, TextureAtlas textureAtlas) {
        List<Plane> planes = new ArrayList<>();

        for (int y = cells.rows - 1; y >= 0; y--) {
            for (int x = 0; x < cells.cols; x++) {
                if (cells.get(x, y) == Terrain.WALL) {
                    if (y==cells.rows-1 || cells.get(x, y+1) != Terrain.WALL) {
                        planes.add(Plane.x(R.drawable.b3, new Vertex(x - 0.55f, y + 0.55f, 0.75f), new Vertex(x + 0.55f, y + 0.55f, 0)));
                    }
                    planes.add(Plane.x(R.drawable.b4, new Vertex(x-0.55f, y-0.55f, 0.76f), new Vertex(x+0.55f, y+0.55f, 0.75f)));
                    planes.add(Plane.x(R.drawable.b2, new Vertex(x-0.55f, y-0.55f, 0.01f), new Vertex(x+0.55f, y+0.55f, 0.01f)));
                    if (y==0 || cells.get(x, y-1) != Terrain.WALL) {
                        planes.add(Plane.x(R.drawable.b5, new Vertex(x - 0.55f, y - 0.55f, 0.75f), new Vertex(x + 0.55f, y - 0.55f, 0)));
                    }
                } else if (cells.get(x, y) == Terrain.FLOOR){
                    planes.add(Plane.x(R.drawable.b1, new Vertex(x-0.5f, y-0.5f, 0), new Vertex(x+0.5f, y+0.5f, 0)));
                }
            }
        }

        return new Mesh(planes, textureAtlas);
    }

    private VertexTexBuffer createPlayerVertexBuffer() {
        return createBillboard(R.drawable.c1, 0.5f, 0.25f, 0.25f);
    }

    private VertexTexBuffer createMonsterVertexBuffer() {
        return createBillboard(R.drawable.c2, 0.45f, 0.1f, 0f);
    }

    private VertexTexBuffer createBillboard(int bitmap, float y, float z, float t) {
        Rect rect = textureAtlas.get(bitmap);
        int w = rect.width();
        int h = rect.height();
        float scale = 1/96f;
        float c = 15/16f;
        float s = (float) Math.sqrt(1 - c * c);
        Plane p = Plane.x(bitmap,
                new Vertex(-w * scale / 2, y - h * scale * (c - s * t), z + h * scale * (s + c * t)),
                new Vertex(w * scale / 2, y, z));
        return new Mesh(Collections.singletonList(p), textureAtlas).vertexBuffer;
    }

    private static Mask createMask(Region region) {
        return createMask(region.west, region.north, region.east, region.south);
    }

    private static Mask createMask(float left, float top, float right, float bottom) {
        return new Mask(left, top, right, bottom, GLES20Utils.multiplyAlpha(0x88442211));
    }

    public void rest() {
        executeCommand(new Command.Rest());
    }

    public void move(Direction direction) {
        Monster player = shell.getPlayer();
        if (player.isMovableTo(direction)) {
            executeCommand(new Command.Move(direction));
        }
    }

    public void attack(Direction direction) {
        Monster player = shell.getPlayer();
        Position position = player.getPosition();
        if (shell.getMonster(position.plus(direction)) != null) {
            executeCommand(new Command.Attack(direction));
        }
    }

    private void executeCommand(Command command) {
        if (moveAnimator.isRunning()) {
            return;
        }
        if (!shell.isExecutable(command)) {
            return;
        }
        shell.execute(command);
        handleMessage();
    }

    private void handleMessage() {
        Message message;
        while ((message = shell.peekMessage()) != null) {
            if (message instanceof Message.Move || message instanceof Message.Rest) {
                handleMove();
                return;
            } else if (message instanceof Message.Injured) {
                handleInjured();
                return;
            } else {
                shell.removeMessage();
                if (message instanceof Message.Died) {
                    updateMonsterSprites();
                    glView.requestRender();
                }
                System.out.println(message);
            }
        }

        postDelayed(new Runnable() {
            @Override
            public void run() {
                controlView.checkMove();
            }
        }, 1);
    }

    private static class MoveTracker {
        private final Map<Monster, List<Position>> moves = new HashMap<>();

        public void add(Monster monster) {
            List<Position> move = moves.get(monster);
            Position position = monster.getPosition();
            if (move == null) {
                move = new ArrayList<>();
                move.add(position);
                moves.put(monster, move);
            } else if (!position.equals(move.get(move.size() - 1))) {
                move.add(position);
            }
        }

        public void addAll(Collection<Monster> monsters) {
            for (Monster monster : monsters) {
                add(monster);
            }
        }

        public Collection<Monster> getMonsters() {
            return moves.keySet();
        }

        public PointF getPoint(Monster monster, float fraction) {
            List<Position> move = moves.get(monster);
            if (move.size() <= 1) {
                Position p = move.get(0);
                return new PointF(p.x, p.y);
            }

            float t = fraction * (move.size() - 1);
            int i = (int) t;
            float f = t - i;

            Position p1 = move.get(i);
            Position p2 = move.get(i + 1);
            return new PointF(p1.x + (p2.x - p1.x) * f, p1.y + (p2.y - p1.y) * f);
        }
    }

    private void handleMove() {
        final Region sight1 = shell.getSight();
        final float x1 = scrollState.x;
        final float y1 = scrollState.y;
        final float scale1 = scrollState.scale;

        final MoveTracker moveTracker = new MoveTracker();
        moveTracker.addAll(shell.getMonsters());

        Message message;
        while ((message = shell.peekMessage()) != null && (message instanceof Message.Move || message instanceof Message.Rest)) {
            if (message instanceof Message.Move) {
                Monster monster = ((Message.Move) message).monster;
                moveTracker.add(monster);
                shell.removeMessage();
                moveTracker.add(monster);
                if (monster == shell.getPlayer() && !sight1.equals(shell.getSight())) {
                    moveTracker.addAll(shell.getMonsters());
                }
            } else {
                shell.removeMessage();
            }
        }

        updateStatus();
        updateStage();
        glView.requestRender();

        final Region sight2 = shell.getSight();
        final float x2 = (shell.getPlayer().getPosition().x * 6 + sight2.west + sight2.east) / 8f;
        final float y2 = (shell.getPlayer().getPosition().y * 6 + sight2.north + sight2.south) / 8f;
        final float scale2 = detectScale();

        moveAnimator.start(new Animator.Callback() {
            @Override
            public void onAnimationStart() {
            }
            @Override
            public void onAnimationUpdate(float fraction) {
                List<Sprite> sprites = new ArrayList<>();
                for (Monster monster : moveTracker.getMonsters()) {
                    PointF p = moveTracker.getPoint(monster, fraction);
                    sprites.add(createSprite(monster, p.x, p.y));
                }
                renderer.setSprites(sprites);

                renderer.setMask(createMask(
                        sight1.west + (sight2.west - sight1.west) * fraction,
                        sight1.north + (sight2.north - sight1.north) * fraction,
                        sight1.east + (sight2.east - sight1.east) * fraction,
                        sight1.south + (sight2.south - sight1.south) * fraction));

                //glView.requestRender();
            }
            @Override
            public void onAnimationEnd() {
                updateMonsterSprites();
                updateMask();
                //glView.requestRender();

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handleMessage();
                    }
                }, 20);
            }
        }, 200);

        cameraAnimator.start(new Animator.Callback() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationUpdate(float fraction) {
                scroller.updateState(new Scroller.State(
                        x1 + (x2 - x1) * fraction,
                        y1 + (y2 - y1) * fraction,
                        scale1 + (scale2 - scale1) * fraction));

                //glView.requestRender();
            }

            @Override
            public void onAnimationEnd() {
                //scroller.updateState(new Scroller.State(x2, y2, scale2));
            }
        }, 2000, new DecelerateInterpolator(2));
    }

    private void updateStatus() {
        Monster player = shell.getPlayer();
        statusView.setLife(player.getLife());
        statusView.setMaxLife(player.getMaxLife());
    }

    private void handleInjured() {
        Message.Injured message = (Message.Injured) shell.removeMessage();
        Monster monster = message.monster;
        Position position = monster.getPosition();
        PointF p = getScreenPoint(position.x, position.y, 0.5f);

        final TextView textView = new TextView(getContext());
        textView.setText("" + message.quantity);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        textView.setTextColor(0xffa51d1d);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        addView(textView, new LayoutParams(
                LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        TranslateAnimation animation = new TranslateAnimation(
                Animation.ABSOLUTE, p.x,
                Animation.ABSOLUTE, p.x,
                Animation.ABSOLUTE, p.y,
                Animation.ABSOLUTE, p.y - 70 * getResources().getDisplayMetrics().density);
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator(2));
        textView.startAnimation(animation);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                removeView(textView);
            }
        }, 1000);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                handleMessage();
            }
        }, 200);

        if (monster == shell.getPlayer()) {
            updateStatus();
        }
    }

    private void updateMonsterSprites() {
        List<Sprite> sprites = new ArrayList<>();
        for (Monster monster : shell.getMonsters()) {
            sprites.add(createSprite(monster));
        }
        renderer.setSprites(sprites);
    }

    private void updateStage() {
        renderer.setStageMesh(createStageMesh(shell.getMap(), textureAtlas));
    }

    private void updateMask() {
        renderer.setMask(createMask(shell.getSight()));
    }

    public boolean isAnimating() {
        return moveAnimator.isRunning();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        cameraAnimator.cancel();
        return scroller.handleTouchEvent(event) || super.onTouchEvent(event);
    }


    private class GLAnimator {
        private class Entry implements MainViewRenderer.Task {
            private final Animator.Callback callback;
            private final long duration;
            private final Interpolator interpolator;

            private long startTime = -1;
            private volatile boolean cancelled;
            private volatile boolean end;

            private Entry(Animator.Callback callback, long duration, Interpolator interpolator) {
                this.callback = callback;
                this.duration = duration;
                this.interpolator = interpolator;
            }

            @Override
            public void run(long time) {
                if (startTime < 0) {
                    if (cancelled) {
                        end = true;
                        return;
                    }
                    startTime = time;
                    callback.onAnimationStart();
                } else if (cancelled || startTime + duration <= time) {
                    callback.onAnimationEnd();
                    end = true;
                    return;
                } else {
                    float fraction = (float) (time - startTime) / duration;
                    if (interpolator != null) {
                        fraction = interpolator.getInterpolation(fraction);
                    }
                    callback.onAnimationUpdate(fraction);
                }

                renderer.addTask(this);
                glView.requestRender();
            }
        }

        private Entry entry;

        public void start(Animator.Callback callback, long duration) {
            start(callback, duration, null);
        }

        public synchronized void start(Animator.Callback callback, long duration, Interpolator interpolator) {
            cancel();

            this.entry = new Entry(callback, duration, interpolator);

            renderer.addTask(entry);
            glView.requestRender();
        }

        public synchronized void cancel() {
            if (entry != null) {
                entry.cancelled = true;
            }
        }

        public synchronized boolean isRunning() {
            return entry != null && !entry.end;
        }
    }
}
