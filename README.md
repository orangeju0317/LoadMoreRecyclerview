# LoadMoreRecyclerview
封装了RecyclerView和RecyclerView的Adapter，使其具有上拉加载更多的功能，还接入了SwipeRefreshLayout，可以下拉刷新，并且不会冲突

效果如下：

![](https://github.com/orangeju0317/LoadMoreRecyclerview/blob/master/wiki/demoAnim.gif)

## 1. MyRecyclerView的使用
com.orange.loadmorerecyclerview.MyRecyclerView是继承了系统 的Recyclerview，使其具有上拉加载的功能，这里对MyRecyclerview的方法作一些说明：
```
setLoadMoreEnable(boolean) 是否允许上拉加载
setDelayMillis(long) 设置延迟加载的时间 单位为毫秒 默认是0ms
setOnLoadMoreListener() 使用者需要调用这个方法，实现OnLoadMoreListener，来复写加载更多时的数据操作
```
接口OnLoadMoreListener中的方法：
```
onLoadBefore() 加载之前执行的方法，可以做业务上的判断或者准备等
onLoadMore() 加载更多时的具体数据操作，可以从网络等获取数据
```

## 2. BaseAdapter的使用
BaseAdapter在封装之后只需要简单的在构造时设置数据源List、item_view的layout id，调用`setBinder()`设置数据的展示方式即可：
```
BaseAdapter<String> adapter = new BaseAdapter<>(list, this, R.layout.item_view);
adapter.setBinder(new BaseAdapter.OnBindViewHolder() {
            @Override
            public void bindView(BaseAdapter.ViewHolder holder, int position) {
                holder.setText(R.id.tv_item1, list.get(position));
            }
        });
```
这里`holder.setText(R.id.tv_item1, list.get(position));`，
对于TextView，设置item_view的控件id和String或者R.string.strid均可，还有对ImageView的简单使用，如果holder的方法不满足需求，可以自行增加

## 注意事项
1. 在app/build.gradle中需添加依赖：
```
compile 'com.android.support:recyclerview-v7:25.3.1'
compile 'com.pnikosis:materialish-progress:1.7'
```
2. 使用时必须复制MyRecyclerView、BaseAdapter、res/layout/view_load_more.xml
3. MyRecyclerView设置adapter时必须为BaseAdapter