package ghost.gboardgametracker.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import ghost.gboardgametracker.di.scope.ApplicationContext;
import ghost.gboardgametracker.di.scope.PreferenceInfo;
import ghost.gboardgametracker.utils.AppConstants;

import javax.inject.Inject;

/**
 * Created by hoangnh on 2/21/17.
 */

public class AppPreferencesHelper implements PreferencesHelper {

    private static final String PREF_KEY_PLAYING_GAME__ID = "PREF_KEY_PLAYING_GAME_ID";

    private final SharedPreferences mPrefs;

    @Inject
    public AppPreferencesHelper(@ApplicationContext Context context, @PreferenceInfo String prefFileName) {
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }

    @Override
    public Long loadPlayingGameId() {
        return mPrefs.getLong(PREF_KEY_PLAYING_GAME__ID, AppConstants.NULL_ID);
    }

    @Override
    public void setPlayingGameId(Long gameId) {
        mPrefs.edit().putLong(PREF_KEY_PLAYING_GAME__ID, gameId).apply();
    }
}
