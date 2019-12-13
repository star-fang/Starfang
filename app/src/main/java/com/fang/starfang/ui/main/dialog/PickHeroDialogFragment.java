package com.fang.starfang.ui.main.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.AppConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.source.Branch;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.local.model.realm.source.ItemCate;
import com.fang.starfang.ui.common.UpdateDialogFragment;
import com.fang.starfang.ui.main.adapter.PickHeroSimRealmAdapter;

import org.apache.commons.lang3.math.NumberUtils;

import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class PickHeroDialogFragment extends UpdateDialogFragment {


    public static PickHeroDialogFragment newInstance( int itemID ) {
        Bundle args = new Bundle();
        args.putInt(AppConstant.INTENT_KEY_ITEM_ID, itemID);
        PickHeroDialogFragment pickHeroDialogFragment = new PickHeroDialogFragment();
        pickHeroDialogFragment.setArguments(args);
        return pickHeroDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = View.inflate(mActivity, R.layout.dialog_pick_hero, null);
        Bundle args = getArguments();
        int itemID = 0;
        if(args != null) {
            itemID = args.getInt(AppConstant.INTENT_KEY_ITEM_ID);
        }

        ItemSim itemSim = realm.where(ItemSim.class).equalTo(ItemSim.FIELD_ID,itemID).findFirst();
        if( itemSim != null ) {
            Item item = itemSim.getItem();
            if (item != null) {

                String itemSubCate = item.getItemSubCate(); // 검, 노, 전포, .., 보조구
                ItemCate itemCate = realm.where(ItemCate.class).equalTo(ItemCate.FIELD_SUB_CATE, itemSubCate).findFirst();
                if (itemCate != null) {
                    RealmResults<Heroes> heroesRealmResults;
                    String itemRestrictionBranch = item.getItemRestrictionBranch();
                    RealmQuery<Heroes> heroesRealmQuery = realm.where(Heroes.class).beginGroup().alwaysFalse();

                    if (itemRestrictionBranch != null) {
                        String[] branches = itemRestrictionBranch.split(",");
                        RealmList<Branch> branchList = new RealmList<>();
                        for (String branchStr : branches) {
                            int branchNo = NumberUtils.toInt(branchStr);
                            Branch branch = realm.where(Branch.class).equalTo(Branch.FIELD_ID, branchNo).findFirst();
                            if (branch != null) {
                                branchList.add(branch);
                            }
                        } // end for
                        if (!branchList.isEmpty()) {
                            for (Branch branch : branchList) {
                                heroesRealmQuery.or().equalTo(Heroes.FIELD_BRANCH_ID, branch.getBranchNo());
                            }

                        }
                    } else {
                        RealmQuery<Branch> branchRealmQuery = realm.where(Branch.class);

                        switch (itemCate.getItemMainCate()) {
                            case AppConstant.WEAPON_KOR:
                                branchRealmQuery.equalTo(Branch.FIELD_CATE_WEAPON, itemSubCate);
                                break;
                            case AppConstant.ARMOR_KOR:
                                branchRealmQuery.equalTo(Branch.FIELD_CATE_ARMOR, itemSubCate);
                                break;
                            default:
                        }// end switch
                        RealmResults<Branch> branchRealmResults = branchRealmQuery.findAll();
                        if (!branchRealmResults.isEmpty()) {
                            for (Branch branch : branchRealmResults) {
                                heroesRealmQuery.or().equalTo(Heroes.FIELD_BRANCH_ID, branch.getBranchNo());
                            }
                        }
                    }
                    heroesRealmResults = heroesRealmQuery.endGroup().findAll();

                    final PickHeroSimRealmAdapter pickHeroRealmAdapter = new PickHeroSimRealmAdapter(heroesRealmResults);
                    final RecyclerView recycler_view_pick_hero = view.findViewById(R.id.recycler_view_pick_hero);
                    recycler_view_pick_hero.setLayoutManager(new LinearLayoutManager(mActivity));
                    recycler_view_pick_hero.setAdapter(pickHeroRealmAdapter);

                    builder.setView(view).setPositiveButton(R.string.wear_kor, (dialog, which) -> {
                        Heroes hero_selected = pickHeroRealmAdapter.getSelectedHero();
                        if (hero_selected != null) {
                            HeroSim hero_before = itemSim.getHeroWhoHasThis();
                            HeroSim heroSim = realm.where(HeroSim.class).equalTo(HeroSim.FIELD_ID, hero_selected.getHeroNo()).findFirst();
                            if (heroSim != null) {
                                if(realm.isInTransaction()) {
                                    realm.commitTransaction();
                                }
                                realm.beginTransaction();
                                switch(itemCate.getItemMainCate()) {
                                    case AppConstant.WEAPON_KOR :
                                        if(hero_before != null ) {
                                            hero_before.setHeroWeapon(null);
                                        }
                                        ItemSim weapon_before = heroSim.getHeroWeapon();
                                        if(weapon_before != null ) {
                                            weapon_before.setHeroWhoHasThis(null);
                                        }
                                        heroSim.setHeroWeapon(itemSim);
                                        break;
                                    case AppConstant.ARMOR_KOR :
                                        if(hero_before != null ) {
                                            hero_before.setHeroArmor(null);
                                        }
                                        ItemSim armor_before = heroSim.getHeroArmor();
                                        if(armor_before != null ) {
                                            armor_before.setHeroWhoHasThis(null);
                                        }
                                        heroSim.setHeroArmor(itemSim);
                                        break;
                                        default:
                                            if(hero_before != null ) {
                                                hero_before.setHeroAid(null);
                                            }
                                            ItemSim aid_before = heroSim.getHeroAid();
                                            if(aid_before != null ) {
                                                aid_before.setHeroWhoHasThis(null);
                                            }
                                        heroSim.setHeroAid(itemSim);
                                } // end switch
                                itemSim.setHeroWhoHasThis(heroSim);
                                //realm.commitTransaction();
                                String message = item.getItemName() + ": " + hero_selected.getHeroName() + " " +  (hero_before == null ? resources.getString(R.string.wear_kor) :
                                        ( "← " + hero_before.getHero().getHeroName() + " " + resources.getString(R.string.modified_kor) ) );
                                onUpdateEventListener.updateEvent(AppConstant.RESULT_CODE_SUCCESS_MODIFY_ITEM, message );
                            }
                        }
                    }).setNegativeButton(R.string.cancel_kor, null);
                }
            }
        }
        return builder.create();
    }

}