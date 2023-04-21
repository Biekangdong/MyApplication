/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.customerlauncher3.DeviceDJMainActivity;
import com.example.customerlauncher3.R;
import com.example.customerlauncher3.ScreenUtils;


/**
 * TextView that draws a bubble behind the text. We cannot use a LineBackgroundSpan
 * because we want to make the bubble taller than the text and TextView's clip is
 * too aggressive.
 */
public class BubbleTextView extends FrameLayout {
    private static final String TAG = "BubbleTextView";
    private static SparseArray<Theme> sPreloaderThemes = new SparseArray<Theme>(2);

    // Dimensions in DP
    private static final float AMBIENT_SHADOW_RADIUS = 2.5f;
    private static final float KEY_SHADOW_RADIUS = 1f;
    private static final float KEY_SHADOW_OFFSET = 0.5f;
    private static final int AMBIENT_SHADOW_COLOR = 0x33000000;
    private static final int KEY_SHADOW_COLOR = 0x66000000;

    private static final int DISPLAY_WORKSPACE = 0;
    private static final int DISPLAY_ALL_APPS = 1;
    private static final int DISPLAY_FOLDER = 2;

    private final DeviceDJMainActivity mDeviceDJMainActivity;
    private Drawable mIcon;
    private final boolean mCenterVertically;
    private final Drawable mBackground;
    private OnLongClickListener mOnLongClickListener;
    private final CheckLongPressHelper mLongPressHelper;
    private final HolographicOutlineHelper mOutlineHelper;
    private final StylusEventHelper mStylusEventHelper;

    private boolean mBackgroundSizeChanged;

    private Bitmap mPressedBackground;

    private float mSlop;

    private final boolean mDeferShadowGenerationOnTouch;
    private final boolean mCustomShadowsEnabled;
    private final boolean mLayoutHorizontal;
    private int mIconSize;

    private boolean mStayPressed;
    private boolean mIgnorePressedStateChange;
    private boolean mDisableRelayout = false;


    private TextView textView;//图标+标题+设备信息
    private TextView textView2;//模式信息
    private int offtextViewY = 0;//偏移
    private ImageView imageView;//删除


    private SpannableStringBuilder spannableStringBuilder;

    private boolean mDeleteTargeted;
    private RectF mTouchDeleteBounds = new RectF();

    private int clickLongType;

    private boolean isBorderShow = false;

    public BubbleTextView(Context context) {
        this(context, null, 0);
    }

    public BubbleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDeviceDJMainActivity = DeviceDJMainActivity.getLauncher(context);
        final DeviceProfile grid = mDeviceDJMainActivity.getDeviceProfile();

        TypedArray a = context.obtainStyledAttributes(attrs,
                com.android.launcher3.R.styleable.BubbleTextView, defStyle, 0);
        mCustomShadowsEnabled = a.getBoolean(com.android.launcher3.R.styleable.BubbleTextView_customShadows, true);
        mLayoutHorizontal = a.getBoolean(com.android.launcher3.R.styleable.BubbleTextView_layoutHorizontal, false);
        mDeferShadowGenerationOnTouch =
                a.getBoolean(com.android.launcher3.R.styleable.BubbleTextView_deferShadowGeneration, false);


        textView = new TextView(getContext());
        //textView.setTextColor(Color.WHITE);
        textView.setTextSize(14);
        textView.setText("hello");
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        ColorStateList colorStateList = (ColorStateList) getResources().getColorStateList(com.android.launcher3.R.color.text_statues_selector);
        textView.setTextColor(colorStateList);
        addView(textView);


        int display = a.getInteger(com.android.launcher3.R.styleable.BubbleTextView_iconDisplay, DISPLAY_WORKSPACE);
        int defaultIconSize = grid.iconSizePx;
        setTextStyle(textView, display, grid);


        mCenterVertically = a.getBoolean(com.android.launcher3.R.styleable.BubbleTextView_centerVertically, false);

        mIconSize = a.getDimensionPixelSize(com.android.launcher3.R.styleable.BubbleTextView_iconSizeOverride,
                defaultIconSize);
        a.recycle();

