package io.github.clendy.leanback.widget;

import android.view.View;

/**
 * OnItemClickListener
 *
 * @author Clendy 2016/11/16 016 15:02
 */
public interface OnItemClickListener<T> {
    void onItemClick(View view, int position, T entity);
}
