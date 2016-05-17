package info.elekiuo.grandgrotto.geometry;

import java.util.Arrays;

public class MutableMatrix<T> extends Matrix<T> {
    private MutableMatrix(Matrix matrix) {
        super(matrix.cols, matrix.rows, matrix.array, matrix.offset, matrix.stride);
    }

    public MutableMatrix(int cols, int rows) {
        this(new Matrix(cols, rows));
    }

    public void set(int x, int y, T value) {
        array[index(x, y)] = value;
    }

    public void set(Position position, T value) {
        set(position.x, position.y, value);
    }

    public void fill(T value) {
        if (cols == stride) {
            int start = offset;
            int end = start + cols * rows;
            Arrays.fill(array, start, end, value);
        } else {
            for (int y = 0; y < rows; y++) {
                int start = offset + y * stride;
                int end = start + cols;
                Arrays.fill(array, start, end, value);
            }
        }
    }

    public void copyFrom(Matrix<? extends T> matrix) {
        if (cols != matrix.cols || rows != matrix.rows) {
            throw new IllegalArgumentException("The size does not match");
        }
        for (int y = 0; y < rows; y++) {
            System.arraycopy(
                    matrix.array, matrix.offset + y * matrix.stride,
                    array, offset + y * stride,
                    cols);
        }
    }

    public MutableMatrix<T> view(Region region) {
        return new MutableMatrix<>(super.view(region));
    }
}
