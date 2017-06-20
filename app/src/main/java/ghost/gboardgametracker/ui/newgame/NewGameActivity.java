package ghost.gboardgametracker.ui.newgame;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ghost.gboardgametracker.R;
import ghost.gboardgametracker.data.db.model.Game;
import ghost.gboardgametracker.data.db.model.Player;
import ghost.gboardgametracker.ui.base.BaseDrawerActivity;
import ghost.gboardgametracker.ui.navigation.NavigationItemEnum;
import ghost.gboardgametracker.ui.newgame.model.NewGameUIModel;
import ghost.gboardgametracker.ui.play.GamePlayActivity;
import ghost.gboardgametracker.utils.AppConstants;

import javax.inject.Inject;
import java.util.ArrayList;

public class NewGameActivity extends BaseDrawerActivity implements NewGameMvpView {

    private static final String EXTRA_KEY_GAME_ID = "game_id";

    @Inject
    NewGameMvpPresenter<NewGameMvpView> mPresenter;

    @BindView(R.id.player_list_view)
    RecyclerView mPlayerListView;

    @BindView(R.id.game_title)
    EditText mGameTitleEditText;

    @BindView(R.id.cl_root_view)
    View activityView;

    private PlayerAdapter mPlayerAdapter;
    private NewGameUIModel mNewGameUIModel;
    private int mNewPlayerPos = -1;
    
    public static void start(Context context) {
        Intent starter = new Intent(context, NewGameActivity.class);
        context.startActivity(starter);
    }

    public static void start(Context context, Long gameId) {
        Intent starter = new Intent(context, NewGameActivity.class);
        starter.putExtra(EXTRA_KEY_GAME_ID, gameId);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        ButterKnife.bind(this);

        mNewGameUIModel = new NewGameUIModel();
        mNewGameUIModel.game = new Game();
        mNewGameUIModel.playerList = new ArrayList<>();

        setupView();

        Long gameId = getIntent().getLongExtra(EXTRA_KEY_GAME_ID, AppConstants.NULL_ID);
        mPresenter.loadGameInfo(gameId);

        mPresenter.onViewLoaded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                mNewGameUIModel.game.setGameTitle(mGameTitleEditText.getText().toString());
                mPresenter.onClickStartGame(mNewGameUIModel);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void displayGameInfo(NewGameUIModel newGameUIModel) {
        mNewGameUIModel = newGameUIModel;
        if (!TextUtils.isEmpty(mNewGameUIModel.game.getGameTitle())) {
            mGameTitleEditText.setText(mNewGameUIModel.game.getGameTitle());
        }
        mPlayerAdapter.notifyDataSetChanged();
    }

    @Override
    public void navigationToGamePlay(Long gameId) {
        GamePlayActivity.start(this, gameId);
        finish();
    }

    @Override
    public void removePlayer(NewGameUIModel newGameUIModel, int playerPos) {
        mNewGameUIModel = newGameUIModel;
        mPlayerAdapter.notifyItemRemoved(playerPos);
        mPlayerAdapter.notifyItemRangeChanged(playerPos, mNewGameUIModel.playerList.size());
    }

    @Override
    public void addNewPlayer(NewGameUIModel newGameUIModel, int playerPos) {
        mNewGameUIModel = newGameUIModel;
        mNewPlayerPos = playerPos;
        mPlayerAdapter.notifyItemInserted(playerPos);
        mPlayerListView.scrollToPosition(playerPos);
    }

    @Override
    protected void componentInjection() {
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);
    }

    @Override
    protected void setupActionBar() {
        super.setupActionBar();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.new_game);
        }
    }

    @Override
    protected NavigationItemEnum getSelfNavigationItem() {
        return NavigationItemEnum.NEW_GAME;
    }

    @OnClick(R.id.add_player_btn)
    protected void onClickAddPlayerBtn() {
        hideKeyboard();
        mPresenter.addNewPlayer();
    }

    private void setupView() {

        activityView.requestFocus();

        mPlayerListView.setLayoutManager(new LinearLayoutManager(this));
        mPlayerListView.setHasFixedSize(true);
        mPlayerAdapter = new PlayerAdapter();
        mPlayerListView.setAdapter(mPlayerAdapter);
    }

    class PlayerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.player_name)
        EditText mPlayerName;

        @BindView(R.id.player_number)
        TextView mPlayerNumber;

        @BindView(R.id.player_remove)
        ImageButton playerRemove;

        PlayerNameTextWatcher mPlayerNameTextWatcher;

        public PlayerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mPlayerNameTextWatcher = new PlayerNameTextWatcher();
            mPlayerName.addTextChangedListener(mPlayerNameTextWatcher);
        }
    }

    private class PlayerAdapter extends RecyclerView.Adapter<PlayerViewHolder> {

        @Override
        public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View viewItem = LayoutInflater.from(NewGameActivity.this).inflate(R.layout.item_new_player, parent, false);
            return new PlayerViewHolder(viewItem);
        }

        @Override
        public void onBindViewHolder(PlayerViewHolder holder, int position) {
            Player player = mNewGameUIModel.playerList.get(position);
            holder.mPlayerNumber.setText(String.valueOf(position + 1));
            holder.mPlayerNameTextWatcher.setPostion(position);
            if (player.getName() != null) {
                holder.mPlayerName.setText(player.getName());
            } else {
                holder.mPlayerName.setText("");
                holder.mPlayerName.setHint(getString(R.string.new_player_name));
            }
            holder.itemView.setTag(position);
            if (position == mNewPlayerPos) {
                holder.mPlayerName.requestFocus();
            }

            holder.playerRemove.setTag(position);
            holder.playerRemove.setOnClickListener(v -> {
                mPresenter.removePlayer((int) v.getTag());
                hideKeyboard();
            });
        }

        @Override
        public int getItemCount() {
            return mNewGameUIModel.playerList.size();
        }

    }

    private class PlayerNameTextWatcher implements TextWatcher {

        private int mPostion;

        public void setPostion(int postion) {
            this.mPostion = postion;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mNewGameUIModel.playerList.get(mPostion).setName(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }
}
