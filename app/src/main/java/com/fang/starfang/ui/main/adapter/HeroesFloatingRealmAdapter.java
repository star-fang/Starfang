package com.fang.starfang.ui.main.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.FangConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.primitive.RealmString;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.simulator.RelicSim;
import com.fang.starfang.local.model.realm.source.Branch;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.local.model.realm.source.RelicCombination;
import com.fang.starfang.local.model.realm.source.RelicPRFX;
import com.fang.starfang.local.model.realm.source.RelicSFX;
import com.fang.starfang.ui.main.dialog.PickItemSimDialogFragment;
import com.fang.starfang.ui.main.dialog.PickRelicSimDialogFragment;
import com.fang.starfang.ui.main.dialog.ReinforceItemDialogFragment;
import com.fang.starfang.ui.main.adapter.filter.HeroSimFilter;

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
    private static final String R_TEXT_HERO_SPEC_BRANCH_LEVEL = "text_hero_spec_branch_level";
    private static final String R_TEXT_HERO_SPEC_BRANCH = "text_hero_spec_branch";
    private static final String R_TEXT_HERO_SPEC_BRANCH_VAL = "text_hero_spec_branch_val";
    //private static final String R_ROW_HERO_SPEC_UNIQUE = "row_hero_spec_unique";
    private static final String R_TEXT_HERO_SPEC_UNIQUE_LEVEL = "text_hero_spec_unique_level";
    private static final String R_TEXT_HERO_SPEC_UNIQUE = "text_hero_spec_unique";
    private static final String R_TEXT_HERO_SPEC_UNIQUE_VAL = "text_hero_spec_unique_val";

    private static final String R_TEXT_RELIC = "text_relic";
    private static final String R_TEXT_PREFIX_SLOT = "_prefix_slot";
    private static final String R_TEXT_SUFFIX_SLOT = "_suffix_slot";
    private static final String R_TEXT_LEVEL_SLOT = "_level_slot";

    private static final String TAG = "FANG_ADAPTER_FLOATING";
    private final static int[] COST_PLUS_BY_UPGRADE = {0, 3, 5, 8, 10};
    private Realm realm;
    private final static String ID_STR = "id";
    private String packageName;
    private Resources resources;
    private int color_text_checked;
    private int color_text_unchecked;
    private TypedArray colors_relic_suffix;
    private TypedArray colors_relic_guardian;
    private Drawable[] relicSlotDrawables;
    private FragmentManager fragmentManager;
    private Context context;
    private String[] guardians;
    private String[] relicGradeSuperScripts;
    private static HeroesFloatingRealmAdapter instance;

    @org.jetbrains.annotations.Contract(pure = true)
    public static HeroesFloatingRealmAdapter getInstance() {
        return instance;
    }

    public static void setInstance(Realm realm, FragmentManager fragmentManager, Context context) {
        instance = new HeroesFloatingRealmAdapter(realm, fragmentManager, context);
    }

    private HeroesFloatingRealmAdapter(@NonNull Realm realm, FragmentManager fragmentManager, @NonNull Context context) {
        super(realm.where(HeroSim.class).findAll().sort(HeroSim.FIELD_HERO + "." + Heroes.FIELD_NAME).
                sort(HeroSim.FIELD_GRADE, Sort.DESCENDING).sort(HeroSim.FIELD_LEVEL, Sort.DESCENDING), false);
        this.realm = realm;
        this.context = context;
        this.packageName = context.getPackageName();
        this.resources = context.getResources();
        this.color_text_checked = ContextCompat.getColor(context, R.color.colorCheckedText);
        this.color_text_unchecked = ContextCompat.getColor(context, R.color.colorUnCheckedText);
        this.colors_relic_suffix = resources.obtainTypedArray(R.array.color_suffix);
        this.colors_relic_guardian = resources.obtainTypedArray(R.array.color_guardian);
        this.relicSlotDrawables = new Drawable[]{
                ContextCompat.getDrawable(context, R.drawable.ic_looks_one_white_24dp),
                ContextCompat.getDrawable(context, R.drawable.ic_looks_two_white_24dp),
                ContextCompat.getDrawable(context, R.drawable.ic_looks_3_white_24dp)
        };

        this.guardians = resources.getStringArray(R.array.guardians);
        this.relicGradeSuperScripts = resources.getStringArray(R.array.relic_grade_super_scripts);
        this.fragmentManager = fragmentManager;
        Log.d(TAG, "constructed");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_heroes_floating, viewGroup, false);
        return new HeroesFloatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        HeroesFloatingViewHolder heroesViewHolder = (HeroesFloatingViewHolder) viewHolder;

        HeroSim heroSim = getItem(i);
        if (heroSim != null) {
            try {
                heroesViewHolder.bind(heroSim);
            } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }


    public void sort(ArrayList<Pair<String, Sort>> sortPairs) {
        OrderedRealmCollection<HeroSim> realmCollection = this.getData();
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
        return new HeroSimFilter(this, realm);
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
        private AppCompatImageButton button_spec_change_view;

        private AppCompatTextView[] text_item_reinforcement;
        private AppCompatTextView[] text_item_name;

        private AppCompatTextView text_hero_lineage;

        private AppCompatTextView[][] text_relic_prefix_slot;
        private AppCompatTextView[][] text_relic_suffix_slot;
        private AppCompatTextView[][] text_relic_level_slot;

        private NestedScrollView scroll_hero_relic_slot;
        private AppCompatTextView text_relic_slot_guardian;
        private AppCompatTextView text_relic_slot_combination;
        private AppCompatTextView text_relic_slot_combination_val;
        private AppCompatImageButton button_relic_change_slot;

        private HeroesFloatingViewHolder(View itemView) {
            super(itemView);

            scroll_hero_relic_slot = itemView.findViewById(R.id.scroll_hero_relic_slot);
            text_relic_slot_guardian = itemView.findViewById(R.id.text_relic_slot_guardian);
            text_relic_slot_combination = itemView.findViewById(R.id.text_relic_slot_combination);
            text_relic_slot_combination_val = itemView.findViewById(R.id.text_relic_slot_combination_val);
            button_relic_change_slot = itemView.findViewById(R.id.button_relic_change_slot);

            text_relic_prefix_slot = new AppCompatTextView[3][];
            text_relic_suffix_slot = new AppCompatTextView[3][];
            text_relic_level_slot = new AppCompatTextView[3][];

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

            text_item_reinforcement = new AppCompatTextView[3];
            text_item_name = new AppCompatTextView[3];

            text_item_reinforcement[0] = itemView.findViewById(R.id.text_item_weapon_reinforcement);
            text_item_name[0] = itemView.findViewById(R.id.text_item_weapon_name);
            text_item_reinforcement[1] = itemView.findViewById(R.id.text_item_armor_reinforcement);
            text_item_name[1] = itemView.findViewById(R.id.text_item_armor_name);
            text_item_reinforcement[2] = itemView.findViewById(R.id.text_item_aid_reinforcement);
            text_item_name[2] = itemView.findViewById(R.id.text_item_aid_name);

            text_hero_lineage = itemView.findViewById(R.id.text_hero_lineage);

            for (int i = 0; i < 5; i++) {
                text_hero_grade_star[i] = itemView.findViewById(resources.getIdentifier
                        (R_TEXT_HERO_GRADE_STAR + (i + 1), ID_STR, packageName));
                text_hero_grade_cost[i] = itemView.findViewById(resources.getIdentifier
                        (R_TEXT_HERO_GRADE_COST + (i + 1), ID_STR, packageName));

                text_hero_stat[i] = itemView.findViewById(resources.getIdentifier
                        (R_TEXT_HERO_STAT + (i + 1), ID_STR, packageName));
                text_hero_plus_stat[i] = itemView.findViewById(resources.getIdentifier
                        (R_TEXT_HERO_STAT_PLUS + (i + 1), ID_STR, packageName));
                text_hero_sum_stat[i] = itemView.findViewById(resources.getIdentifier
                        (R_TEXT_HERO_STAT_SUM + (i + 1), ID_STR, packageName));

                text_hero_power_grade[i] = itemView.findViewById(resources.getIdentifier
                        (R_TEXT_HERO_POWER_GRADE + (i + 1), ID_STR, packageName));
                text_hero_power[i] = itemView.findViewById(resources.getIdentifier
                        (R_TEXT_HERO_POWER + (i + 1), ID_STR, packageName));

                text_hero_spec_branch_level[i] = itemView.findViewById(resources.getIdentifier
                        (R_TEXT_HERO_SPEC_BRANCH_LEVEL + (i + 1), ID_STR, packageName));
                text_hero_spec_branch[i] = itemView.findViewById(resources.getIdentifier
                        (R_TEXT_HERO_SPEC_BRANCH + (i + 1), ID_STR, packageName));
                text_hero_spec_branch_val[i] = itemView.findViewById(resources.getIdentifier
                        (R_TEXT_HERO_SPEC_BRANCH_VAL + (i + 1), ID_STR, packageName));


                if (i < 4) {
                    text_hero_spec_unique_level[i] = itemView.findViewById(resources.getIdentifier
                            (R_TEXT_HERO_SPEC_UNIQUE_LEVEL + (i + 1), ID_STR, packageName));
                    text_hero_spec_unique[i] = itemView.findViewById(resources.getIdentifier
                            (R_TEXT_HERO_SPEC_UNIQUE + (i + 1), ID_STR, packageName));
                    text_hero_spec_unique_val[i] = itemView.findViewById(resources.getIdentifier
                            (R_TEXT_HERO_SPEC_UNIQUE_VAL + (i + 1), ID_STR, packageName));
                }
            }


            for(int i = 0; i < 3; i++ ) {
                text_relic_prefix_slot[i] = new AppCompatTextView[4];
                text_relic_suffix_slot[i] = new AppCompatTextView[4];
                text_relic_level_slot[i] = new AppCompatTextView[4];

                for(int j = 0; j < 4; j++) {
                    text_relic_prefix_slot[i][j] = itemView.findViewById(resources.getIdentifier
                            (R_TEXT_RELIC + (j + 1) + R_TEXT_PREFIX_SLOT + (i + 1), ID_STR, packageName));

                    text_relic_suffix_slot[i][j] = itemView.findViewById(resources.getIdentifier
                            (R_TEXT_RELIC + (j + 1) + R_TEXT_SUFFIX_SLOT + (i + 1), ID_STR, packageName));

                    text_relic_level_slot[i][j] = itemView.findViewById(resources.getIdentifier
                            (R_TEXT_RELIC + (j + 1) + R_TEXT_LEVEL_SLOT + (i + 1), ID_STR, packageName));
                }

            }
        }

        private void bind(final HeroSim heroSim) throws NullPointerException, ArrayIndexOutOfBoundsException {

            final int heroID = heroSim.getHeroNo();
            Heroes hero = heroSim.getHero();
            Branch branch = heroSim.getHeroBranch();
            RealmList<RealmInteger> heroStats = hero.getHeroStats();
            RealmList<RealmString> heroSpecs = hero.getHeroSpecs();
            RealmList<RealmString> heroSpecVals = hero.getHeroSpecValues();


            RealmList<RealmString> branchSpecs = null;
            RealmList<RealmString> branchSpecVals = null;
            RealmList<RealmString> branchStatGGs = null;
            RealmList<RealmString> branchGrades = null;

            int cost_init = hero.getHeroCost();
            int heroGrade = heroSim.getHeroGrade();
            String sumOfSpecScores = String.valueOf(heroSim.getHeroSpecScoreSum());
            RealmList<RealmInteger> heroPlusStats = heroSim.getHeroPlusStats();
            RealmList<RealmInteger> heroPowers = heroSim.getHeroPowers();
            RealmList<Integer> checkedSpecsIndexes = heroSim.getHeroSpecsChecked();
            text_spec_score_total.setText(sumOfSpecScores);
            text_hero_lineage.setText(hero.getHeroLineage());

            if (branch != null) {
                branchSpecs = branch.getBranchSpecs();
                branchSpecVals = branch.getBranchSpecValues();
                branchStatGGs = branch.getBranchStatGGs();
                branchGrades = branch.getBranchGrade();
            }
            for (int i = 0; i < 5; i++) {
                int plus_cost = COST_PLUS_BY_UPGRADE[i];
                text_hero_grade_cost[i].setText(String.valueOf(cost_init + plus_cost));
                if (i == heroGrade - 1) {
                    text_hero_grade_star[i].setTextColor(color_text_checked);
                    text_hero_grade_cost[i].setTextColor(color_text_checked);
                } else {
                    text_hero_grade_star[i].setTextColor(color_text_unchecked);
                    text_hero_grade_cost[i].setTextColor(color_text_unchecked);
                }
                RealmInteger heroStat = heroStats.get(i);
                String heroPlusStatStr = "";
                String heroSumStatStr = "";
                if (heroStat != null) {
                    int sumStat = heroStat.toInt();
                    text_hero_stat[i].setText(heroStat.toString());
                    if (heroPlusStats != null) {
                        RealmInteger heroPlusStat = heroPlusStats.get(i);
                        if (heroPlusStat != null) {
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
                if (branchStatGGs != null) {
                    RealmString branchStatGG = branchStatGGs.get(i);
                    if (branchStatGG != null) {
                        branchStatGGStr = branchStatGG.toString();
                    }
                }
                text_hero_power_grade[i].setText(branchStatGGStr);

                String branchSpecStr = "";
                String branchSpecValStr = "";
                if (branchSpecs != null) {
                    // 0 ~ 4
                    RealmString branchSpec = branchSpecs.get(i);
                    if (branchSpec != null) {
                        branchSpecStr = branchSpec.toString();
                        if (branchSpecVals != null) {
                            RealmString branchSpecVal = branchSpecVals.get(i);
                            if (branchSpecVal != null) {
                                branchSpecValStr = branchSpecVal.toString();
                            }
                        }
                    }
                }
                int color_text = color_text_unchecked;
                if (checkedSpecsIndexes != null) {
                    if (checkedSpecsIndexes.contains(i)) {
                        color_text = color_text_checked;
                    }
                }

                text_hero_spec_branch_level[i].setTextColor(color_text);
                text_hero_spec_branch[i].setText(branchSpecStr);
                text_hero_spec_branch[i].setTextColor(color_text);
                text_hero_spec_branch_val[i].setText(branchSpecValStr);
                text_hero_spec_branch_val[i].setTextColor(color_text);
                if (i < 4) {
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
            if (branchGrades != null) {
                RealmString branchGrade = branchGrades.get(heroGrade - 1);
                if (branchGrade != null) {
                    branchGradeStr = branchGrade.toString();
                }
            }
            text_hero_grade.setText(branchGradeStr);
            scroll_hero_specs.scrollTo(0, 0);
            button_spec_change_view.setOnClickListener(v -> {
                int currY = scroll_hero_specs.getScrollY();
                int innerHeight = scroll_hero_specs.getChildAt(0).getHeight();
                int scrollHeight = scroll_hero_specs.getHeight();
                int scrollY = innerHeight - scrollHeight;
                //Log.d(TAG, currY + ":" + scrollY );
                if (currY == 0) {
                    button_spec_change_view.setImageResource(R.drawable.ic_arrow_upward_white_24dp);
                    scroll_hero_specs.smoothScrollTo(0, scrollY);
                } else {
                    button_spec_change_view.setImageResource(R.drawable.ic_arrow_downward_white_24dp);
                    scroll_hero_specs.smoothScrollTo(0, 0);
                }
            });

            RealmList<ItemSim> itemSims = heroSim.getHeroItemSims();

            for (int i = 0; i < 3; i++) {
                ItemSim itemSim = itemSims.get(i);
                String itemSimReinforceStr = itemSim == null ? null : "+" + itemSim.getItemReinforcement();
                String itemSimName = itemSim == null ? null : itemSim.getItem().getItemName();
                text_item_reinforcement[i].setText(itemSimReinforceStr);
                text_item_name[i].setText(itemSimName);
                int finalI = i;
                text_item_reinforcement[i].setOnClickListener(v -> {
                    if (itemSim != null) {
                        ReinforceItemDialogFragment.newInstance(itemSim.getItemID(), finalI).show(
                                fragmentManager, TAG);
                    }
                });
                text_item_name[i].setOnClickListener(v -> {

                    String subCate;
                    if (branch != null) {
                        switch (finalI) {
                            case FangConstant.ITEM_MAIN_CATEGORY_CODE_WEAPON:
                                subCate = branch.getBranchWeaponSubCate();
                                break;
                            case FangConstant.ITEM_MAIN_CATEGORY_CODE_ARMOR:
                                subCate = branch.getBranchArmorSubCate();
                                break;
                            case FangConstant.ITEM_MAIN_CATEGORY_CODE_AID:
                                subCate = FangConstant.AID_KOR;
                                break;
                            default:
                                subCate = "";
                        }
                    } else {
                        subCate = "";
                    }
                    PickItemSimDialogFragment.newInstance(heroSim.getHeroNo(), subCate, finalI).show(
                            fragmentManager, TAG);

                });
            }

            for( int i = 0; i < 3; i++) {
                RealmList<RelicSim> slot = heroSim.getHeroRelicSlot(i + 1);

                String[] prefixStr = new String[4];
                String[] suffixStr = new String[4];
                String[] levelStr = new String[4];
                int[] color_suffix = new int[4];

                for( RelicSim relicSim : slot ) {
                    RelicPRFX relicPRFX = relicSim.getPrefix();
                    RelicSFX relicSFX = relicSim.getSuffix();
                    int position = relicSim.getPositionInSlot();
                    if( position > 0 && position < 5) {

                        prefixStr[position - 1] = relicPRFX == null ? null :
                                relicPRFX.getRelicPrefixName();

                        suffixStr[position - 1] = relicSFX == null ? null :
                                relicSFX.getRelicSuffixName() + " " + relicGradeSuperScripts[relicSFX.getRelicSuffixGrade() - 1];

                        color_suffix[position - 1] = relicSFX == null ? 0 :
                                colors_relic_suffix.getColor(relicSFX.getRelicSuffixID() % 7, 0);


                        levelStr[position - 1] = relicSim.getRelicLevelStr();

                    }
                }
                    for (int j = 0; j < 4; j++) {
                        text_relic_prefix_slot[i][j].setText(prefixStr[j]);
                        text_relic_suffix_slot[i][j].setText(suffixStr[j] == null ? "+" : suffixStr[j]);
                        text_relic_suffix_slot[i][j].setTextColor(color_suffix[j] == 0 ? color_text_unchecked : color_suffix[j]);
                        text_relic_level_slot[i][j].setText(levelStr[j]);
                        final int relicSlot = i + 1;
                        final int relicPosition = j + 1;
                        text_relic_suffix_slot[i][j].setOnClickListener( v -> PickRelicSimDialogFragment.newInstance(heroSim.getHeroNo(), relicPosition, relicSlot).show(fragmentManager, TAG));
                        text_relic_level_slot[i][j].setOnClickListener( v -> realm.executeTransactionAsync(bgRealm -> {
                            HeroSim bgHeroSim = bgRealm.where(HeroSim.class).equalTo(HeroSim.FIELD_ID, heroID).findFirst();
                            if( bgHeroSim != null ) {
                                bgHeroSim.relicLevelUp(relicSlot, relicPosition);
                            }
                        }, () -> {
                            int relicLevel = heroSim.getRelicLevel(relicSlot, relicPosition);
                            String relicLevelStr;
                            switch( relicLevel ) {
                                case 0:
                                    PickRelicSimDialogFragment.newInstance(heroSim.getHeroNo(), relicPosition, relicSlot).show(fragmentManager, TAG);
                                    break;
                                case 1:Toast.makeText( context, suffixStr[relicPosition - 1]  + resources.getString(R.string.desc_relic_level_init), Toast.LENGTH_SHORT).show();
                                    relicLevelStr = resources.getString(R.string.level) + 1;
                                    text_relic_level_slot[relicSlot - 1][relicPosition - 1].setText(relicLevelStr);
                                    break;
                                default:
                                    Toast.makeText( context, suffixStr[relicPosition - 1]  + resources.getString(R.string.desc_relic_level_up)  + relicLevel, Toast.LENGTH_SHORT).show();
                                    relicLevelStr = resources.getString(R.string.level) + relicLevel;
                                    text_relic_level_slot[relicSlot - 1][relicPosition - 1].setText(relicLevelStr);
                            }

                        }));

                    }

            }

            final int currentRelicSlot = heroSim.getCurrentRelicSlot();
            final RelicCombination currentRelicCombination = heroSim.getRelicCombination( currentRelicSlot );
            text_relic_slot_combination.setText( currentRelicCombination == null ? null : currentRelicCombination.getRelicCombinationSpec());
            text_relic_slot_combination_val.setText( currentRelicCombination == null ? null : currentRelicCombination.getRelicCombinationSpecVal() );
            int currentGuardianType = currentRelicCombination == null ? 0 :  currentRelicCombination.getGuardianType(); // 1,2,3,4
            boolean currentArrayIndexOutOfBounds = currentGuardianType <= 0  || currentGuardianType > guardians.length;
            text_relic_slot_guardian.setText( currentArrayIndexOutOfBounds ? null : guardians[currentGuardianType - 1] );
            text_relic_slot_guardian.setTextColor( currentArrayIndexOutOfBounds ? 0: colors_relic_guardian.getColor(currentGuardianType - 1, 0 ) );
            final int slotScrollHeight = scroll_hero_relic_slot.getHeight();
            button_relic_change_slot.setImageDrawable( relicSlotDrawables[currentRelicSlot - 1]);
            scroll_hero_relic_slot.scrollTo(0, (currentRelicSlot - 1) * slotScrollHeight);

            button_relic_change_slot.setOnClickListener( v -> {
                int relicSlot = heroSim.getCurrentRelicSlot();
                // 0 -> 1, 1 -> 2, 2 -> 0
                // a + 1 % 3
                int changedRelicSlot = ( relicSlot ) % 3 + 1;

                final String message = hero.getHeroName() + " " + resources.getString(R.string.relic_slot_kor) + changedRelicSlot + resources.getString(R.string.wear_kor);

                realm.executeTransactionAsync( bgRealm -> {
                    HeroSim bgHeroSim = bgRealm.where(HeroSim.class).equalTo(HeroSim.FIELD_ID, heroID).findFirst();
                    if( bgHeroSim != null) {
                        bgHeroSim.setCurrentRelicSlot(changedRelicSlot);
                    }

                }, () -> {
                    RelicCombination changedRelicCombination = heroSim.getRelicCombination( changedRelicSlot );
                    text_relic_slot_combination.setText(changedRelicCombination == null ? null : changedRelicCombination.getRelicCombinationSpec());
                    text_relic_slot_combination_val.setText(changedRelicCombination == null ? null : changedRelicCombination.getRelicCombinationSpecVal());
                    int changedGuardianType = changedRelicCombination == null ? 0 :  changedRelicCombination.getGuardianType(); // 1,2,3,4
                    boolean changedArrayIndexOutOfBounds = changedGuardianType <= 0  || changedGuardianType > guardians.length;
                    text_relic_slot_guardian.setText( changedArrayIndexOutOfBounds ? null : guardians[changedGuardianType - 1] );
                    text_relic_slot_guardian.setTextColor( changedArrayIndexOutOfBounds ? 0: colors_relic_guardian.getColor(changedGuardianType - 1, 0 ) );
                    button_relic_change_slot.setImageDrawable( relicSlotDrawables[changedRelicSlot - 1]);
                    scroll_hero_relic_slot.smoothScrollTo(0, (changedRelicSlot - 1) * slotScrollHeight);

                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                });

            });


        } // end bind()
    }

}
