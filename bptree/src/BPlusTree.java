import java.io.Serializable;
import java.util.*;

/**
 * Created by Prev on 2017. 9. 4..
 */
public class BPlusTree implements Serializable {
    private static final long serialVersionUID = 1L;

    public Node rootNode;
    private int maxChildNodes;

    BPlusTree(int maxChildNodes) {
        this.maxChildNodes = maxChildNodes;
        this.rootNode = new LeafNode(null);
    }

    public int getMaxChildNodes() {
        return maxChildNodes;
    }

    /**
     * Traversal nodes for debug
     */
    public void traversal() {
        Queue<Pair<Node, Integer>> que = new LinkedList<>();
        que.offer(new Pair<>(this.rootNode, 0));

        while (!que.isEmpty()) {
            Pair<Node, Integer> polled = que.poll();
            Node node = polled.left;
            Integer depth = polled.right;

            for (int i = 0; i < depth; i++)
                System.out.print("  ");

            if (node instanceof NonLeafNode) {
                System.out.printf("N#%d \t", node.hashCode() % 1000);

                for (Pair<Integer, Node> p : ((NonLeafNode) node).p) {
                    System.out.printf("(%d, #%d) ", p.left, p.right.hashCode() % 1000);
                    que.offer(new Pair<>(p.right, depth+1));
                }
                if (((NonLeafNode) node).r != null) {
                    System.out.printf(" ($, #%d)", ((NonLeafNode) node).r.hashCode() % 1000);
                    que.offer(new Pair<>(((NonLeafNode) node).r, depth+1));
                }

            } else if (node instanceof LeafNode) {
                System.out.printf("L#%d \t", node.hashCode() % 1000);

                for (Pair<Integer, Integer> p : ((LeafNode) node).p)
                    System.out.printf("(%d, %d) ", p.left, p.right);
            }

//            if (node.getParent() != null)
//                System.out.printf("  [[#%d]]", node.getParent().hashCode() % 1000);

            System.out.println("");
        }
    }


    /**
     * Insert (key, value) pair to tree
     * @param key
     * @param value
     * @return true if insertion is success
     *         false if ignored because key is duplicated
     */
    public Boolean insert(int key, int value) {
        LeafNode node = this.search(key).leafNode;

        if (Arrays.binarySearch(node.getKeys(), key) >= 0)
            // Ignore if key exists
            return false;

        _insertValue(key, value, node);

//        this.traversal();
//        System.out.println("------------------------------");
        return true;
    }

    private void _insertValue(int key, int value, LeafNode node) {
        node.insert(key, value);

        if (node.getKeyCounts() >= this.maxChildNodes)
            _splitNode(node);
    }

    private void _splitNode(Node node) {
        Node newNode = null;

        if (node == this.rootNode) {
            this.rootNode = new NonLeafNode(null);
            node.setParent((NonLeafNode) this.rootNode);

            ((NonLeafNode) this.rootNode).r = node;
        }


        if (node instanceof LeafNode) {
            newNode = new LeafNode(node.getParent());
            ((LeafNode) newNode).p = new ArrayList<>(
                    ((LeafNode) node).p.subList(0, (node.getKeyCounts()+1) / 2)
            );
            ((LeafNode) node).p = new ArrayList<>(
                    ((LeafNode) node).p.subList((node.getKeyCounts()+1) / 2, node.getKeyCounts())
            );

            // Change directions
            if (((LeafNode) node).l != null) ((LeafNode) node).l.r = ((LeafNode) newNode);
            ((LeafNode) newNode).l = ((LeafNode) node).l;
            ((LeafNode) newNode).r = ((LeafNode) node);
            ((LeafNode) node).l = ((LeafNode) newNode);


        }else if (node instanceof NonLeafNode) {
            newNode = new NonLeafNode(node.getParent());
            ((NonLeafNode) newNode).p = new ArrayList<>(
                    ((NonLeafNode) node).p.subList(0, (node.getKeyCounts()+1) / 2)
            );
            ((NonLeafNode) node).p = new ArrayList<>(
                    ((NonLeafNode) node).p.subList((node.getKeyCounts()+1) / 2, node.getKeyCounts())
            );

            for (Node child: ((NonLeafNode) newNode).getChildren())
                child.setParent((NonLeafNode) newNode);

            //((NonLeafNode) newNode).r = ((NonLeafNode) newNode).p.get( newNode.getKeyCounts() - 1 ).right;
        }

        _insertNode(newNode, node, node.getParent());

        if (node.getParent().getKeyCounts() >= this.maxChildNodes) {
            _splitNode(node.getParent());
        }
    }

    private void _insertNode(Node leftNode, Node rightNode, NonLeafNode parentNode) {
        //int key = rightNode.getKeys()[0];

        // Get left-most Node from root
        Node leftMostOfRightNode = rightNode;
        while (leftMostOfRightNode instanceof NonLeafNode)
            leftMostOfRightNode = ((NonLeafNode) leftMostOfRightNode).p.get(0).right;

        int key = leftMostOfRightNode.getKeys()[0];
        parentNode.insert(key, leftNode);
    }


