package com.fang.starfang.ui.main.recycler.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.ui.main.dialog.PickHeroDialogFragment;
import com.fang.starfang.ui.main.recycler.filter.ItemSimFilter;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class ItemSimsFloatingRealmAdapter extends RealmRecyclerViewAdapter<ItemSim, RecyclerView.ViewHolder> implements Filterable {

    private static final String TAG = "FANG_ADAPTER_ITEM_FLOAT";

    private static ItemSimsFloatingRealmAdapter instance;

    private FragmentManager fragmentManager;
    private Realm realm;


    public static ItemSimsFloatingRealmAdapter getInstance() {
        return instance;
    }
    public static void setInstance( Realm realm, FragmentManager fragmentManager ) {
        instance = new ItemSimsFloatingRealmAdapter(realm, fragmentManager);
    }

    @NonNull
    @Override
    public ItemSimsFloatingRealmAdapter.ItemsSimViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_items_floating,viewGroup,false);
        return new ItemSimsFloatingRealmAdapter.ItemsSimViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemSim itemSim = getItem( position );
        ItemsSimViewHolder itemsViewHolder = (ItemsSimViewHolder) holder;
        if( itemSim != null ) {
            itemsViewHolder.bind(itemSim);
        }
    }


    private ItemSimsFloatingRealmAdapter(Realm realm, FragmentManager fragmentManager) {
        super(realm.where(ItemSim.class).findAll().sort(ItemSim.FIELD_REINF), false);
        this.realm = realm;
        this.fragmentManager = fragmentManager;
        Log.d(TAG, "constructed");
    }

    @Override
    public Filter getFilter() {
        return new ItemSimFilter(this,realm);
    }


    public class ItemsSimViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView text_item_hero_branch;
        AppCompatTextView text_item_hero_name;
        AppCompatTextView text_item_spec_grade6;
        AppCompatTextView text_item_spec_grade_val6;
        AppCompatTextView text_item_spec_grade12;
        AppCompatTextView text_item_spec_grade_val12;

        private ItemsSimViewHolder(View itemView) {
            super(itemView);
            text_item_hero_branch = itemView.findViewById(R.id.text_item_hero_branch);
            text_item_hero_name = itemView.findViewById(R.id.text_item_hero_name);
            text_item_spec_grade6 = itemView.findViewById(R.id.text_item_spec_grade6);
            text_item_spec_grade_val6 = itemView.findViewById(R.id.text_item_spec_grade_val6);
            text_item_spec_grade12 = itemView.findViewById(R.id.text_item_spec_grade12);
            text_item_spec_grade_val12 = itemView.findViewById(R.id.text_item_spec_grade_val12);
        }


        private void bind(ItemSim itemSim) {
            HeroSim heroSim = itemSim.getHeroWhoHasThis();
            String heroName = "";
            String heroBranch = "";
            String specGrade6 = itemSim.getSpecNameGrade6();
            String specGradeVal6 = itemSim.getSpecValueGrade6();
            String specGrade12 = itemSim.getSpecNameGrade12();
            String specGradeVal12 = itemSim.getSpecValueGrade12();
            if(heroSim != null ) {
                Heroes hero = heroSim.getHero();
                if(hero != null) {
                    heroName = hero.getHeroName();
                    heroBranch = hero.getHeroBranch();
                }
            }// end if heroSim != null


            text_item_hero_branch.setText(heroBranch);
            text_item_hero_name.setText(heroName);
            text_item_spec_grade6.setText(specGrade6 == null ? "" : specGrade6);
            text_item_spec_grade_val6.setText(specGradeVal6 == null ? "" : specGradeVal6);
            text_item_spec_grade12.setText(specGrade12 == null ? "" : specGrade12);
            text_item_spec_grade_val12.setText(specGradeVal12 == null ? "" : specGradeVal12);

            text_item_hero_name.setOnClickListener( v -> PickHeroDialogFragment.newInstance(itemSim.getItemID()).show(fragmentManager,TAG));
        }
    }

}
