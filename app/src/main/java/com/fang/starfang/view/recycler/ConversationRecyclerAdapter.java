package com.fang.starfang.view.recycler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fang.starfang.R;
import com.fang.starfang.local.model.realm.Conversation;

import java.lang.ref.WeakReference;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;

public class ConversationRecyclerAdapter extends RealmRecyclerViewAdapter<Conversation, RecyclerView.ViewHolder>{

    private static final String TAG = "FANG_CONVERSATION";
    private WeakReference<RecyclerView> recyclerViewWeakReference;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //Log.d(TAG,"onCreateViewHolder");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_conversation,viewGroup,false);
        return new ConversationRecyclerAdapter.ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        ConversationRecyclerAdapter.ConversationViewHolder conversationViewHolder =
                (ConversationRecyclerAdapter.ConversationViewHolder) viewHolder;

        Conversation conversation = getItem(i);
        conversationViewHolder.bind(conversation);

    }

    public ConversationRecyclerAdapter(Realm realm, RecyclerView recyclerView) {
        super(realm.where(Conversation.class).findAll(),true);
        //Log.d(TAG,"ConversationRecyclerAdapter constructed");
        realm.addChangeListener(o -> {
            notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(getItemCount());
        });
        recyclerView.smoothScrollToPosition(getItemCount());
        recyclerViewWeakReference = new WeakReference<>(recyclerView);
    }



    /*
    public void filterResults( String cs ) {
        cs = (cs == null) ? null : cs.toLowerCase().trim();
        RealmQuery<Heroes> query = realm.where(Heroes.class);
        if( !(cs == null || "".equals(cs))) {
            query.contains(Heroes.FIELD_NAME,cs, Case.INSENSITIVE);
        }
        updateData(query.findAll());
    }

    @Override
    public Filter getFilter() {
        return new HeroesRecyclerAdapter.HeroesFilter(this);
    }









    private class HeroesFilter extends Filter {
        private final HeroesRecyclerAdapter adapter;
        private HeroesFilter(HeroesRecyclerAdapter adapter) {
            super();
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filterResults(constraint.toString());

        }
    }
 */

    //https://stackoverflow.com/questions/48508017/how-to-update-recycleview-single-item-while-realm-database-update



    public class ConversationViewHolder extends RecyclerView.ViewHolder {
        private TextView sandCatTv;
        private TextView catRoomTv;
        private TextView timestampTv;
        private TextView conversationTv;

        private ConversationViewHolder(View itemView) {
            super(itemView);

            sandCatTv = itemView.findViewById(R.id.conv_sendCat);
            catRoomTv = itemView.findViewById(R.id.conv_catRoom);
            timestampTv = itemView.findViewById(R.id.conv_timestamp);
            conversationTv = itemView.findViewById(R.id.conv_conversation);
        }



        private void bind(final Conversation conversation ) {
            try {
                //Log.d(TAG,conversation.getSandCat());
                sandCatTv.setText(conversation.getSandCat());
                timestampTv.setText(conversation.getTimestamp());
                catRoomTv.setText(conversation.getCatRoom());
                conversationTv.setText(conversation.getConversation());

            } catch( NullPointerException ignored) {
            }
            //itemView.setOnClickListener(v -> {
            //});
        }


    }

}
