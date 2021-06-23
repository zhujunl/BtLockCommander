package com.miaxis.btlockcommanderdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.model.entity.NbPerson;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ServerCommandAdapter<T> extends RecyclerView.Adapter<ServerCommandAdapter.MyViewHolder> {

    private List<T> dataList;
    private LayoutInflater layoutInflater;
    private OnItemClickListener mOnItemClickListener;

    public ServerCommandAdapter(Context context, List<T> dataList) {
        this.dataList = dataList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_server_command, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ServerCommandAdapter.MyViewHolder holder, int position) {
        NbPerson nbPerson = (NbPerson) dataList.get(position);
        holder.tvPersonName.setText(nbPerson.getName());
        holder.tvState.setText(nbPerson.getPersonId());
        holder.btnAction.setVisibility(nbPerson.isOpen() ? View.VISIBLE : View.INVISIBLE);
        holder.btnAction.setOnClickListener(v -> mOnItemClickListener.onItemClick(holder.btnAction, holder.getLayoutPosition()));
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
        @BindView(R.id.tv_state)
        TextView tvState;
        @BindView(R.id.btn_action)
        Button btnAction;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}