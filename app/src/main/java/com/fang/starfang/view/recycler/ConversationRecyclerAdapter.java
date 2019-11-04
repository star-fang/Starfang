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

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;

public class ConversationRecyclerAdapter extends RealmRecyclerViewAdapter<Conversation, RecyclerView.ViewHolder>{

    private Realm realm;
    private FragmentManager fragmentManager;
    private static final String TAG = "FANG_CONVERSATION";

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_conversation,viewGroup,false);

        return new ConversationRecyclerAdapter.ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        ConversationRecyclerAdapter.ConversationViewHolder conversationViewHolder = (ConversationRecyclerAdapter.ConversationViewHolder) viewHolder;

        Conversation conversation = getItem(i);
        conversationViewHolder.bind(conversation);

    }

    public ConversationRecyclerAdapter(Realm realm, FragmentManager fragmentManager) {
        super(realm.where(Conversation.class).findAll(),false);
        this.realm = realm;
        this.fragmentManager = fragmentManager;

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



    public class ConversationViewHolder extends RecyclerView.ViewHolder {
        public TextView sandCatTv;
        public TextView catRoomTv;
        public TextView timestampTv;
        public TextView conversationTv;

        public ConversationViewHolder(View itemView) {
            super(itemView);

            sandCatTv = (TextView)itemView.findViewById(R.id.conv_sendCat);
            catRoomTv = (TextView)itemView.findViewById(R.id.conv_catRoom);
            timestampTv = (TextView)itemView.findViewById(R.id.conv_timestamp);
            conversationTv = (TextView)itemView.findViewById(R.id.conv_conversation);
        }



        public void bind(final Conversation conversation ) {
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