        if (mCustomShadowsEnabled) {
            // Draw the background itself as the parent is drawn twice.
            mBackground = getBackground();
            setBackground(null);
//            setBackgroundColor(Color.WHITE);
            // Set shadow layer as the larger shadow to that the textView does not clip the shadow.
            float density = getResources().getDisplayMetrics().density;
            textView.setShadowLayer(density * AMBIENT_SHADOW_RADIUS, 0, 0, AMBIENT_SHADOW_COLOR);
        } else {
            mBackground = null;
        }


        mLongPressHelper = new CheckLongPressHelper(this, new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (clickLongType != 1 && LauncherModel.CHANGESTATUESTYPE == 1) {
                    textView.setText("");
                }
                return false;
            }
        });
        mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);

        mOutlineHelper = HolographicOutlineHelper.obtain(getContext());


        imageView = new ImageView(getContext());
        imageView.setImageResource(R.mipmap.dj_delete);
        addView(imageView);
        LayoutParams layoutParamsImage = (LayoutParams) imageView.getLayoutParams();
        layoutParamsImage.gravity = Gravity.RIGHT | Gravity.TOP;
        layoutParamsImage.width = ScreenUtils.dip2px(getContext(), 24);
        layoutParamsImage.height = ScreenUtils.dip2px(getContext(), 24);
        if (LauncherModel.CHANGESTATUESTYPE == 1) {
            imageView.setVisibility(VISIBLE);
        } else {
            imageView.setVisibility(GONE);
        }

        imageView.setLayoutParams(layoutParamsImage);


        textView2 = new TextView(getContext());
        textView2.setTextColor(Color.BLACK);
        textView2.setTextSize(12);
        textView2.setText("CCT");
        textView2.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(textView2);
        LayoutParams layoutParams2 = (LayoutParams) textView2.getLayoutParams();
        layoutParams2.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams2.topMargin = ScreenUtils.dip2px(getContext(), 6) + offtextViewY;
        textView2.setLayoutParams(layoutParams2);

    }


    //设置字体格式
    public void setTextStyle(TextView textView, int display, DeviceProfile grid) {
        LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        offtextViewY = ScreenUtils.dip2px(getContext(), 10);
        layoutParams.topMargin = offtextViewY;
        textView.setLayoutParams(layoutParams);


        if (display == DISPLAY_WORKSPACE) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, grid.iconTextSizePx);
        } else if (display == DISPLAY_ALL_APPS) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, grid.allAppsIconTextSizePx);
            textView.setCompoundDrawablePadding(grid.allAppsIconDrawablePaddingPx);
        } else if (display == DISPLAY_FOLDER) {
            textView.setCompoundDrawablePadding(grid.folderChildDrawablePaddingPx);
        }
    }

    public void setClickLongType(int clickLongType) {
        this.clickLongType = clickLongType;
        if (clickLongType == 1) {
            imageView.setVisibility(GONE);
        }
    }

    public void applyFromShortcutInfo(ShortcutInfo info) {
        applyFromShortcutInfo(info, false);
    }

    public void applyFromShortcutInfo(ShortcutInfo info,
                                      boolean promiseStateChanged) {
        BorderBitmapDrawable gradientDrawable;
        if (TextUtils.isEmpty(info.color)) {
            int[] colors = new int[]{Color.parseColor("#FF4D4D"), Color.parseColor("#3333FF")};
            gradientDrawable = new BorderBitmapDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        } else {
            gradientDrawable = new BorderBitmapDrawable();
            int colorparse = Color.parseColor(info.color);
            //设置背景色
            gradientDrawable.setColor(colorparse);

        }

        //是否黑场
        if (info.isBlack) {
            int colorparse = Color.BLACK;
            gradientDrawable.setColor(colorparse);
        }

        //设置边框的宽度以及边框的颜色
        gradientDrawable.setStroke(2, Color.TRANSPARENT);
        //设置圆角的半径
        gradientDrawable.setCornerRadius(20);
        //设置shape形状
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setSize(mIconSize, mIconSize);
        applyIconAndLabel(gradientDrawable, info);
        setTag(info);
        if (promiseStateChanged || info.isPromise()) {
            applyState(promiseStateChanged);
        }

    }


    private void applyIconAndLabel(Drawable icon, ItemInfo info) {
        setIcon(icon);

        //biekangdong 单元名字
        SpannableString spannableString1;
        if (TextUtils.isEmpty(info.title)) {
            spannableString1 = new SpannableString("未命名");
        } else {
            spannableString1 = new SpannableString(info.title);
        }


        String lightName = info.LightName;
        String lightInfo = lightName;
        spannableString1.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")), 0, spannableString1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString1.setSpan(new AbsoluteSizeSpan(12, true), 0, spannableString1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString spannableString2 = new SpannableString(lightInfo);
        spannableString2.setSpan(new ForegroundColorSpan(Color.parseColor("#8E8E8E")), 0, spannableString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString2.setSpan(new AbsoluteSizeSpan(10, true), 0, spannableString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(spannableString1).append("\n").append(spannableString2);
        textView.setText(spannableStringBuilder);


        int modeTextColor = Color.BLACK;
        //是否黑场
        if (info.isBlack || info.color.equals("#000000")) {
            modeTextColor = Color.WHITE;
        } else {
            modeTextColor = Color.BLACK;
        }

        String MODE = info.MODE;
        textView2.setText(MODE);
        textView2.setTextColor(modeTextColor);


        if (mIcon instanceof BorderBitmapDrawable) {
            isBorderShow = !isBorderShow;
            BorderBitmapDrawable d = (BorderBitmapDrawable) mIcon;
            if (info.isSelected) {
                d.animateState(BorderBitmapDrawable.State.PRESSED);
            } else {
                d.animateState(BorderBitmapDrawable.State.NORMAL);
            }
        }

    }


    /**
     * Overrides the default long press timeout.
     */
    public void setLongPressTimeout(int longPressTimeout) {
        mLongPressHelper.setLongPressTimeout(longPressTimeout);
    }


    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mBackground || super.verifyDrawable(who);
    }

    @Override
    public void setTag(Object tag) {
        if (tag != null) {
            LauncherModel.checkItemInfo((ItemInfo) tag);
        }
        super.setTag(tag);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);

        if (!mIgnorePressedStateChange) {
            updateIconState();
        }
    }

    /**
     * Returns the icon for this view.
     */
    public Drawable getIcon() {
        return mIcon;
    }


    private void updateIconState() {
        if (mIcon instanceof FastBitmapDrawable) {
            //FastBitmapDrawable d =  new FastBitmapDrawable(bitmap);
            FastBitmapDrawable d = (FastBitmapDrawable) mIcon;
            if (getTag() instanceof ItemInfo
                    && ((ItemInfo) getTag()).isDisabled()) {
                d.animateState(FastBitmapDrawable.State.DISABLED);
            } else if (isPressed() || mStayPressed) {
                d.animateState(FastBitmapDrawable.State.PRESSED);
            } else {
                d.animateState(FastBitmapDrawable.State.NORMAL);
            }
        }
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        super.setOnLongClickListener(l);
        mOnLongClickListener = l;
    }

    public OnLongClickListener getOnLongClickListener() {
        return mOnLongClickListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Call the superclass onTouchEvent first, because sometimes it changes the state to
        // isPressed() on an ACTION_UP
        boolean result = super.onTouchEvent(event);

        // Check for a stylus button press, if it occurs cancel any long press checks.
        if (mStylusEventHelper.onMotionEvent(event)) {
            mLongPressHelper.cancelLongPress();
            result = true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("onTouchEvent", "ACTION_DOWN");
                if (LauncherModel.CHANGESTATUESTYPE == 1) {//管理模式
                    mDeleteTargeted = mTouchDeleteBounds.contains(event.getX(), event.getY());
                }

                // So that the pressed outline is visible immediately on setStayPressed(),
                // we pre-create it on ACTION_DOWN (it takes a small but perceptible amount of time
                // to create it)
                if (!mDeferShadowGenerationOnTouch && mPressedBackground == null) {
                    mPressedBackground = mOutlineHelper.createMediumDropShadow(this);
                }

                // If we're in a stylus button press, don't check for long press.
                if (!mStylusEventHelper.inStylusButtonPressed()) {
                    mLongPressHelper.postCheckForLongPress();
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //mDeleteTargeted=false;
                Log.e("onTouchEvent", "ACTION_UP");
                textView.setText(spannableStringBuilder);
                // If we've touched down and up on an item, and it's still not "pressed", then
                // destroy the pressed outline
                if (!isPressed()) {
                    mPressedBackground = null;
                }

                mLongPressHelper.cancelLongPress();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!Utilities.pointInView(this, event.getX(), event.getY(), mSlop)) {
                    mLongPressHelper.cancelLongPress();
                }
                break;
        }
        return result;
    }

    void setStayPressed(boolean stayPressed) {
        mStayPressed = stayPressed;
        if (!stayPressed) {
            HolographicOutlineHelper.obtain(getContext()).recycleShadowBitmap(mPressedBackground);
            mPressedBackground = null;
        } else {
            if (mPressedBackground == null) {
                mPressedBackground = mOutlineHelper.createMediumDropShadow(this);
            }
        }

        // Only show the shadow effect when persistent pressed state is set.
        ViewParent parent = getParent();
        if (parent != null && parent.getParent() instanceof BubbleTextShadowHandler) {
            ((BubbleTextShadowHandler) parent.getParent()).setPressedIcon(
                    this, mPressedBackground);
        }

        updateIconState();
    }

    void clearPressedBackground() {
        setPressed(false);
        setStayPressed(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (super.onKeyDown(keyCode, event)) {
            // Pre-create shadow so show immediately on click.
            if (mPressedBackground == null) {
                mPressedBackground = mOutlineHelper.createMediumDropShadow(this);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // Unlike touch events, keypress event propagate pressed state change immediately,
        // without waiting for onClickHandler to execute. Disable pressed state changes here
        // to avoid flickering.
        mIgnorePressedStateChange = true;
        boolean result = super.onKeyUp(keyCode, event);

        mPressedBackground = null;
        mIgnorePressedStateChange = false;
        updateIconState();
        return result;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!mCustomShadowsEnabled) {
            super.draw(canvas);
            return;
        }

        final Drawable background = mBackground;
        if (background != null) {
            final int scrollX = getScrollX();
            final int scrollY = getScrollY();

            if (mBackgroundSizeChanged) {
                background.setBounds(0, 0, getRight() - getLeft(), getBottom() - getTop());
                mBackgroundSizeChanged = false;
            }

            if ((scrollX | scrollY) == 0) {
                background.draw(canvas);
            } else {
                canvas.translate(scrollX, scrollY);
                background.draw(canvas);
                canvas.translate(-scrollX, -scrollY);
            }
        }

        // If text is transparent, don't draw any shadow
        if (textView.getCurrentTextColor() == getResources().getColor(android.R.color.transparent)) {
            textView.getPaint().clearShadowLayer();
            super.draw(canvas);
            return;
        }

        // We enhance the shadow by drawing the shadow twice
        float density = getResources().getDisplayMetrics().density;
        textView.getPaint().setShadowLayer(density * AMBIENT_SHADOW_RADIUS, 0, 0, AMBIENT_SHADOW_COLOR);
        super.draw(canvas);
        canvas.save();
        canvas.clipRect(getScrollX(), getScrollY() + textView.getExtendedPaddingTop(),
                getScrollX() + getWidth(),
                getScrollY() + getHeight(), Region.Op.INTERSECT);
        textView.getPaint().setShadowLayer(
                density * KEY_SHADOW_RADIUS, 0.0f, density * KEY_SHADOW_OFFSET, KEY_SHADOW_COLOR);
        super.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mBackground != null) mBackground.setCallback(this);

        if (mIcon instanceof PreloadIconDrawable) {
            ((PreloadIconDrawable) mIcon).applyPreloaderTheme(getPreloaderTheme());
        }
        mSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }


    public TextView getTextView() {
        return textView;
    }


    public TextView getTextView2() {
        return textView2;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Paint.FontMetrics fm = textView.getPaint().getFontMetrics();
        int cellHeightPx = mIconSize + textView.getCompoundDrawablePadding() +
                (int) Math.ceil(fm.bottom - fm.top);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setPadding(getPaddingLeft(), 0, getPaddingRight(),
                getPaddingBottom());
//        setPadding(getPaddingLeft(), (height - cellHeightPx) / 2, getPaddingRight(),
//                getPaddingBottom());
        LayoutParams layoutParamsImage = (LayoutParams) imageView.getLayoutParams();
        layoutParamsImage.rightMargin = (int) ((width - mIconSize) / 2f - ScreenUtils.dip2px(getContext(), 12));

        float padding = (width - mIconSize) / 2f;
        mTouchDeleteBounds.set(padding + mIconSize - ScreenUtils.dip2px(mDeviceDJMainActivity, 12), 0, padding + mIconSize + ScreenUtils.dip2px(mDeviceDJMainActivity, 12), ScreenUtils.dip2px(mDeviceDJMainActivity, 24));


        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBackground != null) mBackground.setCallback(null);
    }

