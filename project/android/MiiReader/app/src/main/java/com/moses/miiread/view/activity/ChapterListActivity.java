package com.moses.miiread.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.hwangjr.rxbus.RxBus;
import com.kunfei.basemvplib.impl.IPresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.moses.miiread.BitIntentDataManager;
import com.moses.miiread.R;
import com.moses.miiread.base.BaseTabActivity;
import com.moses.miiread.base.MBaseActivity;
import com.moses.miiread.bean.BookShelfBean;
import com.moses.miiread.help.ReadBookControl;
import com.moses.miiread.utils.ColorUtil;
import com.moses.miiread.utils.theme.ATH;
import com.moses.miiread.utils.theme.MaterialValueHelper;
import com.moses.miiread.utils.theme.ThemeStore;
import com.moses.miiread.view.fragment.BookmarkFragment;
import com.moses.miiread.view.fragment.ChapterListFragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ChapterListActivity extends BaseTabActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ReadBookControl readBookControl = ReadBookControl.getInstance();
    private SearchView searchView;
    private BookShelfBean bookShelf;

    public static void startThis(MBaseActivity activity, BookShelfBean bookShelf) {
        if (bookShelf.getChapterList().size() == 0) return;
        Intent intent = new Intent(activity, ChapterListActivity.class);
        String key = String.valueOf(System.currentTimeMillis());
        intent.putExtra("data_key", key);
        try {
            BitIntentDataManager.getInstance().putData(key, bookShelf.clone());
        } catch (CloneNotSupportedException e) {
            BitIntentDataManager.getInstance().putData(key, bookShelf);
            e.printStackTrace();
        }
        activity.startActivity(intent);
    }

    @Override
    protected IPresenter initInjector() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setOrientation(readBookControl.getScreenDirection());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (bookShelf != null) {
            String key = String.valueOf(System.currentTimeMillis());
            getIntent().putExtra("data_key", key);
            BitIntentDataManager.getInstance().putData(key, bookShelf);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreateActivity() {
        getWindow().getDecorView().setBackgroundColor(ThemeStore.backgroundColor(this));
        setContentView(R.layout.activity_chapterlist);
        ButterKnife.bind(this);
        setupActionBar();
        mTlIndicator.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorControlActivated));
        mTlIndicator.setTabTextColors(ColorUtil.isColorLight(ThemeStore.primaryColor(this)) ? Color.BLACK : Color.WHITE,
                getResources().getColor(R.color.colorControlActivated));
    }

    @Override
    protected void initData() {
        String key = getIntent().getStringExtra("data_key");
        bookShelf = (BookShelfBean) BitIntentDataManager.getInstance().getData(key);
        BitIntentDataManager.getInstance().cleanData(key);
    }

    /**************abstract***********/
    @Override
    protected List<Fragment> createTabFragments() {
        return new ArrayList<>();
    }

    private List<Fragment> createTabFragmentsLazy() {
        ChapterListFragment chapterListFragment = new ChapterListFragment();
        BookmarkFragment bookmarkFragment = new BookmarkFragment();
        return Arrays.asList(chapterListFragment, bookmarkFragment);
    }

    @Override
    protected List<String> createTabTitles() {
        return new ArrayList<>();
    }

    private List<String> createTabTitlesLazy() {
        return Arrays.asList(getString(R.string.chapter_list), getString(R.string.bookmark));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_view, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        searchView = (SearchView) search.getActionView();
        ATH.setTint(searchView, MaterialValueHelper.getPrimaryTextColor(this, ColorUtil.isColorLight(ThemeStore.primaryColor(this))));
        searchView.setMaxWidth(getResources().getDisplayMetrics().widthPixels);
        searchView.onActionViewCollapsed();
        searchView.setOnCloseListener(() -> {
            mTlIndicator.setVisibility(VISIBLE);
            return false;
        });
        searchView.setOnSearchClickListener(view -> mTlIndicator.setVisibility(GONE));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((BookmarkFragment) mFragmentList.get(1)).startSearch(newText);
                ((ChapterListFragment) mFragmentList.get(0)).startSearch(newText);
                return false;
            }
        });
        setUpTabLayoutLazy(createTabFragmentsLazy(), createTabTitlesLazy());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mTlIndicator.getVisibility() != VISIBLE) {
            searchViewCollapsed();
        }
        finish();
    }

    //设置ToolBar
    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public BookShelfBean getBookShelf() {
        return bookShelf;
    }

    public void searchViewCollapsed() {
        searchView.onActionViewCollapsed();
        mTlIndicator.setVisibility(VISIBLE);
    }

}
