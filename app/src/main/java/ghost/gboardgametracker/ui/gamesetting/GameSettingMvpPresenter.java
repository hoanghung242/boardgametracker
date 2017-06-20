package ghost.gboardgametracker.ui.gamesetting;

import ghost.gboardgametracker.di.scope.PerActivity;
import ghost.gboardgametracker.ui.base.MvpPresenter;

/**
 * Created by hoangnh on 3/6/17.
 */

@PerActivity
public interface GameSettingMvpPresenter<V extends GameSettingMvpView> extends MvpPresenter<V> {

    void loadGameSettingInfo(Long gameHistoryId);

    void setTotalPlayTime(Long totalPlayTime);

    void enableTotalPlayTime(Boolean isEnabled);

    void onCancelSetTotalTime();

    void setPlayerTurnTime(Long totalTime);

    void enablePlayerTurnTime(Boolean isEnabled);

    void onCancelSetPlayerTurnTime();

    void enableStartingScore(Boolean isEnabled);

    void setStartingScore(Long startingScore);

    void onCancelSetStartingScore();

    void toggleWinningScoreCondition();

    void enableRollingDice(Boolean isEnabled);

    void setNumberOfRollingDice(Integer numberOfRollingDice);
}