//    public void setTextColor(int color) {
//        mTextColor = color;
//        textView.setTextColor(color);
//    }
//
//
//    public void setTextColor(ColorStateList colors) {
//        mTextColor = colors.getDefaultColor();
//        textView.setTextColor(colors);
//    }

    //biekangdong 控制可见
    public void setTextVisibility(boolean visible) {
        Resources res = getResources();
        if (visible) {
            //textView.setTextColor(mTextColor);
        } else {
            textView.setTextColor(res.getColor(android.R.color.transparent));
        }
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();

        mLongPressHelper.cancelLongPress();
    }

    public void applyState(boolean promiseStateChanged) {
        if (getTag() instanceof ShortcutInfo) {
            ShortcutInfo info = (ShortcutInfo) getTag();
//            final boolean isPromise = info.isPromise();
//            final int progressLevel = isPromise ?
//                    ((info.hasStatusFlag(ShortcutInfo.FLAG_INSTALL_SESSION_ACTIVE) ?
//                            info.getInstallProgress() : 0)) : 100;
//
//            setContentDescription(progressLevel > 0 ?
//                    getContext().getString(R.string.app_downloading_title, info.title,
//                            NumberFormat.getPercentInstance().format(progressLevel * 0.01)) :
//                    getContext().getString(R.string.app_waiting_download_title, info.title));

//            if (mIcon != null) {
//                final PreloadIconDrawable preloadDrawable;
//                if (mIcon instanceof PreloadIconDrawable) {
//                    preloadDrawable = (PreloadIconDrawable) mIcon;
//                } else {
//                    preloadDrawable = new PreloadIconDrawable(mIcon, getPreloaderTheme());
//                    setIcon(preloadDrawable);
//                }
//
//                preloadDrawable.setLevel(progressLevel);
//                if (promiseStateChanged) {
//                    preloadDrawable.maybePerformFinishedAnimation();
//                }
//            }

            imageView.setVisibility(GONE);
            switch (info.getStatuesType()) {//0，正常 1，管理 2，编辑
                case 0:

                    break;
                case 1:
                    imageView.setVisibility(VISIBLE);
                    break;
                case 2:
                    break;
            }
        }
    }

    private Theme getPreloaderTheme() {
        Object tag = getTag();
        int style = ((tag != null) && (tag instanceof ShortcutInfo) &&
                (((ShortcutInfo) tag).container >= 0)) ? com.android.launcher3.R.style.PreloadIcon_Folder
                : com.android.launcher3.R.style.PreloadIcon;
        Theme theme = sPreloaderThemes.get(style);
        if (theme == null) {
            theme = getResources().newTheme();
            theme.applyStyle(style, true);
            sPreloaderThemes.put(style, theme);
        }
        return theme;
    }

    /**
     * Sets the icon for this view based on the layout direction.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setIcon(Drawable icon) {
        mIcon = icon;
        if (mIconSize != -1) {
            mIcon.setBounds(0, 0, mIconSize, mIconSize);
        }
        applyCompoundDrawables(mIcon);
    }

    protected void applyCompoundDrawables(Drawable icon) {
        if (mLayoutHorizontal) {
            if (Utilities.ATLEAST_JB_MR1) {
                textView.setCompoundDrawablesRelative(icon, null, null, null);
            } else {
                textView.setCompoundDrawables(icon, null, null, null);
            }
        } else {
            textView.setCompoundDrawables(null, icon, null, null);
        }
    }

    @Override
    public void requestLayout() {
        if (!mDisableRelayout) {
            super.requestLayout();
        }
    }


    /**
     * Interface to be implemented by the grand parent to allow click shadow effect.
     */
    public interface BubbleTextShadowHandler {
        void setPressedIcon(BubbleTextView icon, Bitmap background);
    }


    public boolean ismDeleteTargeted() {
        return mDeleteTargeted;
    }

}
