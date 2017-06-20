package ghost.gboardgametracker.ui.gamefinish;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.clans.fab.FloatingActionButton;
import ghost.gboardgametracker.R;
import ghost.gboardgametracker.ui.base.BaseActivity;
import ghost.gboardgametracker.ui.gamefinish.model.GamePlayUIModel;
import ghost.gboardgametracker.ui.gamefinish.model.PlayerUIModel;
import ghost.gboardgametracker.ui.gamelog.GameLogActivity;
import ghost.gboardgametracker.ui.newgame.NewGameActivity;
import ghost.gboardgametracker.utils.AppConstants;
import ghost.gboardgametracker.utils.ViewHelper;
import ghost.gboardgametracker.utils.widget.dialog.GameDialogBuilder;

import javax.inject.Inject;
import java.util.ArrayList;

public class GameFinishActivity extends BaseActivity implements GameFinishMvpView{

    private static final String EXTRA_KEY_GAME_ID = "KEY_GAME_ID";
    private static final String EXTRA_KEY_GAME_TITLE = "KEY_GAME_TITLE";

    @BindView(R.id.player_list_view)
    RecyclerView mPlayerListView;

    @BindView(R.id.add_new_game_btn)
    FloatingActionButton mPlayGameBtn;

    private GamePlayUIModel mGamePlayUIModel;
    private PlayerAdapter mPlayerAdapter;

    @Inject
    GameFinishMvpPresenter<GameFinishMvpView> mPresenter;

    public static void start(Context context, Long gameId, String gameTitle) {
        Intent starter = new Intent(context, GameFinishActivity.class);
        starter.putExtra(EXTRA_KEY_GAME_ID, gameId);
        starter.putExtra(EXTRA_KEY_GAME_TITLE, gameTitle);
        context.startActivity(starter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.game_finish_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_game_log:
                mPresenter.onClickGameLogMenu();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void displayGameInfo(GamePlayUIModel gamePlayUIModel) {
        mGamePlayUIModel = gamePlayUIModel;
        mPlayerAdapter.notifyDataSetChanged();
    }

    @Override
    public void navigateToNewGame(Long gameId) {
        GameDialogBuilder builder = new GameDialogBuilder(this);
        builder.setDialogTitle(R.string.start_new_game);
        builder.setDialogIcon(R.drawable.ic_play_circle_filled_white);
        builder.setDialogDescription(R.string.do_you_want_to_create_a_new_game_with_same_setting);
        builder.setPositiveButton(R.string.ok, dialog -> {
            dialog.dismiss();
            GameFinishActivity.this.finish();
            GameFinishActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            NewGameActivity.start(GameFinishActivity.this, gameId);
        });

        builder.setNegativeButton(R.string.cancel, dialog -> dialog.dismiss());
        builder.create().show();
    }

    @Override
    public void navigateToGameLog(Long gameId, String gameTitle) {
        GameLogActivity.start(this, gameId, gameTitle);
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finish);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            String gameTitle = getIntent().getStringExtra(EXTRA_KEY_GAME_TITLE);
            getSupportActionBar().setTitle(gameTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mGamePlayUIModel = new GamePlayUIModel();
        mGamePlayUIModel.gamePlayerUIModelList = new ArrayList<>();

        Long gameId = getIntent().getLongExtra(EXTRA_KEY_GAME_ID, AppConstants.NULL_ID);
        mPresenter.onCreated(gameId);

        setupView();
    }

    @Override
    protected void componentInjection() {
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);
    }

    @OnClick(R.id.add_new_game_btn)
    protected void onClickPlayGame() {
        mPresenter.onClickPlayBtn();
    }

    private void setupView() {
        mPlayerListView.setLayoutManager(new LinearLayoutManager(this));
        mPlayerListView.setHasFixedSize(true);
        mPlayerAdapter = new PlayerAdapter();
        mPlayerListView.setAdapter(mPlayerAdapter);
        mPlayerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mPlayGameBtn.isShown()) {
                    mPlayGameBtn.hide(true);
                } else if (dy < 0 && !mPlayGameBtn.isShown()) {
                    mPlayGameBtn.show(true);
                }
            }
        });
    }

    private long getStartingScore() {
        return mGamePlayUIModel.game.getStartingScore() == null ? 0 : mGamePlayUIModel.game.getStartingScore();
    }

    private class PlayerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int HEADER_TYPE = 0;
        private static final int PLAYER_TYPE = 1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == HEADER_TYPE) {
                View itemView = LayoutInflater.from(GameFinishActivity.this).inflate(R.layout.item_finish_game_header,
                        parent, false);
                return new HeaderViewHolder(itemView);
            } else if (viewType == PLAYER_TYPE) {
                View itemView = LayoutInflater.from(GameFinishActivity.this).inflate(R.layout.item_finish_game_player,
                        parent, false);
                return new PlayerViewHolder(itemView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HeaderViewHolder) {
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                if (mGamePlayUIModel.game != null) {
                    if (mGamePlayUIModel.game.getCreationTime() != null) {
                        headerViewHolder.creationDateView.setText(ViewHelper.getDateString(mGamePlayUIModel.game.getCreationTime()));
                    }

                    if (mGamePlayUIModel.gameFinishInfo.getCreationDate() != null) {
                        headerViewHolder.endingDateView.setText(ViewHelper.getDateString(mGamePlayUIModel.gameFinishInfo.getCreationDate()));
                    }

                    int winningResId = mGamePlayUIModel.game.getWinningScoreConditionType() == null || mGamePlayUIModel
                            .game.getWinningScoreConditionType() == 0 ? R.string.highest_score_win : R.string.lowest_score_win;
                    headerViewHolder.winningConditionView.setText(winningResId);

                    if (mGamePlayUIModel.game.getStartingScoreEnabled() != null && mGamePlayUIModel.game
                            .getStartingScoreEnabled()) {
                        headerViewHolder.startingScoreView.setText(ViewHelper.getFormatScore(getStartingScore()));
                    } else {
                        headerViewHolder.startingScoreView.setText(R.string.not_set);
                    }
                }
            } else if (holder instanceof PlayerViewHolder) {
                PlayerViewHolder playerViewHolder = (PlayerViewHolder) holder;
                PlayerUIModel playerUIModel = mGamePlayUIModel.gamePlayerUIModelList.get(position - 1);
                if (playerUIModel.rank == 1) {
                    // Winner layout
                    playerViewHolder.playerRankView.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_shape_winner_background));
                } else {
                    playerViewHolder.playerRankView.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_shape_background));
                }
                playerViewHolder.playerRankView.setText(String.valueOf(playerUIModel.rank));
                playerViewHolder.playerNameView.setText(playerUIModel.name);
                playerViewHolder.playerScoreView.setText(ViewHelper.getFormatScore(playerUIModel.score + getStartingScore()));
            }
        }

        @Override
        public int getItemCount() {
            return mGamePlayUIModel.gamePlayerUIModelList.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return HEADER_TYPE;
            } else {
                return PLAYER_TYPE;
            }
        }
    }

    class PlayerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.player_rank)
        TextView playerRankView;

        @BindView(R.id.player_name)
        TextView playerNameView;

        @BindView(R.id.player_score)
        TextView playerScoreView;

        public PlayerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.creation_date)
        TextView creationDateView;

        @BindView(R.id.ending_date)
        TextView endingDateView;

        @BindView(R.id.winning_condition)
        TextView winningConditionView;

        @BindView(R.id.starting_score)
        TextView startingScoreView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
