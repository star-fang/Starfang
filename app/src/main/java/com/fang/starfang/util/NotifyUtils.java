package com.fang.starfang.util;

import com.fang.starfang.ui.main.recycler.adapter.HeroesFixedRealmAdapter;
import com.fang.starfang.ui.main.recycler.adapter.HeroesFloatingRealmAdapter;
import com.fang.starfang.ui.main.recycler.adapter.ItemSimsFixedRealmAdapter;
import com.fang.starfang.ui.main.recycler.adapter.ItemSimsFloatingRealmAdapter;

public class NotifyUtils {

    public static void notyfyToMainAdapters() {
        HeroesFixedRealmAdapter fixedInstance = HeroesFixedRealmAdapter.getInstance();
        if(fixedInstance != null ) {
            fixedInstance.notifyDataSetChanged();
        }
        HeroesFloatingRealmAdapter floatingInstance = HeroesFloatingRealmAdapter.getInstance();
        if( floatingInstance != null ) {
            floatingInstance.notifyDataSetChanged();
        }

        ItemSimsFixedRealmAdapter itemSimsFixedRealmAdapter = com.fang.starfang.ui.main.recycler.adapter.ItemSimsFixedRealmAdapter.getInstance();
        if( itemSimsFixedRealmAdapter != null ) {
            itemSimsFixedRealmAdapter.notifyDataSetChanged();
        }

        ItemSimsFloatingRealmAdapter itemSimsFloatingRealmAdapter = ItemSimsFloatingRealmAdapter.getInstance();
        if( itemSimsFloatingRealmAdapter != null ) {
            itemSimsFloatingRealmAdapter.notifyDataSetChanged();
        }
    }


    public static void notifyToAdapter(boolean hero_fixed, boolean hero_floating, boolean item_floating, boolean item_fixed ) {
        if(hero_fixed) {
            HeroesFixedRealmAdapter fixedInstance = HeroesFixedRealmAdapter.getInstance();
            if(fixedInstance != null ) {
                fixedInstance.notifyDataSetChanged();
            }
        }

        if (hero_floating) {
            HeroesFloatingRealmAdapter floatingInstance = HeroesFloatingRealmAdapter.getInstance();
            if( floatingInstance != null ) {
                floatingInstance.notifyDataSetChanged();
            }
        }

        if (item_fixed) {
            ItemSimsFixedRealmAdapter itemSimsFixedRealmAdapter = com.fang.starfang.ui.main.recycler.adapter.ItemSimsFixedRealmAdapter.getInstance();
            if( itemSimsFixedRealmAdapter != null ) {
                itemSimsFixedRealmAdapter.notifyDataSetChanged();
            }
        }

        if (item_floating) {
            ItemSimsFloatingRealmAdapter itemSimsFloatingRealmAdapter = ItemSimsFloatingRealmAdapter.getInstance();
            if( itemSimsFloatingRealmAdapter != null ) {
                itemSimsFloatingRealmAdapter.notifyDataSetChanged();
            }
        }

    }
}
