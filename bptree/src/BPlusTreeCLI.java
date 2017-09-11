/**
 * B+ tree CLI
 *
 * Usage: BPlusTreeCLI
 *    -c <index_file> <b>
 *    -i <index_file> <data_file>
 *    -d <index_file> <data_file>
 *    -s <index_file> <key>
 *    -r <index_file> <start_key> <end_key>
 *
 * @author Prev (0soo.2@prev.kr)
 */

public class BPlusTreeCLI {

    static final String PROGRAM_NAME = "BPlusTreeCLI";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println(CLIUtil.getHelpMessage());
            System.exit(-1);
        }

        switch (args[0]) {
            case "-c" :
                CLIUtil.guaranteeArgs(args, 3);
                create(
                        args[1],
                        Integer.parseInt(args[2])
                );
                break;

            case "-i" :
                CLIUtil.guaranteeArgs(args, 3);
                insert(args[1], args[2]);
                break;

            case "-d" :
                CLIUtil.guaranteeArgs(args, 3);
                delete(args[1], args[2]);
                break;

            case "-s" :
                CLIUtil.guaranteeArgs(args, 3);
                search(
                        args[1],
                        Integer.parseInt(args[2])
                );
                break;

            case "-r" :
                CLIUtil.guaranteeArgs(args, 4);
                rangedSearch(
                        args[1],
                        Integer.parseInt(args[2]),
                        Integer.parseInt(args[3])
                );
                break;

            default:
                System.out.println(CLIUtil.getHelpMessage());
                System.exit(-1);
        }
    }

    /**
     * Create B+ tree
     * @param indexFileName
     * @param b
     */
    static void create(String indexFileName, int b) {
        BPlusTree tree = new BPlusTree(b);
        DataFileUtil.saveTree(indexFileName, tree);
    }


    /**
     * Insert Dataset to B+ tree
     * @param indexFileName
     * @param dataFileName
     */
    static void insert(String indexFileName, String dataFileName) {
        BPlusTree tree = DataFileUtil.loadTree(indexFileName);
        int[][] data = DataFileUtil.loadIntCSV(dataFileName);
        int insertedRows = 0;

        for(int[] row: data) {
            if (tree.insert(row[0], row[1]))
                insertedRows++;
        }

        System.out.printf("%d lines are inserted\n", insertedRows);
        DataFileUtil.saveTree(indexFileName, tree);
    }

    /**
     * Delete Dataset from B+ tree
     * @param indexFileName
     * @param dataFileName
     */
    static void delete(String indexFileName, String dataFileName) {
        BPlusTree tree = DataFileUtil.loadTree(indexFileName);
        int[][] data = DataFileUtil.loadIntCSV(dataFileName);

        int deletedRows = 0;

        for(int[] row: data) {
            if (tree.remove(row[0]))
            deletedRows++;
        }

        System.out.printf("%d keys are deleted\n", deletedRows);
        DataFileUtil.saveTree(indexFileName, tree);
    }

    /**
     * Search from B+ tree
     * @param indexFileName
     * @param key
     */
    static void search(String indexFileName, int key) {
        BPlusTree tree = DataFileUtil.loadTree(indexFileName);

        for (Node node: tree.search(key).history) {
            if (node == null) {
                System.out.println("NOT FOUND");
                return;

            }else if (node instanceof LeafNode) {
                for (Pair<Integer, Integer> p: ((LeafNode) node).p) {
                    if (p.left == key) {
                        System.out.println(p.right);
                        return;
                    }
                }
                System.out.println("NOT FOUND");


            }else {
                StringBuilder msg = new StringBuilder();
                int[] keys = node.getKeys();
                for (int i = 0; i < keys.length; i++) {
                    msg.append(keys[i]);
                    if (i != keys.length - 1)
                        msg.append(',');
                }
                System.out.println(msg.toString());

            }
        }
    }

    /**
     * Search by range in B+ tree
     * @param indexFileName
     * @param startKey
     * @param endKey
     */
    static void rangedSearch(String indexFileName, int startKey, int endKey) {
        BPlusTree tree = DataFileUtil.loadTree(indexFileName);

        for (Pair<Integer, Integer> pair: tree.rangedSearch(startKey, endKey)) {
            System.out.printf("%d,%d\n", pair.left, pair.right);
        }
    }
}


class CLIUtil {
    static String getHelpMessage() {
        return "Usage: " + BPlusTreeCLI.PROGRAM_NAME + "\n\t" +
                "-c <index_file> <b>\n\t" +
                "-i <index_file> <data_file>\n\t" +
                "-d <index_file> <data_file>\n\t" +
                "-s <index_file> <key>\n\t" +
                "-r <index_file> <start_key> <end_key>\n";
    }

    static void guaranteeArgs(String[] args, int number) {
        if (args.length != number) {
            System.out.println(getHelpMessage());
            System.exit(-1);
        }
    }
}