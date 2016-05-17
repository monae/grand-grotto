package info.elekiuo.grandgrotto.algorithm;

public class UnionFind {
    private final int[] parents;
    private final int[] ranks;
    private int components;

    public UnionFind(int size) {
        parents = new int[size];
        ranks = new int[size];

        for (int i = 0; i < size; i++) {
            parents[i] = i;
        }

        components = size;
    }

    public int find(int i) {
        int p = parents[i];
        if (i == p) {
            return i;
        } else {
            return parents[i] = find(p);
        }
    }

    public boolean union(int i, int j) {
        int root1 = find(i);
        int root2 = find(j);

        if (root2 == root1) {
            return false;
        }

        if (ranks[root1] > ranks[root2]) {
            parents[root2] = root1;
        } else if (ranks[root2] > ranks[root1]) {
            parents[root1] = root2;
        } else {
            parents[root2] = root1;
            ranks[root1]++;
        }

        components--;
        return true;
    }

    public int getComponents() {
        return components;
    }
}
