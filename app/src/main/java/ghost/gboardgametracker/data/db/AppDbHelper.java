package ghost.gboardgametracker.data.db;

import android.database.Cursor;
import ghost.gboardgametracker.data.db.model.*;
import ghost.gboardgametracker.data.utils.PlayerActionEnum;
import ghost.gboardgametracker.task.exception.MinimumNumberOfPlayerException;
import io.reactivex.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoangnh on 2/10/17.
 */

@Singleton
public class AppDbHelper implements DbHelper{

    private DaoSession mDaoSession;

    @Inject
    public AppDbHelper(DbOpenHelper dbOpenHelper) {
        mDaoSession = new DaoMaster(dbOpenHelper.getWritableDb()).newSession();
    }

    @Override
    public Observable<Long> savePlayer(final Player player) {
        return Observable.fromCallable(() -> {
            player.validate();
            return mDaoSession.getPlayerDao().insert(player);
        });
    }

    @Override
    public Observable<List<Long>> savePlayerList(final List<Player> playerList) {
        return Observable.fromCallable(() -> {
            List<Long> idList = new ArrayList<>();
            for (Player player : playerList) {
                player.validate();
                mDaoSession.getPlayerDao().insert(player);
                idList.add(mDaoSession.getPlayerDao().insertOrReplace(player));
            }
            return idList;
        });
    }

    @Override
    public Observable<List<Player>> loadAllPlayers() {
        return Observable.fromCallable(() -> mDaoSession.getPlayerDao().loadAll());
    }

    @Override
    public Observable<Boolean> updateGame(final Game game) {
        return Observable.fromCallable(() -> {
            game.validate();
            mDaoSession.getGameDao().save(game);
            setGameLastUpdateTime(game.getId());
            return true;
        });
    }

    @Override
    public Observable<List<Game>> loadAllGames() {
        return Observable.fromCallable(() -> {
            List<Game> allGames = mDaoSession.getGameDao().queryBuilder()
                .orderDesc(GameDao.Properties.LastUpdateTime)
                .list();
            return allGames;
            });
    }

    @Override
    public Observable<List<Game>> loadAllGamesSortedByCreationTimeDesc() {
        return Observable.fromCallable(() -> mDaoSession.getGameDao().queryBuilder()
                .orderDesc(GameDao.Properties.CreationTime)
                .list());
    }

    @Override
    public Observable<List<Game>> loadAllGames(int offset, int limit) {
        return Observable.fromCallable(() -> mDaoSession.getGameDao().queryBuilder()
                .orderDesc(GameDao.Properties.LastUpdateTime)
                .limit(limit)
                .offset(offset)
                .list());
    }

    @Override
    public Observable<Game> loadGame(final Long gameId) {
        return Observable.fromCallable(() -> mDaoSession.getGameDao().load(gameId));
    }

    @Override
    public Observable<Game> loadLastPlayedGame() {
        return Observable.fromCallable(() -> {
            List<Game> gameList = mDaoSession.getGameDao().queryBuilder()
                    .whereOr(GameDao.Properties.GameFinish.isNull(), GameDao.Properties.GameFinish.eq(false))
                    .orderDesc(GameDao.Properties.LastUpdateTime)
                    .limit(1)
                    .list();

            return gameList.get(0);
        });
    }

    @Override
    public Observable<Long> saveGameFinishInfo(GameFinishInfo gameFinishInfo) {
        return Observable.fromCallable(() -> mDaoSession.getGameFinishInfoDao().insert(gameFinishInfo));
    }

    @Override
    public Observable<Long> finishGame(Long gameId, Long winnerPlayerId) {
        return Observable.fromCallable(() -> {
            Game game = mDaoSession.getGameDao().load(gameId);
            GameFinishInfo gameFinishInfo = new GameFinishInfo();
            gameFinishInfo.setGameId(game.getId());
            gameFinishInfo.setCreationDate(System.currentTimeMillis());
            gameFinishInfo.setWinnerPlayerId(winnerPlayerId);

            long gameFinishId = mDaoSession.insert(gameFinishInfo);
            game.setGameFinish(true);
            game.setGameFinishInfoId(gameFinishId);
            mDaoSession.getGameDao().save(game);

            setGameLastUpdateTime(gameId);

            return gameFinishId;
        });
    }

    @Override
    public Observable<GameFinishInfo> loadGameFinishInfo(Long gameFinishInfoId) {
        return Observable.fromCallable(() -> mDaoSession.getGameFinishInfoDao().load(gameFinishInfoId));
    }

