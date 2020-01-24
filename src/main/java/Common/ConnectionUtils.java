package Common;

import java.io.*;

public class ConnectionUtils {

    public static String readBufferReader(BufferedReader br) throws IOException {
        return br.readLine();
    }

    public static void sendObjectOutputStream(ObjectOutputStream os, String json) throws IOException {
        os.writeObject(json);

        PrintStream ps = new PrintStream(os);
        if (ps.checkError()){
            throw new IOException("Error sending client with objectStream " + os.toString());
        }
    }

}
