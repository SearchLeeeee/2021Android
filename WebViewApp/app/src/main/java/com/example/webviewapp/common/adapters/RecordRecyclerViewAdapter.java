package com.example.webviewapp.common.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webviewapp.R;
import com.example.webviewapp.common.utils.DataUtils;
import com.example.webviewapp.data.Record;

import java.util.List;

public class RecordRecyclerViewAdapter extends RecyclerView.Adapter<RecordRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecordRecyclerViewAdapt";

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
        holder.date.setText(DataUtils.time2Date(record.getTime()));
        holder.title.setText(record.getTitle());
        holder.details.setText(record.getDetails());
        checkDate(holder, position, record);
        //TODO:处理每个item的事件
    }

    /**
     * 检查这条记录日期是否和上一条的相同，实现按日期分组
     * TODO：由于item会被回收，刷新页面会刷掉日期。从数据源入手？每个记录按日期分组
     *
     * @param holder
     * @param position
     * @param record
     */
    private void checkDate(ViewHolder holder, int position, Record record) {
        if (position - 1 >= 0) {
            String yesterday = DataUtils.time2Date(records.get(position - 1).getTime());
            String now = DataUtils.time2Date(record.getTime());
            if (now.equals(yesterday)) {
                holder.date.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public int getItemCount() {
        return records.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView title;
        public TextView details;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            title = itemView.findViewById(R.id.title);
            details = itemView.findViewById(R.id.details);
        }

    }
}
