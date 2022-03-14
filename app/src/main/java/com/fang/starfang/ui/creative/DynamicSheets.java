package com.fang.starfang.ui.creative;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.ui.main.adapter.DynamicRealmRecyclerAdapter;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.realm.DynamicRealmObject;
import io.realm.OrderedRealmCollection;


public class DynamicSheets extends LinearLayout {

    private final static String TAG = "FANG_SHEET";

    private enum ff {FIXED, FLOATING}

    private final static int fCount = 2;
    public final static int I_FIXED = 0;
    public final static int I_FLOATING = 1;

    private LinearLayout[] parts; // Object > View > ViewGroup > LinearLayout
    private LinearLayout[] columns;
    private DiagonalScrollRecyclerView[] tuples_diagonal;
    //private RecyclerView[] tuples_recycler;
    private List[] columnMetas;
    private Context mContext;

    public DynamicSheets(Context context) {
        super(context);
        this.mContext = context;
        buildSheets();
        //Log.d(TAG,"constructed");
    }

    public DynamicSheets(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        buildSheets();
        //Log.d(TAG,"constructed with attrs");
    }

    private void buildSheets() {
        this.setOrientation(LinearLayout.HORIZONTAL);
        parts = new LinearLayout[fCount];
        columns = new LinearLayout[fCount];
        tuples_diagonal = new DiagonalScrollRecyclerView[fCount];
        RecyclerView[] tuples_recycler = new RecyclerView[fCount];
        columnMetas = new LinkedList[fCount];

        for (ff f : ff.values()) {
            int i = f.ordinal();
            parts[i] = new LinearLayout(mContext);
            parts[i].setOrientation(LinearLayout.VERTICAL);

            this.addView(parts[i]);

            columns[i] = new LinearLayout(mContext);
            columns[i].setOrientation(LinearLayout.HORIZONTAL);
            columns[i].setLayoutParams( new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            ));
            parts[i].addView(columns[i]);

            tuples_diagonal[i] = new DiagonalScrollRecyclerView(mContext);
            tuples_diagonal[i].setLayoutParams( new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT
            ));
            tuples_recycler[i] =  new RecyclerView(mContext);
            tuples_recycler[i].setLayoutParams( new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT
            ));
            tuples_diagonal[i].setRecyclerView(tuples_recycler[i]);
            tuples_diagonal[i].setRecyclerLayoutManager(new LinearLayoutManager(mContext));
            parts[i].addView(tuples_diagonal[i]);

        }

        parts[I_FIXED].setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, // width
                LayoutParams.MATCH_PARENT)); // height

        parts[I_FLOATING].setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        synchronize( tuples_recycler[ff.FIXED.ordinal()], tuples_recycler[ff.FLOATING.ordinal()]);

        this.invalidate();
        Log.d(TAG, "build complete");
    }

    private void synchronize(final RecyclerView fixed, final RecyclerView floating) {
        fixed.clearOnScrollListeners();
        floating.clearOnScrollListeners();

        final RecyclerView.OnScrollListener[] heroRecyclerViewListeners =
                new RecyclerView.OnScrollListener[2];
        heroRecyclerViewListeners[0] = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                fixed.removeOnScrollListener(heroRecyclerViewListeners[1]);
                fixed.scrollBy(0, dy);
                fixed.addOnScrollListener(heroRecyclerViewListeners[1]);
            }

        };

        heroRecyclerViewListeners[1] = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                floating.removeOnScrollListener(heroRecyclerViewListeners[0]);
                floating.scrollBy(0, dy);
                floating.addOnScrollListener(heroRecyclerViewListeners[0]);
            }
        };

        floating.addOnScrollListener(heroRecyclerViewListeners[0]);
        fixed.addOnScrollListener(heroRecyclerViewListeners[1]);
    }

    public void setAdapter(RecyclerView.Adapter adapter, int i ) {
        tuples_diagonal[i].setRecyclerAdapter(adapter);

        Log.d(TAG, "adapters set");
    }


    public DynamicSheets insertColumnInfo(String[] id, String[] text, int[] width, int index) {
        List<Triple<String, String, Integer>> metaList = new LinkedList<>();
        for (int i = 0; i < id.length; i++) {
            Triple<String, String, Integer> metadata = new ImmutableTriple<>(id[i], text[i], width[i]);
            metaList.add(metadata);
            columnMetas[index] = metaList;
        }
        return this;
    }

    public void innerSwapColumn(String id1, String id2, int index) {
        List metaList = columnMetas[index];
        Triple triple1 = null;
        Triple triple2 = null;
        if (metaList instanceof LinkedList<?>) {
            for (Object metaObj : (LinkedList<?>) metaList) {
                if (metaObj instanceof Triple) {
                    Triple tripleObj = (Triple) metaObj;
                    Object idObj = tripleObj.getLeft();
                    if (idObj.equals(id1)) {
                        triple1 = tripleObj;
                    } else if (idObj.equals(id2)) {
                        triple2 = tripleObj;
                    }
                }
            }
        }

        if (triple1 != null && triple2 != null) {
            Collections.swap(
                    columnMetas[index]
                    , metaList.indexOf(triple1)
                    , metaList.indexOf(triple2));
        }
    }

    private boolean hasNoParent(@NonNull View child, View parent) {
        ViewParent viewParent = child.getParent();
        if (viewParent instanceof View) {
            return ((View) viewParent).getId() != parent.getId();
        }
        return true;
    }

    public void changeTuple(DiagonalScrollRecyclerView tuple, int index) {
        this.tuples_diagonal[index] = tuple;
    }





}
