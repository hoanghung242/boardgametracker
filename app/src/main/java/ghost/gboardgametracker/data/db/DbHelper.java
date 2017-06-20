package ghost.gboardgametracker.data.db;

import ghost.gboardgametracker.data.db.model.Game;
import ghost.gboardgametracker.data.db.model.GameFinishInfo;
import ghost.gboardgametracker.data.db.model.GamePlayerDetail;
import ghost.gboardgametracker.data.db.model.Player;
import ghost.gboardgametracker.data.db.model.PlayerActionLog;
import io.reactivex.Observable;

import java.util.List;

/**
 * Created by hoangnh on 2/10/17.
 */

public interface DbHelper {

    // Player
    Observable<Long> savePlayer(Player player);

    Observable<List<Long>> savePlayerList(List<Player> playerList);

    Observable<List<Player>> loadAllPlayers();

    Observable<List<Player>> loadPlayerListFromGame(Long gameId);

    Observable<Player> loadPlayer(Long playerId);

    // Game

    Observable<Long> saveGame(Game game, List<Player> playerList);

    Observable<Boolean> updateGame(Game game);

    @Deprecated
    Observable<List<Game>> loadAllGames();

    Observable<List<Game>> loadAllGamesSortedByCreationTimeDesc();

    /**
     * Load game data
     * @param offset : starting index of item data
     * @param limit : maximum number of items to load
     * @return
     */
    Observable<List<Game>> loadAllGames(int offset, int limit);

    Observable<Game> loadGame(Long gameId);

    Observable<Game> loadLastPlayedGame();

    // Finish Game

    Observable<Long> saveGameFinishInfo(GameFinishInfo gameFinishInfo);

    Observable<Long> finishGame(Long gameId, Long winnerPlayerId);

    Observable<GameFinishInfo> loadGameFinishInfo(Long gameFinishInfoId);

    Observable<GameFinishInfo> loadGameFinishInfoFromGame(Long gameId);

    // Relation

    Observable<Long> saveGamePlayerDetail(GamePlayerDetail gamePlayerDetail);

    Observable<Boolean> updateGamePlayerDetail(GamePlayerDetail gamePlayerDetail);

    Observable<Boolean> addGamePlayerScore(Long gameId, Long playerId, Long offset);

    Observable<Boolean> removeGamePlayerScore(Long gameId, Long playerId, Long offset);

    Observable<Boolean> transferGamePlayerScore(Long gameId, Long fromPlayerId, Long toPlayerId, Long offset);

    Observable<List<Long>> saveGamePlayerDetailList(List<GamePlayerDetail> gamePlayerDetailList);

    Observable<GamePlayerDetail> loadGamePlayerDetail(Long id);

    Observable<GamePlayerDetail> loadGamePlayerDetail(Long gameId, Long playerId);

    // Action Log

    Observable<Long> savePlayerActionLog(PlayerActionLog playerActionLog);

    Observable<List<PlayerActionLog>> loadPlayerActionLogsFromGame(Long gameId);

    Observable<List<PlayerActionLog>> loadPlayerActionLogsFromGame(Long gameId, int limit);

    Observable<List<Long>> loadScoreLogsFromGame(Long gameId, int limit);
}
