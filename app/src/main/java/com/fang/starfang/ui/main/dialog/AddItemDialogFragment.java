package com.fang.starfang.ui.main.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.FangConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.ItemSim;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.local.model.realm.source.ItemCate;
import com.fang.starfang.ui.creative.UpdateDialogFragment;
import com.fang.starfang.ui.main.adapter.ItemsRealmAdapter;
import com.fang.starfang.util.ScreenUtils;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(mContext, R.layout.dialog_add_item, null);
        final AppCompatTextView text_dialog_add_item_info = view.findViewById(R.id.text_dialog_add_item_info);
        final AppCompatTextView text_dialog_add_item_desc = view.findViewById(R.id.text_dialog_add_item_desc);
        final RecyclerView recycler_view_all_items = view.findViewById(R.id.recycler_view_all_items);
        recycler_view_all_items.setLayoutManager(new GridLayoutManager(mContext, ScreenUtils.calculateNoOfColumns(mContext,80.0)));

        ItemsRealmAdapter itemsRealmAdapter = new ItemsRealmAdapter(
                realm.where(Item.class).findAll().sort(Item.FIELD_GRD,Sort.DESCENDING)
                , text_dialog_add_item_info
                , text_dialog_add_item_desc
        );
        recycler_view_all_items.setAdapter(itemsRealmAdapter);

        final NumberPicker picker_item_grade = view.findViewById(R.id.picker_item_grade);
        final NumberPicker picker_item_category_main = view.findViewById(R.id.picker_item_category_main);
        final NumberPicker picker_item_category_sub = view.findViewById(R.id.picker_item_category_sub);
        final String GRADE_KOR = resources.getString(R.string.grade_kor);
        final String ALL_PICK_KOR = resources.getString(R.string.all_pick_kor);

        try {
            RealmResults<Item> grades = realm.where(Item.class).distinct(Item.FIELD_GRD).findAll().sort(Item.FIELD_GRD, Sort.DESCENDING);
            ArrayList<String> gradeList = new ArrayList<>();
            gradeList.add(ALL_PICK_KOR);
            for (Item grade : grades) {
                if (grade != null) {
                    String gradeStr = grade.getItemGrade();
                    gradeStr += NumberUtils.isDigits(gradeStr) ? GRADE_KOR : "";
                    gradeList.add(gradeStr);
                }
            }

            picker_item_grade.setMinValue(0);
            picker_item_grade.setMaxValue(grades.size());
            picker_item_grade.setDisplayedValues(gradeList.toArray(new String[0]));

            RealmResults<ItemCate> itemCategories_main = realm.where(ItemCate.class).distinct(ItemCate.FIELD_MAIN_CATE).findAll();
            ArrayList<String> mainCategoryList = new ArrayList<>();
            mainCategoryList.add(ALL_PICK_KOR);
            for (ItemCate cate_main : itemCategories_main) {
                if (cate_main != null) {
                    String mainCateStr = cate_main.getItemMainCate();
                    mainCategoryList.add(mainCateStr);
                }
            }

            picker_item_category_main.setMinValue(0);
            picker_item_category_main.setMaxValue(itemCategories_main.size());
            picker_item_category_main.setDisplayedValues(mainCategoryList.toArray(new String[0]));


            RealmResults<ItemCate> itemCategories_sub = realm.where(ItemCate.class).distinct(ItemCate.FIELD_SUB_CATE).findAll();
            ArrayList<String> subCategoryList = new ArrayList<>();
            subCategoryList.add(ALL_PICK_KOR);
            for (ItemCate cate_sub : itemCategories_sub) {
                if (cate_sub != null) {
                    String subCateStr = cate_sub.getItemSubCate();
                    subCategoryList.add(subCateStr);
                }
            }

            picker_item_category_sub.setMinValue(0);
            picker_item_category_sub.setMaxValue(itemCategories_sub.size());
            picker_item_category_sub.setDisplayedValues(subCategoryList.toArray(new String[0]));


            picker_item_grade.setOnValueChangedListener((picker, oldVal, newVal) -> {
                String selected_grade = gradeList.get(newVal);
                String selected_category_main = mainCategoryList.get(picker_item_category_main.getValue());
                String selected_category_sub = subCategoryList.get(picker_item_category_sub.getValue());
                String cs = selected_grade + FangConstant.CONSTRAINT_SEPARATOR + selected_category_main + FangConstant.CONSTRAINT_SEPARATOR + selected_category_sub;
                itemsRealmAdapter.getFilter().filter(cs.replace(ALL_PICK_KOR,""));
            });


            picker_item_category_main.setOnValueChangedListener((picker, oldVal, newVal) -> {
                String selected_grade = gradeList.get(picker_item_grade.getValue());
                String selected_category_main = mainCategoryList.get(newVal);

                RealmQuery<ItemCate> categories_sub_query = realm.where(ItemCate.class);
                if (newVal > 0) {
                    categories_sub_query.equalTo(ItemCate.FIELD_MAIN_CATE, selected_category_main);
                }
                RealmResults<ItemCate> categories_sub = categories_sub_query.findAll();
                subCategoryList.clear();
                subCategoryList.add(ALL_PICK_KOR);
                for (ItemCate cate_sub : categories_sub) {
                    if (cate_sub != null) {
                        String subCateStr = cate_sub.getItemSubCate();
                        subCategoryList.add(subCateStr);
                    }
                }
                try {
                    picker_item_category_sub.setDisplayedValues(null);
                    picker_item_category_sub.setMinValue(0);
                    picker_item_category_sub.setMaxValue(categories_sub.size());
                    picker_item_category_sub.setDisplayedValues(subCategoryList.toArray(new String[0]));


                    String selected_category_sub = subCategoryList.get(picker_item_category_sub.getValue());
                    String cs = selected_grade + FangConstant.CONSTRAINT_SEPARATOR + selected_category_main + FangConstant.CONSTRAINT_SEPARATOR + selected_category_sub;
                    itemsRealmAdapter.getFilter().filter(cs.replace(ALL_PICK_KOR,""));
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.e(TAG,Log.getStackTraceString(e));
                }
            });

            picker_item_category_sub.setOnValueChangedListener((picker, oldVal, newVal) -> {
                String selected_grade = gradeList.get(picker_item_grade.getValue());
                String selected_category_main = mainCategoryList.get(picker_item_category_main.getValue());
                String selected_category_sub = subCategoryList.get(newVal);
                String cs = selected_grade + FangConstant.CONSTRAINT_SEPARATOR + selected_category_main + FangConstant.CONSTRAINT_SEPARATOR + selected_category_sub;
                itemsRealmAdapter.getFilter().filter(cs.replace(ALL_PICK_KOR,""));
            });


        } catch( IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            Log.e(TAG,Log.getStackTraceString(e));
        }
        builder.setView(view).setPositiveButton(R.string.create_kor, (dialogInterface, i) -> {
            Item item_selected = itemsRealmAdapter.getSelectedItem();
            if(item_selected != null) {
                final int itemNo = item_selected.getItemNo();
                    realm.executeTransactionAsync( bgRealm -> {
                        Item bgItem = bgRealm.where(Item.class).equalTo(Item.FIELD_NO, itemNo).findFirst();
                        if( bgItem != null ) {
                            try {
                                ItemSim itemSim = new ItemSim(bgItem);
                                bgRealm.copyToRealm(itemSim);
                            } catch (RealmPrimaryKeyConstraintException | RealmException e) {
                                Log.e(TAG,Log.getStackTraceString(e));
                            }



                        }
                    }, () -> {
                        final String message = item_selected.getItemName() + " " + resources.getString(R.string.added_kor);
                        for(OnUpdateEventListener listener : listeners ) {
                            listener.updateEvent(FangConstant.RESULT_CODE_SUCCESS, message, null);
                        }
                    });




            }
        }).setNegativeButton(R.string.cancel_kor, null);

        return builder.create();
    }

}