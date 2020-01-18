package GameManager;

import Common.GameStage;
import Player.IPlayer;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManagerImpl implements IGameManager {

    final static Logger logger = Logger.getLogger(GameManagerImpl.class);

    private int gameRoundsNumber;

    private Map<Integer,IPlayer> players;
    private List<GameStage> gameStages;

    private HashMap<Integer,Integer> numberQuestionsAnswered;

    private final String MSG_UPDATE = "Player %s scored %f.";
    private final String MSG_END    = "Player %s finished game.";


    public GameManagerImpl(Map<Integer,IPlayer> players, List<GameStage> gameStages){
        logger.info(String.format("initializing game manager with %d players and %d game stages",players.size(),gameStages.size()));
        this.players = players;
        this.gameStages = gameStages;
        gameRoundsNumber = gameStages.size();
        numberQuestionsAnswered = new HashMap<>();
    }

    public void startGame(){
        for(Integer playerId : players.keySet()){
            numberQuestionsAnswered.put(playerId,0);
            IPlayer player = players.get(playerId);
            logger.debug(String.format("starting game for player %d",playerId));
            player.init(this, gameStages);
            Thread p = new Thread(player);
            p.start();
        }
    }

    public void receiveAnswer(int currPlayerId, String answer, float time){
        logger.debug(String.format("received answer '%s' from player %d in %fs ",answer,currPlayerId,time));
        IPlayer currPlayer = players.get(currPlayerId);

        int questionsAnswered = numberQuestionsAnswered.get(currPlayerId);

        // grading answer
        GameStage currPlayerGameStage = gameStages.get(questionsAnswered);
        float answerScore = gradeGameStage(currPlayerGameStage, answer, time);
        // sending score
        currPlayer.grade(answerScore);

        // updating player status
        int currentQuestionsAnswered = questionsAnswered+1;
        numberQuestionsAnswered.put(currPlayerId,currentQuestionsAnswered);

        // sending updates
        String updateMsg = String.format(MSG_UPDATE,currPlayerId,answerScore);
        for(Integer playerId : players.keySet()){
            IPlayer player = players.get(playerId);
            logger.trace(String.format("updating player %d with update msg '%s'",player.getId(),updateMsg));
            player.update(updateMsg);
        }

        if (currentQuestionsAnswered == gameRoundsNumber){
            String endMsg = String.format(MSG_END,currPlayerId);
            logger.debug(String.format("player %d finished answering all his questions",currPlayer.getId()));
            currPlayer.end(endMsg);
        }
    }

    private float gradeGameStage(GameStage gameStage, String answer, float time) {
        float grade = gameStage.isAnswerCorrect(answer) ? 1f : 0f ;
        grade*=time;
        logger.debug(String.format("Graded '%f' answer '%s' for question %s",grade,answer,gameStage.getQuestion()));
        return grade;
    }

}
