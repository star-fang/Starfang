package com.fang.starfang.ui.main.recycler.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.source.Branch;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.ui.main.recycler.filter.HeroFilter;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

public class HeroesFloatingRecyclerAdapter extends RealmRecyclerViewAdapter<Heroes, RecyclerView.ViewHolder> implements Filterable {
    private static final String TAG = "FANG_FLOATING_ADAPTER";
    private String sort_field;
    private Sort sort;
    private Context context;
    private final static int[] COST_PLUS_BY_UPGRADE = {0,3,5,8,10};

    public HeroesFloatingRecyclerAdapter(RealmResults<Heroes> realmResults, Context context) {
        super(realmResults,false);
        this.context = context;
        sort_field = null;
        sort = null;
        Log.d(TAG,"HeroesFloatingRecyclerAdapter constructed" );
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_heroes_floating,viewGroup,false);
        return new HeroesFloatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        HeroesFloatingViewHolder heroesViewHolder = (HeroesFloatingViewHolder) viewHolder;

        Heroes hero = getItem(i);
        if( hero != null ) {
            heroesViewHolder.bind(hero);

        }
    }

    public void setSort(String field, Sort sort) {
        this.sort_field = field;
        this.sort = sort;
    }

    public String getCurSortField() {
        return  sort_field;
    }

    public Sort getCurSort() {
        return  sort;
    }

    @Override
    public Filter getFilter() {
        return new HeroFilter(this );
    }


    private class HeroesFloatingViewHolder extends RecyclerView.ViewHolder {
        private View[] row_hero_grade;
        private AppCompatTextView[] text_hero_grade_name;
        private AppCompatTextView[] text_hero_grade_cost;
        private AppCompatTextView text_hero_lineage;
        private AppCompatTextView[] text_hero_stat;
        private AppCompatTextView[] text_hero_plus_stat;
        private AppCompatCheckedTextView[] text_hero_spec_unique;
        private AppCompatCheckedTextView[] text_hero_spec_branch;
        private AppCompatTextView text_hero_stat_sum;
        private AppCompatTextView text_hero_stat_sum_total;
        private View cell_specs_unique;
        private View cell_specs_branch;
        private View cell_hero_stats;

        private HeroesFloatingViewHolder(View itemView) {
            super(itemView);

            row_hero_grade = new View[5];
            text_hero_grade_name = new AppCompatTextView[5];
            text_hero_grade_cost = new AppCompatTextView[5];
            text_hero_lineage = itemView.findViewById(R.id.text_hero_lineage);
            text_hero_stat = new AppCompatTextView[5];
            text_hero_plus_stat = new AppCompatTextView[5];
            text_hero_spec_unique = new AppCompatCheckedTextView[6];
            text_hero_spec_branch = new AppCompatCheckedTextView[5];
            text_hero_stat_sum = itemView.findViewById(R.id.text_hero_stat_sum);
            text_hero_stat_sum_total = itemView.findViewById(R.id.text_hero_stat_sum_total);
            cell_specs_unique = itemView.findViewById(R.id.cell_specs_unique);
            cell_specs_branch = itemView.findViewById(R.id.cell_specs_branch);
            cell_hero_stats = itemView.findViewById(R.id.cell_hero_stats);

            String id = "id";
            String packageName = context.getPackageName();
            Resources resources = context.getResources();
            for(int i = 0; i < 6; i++ ) {
                if( i < 5) {
                    int gradeRowID = resources.getIdentifier
                            ("row_hero_grade" + (i + 1), id, packageName);
                    row_hero_grade[i] = itemView.findViewById(gradeRowID);
                    int gradeNameID = resources.getIdentifier
                            ("text_hero_grade_name" + (i + 1), id, packageName);
                    text_hero_grade_name[i] = itemView.findViewById(gradeNameID);
                    int gradeCostID = resources.getIdentifier
                            ("text_hero_grade_cost" + (i + 1), id, packageName);
                    text_hero_grade_cost[i] = itemView.findViewById(gradeCostID);
                    int statID = resources.getIdentifier
                            ("text_hero_stat" + (i + 1), id, packageName);
                    text_hero_stat[i] = itemView.findViewById(statID);
                    int plusStatID = resources.getIdentifier
                            ("text_hero_plus_stat" + (i + 1), id, packageName);
                    text_hero_plus_stat[i] = itemView.findViewById(plusStatID);
                    int branchSpecID = resources.getIdentifier
                            ("text_hero_spec_branch" + (i + 1), id, packageName);
                    text_hero_spec_branch[i] = itemView.findViewById(branchSpecID);
                }

                int uniqueSpecID = resources.getIdentifier
                        ("text_hero_spec_unique" + (i + 1), id, packageName);
                text_hero_spec_unique[i] = itemView.findViewById(uniqueSpecID);
            }
        }



        private void bind(final Heroes hero ) {

            Realm realm = Realm.getDefaultInstance();
            HeroSim heroSim = realm.where(HeroSim.class).equalTo(HeroSim.FIELD_ID, hero.getHeroNo()).findFirst();
            Branch branch = realm.where(Branch.class).equalTo(Branch.FIELD_ID,hero.getBranchNo()).findFirst();
            RealmList<RealmInteger> heroStats = hero.getHeroStats();
            RealmList<RealmInteger> heroPlusStats = null;

            RealmList<RealmString> heroSpecs = hero.getHeroSpecs();
            RealmList<RealmString> heroSpecVals = hero.getHeroSpecValues();

            RealmList<RealmString> branchSpecs = null;
            RealmList<RealmString> branchSpecVals = null;
            RealmList<RealmString> branchGrades = null;

            int heroGrade = 1;
            int cost_init = hero.getHeroCost();
            if(heroSim != null) {
                heroGrade = heroSim.getHeroGrade();
                heroPlusStats = heroSim.getHeroStatsUp();
            }

            text_hero_lineage.setText(hero.getHeroLineage());

            if(branch != null) {
                branchSpecs = branch.getBranchSpecs();
                branchSpecVals = branch.getBranchSpecValues();
                branchGrades = branch.getBranchGrade();
            }

            int plusStatSum = 0;
            for(int i = 0; i < 6; i ++ ) {
                if( i < 5 ) {
                    int plus_cost = COST_PLUS_BY_UPGRADE[i];
                    text_hero_grade_cost[i].setText(String.valueOf(cost_init + plus_cost));
                    String gradeName = "";
                    if(branchGrades != null) {
                        RealmString branchGrade = branchGrades.get(i);
                        if(branchGrade != null) {
                            gradeName = branchGrade.toString();
                        }
                    }
                    text_hero_grade_name[i].setText(gradeName);
                    if(i == heroGrade - 1 ) {
                        row_hero_grade[i].setBackgroundResource(R.drawable.rect_checked);
                    } else {
                        row_hero_grade[i].setBackgroundResource(0);
                    }
                    RealmInteger heroStat = heroStats.get(i);
                    text_hero_plus_stat[i].setVisibility(View.GONE);
                    if(heroStat != null) {
                        int baseStat = heroStat.toInt();
                        if( heroPlusStats != null) {
                            RealmInteger heroPlusStat = heroPlusStats.get(i);
                            if(heroPlusStat != null) {
                                int plusStat = heroPlusStat.toInt();
                                plusStatSum += plusStat;
                                if( plusStat > 0 ) {
                                    baseStat += plusStat;
                                    text_hero_plus_stat[i].setVisibility(View.VISIBLE);
                                    String plusStatStr = "+" + plusStat;
                                    text_hero_plus_stat[i].setText(plusStatStr);
                                }
                            }
                        }
                        text_hero_stat[i].setText(String.valueOf(baseStat));
                    }

                    if( branchSpecs != null) {
                        RealmString branchSpec = branchSpecs.get(i);
                        if( branchSpec != null ) {
                            String branchSpecStr = branchSpec.toString();
                            if(branchSpecVals != null) {
                                RealmString branchSpecVal = branchSpecVals.get(i);
                                if(branchSpecVal != null ) {
                                    branchSpecStr += (" " + branchSpecVal.toString());
                                }
                            }
                            text_hero_spec_branch[i].setText(branchSpecStr);
                            text_hero_spec_branch[i].setVisibility(View.VISIBLE);
                        }
                    } else {
                        text_hero_spec_branch[i].setVisibility(View.GONE);
                    }
                }

                RealmString heroSpec = heroSpecs.get(i);
                if(heroSpec != null) {
                    String heroSpecStr = heroSpec.toString();
                    if( i < 4 ) {
                        RealmString heroSpecVal = heroSpecVals.get(i);
                        if(heroSpecVal != null) {
                            heroSpecStr += (" " + heroSpecVal.toString());
                        }
                    }
                    text_hero_spec_unique[i].setVisibility(View.VISIBLE);
                    text_hero_spec_unique[i].setText(heroSpecStr);
                } else {
                    text_hero_spec_unique[i].setVisibility(View.GONE);
                }


            }

            text_hero_stat_sum.setText(String.valueOf(plusStatSum));
            text_hero_stat_sum_total.setText(String.valueOf(heroGrade * 100));

        }
    }

}
