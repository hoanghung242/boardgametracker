package ghost.gboardgametracker.ui.play;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.wang.avi.AVLoadingIndicatorView;
import ghost.gboardgametracker.R;
import ghost.gboardgametracker.data.utils.PlayerActionEnum;
import ghost.gboardgametracker.ui.base.BaseDrawerActivity;
import ghost.gboardgametracker.ui.gamefinish.GameFinishActivity;
import ghost.gboardgametracker.ui.gamelog.GameLogActivity;
import ghost.gboardgametracker.ui.gamesetting.GameSettingActivity;
import ghost.gboardgametracker.ui.navigation.NavigationItemEnum;
import ghost.gboardgametracker.ui.play.model.GamePlayUIModel;
import ghost.gboardgametracker.ui.play.model.PlayerUIModel;
import ghost.gboardgametracker.ui.play.util.PlayerListDiffUtilCallBack;
import ghost.gboardgametracker.ui.playtime.PlayTimeActivity;
import ghost.gboardgametracker.utils.AppConstants;
import ghost.gboardgametracker.utils.ViewHelper;
import ghost.gboardgametracker.utils.widget.dialog.GameDialogBuilder;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePlayActivity extends BaseDrawerActivity implements GamePlayMvpView {

    private static final String EXTRA_KEY_GAME_ID = "game_id";

    private static final int[] DICE_IMAGES_ARR_ID = new int[] {R.mipmap.ic_dice_1, R.mipmap.ic_dice_2, R.mipmap.ic_dice_3,
            R.mipmap.ic_dice_4, R.mipmap.ic_dice_5, R.mipmap.ic_dice_6};

    @Inject
    GamePlayMvpPresenter<GamePlayMvpView> mPresenter;

    @BindView(R.id.no_game_layout_view)
    View mNoGameLayoutView;

    @BindView(R.id.player_list_view)
    RecyclerView mPlayerListView;

    @BindView(R.id.action_menu)
    FloatingActionMenu mFloatingActionMenu;

    @BindView(R.id.start_player_turn_time_btn)
    FloatingActionButton mPlayerTurnTimerBtn;

    @BindView(R.id.pick_random_player_btn)
    FloatingActionButton mPickRandomPlayerBtn;

    @BindView(R.id.roll_a_dice_btn)
    FloatingActionButton mRollDiceBtn;

    @BindView(R.id.adView)
    AdView mAdView;

    private PlayerAdapter mPlayerAdapter;

    private GamePlayUIModel mGamePlayUIModel;

    private CountDownTimer mTotalCountdownTimer;

    private int mRandomPlayerPos = -1;
    private CountDownTimer mRandomPlayerShowTimer;

    private List<RelativeLayout> mDiceImageLayoutList = new ArrayList<>();
    private Random mRandom = new Random();
    private Handler mHandler = new Handler();

    public static void start(Context context, Long gameId) {
        Intent starter = new Intent(context, GamePlayActivity.class);
        starter.putExtra(EXTRA_KEY_GAME_ID, gameId);
        context.startActivity(starter);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, GamePlayActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void displayGameInfo(GamePlayUIModel gamePlayUIModel) {
        mNoGameLayoutView.setVisibility(View.GONE);

        PlayerListDiffUtilCallBack playerListDiffUtilCallBack = new PlayerListDiffUtilCallBack(mGamePlayUIModel.gamePlayerUIModelList,
                gamePlayUIModel.gamePlayerUIModelList, isStartingScoreChange(mGamePlayUIModel, gamePlayUIModel));

        mGamePlayUIModel = new GamePlayUIModel();
        mGamePlayUIModel.game = gamePlayUIModel.game;
        mGamePlayUIModel.gamePlayerUIModelList = new ArrayList<>();
        for(PlayerUIModel playerUIModel : gamePlayUIModel.gamePlayerUIModelList) {
            mGamePlayUIModel.gamePlayerUIModelList.add(PlayerUIModel.create(playerUIModel));
        }

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(playerListDiffUtilCallBack);
        diffResult.dispatchUpdatesTo(mPlayerAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mGamePlayUIModel.game.getGameTitle());
        }

        if (gamePlayUIModel.game.getRollingDiceEnabled() != null &&
                gamePlayUIModel.game.getRollingDiceEnabled() && gamePlayUIModel.game.getNumberOfRollingDices() != null) {
            // Roll a dice enabled
            mRollDiceBtn.setVisibility(View.VISIBLE);
        } else {
            mRollDiceBtn.setVisibility(View.GONE);
        }

        if (gamePlayUIModel.game.getPlayerTurnTimeEnabled() != null &&
                gamePlayUIModel.game.getPlayerTurnTimeEnabled() && gamePlayUIModel.game.getPlayerTurnTime() != null) {
            // Player turn time enable, show the timer
            mPlayerTurnTimerBtn.setVisibility(View.VISIBLE);
            mPlayerTurnTimerBtn.setLabelText(ViewHelper.getTimeDescription(this, gamePlayUIModel.game.getPlayerTurnTime()));
        } else {
            // Ask user to enable player turn time
            mPlayerTurnTimerBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void displayNoGamePlayingNow() {
        mNoGameLayoutView.setVisibility(View.VISIBLE);
        mFloatingActionMenu.setVisibility(View.GONE);
    }

    @Override
    public void showAddScoreDialog(int playerPos, List<Long> scoreSuggestList) {
        PlayerUIModel gamePlayerUIModel1 = mGamePlayUIModel.gamePlayerUIModelList.get(playerPos);
        String title = GamePlayActivity.this.getString(R.string.add_to_player_format, gamePlayerUIModel1.name);
        showPlayerActionDialog(title, playerPos, PlayerActionEnum.ACTION_ADD, scoreSuggestList);
    }

    @Override
    public void showRemoveScoreDialog(int playerPos, List<Long> scoreSuggestList) {
        PlayerUIModel gamePlayerUIModel12 = mGamePlayUIModel.gamePlayerUIModelList.get(playerPos);
        String title = GamePlayActivity.this.getString(R.string.remove_from_player_format, gamePlayerUIModel12
                .name);
        showPlayerActionDialog(title, playerPos, PlayerActionEnum.ACTION_REMOVE, scoreSuggestList);
    }

    @Override
    public void showTransferScoreDialog(int playerPos, List<Long> scoreSuggestList) {
        PlayerUIModel gamePlayerUIModel13 = mGamePlayUIModel.gamePlayerUIModelList.get(playerPos);
        String title = GamePlayActivity.this.getString(R.string.transfer_from_player_format,
                gamePlayerUIModel13.name);
        showPlayerActionDialog(title, playerPos, PlayerActionEnum.ACTION_TRANSFER, scoreSuggestList);
    }

    @Override
    public void showRandomPlayer(int playerPos) {
        mFloatingActionMenu.close(true);
        mPlayerAdapter.notifyItemChanged(mRandomPlayerPos);
        mRandomPlayerPos = playerPos;
        mPlayerAdapter.notifyItemChanged(mRandomPlayerPos);
        mPlayerListView.scrollToPosition(mRandomPlayerPos);

        if (mRandomPlayerShowTimer != null) {
            mRandomPlayerShowTimer.cancel();
        }

        // after a specific time, no longer highlight the random player
        mRandomPlayerShowTimer = new CountDownTimer(AppConstants.GAME_PLAY_TIME.RANDOM_PLAYER_SHOW_TIME, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                int oldPos = mRandomPlayerPos;
                mRandomPlayerPos = -1;
                if (mPlayerAdapter != null) {
                    mPlayerAdapter.notifyItemChanged(oldPos);
                }
            }
        };

        mRandomPlayerShowTimer.start();
    }

    @Override
    public void showRollDice(int numberOfDice) {
        GameDialogBuilder builder = new GameDialogBuilder(this);
        builder.setDialogTitle(R.string.roll_a_dice);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_roll_dice, null);
        setupDiceView((FlexboxLayout) contentView.findViewById(R.id.dice_group_layout), numberOfDice);
        builder.setContentView(contentView);
        builder.setPositiveButton(R.string.close, DialogInterface::dismiss);
        builder.create().show();
    }

    @Override
    public void navigateToGameSetting(Long gameId) {
        GameSettingActivity.start(this, gameId);
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void navigateToPlayTime(Long duration) {
        PlayTimeActivity.createInstance(this, duration);
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void navigateToGameLog(Long gameId, String gameTitle) {
        GameLogActivity.start(this, gameId, gameTitle);
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void navigateToGameFinish(Long gameId, String gameTitle) {
        GamePlayActivity.this.finish();
        GamePlayActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        GameFinishActivity.start(GamePlayActivity.this, gameId, gameTitle);
    }

    @Override
    public void onAddedScore(int playerPos, long scoreOffset) {
        PlayerUIModel playerUIModel = mGamePlayUIModel.gamePlayerUIModelList.get(playerPos);
        this.showSnackBarMessage(getString(R.string.add_score_to_player_format, scoreOffset, playerUIModel.name));
    }

    @Override
    public void onRemovedScore(int playerPos, long scoreOffset) {
        PlayerUIModel playerUIModel = mGamePlayUIModel.gamePlayerUIModelList.get(playerPos);
        this.showSnackBarMessage(getString(R.string.remove_score_from_player_format, scoreOffset, playerUIModel.name));
    }

    @Override
    public void onTransferedScore(int fromPlayerPos, int toPlayerPos, long scoreOffset) {
        PlayerUIModel fromPlayer = mGamePlayUIModel.gamePlayerUIModelList.get(fromPlayerPos);
        PlayerUIModel toPlayer = mGamePlayUIModel.gamePlayerUIModelList.get(toPlayerPos);
        this.showSnackBarMessage(getString(R.string.transfer_score_to_player_format, scoreOffset, fromPlayer.name,
                toPlayer.name));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.game_play_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_game_settings:
                mPresenter.onClickGameSettings();
                return true;

            case R.id.action_game_log:
                mPresenter.onClickGameLogMenu();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        ButterKnife.bind(this);

        mGamePlayUIModel = new GamePlayUIModel();
        mGamePlayUIModel.gamePlayerUIModelList = new ArrayList<>();

        showAd();
        setupGamePLay();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Long gameId = getIntent().getLongExtra(EXTRA_KEY_GAME_ID, AppConstants.NULL_ID);
        mPresenter.loadGamePlayInfo(gameId);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mTotalCountdownTimer != null) {
            mTotalCountdownTimer.cancel();
        }
    }

    @Override
    protected void componentInjection() {
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);
    }

    @Override
    protected NavigationItemEnum getSelfNavigationItem() {
        return NavigationItemEnum.PLAYING;
    }

    @OnClick(R.id.start_player_turn_time_btn)
    void onClickStartPlayerTurnTimeBtn() {
        mPresenter.onClickStartPlayerTurnTimeBtn();
    }

    @OnClick(R.id.pick_random_player_btn)
    void onClickPickRandomPlayerBtn() {
        mPresenter.onClickPickRandomPlayer();
    }

    @OnClick(R.id.finish_game_btn)
    void onClickFinishGameBtn() {
        GameDialogBuilder builder = new GameDialogBuilder(this);
        builder.setDialogTitle(R.string.finish_game);
        builder.setDialogIcon(R.drawable.ic_done_circle);
        builder.setDialogDescription(R.string.are_you_sure_you_want_to_finish_this_game_now);
        builder.setPositiveButton(R.string.ok, dialog -> {
            dialog.dismiss();
            mPresenter.onClickFinishGameBtn();
        });

        builder.setNegativeButton(R.string.cancel, dialog -> dialog.dismiss());
        builder.create().show();
    }

    @OnClick(R.id.roll_a_dice_btn)
    void onClickRollDiceBtn() {
        mPresenter.onClickRollDiceBtn();
    }

    private void setupGamePLay() {
        mPlayerListView.setLayoutManager(new LinearLayoutManager(this));
        mPlayerListView.setHasFixedSize(true);
        mPlayerAdapter = new PlayerAdapter();
        mPlayerListView.setAdapter(mPlayerAdapter);
        mPlayerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mFloatingActionMenu.isShown()) {
                    hideSetting();
                } else if (dy < 0 && !mFloatingActionMenu.isShown()) {
                    showSetting();
                }
            }
        });
    }

    private void hideSetting() {
        mFloatingActionMenu.hideMenu(true);
    }

    private void showSetting() {
        mFloatingActionMenu.showMenu(true);
    }

    private void showPlayerActionDialog(String title, final int playerPos, final PlayerActionEnum playerAction,
                                        List<Long> scoreSuggestList) {

        GameDialogBuilder builder = new GameDialogBuilder(this);
        builder.setDialogTitle(title);

        View contentView = LayoutInflater.from(this).inflate(R.layout.user_action_dialog, null);
        View transferLayout = contentView.findViewById(R.id.transfer_layout);
        EditText actionScoreText = (EditText) contentView.findViewById(R.id.player_action_score_input);
        FlexboxLayout suggestLayout = (FlexboxLayout) contentView.findViewById(R.id.score_suggest_layout);
        if (scoreSuggestList != null) {
            for (Long suggestItem : scoreSuggestList) {
                View view = LayoutInflater.from(this).inflate(R.layout.item_score_suggest, suggestLayout, false);
                TextView scoreView = (TextView) view.findViewById(R.id.score);
                scoreView.setText(String.valueOf(suggestItem));
                view.setTag(suggestItem);
                view.setOnClickListener(v -> {
                    Long value = (Long) v.getTag();
                    actionScoreText.setText(String.valueOf(value));
                });
                suggestLayout.addView(view);
            }
        }

        final Spinner playerSpinner = (Spinner) contentView.findViewById(R.id.player_spinner);

        int positiveBtnTitle;

        switch (playerAction) {
            case ACTION_ADD:
                positiveBtnTitle = R.string.add;
                builder.setDialogIcon(R.drawable.ic_add_circle_white);
                break;
            case ACTION_REMOVE:
                positiveBtnTitle = R.string.remove;
                builder.setDialogIcon(R.drawable.ic_remove_circle_white);
                break;
            case ACTION_TRANSFER:
                positiveBtnTitle = R.string.transfer;
                builder.setDialogIcon(R.drawable.ic_transfer_circle_white);
                // Setup player selection view
                transferLayout.setVisibility(View.VISIBLE);
                List<String> playerNameList = new ArrayList<>();
                for (int i = 0; i < mGamePlayUIModel.gamePlayerUIModelList.size(); ++i) {
                    // add all players to transfer list except the current one
                    // hence, the transfer list position will be different from the original player list
                    // if the index k in transfer list will be (k + 1) in original list if k >= playerPos;
                    if (i != playerPos) {
                        PlayerUIModel playerUIModel = mGamePlayUIModel.gamePlayerUIModelList.get(i);
                        playerNameList.add(playerUIModel.name);
                    }
                }

                //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                //    playerNameList);
                //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_transfer_player_view,
                    playerNameList);
                adapter.setDropDownViewResource(R.layout.item_transfer_player_selected_view);
                playerSpinner.setAdapter(adapter);

                break;
            default:
                return;
        }

        builder.setContentView(contentView);
        builder.setPositiveButton(positiveBtnTitle, dialog -> {
            dialog.dismiss();
            if (!TextUtils.isEmpty(actionScoreText.getText())) {
                long editScore = Long.parseLong(actionScoreText.getText().toString());

                if (editScore > 0) {
                    switch (playerAction) {
                        case ACTION_TRANSFER:
                            int otherPlayerPos = playerSpinner.getSelectedItemPosition(); // position in transfer list
                            if (otherPlayerPos >= playerPos) {
                                otherPlayerPos++; // real position in original player list
                            }
                            mPresenter.transferScore(playerPos, otherPlayerPos, editScore);
                            break;
                        case ACTION_ADD:
                            mPresenter.addScoreToPlayer(playerPos, editScore);
                            break;
                        case ACTION_REMOVE:
                            mPresenter.removeScoreToPlayer(playerPos, editScore);
                            break;
                    }
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, DialogInterface::dismiss);
        builder.create().show();
    }

    private boolean isStartingScoreChange(GamePlayUIModel oldGame, GamePlayUIModel newGame) {
        if (oldGame.game == null && newGame.game == null) {
            return false;
        }

        if (oldGame.game == null || newGame.game == null ) {
            return true;
        }

        if (oldGame.game.getStartingScoreEnabled() == null && newGame.game.getStartingScoreEnabled() == null) {
            return false;
        }

        if (oldGame.game.getStartingScoreEnabled() != null && newGame.game.getStartingScoreEnabled() != null) {
            long oldScore = oldGame.game.getStartingScore() == null ? 0L : oldGame.game.getStartingScore();
            long newScore = newGame.game.getStartingScore() == null ? 0L : newGame.game.getStartingScore();
            return (oldScore == newScore);
        }

        return true;
    }

    private void setupDiceView(FlexboxLayout mDiceGroupLayout, int numberOfDice) {
        for (int i = 0; i < numberOfDice; ++i) {
            RelativeLayout view = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.item_dice_image, mDiceGroupLayout, false);
            mDiceGroupLayout.addView(view);
            mDiceImageLayoutList.add(view);
        }

        mDiceGroupLayout.setOnClickListener(v -> {
            // flip all dices
            v.setClickable(false);
            for (RelativeLayout diceImageLayout : mDiceImageLayoutList) {
                flip(v, diceImageLayout);
            }
        });
    }

    private void showAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void flip(View parent, RelativeLayout diceImageLayout) {
        ImageView diceImage = (ImageView) diceImageLayout.findViewById(R.id.dice_image);
        AVLoadingIndicatorView indicatorView = (AVLoadingIndicatorView) diceImageLayout.findViewById(R.id.dice_loading_indicator);

        indicatorView.setVisibility(View.VISIBLE);
        diceImage.setVisibility(View.GONE);
        mHandler.postDelayed(() -> {
            indicatorView.setVisibility(View.GONE);
            int diceNum = mRandom.nextInt(DICE_IMAGES_ARR_ID.length);
            diceImage.setVisibility(View.VISIBLE);
            diceImage.setImageDrawable(getResources().getDrawable(DICE_IMAGES_ARR_ID[diceNum]));
            parent.setClickable(true);
        }, 3000);
    }

    class PlayerAdapter extends RecyclerView.Adapter<PlayerViewHolder> {

        @Override
        public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View viewItem = LayoutInflater.from(GamePlayActivity.this).inflate(R.layout.item_in_game_player, parent, false);
            return new PlayerViewHolder(viewItem);
        }

        @Override
        public void onBindViewHolder(PlayerViewHolder holder, int position) {
            PlayerUIModel gamePlayerUIModel = mGamePlayUIModel.gamePlayerUIModelList.get(position);

            if (position == mRandomPlayerPos) {
                // Highlight random player
                holder.playerView.setBackgroundResource(R.color.random_player_bg_color);
            } else {
                holder.playerView.setBackgroundResource(R.color.transparent);
            }

            holder.playerName.setText(String.valueOf(gamePlayerUIModel.name));
            Long startingScore = 0L;
            if (mGamePlayUIModel.game.getStartingScoreEnabled() != null && mGamePlayUIModel.game.getStartingScoreEnabled()) {
                startingScore = mGamePlayUIModel.game.getStartingScore() == null ? 0 :mGamePlayUIModel.game.getStartingScore();
            }
            holder.playerScore.setText(ViewHelper.getFormatScore(gamePlayerUIModel.score + startingScore));

            holder.playerActionAdd.setTag(position);
            holder.playerActionAdd.setOnClickListener(v -> {
                mPresenter.onClickAddScore((int) v.getTag());
            });

            holder.playerActionRemove.setTag(position);
            holder.playerActionRemove.setOnClickListener(v -> {
                mPresenter.onClickRemoveScore((int) v.getTag());
            });

            holder.playerActionTransfer.setTag(position);
            holder.playerActionTransfer.setOnClickListener(v -> {
                mPresenter.onClickTransferScore((int) v.getTag());
            });
        }

        @Override
        public int getItemCount() {
            return mGamePlayUIModel.gamePlayerUIModelList.size();
        }
    }

    class PlayerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.player_view)
        View playerView;

        @BindView(R.id.player_name)
        TextView playerName;

        @BindView(R.id.player_score)
        TextView playerScore;

        @BindView(R.id.player_action_add)
        View playerActionAdd;

        @BindView(R.id.player_action_remove)
        View playerActionRemove;

        @BindView(R.id.player_action_transfer)
        View playerActionTransfer;

        public PlayerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
