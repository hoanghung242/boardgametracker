package ghost.gboardgametracker.ui.gamesetting;

import ghost.gboardgametracker.ui.base.MvpView;

/**
 * Created by hoangnh on 3/6/17.
 */

public interface GameSettingMvpView extends MvpView{

    void displayTotalTimeSettings(Boolean enable, Long totalTimeInMilliSeconds);

    void showTotalTimePickerDialog(Long totalTime);

    void displayPlayerTurnTimeSettings(Boolean enable, Long totalTimeInMilliSeconds);

    void showPlayerTurnTimePickerDialog(Long totalTime);

    void showStartingScoreSetting(Boolean enable, Long startingScore);

    void showStartingScorePickerDialog(Long score);

    void showWinningScoreConditionSetting(Integer winningScoreConditionType);

    void showRollingDiceSetting(Boolean enable, Integer numberOfRollingDices);

    void showRollingDicePickerDialog(Integer numberOfRollingDices);
}
