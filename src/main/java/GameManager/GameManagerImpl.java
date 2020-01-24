package GameManager;

import Common.GameStage;
import Player.IPlayer;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class GameManagerImpl implements IGameManager {
    // TODO - improve UT
    final static Logger logger = Logger.getLogger(GameManagerImpl.class);

    private int gameRoundsNumber;

    private Map<Integer,IPlayer> players;
    private List<GameStage> gameStages;

    private int activePlayers;

    private final String MSG_UPDATE = "Player %s scored %f.";
    private final String MSG_END    = "Player %s finished game.";


    public GameManagerImpl(Map<Integer,IPlayer> players, List<GameStage> gameStages){
        logger.info(String.format("initializing game manager with %d players and %d game stages",players.size(),gameStages.size()));
        this.players = players;
        this.gameStages = gameStages;
        gameRoundsNumber = gameStages.size();
        activePlayers=players.size();
    }

    public void startGame(){
        for(Integer playerId : players.keySet()){
            IPlayer player = players.get(playerId);

            logger.debug(String.format("player %d - game started",playerId));
            player.init(this, gameStages);
            Thread p = new Thread(player);
            p.start();
        }
    }

    public void receiveAnswer(int currPlayerId, String answer, float time){
        logger.debug(String.format("player %d - received answer '%s' in %fs ",currPlayerId,answer,time));
        IPlayer currPlayer = players.get(currPlayerId);

        int questionsAnswered = currPlayer.getQuestionsAnswered();
        if (questionsAnswered < gameStages.size()) {
            // grading answer and sending score
            GameStage currPlayerGameStage = gameStages.get(questionsAnswered);
            float answerScore = gradeGameStage(currPlayerGameStage, answer, time);
            logger.debug(String.format("player %d - scored '%f' in question #%d", currPlayer.getId(), answerScore,questionsAnswered));
            currPlayer.grade(answerScore);

            // updating player question counter
            currPlayer.setQuestionsAnswered(++questionsAnswered);

            // sending updates
            String updateMsg = String.format(MSG_UPDATE, currPlayerId, answerScore);
            updateAllPlayers(updateMsg);

            if (questionsAnswered == gameRoundsNumber) {
                String endMsg = String.format(MSG_END, currPlayerId);
                logger.debug(String.format("player %d - finished answering questions with score %f. Waiting for other players" +
                        " to finish.", currPlayer.getId(),currPlayer.getScore()));
                currPlayer.update(endMsg);

                if(getActivePlayersNumber()==1){ // last player
                    logger.debug(String.format("player %d - last active player. Notifying rest of the players game ended",currPlayerId));
                    String summary = "SUMMARY";
                    for (Integer playerId : players.keySet()) {
                        IPlayer player = players.get(playerId);
                        logger.trace(String.format("player %d - updating with end msg '%s'", player.getId(), updateMsg));
                        player.end(summary);
                    }
                }
                else {
                    decreasegetActivePlayersNumber();
                    logger.debug(String.format("player %d - decreased active players number to %d",currPlayerId,getActivePlayersNumber()));
                }
            }
        }
    }

    private float gradeGameStage(GameStage gameStage, String answer, float time) {
        float grade = gameStage.isAnswerCorrect(answer) ? 1f : 0f ;
        grade*=time;
        logger.trace(String.format("Graded '%f' answer '%s' for question %s",grade,answer,gameStage.getQuestion()));
        return grade;
    }

    private synchronized void decreasegetActivePlayersNumber(){
        activePlayers-=1;
    }

    private synchronized int getActivePlayersNumber(){
        return activePlayers;
    }

    private void updateAllPlayers(String updateMsg){
        for (Integer playerId : players.keySet()) {
            IPlayer player = players.get(playerId);
            logger.trace(String.format("updating player %d with update msg '%s'", player.getId(), updateMsg));
            player.update(updateMsg);
        }
    }

}
