package ghost.gboardgametracker.ui.play;

import ghost.gboardgametracker.data.DataManager;
import ghost.gboardgametracker.data.db.model.Player;
import ghost.gboardgametracker.ui.base.BasePresenter;
import ghost.gboardgametracker.ui.play.model.GamePlayUIModel;
import ghost.gboardgametracker.ui.play.model.PlayerUIModel;
import ghost.gboardgametracker.utils.AppConstants;
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
import java.util.Random;

/**
 * Created by hoangnh on 2/21/17.
 */

public class GamePlayPresenter<V extends GamePlayMvpView> extends BasePresenter<V> implements GamePlayMvpPresenter<V> {

    private Long mGameId;
    private GamePlayUIModel mGamePlayUIModel;
    private SORT_TYPE mSortType = SORT_TYPE.SCORE_DESCENDING;
    private Random mRandom = new Random();
    private boolean mNoGamePlaying;

    @Inject
    public GamePlayPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void loadGamePlayInfo(Long gameId) {
        if (gameId == AppConstants.NULL_ID) {
            loadLastPlayedGame();
        } else {
            loadGame(gameId);
        }
    }

    @Override
    public void onClickAddScore(int playerPos) {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().loadScoreLogsFromGame(mGameId, AppConstants.GAME_PLAY.NUMBER_OF_SCORE_SUGGESTION)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scoreSuggestList -> {
                    getMvpView().hideLoading();
                    getMvpView().showAddScoreDialog(playerPos, scoreSuggestList);
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().showAddScoreDialog(playerPos, null);
                }));
    }

    @Override
    public void onClickRemoveScore(int playerPos) {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().loadScoreLogsFromGame(mGameId, AppConstants.GAME_PLAY.NUMBER_OF_SCORE_SUGGESTION)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scoreSuggestList -> {
                    getMvpView().hideLoading();
                    getMvpView().showRemoveScoreDialog(playerPos, scoreSuggestList);
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().showRemoveScoreDialog(playerPos, null);
                }));
    }

    @Override
    public void onClickTransferScore(int playerPos) {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().loadScoreLogsFromGame(mGameId, AppConstants.GAME_PLAY.NUMBER_OF_SCORE_SUGGESTION)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scoreSuggestList -> {
                    getMvpView().hideLoading();
                    getMvpView().showTransferScoreDialog(playerPos, scoreSuggestList);
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().showTransferScoreDialog(playerPos, null);
                }));
    }

    @Override
    public void addScoreToPlayer(int playerPos, Long scoreOffset) {
        PlayerUIModel playerUIModel= mGamePlayUIModel.gamePlayerUIModelList.get(playerPos);

        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().addGamePlayerScore(mGameId, playerUIModel.id, scoreOffset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    playerUIModel.score += scoreOffset;
                    getMvpView().onAddedScore(playerPos, scoreOffset);
                    sortPlayers();
                    getMvpView().hideLoading();
                    getMvpView().displayGameInfo(mGamePlayUIModel);
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    @Override
    public void removeScoreToPlayer(int playerPos, Long scoreOffset) {
        PlayerUIModel playerUIModel= mGamePlayUIModel.gamePlayerUIModelList.get(playerPos);

        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().removeGamePlayerScore(mGameId, playerUIModel.id, scoreOffset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    playerUIModel.score -= scoreOffset;
                    getMvpView().onRemovedScore(playerPos, scoreOffset);
                    sortPlayers();
                    getMvpView().hideLoading();
                    getMvpView().displayGameInfo(mGamePlayUIModel);
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    @Override
    public void transferScore(int fromPlayerPos, int toPlayerPos, Long scoreOffset) {
        PlayerUIModel fromPlayerUIModel= mGamePlayUIModel.gamePlayerUIModelList.get(fromPlayerPos);
        PlayerUIModel toPlayerUIModel= mGamePlayUIModel.gamePlayerUIModelList.get(toPlayerPos);

        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().transferGamePlayerScore(mGameId, fromPlayerUIModel.id,
                toPlayerUIModel.id, scoreOffset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    fromPlayerUIModel.score -= scoreOffset;
                    toPlayerUIModel.score += scoreOffset;
                    getMvpView().onTransferedScore(fromPlayerPos, toPlayerPos, scoreOffset);
                    sortPlayers();
                    getMvpView().hideLoading();
                    getMvpView().displayGameInfo(mGamePlayUIModel);
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    @Override
    public void sortPlayersByScoreDescending() {
        mSortType = SORT_TYPE.SCORE_DESCENDING;
        sortPlayers();
        getMvpView().displayGameInfo(mGamePlayUIModel);
    }

    @Override
    public void sortPlayersByScoreAscending() {
        mSortType = SORT_TYPE.SCORE_ASCENDING;
        sortPlayers();
        getMvpView().displayGameInfo(mGamePlayUIModel);
    }

    @Override
    public void onClickGameSettings() {
        if (!mNoGamePlaying) {
            getMvpView().navigateToGameSetting(mGameId);
        }
    }

    @Override
    public void onClickStartPlayerTurnTimeBtn() {
        if (mGamePlayUIModel.game.getPlayerTurnTime() != null && mGamePlayUIModel.game.getPlayerTurnTimeEnabled()) {
            getMvpView().navigateToPlayTime(mGamePlayUIModel.game.getPlayerTurnTime());
        } else {
            getMvpView().navigateToGameSetting(mGameId);
        }
    }

    @Override
    public void onClickGameLogMenu() {
        if (!mNoGamePlaying) {
            getMvpView().navigateToGameLog(mGameId, mGamePlayUIModel.game.getGameTitle());
        }
    }

    @Override
    public void onClickPickRandomPlayer() {
        int playerPos = mRandom.nextInt(mGamePlayUIModel.gamePlayerUIModelList.size());
        getMvpView().showRandomPlayer(playerPos);
    }

    @Override
    public void onClickFinishGameBtn() {
        getMvpView().showLoading();
        long winnerId = mGamePlayUIModel.gamePlayerUIModelList.get(0).id; // first player is winner
        getCompositeDisposable().add(getDataManager().finishGame(mGameId, winnerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    getMvpView().hideLoading();
                    getMvpView().navigateToGameFinish(mGameId, mGamePlayUIModel.game.getGameTitle());
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));

    }

    @Override
    public void onClickRollDiceBtn() {
        if (mGamePlayUIModel.game.getNumberOfRollingDices() == null) {
            mGamePlayUIModel.game.setNumberOfRollingDices(1);
        }
        getMvpView().showRollDice(mGamePlayUIModel.game.getNumberOfRollingDices());
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

    private Observable<GamePlayUIModel> getGamePlayUIModel(final Long gameId, final List<PlayerUIModel> playerUIModelList) {
        return getDataManager().loadGame(gameId)
                .flatMap(game -> getDataManager().loadGame(game.getId()))
                .map(game -> {
                    GamePlayUIModel gamePlayUIModel = new GamePlayUIModel();
                    gamePlayUIModel.game = game;
                    gamePlayUIModel.gamePlayerUIModelList = playerUIModelList;
                    return gamePlayUIModel;
                });
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

    private void loadLastPlayedGame() {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().loadLastPlayedGame()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lastPlayedGame -> {
                    getMvpView().hideLoading();
                    loadGame(lastPlayedGame.getId());
                }, throwable -> {
                    getMvpView().hideLoading();
                    mNoGamePlaying = true;
                    getMvpView().displayNoGamePlayingNow();
                }));
    }

    private void loadGame(Long gameId) {
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
                    getMvpView().displayGameInfo(mGamePlayUIModel);
                }, throwable -> {
                    getMvpView().hideLoading();
                    getMvpView().onReceiveThrowable(throwable);
                }));
    }

    private enum SORT_TYPE {
        SCORE_DESCENDING, SCORE_ASCENDING;
    }
}
