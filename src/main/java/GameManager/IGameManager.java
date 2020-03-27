package GameManager;

import Common.GameStage;

public interface IGameManager {

        void startGame();

        void receiveAnswer(String playerName, GameStage gameStage, String answer, float time);

        void receiveDisconnect(String playerName);
}



