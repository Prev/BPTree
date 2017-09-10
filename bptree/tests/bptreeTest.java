import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.Assert.*;


public class bptreeTest {

    static final String TEST_INDEX_FILE = "tests/datafiles/_index.dat";
    static final String TEST_INSERT_FILE = "tests/datafiles/insert.csv";
    static final String TEST_INSERT_FILE2 = "tests/datafiles/insert2.csv";
    static final String TEST_INSERT_FILE0 = "tests/datafiles/insert0.csv";
    static final String TEST_DELETE_FILE = "tests/datafiles/delete.csv";


    void guaranteeBPlusTree(BPlusTree tree) {
        Queue<Node> que = new LinkedList<>();
        que.offer(tree.rootNode);

        int b = tree.getMaxChildNodes();

        while (!que.isEmpty()) {
            Node node = que.poll();

            if (node == tree.rootNode) {
                if (node instanceof NonLeafNode)
                    assertTrue(((NonLeafNode) node).getChildrenCounts() >= 2);

                else if (node instanceof LeafNode)
                    assertTrue(node.getKeyCounts() <= b-1);

            } else if (node instanceof NonLeafNode) {
                int childrenCounts = ((NonLeafNode) node).getChildrenCounts();

                assertTrue(childrenCounts >= b / 2);
                assertTrue(childrenCounts <= b);

            } else if (node instanceof LeafNode) {
                assertTrue(node.getKeyCounts() >= (b-1) / 2);
                assertTrue(node.getKeyCounts() <= b-1);

            }

        }
    }

    @Test
    public void testCreation() {
        int maxChildNodes = 10;

        bptree.create(TEST_INDEX_FILE, maxChildNodes);
        BPlusTree tree = DataFileUtil.loadTree(TEST_INDEX_FILE);

        assertEquals(tree.getMaxChildNodes(), maxChildNodes);
    }


    @Test
    public void testInsertion() {
        bptree.create(TEST_INDEX_FILE, 4);
        bptree.insert(TEST_INDEX_FILE, TEST_INSERT_FILE);

        BPlusTree tree = DataFileUtil.loadTree(TEST_INDEX_FILE);

        tree.traversal();
        ArrayList<Pair<Integer, Integer>> actual = tree.getList();

        assertEquals(1, tree.rootNode.getKeyCounts());

        for (int i = 0; i < 5; i++) {
            assertEquals(i+1, (long) actual.get(i).left);
            assertEquals(i+1, (long) actual.get(i).right);
        }

        guaranteeBPlusTree(tree);
    }

    @Test
    public void testInsertion2() {
        bptree.create(TEST_INDEX_FILE, 3);
        bptree.insert(TEST_INDEX_FILE, TEST_INSERT_FILE);

        BPlusTree tree = DataFileUtil.loadTree(TEST_INDEX_FILE);

        tree.traversal();
        ArrayList<Pair<Integer, Integer>> actual = tree.getList();

        assertEquals(2, tree.rootNode.getKeyCounts());

        for (int i = 0; i < 5; i++) {
            assertEquals(i+1, (long) actual.get(i).left);
            assertEquals(i+1, (long) actual.get(i).right);
        }

        guaranteeBPlusTree(tree);
    }

    @Test
    public void testInsertion3() {
        bptree.create(TEST_INDEX_FILE, 2);
        bptree.insert(TEST_INDEX_FILE, TEST_INSERT_FILE2);

        BPlusTree tree = DataFileUtil.loadTree(TEST_INDEX_FILE);

        tree.traversal();
        ArrayList<Pair<Integer, Integer>> actual = tree.getList();

        for (int i = 1; i < actual.size(); i++) {
            assertTrue(actual.get(i-1).left < actual.get(i).left);
            assertTrue(actual.get(i).left * 10 == actual.get(i).right);
        }

        guaranteeBPlusTree(tree);
    }



    @Test
    public void testDeletion() {
        bptree.create(TEST_INDEX_FILE, 3);
        bptree.insert(TEST_INDEX_FILE, TEST_INSERT_FILE);
        bptree.delete(TEST_INDEX_FILE, TEST_DELETE_FILE);

        BPlusTree tree = DataFileUtil.loadTree(TEST_INDEX_FILE);

        tree.traversal();

        //assertEquals(tree.rootNode.getKeyCounts(), 3);

        assertTrue(tree.search(1).hit);
        assertFalse(tree.search(2).hit);
        assertTrue(tree.search(3).hit);
        assertTrue(tree.search(4).hit);
        assertFalse(tree.search(5).hit);
    }


    @Test
    public void testSearch() {
        bptree.create(TEST_INDEX_FILE, 8);
        bptree.insert(TEST_INDEX_FILE, TEST_INSERT_FILE);

        BPlusTree tree = DataFileUtil.loadTree(TEST_INDEX_FILE);

        for (int i = 1; i <= 5; i++)
            assertEquals(tree.search(i).value, i);
    }

    @Test
    public void testSearch2() {
        bptree.create(TEST_INDEX_FILE, 8);
        bptree.insert(TEST_INDEX_FILE, TEST_INSERT_FILE0);

        //BPlusTree tree = DataFileUtil.loadTree(TEST_INDEX_FILE);

        bptree.search(TEST_INDEX_FILE, 68);
    }

    @Test
    public void testSorted() {
        bptree.create(TEST_INDEX_FILE, 8);
        bptree.insert(TEST_INDEX_FILE, TEST_INSERT_FILE);

        BPlusTree tree = DataFileUtil.loadTree(TEST_INDEX_FILE);

        ArrayList<Pair<Integer, Integer>> actual = tree.getList();

        for (int i = 0; i < 5; i++) {
            assertEquals(i+1, (long) actual.get(i).left);
            assertEquals(i+1, (long) actual.get(i).right);
        }
    }
}
