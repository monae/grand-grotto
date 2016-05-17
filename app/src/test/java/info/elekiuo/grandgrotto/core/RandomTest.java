package info.elekiuo.grandgrotto.core;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class RandomTest {
    @Test
    public void testNext() {
        Random random = new Random(123);
        for (int i = 0; i < 100; i++) {
            assertEquals(0, random.next(1));
        }
        for (int i = 0; i < 100; i++) {
            assertThat(random.next(8), both(greaterThanOrEqualTo(0)).and(lessThan(8)));
        }
        for (int i = 0; i < 100; i++) {
            assertThat(random.next(57), both(greaterThanOrEqualTo(0)).and(lessThan(57)));
        }
    }

    @Test
    public void testNext2() {
        Random random = new Random(123);
        for (int i = 0; i < 100; i++) {
            assertEquals(0, random.next2(1));
        }
        for (int i = 0; i < 100; i++) {
            assertThat(random.next2(8), both(greaterThanOrEqualTo(0)).and(lessThan(8)));
        }
        for (int i = 0; i < 100; i++) {
            assertThat(random.next2(57), both(greaterThanOrEqualTo(0)).and(lessThan(57)));
        }
    }
}
