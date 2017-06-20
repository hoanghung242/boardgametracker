package ghost.gboardgametracker.ui.gamedetail;

import ghost.gboardgametracker.di.scope.PerActivity;
import ghost.gboardgametracker.ui.base.MvpPresenter;

/**
 * Created by hoangnh on 2/21/17.
 */

@PerActivity
public interface GameDetailMvpPresenter<V extends GameDetailMvpView> extends MvpPresenter<V> {

    void loadGameInfo(Long gameId);
}