    @Override
    public Observable<GameFinishInfo> loadGameFinishInfoFromGame(Long gameId) {
        return Observable.fromCallable(() -> {
            Game game = mDaoSession.getGameDao().load(gameId);
            return mDaoSession.getGameFinishInfoDao().load(game.getGameFinishInfoId());
        });
    }

    @Override
    public Observable<List<Player>> loadPlayerListFromGame(final Long gameId) {
        return Observable.fromCallable(() -> {
            List<GamePlayerDetail> gamePlayerDetailList = mDaoSession.getGamePlayerDetailDao().queryBuilder()
                    .where(GamePlayerDetailDao.Properties.GameId.eq(gameId))
                    .list();

            List<Player> playerList = new ArrayList<>();
            if (gamePlayerDetailList != null) {
                for (GamePlayerDetail gamePlayerDetail : gamePlayerDetailList) {
                    Player player = mDaoSession.getPlayerDao().load(gamePlayerDetail.getPlayerId());
                    if (player != null) {
                        playerList.add(player);
                    }
                }
            }
            return playerList;
        });
    }

    @Override
    public Observable<Player> loadPlayer(Long playerId) {
        return Observable.fromCallable(() -> mDaoSession.getPlayerDao().load(playerId));
    }

    @Override
    public Observable<Long> saveGame(Game game, List<Player> playerList) {
        return Observable.fromCallable(() -> {
            game.validate();
            if (playerList == null || playerList.size() < 2) {
                throw new MinimumNumberOfPlayerException();
            }
            for (Player player : playerList) {
                player.validate();
            }

            // Initialize new game with default values
            game.setId(null);
            game.setCreationTime(System.currentTimeMillis());
            game.setLastUpdateTime(game.getCreationTime());

            game.setGameFinish(false);
            game.setGameFinishInfoId(null);

            game.setStartingScoreEnabled(false);
            game.setStartingScore(0L);

            game.setTotalPlayTimeEnabled(false);
            game.setTotalPlayTime(0L);

            game.setPlayerTurnTimeEnabled(true);
            game.setPlayerTurnTime(60000L);
            mDaoSession.clear();

            game.setRollingDiceEnabled(true);
            game.setNumberOfRollingDices(2);

            Long gameId = mDaoSession.getGameDao().insert(game);
            for (Player player : playerList) {
                if (player.getId() == null) {
                    // Load the player with same name instead of create new player
                    List<Player> sameNameList = mDaoSession.getPlayerDao().queryBuilder()
                            .where(PlayerDao.Properties.Name.eq(player.getName()))
                            .list();
                    if (sameNameList != null && sameNameList.size() > 0) {
                        player.setId(sameNameList.get(0).getId());
                    } else {
                        mDaoSession.getPlayerDao().insert(player);
                    }
                }
                GamePlayerDetail gamePlayerDetail = new GamePlayerDetail();
                gamePlayerDetail.setGameId(gameId);
                gamePlayerDetail.setPlayerId(player.getId());
                gamePlayerDetail.validate();
                mDaoSession.getGamePlayerDetailDao().insert(gamePlayerDetail);
            }
            return gameId;
        });
    }

    @Override
    public Observable<Long> saveGamePlayerDetail(final GamePlayerDetail gamePlayerDetail) {
        return Observable.fromCallable(() -> {
            gamePlayerDetail.validate();
            return mDaoSession.getGamePlayerDetailDao().insert(gamePlayerDetail);
        });
    }

    @Override
    public Observable<Boolean> updateGamePlayerDetail(final GamePlayerDetail gamePlayerDetail) {
        return Observable.fromCallable(() -> {
            gamePlayerDetail.validate();
            mDaoSession.getGamePlayerDetailDao().save(gamePlayerDetail);
            return true;
        });
    }

    @Override
    public Observable<Boolean> addGamePlayerScore(Long gameId, Long playerId, Long offset) {
        return Observable.fromCallable(() -> {
            GamePlayerDetail gamePlayerDetail = mDaoSession.getGamePlayerDetailDao().queryBuilder()
                    .where(GamePlayerDetailDao.Properties.GameId.eq(gameId),
                            GamePlayerDetailDao.Properties.PlayerId.eq(playerId))
                    .list().get(0);
            Long currentScore = gamePlayerDetail.getScore() != null ? gamePlayerDetail.getScore() : 0L;
            gamePlayerDetail.setScore(currentScore + offset);
            mDaoSession.getGamePlayerDetailDao().save(gamePlayerDetail);

            setGameLastUpdateTime(gameId);
            saveUserActionAddLog(gameId, playerId, offset);
            return true;
        });
    }

