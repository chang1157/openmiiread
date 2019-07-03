//Copyright (c) 2017. origin. All rights reserved.
package com.moses.miiread.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hwangjr.rxbus.RxBus;
import com.kunfei.basemvplib.AppActivityManager;
import com.moses.miiread.BitIntentDataManager;
import com.moses.miiread.R;
import com.moses.miiread.base.MBaseActivity;
import com.moses.miiread.bean.BookInfoBean;
import com.moses.miiread.bean.BookShelfBean;
import com.moses.miiread.bean.SearchBookBean;
import com.moses.miiread.constant.RxBusTag;
import com.moses.miiread.help.BlurTransformation;
import com.moses.miiread.presenter.BookDetailPresenter;
import com.moses.miiread.presenter.ReadBookPresenter;
import com.moses.miiread.presenter.contract.BookDetailContract;
import com.moses.miiread.widget.CoverImageView;
import com.moses.miiread.widget.modialog.MoDialogHUD;
import com.victor.loading.rotate.RotateLoading;

import java.io.File;
import java.util.Objects;

import static com.moses.miiread.presenter.BookDetailPresenter.FROM_BOOKSHELF;
import static com.moses.miiread.presenter.BookDetailPresenter.FROM_SEARCH;

public class BookDetailActivity extends MBaseActivity<BookDetailContract.Presenter> implements BookDetailContract.View {
    @BindView(R.id.content_ll)
    View vwContent;
    @BindView(R.id.ifl_content)
    View bottomContent;
    @BindView(R.id.iv_blur_cover)
    AppCompatImageView ivBlurCover;
    @BindView(R.id.book_cover)
    CoverImageView ivCover;
    @BindView(R.id.book_name)
    TextView tvName;
    @BindView(R.id.author_name)
    TextView tvAuthor;
    @BindView(R.id.book_source)
    TextView tvOrigin;
    @BindView(R.id.right_btn)
    TextView tvRead;
    @BindView(R.id.left_btn)
    TextView tvShelf;
    @BindView(R.id.rl_loading)
    RotateLoading loadingV;
    @BindView(R.id.change_origin)
    TextView tvChangeOrigin;
    @BindView(R.id.book_enable_update)
    TextView tvEnableUpdate;
    @BindView(R.id.book_enable_update_check)
    CheckBox enableUpdateCheck;
    @BindView(R.id.book_delete)
    TextView tvBookDelete;
    @BindView(R.id.book_reload)
    TextView tvBookReload;
    @BindView(R.id.book_edit_info)
    TextView tvBookEditInfo;
    @BindView(R.id.start_read)
    TextView startRead;

