package io.github.clendy.leanback.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * RecyclerViewBring
 *
 * @author Clendy 2016/8/8 008 17:20
 */
public class RecyclerViewBring {

    private static final String TAG = RecyclerViewBring.class.getSimpleName();

    private int position = 0;

    public RecyclerViewBring(ViewGroup vg) {
        vg.setClipChildren(false);
        vg.setClipToPadding(false);
    }

    public void bringChildToFront(ViewGroup vg, View child) {
        position = vg.indexOfChild(child);
        if (position != -1) {
            vg.postInvalidate();
        }
    }

    public int getChildDrawingOrder(@NonNull RecyclerView parent, int childCount, int i) {
        View focusedChild = parent.getFocusedChild();
        int childAdapterPosition = parent.getChildAdapterPosition(focusedChild);
        if (focusedChild != null) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                position = childAdapterPosition - ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            }

            if (position < 0) {
                return i;
            } else {
                if (i == childCount - 1) {
                    return (position > i) ? i : position;
                }

                if (i == position) {
                    return childCount - 1;
                }
            }
        }

        return i;

    }
}
