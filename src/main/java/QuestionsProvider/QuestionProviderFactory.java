package QuestionsProvider;

import org.apache.log4j.Logger;

import Common.GameType.GameTypeEnum;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QuestionProviderFactory {

    private final static Logger logger = Logger.getLogger(QuestionProviderFactory.class);


    private static IQuestionProvider getQuestionProvider(GameTypeEnum type) throws IOException {

        IQuestionProvider questionProvider = null;

        switch (type){
            case TEST:
                questionProvider = new LocalQuestionsProvider();
                break;
            case GEO:
                questionProvider = createGeoRemote();
                break;
        }
        return questionProvider;

    }

    private static IQuestionProvider createGeoRemote() {
        return new RemoteQuestionsProvider();
    }


    // todo - add UT
    public static Map<GameTypeEnum, IQuestionProvider> getAll() {
        Map<GameTypeEnum,IQuestionProvider> questionProviders = new HashMap<>();
        for(GameTypeEnum type: GameTypeEnum.values()){
            IQuestionProvider questionProvider;
            try {
                questionProvider = getQuestionProvider(type);
            } catch (IOException e) {
                logger.error(String.format("Failed initializing question provider of type %s",type));
                e.printStackTrace();
                continue;
            }
            questionProviders.put(type, questionProvider);
        }
        return questionProviders;
    }

}
