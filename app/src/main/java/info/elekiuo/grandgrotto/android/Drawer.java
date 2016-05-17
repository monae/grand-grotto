package info.elekiuo.grandgrotto.android;

import static android.opengl.GLES20.*;

public class Drawer {
    private static final String VERTEX_SHADER_CODE = "" +
            "attribute vec4 position;" +
            "attribute vec2 coord;" +
            "attribute vec4 color;" +
            "varying vec2 outCoord;" +
            "varying vec4 outColor;" +
            "uniform mat4 matrix;" +
            "void main() {" +
            "  gl_Position = matrix * position;" +
            "  outCoord = coord;" +
            "  outColor = color;" +
            "}";

    private static final String FRAGMENT_SHADER_CODE = "" +
            "precision mediump float;" +
            "varying vec2 outCoord;" +
            "varying vec4 outColor;" +
            "uniform sampler2D texture;" +
            "void main() {" +
            "  vec4 color = outColor * texture2D(texture, outCoord);" +
            "  if (color.a > 0.01) {" +
            "    gl_FragColor = color;" +
            "  } else {" +
            "    discard;" +
            "  }" +
            "}";

    private final int program;
    private final int positionAttrib;
    private final int coordAttrib;
    private final int colorAttrib;
    private final int matrixUniform;
    private final int textureUniform;

    {
        program = GLES20Utils.loadProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);

        positionAttrib = glGetAttribLocation(program, "position");
        coordAttrib = glGetAttribLocation(program, "coord");
        colorAttrib = glGetAttribLocation(program, "color");
        matrixUniform = glGetUniformLocation(program, "matrix");
        textureUniform = glGetUniformLocation(program, "texture");
    }

    public void useProgram() {
        glUseProgram(program);
        glUniform1i(textureUniform, 0);
    }

    public void setVertexBuffer(VertexBuffer vertexBuffer, float u, float v, int color) {
        glEnableVertexAttribArray(positionAttrib);
        glDisableVertexAttribArray(coordAttrib);
        glDisableVertexAttribArray(colorAttrib);
        glVertexAttribPointer(positionAttrib, 3, GL_FLOAT, false, 3 * 4, vertexBuffer.buffer.position(0));
        glVertexAttrib2f(coordAttrib, u, v);
        glVertexAttrib4f(colorAttrib, ((color >>> 16) & 255) / 255f, ((color >>> 8) & 255) / 255f, (color & 255) / 255f, (color >>> 24) / 255f);
    }

    public void setVertexBuffer(VertexColorBuffer vertexBuffer, float u, float v) {
        glEnableVertexAttribArray(positionAttrib);
        glDisableVertexAttribArray(coordAttrib);
        glEnableVertexAttribArray(colorAttrib);
        glVertexAttribPointer(positionAttrib, 3, GL_FLOAT, false, 4 * 4, vertexBuffer.buffer.position(0));
        glVertexAttrib2f(coordAttrib, u, v);
        glVertexAttribPointer(colorAttrib, 4, GL_UNSIGNED_BYTE, true, 4 * 4, vertexBuffer.buffer.position(3));
    }

    public void setVertexBuffer(VertexTexBuffer vertexBuffer, int color) {
        glEnableVertexAttribArray(positionAttrib);
        glEnableVertexAttribArray(coordAttrib);
        glDisableVertexAttribArray(colorAttrib);
        glVertexAttribPointer(positionAttrib, 3, GL_FLOAT, false, 5 * 4, vertexBuffer.buffer.position(0));
        glVertexAttribPointer(coordAttrib, 2, GL_FLOAT, false, 5 * 4, vertexBuffer.buffer.position(3));
        glVertexAttrib4f(colorAttrib, ((color >>> 16) & 255) / 255f, ((color >>> 8) & 255) / 255f, (color & 255) / 255f, (color >>> 24) / 255f);
    }

    public void setVertexBuffer(VertexTexColorBuffer vertexBuffer) {
        glEnableVertexAttribArray(positionAttrib);
        glEnableVertexAttribArray(coordAttrib);
        glEnableVertexAttribArray(colorAttrib);
        glVertexAttribPointer(positionAttrib, 3, GL_FLOAT, false, 6 * 4, vertexBuffer.buffer.position(0));
        glVertexAttribPointer(coordAttrib, 2, GL_FLOAT, false, 6 * 4, vertexBuffer.buffer.position(3));
        glVertexAttribPointer(colorAttrib, 4, GL_UNSIGNED_BYTE, true, 6 * 4, vertexBuffer.buffer.position(5));
    }

    public void setMatrix(float[] matrix) {
        glUniformMatrix4fv(matrixUniform, 1, false, matrix, 0);
    }
}
