import java.io.*;
import java.util.ArrayList;

/**
 * Data Util for B+ tree CLI
 *
 * @author Prev (0soo.2@prev.kr)
 */

public class DataFileUtil {

    /**
     * Load CSV from filename which only contains integers
     * @param fileName
     * @return 2d int array
     */
    static int[][] loadIntCSV(String fileName) {
        ArrayList<String> lines = new ArrayList<String>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while(true) {
                String line = br.readLine();
                if (line == null) break;

                lines.add(line);
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        int[][] ret = new int[lines.size()][];

        for (int i = 0; i < lines.size(); i++) {
            String[] tokens = lines.get(i).split(",");
            ret[i] = new int[tokens.length];

            for (int j = 0; j < tokens.length; j++) {
                ret[i][j] = Integer.parseInt(tokens[j]);
            }
        }

        return ret;
    }

    /**
     * Get fileName and BPlusTree instance and save as file (serialization)
     * @param fileName
     * @param tree
     */
    static void saveTree(String fileName, BPlusTree tree) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tree);
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load saved tree in file and return BPlusTree instance (de-serialization)
     * @param fileName
     * @return BPlusTree instance
     */
    static BPlusTree loadTree(String fileName) {
        BPlusTree result = null;

        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);

            result = (BPlusTree) ois.readObject();
            ois.close();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }
}
