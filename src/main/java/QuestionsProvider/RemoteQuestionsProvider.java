package QuestionsProvider;

import Common.GameStage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import Common.Converter;

import java.net.URL;
import java.util.List;

public class RemoteQuestionsProvider implements IQuestionProvider{

    public List<GameStage> getQuestions(int num){
        StringBuilder sb = new StringBuilder("");
        try {
//            URL url = new URL("http://localhost:500/data/");
            URL url = new URL("http://172.18.0.2:500/data/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
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
