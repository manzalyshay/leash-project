package com.shaym.leash.ui.forecast;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shaym.leash.R;

import java.util.ArrayList;
import java.util.List;

public class DayPickerAdapter extends RecyclerView.Adapter<DayPickerAdapter.ViewHolder> {

    private List<String> mDays;
    int selected_position = 0; // You have to set this globally in the Adapter class
    onDaySelectedListener listener;



 DayPickerAdapter( onDaySelectedListener listener){
     this.listener = listener;
     mDays = new ArrayList<>();
}

    public void setDays(List<String> days) {
     mDays = days;
     notifyDataSetChanged();
    }


    @NonNull
    @Override
    public DayPickerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_day_picker, parent, false);

      return new ViewHolder(contactView, this); }

    @Override
    public void onBindViewHolder(@NonNull DayPickerAdapter.ViewHolder holder, int position) {
        holder.dayTextView.setText(mDays.get(position));

        if (selected_position == position){

            holder.itemView.setBackgroundResource(R.drawable.underline_cameras);
            listener.onDaySelected(position);
        }
        else {
            holder.itemView.setBackgroundResource(0);

        }
    }

    @Override
    public int getItemCount() {
        return mDays.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "ViewHolder";
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView dayTextView;
        DayPickerAdapter adapter;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolder(View itemView, DayPickerAdapter adapter) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            this.adapter = adapter;
            dayTextView = itemView.findViewById(R.id.day);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Below line is just like a safety check, because sometimes holder could be null,
            // in that case, getAdapterPosition() will return RecyclerView.NO_POSITION
            Log.d(TAG, "onClick: ");
            if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

            // Updating old as well as new positions
            adapter.notifyItemChanged(adapter.selected_position);
            adapter.selected_position = getAdapterPosition();
            adapter.notifyItemChanged(adapter.selected_position);

        }
    }
}
