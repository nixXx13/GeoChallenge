package Common;

import java.io.*;

public class ConnectionUtils {

    public static String readBufferReader(BufferedReader br){
        String s = null;
        try {
            s = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static void sendObjectOutputStream(ObjectOutputStream os, String json) throws IOException {
        os.writeObject(json);

        PrintStream ps = new PrintStream(os);
        if (ps.checkError()){
            throw new IOException("Error sending client with objectStream " + os.toString());
        }
    }

}
