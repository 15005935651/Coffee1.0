package com.qin.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.qin.R;
import com.qin.recevier.NetworkChangedReceiver;
import com.qin.util.ScreenUtils;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MedicineKonwledge extends AppCompatActivity {

    @BindView(R.id.medicine_webView)
    WebView medicineWebView;
    @BindView(R.id.medicine_toolbar)
    Toolbar medicineToolbar;
    private NetworkChangedReceiver mNetworkChangedReceiver;
    private Dialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_konwledge);
        ButterKnife.bind(this);
        mNetworkChangedReceiver = new NetworkChangedReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkChangedReceiver, intentFilter);
        initData();
        initDialog();
        mDialog.show();

    }

    protected void initData() {
        medicineToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MedicineKonwledge.this.finish();
            }
        });
//        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(RefreshLayout refreshLayout) {
//
//            }
//        });
//        mRefreshLayout.autoRefresh();
        medicineWebView.loadUrl("https://www.baidu.com/");

        medicineWebView.getSettings().setJavaScriptEnabled(true);
        medicineWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //     mRefreshLayout.finishRefresh();
                view.loadUrl(String.format(Locale.CHINA, "javascript:document.body.style.paddingTop='%fpx'; void 0", DensityUtil.px2dp(medicineWebView.getPaddingTop())));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                    }
                },500);
            }
        });
    }




    private void initDialog() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ScreenUtils.getWindowWidth(MedicineKonwledge.this), ScreenUtils.getWindowHeight(MedicineKonwledge.this));
        params.width = (int) (MedicineKonwledge.this.getWindowManager().getDefaultDisplay().getWidth() * 0.5f);
        params.height = (int) (MedicineKonwledge.this.getWindowManager().getDefaultDisplay().getWidth() * 0.5f);
        mDialog = new Dialog(MedicineKonwledge.this);
        View view = View.inflate(MedicineKonwledge.this, R.layout.dialog_loading, null);
        mDialog.setContentView(view);
        mDialog.setContentView(view, params);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(true);

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });


    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mNetworkChangedReceiver);
        super.onDestroy();

    }





}
