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
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.source.Branch;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.ui.main.recycler.filter.HeroSimFilter;

import java.util.ArrayList;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.Sort;

public class HeroesFloatingRealmAdapter extends RealmRecyclerViewAdapter<HeroSim, RecyclerView.ViewHolder> implements Filterable {

    private static final String R_TEXT_HERO_GRADE_STAR = "text_hero_grade_star";
    private static final String R_TEXT_HERO_GRADE_COST = "text_hero_grade_cost";

    private static final String R_TEXT_HERO_STAT = "text_hero_stat";
    private static final String R_TEXT_HERO_STAT_PLUS = "text_hero_plus_stat";
    private static final String R_TEXT_HERO_STAT_SUM = "text_hero_sum_stat";

    private static final String R_TEXT_HERO_POWER_GRADE = "text_hero_power_grade";
    private static final String R_TEXT_HERO_POWER = "text_hero_power";

    //private static final String R_ROW_HERO_SPEC_BRANCH = "row_hero_spec_branch";
    private static final String R_TEXT_HERO_SEPC_BRANCH_LEVEL = "text_hero_spec_branch_level";
    private static final String R_TEXT_HERO_SEPC_BRANCH = "text_hero_spec_branch";
    private static final String R_TEXT_HERO_SEPC_BRANCH_VAL =  "text_hero_spec_branch_val";
    //private static final String R_ROW_HERO_SPEC_UNIQUE = "row_hero_spec_unique";
    private static final String R_TEXT_HERO_SEPC_UNIQUE_LEVEL = "text_hero_spec_unique_level";
    private static final String R_TEXT_HERO_SEPC_UNIQUE = "text_hero_spec_unique";
    private static final String R_TEXT_HERO_SEPC_UNIQUE_VAL =  "text_hero_spec_unique_val";

    private static final String TAG = "FANG_ADAPTER_FLOATING";
    private final static int[] COST_PLUS_BY_UPGRADE = {0,3,5,8,10};
    private Realm realm;
    private final static String ID_STR = "id";
    private String packageName;
    private Resources resources;
    private int color_text_checked;
    private int color_text_unchecked;
    private static HeroesFloatingRealmAdapter instance = null;

    public static HeroesFloatingRealmAdapter getInstance() {
        return instance;
    }

    public HeroesFloatingRealmAdapter(Realm realm, Context context) {
        super(realm.where(HeroSim.class).findAll().sort(HeroSim.FIELD_HERO+"."+Heroes.FIELD_NAME).
                sort(HeroSim.FIELD_GRADE,Sort.DESCENDING).sort(HeroSim.FIELD_LEVEL, Sort.DESCENDING),false);
        this.realm = realm;
        this.packageName = context.getPackageName();
        this.resources = context.getResources();
        this.color_text_checked = ContextCompat.getColor(context,R.color.colorCheckedText);
        this.color_text_unchecked = ContextCompat.getColor(context,R.color.colorUnCheckedText);
        instance = this;

        Log.d(TAG,"constructed" );
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

        HeroSim heroSim = getItem(i);
        if( heroSim != null ) {
            heroesViewHolder.bind(heroSim);

        }
    }


    public void sort(ArrayList<Pair<String, Sort>> sortPairs) {
        OrderedRealmCollection<HeroSim> realmCollection = this.getData();
        for( Pair<String, Sort> pair : sortPairs) {
            String cs = pair.first;
            Sort sort = pair.second;
            if(realmCollection != null && cs != null && sort != null ) {
                realmCollection = realmCollection.sort(cs, sort);
            }

        }
        updateData(realmCollection);
    }


    @Override
    public Filter getFilter() {
        return new HeroSimFilter(this, realm );
    }


    private class HeroesFloatingViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView[] text_hero_grade_star;
        private AppCompatTextView[] text_hero_grade_cost;
        private AppCompatTextView text_hero_grade;

        private AppCompatTextView[] text_hero_stat;
        private AppCompatTextView[] text_hero_plus_stat;
        private AppCompatTextView[] text_hero_sum_stat;
        private AppCompatTextView text_hero_stat_sum;
        private AppCompatTextView text_hero_stat_sum_total;

