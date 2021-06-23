package com.miaxis.btlockcommanderdemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.adapter.listener.SlideSwapAction;
import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;
import com.miaxis.btlockcommanderdemo.util.ValueUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BtDeviceSlideAdapter<T> extends RecyclerView.Adapter<BtDeviceSlideAdapter.MyViewHolder> {

    private List<T> dataList;
    private LayoutInflater layoutInflater;
    private OnItemClickListener mOnItemClickListener;

    public BtDeviceSlideAdapter(Context context, List<T> dataList) {
        this.dataList = dataList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_bt_device_slide, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BtDeviceSlideAdapter.MyViewHolder holder, int position) {
        BtDevice btDevice = (BtDevice) dataList.get(position);
        holder.tvName.setText(btDevice.getSerialNumber());
        holder.tvMac.setText(btDevice.getMac());
        holder.btnAction.setOnClickListener(v -> mOnItemClickListener.onItemClick(holder.btnAction, holder.getLayoutPosition()));
        holder.tvDelete.setOnClickListener(v -> mOnItemClickListener.onDeleteClick(holder.tvDelete, holder.getLayoutPosition()));
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

    public boolean contains(T t) {
        for (T mT : dataList) {
            if (TextUtils.equals(((BtDevice) t).getMac(), ((BtDevice) mT).getMac())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onDeleteClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements SlideSwapAction {

        @BindView(R.id.tv_delete)
        TextView tvDelete;
        @BindView(R.id.ll_item)
        LinearLayout llItem;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_mac)
        TextView tvMac;
        @BindView(R.id.btn_action)
        Button btnAction;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public float getActionWidth() {
            return ValueUtil.dip2px(tvDelete.getContext(), 100);
        }

        @Override
        public View ItemView() {
            return llItem;
        }
    }

}