package com.example.webviewapp.common.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webviewapp.R;
import com.example.webviewapp.data.Record;

import java.util.List;

public class RecordRecyclerViewAdapter extends RecyclerView.Adapter<RecordRecyclerViewAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Record> records;
    private Context context;
    private Integer layoutResId;

    public RecordRecyclerViewAdapter(List<Record> records, Context context, Integer layoutResId) {
        this.records = records;
        this.context = context;
        this.layoutResId = layoutResId;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(inflater.inflate(layoutResId, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = records.get(position);
        holder.time.setText((int) record.getTime());
        holder.title.setText(record.getTitle());
        holder.details.setText(record.getDetails());
        //TODO:处理每个item的事件
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time;
        public TextView title;
        public TextView details;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            title = itemView.findViewById(R.id.title);
            details = itemView.findViewById(R.id.details);
        }

    }
}
