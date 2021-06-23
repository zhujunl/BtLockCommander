package com.miaxis.btlockcommanderdemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.model.entity.PersonDto;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LockPersonAdapter<T> extends RecyclerView.Adapter<LockPersonAdapter.MyViewHolder> {

    private List<T> dataList;
    private LayoutInflater layoutInflater;
    private OnItemClickListener mOnItemClickListener;

    public LockPersonAdapter(Context context, List<T> dataList) {
        this.dataList = dataList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_lock_person, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final LockPersonAdapter.MyViewHolder holder, int position) {
//        PersonDto personDto = (PersonDto) dataList.get(position);
        holder.tvPersonName.setText("人员编号");
        holder.tvPersonId.setText((String) dataList.get(position));
    }

    public List<T> getDataList() {
        return dataList;
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

    public T getData(int position) {
        return dataList.get(position);
    }

    public void addData(T t) {
        dataList.add(t);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_person_name)
        TextView tvPersonName;
        @BindView(R.id.tv_person_id)
        TextView tvPersonId;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}