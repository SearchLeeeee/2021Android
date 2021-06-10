package com.example.webviewapp.common.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webviewapp.R;
import com.example.webviewapp.common.utils.DataUtils;
import com.example.webviewapp.data.Record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordRecyclerViewAdapter extends RecyclerView.Adapter<RecordRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecordRecyclerViewAdapt";
    private OnItemClickListener onItemClickListener;

    private LayoutInflater inflater;
    private List<Record> records;
    private Context context;
    private Integer layoutResId;

    private Map<Integer, Boolean> checkboxMap = new HashMap<>();
    private Map<Integer, Boolean> dateMap = new HashMap<>();
    // 判断RecyclerView是否正在计算layout或滑动，不在计算的时候通知适配器更新
    private boolean onBind;

    public RecordRecyclerViewAdapter(List<Record> records, Context context, Integer layoutResId) {
        this.records = records;
        this.context = context;
        this.layoutResId = layoutResId;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(layoutResId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = records.get(position);
        holder.date.setText(DataUtils.time2Date(record.getTime()));
        holder.title.setText(record.getTitle());
        holder.details.setText(record.getDetails());
        checkDate(holder, position, record);

        // 关联点击行为
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(v, position));
            holder.itemView.setOnLongClickListener(v -> {
                onItemClickListener.onItemLongClick(v, position);
                return true;
            });
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxMap.clear();
                checkboxMap.put(position, true);
            } else {
                checkboxMap.remove(position);
            }
            if (!onBind) {
                notifyDataSetChanged();
            }
        });
        onBind = true;
        holder.checkBox.setChecked(checkboxMap != null && checkboxMap.containsKey(position));
        onBind = false;
    }

    /**
     * 检查这条记录日期是否和上一条的相同，实现按日期分组
     * TODO：由于item会被回收，刷新页面会刷掉日期。从数据源入手？每个记录按日期分组。或者用map记录位置？
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
                dateMap.put(position, true);
            }
        }
        if (dateMap.containsKey(position)) {
            holder.date.setVisibility(View.GONE);
        }
    }

    public long getSelectedPosition() {
        if (checkboxMap != null) {
            List<Integer> list = new ArrayList<>(checkboxMap.keySet());
            return records.get(list.get(0)).getPrimaryKey();
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView title;
        public TextView details;
        public CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            title = itemView.findViewById(R.id.title);
            details = itemView.findViewById(R.id.details);
            checkBox = itemView.findViewById(R.id.checkbox);
        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置点按和长按回调
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }
}
