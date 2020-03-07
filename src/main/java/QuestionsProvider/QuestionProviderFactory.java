package QuestionsProvider;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class QuestionProviderFactory {

    public enum QuestionProviderType{
        LOCAL_TEST,
        GEO_REMOTE,

    }

    public static IQuestionProvider getQuestionProvider(QuestionProviderType type) throws IOException {

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

    private static IQuestionProvider createGeoRemote() throws IOException {
        URL url = new URL("http://localhost:5000/data/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        return new RemoteQuestionsProvider(conn);
    }

}
