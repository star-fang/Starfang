package com.fang.starfang.ui.main.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.FangConstant;
import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.simulator.HeroSim;
import com.fang.starfang.local.model.realm.source.Heroes;
import com.fang.starfang.ui.creative.DiagonalScrollRecyclerView;
import com.fang.starfang.ui.creative.DynamicSheets;
import com.fang.starfang.ui.creative.UpdateDialogFragment;
import com.fang.starfang.ui.main.adapter.HeroesFixedRealmAdapter;
import com.fang.starfang.ui.main.adapter.HeroesFloatingRealmAdapter;
import com.google.android.material.snackbar.Snackbar;

import io.realm.OrderedRealmCollection;
public class HeroesFragment extends PlaceholderFragment implements UpdateDialogFragment.OnUpdateEventListener {

    private final static String TAG = "FANG_FRAG_HERO";

    static HeroesFragment newInstance(int index) {
        HeroesFragment heroesFragment = new HeroesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        heroesFragment.setArguments(bundle);
        return heroesFragment;
    }

    private HeroesFixedRealmAdapter heroFixAdapter;
    private HeroesFloatingRealmAdapter heroFloatAdapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        final View child_sim = inflater.inflate(R.layout.fragment_heroes_dynamic, container, false);

        OrderedRealmCollection<HeroSim> heroCollection = realm.where(HeroSim.class).findAll();
        HeroesFixedRealmAdapter heroFixAdapter = new HeroesFixedRealmAdapter(heroCollection, getParentFragmentManager());
        HeroesFloatingRealmAdapter heroFloatAdapter = new HeroesFloatingRealmAdapter(heroCollection, getParentFragmentManager(), mActivity);

        DynamicSheets sheets = child_sim.findViewById(R.id.dynamic_heroes);
        sheets.setAdapter(heroFixAdapter,0);
        sheets.setAdapter(heroFloatAdapter,1);

        return child_sim;

    }

    @Override
    public void updateEvent(int resultCode, String message, int[] pos) {
        if (resultCode == FangConstant.RESULT_CODE_SUCCESS) {
            //heroFixAdapter.notifyDataSetChanged();
            // heroFloatAdapter.notifyDataSetChanged();
        }

        View view = getView();
        if (message != null && view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
            //showCancelableSnackBar(view, message,notifyType);
        }
    }

}