    private MoDialogHUD moDialogHUD;
    private String author;
    private BookShelfBean bookShelfBean;
    private String coverPath;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (bottomVelTracker == null)
            bottomVelTracker = VelocityTracker.obtain();
        else bottomVelTracker.clear();
    }

    @Override
    protected BookDetailContract.Presenter initInjector() {
        return new BookDetailPresenter();
    }

    @Override
    protected void onCreateActivity() {
        setTheme(R.style.CAppTransparentTheme);
        setContentView(R.layout.activity_book_detail);
    }

    @Override
    protected void initData() {
        mPresenter.initData(getIntent());
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    @Override
    protected void bindView() {
        ButterKnife.bind(this);
        //弹窗
        moDialogHUD = new MoDialogHUD(this);
        if (mPresenter.getOpenFrom() == FROM_SEARCH) {
            if (mPresenter.getSearchBook() == null) return;
            SearchBookBean searchBookBean = mPresenter.getSearchBook();
            upImageView(searchBookBean.getCoverUrl());
            tvName.setText(searchBookBean.getName());
            author = searchBookBean.getAuthor();
            tvAuthor.setText(TextUtils.isEmpty(author) ? "未知" : author);
            String origin = TextUtils.isEmpty(searchBookBean.getOrigin()) ? "未知" : searchBookBean.getOrigin();
            tvOrigin.setText(origin);
            tvShelf.setText(R.string.add_to_shelf);
            tvRead.setText(R.string.start_read);
            tvRead.setOnClickListener(v -> {
                //放入书架
            });
        }
        updateView();
        calBottomView();
    }

    @Override
    public void updateView() {
        bookShelfBean = mPresenter.getBookShelf();
        BookInfoBean bookInfoBean;
        if (null != bookShelfBean) {
            bookInfoBean = bookShelfBean.getBookInfoBean();
            tvName.setText(bookInfoBean.getName());
            author = bookInfoBean.getAuthor();
            tvAuthor.setText(TextUtils.isEmpty(author) ? "未知" : author);
            if (mPresenter.getInBookShelf()) {
                tvShelf.setText(R.string.remove_from_bookshelf);
                tvRead.setText(R.string.continue_read);
                tvShelf.setOnClickListener(v -> {
                    //从书架移出
                    mPresenter.removeFromBookShelf();
                });
            } else {
                tvShelf.setText(R.string.add_to_shelf);
                tvRead.setText(R.string.start_read);
                tvShelf.setOnClickListener(v -> {
                    //放入书架
                    mPresenter.addToBookShelf();
                });
            }

            String origin = bookInfoBean.getOrigin();
            if (!TextUtils.isEmpty(origin)) {
                tvOrigin.setText(origin);
            } else {
                tvOrigin.setVisibility(View.INVISIBLE);
            }
            if (!TextUtils.isEmpty(bookShelfBean.getCustomCoverPath())) {
                upImageView(bookShelfBean.getCustomCoverPath());
            } else {
                upImageView(bookInfoBean.getCoverUrl());
            }
            upChapterSizeTv();
        }
        loadingV.setVisibility(View.GONE);
        loadingV.stop();
        loadingV.setOnClickListener(null);

        if (mPresenter.getBookShelf().getAllowUpdate()) {
            enableUpdateCheck.setChecked(true);
            tvEnableUpdate.setText(getResources().getString(R.string.allow_update));
        } else {
            enableUpdateCheck.setChecked(false);
            tvEnableUpdate.setText(getResources().getString(R.string.disable_update));
        }

        if (getIntent().getIntExtra("openFrom", FROM_BOOKSHELF) == FROM_BOOKSHELF) {
            tvEnableUpdate.setVisibility(View.VISIBLE);
            tvBookDelete.setVisibility(View.VISIBLE);
            enableUpdateCheck.setVisibility(View.VISIBLE);
            tvRead.setVisibility(View.GONE);
            tvShelf.setVisibility(View.GONE);
            startRead.setVisibility(View.VISIBLE);
        } else {
            tvEnableUpdate.setVisibility(View.GONE);
            tvBookDelete.setVisibility(View.GONE);
            enableUpdateCheck.setVisibility(View.GONE);
            tvRead.setVisibility(View.VISIBLE);
            tvShelf.setVisibility(View.VISIBLE);
            startRead.setVisibility(View.GONE);
        }
    }

    @Override
    public void getBookShelfError() {
        loadingV.setVisibility(View.VISIBLE);
        loadingV.start();
        loadingV.setOnClickListener(v -> {
            loadingV.setOnClickListener(null);
            mPresenter.getBookShelfInfo();
        });
    }

    private void upImageView(String path) {
        if (TextUtils.isEmpty(path)) return;
        if (Objects.equals(coverPath, path)) return;
        if (this.isFinishing()) return;
        coverPath = path;
        if (coverPath.startsWith("http")) {
            Glide.with(this).load(coverPath)
                    .apply(new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESOURCE).centerCrop()
                            .placeholder(R.drawable.img_cover_default))
                    .into(ivCover);
            Glide.with(this).load(coverPath)
                    .apply(new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESOURCE).centerCrop()
                            .placeholder(R.drawable.img_cover_gs))
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(this, 25)))
                    .into(ivBlurCover);
        } else {
            File file = new File(coverPath);
            Glide.with(this).load(file)
                    .apply(new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESOURCE).centerCrop()
                            .placeholder(R.drawable.img_cover_default))
                    .into(ivCover);
            Glide.with(this).load(file)
                    .apply(new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESOURCE).centerCrop()
                            .placeholder(R.drawable.img_cover_gs))
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(this, 25)))
                    .into(ivBlurCover);
        }
    }

    private void refresh() {
        loadingV.setVisibility(View.VISIBLE);
        loadingV.start();
        loadingV.setOnClickListener(null);
        mPresenter.getBookShelfInfo();
    }

    @Override
    protected void firstRequest() {
        super.firstRequest();
        if (mPresenter.getOpenFrom() == BookDetailPresenter.FROM_SEARCH) {
            //网络请求
            mPresenter.getBookShelfInfo();
        }
    }

    @SuppressLint("DefaultLocale")
    private void upChapterSizeTv() {
//        String chapterSize = "";
//        if (mPresenter.getOpenFrom() == FROM_BOOKSHELF && bookShelfBean.getChapterListSize() > 0) {
//            int newChapterNum = bookShelfBean.getChapterListSize() - 1 - bookShelfBean.getDurChapter();
//            if (newChapterNum > 0)
//                chapterSize = String.format("(+%d)", newChapterNum);
//        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bindEvent() {
        ivBlurCover.setOnClickListener(null);
        vwContent.setOnClickListener(v -> bottomOut());

        tvChangeOrigin.setOnClickListener(view -> moDialogHUD.showChangeSource(mPresenter.getBookShelf(),
                searchBookBean -> {
                    tvOrigin.setText(searchBookBean.getOrigin());
                    loadingV.setVisibility(View.VISIBLE);
                    loadingV.start();
                    loadingV.setOnClickListener(null);
                    if (mPresenter.getOpenFrom() == FROM_BOOKSHELF) {
                        mPresenter.changeBookSource(searchBookBean);
                    } else {
                        mPresenter.initBookFormSearch(searchBookBean);
                        mPresenter.getBookShelfInfo();
                    }
                }));

        View.OnClickListener readClickAction = v -> {
            //进入阅读
            Intent intent = new Intent(BookDetailActivity.this, ReadBookActivity.class);
            intent.putExtra("openFrom", ReadBookPresenter.OPEN_FROM_APP);
            String key = String.valueOf(System.currentTimeMillis());
            intent.putExtra("data_key", key);
            try {
                BitIntentDataManager.getInstance().putData(key, mPresenter.getBookShelf().clone());
            } catch (CloneNotSupportedException e) {
                BitIntentDataManager.getInstance().putData(key, mPresenter.getBookShelf());
                e.printStackTrace();
            }
            startActivityByAnim(intent, android.R.anim.fade_in, android.R.anim.fade_out);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (getStart_share_ele()) {
                    finishAfterTransition();
                } else {
                    finish();
                    overridePendingTransition(0, android.R.anim.fade_out);
                }
            } else {
                finish();
                overridePendingTransition(0, android.R.anim.fade_out);
            }
        };
        tvRead.setOnClickListener(readClickAction);
        startRead.setOnClickListener(readClickAction);

        tvBookReload.setOnClickListener(v -> refresh());

        tvBookEditInfo.setOnClickListener(v -> jumpBookInfoEdit());

        enableUpdateCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mPresenter.getBookShelf().setAllowUpdate(isChecked);
            if (mPresenter.getInBookShelf())
                mPresenter.addToBookShelf();
        });

        tvEnableUpdate.setOnClickListener(v -> enableUpdateCheck.setChecked(!enableUpdateCheck.isChecked()));

        tvAuthor.setOnClickListener(view -> {
            if (TextUtils.isEmpty(author)) return;
            if (!AppActivityManager.getInstance().isExist(SearchBookActivity.class)) {
                SearchBookActivity.startByKey(this, author);
            } else {
                RxBus.get().post(RxBusTag.SEARCH_BOOK, author);
            }
            finish();
        });

        tvBookDelete.setOnClickListener(v -> {
            //放入书架
            Dialog[] dialog = new Dialog[1];
            View pop = LayoutInflater.from(this).inflate(R.layout.dialog_normal_confirm, null, false);
            TextView title = pop.findViewById(R.id.title);
            TextView msg = pop.findViewById(R.id.msg_tv);
            TextView cancel = pop.findViewById(R.id.cancel);
            TextView confirm = pop.findViewById(R.id.confirm);
            title.setText(R.string.remove_from_bookshelf);
            msg.setText(R.string.sure_remove_from_bookshelf);
            cancel.setText(R.string.no);
            confirm.setText(R.string.yes);

            cancel.setOnClickListener(view -> dialog[0].dismiss());
            confirm.setOnClickListener(view -> {
                mPresenter.removeFromBookShelf();
                dialog[0].dismiss();
                bottomOut();
            });
            dialog[0] = new AlertDialog.Builder(this, R.style.alertDialogTheme)
                    .setView(pop)
                    .show();
        });
    }

    private void jumpBookInfoEdit() {
        if (mPresenter.getOpenFrom() == FROM_BOOKSHELF)
            BookInfoEditActivity.startThis(this, mPresenter.getBookShelf().getNoteUrl());
        else {
            Intent intent = new Intent(BookDetailActivity.this, BookInfoEditActivity.class);
            intent.putExtra("searchBook", mPresenter.getSearchBook());
            startActivity(intent);
        }
        handler.postDelayed(this::bottomOut, 400);
    }

    @Override
    public void onDestroy() {
        moDialogHUD.dismiss();
        bottomVelTracker.recycle();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        bottomOut();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void calBottomView() {
        ViewTreeObserver vto = bottomContent.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bottomContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                bottomViewHeight = bottomContent.getMeasuredHeight();
                bottomContent.setTranslationY(bottomViewHeight);
                translateY = bottomViewHeight;
                //
                bottomIn();
            }
        });

        bottomContent.setOnTouchListener(new View.OnTouchListener() {
            float lastDownY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bottomVelTracker.addMovement(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastDownY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        bottomVelTracker.computeCurrentVelocity(1000);
                        float y = event.getY();
                        if (translateY + y - lastDownY < 0)
                            translateY = 0;
                        else if (translateY + y - lastDownY > bottomViewHeight)
                            translateY = bottomViewHeight;
                        else
                            translateY += (y - lastDownY);
                        bottomContent.setTranslationY(translateY);
                        break;
                    case MotionEvent.ACTION_UP:
                        lastDownY = 0;
                        bottomVelTracker.computeCurrentVelocity(1000);
                        if (bottomVelTracker.getYVelocity() > 0) {
                            if (bottomVelTracker.getYVelocity() > 200)
                                bottomOut();
                            else if (translateY > 0.2f * bottomViewHeight)
                                bottomOut();
                            else bottomIn();
                        } else {
                            if (bottomVelTracker.getYVelocity() < 200)
                                bottomIn();
                            else if (translateY < 0.8f * bottomViewHeight)
                                bottomIn();
                            else bottomOut();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void bottomIn() {
        ValueAnimator animator = ValueAnimator.ofFloat(translateY, 0);
        animator.addUpdateListener(animation -> {
            translateY = (float) animation.getAnimatedValue();
            bottomContent.setTranslationY(translateY);
        });
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    private void bottomOut() {
        ValueAnimator animator = ValueAnimator.ofFloat(translateY, bottomViewHeight);
        animator.addUpdateListener(animation -> {
            translateY = (float) animation.getAnimatedValue();
            bottomContent.setTranslationY(translateY);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (getStart_share_ele()) {
                        finishAfterTransition();
                    } else {
                        finish();
                        overridePendingTransition(0, 0);
                    }
                }
            }
        });
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    private VelocityTracker bottomVelTracker;
    private float translateY;
    private int bottomViewHeight;
}