        private AppCompatTextView[] text_hero_power_grade;
        private AppCompatTextView[] text_hero_power;
        private AppCompatTextView text_hero_power_sum;

        //private View[] row_hero_spec_unique;
        private AppCompatTextView[] text_hero_spec_unique_level;
        private AppCompatTextView[] text_hero_spec_unique;
        private AppCompatTextView[] text_hero_spec_unique_val;
        //private View[] row_hero_spec_branch;
        private AppCompatTextView[] text_hero_spec_branch_level;
        private AppCompatTextView[] text_hero_spec_branch;
        private AppCompatTextView[] text_hero_spec_branch_val;
        private AppCompatTextView text_spec_score_total;
        private NestedScrollView scroll_hero_specs;
        private AppCompatButton button_spec_change_view;
        private AppCompatImageView image_spec_arrow;

        private AppCompatTextView text_hero_lineage;

        private HeroesFloatingViewHolder(View itemView) {
            super(itemView);
            text_hero_grade_star = new AppCompatTextView[5];
            text_hero_grade_cost = new AppCompatTextView[5];
            text_hero_grade = itemView.findViewById(R.id.text_hero_grade);

            text_hero_stat = new AppCompatTextView[5];
            text_hero_plus_stat = new AppCompatTextView[5];
            text_hero_sum_stat = new AppCompatTextView[5];
            text_hero_stat_sum = itemView.findViewById(R.id.text_hero_stat_sum);
            text_hero_stat_sum_total = itemView.findViewById(R.id.text_hero_stat_sum_total);

            text_hero_power_grade = new AppCompatTextView[5];
            text_hero_power = new AppCompatTextView[5];
            text_hero_power_sum = itemView.findViewById(R.id.text_hero_power_sum);

            //row_hero_spec_unique = new View[6];
            text_hero_spec_unique_level = new AppCompatTextView[4];
            text_hero_spec_unique = new AppCompatTextView[4];
            text_hero_spec_unique_val = new AppCompatTextView[4];
            //row_hero_spec_branch = new View[5];
            text_hero_spec_branch_level = new AppCompatTextView[5];
            text_hero_spec_branch = new AppCompatTextView[5];
            text_hero_spec_branch_val = new AppCompatTextView[5];
            text_spec_score_total = itemView.findViewById(R.id.text_spec_score_total);
            scroll_hero_specs = itemView.findViewById(R.id.scroll_hero_specs);
            button_spec_change_view = itemView.findViewById(R.id.button_spec_change_view);
            image_spec_arrow = itemView.findViewById(R.id.image_spec_arrow);

            text_hero_lineage = itemView.findViewById(R.id.text_hero_lineage);

            for(int i = 0; i < 5; i++ ) {
                    text_hero_grade_star[i] = itemView.findViewById(resources.getIdentifier
                            (R_TEXT_HERO_GRADE_STAR + (i + 1), ID_STR, packageName));
                    text_hero_grade_cost[i] = itemView.findViewById(resources.getIdentifier
                            ( R_TEXT_HERO_GRADE_COST + (i + 1), ID_STR, packageName));

                    text_hero_stat[i] = itemView.findViewById(resources.getIdentifier
                            ( R_TEXT_HERO_STAT + (i + 1), ID_STR, packageName));
                    text_hero_plus_stat[i] = itemView.findViewById(resources.getIdentifier
                            ( R_TEXT_HERO_STAT_PLUS + (i + 1), ID_STR, packageName));
                    text_hero_sum_stat[i] = itemView.findViewById(resources.getIdentifier
                            ( R_TEXT_HERO_STAT_SUM + (i + 1), ID_STR, packageName));

                    text_hero_power_grade[i] = itemView.findViewById(resources.getIdentifier
                            ( R_TEXT_HERO_POWER_GRADE + (i + 1), ID_STR, packageName));
                    text_hero_power[i] = itemView.findViewById(resources.getIdentifier
                            ( R_TEXT_HERO_POWER + (i + 1), ID_STR, packageName));

                    text_hero_spec_branch_level[i] = itemView.findViewById(resources.getIdentifier
                            ( R_TEXT_HERO_SEPC_BRANCH_LEVEL + (i + 1), ID_STR, packageName));
                    text_hero_spec_branch[i] = itemView.findViewById(resources.getIdentifier
                            ( R_TEXT_HERO_SEPC_BRANCH + (i + 1), ID_STR, packageName));
                    text_hero_spec_branch_val[i] = itemView.findViewById(resources.getIdentifier
                            ( R_TEXT_HERO_SEPC_BRANCH_VAL + (i + 1), ID_STR, packageName));


                if( i < 4 ) {
                    text_hero_spec_unique_level[i] = itemView.findViewById(resources.getIdentifier
                            (R_TEXT_HERO_SEPC_UNIQUE_LEVEL + (i + 1), ID_STR, packageName));
                    text_hero_spec_unique[i] = itemView.findViewById(resources.getIdentifier
                            (R_TEXT_HERO_SEPC_UNIQUE + (i + 1), ID_STR, packageName));
                    text_hero_spec_unique_val[i] = itemView.findViewById(resources.getIdentifier
                            (R_TEXT_HERO_SEPC_UNIQUE_VAL + (i + 1), ID_STR, packageName));
                }
            }
        }

