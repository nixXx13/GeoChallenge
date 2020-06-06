package QuestionsProvider;

import Util.Constants;
import org.apache.log4j.Logger;

import Common.GameType.GameTypeEnum;
import java.util.HashMap;
import java.util.Map;

public class QuestionProviderFactory {

    private final static Logger logger = Logger.getLogger(QuestionProviderFactory.class);

    private static IQuestionProvider getQuestionProvider(GameTypeEnum type){

        String dockerIp = Constants.QUESTION_PROVIDER_DOCKER_IP;
        int dockerPort = Constants.QUESTION_PROVIDER_DOCKER_PORT;

        IQuestionProvider questionProvider = null;

        switch (type){
            case TEST:
                questionProvider = new LocalQuestionsProvider();
                break;
            case MATH:
                questionProvider = new RemoteQuestionsProvider(String.format("http://%s:%d/data/math/", dockerIp ,dockerPort));
                break;
            case GEO:
                questionProvider = new RemoteQuestionsProvider(String.format("http://%s:%d/data/geo/", dockerIp ,dockerPort));
                break;
        }
        return questionProvider;

    }


    public static Map<GameTypeEnum, IQuestionProvider> getAll() {
        Map<GameTypeEnum,IQuestionProvider> questionProviders = new HashMap<>();
        for(GameTypeEnum type: GameTypeEnum.values()){
            IQuestionProvider questionProvider;
            questionProvider = getQuestionProvider(type);
            questionProviders.put(type, questionProvider);
        }
        return questionProviders;
    }

}
