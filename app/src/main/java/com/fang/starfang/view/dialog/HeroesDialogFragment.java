package com.fang.starfang.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.Heroes;

public class HeroesDialogFragment extends DialogFragment {


    public static HeroesDialogFragment newInstance(Heroes hero ) {
        HeroesDialogFragment fragment = new HeroesDialogFragment();
        Bundle args = new Bundle();
        args.putString("hero_name",hero.getHeroName());
        args.putString("hero_branch",hero.getHeroBranch());
        args.putInt("hero_cost",hero.getHeroCost()+10);
        args.putString("hero_lineage",hero.getHeroLineage());
        fragment.setArguments(args);
        return fragment;
    }

   @NonNull
   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState ) {
       AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
       LayoutInflater inflater = getActivity().getLayoutInflater();
       View view = inflater.inflate(R.layout.dialog_heroes, null);
       EditText mName = view.findViewById(R.id.dialog_hero_name);
       mName.setText((String)getArguments().get("hero_name"));
       EditText mBranch = view.findViewById(R.id.dialog_hero_branch);
       mBranch.setText((String)getArguments().get("hero_branch"));
       EditText mCost = view.findViewById(R.id.dialog_hero_cost);
       mCost.setText(String.valueOf(getArguments().get("hero_cost")));
       EditText mLineage = view.findViewById(R.id.dialog_hero_lineage);
       mLineage.setText((String)getArguments().get("hero_lineage"));
       builder.setView(view).setPositiveButton("변경", (dialog, which) -> {

       }).setNegativeButton("취소",null);

       return builder.create();
   }

}
