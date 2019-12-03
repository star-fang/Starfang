package com.fang.starfang.ui.main.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.AppConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.source.Item;
import com.fang.starfang.local.model.realm.source.ItemCate;
import com.fang.starfang.ui.main.dialog.AddItemDialogFragment;
import com.fang.starfang.ui.main.recycler.adapter.ItemSimsFixedRealmAdapter;
import com.fang.starfang.ui.main.recycler.adapter.ItemSimsFloatingRealmAdapter;
import com.fang.starfang.ui.main.recycler.custom.DiagonalScrollRecyclerView;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class ItemsFragment extends PlaceholderFragment {

    private static final String TAG = "FANG_FRAG_ITEM";
    private Realm realm;
    private FragmentManager fragmentManager;

    static ItemsFragment newInstance(int index) {
        ItemsFragment itemsFragment = new ItemsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        itemsFragment.setArguments(bundle);
        return itemsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.fragmentManager = getFragmentManager();
        //Log.d(TAG,"_ON CREATE");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
        //Log.d(TAG, "_ON DESTROY VIEW : realm instance closed");
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG,"_ON CREATE VIEW");

        realm = Realm.getDefaultInstance();
        final View view = inflater.inflate(R.layout.fragment_items, container, false);
        AppCompatButton button_add_item = view.findViewById(R.id.button_add_item);
        button_add_item.setOnClickListener( v -> {
            FragmentManager manager = getFragmentManager();
            if (manager != null) {
                AddItemDialogFragment.newInstance().show(manager,TAG);
            }
        });

        final RecyclerView recycler_view_items_fixed = view.findViewById(R.id.recycler_view_items_fixed);
        final RecyclerView recycler_view_items_floating = view.findViewById(R.id.recycler_view_items_floating);
        recycler_view_items_fixed.setLayoutManager(new LinearLayoutManager(mActivity));
        recycler_view_items_floating.setLayoutManager(new LinearLayoutManager(mActivity));
        final DiagonalScrollRecyclerView recycler_view_items_content = view.findViewById(R.id.recycler_view_items_content);
        recycler_view_items_content.setRecyclerView(recycler_view_items_floating);

        final ItemSimsFixedRealmAdapter itemSimsFixedRealmAdapter = new ItemSimsFixedRealmAdapter(realm, fragmentManager);
        recycler_view_items_fixed.setAdapter(itemSimsFixedRealmAdapter);

        final ItemSimsFloatingRealmAdapter itemSimsFloatingRealmAdapter = new ItemSimsFloatingRealmAdapter(realm, fragmentManager);
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


        final AppCompatSpinner spinner_item_grade = view.findViewById(R.id.spinner_item_sim_grade);
        final AppCompatSpinner spinner_item_category_main = view.findViewById(R.id.spinner_item_sim_category_main);
        final AppCompatSpinner spinner_item_category_sub = view.findViewById(R.id.spinner_item_sim_category_sub);

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
        RealmResults <ItemCate> itemCates_main = realm.where(ItemCate.class).distinct(ItemCate.FIELD_MAIN_CATE).findAll();
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
                        String cs = selected_grade + AppConstant.CONSTRAINT_SEPARATOR + selected_category_main + AppConstant.CONSTRAINT_SEPARATOR + selected_category_sub;
                        itemSimsFixedRealmAdapter.getFilter().filter(cs);
                        itemSimsFloatingRealmAdapter.getFilter().filter(cs);
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

                        String cs = selected_grade + AppConstant.CONSTRAINT_SEPARATOR + selected_category_main + AppConstant.CONSTRAINT_SEPARATOR + selected_category_sub;
                        itemSimsFixedRealmAdapter.getFilter().filter(cs);
                        itemSimsFloatingRealmAdapter.getFilter().filter(cs);
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
                        String cs = selected_grade + AppConstant.CONSTRAINT_SEPARATOR + selected_category_main + AppConstant.CONSTRAINT_SEPARATOR + selected_category_sub;
                        itemSimsFixedRealmAdapter.getFilter().filter(cs);
                        itemSimsFloatingRealmAdapter.getFilter().filter(cs);
                        Log.d(TAG,"category_sub selected :" + selected_category_sub);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                }
        );
        return view;

    }

}