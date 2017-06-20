package ghost.gboardgametracker.ui.gamelog;

import ghost.gboardgametracker.di.scope.PerActivity;
import ghost.gboardgametracker.ui.base.MvpPresenter;

/**
 * Created by hoangnh on 3/9/17.
 */

@PerActivity
public interface GameLogMvpPresenter<V extends GameLogMvpView> extends MvpPresenter<V> {

    void onCreated(Long gameId);

}
