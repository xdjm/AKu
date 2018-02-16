package com.xd.commander.aku.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.webkit.WebSettings;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xd.commander.aku.R;
import com.xd.commander.aku.bean.Items;

import java.util.List;

import im.delight.android.webview.AdvancedWebView;

/**
 * @author Administrator 22:25 2018/1/7
 */
public class QuickAdapter extends BaseQuickAdapter<Items,BaseViewHolder>{
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization TAGS.
     *
     * @param layoutResId The layout resource id of each item.
     * @param data        A new list is created out of this one to avoid mutable list
     */
    public QuickAdapter(@LayoutRes int layoutResId, @Nullable List<Items> data) {
        super(layoutResId, data);
    }

    /**
     * Implement this method and use the helper to adapt the view to the given items.
     *
     * @param helper A fully initialized helper.
     * @param items   The items that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, Items items) {
        helper.setText(R.id.name, items.Name)
                .setText(R.id.tag, items.Tag)
                .setText(R.id.time, items.Time)
                .setText(R.id.author, items.Author);
        AdvancedWebView h = helper.getView(R.id.desc);
        h.setBackgroundColor(0);
        h.getSettings().setUseWideViewPort(true);
        h.getSettings().setTextZoom(250);
        h.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        h.getSettings().setLoadWithOverviewMode(true);
        h.loadHtml(items.Desc.replace("data-layzr", "src"));
    }
}
