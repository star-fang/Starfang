package com.fang.starfang.ui.main.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.FangConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.local.model.realm.source.ItemCate;
import com.fang.starfang.ui.main.adapter.ItemSimsFixedRealmAdapter;
import com.fang.starfang.ui.main.adapter.ItemSimsFloatingRealmAdapter;
import com.fang.starfang.ui.common.DiagonalScrollRecyclerView;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;

import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class ItemsFragment extends PlaceholderFragment {

    private final static String TAG = "FANG_FRAG_ITEM";

    static ItemsFragment newInstance(int index) {
        ItemsFragment itemsFragment = new ItemsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        itemsFragment.setArguments(bundle);
        return itemsFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_items, container, false);
        final RecyclerView recycler_view_items_fixed = view.findViewById(R.id.recycler_view_items_fixed);
        final RecyclerView recycler_view_items_floating = view.findViewById(R.id.recycler_view_items_floating);
        recycler_view_items_fixed.setLayoutManager(new LinearLayoutManager(mActivity));
        recycler_view_items_floating.setLayoutManager(new LinearLayoutManager(mActivity));
        final DiagonalScrollRecyclerView recycler_view_items_content = view.findViewById(R.id.recycler_view_items_content);
        recycler_view_items_content.setRecyclerView(recycler_view_items_floating);

        final ItemSimsFixedRealmAdapter itemSimsFixedRealmAdapter = ItemSimsFixedRealmAdapter.getInstance();
        recycler_view_items_fixed.setAdapter(itemSimsFixedRealmAdapter);

        final ItemSimsFloatingRealmAdapter itemSimsFloatingRealmAdapter = ItemSimsFloatingRealmAdapter.getInstance();
        recycler_view_items_floating.setAdapter(itemSimsFloatingRealmAdapter);


        final RecyclerView.OnScrollListener[] itemRecyclerViewListeners =
                new RecyclerView.OnScrollListener[2];
        itemRecyclerViewListeners[0] = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recycler_view_items_fixed.removeOnScrollListener(itemRecyclerViewListeners[1]);
                recycler_view_items_fixed.scrollBy(0,dy);
                recycler_view_items_fixed.addOnScrollListener(itemRecyclerViewListeners[1]);
            }
        };

        itemRecyclerViewListeners[1] = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recycler_view_items_floating.removeOnScrollListener(itemRecyclerViewListeners[0]);
                recycler_view_items_floating.scrollBy(0,dy);
                recycler_view_items_floating.addOnScrollListener(itemRecyclerViewListeners[0]);
            }
        };

        recycler_view_items_floating.addOnScrollListener(itemRecyclerViewListeners[0]);
        recycler_view_items_fixed.addOnScrollListener(itemRecyclerViewListeners[1]);

        final NumberPicker picker_item_sim_grade = view.findViewById(R.id.picker_item_sim_grade);
        final NumberPicker picker_item_sim_category_main = view.findViewById(R.id.picker_item_sim_category_main);
        final NumberPicker picker_item_sim_category_sub = view.findViewById(R.id.picker_item_sim_category_sub);

        final String GRADE_KOR = getResources().getString(R.string.grade_kor);
        final String ALL_PICK_KOR = getResources().getString(R.string.all_pick_kor);

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

            picker_item_sim_grade.setMinValue(0);
            picker_item_sim_grade.setMaxValue(grades.size());
            picker_item_sim_grade.setDisplayedValues(gradeList.toArray(new String[0]));

            RealmResults<ItemCate> itemCategories_main = realm.where(ItemCate.class).distinct(ItemCate.FIELD_MAIN_CATE).findAll();
            ArrayList<String> mainCategoryList = new ArrayList<>();
            mainCategoryList.add(ALL_PICK_KOR);
            for (ItemCate cate_main : itemCategories_main) {
                if (cate_main != null) {
                    String mainCateStr = cate_main.getItemMainCate();
                    mainCategoryList.add(mainCateStr);
                }
            }

            picker_item_sim_category_main.setMinValue(0);
            picker_item_sim_category_main.setMaxValue(itemCategories_main.size());
            picker_item_sim_category_main.setDisplayedValues(mainCategoryList.toArray(new String[0]));


            RealmResults<ItemCate> itemCategories_sub = realm.where(ItemCate.class).distinct(ItemCate.FIELD_SUB_CATE).findAll();
            ArrayList<String> subCategoryList = new ArrayList<>();
            subCategoryList.add(ALL_PICK_KOR);
            for (ItemCate cate_sub : itemCategories_sub) {
                if (cate_sub != null) {
                    String subCateStr = cate_sub.getItemSubCate();
                    subCategoryList.add(subCateStr);
                }
            }

            picker_item_sim_category_sub.setMinValue(0);
            picker_item_sim_category_sub.setMaxValue(itemCategories_sub.size());
            picker_item_sim_category_sub.setDisplayedValues(subCategoryList.toArray(new String[0]));


            picker_item_sim_grade.setOnValueChangedListener((picker, oldVal, newVal) -> {
                String selected_grade = gradeList.get(newVal);
                String selected_category_main = mainCategoryList.get(picker_item_sim_category_main.getValue());
                String selected_category_sub = subCategoryList.get(picker_item_sim_category_sub.getValue());
                String cs = selected_grade + FangConstant.CONSTRAINT_SEPARATOR + selected_category_main + FangConstant.CONSTRAINT_SEPARATOR + selected_category_sub;
                itemSimsFixedRealmAdapter.getFilter().filter(cs.replace(ALL_PICK_KOR,""));
                itemSimsFloatingRealmAdapter.getFilter().filter(cs.replace(ALL_PICK_KOR,""));
            });


            picker_item_sim_category_main.setOnValueChangedListener((picker, oldVal, newVal) -> {
                String selected_grade = gradeList.get(picker_item_sim_grade.getValue());
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
                    picker_item_sim_category_sub.setDisplayedValues(null);
                    picker_item_sim_category_sub.setMinValue(0);
                    picker_item_sim_category_sub.setMaxValue(categories_sub.size());
                    picker_item_sim_category_sub.setDisplayedValues(subCategoryList.toArray(new String[0]));


                    String selected_category_sub = subCategoryList.get(picker_item_sim_category_sub.getValue());
                    String cs = selected_grade + FangConstant.CONSTRAINT_SEPARATOR + selected_category_main + FangConstant.CONSTRAINT_SEPARATOR + selected_category_sub;
                    itemSimsFixedRealmAdapter.getFilter().filter(cs.replace(ALL_PICK_KOR,""));
                    itemSimsFloatingRealmAdapter.getFilter().filter(cs.replace(ALL_PICK_KOR,""));
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.d(TAG,"subCate : " + e.toString());
                }
            });

            picker_item_sim_category_sub.setOnValueChangedListener((picker, oldVal, newVal) -> {
                String selected_grade = gradeList.get(picker_item_sim_grade.getValue());
                String selected_category_main = mainCategoryList.get(picker_item_sim_category_main.getValue());
                String selected_category_sub = subCategoryList.get(newVal);
                String cs = selected_grade + FangConstant.CONSTRAINT_SEPARATOR + selected_category_main + FangConstant.CONSTRAINT_SEPARATOR + selected_category_sub;
                itemSimsFixedRealmAdapter.getFilter().filter(cs.replace(ALL_PICK_KOR,""));
                itemSimsFloatingRealmAdapter.getFilter().filter(cs.replace(ALL_PICK_KOR,""));
            });


        } catch( IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            Log.d(TAG, e.toString());
        }


        return view;

    }

}