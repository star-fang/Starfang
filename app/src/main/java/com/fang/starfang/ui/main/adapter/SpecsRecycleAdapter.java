package com.fang.starfang.ui.main.adapter;

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

public class SpecsRecycleAdapter extends RecyclerView.Adapter<SpecsRecycleAdapter.SpecsRecyclerViewAdapterViewHolder>
        implements Filterable {

    private static final String TAG = "FANG_ADAPTER_SPEC";
    private ArrayList<String> titles;
    private ArrayList<String> titlesFiltered;
    private ArrayList<String> specs;
    private ArrayList<String> specsFiltered;
    private ArrayList<String> specVals;
    private ArrayList<String> specValsFiltered;
    private ArrayList<Integer> checkedLevels;
    private boolean pasv;
    private TextView resultView;
    //private static final int[] RESTRICT_PICK_SPECS_BY_GRADE = {1,1,2,2,3};


    @NonNull
    @Override
    public SpecsRecyclerViewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_heroes_cell_spec, viewGroup, false);
        return new SpecsRecycleAdapter.SpecsRecyclerViewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecsRecyclerViewAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return titlesFiltered.size();
    }


    public SpecsRecycleAdapter(ArrayList<String> titles, ArrayList<String> specs
            , ArrayList<String> specVals, ArrayList<Integer> checkedLevels, boolean pasv, TextView resultView) {

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
            private int cs;

            @Override
            protected FilterResults performFiltering(@NonNull CharSequence charSequence) {

                String csStr = charSequence.toString();
                cs = NumberUtils.toInt(csStr, 0);
                //Log.d(TAG,"CS: " + cs);
                // 1 ~ 99  or 1 ~ 5
                if (cs != 0) {


                    if (!pasv && checkedLevels != null) {
                        Integer level_before = cs + 1;
                        if (checkedLevels.contains(level_before)) {
                            checkedLevels.remove(level_before);
                            setScoreResultView();
                            Log.d(TAG, "level down : checked spec removed");
                        }
                    }

                    ArrayList<Integer> results = new ArrayList<>();

                    for (int i = 0; i < titles.size(); i++) {
                        String titleStr = titles.get(i);
                        //Log.d(TAG,"titleStr: " + titleStr);
                        int titleValue = NumberUtils.toInt(titleStr.replaceAll("[^0-9]", ""), 0);
                        //Log.d(TAG,"titleValue: " + titleValue);

                        if (titleValue <= cs) {
                            results.add(i);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = results;

                    return filterResults;
                } else {
                    return null;
                }
            }

            @Override
            protected void publishResults(@NonNull CharSequence charSequence, FilterResults filterResults) {
                if (filterResults == null) {
                    Log.d(TAG, "FILTER RESULT IS NULL");
                    return;
                }

                titlesFiltered = new ArrayList<>();
                specsFiltered = new ArrayList<>();
                specValsFiltered = new ArrayList<>(); // do not clear()

                Object values = filterResults.values;
                if (values instanceof ArrayList<?>) {
                    ArrayList<?> al = (ArrayList<?>) values;
                    if (al.size() > 0) {
                        for (Object o : al) {
                            if (o instanceof Integer) {
                                Integer integer = (Integer) o;
                                String titleStr = titles.get(integer);
                                String specValStr = specVals.get(integer);
                                if (pasv) {
                                    if (specValStr != null) {
                                        if (specValStr.contains("/")) {
                                            int titleValue = NumberUtils.toInt(titleStr.replaceAll("[^0-9]", ""), 0);
                                            String[] specValSplit = specValStr.split("/");
                                            int index = (cs - titleValue);
                                            if (index >= specValSplit.length || index < 0) {
                                                index = specValSplit.length - 1;
                                            }
                                            String specValCurCS = specValSplit[index];
                                            String specValCurCSValue = specValCurCS.replaceAll("[^0-9]", "");
                                            if (specValStr.contains("%")) {
                                                specValStr = specValCurCSValue + "%";
                                            } else {
                                                specValStr = "[" + specValCurCSValue + "]";
                                            }

                                        }
                                    }
                                }
                                titlesFiltered.add(titleStr);
                                specsFiltered.add(specs.get(integer));
                                specValsFiltered.add(specValStr);
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
            if (pasv) {
                button_cell_spec.setBackgroundResource(R.drawable.rect_button);
                button_cell_spec.setOnClickListener(null);
            } else {
                Integer titleInteger = NumberUtils.toInt(titleStr.replaceAll("[^0-9]", ""), 0);
                if (checkedLevels.contains(titleInteger)) {
                    button_cell_spec.setChecked(true);
                    button_cell_spec.setBackgroundResource(R.drawable.rect_checked);
                } else {
                    button_cell_spec.setChecked(false);
                    button_cell_spec.setBackgroundResource(R.drawable.rect_button);
                }

                button_cell_spec.setEnabled(true);
                button_cell_spec.setOnClickListener(v -> {
                    if (button_cell_spec.isChecked()) {
                        button_cell_spec.setChecked(false);
                        button_cell_spec.setBackgroundResource(R.drawable.rect_button);
                        checkedLevels.remove(titleInteger);
                        setScoreResultView();
                    } else if (checkedLevels.size() < 3) {
                        button_cell_spec.setChecked(true);
                        button_cell_spec.setBackgroundResource(R.drawable.rect_checked);
                        checkedLevels.add(titleInteger);
                        setScoreResultView();
                    }
                });
            }
        }
    }

    private void setScoreResultView() {
        if (resultView == null) {
            return;
        }
        int sum = 0;
        for (int level : checkedLevels) {
            sum += HeroSim.getSpecScoreByLevel(level);
        }
        resultView.setText(String.valueOf(sum));
    }

}
