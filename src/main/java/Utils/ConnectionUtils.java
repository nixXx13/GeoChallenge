package Utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

public class ConnectionUtils {

    public static String readObjectInputStream(ObjectInputStream is){
        String s = null;
        try {
            s = (String) is.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static void sendObjectOutputStream(ObjectOutputStream os, String s) throws IOException {
        os.writeObject(s);

        PrintStream ps = new PrintStream(os);
        if (ps.checkError()){
            throw new IOException("Error sending client with objectStream " + os.toString());
        }
    }
}
