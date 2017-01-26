/*
 * Copyright 2016 czy1121
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ezy.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ezy.library.noticeview.R;


public class NoticeView extends TextSwitcher {

    private Animation mInUp = anim(1.5f, 0);
    private Animation mOutUp = anim(0, -1.5f);

    private List<String> mDataList = new ArrayList<>();

    private int mIndex = 0;
    private int mInterval = 4000;
    private int mDuration = 900;

    private Drawable mIcon;
    private int mIconTint = 0xff999999;
    private int mIconPadding = 0;
    private int mPaddingLeft = 0;

    private boolean mIsVisible = false;
    private boolean mIsStarted = false;
    private boolean mIsResumed = true;
    private boolean mIsRunning = false;
    private final TextFactory mDefaultFactory = new TextFactory();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsRunning) {
                show(mIndex + 1);
                postDelayed(mRunnable, mInterval);
            }
        }
    };

    public NoticeView(Context context) {
        this(context, null);
    }

    public NoticeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context, attrs);
        setInAnimation(mInUp);
        setOutAnimation(mOutUp);
        setFactory(mDefaultFactory);
        mInUp.setDuration(mDuration);
        mOutUp.setDuration(mDuration);
    }

    private void initWithContext(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NoticeView);
        mIcon = a.getDrawable(R.styleable.NoticeView_nvIcon);
        mIconPadding = (int)a.getDimension(R.styleable.NoticeView_nvIconPadding, 0);

        boolean hasIconTint = a.hasValue(R.styleable.NoticeView_nvIconTint);

        if (hasIconTint) {
            mIconTint = a.getColor(R.styleable.NoticeView_nvIconTint, 0xff999999);
        }

        mInterval = a.getInteger(R.styleable.NoticeView_nvInterval, 4000);
        mDuration = a.getInteger(R.styleable.NoticeView_nvDuration, 900);

        mDefaultFactory.resolve(a);
        a.recycle();

        if (mIcon != null) {
            mPaddingLeft = getPaddingLeft();
            int realPaddingLeft = mPaddingLeft + mIconPadding + mIcon.getIntrinsicWidth();
            setPadding(realPaddingLeft, getPaddingTop(), getPaddingRight(), getPaddingBottom());

            if (hasIconTint) {
                mIcon = mIcon.mutate();
                DrawableCompat.setTint(mIcon, mIconTint);
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mIcon != null) {
            int y = (getMeasuredHeight() - mIcon.getIntrinsicWidth()) / 2;
            mIcon.setBounds(mPaddingLeft, y, mPaddingLeft + mIcon.getIntrinsicWidth(), y + mIcon.getIntrinsicHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIcon != null) {
            mIcon.draw(canvas);
        }
    }

    public int getIndex() {
        return mIndex;
    }

    public void start(List<String> list) {
        mDataList = list;
        if (mDataList == null || mDataList.size() < 1) {
            mIsStarted = false;
            update();
        } else {
            mIsStarted = true;
            update();
            show(0);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsVisible = false;
        update();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mIsVisible = visibility == VISIBLE;
        update();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            mIsResumed = false;
            update();
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            mIsResumed = true;
            update();
            break;

        }
        return super.dispatchTouchEvent(ev);
    }

    private void update() {
        boolean running = mIsVisible && mIsResumed && mIsStarted;
        if (running != mIsRunning) {
            if (running) {
                postDelayed(mRunnable, mInterval);
            } else {
                removeCallbacks(mRunnable);
            }
            mIsRunning = running;
        }
        Log.e("ezy", "update() visible=" + mIsVisible + ", started=" + mIsStarted + ", running=" + mIsRunning);
    }

    private void show(int index) {
        mIndex = index % mDataList.size();
        setText(Html.fromHtml(mDataList.get(mIndex)));
    }

    private Animation anim(float from, float to) {
        final TranslateAnimation anim = new TranslateAnimation(0, 0f, 0, 0f, Animation.RELATIVE_TO_PARENT, from, Animation.RELATIVE_TO_PARENT, to);
        anim.setDuration(mDuration);
        anim.setFillAfter(false);
        anim.setInterpolator(new LinearInterpolator());
        return anim;
    }

    class TextFactory implements ViewFactory {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();

        float size = dp2px(14);
        int color = 1;
        int lines = 1;
        int gravity = Gravity.LEFT;

        void resolve(TypedArray ta) {
            lines = ta.getInteger(R.styleable.NoticeView_nvTextMaxLines, lines);
            size = ta.getDimension(R.styleable.NoticeView_nvTextSize, size);
            color = ta.getColor(R.styleable.NoticeView_nvTextColor, color);
            gravity = ta.getInteger(R.styleable.NoticeView_nvTextGravity, gravity);
        }

        private int dp2px(float dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
        }
        @Override
        public View makeView() {
            TextView tv = new TextView(getContext());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            tv.setMaxLines(lines);
            if (color != 1) {
                tv.setTextColor(color);
            }
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setGravity(Gravity.CENTER_VERTICAL | gravity);
            tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            return tv;
        }
    }
}