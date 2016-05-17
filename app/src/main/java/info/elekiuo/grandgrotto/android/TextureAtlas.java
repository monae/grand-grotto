package info.elekiuo.grandgrotto.android;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextureAtlas {
    private final static int MAX_WIDTH = 512;

    private final Resources resources;
    private final Map<Integer, Rect> map;
    public final int width;
    public final int height;

    private static class Entry implements Comparable<Entry> {
        public final int id;
        public final int width;
        public final int height;

        private Entry(int id, int width, int height) {
            this.id = id;
            this.width = width;
            this.height = height;
        }

        @Override
        public int compareTo(Entry another) {
            if (this.height < another.height) {
                return 1;
            } else if (this.height > another.height) {
                return -1;
            } else if (this.width < another.width) {
                return 1;
            } else if (this.width > another.width) {
                return -1;
            } else if (this.id < another.id) {
                return -1;
            } else if (this.id > another.id) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public TextureAtlas(Resources resources, Map<Integer, Rect> map, int width, int height) {
        this.resources = resources;
        this.map = map;
        this.width = width;
        this.height = height;
    }

    public static TextureAtlas generate(Resources resources, int[] ids) {
        List<Entry> entries = new ArrayList<>();
        for (int id : ids) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(resources, id, opts);
            entries.add(new Entry(id, opts.outWidth, opts.outHeight));
        }
        Collections.sort(entries);

        int width = MAX_WIDTH;

        Map<Integer, Rect> map = new HashMap<>();
        int x = 0;
        int y = 0;
        int lineHeight = 0;
        for (Entry entry : entries) {
            Rect rect = new Rect(0, 0, entry.width, entry.height);
            if (width < x + entry.width) {
                x = 0;
                y = lineHeight;
                lineHeight = 0;
            }
            rect.offsetTo(x, y);
            map.put(entry.id, rect);
            x += entry.width;
            lineHeight = Math.max(lineHeight, entry.height);
        }

        int height = ceilToPowerOf2(y + lineHeight);

        return new TextureAtlas(resources, Collections.unmodifiableMap(map), width, height);
    }

    private static int ceilToPowerOf2(int v) {
        v--;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        v++;
        return v;
    }

    public Rect get(int bitmap) {
        return map.get(bitmap);
    }

    public int createTexture() {
        Bitmap textureBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(textureBitmap);
        for (Map.Entry<Integer, Rect> entry : map.entrySet()) {
            int id = entry.getKey();
            Rect rect = entry.getValue();
            Bitmap bitmap = BitmapFactory.decodeResource(resources, id);
            canvas.drawBitmap(bitmap, rect.left, rect.top, null);
            bitmap.recycle();
        }
        int texture = GLES20Utils.createTexture(textureBitmap);
        textureBitmap.recycle();

        return texture;
    }
}
