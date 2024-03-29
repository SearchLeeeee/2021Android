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
import com.example.webviewapp.common.utils.DataFormatUtils;
import com.example.webviewapp.data.Record;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordRecyclerViewAdapter extends RecyclerView.Adapter<RecordRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecordRecyclerViewAdapt";
    private OnItemClickListener onItemClickListener;

    private final LayoutInflater inflater;
    private List<Record> records;
    private final Context context;
    private final Integer layoutResId;
    DataFormatUtils dataFormatUtils = new DataFormatUtils();

    private final Map<Integer, Boolean> checkboxMap = new HashMap<>();
    private final Map<Integer, Boolean> dateMap = new HashMap<>();
    public final boolean[] visible = {false};
    // 判断RecyclerView是否正在计算layout或滑动，不在计算的时候通知适配器更新
    private boolean onBind;

    public RecordRecyclerViewAdapter(List<Record> records, Context context, Integer layoutResId) {
        this.records = records;
        this.context = context;
        this.layoutResId = layoutResId;
        inflater = LayoutInflater.from(context);

        initDateMap(records);
    }

    /**
     * 记录不是一天中第一条记录的位置
     *
     * @param records
     */
    private void initDateMap(List<Record> records) {
        for (int i = 0; i < records.size() - 1; i++) {
            String today = DataFormatUtils.time2Date(records.get(i).getTime());
            String lastDay = DataFormatUtils.time2Date(records.get(i + 1).getTime());
            if (lastDay.equals(today)) {
                dateMap.put(i + 1, true);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(layoutResId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = records.get(position);
        initItemView(holder, position, record);
        initCheckBox(holder, position);
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(v, position));
            holder.itemView.setOnLongClickListener(v -> {
                onItemClickListener.onItemLongClick(v, position);
                visible[0] = true;
                return true;
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull @NotNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (visible[0]) holder.checkBox.setVisibility(View.VISIBLE);
        else holder.checkBox.setVisibility(View.INVISIBLE);
    }

    private void initCheckBox(@NonNull ViewHolder holder, int position) {
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxMap.put(position, true);
            } else {
                checkboxMap.remove(position);
            }
            if (!onBind) {
                notifyDataSetChanged();
            }
        });
        // 防止在刷新的时候notify
        onBind = true;
        holder.checkBox.setChecked(checkboxMap != null && checkboxMap.containsKey(position));
        onBind = false;
    }

    private void initItemView(@NonNull ViewHolder holder, int position, Record record) {
        if (!dateMap.containsKey(position)) {
            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(DataFormatUtils.time2Date(record.getTime()));
        } else {
            holder.date.setVisibility(View.GONE);
        }
        holder.checkBox.setVisibility(View.INVISIBLE);
        String title = dataFormatUtils.text2Show(20, record.getTitle());
        holder.title.setText(title);
        String url = dataFormatUtils.text2Show(30, record.getUrl());
        holder.url.setText(url);
    }

    public long getSelectedPosition() {
        if (checkboxMap.size() != 0) {
            List<Integer> list = new ArrayList<>(checkboxMap.keySet());
            return records.get(list.get(0)).getPrimaryKey();
        }
        return 0;
    }

    public List<String> getSelectedPositions() {
        List<String> res = new ArrayList<>();
        if (checkboxMap.size() != 0) {
            List<Integer> list = new ArrayList<>(checkboxMap.keySet());
            for (int i = 0; i < list.size(); i++)
                res.add(records.get(list.get(i)).getUrl());
//            return records.get(list.get(0)).getPrimaryKey();
        }
        return res;
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    /**
     * 设置点按和长按回调
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public interface CheckBoxSelectoe {
        List<Long> recordId(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView title;
        public TextView url;
        public CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            title = itemView.findViewById(R.id.title);
            url = itemView.findViewById(R.id.url);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
