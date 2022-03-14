package com.fang.starfang.ui.main.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.ui.creative.DynamicFilter;

import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.realm.DynamicRealmObject;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

public class DynamicRealmRecyclerAdapter extends RealmRecyclerViewAdapter<DynamicRealmObject, RecyclerView.ViewHolder> implements Filterable {

    private final String TAG = "FANG_ADPT_DYNM";
    private FragmentManager fragmentManager;
    private List metadata;


    public DynamicRealmRecyclerAdapter(
            OrderedRealmCollection<DynamicRealmObject> heroCollection
            , FragmentManager fragmentManager, List metadata) {
        super(heroCollection, false);
        this.fragmentManager = fragmentManager;
        this.metadata = metadata;

        Log.d(TAG, "constructed");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LinearLayout linearLayout = new LinearLayout(viewGroup.getContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        return new DynamicViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        DynamicViewHolder heroesViewHolder = (DynamicViewHolder) viewHolder;

        DynamicRealmObject dynamicRealmObject = getItem(i);
        heroesViewHolder.bind(dynamicRealmObject);
    }

    public void sort(ArrayList<Pair<String, Sort>> sortPairs) {
        OrderedRealmCollection<DynamicRealmObject> realmCollection = this.getData();
        for (Pair<String, Sort> pair : sortPairs) {
            String cs = pair.first;
            Sort sort = pair.second;
            if (realmCollection != null && cs != null && sort != null) {
                realmCollection = realmCollection.sort(cs, sort);
            }

        }
        updateData(realmCollection);
    }

    @Override
    public Filter getFilter() {
        return new DynamicFilter();
    }

    private class DynamicViewHolder extends RecyclerView.ViewHolder {

        private List<Pair<String, AppCompatTextView>> metaPairList;

        private DynamicViewHolder(View itemView) {
            super(itemView);

            if (metadata instanceof LinkedList<?>
                    && itemView instanceof LinearLayout) {
                LinearLayout layout = (LinearLayout)itemView;
                metaPairList = new LinkedList<>();
                for (Object metaObject : (LinkedList<?>) metadata) {
                    if( metaObject instanceof Triple ) {
                        Triple tripleMeta = (Triple)metaObject;
                        String fieldName = (String)tripleMeta.getLeft();

                        AppCompatTextView textView = new AppCompatTextView(itemView.getContext());
                        textView.setId(metaPairList.size());
                        textView.setWidth((Integer)tripleMeta.getRight());

                        metaPairList.add(new Pair<>(fieldName, textView));
                        layout.addView(textView);
                    }
                }
            }
        }

        private void bind(final DynamicRealmObject dynamicRealmObject) {
            for( Pair<String, AppCompatTextView> metaPair : metaPairList ) {
                String fieldName = metaPair.first;
                AppCompatTextView textView = metaPair.second;
                if( fieldName != null && textView != null) {
                    String text = dynamicRealmObject.get(fieldName);
                    textView.setText(text);
                }
            }

        }
    }

}