    @Override
    public Observable<Boolean> removeGamePlayerScore(Long gameId, Long playerId, Long offset) {
        return Observable.fromCallable(() -> {
            GamePlayerDetail gamePlayerDetail = mDaoSession.getGamePlayerDetailDao().queryBuilder()
                    .where(GamePlayerDetailDao.Properties.GameId.eq(gameId),
                            GamePlayerDetailDao.Properties.PlayerId.eq(playerId))
                    .list().get(0);
            Long currentScore = gamePlayerDetail.getScore() != null ? gamePlayerDetail.getScore() : 0L;
            gamePlayerDetail.setScore(currentScore - offset);
            mDaoSession.getGamePlayerDetailDao().save(gamePlayerDetail);

            setGameLastUpdateTime(gameId);
            saveUserActionRemoveLog(gameId, playerId, offset);
            return true;
        });
    }

    @Override
    public Observable<Boolean> transferGamePlayerScore(Long gameId, Long fromPlayerId, Long toPlayerId, Long offset) {
        return Observable.fromCallable(() -> {
            GamePlayerDetail fromGamePlayerDetail = mDaoSession.getGamePlayerDetailDao().queryBuilder()
                    .where(GamePlayerDetailDao.Properties.GameId.eq(gameId),
                            GamePlayerDetailDao.Properties.PlayerId.eq(fromPlayerId))
                    .list().get(0);
            GamePlayerDetail toGamePlayerDetail = mDaoSession.getGamePlayerDetailDao().queryBuilder()
                    .where(GamePlayerDetailDao.Properties.GameId.eq(gameId),
                            GamePlayerDetailDao.Properties.PlayerId.eq(toPlayerId))
                    .list().get(0);

            Long fromPlayerScore = fromGamePlayerDetail.getScore() != null ? fromGamePlayerDetail.getScore() : 0L;
            fromGamePlayerDetail.setScore(fromPlayerScore - offset);
            mDaoSession.getGamePlayerDetailDao().save(fromGamePlayerDetail);

            Long toPlayerScore = toGamePlayerDetail.getScore() != null ? toGamePlayerDetail.getScore() : 0L;
            toGamePlayerDetail.setScore(toPlayerScore + offset);
            mDaoSession.getGamePlayerDetailDao().save(toGamePlayerDetail);

            setGameLastUpdateTime(gameId);
            saveUserActionTransferLog(gameId, fromPlayerId, toPlayerId, offset);

            return true;
        });
    }

    @Override
    public Observable<List<Long>> saveGamePlayerDetailList(final List<GamePlayerDetail> gamePlayerDetailList) {
        return Observable.fromCallable(() -> {
            List<Long> idList = new ArrayList<>();
            for (GamePlayerDetail gamePlayerDetail : gamePlayerDetailList) {
                Long id = mDaoSession.getGamePlayerDetailDao().insertOrReplace(gamePlayerDetail);
                idList.add(id);
            }
            return idList;
        });
    }

    @Override
    public Observable<GamePlayerDetail> loadGamePlayerDetail(final Long id) {
        return Observable.fromCallable(() -> mDaoSession.getGamePlayerDetailDao().load(id));
    }

    @Override
    public Observable<GamePlayerDetail> loadGamePlayerDetail(final Long gameId, final Long playerId) {
        return Observable.fromCallable(() -> {
            List<GamePlayerDetail> gamePlayerDetailList = mDaoSession.getGamePlayerDetailDao().queryBuilder()
                    .where(GamePlayerDetailDao.Properties.GameId.eq(gameId),
                            GamePlayerDetailDao.Properties.PlayerId.eq(playerId))
                    .list();
            return gamePlayerDetailList.get(0);
        });
    }

    @Override
    public Observable<Long> savePlayerActionLog(final PlayerActionLog playerActionLog) {
        return Observable.fromCallable(() -> {
            playerActionLog.validate();
            return mDaoSession.getPlayerActionLogDao().insert(playerActionLog);
        });
    }

