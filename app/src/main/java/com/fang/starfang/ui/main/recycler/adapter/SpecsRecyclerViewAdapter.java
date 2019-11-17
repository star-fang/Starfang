package com.fang.starfang.ui.main.recycler.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;

public class SpecsRecyclerViewAdapter extends RecyclerView.Adapter<SpecsRecyclerViewAdapter.SpecsRecyclerViewAdapterViewHolder>
implements Filterable {

    private static final String TAG = "FANG_SPEC_ADAPTER";
    private ArrayList<String> titles;
    private ArrayList<String> titlesFiltered;
    private ArrayList<String> specs;
    private ArrayList<String> specsFiltered;
    private ArrayList<String> specVals;
    private ArrayList<String> specValsFiltered;
    private boolean pasv;

    @NonNull
    @Override
    public SpecsRecyclerViewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_heroes_cell_spec_cell,viewGroup,false);
        return new SpecsRecyclerViewAdapter.SpecsRecyclerViewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecsRecyclerViewAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return titlesFiltered.size();
    }


    public SpecsRecyclerViewAdapter(ArrayList<String> titles, ArrayList<String> specs
            ,ArrayList<String> specVals , boolean pasv) {

        this.titles = titles;
        this.titlesFiltered = titles;
        this.specs = specs;
        this.specsFiltered = specs;
        this.specVals = specVals;
        this.specValsFiltered = specVals;
        this.pasv = pasv;

        Log.d(TAG, "SpecsRecyclerViewAdapter constructed");
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String csStr = charSequence.toString();
                int cs = NumberUtils.toInt(csStr,0);
                //Log.d(TAG,"CS: " + cs);
                if( cs == 0 ) {
                    return null;
                }

                ArrayList<Integer> results = new ArrayList<>();

                for( int i = 0; i < titles.size(); i++ ) {
                    String titleStr = titles.get(i);
                    //Log.d(TAG,"titleStr: " + titleStr);
                    int titleValue = NumberUtils.toInt(titleStr.replaceAll("[^0-9]", ""),0);
                    //Log.d(TAG,"titleValue: " + titleValue);


                    if( titleValue <= cs ) {
                        results.add(i);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = results;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if( filterResults == null) {
                    Log.d(TAG,"FILTER RESULT IS NULL");
                    return;
                }

                titlesFiltered = new ArrayList<>();
                specsFiltered = new ArrayList<>();
                specValsFiltered = new ArrayList<>(); // do not clear()

                Object values = filterResults.values;
                if( values instanceof ArrayList<?>) {
                    ArrayList<?> al = (ArrayList<?>)values;
                    if(al.size()>0) {
                        for(Object o : al) {
                            if(o  instanceof Integer) {
                                Integer integer = (Integer) o;
                                titlesFiltered.add(titles.get(integer));
                                specsFiltered.add(specs.get(integer));
                                specValsFiltered.add(specVals.get(integer));
                            }
                        }
                    }
                }

                notifyDataSetChanged();
            }
        };
    }






    class SpecsRecyclerViewAdapterViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView text_title_pasv_spec_grade;
        private AppCompatTextView text_pasv_spec_grade;
        private AppCompatTextView text_pasv_spec_val_grade;

        private SpecsRecyclerViewAdapterViewHolder(View itemView) {
            super(itemView);
            text_title_pasv_spec_grade = itemView.findViewById(R.id.text_title_pasv_spec_grade);
            text_pasv_spec_grade = itemView.findViewById(R.id.text_pasv_spec_grade);
            text_pasv_spec_val_grade = itemView.findViewById(R.id.text_pasv_spec_val_grade);
        }

        private void bind(int position) {
            text_title_pasv_spec_grade.setText(titlesFiltered.get(position));
            text_pasv_spec_grade.setText(specsFiltered.get(position));
            text_pasv_spec_val_grade.setText(specValsFiltered.get(position));
        }
    }

}
