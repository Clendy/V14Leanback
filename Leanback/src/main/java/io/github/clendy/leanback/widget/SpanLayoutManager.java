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
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import static android.support.v7.widget.RecyclerView.NO_ID;
import static android.support.v7.widget.RecyclerView.NO_POSITION;


public class SpanLayoutManager extends GridLayoutManager {

    private static final String TAG = SpanLayoutManager.class.getSimpleName();

    private final RecyclerView mBaseGridView;
    private int mSpanCount = 1;

    private Context mContext;

    private int mFocusPosition = NO_POSITION;
    private int mFocusPositionOffset = 0;

    private final ViewsStateBundle mChildrenStates = new ViewsStateBundle();

    private final static int MAX_PENDING_MOVES = 10;

    private int mOrientation = HORIZONTAL;

    private OnChildSelectedListener mChildSelectedListener = null;

    private int mFocusScrollStrategy = BaseGridView.FOCUS_SCROLL_ALIGNED;
    private static int[] sTwoInts = new int[2];
    private boolean mReverseFlowPrimary = false;
    private PendingMoveSmoothScroller mSmoothScroller;

    abstract class GridLinearSmoothScroller extends LinearSmoothScroller {

        GridLinearSmoothScroller() {
            super(mContext);
        }

        @Override
        protected void onStop() {
            View targetView = findViewByPosition(getTargetPosition());
            Log.d(TAG, "targetView:" + targetView);
            if (needsDispatchChildSelectedOnStop()) {
                dispatchChildSelected();
            }
            super.onStop();
        }

        boolean needsDispatchChildSelectedOnStop() {
            return true;
        }

        @Override
        protected void onTargetFound(View targetView,
                                     RecyclerView.State state, Action action) {
            int dx, dy;
            if (mOrientation == HORIZONTAL) {
                dx = sTwoInts[0];
                dy = sTwoInts[1];
            } else {
                dx = sTwoInts[1];
                dy = sTwoInts[0];
            }
            final int distance = (int) Math.sqrt(dx * dx + dy * dy);
            final int time = calculateTimeForDeceleration(distance);
            action.update(dx, dy, time, mDecelerateInterpolator);
        }
    }


    final class PendingMoveSmoothScroller extends GridLinearSmoothScroller {
        // -2 is a target position that LinearSmoothScroller can never find until
        // consumePendingMovesXXX() sets real targetPosition.
        final static int TARGET_UNDEFINED = -2;
        // whether the grid is staggered.
        private final boolean mStaggeredGrid;
        // Number of pending movements on primary direction, negative if PREV_ITEM.
        private int mPendingMoves;

        PendingMoveSmoothScroller(int initialPendingMoves, boolean staggeredGrid) {
            mPendingMoves = initialPendingMoves;
            mStaggeredGrid = staggeredGrid;
            setTargetPosition(TARGET_UNDEFINED);
        }

        void increasePendingMoves() {
            if (mPendingMoves < MAX_PENDING_MOVES) {
                mPendingMoves++;
                if (mPendingMoves == 0) {
                    dispatchChildSelected();
                }
            }
        }

        void decreasePendingMoves() {
            if (mPendingMoves > -MAX_PENDING_MOVES) {
                mPendingMoves--;
                if (mPendingMoves == 0) {
                    dispatchChildSelected();
                }
            }
        }


        boolean canScrollTo(View view) {
            return view.getVisibility() == View.VISIBLE && (!hasFocus() || view.hasFocusable());
        }


        @Override
        protected void updateActionForInterimTarget(Action action) {
            if (mPendingMoves == 0) {
                return;
            }
            super.updateActionForInterimTarget(action);
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            if (mPendingMoves == 0) {
                return null;
            }
            int direction = (mReverseFlowPrimary ? mPendingMoves > 0 : mPendingMoves < 0) ? -1 : 1;
            if (mOrientation == HORIZONTAL) {
                return new PointF(direction, 0);
            } else {
                return new PointF(0, direction);
            }
        }

        @Override
        boolean needsDispatchChildSelectedOnStop() {
            return mPendingMoves != 0;
        }

        @Override
        protected void onStop() {
            super.onStop();
            // if we hit wall,  need clear the remaining pending moves.
            mPendingMoves = 0;
            View v = findViewByPosition(getTargetPosition());
            if (v != null) scrollToView(v, true);
        }
    }

