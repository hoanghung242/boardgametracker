package ghost.gboardgametracker.ui.play;

import ghost.gboardgametracker.ui.base.MvpView;
import ghost.gboardgametracker.ui.play.model.GamePlayUIModel;

import java.util.List;

/**
 * Created by hoangnh on 2/21/17.
 */

public interface GamePlayMvpView extends MvpView {

    void displayGameInfo(GamePlayUIModel gamePlayerUIModel);

    void displayNoGamePlayingNow();

    void showAddScoreDialog(int playerPos, List<Long> scoreSuggestList);

    void showRemoveScoreDialog(int playerPos, List<Long> scoreSuggestList);

    void showTransferScoreDialog(int playerPos, List<Long> scoreSuggestList);

    void showRandomPlayer(int playerPos);

    void showRollDice(int numberOfDice);

    void onAddedScore(int playerPos, long scoreOffset);

    void onRemovedScore(int playerPos, long scoreOffset);

    void onTransferedScore(int fromPlayerPos, int toPlayerPos, long scoreOffset);

    void navigateToGameSetting(Long gameId);

    void navigateToPlayTime(Long duration);

    void navigateToGameLog(Long gameId, String gameTitle);

    void navigateToGameFinish(Long gameId, String gameTitle);
}
