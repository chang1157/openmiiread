package com.moses.miiread.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kunfei.basemvplib.impl.IPresenter;
import com.moses.miiread.R;
import com.moses.miiread.base.MBaseActivity;
import com.moses.miiread.bean.BookSourceBean;
import com.moses.miiread.bean.FindKindBean;
import com.moses.miiread.model.BookSourceManager;
import com.moses.miiread.presenter.ChoiceBookPresenter;
import com.moses.miiread.utils.RxUtils;
import com.moses.miiread.utils.ScreenUtils;
import com.moses.miiread.utils.StringUtils;
import com.moses.miiread.utils.theme.ThemeStore;
import com.moses.miiread.view.adapter.ChoiceBookPagerAdapter;
import com.moses.miiread.view.adapter.ChoiceCatoTagAdapter;
import com.moses.miiread.view.adapter.base.OnItemClickListenerTwo;
import com.moses.miiread.view.fragment.ChoiceBookPagerFragment;
import com.moses.miiread.widget.MenuButton;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChoiceCatoActivity extends MBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.v_pager)
    ViewPager vPager;
    @BindView(R.id.tab_strip)
    PagerTabStrip tabStrip;
    @BindView(R.id.cato_lv)
    RecyclerView cato_lv;
    @BindView(R.id.cato_lv_parent)
    View cato_lv_parent;
    @BindView(R.id.closeV)
    View closeV;
    @BindView(R.id.menu_btn)
    MenuButton menuBtn;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    private Disposable disposable;

    private BookSourceBean sourceBean;
    protected ChoiceBookPagerAdapter bookPagerAdapter;

    //
    protected ChoiceCatoTagAdapter tagAdapter;
    private AtomicBoolean isTagLvOpen = new AtomicBoolean(false);
    private ValueAnimator openTagAnimator;
    private ValueAnimator closeTagAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String sourceUrl = getIntent().getStringExtra("sourceUrl");
        sourceBean = BookSourceManager.getBookSourceByUrl(sourceUrl);
        getKindData();
    }

    @Override
    protected IPresenter initInjector() {
        return null;
    }

    @Override
    protected void onCreateActivity() {
        getWindow().getDecorView().setBackgroundColor(ThemeStore.backgroundColor(this));
        setContentView(R.layout.activity_choice_cato);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void bindView() {
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        setupActionBar();
        tabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorControlActivated));
        tabStrip.setTextColor(getResources().getColor(R.color.colorControlActivated));
        cato_lv.setLayoutManager(new GridLayoutManager(getContext(), 4));

    }

    //设置ToolBar
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbarTitle.setText(StringUtils.fixSourceDisplayName(getIntent().getStringExtra("sourceName")));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    //菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }

    @Override
    protected void bindEvent() {
        super.bindEvent();
        closeV.setOnClickListener(v -> {
            toggleTags(false);
            menuBtn.close();
        });
        menuBtn.setOnMenuButtonClickListener(new MenuButton.OnMenuButtonClickListener() {
            @Override
            public void onOpen() {
                toggleTags(true);
            }

            @Override
            public void onClose() {
                toggleTags(false);
            }
        });
    }

    private void getKindData() {
        if (disposable != null) return;
        Single.create((SingleOnSubscribe<List<FindKindBean>>) e -> {
            List<FindKindBean> kindBeans = new ArrayList<>();
            try {
                if (!TextUtils.isEmpty(sourceBean.getRuleFindUrl())) {
                    String[] kindA = sourceBean.getRuleFindUrl().split("(&&|\n)+");
                    for (String kindB : kindA) {
                        if (kindB.trim().isEmpty()) continue;
                        String[] kind = kindB.split("::");
                        FindKindBean findKindBean = new FindKindBean();
                        findKindBean.setGroup(sourceBean.getBookSourceName());
                        findKindBean.setTag(sourceBean.getBookSourceUrl());
                        findKindBean.setKindName(kind[0]);
                        findKindBean.setKindUrl(kind[1]);
                        kindBeans.add(findKindBean);
                    }
                    e.onSuccess(kindBeans);
                }
            } catch (Exception exception) {
                sourceBean.addGroup("发现规则语法错误");
                BookSourceManager.addBookSource(sourceBean);
            }
        })
                .compose(RxUtils::toSimpleSingle)
                .subscribe(new SingleObserver<List<FindKindBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onSuccess(List<FindKindBean> kindBeans) {
                        updateUI(kindBeans);
                        disposable.dispose();
                        disposable = null;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        disposable.dispose();
                        disposable = null;
                    }
                });
    }

    private void refreshChildOnCreate(int curPosi) {
        List<Integer> descIndexs = new ArrayList<>();
        descIndexs.add(curPosi);
        if (bookPagerAdapter.getCount() > 1) {
            if (curPosi == 0)
                descIndexs.add(curPosi + 1);
            else if (curPosi == bookPagerAdapter.getCount() - 1)
                descIndexs.add(curPosi - 1);
            else {
                descIndexs.add(curPosi + 1);
                descIndexs.add(curPosi - 1);
            }
        }
        for (Integer descIndex : descIndexs) {
            ChoiceBookPagerFragment fragment = (ChoiceBookPagerFragment) bookPagerAdapter.getItem(descIndex);
            ChoiceBookPresenter presenter = fragment.getPresenter();
            if(presenter == null)
                continue;
            if (!presenter.isFreshedOnCreated())
                presenter.refreshOnCreate();
        }
    }

    private void updateUI(List<FindKindBean> kindBeans) {
        //viewpager
        List<ChoiceBookPagerFragment> fragments = new ArrayList<>();
        for (FindKindBean kindBean : kindBeans) {
            ChoiceBookPagerFragment fragment = new ChoiceBookPagerFragment(kindBean.getKindUrl(), kindBean.getKindName(), kindBean.getTag());
            fragments.add(fragment);
        }
        bookPagerAdapter = new ChoiceBookPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragments);
        vPager.setAdapter(bookPagerAdapter);
        vPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (tagAdapter != null) {
                    tagAdapter.setCurSelectedIndex(position);
                    cato_lv.scrollToPosition(position);
                    if (isTagLvOpen.get())
                        menuBtn.close();
                }
                refreshChildOnCreate(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vPager.setOffscreenPageLimit(kindBeans.size());
        refreshChildOnCreate(0);

        //cato list view on top
        tagAdapter = new ChoiceCatoTagAdapter(this, kindBeans).setOnItemClickListenerTwo(new OnItemClickListenerTwo() {
            @Override
            public void onClick(View view, int index) {
                vPager.setCurrentItem(index);
                toggleTags(false);
            }

            @Override
            public void onLongClick(View view, int index) {

            }
        });
        cato_lv.setAdapter(tagAdapter);
        ViewTreeObserver vto = cato_lv_parent.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                cato_lv_parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                cato_lv_parent.setVisibility(View.GONE);
            }
        });
    }

    private void toggleTags(boolean open) {
        if ((openTagAnimator != null && openTagAnimator.isRunning()) || (closeTagAnimator != null && closeTagAnimator.isRunning()))
            return;
        if (open) {
            ViewTreeObserver clvCato = cato_lv.getViewTreeObserver();
            clvCato.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    cato_lv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    fixCatoLVHeight();
                    openTagAnimator = ObjectAnimator.ofFloat(cato_lv_parent, View.TRANSLATION_Y, -cato_lv_parent.getHeight(), 0);
                    openTagAnimator.setDuration(400);
                    openTagAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            openTagAnimator = null;
                            isTagLvOpen.set(true);
                        }
                    });
                    openTagAnimator.setInterpolator(new DecelerateInterpolator());
                    openTagAnimator.start();
                }
            });
            cato_lv_parent.setVisibility(View.VISIBLE);
        } else {
            closeTagAnimator = ObjectAnimator.ofFloat(cato_lv_parent, View.TRANSLATION_Y, 0, -cato_lv_parent.getHeight());
            closeTagAnimator.setDuration(400);
            closeTagAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    closeTagAnimator = null;
                    cato_lv_parent.setVisibility(View.GONE);
                    isTagLvOpen.set(false);
                }
            });
            closeTagAnimator.setInterpolator((Interpolator) t -> (1 - t) * t + t * t * t);
            closeTagAnimator.start();
        }
    }

    private void fixCatoLVHeight() {
        int[] screenWH = ScreenUtils.getScreenSize(this);
        int contentH = orientation == Configuration.ORIENTATION_PORTRAIT ? Math.max(screenWH[0], screenWH[1]) : Math.min(screenWH[0], screenWH[1])
                - ScreenUtils.getStatusBarHeight() - ScreenUtils.getNavigationBarHeight();
        if (cato_lv.getMeasuredHeight() > contentH * .5f) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) cato_lv.getLayoutParams();
            lp.height = (int) (contentH * .5f);
            cato_lv.setLayoutParams(lp);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientation = newConfig.orientation;
        if (cato_lv.getMeasuredHeight() != 0)
            fixCatoLVHeight();
    }

    private int orientation = Configuration.ORIENTATION_PORTRAIT;
}