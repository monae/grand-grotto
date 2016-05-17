package info.elekiuo.grandgrotto.geometry;

import org.junit.Test;

import static org.junit.Assert.*;

public class MatrixTest {
    @Test
    public void testView() {
        MutableMatrix<String> matrix = new MutableMatrix<>(10, 4);
        for (int y = 0; y < matrix.rows; y++) {
            for (int x = 0; x < matrix.cols; x++) {
                matrix.set(x, y, y + "" + x);
            }
        }
        assertEquals("" +
                "00 01 02 03 04 05 06 07 08 09 \n" +
                "10 11 12 13 14 15 16 17 18 19 \n" +
                "20 21 22 23 24 25 26 27 28 29 \n" +
                "30 31 32 33 34 35 36 37 38 39 \n" +
                "", dump(matrix));

        MutableMatrix<String> subMatrix = matrix.view(new Region(1, 1, 9, 4));
        assertEquals(8, subMatrix.cols);
        assertEquals(3, subMatrix.rows);

        assertEquals("" +
                "11 12 13 14 15 16 17 18 \n" +
                "21 22 23 24 25 26 27 28 \n" +
                "31 32 33 34 35 36 37 38 \n" +
                "", dump(subMatrix));

        subMatrix.set(6, 1, "**");
        assertEquals("" +
                "11 12 13 14 15 16 17 18 \n" +
                "21 22 23 24 25 26 ** 28 \n" +
                "31 32 33 34 35 36 37 38 \n" +
                "", dump(subMatrix));
        assertEquals("" +
                "00 01 02 03 04 05 06 07 08 09 \n" +
                "10 11 12 13 14 15 16 17 18 19 \n" +
                "20 21 22 23 24 25 26 ** 28 29 \n" +
                "30 31 32 33 34 35 36 37 38 39 \n" +
                "", dump(matrix));

        subMatrix.fill("##");
        assertEquals("" +
                "## ## ## ## ## ## ## ## \n" +
                "## ## ## ## ## ## ## ## \n" +
                "## ## ## ## ## ## ## ## \n" +
                "", dump(subMatrix));
        assertEquals("" +
                "00 01 02 03 04 05 06 07 08 09 \n" +
                "10 ## ## ## ## ## ## ## ## 19 \n" +
                "20 ## ## ## ## ## ## ## ## 29 \n" +
                "30 ## ## ## ## ## ## ## ## 39 \n" +
                "", dump(matrix));
    }

    private static String dump(Matrix<?> matrix) {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < matrix.rows; y++) {
            for (int x = 0; x < matrix.cols; x++) {
                builder.append(matrix.get(x, y)).append(' ');
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}
