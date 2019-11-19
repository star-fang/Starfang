package com.fang.starfang.ui.main.recycler.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.HeroSim;

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
    private ArrayList<Integer> checkedLevels;
    private boolean pasv;
    private TextView resultView;
    private static final int[] RESTRICT_PICK_SPECS_BY_GRADE = {1,1,2,2,3};


    @NonNull
    @Override
    public SpecsRecyclerViewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_heroes_cell_spec,viewGroup,false);
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
            ,ArrayList<String> specVals, ArrayList<Integer> checkedLevels , boolean pasv, TextView resultView) {

        this.titles = titles;
        this.titlesFiltered = titles;
        this.specs = specs;
        this.specsFiltered = specs;
        this.specVals = specVals;
        this.specValsFiltered = specVals;
        this.pasv = pasv;
        this.checkedLevels = checkedLevels;
        this.resultView = resultView;

        Log.d(TAG, "constructed");
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
        private AppCompatTextView text_cell_title_spec;
        private AppCompatCheckedTextView button_cell_spec;

        private SpecsRecyclerViewAdapterViewHolder(View itemView) {
            super(itemView);
            text_cell_title_spec = itemView.findViewById(R.id.text_cell_title_spec);
            button_cell_spec = itemView.findViewById(R.id.button_cell_spec);
        }

        private void bind(final int position) {
            final String titleStr = titlesFiltered.get(position);
            text_cell_title_spec.setText(titleStr);
            String buttonStr = specsFiltered.get(position) + "\n" + specValsFiltered.get(position);
            button_cell_spec.setText(buttonStr);
            if( pasv ) {
                button_cell_spec.setBackgroundResource(R.drawable.rect_button);
                button_cell_spec.setOnClickListener(null);
            } else {
                Integer titleInteger = NumberUtils.toInt(titleStr.replaceAll("[^0-9]",""),0);
                if(checkedLevels.contains(titleInteger)) {
                    button_cell_spec.setChecked(true);
                    button_cell_spec.setBackgroundResource(R.drawable.rect_checked);
                } else {
                    button_cell_spec.setChecked(false);
                    button_cell_spec.setBackgroundResource(R.drawable.rect_button);
                }

                button_cell_spec.setEnabled(true);
                button_cell_spec.setOnClickListener( v -> {
                    if(button_cell_spec.isChecked()) {
                        button_cell_spec.setChecked(false);
                        button_cell_spec.setBackgroundResource(R.drawable.rect_button);
                        checkedLevels.remove(titleInteger);
                        resultView.setText(getCurSumOfScores());
                    } else if( checkedLevels.size() < 3 ){
                        button_cell_spec.setChecked(true);
                        button_cell_spec.setBackgroundResource(R.drawable.rect_checked);
                        checkedLevels.add(titleInteger);
                        resultView.setText(getCurSumOfScores());
                    }
                });
            }
        }
    }

    private String getCurSumOfScores() {
        int sum = 0;
        for( int level : checkedLevels ) {
            sum += HeroSim.getSpecScoreByLevel(level);
        }
        return String.valueOf(sum);
    }

}
