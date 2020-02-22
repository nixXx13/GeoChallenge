package QuestionsProvider;

import Common.GameStage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import Common.Converter;
import java.util.List;

public class RemoteQuestionsProvider implements IQuestionProvider{

    private HttpURLConnection conn;

    public RemoteQuestionsProvider(HttpURLConnection conn){
        this.conn = conn;
    }

    public List<GameStage> getQuestions(int num){
        StringBuilder sb = new StringBuilder("");
        try {
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");

            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            conn.disconnect();

        }catch (IOException e) {
            e.printStackTrace();
        }

        return Converter.toGameStageList(sb.toString());
    }
}
