package com.fang.starfang.ui.main.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.source.Branch;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.local.model.realm.source.ItemCate;
import com.fang.starfang.ui.main.recycler.adapter.PickHeroRealmAdapter;
import com.fang.starfang.util.NotifyUtils;

import org.apache.commons.lang3.math.NumberUtils;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class PickHeroDialogFragment extends DialogFragment {

    private static final String TAG = "FANG_DIALOG_PICK_HERO";
    private Activity mActivity;
    private Realm realm;

    public static PickHeroDialogFragment newInstance( int itemID ) {

        Bundle args = new Bundle();
        args.putInt("itemID", itemID);
        PickHeroDialogFragment pickHeroDialogFragment = new PickHeroDialogFragment();
        pickHeroDialogFragment.setArguments(args);
        return pickHeroDialogFragment;

    }

    public PickHeroDialogFragment() {


        Log.d(TAG, "constructed");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.d(TAG, "_ON ATTATCH");
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = View.inflate(mActivity, R.layout.dialog_pick_hero, null);
        Bundle args = getArguments();
        int itemID = 0;
        if(args != null) {
            itemID = args.getInt("itemID");
        }

        realm = Realm.getDefaultInstance();
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
                            case AddItemDialogFragment.WEAPON_KOR:
                                branchRealmQuery.equalTo(Branch.FIELD_CATE_WEAPON, itemSubCate);
                                break;
                            case AddItemDialogFragment.AID_KOR:
                                branchRealmQuery.equalTo(Branch.FIELD_CATE_ARMOR, itemSubCate);
                                break;
                            default:
                        }// end switch\
                        RealmResults<Branch> branchRealmResults = branchRealmQuery.findAll();
                        if (!branchRealmResults.isEmpty()) {
                            for (Branch branch : branchRealmResults) {
                                heroesRealmQuery.or().equalTo(Heroes.FIELD_BRANCH_ID, branch.getBranchNo());
                            }
                        }
                    }
                    heroesRealmResults = heroesRealmQuery.endGroup().findAll();

                    final PickHeroRealmAdapter pickHeroRealmAdapter = new PickHeroRealmAdapter(heroesRealmResults);
                    final RecyclerView recycler_view_pick_hero = view.findViewById(R.id.recycler_view_pick_hero);
                    recycler_view_pick_hero.setLayoutManager(new LinearLayoutManager(mActivity));
                    recycler_view_pick_hero.setAdapter(pickHeroRealmAdapter);

                    builder.setView(view).setPositiveButton(R.string.wear_kor, (dialog, which) -> {
                        Heroes hero_selected = pickHeroRealmAdapter.getSelectedHero();
                        if (hero_selected != null) {
                            HeroSim hero_before = itemSim.getHeroWhoHasThis();
                            HeroSim heroSim = realm.where(HeroSim.class).equalTo(HeroSim.FIELD_ID, hero_selected.getHeroNo()).findFirst();
                            if (heroSim != null) {
                                realm.beginTransaction();
                                switch(itemCate.getItemMainCate()) {
                                    case "무기" :
                                        if(hero_before != null ) {
                                            hero_before.setHeroWeapon(null);
                                        }
                                        ItemSim weapon_before = heroSim.getHeroWeapon();
                                        if(weapon_before != null ) {
                                            weapon_before.setHeroWhoHasThis(null);
                                        }
                                        heroSim.setHeroWeapon(itemSim);
                                        break;
                                    case "방어구" :
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
                                realm.commitTransaction();
                                NotifyUtils.notifyToAdapter(true,true,true,true);
                            }
                        }
                    }).setNegativeButton(R.string.cancel_kor, null);
                }
            }
        }
        return builder.create();
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        realm.close();
        super.onDismiss(dialog);
    }
}