import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Prev on 2017. 9. 6..
 */


abstract class Node implements Serializable {
    static final long serialVersionUID = 1L;

    private NonLeafNode parent;

    Node(NonLeafNode parent) {
        this.parent = parent;
    }

    public NonLeafNode getParent() {
        return this.parent;
    }
    public void setParent(NonLeafNode node) {
        this.parent = node;
    }

    abstract public int[] getKeys();
    abstract public int getKeyCounts();

    //abstract public ArrayList<Pair<Integer, Object>> p();

    public ArrayList<Node> getSiblings() {
        if (this.parent == null)
            return new ArrayList<>();
        else
            return this.parent.getChildren();
    }

    public Pair<Node, Node> getNeighbors() {
        Pair<Node, Node> ret = new Pair<>(null, null);
        ArrayList<Node> siblings = this.getSiblings();

        int index = siblings.indexOf(this);

        if (index > 0)
            ret.left = siblings.get(index - 1);
        if (index < siblings.size() - 1)
            ret.right = siblings.get(index + 1);

        return ret;
    }
}


class NonLeafNode extends Node {
    ArrayList<Pair<Integer, Node>> p;
    Node r;

    NonLeafNode(NonLeafNode parent) {
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

    public ArrayList<Node> getChildren() {
        ArrayList<Node> ret = new ArrayList<>();

        for (Pair<Integer, Node> pair: this.p)
            ret.add(pair.right);

        if (this.r != null)
            ret.add(this.r);

        return ret;
    }

    public int getChildrenCounts() {
        int ret = this.getKeyCounts();
        if (this.r != null)
            ++ret;
        return ret;
    }

    public void insert(Pair<Integer, Node> pair) {
        this.p.add(pair);
        this.p.sort(Comparator.comparingInt(o -> o.left));
    }

    public void insert(int key, Node node) {
        this.insert(new Pair<>(key, node));
    }
}

class LeafNode extends Node {
    ArrayList<Pair<Integer, Integer>> p;

    LeafNode l;
    LeafNode r;

    LeafNode(NonLeafNode parent) {
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

    public void insert(Pair<Integer, Integer> pair) {
        this.p.add(pair);
        this.p.sort(Comparator.comparingInt(o -> o.left));
    }

    public void insert(int key, int value) {
        this.insert(new Pair<>(key, value));
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