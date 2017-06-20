package ghost.gboardgametracker.ui.base;

/**
 * Created by hoangnh on 2/10/17.
 */

public interface MvpPresenter<V extends MvpView> {

    void onAttach(V mvpView);

    void onDetach();

    void onViewLoaded();
}
