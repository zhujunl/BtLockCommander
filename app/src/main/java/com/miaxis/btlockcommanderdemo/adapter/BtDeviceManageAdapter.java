package com.miaxis.btlockcommanderdemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miaxis.btlockcommanderdemo.R;
import com.miaxis.btlockcommanderdemo.model.entity.BtDevice;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BtDeviceManageAdapter <T> extends RecyclerView.Adapter<BtDeviceManageAdapter.MyViewHolder> {

    private List<T> dataList;
    private LayoutInflater layoutInflater;
    private OnItemClickListener mOnItemClickListener;

    public BtDeviceManageAdapter(Context context, List<T> dataList) {
        this.dataList = dataList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_bt_device_manage, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BtDeviceManageAdapter.MyViewHolder holder, int position) {
        BtDevice btDevice = (BtDevice) dataList.get(position);
        holder.tvName.setText(btDevice.getSerialNumber());
        holder.tvMac.setText(btDevice.getMac());
        holder.cbSelect.setChecked(btDevice.isSelect());
        holder.llBtDevice.setOnClickListener(v -> {
            boolean checked = holder.cbSelect.isChecked();
            btDevice.setSelect(!checked);
            holder.cbSelect.setChecked(!checked);
        });
        holder.cbSelect.setOnClickListener(v -> {
            boolean checked = holder.cbSelect.isChecked();
            btDevice.setSelect(checked);
            holder.cbSelect.setChecked(checked);
        });
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

    public void selectAll() {
        boolean selectAll = false;
        for (T t : dataList) {
            BtDevice btDevice = (BtDevice) t;
            if (!btDevice.isSelect()) {
                selectAll = true;
            }
        }
        for (T t : dataList) {
            BtDevice btDevice = (BtDevice) t;
            btDevice.setSelect(selectAll);
        }
        notifyDataSetChanged();
    }

    public List<BtDevice> getSelectList() {
        List<BtDevice> selectList = new ArrayList<>();
        for (T t : dataList) {
            BtDevice btDevice = (BtDevice) t;
            if (btDevice.isSelect()) {
                selectList.add(btDevice);
            }
        }
        return selectList;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_mac)
        TextView tvMac;
        @BindView(R.id.btn_action)
        Button btnAction;
        @BindView(R.id.ll_bt_device)
        LinearLayout llBtDevice;
        @BindView(R.id.cb_select)
        CheckBox cbSelect;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
