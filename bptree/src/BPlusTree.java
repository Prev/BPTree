import java.io.Serializable;
import java.util.*;

/**
 * Created by Prev on 2017. 9. 4..
 */
public class BPlusTree implements Serializable {
    private static final long serialVersionUID = 1L;

    int maxChildNodes;
    ArrayList<Node> nodes;
    NonLeafNode rootNode;

    BPlusTree(int maxChildNodes) {
        this.maxChildNodes = maxChildNodes;
        this.nodes = new ArrayList<>();

        this.rootNode = new NonLeafNode();
    }

    public void insert(int key, int value) {
        // TODO
        LeafNode node = new LeafNode();
        node.p.add(new Pair<>(key, value));

        if (this.nodes.size() > 0)
            node.r = this.nodes.get(this.nodes.size()-1);

        this.nodes.add(node);
        rootNode.p.add(new Pair<>(key, node));
    }

    /*public String serialize() {
        String ret = Integer.toString(maxChildNodes) + "\r\n";
        ret += Integer.toString(nodes.size()) + "\r\n";
        ret += Integer.toString(Node.nextUIDNo) + "\r\n";

        for(int i = 0; i < nodes.size(); i++) {
            ret += nodes.get(i).serialize() + "\r\n";
        }

        return ret;
    }

    public static BPlusTree parseFrom(String serializedData) {
        String[] lines = serializedData.split("\r\n");
        int maxChildNodes = Integer.parseInt(lines[0]);
        int nodeNum = Integer.parseInt(lines[1]);
        int nextUIDNo = Integer.parseInt(lines[2]);

        BPlusTree tree = new BPlusTree(maxChildNodes);
        Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();


        for (int i = 0; i < nodeNum; i++) {
            String line = lines[3 + i];
            String[] tokens = line.split(",");

            String type = tokens[0];
            Node node;
            int j;

            switch (type) {
                case "l" :
                    node = new LeafNode();

                    for (j = 4; j < tokens.length; j++)
                        ((LeafNode)node).p.add(new Pair<Integer, Integer>(
                                Integer.parseInt(tokens[j]),
                                Integer.parseInt(tokens[j+1])
                        ));
                    break;


                case "n" :
                    node = new LeafNode();
                    break;

                default:
                    System.out.println("Parse Warning: Unknown Node type: " + type);
                    continue;
            }

            node.uniqueID = Integer.parseInt(tokens[1]);
            node.m = Integer.parseInt(tokens[3]);
            nodeMap.put(node.uniqueID, node);
        }
        return tree;
    }*/
}


class Node implements Serializable {
    private static final long serialVersionUID = 1L;

//    public static int nextUIDNo = 1;
//    int uniqueID;

    int m;  // number of children
    Node r; // rightmost child

    Node() {
//        this.uniqueID = Node.nextUIDNo;
        this.m = 0;
        this.r = null;

//        Node.nextUIDNo++;
    }

    /*public String serialize() {
        return Integer.toString(uniqueID) + "," + r.uniqueID + "," + Integer.toString(m);
    }*/
}


class NonLeafNode extends Node {
    ArrayList<Pair<Integer, Node>> p;

    NonLeafNode() {
        super();
        this.p = new ArrayList<>();
    }

    /*public String serialize() {
        String ret = "n," + super.serialize();

        for(int i = 0; i < p.size(); i++) {
            ret += "," + Integer.toString(p.get(i).left) + "," + Integer.toString(p.get(i).right.uniqueID);
        }
        return ret;
    }*/
}

class LeafNode extends Node {
    ArrayList<Pair<Integer, Integer>> p;

    LeafNode() {
        super();
        this.p = new ArrayList<>();
    }

    /*public String serialize() {
        String ret = "l," + super.serialize();

        for(int i = 0; i < p.size(); i++) {
            ret += "," + Integer.toString(p.get(i).left) + "," + Integer.toString(p.get(i).right);
        }
        return ret;
    }*/
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