    public SpanLayoutManager(Context context, RecyclerView recyclerView, int spanCount) {
        super(context, spanCount);
        mContext = context;
        mBaseGridView = recyclerView;
        mSpanCount = spanCount;
        setOrientation(VERTICAL);
        setReverseLayout(false);
        mSmoothScroller = new PendingMoveSmoothScroller(1, true);
    }

    public SpanLayoutManager(Context context, RecyclerView recyclerView, int spanCount,
                             int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
        mContext = context;
        mBaseGridView = recyclerView;
        mSpanCount = spanCount;
        setOrientation(orientation);
        mSmoothScroller = new PendingMoveSmoothScroller(1, true);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {

        if (position < 0 || getItemCount() <= position) {
            Log.e(TAG, "Ignored smooth scroll to " + position +
                    " as it is not within the item range 0 - " + getItemCount());
            return;
        }
        if (mSmoothScroller != null) {
            mSmoothScroller.setTargetPosition(position);
            startSmoothScroll(mSmoothScroller);
        } else {
            super.smoothScrollToPosition(recyclerView, state, position);
        }
    }

    @Override
    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            return;
        }

        mOrientation = orientation;
    }

    public void setOnChildSelectedListener(OnChildSelectedListener listener) {
        mChildSelectedListener = listener;
    }

    @SuppressWarnings("deprecation")
    private int getPositionByView(View view) {
        if (view == null) {
            return NO_POSITION;
        }
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        if (params == null || params.isItemRemoved()) {
            return NO_POSITION;
        }
        return params.getViewPosition();
    }

    private void dispatchChildSelected() {

        View view = mFocusPosition == NO_POSITION ? null : findViewByPosition(mFocusPosition);
        if (view != null) {
            RecyclerView.ViewHolder vh = mBaseGridView.getChildViewHolder(view);
            if (mChildSelectedListener != null) {
                mChildSelectedListener.onChildSelected(mBaseGridView, view, mFocusPosition,
                        vh == null ? NO_ID : vh.getItemId());
            }
        } else {
            if (mChildSelectedListener != null) {
                mChildSelectedListener.onChildSelected(mBaseGridView, null, NO_POSITION, NO_ID);
            }
        }

    }

    @Override
    public boolean canScrollHorizontally() {
        return mOrientation == HORIZONTAL;
    }

    @Override
    public boolean canScrollVertically() {
        return mOrientation == VERTICAL;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void forceRequestLayout() {
        ViewCompat.postOnAnimation(mBaseGridView, mRequestLayoutRunnable);
    }

    private final Runnable mRequestLayoutRunnable = new Runnable() {
        @Override
        public void run() {
            requestLayout();
        }
    };

    private int getSelection() {
        return mFocusPosition;
    }

    int getChildDrawingOrder(RecyclerView recyclerView, int childCount, int i) {
        View view = findViewByPosition(mFocusPosition);
        if (view == null) {
            return i;
        }
        int focusIndex = recyclerView.indexOfChild(view);
        // supposely 0 1 2 3 4 5 6 7 8 9, 4 is the center item
        // drawing order is 0 1 2 3 9 8 7 6 5 4
        if (i < focusIndex) {
            return i;
        } else if (i < childCount - 1) {
            return focusIndex + childCount - 1 - i;
        } else {
            return focusIndex;
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (mFocusPosition != NO_POSITION && mFocusPositionOffset != Integer.MIN_VALUE) {
            mFocusPosition = mFocusPosition + mFocusPositionOffset;
        }
        mFocusPositionOffset = 0;

        boolean hadFocus = mBaseGridView.hasFocus();
        final boolean scrollToFocus = !isSmoothScrolling()
                && mFocusScrollStrategy == BaseGridView.FOCUS_SCROLL_ALIGNED;
        // appends items till focus position.
        if (mFocusPosition != NO_POSITION) {
            View focusView = findViewByPosition(mFocusPosition);
            if (focusView != null) {
                if (scrollToFocus) {
                    scrollToView(focusView, false);
                }
                if (hadFocus && !focusView.hasFocus()) {
                    focusView.requestFocus();
                }
            }
        }

    }

    private void scrollToView(View view, boolean smooth) {
        scrollToView(view, view == null ? null : view.findFocus(), smooth);
    }

    private void scrollToView(View view, View childView, boolean smooth) {
        int newFocusPosition = getPositionByView(view);
        if (newFocusPosition != mFocusPosition) {
            mFocusPosition = newFocusPosition;
            mFocusPositionOffset = 0;
            mBaseGridView.invalidate();
        }
        if (view == null) {
            return;
        }
        if (!view.hasFocus() && mBaseGridView.hasFocus()) {
            view.requestFocus();
        }

    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        if (oldAdapter != null) {
            mFocusPosition = NO_POSITION;
            mFocusPositionOffset = 0;
        }
        super.onAdapterChanged(oldAdapter, newAdapter);
    }

    @Override
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsAdded(recyclerView, positionStart, itemCount);
        if (mFocusPosition != NO_POSITION && mFocusPositionOffset != Integer.MIN_VALUE) {
            int pos = mFocusPosition + mFocusPositionOffset;
            if (positionStart <= pos) {
                mFocusPositionOffset += itemCount;
            }
        }
        mChildrenStates.clear();
    }

    @Override
    public void onItemsChanged(RecyclerView recyclerView) {
        super.onItemsChanged(recyclerView);
        mFocusPositionOffset = 0;
        mChildrenStates.clear();
    }

    @Override
    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsRemoved(recyclerView, positionStart, itemCount);
        if (mFocusPosition != NO_POSITION && mFocusPositionOffset != Integer.MIN_VALUE) {
            int pos = mFocusPosition + mFocusPositionOffset;
            if (positionStart <= pos) {
                if (positionStart + itemCount > pos) {
                    mFocusPositionOffset = Integer.MIN_VALUE;
                } else {
                    mFocusPositionOffset -= itemCount;
                }
            }
        }
        mChildrenStates.clear();
    }

    @Override
    public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
        super.onItemsMoved(recyclerView, from, to, itemCount);
        if (mFocusPosition != NO_POSITION && mFocusPositionOffset != Integer.MIN_VALUE) {
            int pos = mFocusPosition + mFocusPositionOffset;
            if (from <= pos && pos < from + itemCount) {
                mFocusPositionOffset += to - from;
            } else if (from < pos && to > pos - itemCount) {
                mFocusPositionOffset -= itemCount;
            } else if (from > pos && to < pos) {
                mFocusPositionOffset += itemCount;
            }
        }
        mChildrenStates.clear();
    }

    @Override
    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsUpdated(recyclerView, positionStart, itemCount);
        for (int i = positionStart, end = positionStart + itemCount; i < end; i++) {
            mChildrenStates.remove(i);
        }
    }

    @Override
    public boolean onRequestChildFocus(RecyclerView parent, RecyclerView.State state, View child,
                                       View focused) {
        // TODO: 2017/1/15 015
//        if (mFocusSearchDisabled) {
//            return true;
//        }
        if (getPositionByView(child) == NO_POSITION) {
            // This shouldn't happen, but in case it does be sure not to attempt a
            // scroll to a view whose item has been removed.
            return true;
        }
//        if (!mInLayout && !mInSelection && !mInScroll) {
//            scrollToView(child, focused, true);
//        }
        return true;
    }

    @Override
    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect,
                                                 boolean immediate) {
        return false;
    }

    private final static class SavedState implements Parcelable {

        int index;
        Bundle childStates = Bundle.EMPTY;

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(index);
            out.writeBundle(childStates);
        }

        @SuppressWarnings("hiding")
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

        @Override
        public int describeContents() {
            return 0;
        }

        SavedState(Parcel in) {
            index = in.readInt();
            childStates = in.readBundle(SpanLayoutManager.class.getClassLoader());
        }

        SavedState() {

        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState();
        ss.index = getSelection();
        Bundle bundle = mChildrenStates.saveAsBundle();
        for (int i = 0, count = getChildCount(); i < count; i++) {
            View view = getChildAt(i);
            int position = getPositionByView(view);
            if (position != NO_POSITION) {
                bundle = mChildrenStates.saveOnScreenView(bundle, view, position);
            }
        }
        ss.childStates = bundle;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            return;
        }
        SavedState loadingState = (SavedState) state;
        mFocusPosition = loadingState.index;
        mFocusPositionOffset = 0;
        mChildrenStates.loadFromBundle(loadingState.childStates);
        requestLayout();
    }

}
