/*
 * Copyright (C) 2016 Clendy <yc330483161@163.com | yc330483161@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.clendy.leanback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.View;

import io.github.clendy.leanback.R;

/**
 * A horizontally scrolling lists that supports paging loading
 *
 * @author Clendy
 */
public class HorizontalLoadMoreGridView extends HorizontalGridView {

    private static final String TAG = HorizontalLoadMoreGridView.class.getSimpleName();

    private boolean canLoadMore = false;
    private boolean addLoadingView = false;
    private int allLoadedToastCount = 0;
    private int mLoadState = OnLoadMoreListener.STATE_MORE_LOADED;

    private OnLoadMoreListener mLoadMoreListener;

    private final Runnable mRequestLayoutRunnable = new Runnable() {
        @Override
        public void run() {
            requestLayout();
        }
    };

    private final Runnable mScrollLoadingRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLoadMoreListener != null) {
                notifyMoreLoading();
                allLoadedToastCount = 0;
                mLoadMoreListener.loadMore();
                mLoadMoreListener.showMsgLoading();
            }
        }
    };


    public HorizontalLoadMoreGridView(Context context) {
        this(context, null);
    }

    public HorizontalLoadMoreGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalLoadMoreGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.lbLoadMoreGridView);
        try {
            canLoadMore = a.getBoolean(R.styleable.lbLoadMoreGridView_canLoadMore, false);
            addLoadingView = a.getBoolean(R.styleable.lbLoadMoreGridView_addLoadingView, false);
        } finally {
            a.recycle();
        }
    }

    private void forceRequestLayout() {
        ViewCompat.postOnAnimation(this, mRequestLayoutRunnable);
    }

    private void startLoadingRunnable(){
        ViewCompat.postOnAnimation(this, mScrollLoadingRunnable);
    }

    public int getLoadState() {
        return mLoadState;
    }

    public boolean isCanLoadMore() {
        return canLoadMore;
    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    public boolean isAddLoadingView() {
        return addLoadingView;
    }

    public void setAddLoadingView(boolean addLoadingView) {
        this.addLoadingView = addLoadingView;
    }

    /**
     * if you want invoke this method to load more data, you must to implement
     * the interface {@link OnLoadMoreListener}
     */
    public void loadMoreData() {
        if (canLoadMore && isMoreLoaded()) {
            startLoadingRunnable();
        } else if (canLoadMore && isAllLoaded() && allLoadedToastCount++ <= 0) {
            if (mLoadMoreListener != null) {
                mLoadMoreListener.showMsgAllLoaded();
            }
        }
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    /**
     * notify more data is loading
     */
    public void notifyMoreLoading() {
        mLoadState = OnLoadMoreListener.STATE_MORE_LOADING;
    }

    /**
     * notify more data loading is completed
     */
    public void notifyMoreLoaded() {
        mLoadState = OnLoadMoreListener.STATE_MORE_LOADED;
    }

    /**
     * notify all data loaded
     */
    public void notifyAllLoaded() {
        mLoadState = OnLoadMoreListener.STATE_ALL_LOADED;
    }

    /**
     * to determine if the load is being loaded
     *
     * @return boolean
     */
    public boolean isMoreLoading() {
        return mLoadState == OnLoadMoreListener.STATE_MORE_LOADING;
    }

    public boolean isMoreLoaded() {
        return mLoadState == OnLoadMoreListener.STATE_MORE_LOADED;
    }

    /**
     * To determine whether the data are loaded complete
     *
     * @return boolean
     */
    public boolean isAllLoaded() {
        return mLoadState == OnLoadMoreListener.STATE_ALL_LOADED;
    }


    /**
     * You must override this method if RecyclerView need to page loading and you need to handle
     * the problem of Focus jitter
     *
     * @param focused   focused The view that currently has focus
     * @param direction One of FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, and
     *                  FOCUS_RIGHT, or 0 for not applicable.
     * @return the result of focusSearch
     */
    @Override
    public View focusSearch(View focused, int direction) {
        // Step.1 search focus by onInterceptFocusSearch
        View result = getLayoutManager().onInterceptFocusSearch(focused, direction);
        if (result != null) {
            return result;
        }
        // Step.2 search focus by FocusFinder
        final FocusFinder ff = FocusFinder.getInstance();
        result = ff.findNextFocus(this, focused, direction);
        if (result != null) {
            return result;
        }
        // Step.3 search focus by onFocusSearchFailed
        if (getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager layoutManager = (GridLayoutManager) getLayoutManager();
            if (layoutManager.ensureRecyclerState()) {
                result = layoutManager.onFocusSearchFailed(focused, direction,
                        layoutManager.getRecycler(), layoutManager.getState());
                if (result != null) {
                    return result;
                }
            }
        }

        if (direction == FOCUS_RIGHT) {
            final int position = getLayoutManager().getPosition(focused);
            if (position < getLayoutManager().getItemCount() - 1) {
                forceRequestLayout();
            }
        }

        return null;
    }


}
