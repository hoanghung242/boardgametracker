package ghost.gboardgametracker.ui.play;

import ghost.gboardgametracker.di.scope.PerActivity;
import ghost.gboardgametracker.ui.base.MvpPresenter;

/**
 * Created by hoangnh on 2/21/17.
 */

@PerActivity
public interface GamePlayMvpPresenter<V extends GamePlayMvpView> extends MvpPresenter<V> {

    void loadGamePlayInfo(Long gameId);

    void onClickAddScore(int playerPos);

    void onClickRemoveScore(int playerPos);

    void onClickTransferScore(int playerPos);

    void addScoreToPlayer(int playerPos, Long scoreOffset);

    void removeScoreToPlayer(int playerPos, Long scoreOffset);

    void transferScore(int fromPlayerPos, int toPlayerPos, Long scoreOffset);

    void sortPlayersByScoreDescending();

    void sortPlayersByScoreAscending();

    void onClickGameSettings();

    void onClickStartPlayerTurnTimeBtn();

    void onClickGameLogMenu();

    void onClickPickRandomPlayer();

    void onClickFinishGameBtn();

    void onClickRollDiceBtn();
}
