package QuestionsProvider;

import Common.GameStage;

import Common.Converter;
import Util.RestUtil;

import java.util.ArrayList;
import java.util.List;

public class RemoteQuestionsProvider implements IQuestionProvider{

    private String url;

    RemoteQuestionsProvider(String url){
        this.url = url;
    }

    public List<GameStage> getQuestions(int num){
        String sb = RestUtil.get(url);
        if(!"".equals(sb)){
            // TODO - might throw exception if not "" but not in format
            // Change to json instead of string
            return Converter.toGameStageList(sb);
        }
        return new ArrayList<>();
    }
}
