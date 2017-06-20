package ghost.gboardgametracker.data;

import android.content.Context;
import ghost.gboardgametracker.data.db.DbHelper;
import ghost.gboardgametracker.data.db.model.Game;
import ghost.gboardgametracker.data.db.model.GameFinishInfo;
import ghost.gboardgametracker.data.db.model.GamePlayerDetail;
import ghost.gboardgametracker.data.db.model.Player;
import ghost.gboardgametracker.data.db.model.PlayerActionLog;
import ghost.gboardgametracker.data.prefs.PreferencesHelper;
import ghost.gboardgametracker.di.scope.ApplicationContext;
import io.reactivex.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by hoangnh on 2/10/17.
 */

@Singleton
public class AppDataManager implements DataManager{

    private final Context mContext;
    private final DbHelper mDbHelper;
    private final PreferencesHelper mPreferencesHelper;

    @Inject
    public AppDataManager(@ApplicationContext Context context, DbHelper dbHelper, PreferencesHelper preferencesHelper) {
        mContext = context;
        mDbHelper = dbHelper;
        mPreferencesHelper = preferencesHelper;
    }

    @Override
    public Long loadPlayingGameId() {
        return mPreferencesHelper.loadPlayingGameId();
    }

    @Override
    public void setPlayingGameId(Long inGameId) {
        mPreferencesHelper.setPlayingGameId(inGameId);
    }

    @Override
    public Observable<Long> savePlayer(Player player) {
        return mDbHelper.savePlayer(player);
    }

    @Override
    public Observable<List<Long>> savePlayerList(List<Player> playerList) {
        return mDbHelper.savePlayerList(playerList);
    }

    @Override
    public Observable<List<Player>> loadAllPlayers() {
        return mDbHelper.loadAllPlayers();
    }

    @Override
    public Observable<Boolean> updateGame(Game game) {
        return mDbHelper.updateGame(game);
    }

    @Override
    public Observable<List<Game>> loadAllGames() {
        return mDbHelper.loadAllGames();
    }

    @Override
    public Observable<List<Game>> loadAllGamesSortedByCreationTimeDesc() {
        return mDbHelper.loadAllGamesSortedByCreationTimeDesc();
    }

    @Override
    public Observable<List<Game>> loadAllGames(int offset, int limit) {
        return mDbHelper.loadAllGames(offset, limit);
    }

    @Override
    public Observable<Game> loadGame(Long gameId) {
        return mDbHelper.loadGame(gameId);
    }

    @Override
    public Observable<Game> loadLastPlayedGame() {
        return mDbHelper.loadLastPlayedGame();
    }

    @Override
    public Observable<Long> saveGameFinishInfo(GameFinishInfo gameFinishInfo) {
        return mDbHelper.saveGameFinishInfo(gameFinishInfo);
    }

    @Override
    public Observable<Long> finishGame(Long gameId, Long winnerPlayerId) {
        return mDbHelper.finishGame(gameId, winnerPlayerId);
    }

    @Override
    public Observable<GameFinishInfo> loadGameFinishInfo(Long gameFinishInfoId) {
        return mDbHelper.loadGameFinishInfo(gameFinishInfoId);
    }

    @Override
    public Observable<GameFinishInfo> loadGameFinishInfoFromGame(Long gameId) {
        return mDbHelper.loadGameFinishInfoFromGame(gameId);
    }

    @Override
    public Observable<List<Player>> loadPlayerListFromGame(Long gameId) {
        return mDbHelper.loadPlayerListFromGame(gameId);
    }

    @Override
    public Observable<Player> loadPlayer(Long playerId) {
        return mDbHelper.loadPlayer(playerId);
    }

    @Override
    public Observable<Long> saveGame(Game game, List<Player> playerList) {
        return mDbHelper.saveGame(game, playerList);
    }

    @Override
    public Observable<Long> saveGamePlayerDetail(GamePlayerDetail gamePlayerDetail) {
        return mDbHelper.saveGamePlayerDetail(gamePlayerDetail);
    }

    @Override
    public Observable<Boolean> updateGamePlayerDetail(GamePlayerDetail gamePlayerDetail) {
        return mDbHelper.updateGamePlayerDetail(gamePlayerDetail);
    }

    @Override
    public Observable<Boolean> addGamePlayerScore(Long gameId, Long playerId, Long offset) {
        return mDbHelper.addGamePlayerScore(gameId, playerId, offset);
    }

    @Override
    public Observable<Boolean> removeGamePlayerScore(Long gameId, Long playerId, Long offset) {
        return mDbHelper.removeGamePlayerScore(gameId, playerId, offset);
    }

    @Override
    public Observable<Boolean> transferGamePlayerScore(Long gameId, Long fromPlayerId, Long toPlayerId, Long offset) {
        return mDbHelper.transferGamePlayerScore(gameId, fromPlayerId, toPlayerId, offset);
    }

    @Override
    public Observable<List<Long>> saveGamePlayerDetailList(List<GamePlayerDetail> gamePlayerDetailList) {
        return mDbHelper.saveGamePlayerDetailList(gamePlayerDetailList);
    }

    @Override
    public Observable<GamePlayerDetail> loadGamePlayerDetail(Long id) {
        return mDbHelper.loadGamePlayerDetail(id);
    }

    @Override
    public Observable<GamePlayerDetail> loadGamePlayerDetail(Long gameId, Long playerId) {
        return mDbHelper.loadGamePlayerDetail(gameId, playerId);
    }

    @Override
    public Observable<Long> savePlayerActionLog(PlayerActionLog playerActionLog) {
        return mDbHelper.savePlayerActionLog(playerActionLog);
    }

    @Override
    public Observable<List<PlayerActionLog>> loadPlayerActionLogsFromGame(Long gameId) {
        return mDbHelper.loadPlayerActionLogsFromGame(gameId);
    }

    @Override
    public Observable<List<PlayerActionLog>> loadPlayerActionLogsFromGame(Long gameId, int limit) {
        return mDbHelper.loadPlayerActionLogsFromGame(gameId, limit);
    }

    @Override
    public Observable<List<Long>> loadScoreLogsFromGame(Long gameId, int limit) {
        return mDbHelper.loadScoreLogsFromGame(gameId, limit);
    }
}
