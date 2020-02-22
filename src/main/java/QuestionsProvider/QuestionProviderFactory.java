package QuestionsProvider;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class QuestionProviderFactory {

    public enum QuestionProviderType{
        LOCAL_TEST,
        GEO_REMOTE,

    }

    public static IQuestionProvider getQuestionProvider(QuestionProviderType type){

        IQuestionProvider questionProvider = null;

        switch (type){
            case LOCAL_TEST:
                questionProvider = new LocalQuestionsProvider();
                break;
            case GEO_REMOTE:
                questionProvider = createGeoRemote();
                break;
        }
        return questionProvider;

    }

    private static IQuestionProvider createGeoRemote(){
        IQuestionProvider questionsProvider = null;
        try {
            URL url = new URL("http://localhost:5000/data/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            questionsProvider = new RemoteQuestionsProvider(conn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questionsProvider;
    }

}
