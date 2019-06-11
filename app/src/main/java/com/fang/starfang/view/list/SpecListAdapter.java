package com.fang.starfang.view.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fang.starfang.R;
import com.fang.starfang.model.realm.Spec;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

public class SpecListAdapter extends RealmBaseAdapter<Spec> {

    private static class ViewHolder {
        TextView spec_name;
        TextView spec_desc;
    }

    public SpecListAdapter(OrderedRealmCollection<Spec> data) {
        super(data);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_spec,parent,false);

            viewHolder = new ViewHolder();

            viewHolder.spec_name = convertView.findViewById(R.id.spec_name);
            viewHolder.spec_desc = convertView.findViewById(R.id.spec_desc);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Spec spec = this.getItem(position);
        if( viewHolder != null ) {
            viewHolder.spec_name.setText(spec.getSpecName());
            viewHolder.spec_desc.setText(spec.getSpecDescription());




        }



        return convertView;
    }

    //public void setData(OrderedRealmCollection<Heroes> details) {
    //    this.adapterData = details;
    //   notifyDataSetChanged();
    // }


}
