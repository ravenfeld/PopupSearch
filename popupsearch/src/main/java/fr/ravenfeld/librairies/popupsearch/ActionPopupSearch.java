/*
 * Copyright 2014. Ravenfeld
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under th License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package fr.ravenfeld.librairies.popupsearch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;

public class ActionPopupSearch {

    public enum AnimationPopupSearch {
        ANIM_GROW_FROM_LEFT, ANIM_GROW_FROM_RIGHT, ANIM_GROW_FROM_CENTER, ANIM_AUTO
    }

    private Activity mActivity;
    private PopupWindow mWindow;
    private View mRootView;
    private ImageView mArrowUp;
    private View mViewActionSearch;
    private Drawable mBackground = null;
    private AnimationPopupSearch mAnimStyle;
    private ListViewPopupSearch mListView;
    private View mActivityRootView;
    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;
    private boolean mOpenKeyboard;

    public ActionPopupSearch(Activity activity, View viewActionSearch) {
        mActivity = activity;
        initPopupWindow();
        mViewActionSearch = viewActionSearch;

        setRootViewId(R.layout.popup_search);

        mAnimStyle = AnimationPopupSearch.ANIM_AUTO;
    }


    private void initPopupWindow() {
        mWindow = new PopupWindow(mActivity);
        mWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    onTouchOutSide(new int[]{(int) event.getRawX(), (int) event.getRawY()});
                    return true;
                }
                return false;
            }
        });
        mWindow.setTouchable(true);
        mWindow.setFocusable(false);
        mWindow.setOutsideTouchable(true);
        mWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void setRootViewId(int id) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(id, null);
        mArrowUp = (ImageView) mRootView.findViewById(R.id.arrow_up);
        mListView = (ListViewPopupSearch) mRootView.findViewById(R.id.scroll);
        View footer = mRootView.findViewById(R.id.view_footer);
        mListView.setFooter(footer);
        View header = mRootView.findViewById(R.id.view_header);
        mListView.setHeader(header);
        setContentView(mRootView);
    }


    public void setAdapter(ListAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener item) {
        mListView.setOnItemClickListener(item);
    }

    public void show() {

        if (mRootView == null)
            throw new IllegalStateException("setCntentView was not called with a view to display.");


        if (mBackground == null) {
            mWindow.setBackgroundDrawable(new BitmapDrawable());
        } else {
            mWindow.setBackgroundDrawable(mBackground);
        }

        mWindow.setContentView(mRootView);
        mWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

        int[] location = new int[2];

        mViewActionSearch.getLocationOnScreen(location);

        Rect anchorRect = new Rect(location[0], location[1], location[0] + mViewActionSearch.getWidth(), location[1]
                + mViewActionSearch.getHeight());

        mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        int rootWidth = mRootView.getMeasuredWidth();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int xPos, yPos, arrowPos;

        if (mWindow.getWidth() == LayoutParams.MATCH_PARENT || mWindow.getWidth() == LayoutParams.WRAP_CONTENT) {
            xPos = (screenWidth - rootWidth) / 2;
            arrowPos = anchorRect.centerX();
        } else {
            if (mWindow.getWidth() < (screenWidth - anchorRect.left)) {
                xPos = anchorRect.left;
            } else {
                xPos = anchorRect.left - (mWindow.getWidth() - mViewActionSearch.getWidth());
            }
            arrowPos = anchorRect.centerX() - xPos;
        }
        yPos = anchorRect.bottom;
        showArrow(arrowPos);

        setAnimationStyle(screenWidth, anchorRect.centerX());
        mWindow.showAtLocation(mViewActionSearch, Gravity.NO_GRAVITY, xPos, yPos);
        mListView.setYPos(yPos);


    }


    private void setAnimationStyle(int screenWidth, int requestedX) {
        int arrowPos = requestedX - mArrowUp.getMeasuredWidth() / 2;

        switch (mAnimStyle) {
            case ANIM_GROW_FROM_LEFT:
                mWindow.setAnimationStyle(R.style.Animations_PopupMenu_Left);
                break;

            case ANIM_GROW_FROM_RIGHT:
                mWindow.setAnimationStyle(R.style.Animations_PopupMenu_Right);
                break;

            case ANIM_GROW_FROM_CENTER:
                mWindow.setAnimationStyle(R.style.Animations_PopupMenu_Center);
                break;

            case ANIM_AUTO:
                if (arrowPos <= screenWidth / 4) {
                    mWindow.setAnimationStyle(R.style.Animations_PopupMenu_Left);
                } else if (arrowPos > screenWidth / 4 && arrowPos < 3 * (screenWidth / 4)) {
                    mWindow.setAnimationStyle(R.style.Animations_PopupMenu_Center);
                } else {
                    mWindow.setAnimationStyle(R.style.Animations_PopupMenu_Right);
                }

                break;
        }
    }


    private void showArrow(int requestedX) {

        final int arrowWidth = mArrowUp.getMeasuredWidth();

        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) mArrowUp.getLayoutParams();

        param.leftMargin = requestedX - arrowWidth / 2;

    }

    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        mWindow.setOnDismissListener(listener);
    }


    private void onTouchOutSide(int[] location) {
        if (!isViewContains(mViewActionSearch, location) && !isViewContains(mRootView, location)) {
            dismiss();
        }
    }

    private boolean isViewContains(View view, int[] location) {
        int[] l = new int[2];
        view.getLocationOnScreen(l);
        int x = l[0];
        int y = l[1];
        int w = view.getWidth();
        int h = view.getHeight();

        if (location[0] < x || location[0] > x + w || location[1] < y || location[1] > y + h) {
            return false;
        }
        return true;
    }


    public void setContentView(int layoutResID) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(inflater.inflate(layoutResID, null));
    }

    public void setContentView(View root) {
        mRootView = root;
        mWindow.setContentView(root);
    }


    public void dismiss() {
        hideKeyboard();
        mWindow.dismiss();
    }

    public void update() {
        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.invalidateViews();
            }
        },500);
    }

    public void setWidth(int width) {
        mWindow.setWidth(width);
    }

    public void setMaxHeight(int height) {
        mListView.setMaxHeight(height);
    }

    public boolean isShowing() {
        return mWindow.isShowing();
    }

    public void setBackgroundDrawable(Drawable background) {
        mBackground = background;
    }

    public View getView() {
        return mRootView;
    }

    public void setAnimStyle(AnimationPopupSearch mAnimStyle) {
        this.mAnimStyle = mAnimStyle;
    }

    public void hideKeyboard() {
        InputMethodManager in = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (in.isActive(mViewActionSearch))
            in.hideSoftInputFromWindow(mViewActionSearch.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN, new ResultReceiver(null){
                protected void onReceiveResult (int resultCode, Bundle resultData){
                    update();
                }
            });

    }
}