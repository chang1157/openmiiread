package com.moses.miiread.view.activity;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.moses.miiread.R;
import com.moses.miiread.base.MBaseActivity;
import com.moses.miiread.bean.SubscriptionOrderBean;
import com.moses.miiread.presenter.contract.SubscriptionContract;
import com.moses.miiread.utils.theme.ThemeStore;
import com.moses.miiread.view.adapter.SubscriptionOrderAdapter;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionActivity extends MBaseActivity<SubscriptionContract.Presenter> implements SubscriptionContract.View {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static void startThis(Context context) {
        context.startActivity(new Intent(context, SubscriptionActivity.class));
    }

    @Override
    public void initImmersionBar() {
        super.initImmersionBar();
    }

    @Override
    public void dismissHUD() {

    }

    @Override
    protected SubscriptionContract.Presenter initInjector() {
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //back
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreateActivity() {
        getWindow().getDecorView().setBackgroundColor(ThemeStore.backgroundColor(this));
        setContentView(R.layout.activity_subscription_currentnone);//未登录或没有订阅
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        setupActionBar("订阅");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void bindView() {
        super.bindView();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        List<SubscriptionOrderBean> list = new ArrayList<>();
        list.add(new SubscriptionOrderBean("1个月", "", "解锁全部", true, false, 18f));
        list.get(0).setChecked(true);
        list.add(new SubscriptionOrderBean("3个月", "", "解锁全部", true, false, 48f));
        list.add(new SubscriptionOrderBean("每月", "", "", false, true, 7f, 6185, 2915));
        list.add(new SubscriptionOrderBean("每季度", "", "", false, true, 20f, 13000, 15000));
        list.add(new SubscriptionOrderBean("每年", "", "", false, true, 60f, 39000, 49000));
        list.add(new SubscriptionOrderBean("每月", "", "", false, false, 13f, 10415, 6485));
        list.add(new SubscriptionOrderBean("每季度", "", "", false, false, 38f, 26000, 27300));
        list.add(new SubscriptionOrderBean("每年", "", "", false, false, 98f, 65000, 97175));
        SubscriptionOrderAdapter adapter = new SubscriptionOrderAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }

    //设置ToolBar
    public void setupActionBar(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }
    }
}