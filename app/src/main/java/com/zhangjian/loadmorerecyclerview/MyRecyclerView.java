package com.zhangjian.loadmorerecyclerview;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Orange on 2017/4/26.
 * 封装RecyclerView 使其具有上拉加载更多的功能
 */

public class MyRecyclerView extends RecyclerView {
    public static final int SLIDE_STOP = 0; // 停止
    public static final int SLIDE_DOWN = 1; // 下滑
    public static final int SLIDE_UP = 2; // 上拉
    private int scrollState = SLIDE_STOP;  // 随手势正在进行的滑动状态
    private float y;  // 记录上一次滑动的y轴坐标
    private boolean loadingMore; // 是否处于正在加载更多状态

    private boolean loadMoreEnable = true; // 是否允许加载更多

    private OnLoadMoreListener listener;

    private long delayMillis = 0; // 加载更多的延迟加载时间 默认是0ms

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        addOnScrollListener(new OnScrollListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                Log.d("zjTest", "onScrollStateChanged: " + scrollState);
                if (loadMoreEnable && scrollState == SLIDE_UP && !loadingMore && newState == RecyclerView.SCROLL_STATE_DRAGGING &&
                        ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition() + 1 == getAdapter().getItemCount()) {

                    if (!listener.onLoadBefore()) {
                        return;
                    }

                    if (getAdapter() instanceof BaseAdapter) {
                        ((BaseAdapter) getAdapter()).getBeans().add(null);  // footer标记
                    } else {
                        Toast.makeText(context, "Adapter没有继承ZjAdapter，无法上拉加载", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    getAdapter().notifyDataSetChanged();
                    loadingMore = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List list = ((BaseAdapter) getAdapter()).getBeans();
                            if (list.size() > 0 && list.get(list.size() - 1) == null) {
                                list.remove(list.size() - 1);
                            }
                            listener.onLoadMore();
                            loadingMore = false;
                        }
                    }, delayMillis);
                }
            }
        });
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (loadMoreEnable) {
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        if (event.getY() - y > 0f) {  // 下滑
                            scrollState = SLIDE_DOWN;
                        } else if (event.getY() - y < 0f) {  // 上拉
                            scrollState = SLIDE_UP;
                        }
                        y = event.getY();
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        y = event.getY();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        scrollState = SLIDE_STOP;  // 停止
                    }
                }
                return false;
            }
        });
    }

    public long getDelayMillis() {
        return delayMillis;
    }

    /**
     * 延迟加载更多的时间
     *
     * @param delayMillis 以毫秒为单位
     */
    public void setDelayMillis(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    public boolean isLoadMoreEnable() {
        return loadMoreEnable;
    }

    /**
     * 是否允许加载更多 默认允许
     *
     * @param loadMoreEnable true 允许 false 不允许
     */
    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
    }

    /**
     * 使用者需要调用这个方法 实现自己的加载更多操作
     *
     * @param listener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.listener = listener;
    }

    public interface OnLoadMoreListener {
        /**
         * 用于在加载更多之前需要执行的操作 可以进行条件判断是否可以执行加载更多
         *
         * @return true加载更多 false不执行加载更多
         */
        boolean onLoadBefore();

        void onLoadMore();
    }

}
