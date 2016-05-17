package info.elekiuo.grandgrotto.geometry;

public class Matrix<T> {
    public final int cols;
    public final int rows;
    protected final T[] array;
    protected final int offset;
    protected final int stride;

    protected Matrix(int cols, int rows, Object[] array, int offset, int stride) {
        this.cols = cols;
        this.rows = rows;
        this.array = (T[]) array;
        this.offset = offset;
        this.stride = stride;
    }

    public Matrix(int cols, int rows) {
        this(cols, rows, new Object[cols * rows], 0, cols);
    }

    protected int index(int x, int y) {
        if (0 <= x && x < cols && 0 <= y && y < rows) {
            return offset + x + y * stride;
        } else {
            throw new IndexOutOfBoundsException("(" + x + "," + y + ") is out of bounds: (0,0)...(" + cols + "," + rows + ")");
        }
    }

    public T get(int x, int y) {
        return array[index(x, y)];
    }

    public T get(Position position) {
        return get(position.x, position.y);
    }

    public Matrix<T> view(Region region) {
        if (region.west < 0 || region.east > cols || region.north < 0 || region.south > rows) {
            throw new IndexOutOfBoundsException(region + "is not contained in bounds: (0,0)...(" + cols + "," + rows + ")");
        }
        return new Matrix<>(region.getCols(), region.getRows(), array, index(region.west, region.north), stride);
    }
}
