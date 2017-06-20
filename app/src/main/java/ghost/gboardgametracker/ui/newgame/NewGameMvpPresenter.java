package ghost.gboardgametracker.ui.newgame;

import ghost.gboardgametracker.di.scope.PerActivity;
import ghost.gboardgametracker.ui.base.MvpPresenter;
import ghost.gboardgametracker.ui.newgame.model.NewGameUIModel;

/**
 * Created by hoangnh on 2/13/17.
 */

@PerActivity
public interface NewGameMvpPresenter<V extends NewGameMvpView> extends MvpPresenter<V> {

    void onClickStartGame(NewGameUIModel newGameUIModel);

    void loadGameInfo(Long gameId);

    void addNewPlayer();

    void removePlayer(int playerPos);
}
