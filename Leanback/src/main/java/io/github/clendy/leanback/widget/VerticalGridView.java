/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.clendy.leanback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import io.github.clendy.leanback.R;


/**
 * A {@link android.view.ViewGroup} that shows items in a vertically scrolling list. The items
 * come from the {@link Adapter} associated with this view.
 * <p>
 * {@link Adapter} can optionally implement {@link FacetProviderAdapter} which
 * provides {@link FacetProvider} for a given view type;  {@link ViewHolder}
 * can also implement {@link FacetProvider}.  Facet from ViewHolder
 * has a higher priority than the one from FacetProiderAdapter associated with viewType.
 * Supported optional facets are:
 * <ol>
 * <li> {@link ItemAlignmentFacet}
 * When this facet is provided by ViewHolder or FacetProviderAdapter,  it will
 * override the item alignment settings set on VerticalGridView.  This facet also allows multiple
 * alignment positions within one ViewHolder.
 * </li>
 * </ol>
 */
public class VerticalGridView extends BaseGridView {

    private static final String TAG = VerticalGridView.class.getSimpleName();

    protected boolean focusOutLeft;
    protected boolean focusOutTop;
    protected boolean focusOutRight;
    protected boolean focusOutBottom;


    public VerticalGridView(Context context) {
        this(context, null);
    }

    public VerticalGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mLayoutManager.setOrientation(VERTICAL);
        initAttributes(context, attrs);
    }

    protected void initAttributes(Context context, AttributeSet attrs) {
        initBaseGridViewAttributes(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.lbVerticalGridView);

        focusOutLeft = a.getBoolean(R.styleable.lbVerticalGridView_lbv_focusOutLeft, true);
        focusOutTop = a.getBoolean(R.styleable.lbVerticalGridView_lbv_focusOutTop, true);
        focusOutRight = a.getBoolean(R.styleable.lbVerticalGridView_lbv_focusOutRight, true);
        focusOutBottom = a.getBoolean(R.styleable.lbVerticalGridView_lbv_focusOutBottom, true);

        setColumnWidth(a);
        setNumColumns(a.getInt(R.styleable.lbVerticalGridView_numberOfColumns, 1));

        a.recycle();
    }

    void setColumnWidth(TypedArray array) {
        TypedValue typedValue = array.peekValue(R.styleable.lbVerticalGridView_columnWidth);
        if (typedValue != null) {
            int size = array.getLayoutDimension(R.styleable.lbVerticalGridView_columnWidth, 0);
            setColumnWidth(size);
        }
    }

    /**
     * Sets the number of columns.  Defaults to one.
     */
    public void setNumColumns(int numColumns) {
        mLayoutManager.setNumRows(numColumns);
        requestLayout();
    }

    /**
     * Sets the column width.
     *
     * @param width May be {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}, or a size
     *              in pixels. If zero, column width will be fixed based on number of columns
     *              and view width.
     */
    public void setColumnWidth(int width) {
        mLayoutManager.setRowHeight(width);
        requestLayout();
    }


    /**
     * determine whether the focus is located on the leftmost column
     *
     * @return true if the focus on the leftmost column
     */
    public boolean isFocusOnLeftmostColumn() {
        if (mLayoutManager != null && getFocusedChild() != null) {
            int position = mLayoutManager.getPosition(getFocusedChild());
            Log.i(TAG, "isFocusOnLeftmostColumn, position:" + position);
            Log.i(TAG, "isFocusOnLeftmostColumn, getNumRows():" + mLayoutManager.getNumRows());
            return position % mLayoutManager.getNumRows() == 0;
        }
        return false;
    }

    /**
     * determine whether the focus is located on the topmost row
     *
     * @return true if the focus on the topmost row
     */
    public boolean isFocusOnTopmostRow() {
        if (mLayoutManager != null && getFocusedChild() != null) {
            int position = mLayoutManager.getPosition(getFocusedChild());
            Log.i(TAG, "isFocusOnTopmostRow, position:" + position);
            Log.i(TAG, "isFocusOnTopmostRow, getNumRows():" + mLayoutManager.getNumRows());
            return position < mLayoutManager.getNumRows();
        }
        return false;
    }

    /**
     * determine whether the focus is located on the rightmost column
     *
     * @return true if the focus on the rightmost column
     */
    public boolean isFocusOnRightmostColumn() {
        if (mLayoutManager != null && getFocusedChild() != null) {
            int position = mLayoutManager.getPosition(getFocusedChild());
            Log.i(TAG, "isFocusOnRightmostColumn, position:" + position);
            Log.i(TAG, "isFocusOnRightmostColumn, getNumRows():" + mLayoutManager.getNumRows());
            if (position % mLayoutManager.getNumRows() == mLayoutManager.getNumRows() - 1) {
                return true;
            } else if (position == mLayoutManager.getItemCount() - 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * determine whether the focus is located on the bottom row
     *
     * @return true if the focus on the bottom row
     */
    public boolean isFocusOnBottomRow() {
        if (mLayoutManager != null && getFocusedChild() != null) {
            int position = mLayoutManager.getPosition(getFocusedChild());
            int rowCount = mLayoutManager.getItemCount() / mLayoutManager.getNumRows();
            int rowNum = position / mLayoutManager.getNumRows();
            Log.i(TAG, "isFocusOnBottomRow, position:" + position);
            Log.i(TAG, "isFocusOnBottomRow, getNumRows():" + mLayoutManager.getNumRows());
            Log.i(TAG, "isFocusOnBottomRow, rowCount:" + rowCount);
            Log.i(TAG, "isFocusOnBottomRow, rowNum:" + rowNum);
            return rowNum == rowCount - 1;
        }

        return false;
    }

    /**
     * determine whether the focus is located on the bottom row
     *
     * @param focus    the current focus view
     * @param position The adapter position of the item which is rendered by this View.
     * @return true if the focus on the bottom row
     */
    public boolean isFocusOnBottomRow(View focus, int position) {
        if (mLayoutManager != null && focus != null) {
            int rowCount = mLayoutManager.getItemCount() / mLayoutManager.getNumRows();
            int rowNum = position / mLayoutManager.getNumRows();
            Log.i(TAG, "isFocusOnBottomRow, position:" + position);
            Log.i(TAG, "isFocusOnBottomRow, getNumRows():" + mLayoutManager.getNumRows());
            Log.i(TAG, "isFocusOnBottomRow, rowCount:" + rowCount);
            Log.i(TAG, "isFocusOnBottomRow, rowNum:" + rowNum);
            return rowNum == rowCount - 1;
        }
        return false;
    }

    public boolean isFocusOutLeft() {
        return focusOutLeft;
    }

    public void setFocusOutLeft(boolean focusOutLeft) {
        this.focusOutLeft = focusOutLeft;
    }

    public boolean isFocusOutTop() {
        return focusOutTop;
    }

    public void setFocusOutTop(boolean focusOutTop) {
        this.focusOutTop = focusOutTop;
    }

    public boolean isFocusOutRight() {
        return focusOutRight;
    }

    public void setFocusOutRight(boolean focusOutRight) {
        this.focusOutRight = focusOutRight;
    }

    public boolean isFocusOutBottom() {
        return focusOutBottom;
    }

    public void setFocusOutBottom(boolean focusOutBottom) {
        this.focusOutBottom = focusOutBottom;
    }
}
