package com.g00fy2.lolquiz.util;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

/**
 * Created by thoma on 04.12.2016.
 */

public class ProgressBarAnimation extends Animation {
    private ProgressBar progressBar;
    private int from;
    private int  to;

    public ProgressBarAnimation(ProgressBar progressBar, int from, int to) {
        super();
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
    }
}
