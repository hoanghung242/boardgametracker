package ghost.gboardgametracker.ui.navigation;

import ghost.gboardgametracker.R;
import ghost.gboardgametracker.ui.main.MainActivity;
import ghost.gboardgametracker.ui.newgame.NewGameActivity;
import ghost.gboardgametracker.ui.play.GamePlayActivity;

/**
 * Created by hoangnh on 2/21/17.
 */

public enum NavigationItemEnum {

    PLAYING(R.id.nav_item_playing, R.string.playing, R.drawable.ic_drawer_play, GamePlayActivity.class),

    HOME(R.id.nav_item_home, R.string.home, R.drawable.ic_home, MainActivity.class),

    NEW_GAME(R.id.nav_item_new_game, R.string.playing, R.drawable.ic_drawer_play, NewGameActivity.class),

    //PLAYER(R.id.nav_item_players, R.string.playing, R.drawable.ic_drawer_play, PlayerActivity.class),

    INVALID(0, 0, 0, null);

    private int id;

    private int titleResource;

    private int iconResource;

    private Class classToLaunch;

    private boolean finishCurrentActivity;

    NavigationItemEnum(int id, int titleResource, int iconResource, Class classToLaunch) {
        this(id, titleResource, iconResource, classToLaunch, false);
    }

    NavigationItemEnum(int id, int titleResource, int iconResource, Class classToLaunch,
                       boolean finishCurrentActivity) {
        this.id = id;
        this.titleResource = titleResource;
        this.iconResource = iconResource;
        this.classToLaunch = classToLaunch;
        this.finishCurrentActivity = finishCurrentActivity;
    }

    public static NavigationItemEnum getById(int id) {
        NavigationItemEnum[] values = NavigationItemEnum.values();
        for (int i = 0; i < values.length; ++i) {
            if (values[i].getId() == id) {
                return values[i];
            }
        }
        return INVALID;
    }

    public int getId() {
        return id;
    }

    public int getTitleResource() {
        return titleResource;
    }

    public int getIconResource() {
        return iconResource;
    }

    public Class getClassToLaunch() {
        return classToLaunch;
    }

    public boolean isFinishCurrentActivity() {
        return finishCurrentActivity;
    }
}
