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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.animation.DecelerateInterpolator;

public class BorderBitmapDrawable extends GradientDrawable {

    /**
     * The possible states that a FastBitmapDrawable can be in.
     */
    public enum State {

        NORMAL                      (new DecelerateInterpolator()),
        PRESSED                     ( CLICK_FEEDBACK_INTERPOLATOR);


        public final TimeInterpolator interpolator;

        State( TimeInterpolator interpolator) {
            this.interpolator = interpolator;
        }
    }

    public static final TimeInterpolator CLICK_FEEDBACK_INTERPOLATOR = new TimeInterpolator() {

        @Override
        public float getInterpolation(float input) {
            if (input < 0.05f) {
                return input / 0.05f;
            } else if (input < 0.3f){
                return 1;
            } else {
                return (1 - input) / 0.7f;
            }
        }
    };


    private State mState = State.NORMAL;


    private ValueAnimator valueAnimator;

    public BorderBitmapDrawable() {
        super();
    }

    public BorderBitmapDrawable(Orientation orientation, int[] colors) {
        super(orientation, colors);
    }


    @Override
    public int getMinimumWidth() {
        return getBounds().width();
    }

    @Override
    public int getMinimumHeight() {
        return getBounds().height();
    }


    /**
     * Animates this drawable to a new state.
     *
     * @return whether the state has changed.
     */
    @SuppressLint("WrongConstant")
    public boolean animateState(State newState) {
        if (mState != newState) {
            mState = newState;

            if(mState== State.PRESSED){
                valueAnimator = ValueAnimator.ofFloat(0f, 1f);
                valueAnimator.setDuration(1000);
                valueAnimator.start();
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float curValue = (float) animation.getAnimatedValue();
                        if (curValue >= 0.8f) {
                            //设置边框的宽度以及边框的颜色
                            String strokeColor = "#2CCE38";
                            setStroke(2, Color.parseColor(strokeColor));
                        }

                        if (curValue <= 0.2f) {
                            setStroke(2, Color.TRANSPARENT);
                        }
                    }
                });
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        valueAnimator.setStartDelay(1000);
                        valueAnimator.start();

                    }
                });
            }else if(mState== State.NORMAL){
                cancelAnimator(valueAnimator);
                setStroke(2, Color.TRANSPARENT);
            }

            return true;
        }
        return false;
    }

    /**
     * Immediately sets this drawable to a new state.
     *
     * @return whether the state has changed.
     */
    public boolean setState(State newState) {
        if (mState != newState) {
            mState = newState;

            valueAnimator = cancelAnimator(valueAnimator);
            return true;
        }
        return false;
    }

    /**
     * Returns the current state.
     */
    public State getCurrentState() {
        return mState;
    }


    private ValueAnimator cancelAnimator(ValueAnimator animator) {
        if (animator != null) {
            animator.removeAllListeners();
            animator.cancel();
        }
        return null;
    }
}
