package com.xd.commander.aku;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.translate.demo.Md5;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.tauth.Tencent;
import com.xd.commander.aku.adapter.QuickAdapter;
import com.xd.commander.aku.bean.Items;
import com.xd.commander.aku.bean.Trans;
import com.xd.commander.aku.utils.Constants;
import com.xd.commander.aku.utils.IsInternet;
import com.xw.repo.VectorCompatTextView;

import org.angmarch.views.NiceSpinner;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.okhttp3.Call;
import cn.bmob.v3.okhttp3.Callback;
import cn.bmob.v3.okhttp3.FormBody;
import cn.bmob.v3.okhttp3.OkHttpClient;
import cn.bmob.v3.okhttp3.Request;
import cn.bmob.v3.okhttp3.RequestBody;
import cn.bmob.v3.okhttp3.Response;
import cn.bmob.v3.update.BmobUpdateAgent;
import es.dmoral.toasty.Toasty;
import im.delight.android.webview.AdvancedWebView;
import test.jinesh.sync.OnSyncListner;
/**
 * @author Administrator
 */

public class MainActivity extends AppCompatActivity implements OnSyncListner {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @BindView(R.id.rv_project)
    RecyclerView rvProject;
    BaseQuickAdapter<Items, BaseViewHolder> b;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private int count;
    private BmobQuery<Items> query;
    private NiceSpinner niceSpinner;
    private NiceSpinner niceSpinner2;
    private Boolean isNet = true;
    private ProgressBar progressBar;
    private String [] sort = {"最后更新","名称","星标"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
        Bmob.initialize(this, Constants.BMOB_);
        BmobUpdateAgent.initAppVersion();
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        BmobUpdateAgent.update(this);
        BezierRadarHeader mRefreshHeader = findViewById(R.id.header);
        mRefreshHeader.setEnableHorizontalDrag(true);
        onSync(IsInternet.isInternetCanDo(this));
        toolbar.setSubtitle("Androi开发人员的工具，库和应用程序");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_launch);
        query = new BmobQuery<>();
        query.setLimit(5).addQueryKeys("Tag,Name,Author,Time,Desc,Id").order("-Id");
        rvProject.setLayoutManager(new LinearLayoutManager(this));
        b = new QuickAdapter(R.layout.rv_item, null);
        rvProject.setAdapter(b);
        b.addHeaderView(getHeaderView());
        b.openLoadAnimation();
        getData();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData();

            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                newData();

            }
        });
    }

    void getSort(String s) {
        niceSpinner2.setSelectedIndex(0);
        query.order("-Id").setLimit(5).setSkip(0).addWhereEqualTo("Tag", s).findObjects(new FindListener<Items>() {
            @Override
            public void done(List<Items> list, BmobException e) {
                b.setNewData(list);
            }
        });
    }

    void getData() {
        if (isNet) {
            niceSpinner2.setSelectedIndex(0);
            niceSpinner.setSelectedIndex(0);
            query.setSkip(0).order("-Id").addWhereExists("Tag").findObjects(new FindListener<Items>() {
                @Override
                public void done(List<Items> list, BmobException e) {
                    b.setNewData(list);
                    b.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            Items i = (Items) adapter.getData().get(position);
                            TextView textView = view.findViewById(R.id.tag);
                            AdvancedWebView advancedWebView = (AdvancedWebView) view.findViewById(R.id.desc);
                            Intent intent = new Intent();
                            intent.putExtra("Id", i.Id);
                            intent.putExtra("Author",i.Author);
                            intent.putExtra("name", i.Name);
                            intent.putExtra("tag", i.Tag);
                            intent.putExtra("desc",i.Desc);
                            intent.setClass(MainActivity.this, ProjectActivity.class);
                            startActivity(intent,
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, Pair.create((View)advancedWebView,"shareAnim2"),Pair.create((View)textView,"shareAnim")).toBundle());
                        }
                    });
                    b.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                            AdvancedWebView h = view.findViewById(R.id.desc);
                            Items items = (Items) adapter.getData().get(position);
                            try {
                                trans(items.Desc, h);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            return true;
                        }
                    });
                    b.loadMoreComplete();
                    count = 5;
                    refreshLayout.finishRefresh();
                    if(progressBar.getVisibility()==View.GONE) {
                        toast();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            b.setEmptyView(R.layout.rv_empty, (ViewGroup) rvProject.getParent());
            b.loadMoreComplete();
            refreshLayout.finishRefresh();
        }
    }

    void newData() {
        if (isNet) {
            query.setSkip(count).findObjects(new FindListener<Items>() {
                @Override
                public void done(List<Items> list, BmobException e) {
                    b.addData(list);
                }
            });
            count += 5;
            refreshLayout.finishLoadmore();
        }
    }

    void trans(String x, final AdvancedWebView v) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("q", x)
                .add("from", "en")
                .add("to", "zh")
                .add("appid", Constants.APP_ID)
                .add("salt", "1435660288")
                .add("sign", Md5.md5(Constants.APP_ID + x + 1435660288 + Constants.SECURITY_KEY))
                .build();
        Request request = new Request.Builder()
                .url(Constants.TRANS_URL)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            v.loadHtml(new Gson().fromJson(response.body().string(), Trans.class).getTrans_result().get(0).getDst());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    void toast() {
        Toasty.success(this, "刷新成功！", Toast.LENGTH_SHORT, true).show();
    }

    private View getHeaderView() {
        View view = getLayoutInflater().inflate(R.layout.rv_head, (ViewGroup) rvProject.getParent(), false);
        niceSpinner = view.findViewById(R.id.nice_spinner);
        niceSpinner2 = view.findViewById(R.id.nice_spinner2);
        progressBar = view.findViewById(R.id.loader);
        niceSpinner.attachDataSource(new LinkedList<>(Arrays.asList(Constants.TAGS)));
        niceSpinner2.attachDataSource(new LinkedList<>(Arrays.asList(sort)));
        niceSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    getData();
                } else {
                    getSort(Constants.TAGS[position]);
                }
            }
        });
        niceSpinner2.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                query.order("-"+Constants.select[position]).findObjects(new FindListener<Items>() {
                    @Override
                    public void done(List<Items> list, BmobException e) {
                        b.setNewData(list);
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onSync(boolean isDataAvailable) {
        isNet = isDataAvailable;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            showCustomizeDialog();

        }
        return super.onOptionsItemSelected(item);
    }
    private void showCustomizeDialog() {
        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(MainActivity.this);
        final View dialogView = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.about,null);
        VectorCompatTextView button1 = dialogView.findViewById(R.id.github);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri contentUrl = Uri.parse("https://github.com/xdjm");
                intent.setData(contentUrl);
                startActivity(intent);
            }
        });
        VectorCompatTextView button2 = dialogView.findViewById(R.id.qqqun);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Tencent tencent =  Tencent.createInstance("1106694332", MainActivity.this);
                tencent.joinQQGroup(MainActivity.this,"hLArcXsMILHeQCsskpDJvMIojQnz_mPG");
            }
        });
        VectorCompatTextView button3 = dialogView.findViewById(R.id.qq);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="mqqwpa://im/chat?chat_type=wpa&uin=727787745";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
        VectorCompatTextView button4 = dialogView.findViewById(R.id.update);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUpdateAgent.forceUpdate(MainActivity.this);
            }
        });
        customizeDialog.setView(dialogView);
        customizeDialog.show();
    }
}

