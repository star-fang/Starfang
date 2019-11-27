package com.fang.starfang.ui.main.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.local.model.realm.source.ItemCate;
import com.fang.starfang.ui.main.recycler.adapter.ItemSimsFixedRealmAdapter;
import com.fang.starfang.ui.main.recycler.adapter.ItemsRealmAdapter;
import com.fang.starfang.util.ScreenUtils;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class AddItemDialogFragment extends DialogFragment {

    private static final String TAG = "FANG_ADD_ITEM_DIALOG";
    private Activity mActivity;
    private Realm realm;

    public static AddItemDialogFragment newInstance() {
        return new AddItemDialogFragment();
    }

    public AddItemDialogFragment() {
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
        View view = View.inflate(mActivity, R.layout.dialog_add_item, null);
        final AppCompatTextView text_dialog_add_item_info = view.findViewById(R.id.text_dialog_add_item_info);
        final AppCompatTextView text_dialog_add_item_desc = view.findViewById(R.id.text_dialog_add_item_desc);
        realm = Realm.getDefaultInstance();
        final RecyclerView recycler_view_all_items = view.findViewById(R.id.recycler_view_all_items);
        recycler_view_all_items.setLayoutManager(new GridLayoutManager(mActivity, ScreenUtils.calculateNoOfColumns(mActivity,80.0)));
        ItemsRealmAdapter itemsRealmAdapter = new ItemsRealmAdapter(realm, text_dialog_add_item_info, text_dialog_add_item_desc);
        recycler_view_all_items.setAdapter(itemsRealmAdapter);

        final AppCompatSpinner spinner_item_grade = view.findViewById(R.id.spinner_item_grade);
        final AppCompatSpinner spinner_item_category_main = view.findViewById(R.id.spinner_item_category_main);

        RealmResults<Item> grades = realm.where(Item.class).distinct(Item.FIELD_GRD).findAll();
        List<String> gradeList = new ArrayList<>();
        for( Item grade : grades ) {
            String gradeStr = grade.getItemGrade();
            gradeStr += NumberUtils.isDigits(gradeStr)? "등급" : "";
            gradeList.add(gradeStr);
        }
        ArrayAdapter<String> gradesAdapter = new ArrayAdapter<>(
                mActivity,
                android.R.layout.simple_spinner_dropdown_item,  gradeList
        );
        spinner_item_grade.setAdapter(gradesAdapter);
        RealmResults < ItemCate > itemCates = realm.where(ItemCate.class).distinct(ItemCate.FIELD_MAIN_CATE).findAll();
        List<String> categoryList = new ArrayList<>();
        categoryList.add("전체");
        for( ItemCate cate : itemCates) {
            categoryList.add(cate.getItemMainCate());
        }
        ArrayAdapter<String> cateAdapter = new ArrayAdapter<>(
                mActivity,
        android.R.layout.simple_spinner_dropdown_item, categoryList);

        spinner_item_category_main.setAdapter(cateAdapter);

        spinner_item_grade.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selected_grade = spinner_item_grade.getItemAtPosition(position).toString();
                        String selected_category = spinner_item_category_main.getSelectedItem().toString();
                        itemsRealmAdapter.getFilter().filter(selected_grade + "," + selected_category );
                        Log.d(TAG,"grade selected :" + selected_grade);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


        spinner_item_category_main.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selected_category = spinner_item_category_main.getItemAtPosition(position).toString();
                        String selected_grade = spinner_item_grade.getSelectedItem().toString();
                        itemsRealmAdapter.getFilter().filter(selected_grade + "," + selected_category );
                        Log.d(TAG,"category selected :" + selected_category);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        builder.setView(view).setPositiveButton(R.string.create_kor, (dialogInterface, i) -> {
            Item item_selected = itemsRealmAdapter.getSelectedItem();
            if(item_selected!= null) {
                try {
                    ItemSim itemSim = new ItemSim(item_selected);
                    Log.d(TAG,"itemSim" + itemSim.getItemID() + " created");
                    realm.beginTransaction();
                    realm.copyToRealm(itemSim);
                    realm.commitTransaction();
                    Log.d(TAG,"copy to realm : success");
                    ItemSimsFixedRealmAdapter itemSimsRealmAdapter = ItemSimsFixedRealmAdapter.getInstance();
                    if( itemSimsRealmAdapter != null ) {
                        itemSimsRealmAdapter.notifyDataSetChanged();
                    }
                } catch( RealmPrimaryKeyConstraintException | RealmException e ) {
                    Log.d(TAG,e.toString());
                }
            }
        }).setNegativeButton(R.string.cancel_kor, null);

        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        realm.close();
        super.onDismiss(dialog);
        Log.d(TAG,"_ON DISMISS");
    }


}