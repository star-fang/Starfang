package com.fang.starfang.ui.creative;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MovableFloatingActionButton extends FloatingActionButton implements View.OnTouchListener {

    private final static float CLICK_DRAG_TOLERANCE = 10; // Often, there will be a slight, unintentional, drag when the user taps the FAB, so we need to account for this.

    //private View parent;
    private float downRawX, downRawY;
    private float dX, dY;

    public MovableFloatingActionButton(Context context) {
        super(context);
        init();
    }

    public MovableFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MovableFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent){

        View viewParent = (View)view.getParent();

        int action = motionEvent.getAction();
        if (action == MotionEvent.ACTION_DOWN) {

            downRawX = motionEvent.getRawX();
            downRawY = motionEvent.getRawY();
            dX = viewParent.getX() - downRawX;
            dY = viewParent.getY() - downRawY;
            return true; // Consumed
        }
        else if (action == MotionEvent.ACTION_MOVE) {

            //int viewWidth = view.getWidth();
            //int viewHeight = view.getHeight();

            int parentWidth = viewParent.getWidth();
            int parentHeight = viewParent.getHeight();

            View viewGrandParent = (View)viewParent.getParent();
            int grandWidth = viewGrandParent.getWidth();
            int grandHeight = viewGrandParent.getHeight();

            //ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
            ViewGroup.MarginLayoutParams parentLayoutParams = (ViewGroup.MarginLayoutParams)viewParent.getLayoutParams();

            float newX = motionEvent.getRawX() + dX;
            newX = Math.max(parentLayoutParams.leftMargin, newX); // Don't allow the FAB past the left hand side of the parent
            newX = Math.min(grandWidth - parentWidth - parentLayoutParams.rightMargin, newX); // Don't allow the FAB past the right hand side of the parent

            float newY = motionEvent.getRawY() + dY;
            newY = Math.max(parentLayoutParams.topMargin, newY); // Don't allow the FAB past the top of the parent
            newY = Math.min(grandHeight - parentHeight - parentLayoutParams.bottomMargin, newY); // Don't allow the FAB past the bottom of the parent

            viewParent.animate()
                    .x(newX)
                    .y(newY)
                    .setDuration(0)
                    .start();

            return true; // Consumed

        }
        else if (action == MotionEvent.ACTION_UP) {

            float upRawX = motionEvent.getRawX();
            float upRawY = motionEvent.getRawY();

            float upDX = upRawX - downRawX;
            float upDY = upRawY - downRawY;

            if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE) { // A click
                return performClick();
            }
            else { // A drag
                return true; // Consumed
            }

        }
        else {
            return super.onTouchEvent(motionEvent);
        }

    }

}