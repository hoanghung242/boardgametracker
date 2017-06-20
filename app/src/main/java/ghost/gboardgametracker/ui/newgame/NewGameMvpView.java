package ghost.gboardgametracker.ui.newgame;

import ghost.gboardgametracker.ui.base.MvpView;
import ghost.gboardgametracker.ui.newgame.model.NewGameUIModel;


/**
 * Created by hoangnh on 2/13/17.
 */

public interface NewGameMvpView extends MvpView {

    void displayGameInfo(NewGameUIModel newGameUIModel);

    void navigationToGamePlay(Long gameId);

    void removePlayer(NewGameUIModel newGameUIModel, int playerPos);

    void addNewPlayer(NewGameUIModel newGameUIModel, int playerPos);
}
