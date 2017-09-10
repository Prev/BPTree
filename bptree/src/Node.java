import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Prev on 2017. 9. 6..
 */


abstract class Node implements Serializable {
    static final long serialVersionUID = 1L;

    private Node parent;

    Node(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return this.parent;
    }
    public void setParent(Node node) {
        this.parent = node;
    }

    abstract public int[] getKeys();
    abstract public int getKeyCounts();

    //abstract public ArrayList<Pair<Integer, Object>> p();
}


class NonLeafNode extends Node {
    ArrayList<Pair<Integer, Node>> p;
    Node r;

    NonLeafNode(Node parent) {
        super(parent);
        this.r = null;
        this.p = new ArrayList<>();
    }

    public int[] getKeys() {
        int[] ret = new int[this.p.size()];
        for (int i = 0; i < ret.length; i++) {
            Pair<Integer, Node> pair = this.p.get(i);
            ret[i] = pair.left;
        }
        return ret;
    }

    public int getKeyCounts() {
        return p.size();
    }

    public int getChildrenCounts() {
        int ret = this.getKeyCounts();
        if (this.r != null)
            ++ret;
        return ret;
    }

    public void insert(int key, Node node) {
        this.p.add(new Pair<>(key, node));
        this.p.sort((o1, o2) -> o1.left - o2.left);
    }
}

class LeafNode extends Node {
    ArrayList<Pair<Integer, Integer>> p;

    LeafNode l;
    LeafNode r;

    LeafNode(Node parent) {
        super(parent);
        this.r = null;
        this.l = null;
        this.p = new ArrayList<>();
    }

    public int[] getKeys() {
        int[] ret = new int[this.p.size()];
        for (int i = 0; i < ret.length; i++) {
            Pair<Integer, Integer> pair = this.p.get(i);
            ret[i] = pair.left;
        }
        return ret;
    }

    public int getKeyCounts() {
        return p.size();
    }

    public void insert(int key, int value) {
        this.p.add(new Pair<>(key, value));
        this.p.sort((o1, o2) -> o1.left - o2.left);
    }
}


class Pair<L,R> implements Serializable {
    private static final long serialVersionUID = 1L;

    public L left;
    public R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int hashCode() { return left.hashCode() ^ right.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair) o;
        return this.left.equals(pairo.left) &&
                this.right.equals(pairo.right);
    }

}