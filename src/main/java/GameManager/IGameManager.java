package GameManager;

public interface IGameManager {

        void startGame();

        void receiveAnswer(int currPlayerId, String answer, float time);

        public void receiveDisconnect(int playerId);
}



