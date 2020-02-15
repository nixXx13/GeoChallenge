package QuestionsFetcher;

import Common.GameData;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionFetcher implements IQuestionFetcher{

    private String url;
//    private HttpURLConnection con;
//
//    public QuestionFetcher(String url){
//        this.url = url + "/data/?qNum=";
//
//    }
//
//    public List<GameData> fetch(int questionNumber) throws IOException {
//
//        URL url = new URL(this.url + questionNumber);
//        this.con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("GET");
//
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(con.getInputStream()));
//        String inputLine;
//        StringBuffer content = new StringBuffer();
//        while ((inputLine = in.readLine()) != null) {
//            content.append(inputLine);
//        }
//        in.close();
//    }

    // http://127.0.0.1:5000/data/?qNum=2
}