    /**
     * Remove data by key
     * @param key
     * @return true if deletion is succeed
     *         false if key not found
     */
    public Boolean remove(int key) {
        LeafNode node = this.search(key).leafNode;
        int index = Arrays.binarySearch(node.getKeys(), key);

        if (index < 0)
            // Ignore if key do not exists
            return false;

        _removeValue(key, node);

//        this.traversal();
//        System.out.println("------------------------------------------");
        return true;
    }

    private void _removeValue(int key, LeafNode node) {
        int index = Arrays.binarySearch(node.getKeys(), key);
        node.p.remove(index);

        if (node.getKeyCounts() < (this.maxChildNodes-1) / 2 && node != this.rootNode)
            _balancingNode(node);
    }

    private void _balancingNode(Node node) {
        Pair<Node, Node> neighbors = node.getNeighbors();

        if (neighbors.right != null && neighbors.right.getKeyCounts() + node.getKeyCounts() < this.maxChildNodes )
            // case 1-1: merge (with right)
            _mergeNode(node, neighbors.right, node.getParent());

        else if (neighbors.left != null && neighbors.left.getKeyCounts() + node.getKeyCounts() < this.maxChildNodes )
            // case 1-2: merge (with left)
            _mergeNode(neighbors.left, node, node.getParent());

        else {
            // case 2-1: Redistribute
            Node targetNeighbor = (neighbors.right != null) ? neighbors.right : neighbors.left;

            if (targetNeighbor == null) return;

            if (node instanceof LeafNode)
                ((LeafNode) node).insert(
                        ((LeafNode) targetNeighbor).p.remove(targetNeighbor.getKeyCounts()-1)
                );
            else
                ((NonLeafNode) node).insert(
                        ((NonLeafNode) targetNeighbor).p.remove(targetNeighbor.getKeyCounts()-1)
                );

        }
    }

    private void _mergeNode(Node leftNode, Node rightNode, NonLeafNode parentNode) {
        if (leftNode instanceof LeafNode)
            for (Pair<Integer, Integer> pair: ((LeafNode) leftNode).p)
                ((LeafNode) rightNode).insert(pair);

        else if (leftNode instanceof  NonLeafNode)
            for (Pair<Integer, Node> pair: ((NonLeafNode) leftNode).p)
                ((NonLeafNode) rightNode).insert(pair);

        for (int i = 0; i < parentNode.p.size(); i++) {
            if (parentNode.p.get(i).right == leftNode) {
                parentNode.p.remove(i);
                break;
            }
        }

        if (parentNode == this.rootNode && parentNode.getChildrenCounts() == 1) {
            rightNode.setParent(null);
            this.rootNode = rightNode;
            return;
        }

        if (parentNode.getKeyCounts() < this.maxChildNodes / 2)
            _balancingNode(parentNode);
    }


    /**
     * Get list of (key, value) pairs
     * @return
     */
    public ArrayList<Pair<Integer, Integer>> getList() {
        ArrayList<Pair<Integer, Integer>> ret = new ArrayList<>();

        // Get left-most Node from root
        Node leftMostNode = this.rootNode;
        while (leftMostNode instanceof NonLeafNode)
            leftMostNode = ((NonLeafNode) leftMostNode).p.get(0).right;

        // Iterate
        LeafNode ln = (LeafNode) leftMostNode;
        do {
            for (Pair<Integer, Integer> p: ln.p)
                ret.add(p);
            ln = ln.r;

        }while (ln != null);

        return ret;
    }


    /**
     * Search for value by key
     * @param key
     * @return SearchResult Object{
     *     Boolean hit
     *     int value (result)
     *     LeafNode leafNode;
     *     ArrayList<Node> history;
     * }
     */
    public SearchResult search(int key) {
        ArrayList<Node> history = new ArrayList<>();
        LeafNode leafNode = _searchProc(key, this.rootNode, history);

        if (leafNode == null)
            return SearchResult.miss(leafNode, history);

        for (Pair<Integer, Integer> p: leafNode.p) {
            if (p.left == key)
                return SearchResult.hit(p.right, leafNode, history);
        }

        return SearchResult.miss(leafNode, history);
    }

    private LeafNode _searchProc(int key, Node node, ArrayList<Node> history) {
        if (history != null)
            history.add(node);

        if (node == null)
            return null;

        if (node instanceof LeafNode)
            return (LeafNode) node;

        else {
            for (Pair<Integer, Node> p: ((NonLeafNode) node).p) {
                if (key < p.left)
                    return _searchProc(key, p.right, history);
            }
            return _searchProc(key, ((NonLeafNode) node).r, history);
        }
    }
}


class SearchResult {
    Boolean hit;
    int value;
    LeafNode leafNode;
    ArrayList<Node> history;

    SearchResult(Boolean hit, int value, LeafNode leafNode, ArrayList<Node> history) {
        this.hit = hit;
        this.value = value;
        this.leafNode = leafNode;
        this.history = history;
    }

    static SearchResult hit(int value, LeafNode leafNode, ArrayList<Node> history) {
        return new SearchResult(true, value, leafNode, history);
    }

    static SearchResult miss(LeafNode leafNode, ArrayList<Node> history) {
        return new SearchResult(false, -1, leafNode, history);
    }
}