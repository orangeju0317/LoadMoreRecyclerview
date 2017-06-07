package com.zhangjian.loadmorerecyclerview;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

/**
 * Created by Orange on 2017/4/26.
 * <p>
 * ZjRecyclerView的通用Adapter
 */

public class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = 1;

    private ArrayList<T> mBeans;
    private Context mContext;
    private int itemId;

    private OnBindViewHolder binder;

    public BaseAdapter(ArrayList<T> mBeans, Context mContext, @LayoutRes int resource) {
        this.mBeans = mBeans;
        this.mContext = mContext;
        this.itemId = resource;
    }

    public void setBinder(OnBindViewHolder binder) {
        this.binder = binder;
    }

    ArrayList<T> getBeans() {
        return mBeans;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            default:
            case TYPE_NORMAL:
                return new ViewHolder(LayoutInflater.from(mContext).inflate(itemId, parent, false));

            case TYPE_FOOTER:
                return new FooterViewHolder(LayoutInflater.from(mContext).inflate(R.layout.view_load_more, parent, false));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof BaseAdapter.FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
            footerViewHolder.rcvLoadMore.spin();
        } else {
            ViewHolder holder = (ViewHolder) viewHolder;
            binder.bindView(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mBeans.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object bean = mBeans.get(position);
        if (position == mBeans.size() - 1 && bean == null) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    public interface OnBindViewHolder {
        void bindView(ViewHolder holder, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> mViews;  // 缓存itemView内部的子View
        private View mConvertView;

        ViewHolder(View itemView) {
            super(itemView);
            mViews = new SparseArray<>();
            mConvertView = itemView;
        }

        /**
         * 通过viewId获取控件
         *
         * @param viewId R.id.blabla
         * @return 这个view
         */
        public <T extends View> T getView(int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = mConvertView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }

        // 封装一些常用控件的简单使用
        public ViewHolder setText(int viewId, String s) {
            TextView tv = getView(viewId);
            tv.setText(s);
            return this;
        }

        public ViewHolder setText(int viewId, @StringRes int s) {
            TextView tv = getView(viewId);
            tv.setText(s);
            return this;
        }

        public ViewHolder setImageResource(int viewId, @DrawableRes int resId) {
            ImageView iv = getView(viewId);
            iv.setImageResource(resId);
            return this;
        }

        public ViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
            View view = getView(viewId);
            view.setOnClickListener(listener);
            return this;
        }
    }

    /**
     * 底部加载更多
     */
    private class FooterViewHolder extends RecyclerView.ViewHolder {
        ProgressWheel rcvLoadMore;

        FooterViewHolder(View itemView) {
            super(itemView);
            rcvLoadMore = (ProgressWheel) itemView.findViewById(R.id.rcv_load_more);
        }
    }
}
