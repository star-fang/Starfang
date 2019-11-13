package com.fang.starfang.view.recycler;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;

public class DiagonalScrollRecyclerView extends HorizontalScrollView {
    private static final String TAG = "FANG_DSR";

    private RecyclerView recyclerView;
    private float downX = 0f;
    private float downY = 0;
    private float furthestDistanceMovedPx = 0f;

    public DiagonalScrollRecyclerView(Context context) {
        super(context);
        constructViews(context);
    }

    public DiagonalScrollRecyclerView(Context context, AttributeSet attrs ) {
        super(context, attrs);
        constructViews(context);
    }

    private void constructViews(Context context) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        recyclerView = (RecyclerView)LayoutInflater.from(context).inflate(R.layout.recycler_view_hero_floating,this,false);
        setLayoutParams(params);
        addView(recyclerView);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if( this.getChildCount() != 0 ) {
            super.onTouchEvent((event));
            recyclerView.onTouchEvent(event);
        }

        switch( event.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if (recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() > 0 && isClick(furthestDistanceMovedPx)) {

                    if(recyclerView.getAdapter() instanceof CoordinatesClickListener) {
                        ((CoordinatesClickListener) recyclerView.getAdapter()).clickCoordinates(event.getRawX(),event.getRawY());
                    } else {
                        try {
                            View v = findChildAtLocation(recyclerView, (int) event.getRawX(), (int) event.getRawY());
                            v.performClick();
                        } catch (Exception e) {
                            Log.d(TAG, "onTouch : " + e.getMessage());
                        }
                    }
                    furthestDistanceMovedPx = 0f;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float distanceFromStart = pxDistance(event.getRawX(), event.getRawY(), downX, downY);
                if (distanceFromStart > furthestDistanceMovedPx) {
                    furthestDistanceMovedPx = distanceFromStart;
                }
                break;
                default:
        }
        return true;
    }


    View findChildAtLocation(ViewGroup v, int x, int y) {
        for(int i = 0; i < v.getChildCount(); i++) {
            int[] childCroods = new int[2];
            View child = v.getChildAt(i);

            if(child != null) {
                child.getLocationOnScreen(childCroods);
                Rect childArea = new Rect(childCroods[0],childCroods[1],
                        childCroods[0] + child.getWidth(),
                        childCroods[1] + child.getHeight());
                if(childArea.contains(x,y)) {
                    if( child instanceof ViewGroup) {
                        child = findChildAtLocation((ViewGroup)child, x, y);
                    }
                    return child;
                }
            }
        }
        return null;
    }

    interface  CoordinatesClickListener {
        void clickCoordinates(float x, float y);
    }

    public void setRecyclerViewLayoutManager( RecyclerView.LayoutManager manager ) {
        recyclerView.setLayoutManager(manager);
    }

    public void setRecyclerViewAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    public RecyclerView getRecyclerView(){
        return this.recyclerView;
    }

    private float pxToDP( float px ) {
        return px / getResources().getDisplayMetrics().density;
    }

    private float pxDistance( float x1, float y1, float x2, float y2 ) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float)Math.sqrt((double)(dx * dx + dy * dy));
    }

    private float dpDistance( float x1, float y1, float x2, float y2) {
        float distanceInPx = pxDistance(x1, y1, x2, y2);
        return (float)((int)pxToDP(distanceInPx));
    }
    private boolean isClick(float furthestDistanceMovedPx) {
        return pxToDP(furthestDistanceMovedPx) < 15;
    }
}
