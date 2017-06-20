package ghost.gboardgametracker.ui.player;

import ghost.gboardgametracker.data.DataManager;
import ghost.gboardgametracker.data.db.model.Player;
import ghost.gboardgametracker.ui.base.BasePresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by hoangnh on 2/14/17.
 */

public class PlayerPresenter<V extends PlayerMvpView> extends BasePresenter<V> implements PlayerMvpPresenter<V> {

    @Inject
    public PlayerPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void fetchPlayerList() {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().loadAllPlayers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(players -> {
                    getMvpView().hideLoading();
                    getMvpView().refreshPlayerList(players);
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    @Override
    public void onViewLoaded() {
        super.onViewLoaded();

        fetchPlayerList();
    }
}
