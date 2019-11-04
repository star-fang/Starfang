package com.fang.starfang.view.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.Destiny;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

public class DestinyListAdapter extends RealmBaseAdapter<Destiny> {

    public DestinyListAdapter(OrderedRealmCollection<Destiny> data) {
        super(data);
    }

    private static class ViewHolder {
        TextView des_name;
        TextView des_extras;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_heroes,parent,false);

            viewHolder = new ViewHolder();

            viewHolder.des_name = convertView.findViewById(R.id.hero_no);
            viewHolder.des_extras = convertView.findViewById(R.id.hero_line);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Destiny des = this.getItem( position);
        if( viewHolder != null ) {
            viewHolder.des_name.setText(des.getDesName());
            viewHolder.des_extras.setText(des.getDesCord());
        }



        return convertView;
    }


}
