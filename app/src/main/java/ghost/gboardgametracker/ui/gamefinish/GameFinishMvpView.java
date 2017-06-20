package ghost.gboardgametracker.ui.gamefinish;

import ghost.gboardgametracker.ui.base.MvpView;
import ghost.gboardgametracker.ui.gamefinish.model.GamePlayUIModel;

/**
 * Created by hoangnh on 3/27/17.
 */

public interface GameFinishMvpView extends MvpView {

    void displayGameInfo(GamePlayUIModel gamePlayUIModel);

    void navigateToNewGame(Long gameId);

    void navigateToGameLog(Long gameId, String gameTitle);
}
