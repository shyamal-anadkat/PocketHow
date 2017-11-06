package edu.wisc.ece.pockethow.frontend;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import edu.wisc.ece.pockethow.R;

/**
 * Created by onglp on 11/4/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private String[] mDataset;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        //public CardCategory mCardView;

        final CardView cardView;
        final TextView textView;
        public ViewHolder(View v)
        {
            super(v);
            cardView = (CardView) itemView.findViewById(R.id.category_card_view);
            textView = (TextView) itemView.findViewById(R.id.category_text);
        }
            /*
            public ViewHolder(View v) {
                super(v);
                mCardView = v;
            }
*/
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public CardAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        /*TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);*/

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card_adapter, parent, false);

        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(mDataset[position]);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Toast.makeText(context, "This is my Toast message!",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, searchActivity.class);
                //intent.putExtra(PageDetailFragment.ARG_ITEM_ID, Long.toString(holder.mItem.getID()));
                //send the content of the selected article
                intent.putExtra(searchActivity.codeword, holder.textView.getText());
                //intent.putExtra(PHDBHandler.COLUMN_CONTENT, holder.mView.mContextView.toString());
                context.startActivity(intent);

            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
