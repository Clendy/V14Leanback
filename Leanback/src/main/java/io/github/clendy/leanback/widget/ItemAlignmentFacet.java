/*
 * Copyright (C) 2015 The Android Open Source Project
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

import android.view.View;


public final class ItemAlignmentFacet {

    /**
     * Value indicates that percent is not used.
     */
    public final static float ITEM_ALIGN_OFFSET_PERCENT_DISABLED = -1;

    /**
     * Definition of an alignment position under a view.
     */
    public static class ItemAlignmentDef {
        int mViewId = View.NO_ID;
        int mFocusViewId = View.NO_ID;
        int mOffset = 0;
        float mOffsetPercent = 50f;
        boolean mOffsetWithPadding = false;


        public final void setItemAlignmentOffset(int offset) {
            mOffset = offset;
        }


        public final int getItemAlignmentOffset() {
            return mOffset;
        }


        public final void setItemAlignmentOffsetWithPadding(boolean withPadding) {
            mOffsetWithPadding = withPadding;
        }


        public final boolean isItemAlignmentOffsetWithPadding() {
            return mOffsetWithPadding;
        }


        public final void setItemAlignmentOffsetPercent(float percent) {
            if ((percent < 0 || percent > 100) &&
                    percent != ITEM_ALIGN_OFFSET_PERCENT_DISABLED) {
                throw new IllegalArgumentException();
            }
            mOffsetPercent = percent;
        }


        public final float getItemAlignmentOffsetPercent() {
            return mOffsetPercent;
        }


        public final void setItemAlignmentViewId(int viewId) {
            mViewId = viewId;
        }


        public final int getItemAlignmentViewId() {
            return mViewId;
        }


        public final void setItemAlignmentFocusViewId(int viewId) {
            mFocusViewId = viewId;
        }


        public final int getItemAlignmentFocusViewId() {
            return mFocusViewId != View.NO_ID ? mFocusViewId : mViewId;
        }
    }

    private ItemAlignmentDef[] mAlignmentDefs = new ItemAlignmentDef[]{new ItemAlignmentDef()};

    public boolean isMultiAlignment() {
        return mAlignmentDefs.length > 1;
    }

    public void setAlignmentDefs(ItemAlignmentDef[] defs) {
        if (defs == null || defs.length < 1) {
            throw new IllegalArgumentException();
        }
        mAlignmentDefs = defs;
    }

    public ItemAlignmentDef[] getAlignmentDefs() {
        return mAlignmentDefs;
    }

}
