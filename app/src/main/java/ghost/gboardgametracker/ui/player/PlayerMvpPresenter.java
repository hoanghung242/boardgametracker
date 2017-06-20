package ghost.gboardgametracker.ui.player;

import ghost.gboardgametracker.di.scope.PerActivity;
import ghost.gboardgametracker.ui.base.MvpPresenter;

/**
 * Created by hoangnh on 3/8/17.
 */

@PerActivity
public interface PlayerMvpPresenter<V extends PlayerMvpView> extends MvpPresenter<V> {

    void fetchPlayerList();

}
