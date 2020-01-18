package GameManager;

import Models.GameStage;
import Player.IPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class GameManagerImpl implements IGameManager {

    private int gameRoundsNumber;

    private Map<Integer,IPlayer> players;
    private List<GameStage> gameStages;

    private HashMap<Integer,Integer> numberQuestionsAnswered;

    private final String MSG_UPDATE = "Player %s scored %f.";
    private final String MSG_END    = "Player %s finished game.";


    public GameManagerImpl(Map<Integer,IPlayer> players, List<GameStage> gameStages){
//        LOGGER.fine("setting up GameManagerImpl");
        System.out.println(String.format("[INFO] - initializing game manager with %d players and %d game stages",players.size(),gameStages.size()));
        this.players = players;
        this.gameStages = gameStages;
        gameRoundsNumber = gameStages.size();
        numberQuestionsAnswered = new HashMap<Integer, Integer>();
    }

    public void startGame(){
        initGameData();
        for(Integer playerId : players.keySet()){
            IPlayer player = players.get(playerId);
            System.out.println(String.format("[DEBUG} - starting game for player %d",playerId));
            player.init(this, gameStages);
            Thread p = new Thread(player);
            p.start();
        }
    }

    public void receiveAnswer(int currPlayerId, String answer, float time){
        System.out.println(String.format("[DEBUG} - received answer from player %d - '%s'",currPlayerId,answer));
        IPlayer currPlayer = players.get(currPlayerId);

        int questionsAnswered = numberQuestionsAnswered.get(currPlayerId);

        // grading answer
        GameStage currPlayerGameStage = gameStages.get(questionsAnswered);
        float answerScore = gradeGameStage(currPlayerGameStage, answer, time);
        // sending score
        currPlayer.addScore(answerScore);

        // updating player status
        int currentQuestionsAnswered = questionsAnswered+1;
        numberQuestionsAnswered.put(currPlayerId,currentQuestionsAnswered);



        // sending updates
        String updateMsg = String.format(MSG_UPDATE,currPlayerId,answerScore);
        for(Integer playerId : players.keySet()){
            IPlayer player = players.get(playerId);
            System.out.println(String.format("[DEBUG} - updating player %d with update msg '%s'",player.getId(),updateMsg));
            player.update(IPlayer.UpdateType.STATUS,updateMsg);
        }

        if (currentQuestionsAnswered == gameRoundsNumber){
            String endMsg = String.format(MSG_END,currPlayerId);
            System.out.println(String.format("[DEBUG} - player %d finished answering all his questions",currPlayer.getId()));
            currPlayer.end(IPlayer.UpdateType.END,endMsg);
        }
    }

    private void initGameData(){
        for(Integer playerId : players.keySet()){
            numberQuestionsAnswered.put(playerId,0);
        }
    }

    private float gradeGameStage(GameStage gameStage, String answer, float time) {
        System.out.println(String.format("[DEBUG} - question '%s' - recevied answer '%s' in %f s",gameStage.getQuestion(),answer,time));
        float grade = gameStage.isAnswerCorrect(answer) ? 1f : 0f ;
        return grade*time;
    }

}
