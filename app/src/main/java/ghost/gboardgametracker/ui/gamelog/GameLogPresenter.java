package ghost.gboardgametracker.ui.gamelog;

import ghost.gboardgametracker.data.DataManager;
import ghost.gboardgametracker.data.db.model.Player;
import ghost.gboardgametracker.data.db.model.PlayerActionLog;
import ghost.gboardgametracker.ui.base.BasePresenter;
import ghost.gboardgametracker.ui.gamelog.model.GameLogUIModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by hoangnh on 3/9/17.
 */

public class GameLogPresenter<V extends GameLogMvpView> extends BasePresenter<V> implements GameLogMvpPresenter<V> {

    private Long mGameId;

    @Inject
    public GameLogPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void onCreated(Long gameId) {
        mGameId = gameId;

        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().loadPlayerActionLogsFromGame(gameId)
                .subscribeOn(Schedulers.io())
                .flatMapIterable(new Function<List<PlayerActionLog>, Iterable<PlayerActionLog>>() {
                    @Override
                    public Iterable<PlayerActionLog> apply(List<PlayerActionLog> playerActionLogs) throws Exception {
                        return playerActionLogs;
                    }
                })
                .flatMap(actionLog -> getGameLogUIModel(actionLog))
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gameLogUIModels -> {
                    getMvpView().hideLoading();
                    getMvpView().displayPlayerActionLogs(gameLogUIModels);
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));

    }

    private Observable<GameLogUIModel> getGameLogUIModel(PlayerActionLog playerActionLog) {
        Observable<Player> playerObservable = getDataManager().loadPlayer(playerActionLog.getPlayerId());
        Observable<Player> otherPlayerObservable = getDataManager().loadPlayer(playerActionLog.getToPlayerId());

        if (playerActionLog.getToPlayerId() != null) {
            return Observable.zip(playerObservable, otherPlayerObservable, (player, otherPlayer) ->
                    new GameLogUIModel(playerActionLog.getId(), playerActionLog.getCreationTime(), playerActionLog
                    .getActionType(), playerActionLog.getScoreOffset(), player.getName(), otherPlayer.getName()));
        } else {
            return playerObservable.map(player -> new GameLogUIModel(playerActionLog.getId(), playerActionLog
                    .getCreationTime(), playerActionLog.getActionType(), playerActionLog.getScoreOffset(), player.getName(), null));
        }
    }
}
