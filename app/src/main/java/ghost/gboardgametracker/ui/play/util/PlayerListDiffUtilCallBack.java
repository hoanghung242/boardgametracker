package ghost.gboardgametracker.ui.play.util;

import android.support.v7.util.DiffUtil;
import ghost.gboardgametracker.ui.play.model.PlayerUIModel;

import java.util.List;

/**
 * Created by hoangnh on 3/22/17.
 */

public class PlayerListDiffUtilCallBack extends DiffUtil.Callback {

    List<PlayerUIModel> mOldPlayerUIModels;
    List<PlayerUIModel> mNewPlayerUIModels;
    private boolean mIsStartingScoreChanged;

    public PlayerListDiffUtilCallBack(List<PlayerUIModel> oldPlayerUIModels, List<PlayerUIModel> newPlayerUIModels, boolean isStartingScoreChanged) {
        mOldPlayerUIModels = oldPlayerUIModels;
        mNewPlayerUIModels = newPlayerUIModels;
        mIsStartingScoreChanged = isStartingScoreChanged;
    }

    @Override
    public int getOldListSize() {
        return mOldPlayerUIModels.size();
    }

    @Override
    public int getNewListSize() {
        return mNewPlayerUIModels.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldPlayerUIModels.get(oldItemPosition).id.compareTo(mNewPlayerUIModels.get(newItemPosition).id) == 0;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldPlayerUIModels.get(oldItemPosition).id.compareTo(mNewPlayerUIModels.get(newItemPosition).id) == 0
                && mOldPlayerUIModels.get(oldItemPosition).score.compareTo(mNewPlayerUIModels.get(newItemPosition).score) == 0
                && !mIsStartingScoreChanged;
    }
}
