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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.ui.main.recycler.adapter.ItemSimsRealmAdapter;
import com.fang.starfang.util.NotifyUtils;
import com.fang.starfang.util.ScreenUtils;

import io.realm.Realm;


public class PickItemSimDialogFragment extends DialogFragment {

    private static final String TAG = "FANG_DIALOG_ITEM_PICK";
    private Activity mActivity;
    private Realm realm;

    public static PickItemSimDialogFragment newInstance( int heroID, String itemSubCate, int itemMainCate ) {
        Bundle args = new Bundle();
        args.putInt("heroID", heroID);
        args.putString("itemSubCate", itemSubCate);
        args.putInt("itemMainCate", itemMainCate);
        PickItemSimDialogFragment pickItemSimDialogFragment = new PickItemSimDialogFragment();
        pickItemSimDialogFragment.setArguments(args);
        return pickItemSimDialogFragment;
    }

    public PickItemSimDialogFragment() {
        Log.d(TAG, "constructed");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.d(TAG, "_ON ATTACH");
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = View.inflate(mActivity, R.layout.dialog_heroes_pick_item, null);
        realm = Realm.getDefaultInstance();

        Bundle args = getArguments();
        if( args != null ) {
            int heroID = args.getInt("heroID");
            HeroSim heroSim = realm.where(HeroSim.class).equalTo(HeroSim.FIELD_ID, heroID).findFirst();
            if(heroSim != null) {
                String itemSubCate = args.getString("itemSubCate");
                int itemMainCate = args.getInt("itemMainCate");

                final RecyclerView recycler_view_pick_item_sim = view.findViewById(R.id.recycler_view_pick_item_sim);
                recycler_view_pick_item_sim.setLayoutManager(new GridLayoutManager(mActivity, ScreenUtils.calculateNoOfColumns(mActivity, 75)));
                final ItemSimsRealmAdapter itemSimsRealmAdapter = new ItemSimsRealmAdapter(realm, view.findViewById(R.id.text_dialog_pick_item_info));


                switch (itemMainCate) {
                    case 0:
                        itemSimsRealmAdapter.getFilter().filter(AddItemDialogFragment.ALL_PICK_KOR +
                                "," + AddItemDialogFragment.WEAPON_KOR + "," + itemSubCate);
                        break;
                    case 1:
                        itemSimsRealmAdapter.getFilter().filter(AddItemDialogFragment.ALL_PICK_KOR +
                                "," + AddItemDialogFragment.ARMOR_KOR + "," + itemSubCate);
                        break;
                    default:
                        itemSimsRealmAdapter.getFilter().filter(AddItemDialogFragment.ALL_PICK_KOR +
                                "," + AddItemDialogFragment.AID_KOR + "," + heroSim.getHero().getBranchNo());
                }

                recycler_view_pick_item_sim.setAdapter(itemSimsRealmAdapter);

                builder.setView(view).setPositiveButton(R.string.wear_kor, (dialogInterface, i) -> {
                    ItemSim itemSim_selected = itemSimsRealmAdapter.getSelectedItem();
                    if (itemSim_selected != null) {
                        HeroSim hero_before = itemSim_selected.getHeroWhoHasThis();
                            realm.beginTransaction();
                        switch (itemMainCate) {
                            case 0:
                                ItemSim weapon_before = heroSim.getHeroWeapon();
                                if( weapon_before != null ) {
                                    weapon_before.setHeroWhoHasThis( null );
                                }
                                if(hero_before != null ) {
                                    hero_before.setHeroWeapon( null );
                                }
                                heroSim.setHeroWeapon(itemSim_selected);
                                break;
                            case 1:
                                ItemSim armor_before = heroSim.getHeroArmor();
                                if( armor_before != null ) {
                                    armor_before.setHeroWhoHasThis( null );
                                }
                                if(hero_before != null ) {
                                    hero_before.setHeroArmor( null );
                                }
                                heroSim.setHeroArmor(itemSim_selected);
                                break;
                            default:
                                ItemSim aid_before = heroSim.getHeroAid();
                                if( aid_before != null ) {
                                    aid_before.setHeroWhoHasThis( null );
                                }
                                if(hero_before != null ) {
                                    hero_before.setHeroAid( null );
                                }
                                heroSim.setHeroAid(itemSim_selected);
                        }

                        itemSim_selected.setHeroWhoHasThis( heroSim );
                            realm.commitTransaction();
                        NotifyUtils.notyfyToMainAdapters();
                    }
                }).setNegativeButton(R.string.cancel_kor, null);
            }
        }
        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        realm.close();
        super.onDismiss(dialog);
        Log.d(TAG,"_ON DISMISS");
    }


}