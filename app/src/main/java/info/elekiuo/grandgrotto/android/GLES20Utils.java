package info.elekiuo.grandgrotto.android;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;

public class GLES20Utils {
    private GLES20Utils() {
    }

    public static int loadProgram(String vertexShaderCode, String fragmentShaderCode) {
        int vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode);
        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);

        int[] linkStatuses = new int[1];
        glGetProgramiv(program, GL_LINK_STATUS, linkStatuses, 0);
        if (linkStatuses[0] != GL_TRUE) {
            String log = glGetProgramInfoLog(program);
            glDeleteProgram(program);
            throw new RuntimeException(log);
        }
        return program;
    }

    public static int loadShader(int type, String shaderCode){
        int shader = glCreateShader(type);
        glShaderSource(shader, shaderCode);
        glCompileShader(shader);

        int[] compileStatuses = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, compileStatuses, 0);
        if (compileStatuses[0] != GL_TRUE) {
            String log = glGetShaderInfoLog(shader);
            glDeleteShader(shader);
            throw new RuntimeException(log);
        }
        return shader;
    }

    public static int createTexture(Bitmap bitmap) {
       int[] textures = new int[1];
        glGenTextures(1, textures, 0);

        glBindTexture(GL_TEXTURE_2D, textures[0]);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        return textures[0];
    }

    public static float packColor(int color) {
        int a = color >>> 24;
        int r = (color >>> 16) & 255;
        int g = (color >>> 8) & 255;
        int b = color & 255;
        return Float.intBitsToFloat(ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ?
                (r << 24) | (g << 16) | (b << 8) | a : (a << 24) | (b << 16) | (g << 8) | r);
    }

    public static int multiplyAlpha(int color) {
        int a = color >>> 24;
        int r = ((color >>> 16) & 255) * a / 255;
        int g = ((color >>> 8) & 255) * a / 255;
        int b = (color & 255) * a / 255;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int createTexture(int color) {
        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(color);
        int texture = createTexture(bitmap);
        bitmap.recycle();
        return texture;
    }

    public static ByteBuffer newByteBuffer(int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }

    public static FloatBuffer newFloatBuffer(int size) {
        return newByteBuffer(size * 4).asFloatBuffer();
    }

    public static ShortBuffer newShortBuffer(int size) {
        return newByteBuffer(size * 2).asShortBuffer();
    }
}
