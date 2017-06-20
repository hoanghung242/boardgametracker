package ghost.gboardgametracker.ui.gamedetail;

import ghost.gboardgametracker.ui.base.MvpView;
import ghost.gboardgametracker.ui.gamedetail.model.GameDetailUIModel;

/**
 * Created by hoangnh on 2/21/17.
 */

public interface GameDetailMvpView extends MvpView {

    void displayGameInfo(GameDetailUIModel gameDetailUIModel);

}
