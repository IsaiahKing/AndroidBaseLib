package com.base.scanlistlibrary.scanlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;


import com.base.scanlistlibrary.base.ScanBaseRecyclerViewAdapter;
import com.base.scanlistlibrary.base.DensityUtil;
import com.base.scanlistlibrary.base.ScanContact;
import com.base.scanlistlibrary.videoplay.LoadingVideoView;

import java.util.ArrayList;
import java.util.List;


/**
 * 视频图片加载页面
 * 初始化 view
 *
 * @author zhaolong
 */
public class ScanVideoPlayView<A> extends FrameLayout {


    private Context mContext;
    private ScanVideoListView mScanVideoListView;
    private LoadingVideoView mLoadingVideoView;

    /**
     * 刷新数据listener (下拉刷新和上拉加载)
     */
    private ScanContact.OnRefreshDataListener onRefreshDataListener;
    private ScanContact.OnPageSelectListener mOnPageSelectListener;

    public ScanVideoPlayView(@NonNull Context context) {
        this(context, null);
    }

    public ScanVideoPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(null, true);
    }

    private void init(ScanBaseRecyclerViewAdapter<A> adapter, boolean isPaging) {
        if (adapter != null) {
            initPlayListView(adapter, 0,isPaging);
        }
        initLoadingView();
    }
    public void initPlayListView(ScanBaseRecyclerViewAdapter<A> adapter, boolean isPaging) {
        initPlayListView(adapter,0,isPaging);
    }
    /**
     * 初始化视频列表
     *
     * @param adapter  数据适配器 需要继承于ScanBaseRecyclerViewAdapter
     * @param isPaging true 启动分页功能（也就是说recyclerview自动对齐） false 不启动分页功能
     */
    public void initPlayListView(ScanBaseRecyclerViewAdapter<A> adapter,int emptyLayoutId, boolean isPaging) {
        mScanVideoListView = new ScanVideoListView(mContext,emptyLayoutId, isPaging);
        //创建adapter，需要继承BaseRecyclerViewAdapter<A>
        //给AlivcVideoListView实例化对象添加adapter
        mScanVideoListView.setRecyclerViewAdapter(adapter);
        mScanVideoListView.setVisibility(VISIBLE);
        mScanVideoListView.setOnRecyclerListener(this.mRecyclerListener);
        //设置sdk播放器实例化对象数量
        //mScanVideoListView.setPlayerCount(3);
        //设置下拉、上拉监听进行加载数据
        mScanVideoListView.setOnRefreshDataListener(this.onRefreshDataListener);
        //设置选中回调
        mScanVideoListView.setOnPageSelectListener(this.mOnPageSelectListener);
        //添加到布局中
        addSubView(mScanVideoListView);
    }


    private void initLoadingView() {
        mLoadingVideoView = new LoadingVideoView(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                5);
        params.setMargins(0, 0, 0, DensityUtil.dip2px(getContext(), 4));
        params.gravity = Gravity.BOTTOM;
        addView(mLoadingVideoView, params);
    }


    /**
     * addSubView 添加子view到布局中
     *
     * @param view 子view
     */
    private void addSubView(View view) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        addView(view, params);
    }

    /**
     * 刷新视频列表数据
     *
     * @param datas
     */
    public void refreshVideoList(List<A> datas) {
        List<A> videoList = new ArrayList<>();
        videoList.addAll(datas);
        mScanVideoListView.refreshData(videoList);
        //取消加载loading
        mLoadingVideoView.cancle();

    }

    /**
     * 刷新视频列表数据
     *
     * @param datas 列表数据
     * @param position 初始化显示位置
     */
    public void refreshVideoList(List<A> datas, int position) {
        List<A> videoList = new ArrayList<>();
        videoList.addAll(datas);
        mScanVideoListView.refreshData(videoList, position);
        //取消加载loading
        mLoadingVideoView.cancle();
    }


    /**
     * 添加更多视频
     *
     * @param datas
     */
    public void addMoreData(List<A> datas) {
        List<A> videoList = new ArrayList<>();
        videoList.addAll(datas);
        mScanVideoListView.addMoreData(videoList);
        //取消加载loading
        mLoadingVideoView.cancle();
    }

    /**
     * 设置下拉刷新数据listener
     *
     * @param listener OnRefreshDataListener
     */
    public void setOnRefreshDataListener(ScanContact.OnRefreshDataListener listener) {
        this.onRefreshDataListener = listener;
    }

    public void setOnPageSelectListener(ScanContact.OnPageSelectListener onPageSelectListener) {
        mOnPageSelectListener = onPageSelectListener;
    }

    RecyclerView.RecyclerListener mRecyclerListener;

    public void setOnRecyclerListener(RecyclerView.RecyclerListener recyclerListener) {
        mRecyclerListener = recyclerListener;
    }

    public void onPause() {
        mScanVideoListView.onPause();
    }

    public void onResume() {
        mScanVideoListView.onResume();
    }

    //返回当前的potion
    public int getCurrentPostion() {
        return mScanVideoListView.getCurrentPosition();
    }


    public void startLoading() {
        mScanVideoListView.startLoading();
    }
}
