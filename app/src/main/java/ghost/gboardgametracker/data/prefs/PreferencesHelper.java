package ghost.gboardgametracker.data.prefs;

/**
 * Created by hoangnh on 2/21/17.
 */

public interface PreferencesHelper {

    Long loadPlayingGameId();

    void setPlayingGameId(Long gameId);
}
