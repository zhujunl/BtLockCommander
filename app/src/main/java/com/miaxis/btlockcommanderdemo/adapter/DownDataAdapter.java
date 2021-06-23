package com.miaxis.btlockcommanderdemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.model.entity.DownDataDto;
import com.miaxis.btlockcommanderdemo.util.ValueUtil;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DownDataAdapter<T> extends RecyclerView.Adapter<DownDataAdapter.MyViewHolder> {

    private List<T> dataList;
    private LayoutInflater layoutInflater;
    private OnItemClickListener mOnItemClickListener;

    public DownDataAdapter(Context context, List<T> dataList) {
        this.dataList = dataList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_down_data, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DownDataAdapter.MyViewHolder holder, int position) {
        DownDataDto downDataDto = (DownDataDto) dataList.get(position);
        holder.tvDownDataId.setText(String.valueOf(downDataDto.getId()));
        holder.tvDownDataName.setText(TextUtils.isEmpty(downDataDto.getTypeName()) ? "未知" : downDataDto.getTypeName());
        holder.tvCreateTime.setText(ValueUtil.simpleDateFormat.format(new Date(downDataDto.getCreateTime())));
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public void appendDataList(List<T> dataList) {
        this.dataList.addAll(dataList);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public T getData(int position) {
        return dataList.get(position);
    }

    public void addData(T t) {
        dataList.add(t);
        notifyDataSetChanged();
    }

    public void removeData(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_down_data_id)
        TextView tvDownDataId;
        @BindView(R.id.tv_down_data_name)
        TextView tvDownDataName;
        @BindView(R.id.tv_create_time)
        TextView tvCreateTime;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}