package com.fang.starfang.ui.main.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.primitive.RealmInteger;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.local.task.Reinforcement;
import com.fang.starfang.ui.main.recycler.adapter.ItemPowersRecyclerAdapter;
import com.fang.starfang.ui.main.recycler.adapter.ItemSimsFixedRealmAdapter;
import com.fang.starfang.ui.main.recycler.adapter.ItemSimsFloatingRealmAdapter;
import com.fang.starfang.util.NotifyUtils;

import io.realm.Realm;
import io.realm.RealmList;

import java.util.ArrayList;

public class ReinforceDialogFragment extends DialogFragment {

    private static final String TAG = "FANG_DIALOG_PICK_HERO";
    private Activity mActivity;
    private Realm realm;

    public static ReinforceDialogFragment newInstance( int itemID ) {

        Bundle args = new Bundle();
        args.putInt("itemID", itemID);
        ReinforceDialogFragment reinforceDialogFragment = new ReinforceDialogFragment();
        reinforceDialogFragment.setArguments(args);
        return reinforceDialogFragment;

    }

    public ReinforceDialogFragment() {


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
        View view = View.inflate(mActivity, R.layout.dialog_reinforce_item, null);
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
                final AppCompatSeekBar seek_bar_item_reinforce = view.findViewById(R.id.seek_bar_item_reinforce);
                int curReinforce = itemSim.getItemReinforcement();
                seek_bar_item_reinforce.setProgress(curReinforce);
                final AppCompatEditText text_seek_bar_item_grade_value = view.findViewById(R.id.text_seek_bar_item_grade_value);
                text_seek_bar_item_grade_value.setText(String.valueOf(curReinforce));

                RealmList<RealmInteger> itemBasePowers = item.getItemStats();
                ArrayList<Integer> itemPlusPowers = itemSim.getItemPlusPowersList();
                ArrayList<Integer> itemPowers = itemSim.getItemPowersList();
                Reinforcement reinforcement = new Reinforcement(realm, item);
                final ItemPowersRecyclerAdapter itemPowersRecyclerAdapter = new ItemPowersRecyclerAdapter(itemBasePowers, itemPlusPowers, itemPowers,reinforcement );
                itemPowersRecyclerAdapter.setReinforceValue( curReinforce );
                final RecyclerView recycler_view_dialog_reinforce_power = view.findViewById(R.id.recycler_view_dialog_reinforce_power);
                recycler_view_dialog_reinforce_power.setLayoutManager(new LinearLayoutManager(mActivity));
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
                        itemPowersRecyclerAdapter.setReinforceValue( progress );
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

                builder.setView(view).setPositiveButton("적용", (dialog, which) -> {
                    realm.beginTransaction();
                    for(int i = 0; i < HeroSim.POWERS_KOR.length; i++ ) {
                        itemSim.setItemPlusPowers( itemPlusPowers.get(i) , i);
                        itemSim.setItemPowers( itemPowers.get(i) , i);
                    }
                    itemSim.setItemReinforcement( seek_bar_item_reinforce.getProgress() );

                    realm.commitTransaction();

                    NotifyUtils.notyfyToMainAdapters();
                }).setNegativeButton(R.string.cancel_kor, null);
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