package com.xd.commander.aku;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.r0adkll.slidr.Slidr;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.Tencent;
import com.xd.commander.aku.bean.Author;
import com.xd.commander.aku.bean.Project;
import com.xd.commander.aku.utils.Constants;
import com.xd.commander.aku.utils.MyIUiListener;
import com.xw.repo.VectorCompatTextView;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.okhttp3.Call;
import cn.bmob.v3.okhttp3.Callback;
import cn.bmob.v3.okhttp3.OkHttpClient;
import cn.bmob.v3.okhttp3.Request;
import cn.bmob.v3.okhttp3.Response;
import im.delight.android.webview.AdvancedWebView;

import static com.xd.commander.aku.utils.Constants.css;

/**
 * @author Administrator 0:19 2018/1/18
 */
public class ProjectActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @BindView(R.id.tag)
    TextView tag;
    @BindView(R.id.repo)
    VectorCompatTextView repo;
    @BindView(R.id.time)
    VectorCompatTextView time;
    @BindView(R.id.description)
    AdvancedWebView description;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.desc)
    AdvancedWebView desc;
    @BindView(R.id.star)
    VectorCompatTextView star;
    @BindView(R.id.watch)
    VectorCompatTextView watch;
    @BindView(R.id.fork)
    VectorCompatTextView fork;
    @BindView(R.id.issue)
    VectorCompatTextView issue;
    @BindView(R.id.user_avatar_bg)
    ImageView userAvatarBg;
    @BindView(R.id.loader)
    ProgressBar loader;
    @BindView(R.id.language)
    TextView language;
    @BindView(R.id.version)
    TextView version;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Tencent mTencent;
    private Bundle params;
    private Intent intent;
    private String link;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_main);
        ButterKnife.bind(this);
        Bmob.initialize(this, Constants.BMOB_);
        mTencent = Tencent.createInstance("1106694332", getApplicationContext());
        setTransparentStatusBar();
        int primary = ContextCompat.getColor(this, android.R.color.transparent);
        int secondary = ContextCompat.getColor(this, android.R.color.white);
        Slidr.attach(this, primary, secondary);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
       intent = getIntent();
        toolbarLayout.setTitle(intent.getStringExtra("name"));
        toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright));
        toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright));
        desc.setBackgroundColor(0);
        desc.getSettings().setTextZoom(250);
        desc.getSettings().setUseWideViewPort(true);
        desc.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        desc.getSettings().setLoadWithOverviewMode(true);
        desc.loadHtml("<style>* {color:#fff;} </style>" + intent.getStringExtra("desc"));
        tag.setText(intent.getStringExtra("tag"));
        description.setBackgroundColor(0);
        description.getSettings().setTextZoom(250);
        description.getSettings().setUseWideViewPort(true);
        description.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        description.getSettings().setLoadWithOverviewMode(true);
        description.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });
        try {
            getInfo(intent.getStringExtra("Author"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        getData(intent.getIntExtra("Id", 1));

    }

    void getInfo(String x) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/users/" + x)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final Author author = new Gson().fromJson(response.body().string(), Author.class);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getApplicationContext()).load(author.getAvatar_url()).into(userAvatarBg);
                    }
                });

            }
        });
    }

    void getData(int i) {
        BmobQuery<Project> query = new BmobQuery<>();
        query.addWhereEqualTo("Id", i).findObjects(new FindListener<Project>() {
            @Override
            public void done(List<Project> list, BmobException e) {
                repo.setText(list.get(0).getOwner() + "/" + list.get(0).getRepo());
                time.setText(list.get(0).getUpdated());
                description.loadHtml(css + list.get(0).getDescription().replace("data-layzr", "src"));
                language.setText(list.get(0).getLanguage());
                version.setText(list.get(0).getVersion());
                star.setText(String.valueOf(list.get(0).getStargazers()));
                watch.setText(String.valueOf(list.get(0).getWatchers()));
                fork.setText(String.valueOf(list.get(0).getForks()));
                issue.setText(String.valueOf(list.get(0).getIssues()));
                link = list.get(0).getLink();
                loader.setVisibility(View.GONE);
            }
        });
    }

    protected void setTransparentStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void shareToQQ() {
        params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, intent.getStringExtra("name"));
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, Html.fromHtml(intent.getStringExtra("desc")).toString());
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, link);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://bmob-cdn-16355.b0.upaiyun.com/2018/02/03/a9a556d940a673e88041b8c44c5590b3.png");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "AKu");
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                mTencent.shareToQQ(ProjectActivity.this, params, new MyIUiListener());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        description.onActivityResult(requestCode, resultCode, intent);
        Tencent.onActivityResultData(requestCode, resultCode, data, new MyIUiListener());
        if (requestCode == 0) {
                Tencent.handleResultData(data, new MyIUiListener());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.project_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        description.onDestroy();
        super.onDestroy();
        Glide.with(getApplicationContext()).onDestroy();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            shareToQQ();
        }
        else {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        description.onResume();
    }

    @Override
    protected void onPause() {
        description.onPause();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (!description.onBackPressed())
        { return; }
        super.onBackPressed();
    }
}
