import org.junit.Test;
import static org.junit.Assert.*;


public class bptreeTest {

    static final String TEST_INDEX_FILE = "tests/datafiles/_index.dat";
    static final String TEST_INSERT_FILE = "tests/datafiles/insert.csv";
    static final String TEST_DELETE_FILE = "tests/datafiles/delete.csv";

    @Test
    public void testCreation() {
        int maxChildNodes = 10;

        bptree.create(TEST_INDEX_FILE, maxChildNodes);
        BPlusTree tree = DataFileUtil.loadTree(TEST_INDEX_FILE);

        assertEquals(tree.maxChildNodes, maxChildNodes);
    }


    @Test
    public void testInsertion() {
        bptree.create(TEST_INDEX_FILE, 8);
        bptree.insert(TEST_INDEX_FILE, TEST_INSERT_FILE);

        BPlusTree tree = DataFileUtil.loadTree(TEST_INDEX_FILE);

        assertEquals(tree.rootNode.p.size(), 5);
    }


    @Test
    public void testDeletion() {
        bptree.create(TEST_INDEX_FILE, 8);
        bptree.insert(TEST_INDEX_FILE, TEST_INSERT_FILE);
        bptree.delete(TEST_INDEX_FILE, TEST_DELETE_FILE);

        BPlusTree tree = DataFileUtil.loadTree(TEST_INDEX_FILE);

        assertEquals(tree.rootNode.p.size(), 3);
    }
}
