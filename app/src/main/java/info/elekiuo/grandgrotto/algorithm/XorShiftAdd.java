package info.elekiuo.grandgrotto.algorithm;

public class XorShiftAdd {
    public int length() {
        return 4;
    }

    public void next(int[] state) {
        int s = state[3];
        for (int i = 0; i < 4; i++) {
            int t = state[i];
            t ^= t << 15;
            t ^= t >>> 18;
            t ^= s << 11;
            state[i] = s = t;
        }
    }

    public void initialize(int[] state, long seed) {
        long seed2 = seed * 66859691717410547L + 1234567891011121314L;
        state[0] = (int) seed;
        state[1] = (int) (seed >> 32);
        state[2] = (int) seed2;
        state[3] = (int) (seed2 >> 32);
        for (int i = 0; i < 16; i++) {
            next(state);
        }
    }
}
