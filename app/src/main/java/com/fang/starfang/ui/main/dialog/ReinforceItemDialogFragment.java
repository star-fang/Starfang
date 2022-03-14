package com.fang.starfang.ui.main.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.FangConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.local.task.Reinforcement;
import com.fang.starfang.ui.creative.UpdateDialogFragment;
import com.fang.starfang.ui.main.adapter.ItemPowersRecyclerAdapter;

import io.realm.RealmList;

import java.util.ArrayList;

public class ReinforceItemDialogFragment extends UpdateDialogFragment {

    public static ReinforceItemDialogFragment newInstance(int itemID, int itemMainCate ) {

        Bundle args = new Bundle();
        args.putInt(FangConstant.INTENT_KEY_ITEM_ID, itemID);
        args.putInt(FangConstant.INTENT_KEY_ITEM_CATE_MAIN, itemMainCate);
        ReinforceItemDialogFragment reinforceDialogFragment = new ReinforceItemDialogFragment();
        reinforceDialogFragment.setArguments(args);
        return reinforceDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(mContext, R.layout.dialog_reinforce_item, null);
        Bundle args = getArguments();
        if(args != null) {
            int itemID = args.getInt(FangConstant.INTENT_KEY_ITEM_ID);
            int itemMainCate = args.getInt(FangConstant.INTENT_KEY_ITEM_CATE_MAIN);
            ItemSim itemSim = realm.where(ItemSim.class).equalTo(ItemSim.FIELD_ID, itemID).findFirst();
            if (itemSim != null) {
                Item item = itemSim.getItem();
                if (item != null) {

                    Resources resources = getResources();
                    String grade_no_reinforce = resources.getString(R.string.grade_no_reinforce);

                    final AppCompatSeekBar seek_bar_item_reinforce = view.findViewById(R.id.seek_bar_item_reinforce);
                    int curReinforce = itemSim.getItemReinforcement();
                    seek_bar_item_reinforce.setProgress(curReinforce);
                    final AppCompatEditText text_seek_bar_item_grade_value = view.findViewById(R.id.text_seek_bar_item_grade_value);
                    text_seek_bar_item_grade_value.setText(String.valueOf(curReinforce));

                    RealmList<RealmInteger> itemBasePowers = item.getItemStats();
                    ArrayList<Integer> itemPlusPowers = itemSim.getItemPlusPowersList();
                    ArrayList<Integer> itemPowers = itemSim.getItemPowersList();
                    Reinforcement reinforcement = new Reinforcement(realm, item);
                    final ItemPowersRecyclerAdapter itemPowersRecyclerAdapter = new ItemPowersRecyclerAdapter(
                            itemBasePowers, itemPlusPowers, itemPowers, reinforcement);
                    itemPowersRecyclerAdapter.setReinforceValue(curReinforce);
                    final RecyclerView recycler_view_dialog_reinforce_power = view.findViewById(R.id.recycler_view_dialog_reinforce_power);
                    recycler_view_dialog_reinforce_power.setLayoutManager(new LinearLayoutManager(mContext));
                    recycler_view_dialog_reinforce_power.setAdapter(itemPowersRecyclerAdapter);

                    final AppCompatTextView text_dialog_item_desc = view.findViewById(R.id.text_dialog_item_desc);
                    text_dialog_item_desc.setText(item.getItemDescription());

                    final AppCompatTextView dialog_title_item_name = view.findViewById(R.id.dialog_title_item_name);
                    final AppCompatTextView dialog_title_item_reinforce = view.findViewById(R.id.dialog_title_item_reinforce);
                    final AppCompatTextView dialog_title_item_grade = view.findViewById(R.id.dialog_title_item_grade);
                    dialog_title_item_name.setText(item.getItemName());
                    dialog_title_item_reinforce.setText(String.valueOf(curReinforce));
                    dialog_title_item_grade.setText(item.getItemGrade());

                    seek_bar_item_reinforce.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            itemPowersRecyclerAdapter.setReinforceValue(progress);
                            itemPowersRecyclerAdapter.notifyDataSetChanged();
                            String progressStr = String.valueOf(progress);
                            text_seek_bar_item_grade_value.setText(progressStr);
                            dialog_title_item_reinforce.setText(progressStr);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    builder.setView(view).setPositiveButton(R.string.reinforcement_kor, (dialog, which) -> {
                        int reinforceValue = seek_bar_item_reinforce.getProgress();

                        realm.executeTransactionAsync( bgRealm-> {
                            ItemSim bgItemSim = bgRealm.where(ItemSim.class).equalTo(ItemSim.FIELD_ID, itemID).findFirst();
                            if(bgItemSim != null ) {
                                for (int i = 0; i < HeroSim.POWERS_KOR.length; i++) {
                                    bgItemSim.setItemPlusPowers(itemPlusPowers.get(i), i);
                                    bgItemSim.setItemPowers(itemPowers.get(i), i);
                                }
                                bgItemSim.setItemReinforcement(reinforceValue);
                            }
                        }, () -> {
                            Fragment targetFragment = getTargetFragment();
                            if (targetFragment != null) {
                                Intent intent = new Intent();
                                intent.putExtra(FangConstant.INTENT_KEY_ITEM_CATE_MAIN,itemMainCate);
                                intent.putExtra(FangConstant.INTENT_KEY_ITEM_REINFORCE,
                                        grade_no_reinforce.equals(itemSim.getItem().getItemGrade()) ?
                                                "" : "+" + reinforceValue);
                                targetFragment.onActivityResult(FangConstant.REQ_CODE_REINFORCE_ITEM_DIALOG_FRAGMENT, Activity.RESULT_OK, intent);
                            } else {
                                String message = item.getItemName() + ": +" + reinforceValue + " " + resources.getString(R.string.reinforcement_kor);
                                for(OnUpdateEventListener listener : listeners ) {
                                    listener.updateEvent(FangConstant.RESULT_CODE_SUCCESS, message, null);
                                }
                            }
                        });




                    }).setNegativeButton(R.string.cancel_kor, null);
                }
            }
        }
        return builder.create();
    }


}