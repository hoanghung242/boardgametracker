package ghost.gboardgametracker.utils;

/**
 * Created by hoangnh on 2/10/17.
 */

public interface AppConstants {
    String DB_NAME = "ghost.db";
    String PREF_NAME = "ghost_pref";

    int NAVIGATION_DRAWER_LAUNCH_DELAY = 250;
    long NULL_ID = -1;

    interface GAME_PLAY {
        int NUMBER_OF_SCORE_SUGGESTION = 5;
        int MINIMUM_NUMBER_OF_PLAYERS = 2;
        int MAXIMUM_NUMBER_OF_PLAYERS = 99;
        int MAXIMUM_NUMBER_OF_ROLLING_DICES = 6; // note: have to add more localization strings if this number if set
                                                 // to higher
    }

    interface GAME_PLAY_TIME {
        long RANDOM_PLAYER_SHOW_TIME = 5000;
    }

    interface SHOWCASE {
        long ADD_NEW_GAME_ID = 1;
    }

    interface GAME_LIST {
        int DISPLAY_ADD_NEW_GAME_HINT_NUMBER = 3;
        int DISPLAY_NUMBER_OF_GAME_PER_PAGE = 50;
    }
}
