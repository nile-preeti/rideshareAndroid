package com.ridesharedriver.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ridesharedriver.app.databinding.ServiceLayoutBinding;
import com.ridesharedriver.app.pojo.getVehicleDetails.VehicleData;

import java.util.List;

public class ServiceAlertAdapter extends RecyclerView.Adapter<ServiceAlertAdapter.ServiceViewHolder> {

    private List<VehicleData> vehicleDataList;
    private OnServiceSelect onServiceSelect;
    private Context context;

    public ServiceAlertAdapter(List<VehicleData> vehicleDataList, OnServiceSelect onServiceSelect, Context context) {
        this.vehicleDataList = vehicleDataList;
        this.onServiceSelect = onServiceSelect;
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ServiceViewHolder(ServiceLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {

        String vehicleType[]=vehicleDataList.get(holder.getAdapterPosition()).getVehicleType().split(" ");
        String vehicleCategoryType=vehicleType[0];
        String vehicleCategorySubType="";

        for(String part:vehicleType){
            vehicleCategorySubType+=part+" ";
        }

        holder.binding.chkService.setText(vehicleCategoryType+" ( "+vehicleCategorySubType+")");
//        holder.binding.chkService.setText(vehicleDataList.get(holder.getAdapterPosition()).getVehicleType());
        if (vehicleDataList.get(holder.getAdapterPosition()).getStatus().equalsIgnoreCase("1"))
            holder.binding.chkService.setChecked(true);
        else
            holder.binding.chkService.setChecked(false);
        holder.binding.chkService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onServiceSelect.onItemClicked(vehicleDataList.get(holder.getAdapterPosition()).getServiceId(),isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleDataList.size();
    }

    public class ServiceViewHolder extends RecyclerView.ViewHolder {
        private ServiceLayoutBinding binding;

        public ServiceViewHolder(ServiceLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public interface OnServiceSelect {
        void onItemClicked(String service_id, boolean status);
    }
}