    @Override
    public Observable<List<PlayerActionLog>> loadPlayerActionLogsFromGame(final Long gameId) {
        return Observable.fromCallable(() -> mDaoSession.getPlayerActionLogDao().queryBuilder()
                .where(PlayerActionLogDao.Properties.GameId.eq(gameId))
                .orderDesc(PlayerActionLogDao.Properties.CreationTime)
                .list());
    }


    /**
     * Load latest player actions from specific game log
     * @param gameId - game id to identify the game
     * @param limit - number of log to load
     * @return
     */
    @Override
    public Observable<List<PlayerActionLog>> loadPlayerActionLogsFromGame(Long gameId, int limit) {
        return Observable.fromCallable(() -> mDaoSession.getPlayerActionLogDao().queryBuilder()
                .where(PlayerActionLogDao.Properties.GameId.eq(gameId))
                .orderDesc(PlayerActionLogDao.Properties.CreationTime)
                .limit(limit)
                .list());
    }

    /**
     * Load latest score action from specific game
     * @param gameId - game id to identify the game
     * @param limit - number of score to load
     * @return
     */
    @Override
    public Observable<List<Long>> loadScoreLogsFromGame(Long gameId, int limit) {


        return Observable.fromCallable(() -> {
            String SQL_DISTINCT_SCORE = "SELECT " + PlayerActionLogDao.Properties.ScoreOffset.columnName
                    + " FROM " + PlayerActionLogDao.TABLENAME
                    + " WHERE " + PlayerActionLogDao.Properties.GameId.columnName + "=" + gameId
                    + " GROUP BY " + PlayerActionLogDao.Properties.ScoreOffset.columnName
                    + " ORDER BY MAX (" + PlayerActionLogDao.Properties.CreationTime.columnName + ") DESC"
                    + " LIMIT " + limit;

            List<Long> result = new ArrayList<>();
            Cursor c = mDaoSession.getDatabase().rawQuery(SQL_DISTINCT_SCORE, null);
            try {
                if (c.moveToFirst()) {
                    do {
                        result.add(Long.parseLong(c.getString(0)));
                    } while (c.moveToNext());
                }
            } finally {
                c.close();
            }
            return result;
        });
    }

    private void saveUserActionAddLog(Long gameId, Long playerId, Long scoreOffset) {
        try {
            PlayerActionLog playerActionLog = new PlayerActionLog();
            playerActionLog.setActionType(PlayerActionEnum.ACTION_ADD.getValue());
            playerActionLog.setGameId(gameId);
            playerActionLog.setPlayerId(playerId);
            playerActionLog.setScoreOffset(scoreOffset);
            playerActionLog.setCreationTime(System.currentTimeMillis());

            playerActionLog.validate();
            mDaoSession.getPlayerActionLogDao().insert(playerActionLog);
        } catch (Exception e) {
            // TODO: Log error
        }
    }

    private void saveUserActionRemoveLog(Long gameId, Long playerId, Long scoreOffset) {
        try {
            PlayerActionLog playerActionLog = new PlayerActionLog();
            playerActionLog.setActionType(PlayerActionEnum.ACTION_REMOVE.getValue());
            playerActionLog.setGameId(gameId);
            playerActionLog.setPlayerId(playerId);
            playerActionLog.setScoreOffset(scoreOffset);
            playerActionLog.setCreationTime(System.currentTimeMillis());

            playerActionLog.validate();
            mDaoSession.getPlayerActionLogDao().insert(playerActionLog);
        } catch (Exception e) {
            // TODO: Log error
        }
    }

    private void saveUserActionTransferLog(Long gameId, Long fromPlayerId, Long toPlayerId, Long scoreOffset) {
        try {
            PlayerActionLog playerActionLog = new PlayerActionLog();
            playerActionLog.setActionType(PlayerActionEnum.ACTION_TRANSFER.getValue());
            playerActionLog.setGameId(gameId);
            playerActionLog.setPlayerId(fromPlayerId);
            playerActionLog.setToPlayerId(toPlayerId);
            playerActionLog.setScoreOffset(scoreOffset);
            playerActionLog.setCreationTime(System.currentTimeMillis());

            playerActionLog.validate();
            mDaoSession.getPlayerActionLogDao().insert(playerActionLog);
        } catch (Exception e) {
            // TODO: Log error
        }
    }

    private void setGameLastUpdateTime(Long gameId) {
        Game game = mDaoSession.getGameDao().load(gameId);
        if (game != null) {
            game.setLastUpdateTime(System.currentTimeMillis());
            mDaoSession.getGameDao().save(game);
        }
    }
}
