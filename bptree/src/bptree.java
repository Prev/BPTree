/**
 * Created by Prev on 2017. 9. 4..
 */


class CLIUtil {

    static final String INT_TYPE = "int";
    static final String STRING_TYPE = "string";
    static final String FILE_TYPE = "file";
    static final String WILDCARD = "*";

    static String getHelpMessage() {
        return "Usage: " + bptree.PROGRAM_NAME + "\n\t" +
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

public class bptree {

    static final String PROGRAM_NAME = "bptree";

    public static void main(String[] args) {
        //System.out.println(args[0]);

        if (args.length < 1) {
            System.out.println(CLIUtil.getHelpMessage());
            System.exit(-1);
        }

        BPlusTree tree;

        switch (args[0]) {
            case "-c" :
                // -c <index_file> <b>
                CLIUtil.guaranteeArgs(args, 3);

                create(
                        args[1],
                        Integer.parseInt(args[2])
                );
                break;

            case "-i" :
                // -i <index_file> <data_file>
                CLIUtil.guaranteeArgs(args, 3);

                insert(args[1], args[2]);
                break;

            case "-d" :
                // -d <index_file> <data_file>
                CLIUtil.guaranteeArgs(args, 3);
                delete(args[1], args[2]);
                break;

            case "-s" :
                // -s <index_file> <key>
                CLIUtil.guaranteeArgs(args, 3);
                search(
                        args[1],
                        Integer.parseInt(args[2])
                );
                break;

            case "-r" :
                // -r <index_file> <start_key> <end_key>
                CLIUtil.guaranteeArgs(args, 4);
                rangeSearch(
                        args[1],
                        Integer.parseInt(args[2]),
                        Integer.parseInt(args[3])
                );
                break;

            case "-l" :
                // TEMP, list datas
                // -s <index_file>
                CLIUtil.guaranteeArgs(args, 3);

                System.out.println("list");
                tree = DataFileUtil.loadTree(args[1]);

                for (Pair<Integer, Node> p: tree.rootNode.p) {
                    System.out.println(p.left + ": " + p.right);
                }
                break;

            default:
                System.out.println(CLIUtil.getHelpMessage());
                System.exit(-1);
        }
    }

    /**
     * Create B+ Tree
     * @param indexFileName
     * @param b
     */
    static void create(String indexFileName, int b) {
        System.out.println("create");

        BPlusTree tree = new BPlusTree(b);
        DataFileUtil.saveTree(indexFileName, tree);
    }


    /**
     * Insert Dataset to B+ Tree
     * @param indexFileName
     * @param dataFileName
     */
    static void insert(String indexFileName, String dataFileName) {
        System.out.println("insert");

        BPlusTree tree = DataFileUtil.loadTree(indexFileName);
        System.out.println(tree.maxChildNodes);

        int[][] data = DataFileUtil.loadIntCSV(dataFileName);
        for(int[] a: data) {
            for (int x: a)
                System.out.print(x + " ");

            tree.insert(a[0], a[1]);
            System.out.println("");
        }

        DataFileUtil.saveTree(indexFileName, tree);
    }

    /**
     * Delete Dataset from B+ Tree
     * @param indexFileName
     * @param dataFileName
     */
    static void delete(String indexFileName, String dataFileName) {
        System.out.println("delete");


    }

    /**
     * Search from B+ Tree
     * @param indexFileName
     * @param key
     */
    static void search(String indexFileName, int key) {
        System.out.println("search");
    }

    /**
     * Search by range in B+ Tree
     * @param indexFileName
     * @param startKey
     * @param endKey
     */
    static void rangeSearch(String indexFileName, int startKey, int endKey) {
        System.out.println("range search");
    }
}
