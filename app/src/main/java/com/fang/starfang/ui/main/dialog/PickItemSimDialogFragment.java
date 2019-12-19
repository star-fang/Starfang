package com.fang.starfang.ui.main.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.AppConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.ui.common.UpdateDialogFragment;
import com.fang.starfang.ui.main.adapter.PickItemSimRealmAdapter;
import com.fang.starfang.util.ScreenUtils;


public class PickItemSimDialogFragment extends UpdateDialogFragment {

    //private static final String TAG = "FANG_DIA_PICK_ITEM";

    public static PickItemSimDialogFragment newInstance( int heroID, String itemSubCate, int itemMainCate ) {

        Bundle args = new Bundle();
        args.putInt(AppConstant.INTENT_KEY_HERO_ID, heroID);
        args.putString(AppConstant.INTENT_KEY_ITEM_CATE_SUB, itemSubCate);
        args.putInt(AppConstant.INTENT_KEY_ITEM_CATE_MAIN, itemMainCate);
        PickItemSimDialogFragment pickItemSimDialogFragment = new PickItemSimDialogFragment();
        pickItemSimDialogFragment.setArguments(args);
        return pickItemSimDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = View.inflate(mActivity, R.layout.dialog_pick_item_sim, null);

        Bundle args = getArguments();
        if( args != null ) {
            int heroID = args.getInt(AppConstant.INTENT_KEY_HERO_ID);
            HeroSim heroSim = realm.where(HeroSim.class).equalTo(HeroSim.FIELD_ID, heroID).findFirst();
            if(heroSim != null) {
                String itemSubCate = args.getString(AppConstant.INTENT_KEY_ITEM_CATE_SUB);
                int itemMainCate = args.getInt(AppConstant.INTENT_KEY_ITEM_CATE_MAIN);

                final RecyclerView recycler_view_pick_item_sim = view.findViewById(R.id.recycler_view_pick_item_sim);
                recycler_view_pick_item_sim.setLayoutManager(new GridLayoutManager(mActivity, ScreenUtils.calculateNoOfColumns(mActivity, 75)));
                final PickItemSimRealmAdapter itemSimsRealmAdapter = new PickItemSimRealmAdapter(realm, view.findViewById(R.id.text_dialog_pick_item_info));


                switch (itemMainCate) {
                    case 0:
                        itemSimsRealmAdapter.getFilter().filter(
                                AppConstant.CONSTRAINT_SEPARATOR + AppConstant.WEAPON_KOR + AppConstant.CONSTRAINT_SEPARATOR + itemSubCate);
                        break;
                    case 1:
                        itemSimsRealmAdapter.getFilter().filter(
                                AppConstant.CONSTRAINT_SEPARATOR + AppConstant.ARMOR_KOR + AppConstant.CONSTRAINT_SEPARATOR + itemSubCate);
                        break;
                    default:
                        itemSimsRealmAdapter.getFilter().filter(
                                AppConstant.CONSTRAINT_SEPARATOR + AppConstant.AID_KOR + AppConstant.CONSTRAINT_SEPARATOR + heroSim.getHero().getBranchNo());
                }

                recycler_view_pick_item_sim.setAdapter(itemSimsRealmAdapter);

                builder.setView(view).setPositiveButton(R.string.wear_kor, (dialogInterface, i) -> {
                    ItemSim itemSim_selected = itemSimsRealmAdapter.getSelectedItem();
                    if (itemSim_selected != null) {
                        int itemID = itemSim_selected.getItemID();
                        realm.executeTransactionAsync(bgRealm -> {
                            HeroSim bgHeroSim = bgRealm.where(HeroSim.class).equalTo(HeroSim.FIELD_ID, heroID).findFirst();
                            ItemSim bgItemSim = bgRealm.where(ItemSim.class).equalTo(ItemSim.FIELD_ID,itemID).findFirst();
                            if(bgHeroSim != null && bgItemSim != null ) {
                                HeroSim hero_before = bgItemSim.getHeroWhoHasThis();
                                switch (itemMainCate) {
                                    case 0:
                                        ItemSim weapon_before = bgHeroSim.getHeroWeapon();
                                        if (weapon_before != null) {
                                            weapon_before.setHeroWhoHasThis(null);
                                        }
                                        if (hero_before != null) {
                                            hero_before.setHeroWeapon(null);
                                        }
                                        bgHeroSim.setHeroWeapon(bgItemSim);
                                        break;
                                    case 1:
                                        ItemSim armor_before = bgHeroSim.getHeroArmor();
                                        if (armor_before != null) {
                                            armor_before.setHeroWhoHasThis(null);
                                        }
                                        if (hero_before != null) {
                                            hero_before.setHeroArmor(null);
                                        }
                                        bgHeroSim.setHeroArmor(bgItemSim);
                                        break;
                                    default:
                                        ItemSim aid_before = bgHeroSim.getHeroAid();
                                        if (aid_before != null) {
                                            aid_before.setHeroWhoHasThis(null);
                                        }
                                        if (hero_before != null) {
                                            hero_before.setHeroAid(null);
                                        }
                                        bgHeroSim.setHeroAid(bgItemSim);
                                }
                                bgItemSim.setHeroWhoHasThis( bgHeroSim );
                            }


                        }, () -> {
                            Fragment targetFragment = getTargetFragment();
                            if(targetFragment != null) {
                                Intent intent = new Intent();
                                Item item = itemSim_selected.getItem();
                                intent.putExtra(AppConstant.INTENT_KEY_ITEM_NAME, item.getItemName());
                                intent.putExtra(AppConstant.INTENT_KEY_ITEM_ID, itemSim_selected.getItemID());
                                int reinforceValue = itemSim_selected.getItemReinforcement();
                                String itemGrade = item.getItemGrade();
                                String reinforceStr = AppConstant.ITEM_GRADE_NO_REINFORCE.equals(itemGrade) ? null :
                                        "+" + reinforceValue;
                                intent.putExtra(AppConstant.INTENT_KEY_ITEM_REINFORCE, reinforceStr );
                                intent.putExtra(AppConstant.INTENT_KEY_ITEM_CATE_MAIN, itemMainCate );
                                targetFragment.onActivityResult(AppConstant.REQ_CODE_PICK_ITEM_DIALOG_FRAGMENT,Activity.RESULT_OK,intent);
                            } else {
                                String message = heroSim.getHero().getHeroName() + " " + itemSim_selected.getItem().getItemName() + " " +   resources.getString(R.string.wear_kor);
                                onUpdateEventListener.updateEvent(AppConstant.RESULT_CODE_SUCCESS_MODIFY_ITEM, message);
                            }
                        });
                    }
                }).setNegativeButton(R.string.cancel_kor, null);
            }
        }
        return builder.create();
    }


}