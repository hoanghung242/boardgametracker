package ghost.gboardgametracker.ui.main;

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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import ghost.gboardgametracker.R;
import ghost.gboardgametracker.data.DataManager;
import ghost.gboardgametracker.ui.base.BaseDrawerActivity;
import ghost.gboardgametracker.ui.gamefinish.GameFinishActivity;
import ghost.gboardgametracker.ui.main.model.GameUIModel;
import ghost.gboardgametracker.ui.navigation.NavigationItemEnum;
import ghost.gboardgametracker.ui.newgame.NewGameActivity;
import ghost.gboardgametracker.ui.play.GamePlayActivity;
import ghost.gboardgametracker.utils.AppConstants;
import ghost.gboardgametracker.utils.ViewHelper;
import ghost.gboardgametracker.utils.widget.scroll.EndlessRecyclerViewScrollListener;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseDrawerActivity implements MainMvpView{

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @Inject
    DataManager mDataManager;

    @BindView(R.id.game_list_view)
    RecyclerView mGameListView;

    @BindView(R.id.add_new_game_btn)
    FloatingActionButton mAddNewGameBtn;

    @BindView(R.id.no_game_layout_view)
    View mNoGameView;

    @BindView(R.id.add_new_game_hint)
    TextView mAddNewGameHintView;

    @BindView(R.id.adView)
    AdView mAdView;

    private List<GameUIModel> mGameViewList = new ArrayList<>();
    private GameAdapter mGameAdapter;
    private EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;

    private View.OnClickListener mOnGameClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Long gameid = (Long) v.getTag();
            mPresenter.onClickGame(gameid);
        }
    };

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        showAd();

        setupView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mEndlessRecyclerViewScrollListener.resetState();
        mPresenter.fetchGameViewList();
    }

    @Override
    public void displayGameList(List<GameUIModel> gameViewList) {
        mGameViewList.clear();
        if (gameViewList != null) {
            mGameViewList.addAll(gameViewList);

            if (gameViewList.size() == 0) {
                mNoGameView.setVisibility(View.VISIBLE);
            } else {
                mNoGameView.setVisibility(View.GONE);
            }

            if (gameViewList.size() >= AppConstants.GAME_LIST.DISPLAY_ADD_NEW_GAME_HINT_NUMBER) {
                mAddNewGameHintView.setVisibility(View.GONE);
            } else {
                mAddNewGameHintView.setVisibility(View.VISIBLE);
            }
        }
        mGameAdapter.notifyDataSetChanged();
    }

    @Override
    public void displayMoreGames(List<GameUIModel> gameViewList) {
        if (gameViewList != null) {
            mGameViewList.addAll(gameViewList);
            mGameAdapter.notifyItemRangeInserted(mGameViewList.size() - gameViewList.size(), mGameViewList.size());
        }
    }

    @Override
    public void navigateToGamePlay(Long gameId) {
        GamePlayActivity.start(this, gameId);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void navigateToGameFinish(Long gameId, String gameTitle) {
        GameFinishActivity.start(this, gameId, gameTitle);
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter_all:
                mPresenter.onClickFilterAll();
                return true;

            case R.id.action_filter_playing:
                mPresenter.onClickFilterPlaying();
                return true;

            case R.id.action_filter_finished:
                mPresenter.onClickFilterFinished();
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

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    @OnClick(R.id.add_new_game_btn)
    protected void OnClickAddNewGameBtn() {
        finish();
        NewGameActivity.start(this);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void setupView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mGameListView.setLayoutManager(linearLayoutManager);
        mGameListView.setHasFixedSize(true);
        mGameAdapter = new GameAdapter();
        mGameListView.setAdapter(mGameAdapter);
        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mPresenter.onLoadMoreGames(totalItemsCount);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mAddNewGameBtn.isShown()) {
                    mAddNewGameBtn.hide(true);
                } else if (dy < 0 && !mAddNewGameBtn.isShown()) {
                    mAddNewGameBtn.show(true);
                }
            }
        };

        mGameListView.addOnScrollListener(mEndlessRecyclerViewScrollListener);
    }

    private void showAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected NavigationItemEnum getSelfNavigationItem() {
        return NavigationItemEnum.HOME;
    }

    class GameViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.game_title)
        TextView mTitleTextView;

        @BindView(R.id.game_date)
        TextView mDateTextView;

        @BindView(R.id.game_number_of_players)
        TextView mPlayerNoTextView;

        @BindView(R.id.game_finish_textview)
        TextView mGameFinishTextView;

        public GameViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class GameAdapter extends RecyclerView.Adapter<GameViewHolder> {

        @Override
        public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_game_info, parent, false);
            itemView.setOnClickListener(mOnGameClickListener);
            return new GameViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(GameViewHolder holder, int position) {
            GameUIModel gameUIModel = mGameViewList.get(position);
            holder.mTitleTextView.setText(gameUIModel.game.getGameTitle());
            holder.mDateTextView.setText(ViewHelper.getDateString(gameUIModel.game.getLastUpdateTime()));
            holder.mPlayerNoTextView.setText(getString(R.string.number_of_player_format, gameUIModel.numberOfPlayer));
            if (gameUIModel.game.getGameFinish() != null && gameUIModel.game.getGameFinish()) {
                holder.mGameFinishTextView.setText(R.string.finished);
            } else {
                holder.mGameFinishTextView.setText("");
            }

            holder.itemView.setTag(gameUIModel.game.getId());
        }

        @Override
        public int getItemCount() {
            return mGameViewList.size();
        }
    }
}
