package com.ridesharedriver.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ridesharedriver.app.custom.Utils;
import com.ridesharedriver.app.databinding.AcceptedrequestItemBinding;
import com.ridesharedriver.app.databinding.HelpQuestionHelpRecyclerLayoutBinding;
import com.ridesharedriver.app.pojo.PendingRequestPojo;

import java.io.IOException;
import java.util.List;

//complete Ride Adapter
public class CompleteRideAdapter extends RecyclerView.Adapter<CompleteRideAdapter.CompleteRideHolder> {

    private List<PendingRequestPojo> pendingDataList;
    private OnRideSelect onRideSelect;
    private Context context;

    public CompleteRideAdapter(Context context, List<PendingRequestPojo> pendingDataList, OnRideSelect onRideSelect) {
        this.pendingDataList = pendingDataList;
        this.onRideSelect = onRideSelect;
        this.context = context;
    }

    @NonNull
    @Override
    public CompleteRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CompleteRideHolder(AcceptedrequestItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    private Utils utils = new Utils();
    @Override
    public void onBindViewHolder(@NonNull CompleteRideHolder holder, int position) {
        holder.binding.getRoot().setOnClickListener(e->{
            onRideSelect.onRideSelect(pendingDataList.get(position).getRide_id());
        });

        holder.binding.trackImg.setVisibility(View.GONE);
        holder.binding.callImg.setVisibility(View.GONE);
        holder.binding.acceptImg.setVisibility(View.GONE);
        holder.binding.startImg.setVisibility(View.GONE);
        holder.binding.view.setVisibility(View.GONE);

        holder.binding.txtUsername.setText(pendingDataList.get(position).getUser_name());
        try {
            holder.binding.txtFromAdd.setText( Utils.getMinimumAddress(context, Double.parseDouble(pendingDataList.get(position).getPickup_lat()),
                            Double.parseDouble(pendingDataList.get(position).getPickup_long())));

            holder.binding.txtToAdd.setText( Utils.getMinimumAddress(context, Double.parseDouble(pendingDataList.get(position).getDrop_lat()),
                    Double.parseDouble(pendingDataList.get(position).getDrop_long())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.binding.date.setText(utils.getCurrentDateInSpecificFormat(pendingDataList.get(position).getTime()) + ", " +
                Utils.getformattedTime(pendingDataList.get(position).getTime()));

    }

    @Override
    public int getItemCount() {
        return pendingDataList.size();
    }

    public class CompleteRideHolder extends RecyclerView.ViewHolder{

        private AcceptedrequestItemBinding binding;
        public CompleteRideHolder(AcceptedrequestItemBinding binding) {
            super(binding.getRoot());
            this.binding= binding;
        }


    }

    public interface OnRideSelect{
        void onRideSelect(String ride_id);
    }

    public void setData(List<PendingRequestPojo> pendingRequestPojoList)
    {
        this.pendingDataList = pendingRequestPojoList;
        notifyDataSetChanged();
    }

}
