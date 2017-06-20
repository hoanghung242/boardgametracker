package ghost.gboardgametracker.data.utils;

/**
 * Created by hoangnh on 3/8/17.
 */

public enum PlayerActionEnum {
    ACTION_ADD(0), ACTION_REMOVE(1), ACTION_TRANSFER(2);

    private int mValue;

    private PlayerActionEnum(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }
}
