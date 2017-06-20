package ghost.gboardgametracker.ui.gamelog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ghost.gboardgametracker.R;
import ghost.gboardgametracker.data.utils.PlayerActionEnum;
import ghost.gboardgametracker.ui.base.BaseActivity;
import ghost.gboardgametracker.ui.gamelog.model.GameLogUIModel;
import ghost.gboardgametracker.utils.AppConstants;
import ghost.gboardgametracker.utils.ViewHelper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoangnh on 3/9/17.
 */

public class GameLogActivity extends BaseActivity implements GameLogMvpView{

    private static final String EXTRA_KEY_GAME_ID = "KEY_GAME_ID";
    private static final String EXTRA_KEY_GAME_TITLE = "KEY_GAME_TITLE";

    @Inject
    GameLogMvpPresenter<GameLogMvpView> mPresenter;

    @BindView(R.id.game_log_list_view)
    RecyclerView mGameLogListView;

    @BindView(R.id.no_game_layout_view)
    View mNoGameLayoutView;

    @BindView(R.id.no_item_text)
    TextView mNoItemText;

    private GameLogAdapter mGameLogAdapter;
    private List<GameLogUIModel> mGameLogUIModelList = new ArrayList<>();

    public static void start(Context context, Long gameId, String gameTitle) {
        Intent starter = new Intent(context, GameLogActivity.class);
        starter.putExtra(EXTRA_KEY_GAME_ID, gameId);
        starter.putExtra(EXTRA_KEY_GAME_TITLE, gameTitle);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_log);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            String gameTitle = getIntent().getStringExtra(EXTRA_KEY_GAME_TITLE);
            getSupportActionBar().setTitle(gameTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Long gameId = getIntent().getLongExtra(EXTRA_KEY_GAME_ID, AppConstants.NULL_ID);
        mPresenter.onCreated(gameId);

        setupView();
    }

    private void setupView() {
        mGameLogListView.setLayoutManager(new LinearLayoutManager(this));
        mGameLogListView.setHasFixedSize(true);
        mGameLogAdapter = new GameLogAdapter();
        mGameLogListView.setAdapter(mGameLogAdapter);

        mNoItemText.setText(R.string.no_logs);
    }

    @Override
    public void displayPlayerActionLogs(List<GameLogUIModel> gameLogUIModelList) {
        mGameLogUIModelList.clear();
        if (gameLogUIModelList != null) {
            mGameLogUIModelList.addAll(gameLogUIModelList);
        }

        if (mGameLogUIModelList.size() <= 0) {
            mNoGameLayoutView.setVisibility(View.VISIBLE);
        } else {
            mNoGameLayoutView.setVisibility(View.GONE);
        }
        mGameLogAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void componentInjection() {
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);
    }

    private class GameLogAdapter extends RecyclerView.Adapter<GameLogViewHolder> {

        @Override
        public GameLogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(GameLogActivity.this).inflate(R.layout.item_game_log, parent, false);
            return new GameLogViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(GameLogViewHolder holder, int position) {
            GameLogUIModel gameLogUIModel = mGameLogUIModelList.get(position);

            holder.textTitle.setText(gameLogUIModel.playerName);
            holder.textTime.setText(ViewHelper.getDateString(gameLogUIModel.creationTime));

            if (gameLogUIModel.actionType == PlayerActionEnum.ACTION_ADD.getValue()) {
                    holder.textDescription.setText(getString(R.string.add_score_to_player_format,
                            gameLogUIModel.actionScore, gameLogUIModel.playerName));

            } else if (gameLogUIModel.actionType == PlayerActionEnum.ACTION_REMOVE.getValue()) {
                holder.textDescription.setText(getString(R.string.remove_score_from_player_format,
                        gameLogUIModel.actionScore, gameLogUIModel.playerName));

            } else if (gameLogUIModel.actionType == PlayerActionEnum.ACTION_TRANSFER.getValue()) {
                holder.textDescription.setText(getString(R.string.transfer_score_to_player_format,
                        gameLogUIModel.actionScore, gameLogUIModel.playerName, gameLogUIModel.otherPlayerName));
            }
        }

        @Override
        public int getItemCount() {
            return mGameLogUIModelList.size();
        }
    }

    class GameLogViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_title)
        TextView textTitle;

        @BindView(R.id.text_description)
        TextView textDescription;

        @BindView(R.id.text_time)
        TextView textTime;

        public GameLogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
