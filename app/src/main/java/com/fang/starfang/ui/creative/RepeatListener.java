package com.fang.starfang.ui.creative;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class RepeatListener implements View.OnTouchListener {

    private Handler handler = new Handler();

    private int initialInterval;
    private final int normaInterval;
    private final View.OnClickListener clickListener;
    private View touchedView;

    private Runnable handlerRunnable = new Runnable() {
        @Override
        public void run() {
            if (touchedView.isEnabled()) {
                handler.postDelayed(this, normaInterval);
                clickListener.onClick(touchedView);
            } else {
                handler.removeCallbacks(handlerRunnable);
                touchedView.setPressed(false);
                touchedView = null;
            }
        }
    };

    public RepeatListener(int initialInterval, int normaInterval,
                          View.OnClickListener clickListener) {
        if (clickListener == null) {
            throw new IllegalArgumentException("null runnable");
        }
        if (initialInterval < 0 || normaInterval < 0) {
            throw new IllegalArgumentException("negative interval");
        }

        this.initialInterval = initialInterval;
        this.normaInterval = normaInterval;
        this.clickListener = clickListener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.removeCallbacks(handlerRunnable);
                handler.postDelayed(handlerRunnable, initialInterval);
                touchedView = view;
                touchedView.setPressed(true);
                clickListener.onClick(view);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handler.removeCallbacks(handlerRunnable);
                touchedView.setPressed(false);
                touchedView = null;
                return true;
        }

        return false;
    }
}
