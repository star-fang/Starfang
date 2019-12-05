package com.fang.starfang.ui.main.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.AppConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.local.model.realm.source.ItemCate;
import com.fang.starfang.ui.main.recycler.adapter.ItemsRealmAdapter;
import com.fang.starfang.util.ScreenUtils;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmException;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class AddItemDialogFragment extends UpdateDialogFragment {

    private static final String TAG = "FANG_DIALOG_ADD_ITEM";

    public static AddItemDialogFragment newInstance() {
        return new AddItemDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = View.inflate(mActivity, R.layout.dialog_add_item, null);
        final AppCompatTextView text_dialog_add_item_info = view.findViewById(R.id.text_dialog_add_item_info);
        final AppCompatTextView text_dialog_add_item_desc = view.findViewById(R.id.text_dialog_add_item_desc);
        final RecyclerView recycler_view_all_items = view.findViewById(R.id.recycler_view_all_items);
        recycler_view_all_items.setLayoutManager(new GridLayoutManager(mActivity, ScreenUtils.calculateNoOfColumns(mActivity,80.0)));
        ItemsRealmAdapter itemsRealmAdapter = new ItemsRealmAdapter(realm, text_dialog_add_item_info, text_dialog_add_item_desc);
        recycler_view_all_items.setAdapter(itemsRealmAdapter);

        final AppCompatSpinner spinner_item_grade = view.findViewById(R.id.spinner_item_grade);
        final AppCompatSpinner spinner_item_category_main = view.findViewById(R.id.spinner_item_category_main);
        final AppCompatSpinner spinner_item_category_sub = view.findViewById(R.id.spinner_item_category_sub);

        RealmResults<Item> grades = realm.where(Item.class).distinct(Item.FIELD_GRD).findAll().sort(Item.FIELD_GRD, Sort.DESCENDING);
        List<String> gradeList = new ArrayList<>();
        gradeList.add(AppConstant.ALL_PICK_KOR);
        for( Item grade : grades ) {
            String gradeStr = grade.getItemGrade();
            gradeStr += NumberUtils.isDigits(gradeStr)? AppConstant.GRADE_KOR : "";
            gradeList.add(gradeStr);
        }
        ArrayAdapter<String> gradesAdapter = new ArrayAdapter<>(
                mActivity,
                android.R.layout.simple_spinner_dropdown_item,  gradeList
        );
        spinner_item_grade.setAdapter(gradesAdapter);
        RealmResults < ItemCate > itemCates_main = realm.where(ItemCate.class).distinct(ItemCate.FIELD_MAIN_CATE).findAll();
        List<String> mainCategoryList = new ArrayList<>();
        mainCategoryList.add(AppConstant.ALL_PICK_KOR);
        for( ItemCate cate_main : itemCates_main) {
            mainCategoryList.add(cate_main.getItemMainCate());
        }
        ArrayAdapter<String> mainCateAdapter = new ArrayAdapter<>(
                mActivity,
        android.R.layout.simple_spinner_dropdown_item, mainCategoryList);
        spinner_item_category_main.setAdapter(mainCateAdapter);

        List<String> subCategoryList = new ArrayList<>();
        ArrayAdapter<String> subCateAdapter = new ArrayAdapter<>(
                mActivity,
                android.R.layout.simple_spinner_dropdown_item, subCategoryList);
        spinner_item_category_sub.setAdapter(subCateAdapter);

        spinner_item_grade.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selected_grade = spinner_item_grade.getItemAtPosition(position).toString();
                        String selected_category_main = spinner_item_category_main.getSelectedItem().toString();
                        Object selected_sub_cate = spinner_item_category_sub.getSelectedItem();
                        String selected_category_sub = selected_sub_cate == null ? AppConstant.ALL_PICK_KOR : selected_sub_cate.toString();
                        itemsRealmAdapter.getFilter().filter(selected_grade
                                + AppConstant.CONSTRAINT_SEPARATOR + selected_category_main + AppConstant.CONSTRAINT_SEPARATOR + selected_category_sub );
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
                        String selected_category_main = spinner_item_category_main.getItemAtPosition(position).toString();
                        String selected_grade = spinner_item_grade.getSelectedItem().toString();
                        RealmQuery<ItemCate> categories_sub_query = realm.where(ItemCate.class);
                        if(!selected_category_main.equals(AppConstant.ALL_PICK_KOR)) {
                            categories_sub_query.equalTo(ItemCate.FIELD_MAIN_CATE, selected_category_main);
                        }
                        RealmResults<ItemCate> categories_sub = categories_sub_query.findAll();

                        subCategoryList.clear();
                        subCategoryList.add(AppConstant.ALL_PICK_KOR);
                        for( ItemCate cate_sub : categories_sub) {
                            subCategoryList.add(cate_sub.getItemSubCate() );
                        }
                        subCateAdapter.notifyDataSetChanged();
                        spinner_item_category_sub.setSelection(0);
                        Object selected_sub_cate = spinner_item_category_sub.getSelectedItem();
                        String selected_category_sub = selected_sub_cate == null ? AppConstant.ALL_PICK_KOR : selected_sub_cate.toString();

                        itemsRealmAdapter.getFilter().filter(selected_grade
                                + AppConstant.CONSTRAINT_SEPARATOR + selected_category_main + AppConstant.CONSTRAINT_SEPARATOR + selected_category_sub );
                        Log.d(TAG,"category_main selected :" + selected_category_main);


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        spinner_item_category_sub.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        String selected_category_sub = spinner_item_category_sub.getItemAtPosition(position).toString();
                        String selected_category_main = spinner_item_category_main.getSelectedItem().toString();
                        String selected_grade = spinner_item_grade.getSelectedItem().toString();
                        itemsRealmAdapter.getFilter().filter(selected_grade
                                + AppConstant.CONSTRAINT_SEPARATOR + selected_category_main + AppConstant.CONSTRAINT_SEPARATOR + selected_category_sub );
                        Log.d(TAG,"category_sub selected :" + selected_category_sub);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                }
        );

        builder.setView(view).setPositiveButton(R.string.create_kor, (dialogInterface, i) -> {
            Item item_selected = itemsRealmAdapter.getSelectedItem();
            if(item_selected!= null) {
                try {
                    ItemSim itemSim = new ItemSim(item_selected);
                    //Log.d(TAG,"itemSim" + itemSim.getItemID() + " created");
                    realm.beginTransaction();
                    realm.copyToRealm(itemSim);
                    realm.commitTransaction();
                    //Log.d(TAG,"copy to realm : success");
                    onUpdateEventListener.updateEvent(AppConstant.RESULT_CODE_SUCCESS_ADD_ITEM);
                } catch( RealmPrimaryKeyConstraintException | RealmException e ) {
                    Log.d(TAG,e.toString());
                }
            }
        }).setNegativeButton(R.string.cancel_kor, null);

        return builder.create();
    }

}