        private void bind(final HeroSim heroSim ) {

            Heroes hero = heroSim.getHero();
            Branch branch = realm.where(Branch.class).equalTo(Branch.FIELD_ID,hero.getBranchNo()).findFirst();
            RealmList<RealmInteger> heroStats = hero.getHeroStats();
            RealmList<RealmString> heroSpecs = hero.getHeroSpecs();
            RealmList<RealmString> heroSpecVals = hero.getHeroSpecValues();


            RealmList<RealmString> branchSpecs = null;
            RealmList<RealmString> branchSpecVals = null;
            RealmList<RealmString> branchStatGGs = null;
            RealmList<RealmString> branchGrades = null;

            int cost_init = hero.getHeroCost();
            int  heroGrade = heroSim.getHeroGrade();
            String sumOfSpecScores = String.valueOf(heroSim.getHeroSpecScoreSum());
            RealmList<RealmInteger> heroPlusStats = heroSim.getHeroPlusStats();
            RealmList<RealmInteger> heroPowers = heroSim.getHeroPowers();
            RealmList<Integer>  checkedSpecsIndexes = heroSim.getHeroSpecsChecked();
            text_spec_score_total.setText(sumOfSpecScores);
            text_hero_lineage.setText(hero.getHeroLineage());

            if(branch != null) {
                branchSpecs = branch.getBranchSpecs();
                branchSpecVals = branch.getBranchSpecValues();
                branchStatGGs = branch.getBranchStatGGs();
                branchGrades = branch.getBranchGrade();
            }
            for(int i = 0; i < 5; i ++ ) {
                    int plus_cost = COST_PLUS_BY_UPGRADE[i];
                    text_hero_grade_cost[i].setText(String.valueOf(cost_init + plus_cost));
                    if(i == heroGrade - 1 ) {
                        text_hero_grade_star[i].setTextColor(color_text_checked);
                        text_hero_grade_cost[i].setTextColor(color_text_checked);
                    } else {
                        text_hero_grade_star[i].setTextColor(color_text_unchecked);
                        text_hero_grade_cost[i].setTextColor(color_text_unchecked);
                    }
                    RealmInteger heroStat = heroStats.get(i);
                    String heroPlusStatStr = "";
                    String heroSumStatStr = "";
                    if(heroStat != null) {
                        int sumStat = heroStat.toInt();
                        text_hero_stat[i].setText(heroStat.toString());
                        if( heroPlusStats != null) {
                            RealmInteger heroPlusStat = heroPlusStats.get(i);
                            if(heroPlusStat != null) {
                                int plusStat = heroPlusStat.toInt();
                                sumStat += plusStat;
                                heroPlusStatStr = String.valueOf(plusStat);
                            }
                        }
                        heroSumStatStr = String.valueOf(sumStat);
                    }
                    text_hero_plus_stat[i].setText(heroPlusStatStr);
                    text_hero_sum_stat[i].setText(heroSumStatStr);

                    RealmInteger heroPower = heroPowers.get(i);
                    String heroPowerStr = heroPower == null ? "" : heroPower.toString();
                    text_hero_power[i].setText(heroPowerStr);

                    String branchStatGGStr = "S";
                    if(branchStatGGs != null) {
                        RealmString branchStatGG = branchStatGGs.get(i);
                        if( branchStatGG != null) {
                            branchStatGGStr = branchStatGG.toString();
                        }
                    }
                    text_hero_power_grade[i].setText(branchStatGGStr);

                    String branchSpecStr = "";
                    String branchSpecValStr = "";
                    if( branchSpecs != null) {
                        // 0 ~ 4
                        RealmString branchSpec = branchSpecs.get(i);
                        if( branchSpec != null ) {
                            branchSpecStr = branchSpec.toString();
                            if(branchSpecVals != null) {
                                RealmString branchSpecVal = branchSpecVals.get(i);
                                if(branchSpecVal != null ) {
                                    branchSpecValStr = branchSpecVal.toString();
                                }
                            }
                        }
                    }
                    int color_text = color_text_unchecked;
                    if( checkedSpecsIndexes != null ) {
                        if( checkedSpecsIndexes.contains(i)) {
                            color_text = color_text_checked;
                        }
                    }

                    text_hero_spec_branch_level[i].setTextColor(color_text);
                    text_hero_spec_branch[i].setText(branchSpecStr);
                    text_hero_spec_branch[i].setTextColor(color_text);
                    text_hero_spec_branch_val[i].setText(branchSpecValStr);
                    text_hero_spec_branch_val[i].setTextColor(color_text);
                if( i < 4 ) {
                    RealmString heroSpec = heroSpecs.get(i);
                    String heroSpecStr = "";
                    String heroSpecValStr = "";
                    if (heroSpec != null) {
                        heroSpecStr = heroSpec.toString();

                        RealmString heroSpecVal = heroSpecVals.get(i);
                        if (heroSpecVal != null) {
                            heroSpecValStr = heroSpecVal.toString();
                        }
                    }

                    int color_text_unique = color_text_unchecked;
                    if (checkedSpecsIndexes != null) {
                        if (checkedSpecsIndexes.contains(i + 5)) {
                            color_text_unique = color_text_checked;
                        }
                    }

                    text_hero_spec_unique_level[i].setTextColor(color_text_unique);
                    text_hero_spec_unique[i].setText(heroSpecStr);
                    text_hero_spec_unique[i].setTextColor(color_text_unique);
                    text_hero_spec_unique_val[i].setText(heroSpecValStr);
                    text_hero_spec_unique_val[i].setTextColor(color_text_unique);
                } // end if i < 4
            }  // end for i < 5

            text_hero_stat_sum.setText(String.valueOf(heroSim.getHeroPlusStatSum()));
            text_hero_stat_sum_total.setText(String.valueOf(heroGrade * 100));
            text_hero_power_sum.setText(String.valueOf(heroSim.getHeroPowerSum()));

            String branchGradeStr = hero.getHeroBranch();
            if( branchGrades != null ) {
                RealmString branchGrade = branchGrades.get(heroGrade - 1);
                if(branchGrade != null ) {
                    branchGradeStr = branchGrade.toString();
                }
            }
            text_hero_grade.setText(branchGradeStr);
            image_spec_arrow.setImageResource(R.drawable.ic_arrow_downward_white_24dp);
            scroll_hero_specs.scrollTo(0,0);
                    button_spec_change_view.setOnClickListener(v-> {
                        int currY = scroll_hero_specs.getScrollY();
                        int innerHeight = scroll_hero_specs.getChildAt(0).getHeight();
                        int scrollHeight = scroll_hero_specs.getHeight();
                        int scrollY = innerHeight - scrollHeight;
                        Log.d(TAG, currY + ":" + scrollY );
                        if(currY == 0 ) {
                            image_spec_arrow.setImageResource(R.drawable.ic_arrow_upward_white_24dp);
                            scroll_hero_specs.scrollTo(0, scrollY);
                        } else {
                            image_spec_arrow.setImageResource(R.drawable.ic_arrow_downward_white_24dp);
                            scroll_hero_specs.scrollTo( 0, 0);
                        }
                    });

        } // end bind()
    }

}
