package com.miaxis.btlockcommanderdemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miaxis.btlockcommander.entity.OpenLog;
import com.miaxis.btlockcommander.util.BleUtil;
import com.miaxis.btlockcommanderdemo.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LockLogAdapter extends RecyclerView.Adapter<LockLogAdapter.MyViewHolder> {

    private List<OpenLog> dataList;
    private LayoutInflater layoutInflater;
    private OnItemClickListener mOnItemClickListener;

    public LockLogAdapter(Context context, List<OpenLog> dataList) {
        this.dataList = dataList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_lock_log, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final LockLogAdapter.MyViewHolder holder, int position) {
        OpenLog openLog = (OpenLog) dataList.get(position);
        String openType = BleUtil.convertOpenType(openLog.getOpenType());
        String alarmType = BleUtil.convertAlarmType(openLog.getAlarmType());
        if (TextUtils.isEmpty(alarmType)) {
            holder.tvPersonId.setText(openLog.getPersonId());
            holder.tvOpenType.setText(openType + ",电量" + openLog.getPowerLevel() + "0%");
        } else {
            holder.tvPersonId.setText("报警信息");
            if (TextUtils.equals(alarmType, "临时密码开门")) {
                alarmType += "-" + openLog.getTempId();
            }
            if (alarmType.contains("低电量")) {
                holder.tvOpenType.setText(alarmType);
            } else {
                holder.tvOpenType.setText(alarmType + ",电量" + openLog.getPowerLevel() + "0%");
            }
        }
        holder.tvTime.setText(openLog.getTime());
    }

    public List<OpenLog> getDataList() {
        return dataList;
    }

    public void setDataList(List<OpenLog> dataList) {
        this.dataList = dataList;
    }

    public void appendDataList(List<OpenLog> dataList) {
        this.dataList.addAll(dataList);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public OpenLog getData(int position) {
        return dataList.get(position);
    }

    public void addData(OpenLog t) {
        dataList.add(t);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_person_id)
        TextView tvPersonId;
        @BindView(R.id.tv_open_type)
        TextView tvOpenType;
        @BindView(R.id.tv_alarm_type)
        TextView tvAlarmType;
        @BindView(R.id.tv_time)
        TextView tvTime;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}