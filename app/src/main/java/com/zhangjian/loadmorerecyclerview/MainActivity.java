package com.zhangjian.loadmorerecyclerview;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private int index;  // 记录列表的索引
    private ArrayList<String> list;
    private BaseAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setData();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl);

        MyRecyclerView rcv = (MyRecyclerView) findViewById(R.id.rcv);
        rcv.setLoadMoreEnable(true); // 默认是允许
        rcv.setDelayMillis(1000); // 设置1000毫秒延迟
        rcv.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new BaseAdapter<>(list, this, R.layout.item_view);
        adapter.setBinder(new BaseAdapter.OnBindViewHolder() {
            @Override
            public void bindView(BaseAdapter.ViewHolder holder, int position) {
                holder.setText(R.id.tv_item1, list.get(position));
            }
        });
        rcv.setAdapter(adapter);
        rcv.setOnLoadMoreListener(new MyRecyclerView.OnLoadMoreListener() {
            @Override
            public boolean onLoadBefore() {
                return !refreshLayout.isRefreshing();
            }

            @Override
            public void onLoadMore() {
                setData();
            }
        });

        // 设置下拉刷新
        int[] swipeRefreshColor = {Color.parseColor("#ff33b5e5"),  //android.R.color.holo_blue_light
                Color.parseColor("#ff99cc00"),//holo_green_light
                Color.parseColor("#ffffbb33"),//holo_orange_light
                Color.parseColor("#ffcc0000")};//holo_red_dark
        refreshLayout.setColorSchemeColors(swipeRefreshColor);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        list.clear();
                        index = 0;
                        setData();
                        refreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    private void setData() {
        addData(list);
        adapter.notifyDataSetChanged();
    }

    private void addData(ArrayList<String> list) {
        for (int i = 0; i < 10; i++) {
            list.add("item " + index);
            index++;
        }
    }
}
