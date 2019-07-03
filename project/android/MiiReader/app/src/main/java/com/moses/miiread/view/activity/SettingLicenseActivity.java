package com.moses.miiread.view.activity;

import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kunfei.basemvplib.impl.IPresenter;
import com.moses.miiread.R;
import com.moses.miiread.base.MBaseActivity;
import com.moses.miiread.utils.theme.ThemeStore;

public class SettingLicenseActivity extends MBaseActivity {
    @BindView(R.id.webView)
    WebView vw;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String type = "service_agreement";

    @Override
    protected IPresenter initInjector() {
        return null;
    }

    @Override
    protected void onCreateActivity() {
        getWindow().getDecorView().setBackgroundColor(ThemeStore.backgroundColor(this));
        setContentView(R.layout.activity_settings_license);
    }

    @Override
    protected void initData() {
        type = getIntent().getStringExtra("type");
    }

    @Override
    protected void bindView() {
        super.bindView();
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        setupActionBar();
        WebSettings ws = vw.getSettings();
        ws.setDomStorageEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        vw.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

            }
        });
        if (type.equals("service_agreement"))
            vw.loadUrl("file:///android_asset/www/service_agreement.html");
        else
            vw.loadUrl("file:///android_asset/www/service_disclaimer.html");
    }

    @Override
    protected void bindEvent() {
        super.bindEvent();
        vw.setOnLongClickListener(v -> true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        vw.pauseTimers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        vw.resumeTimers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vw.setVisibility(View.GONE);
        vw.destroy();
    }

    //设置ToolBar
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(type.equals("service_agreement") ? R.string.service_agreement : R.string.disclaimer);
        }
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
}
