package ghost.gboardgametracker.ui.gamefinish;

import ghost.gboardgametracker.data.DataManager;
import ghost.gboardgametracker.data.db.model.Game;
import ghost.gboardgametracker.data.db.model.GameFinishInfo;
import ghost.gboardgametracker.data.db.model.Player;
import ghost.gboardgametracker.ui.base.BasePresenter;
import ghost.gboardgametracker.ui.gamefinish.model.GamePlayUIModel;
import ghost.gboardgametracker.ui.gamefinish.model.PlayerUIModel;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by hoangnh on 3/27/17.
 */

public class GameFinishPresenter<V extends GameFinishMvpView> extends BasePresenter<V> implements
        GameFinishMvpPresenter<V> {

    private Long mGameId;
    private GamePlayUIModel mGamePlayUIModel;
    private SORT_TYPE mSortType;

    @Inject
    public GameFinishPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void onCreated(Long gameId) {
        mGameId = gameId;
        getMvpView().showLoading();

        getCompositeDisposable().add(getDataManager().loadPlayerListFromGame(gameId)
                .subscribeOn(Schedulers.io())
                .flatMapIterable(new Function<List<Player>, Iterable<Player>>() {
                    @Override
                    public Iterable<Player> apply(List<Player> playerList) throws Exception {
                        return playerList;
                    }
                })
                .flatMap(player -> getPlayerUIModel(gameId, player))
                .toList()
                .flatMapObservable(new Function<List<PlayerUIModel>, ObservableSource<GamePlayUIModel>>() {
                    @Override
                    public ObservableSource<GamePlayUIModel> apply(List<PlayerUIModel> playerUIModelList) throws Exception {
                        return getGamePlayUIModel(gameId, playerUIModelList);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gamePlayUIModel -> {
                    getMvpView().hideLoading();
                    mGamePlayUIModel = gamePlayUIModel;
                    mSortType = gamePlayUIModel.game.getWinningScoreConditionType() == null || gamePlayUIModel.game
                            .getWinningScoreConditionType() == 0 ? SORT_TYPE.SCORE_DESCENDING : SORT_TYPE.SCORE_ASCENDING;
                    sortPlayers();
                    rankPlayers();
                    getMvpView().displayGameInfo(mGamePlayUIModel);
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    @Override
    public void onClickPlayBtn() {
        getMvpView().navigateToNewGame(mGameId);
    }

    @Override
    public void onClickGameLogMenu() {
        getMvpView().navigateToGameLog(mGameId, mGamePlayUIModel.game.getGameTitle());
    }

    private Observable<PlayerUIModel> getPlayerUIModel(Long gameId, final Player player) {
        return getDataManager().loadGamePlayerDetail(gameId, player.getId())
                .map(gamePlayerDetail -> {
                    PlayerUIModel playerUIModel = new PlayerUIModel();
                    playerUIModel.id = player.getId();
                    playerUIModel.score = gamePlayerDetail.getScore() == null ? 0 : gamePlayerDetail.getScore();
                    playerUIModel.name = player.getName();
                    return playerUIModel;
                });
    }

    private Observable<GamePlayUIModel> getGamePlayUIModel(final Long gameId, final List<PlayerUIModel> playerUIModelList) {

        Observable<Game> loadGame = getDataManager().loadGame(gameId);
        Observable<GameFinishInfo> loadGameFinishInfo = getDataManager().loadGameFinishInfoFromGame(gameId);

        return Observable.zip(loadGame, loadGameFinishInfo, (game, gameFinishInfo) -> {
            GamePlayUIModel gamePlayUIModel = new GamePlayUIModel();
            gamePlayUIModel.game = game;
            gamePlayUIModel.gameFinishInfo = gameFinishInfo;
            gamePlayUIModel.gamePlayerUIModelList = playerUIModelList;
            return gamePlayUIModel;
        });
    }

    private void sortPlayers() {
        Comparator<PlayerUIModel> comparator = null;
        switch (mSortType) {
            case SCORE_DESCENDING:
                comparator = (o1, o2) -> o2.score.compareTo(o1.score);
                break;
            case SCORE_ASCENDING:
                comparator = (o1, o2) -> o1.score.compareTo(o2.score);
                break;
        }

        Collections.sort(mGamePlayUIModel.gamePlayerUIModelList, comparator);
    }

    private void rankPlayers() {
        if (mGamePlayUIModel.gamePlayerUIModelList != null && mGamePlayUIModel.gamePlayerUIModelList.size() > 0) {
            int currentRank = 1;
            mGamePlayUIModel.gamePlayerUIModelList.get(0).rank = currentRank;
            for (int i = 1; i < mGamePlayUIModel.gamePlayerUIModelList.size(); ++i) {
                PlayerUIModel previousPlayer = mGamePlayUIModel.gamePlayerUIModelList.get(i - 1);
                PlayerUIModel playerUIModel = mGamePlayUIModel.gamePlayerUIModelList.get(i);
                if (playerUIModel.score.compareTo(previousPlayer.score) != 0) {
                    currentRank++;
                }
                playerUIModel.rank = currentRank;
            }
        }
    }

    private enum SORT_TYPE {
        SCORE_DESCENDING, SCORE_ASCENDING;
    }
}
