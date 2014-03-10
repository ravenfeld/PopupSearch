/*
 * Copyright 2014. Ravenfeld
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under th License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package fr.ravenfeld.librairies.popupsearch;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class ListViewPopupSearch extends ListView {

    private int mYPos;
    private View mArrowUp;
    private int mMaxHeight;
    private boolean mMaxHeightEnable = false;

    public ListViewPopupSearch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ListViewPopupSearch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewPopupSearch(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int proposedHeight = MeasureSpec.getSize(heightMeasureSpec);
        Rect r = new Rect();
        getWindowVisibleDisplayFrame(r);

        int heightDifference = proposedHeight - (r.height());
        int height;

        if (heightDifference > -(r.top + mYPos)) {

            View v = getChildAt(0);
            Rect r2 = new Rect();

            if (v != null) {
                v.getDrawingRect(r2);
            }
            height = r.height() - mYPos - r2.height() - mArrowUp.getHeight();
            if (mMaxHeightEnable && height > mMaxHeight) {
                height = mMaxHeight;
            }
        } else {
            height = proposedHeight;
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
    }


    public void setYPos(int pos) {
        mYPos = pos;
    }

    public void setArrowUp(View v) {
        mArrowUp = v;
    }

    public void setMaxHeight(int max) {
        mMaxHeightEnable = true;
        mMaxHeight = max;
    }


}