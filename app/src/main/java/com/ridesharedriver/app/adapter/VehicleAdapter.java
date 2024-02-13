package com.ridesharedriver.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ridesharedriver.app.R;
import com.ridesharedriver.app.interfaces.AddVehicleInterface;
import com.ridesharedriver.app.interfaces.CheckInterface;
import com.ridesharedriver.app.pojo.profileresponse.VehicleDetail;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {
    private ArrayList<VehicleDetail> vehicle;
    Context context;
    private AddVehicleInterface addVehicleInterface;
    private CheckInterface checkInterface;
    private CheckBox lastChecked = null;
    private CheckBox cb;
    private int lastCheckedPos = 0;

    String contactNo = "";
    //private PostedJobIDInterface jobidclicklistener;
    String id;
    private int selectedPosition = -1;

    //RequestTypeInterface callClickListener;
    public VehicleAdapter(Context context, ArrayList<VehicleDetail> vehicle, AddVehicleInterface addVehicleInterface, CheckInterface checkInterface) {
        this.vehicle = vehicle;
        this.context = context;
        this.addVehicleInterface=addVehicleInterface;
        this.checkInterface=checkInterface;
    }

    @Override
    public VehicleAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_vehicle_item, viewGroup, false);
        return new VehicleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VehicleAdapter.ViewHolder viewHolder, int i) {

        viewHolder.txtVehicleColor.setText(vehicle.get(i).getColor());
        viewHolder.txtVehicleBrand.setText(vehicle.get(i).getBrandName());
        viewHolder.txtVehicleModel.setText(vehicle.get(i).getModelName());
        viewHolder.txtVehicleType.setText(vehicle.get(i).getVehicleType());
        viewHolder.txtVehicleNo.setText(vehicle.get(i).getVehicleNo());
        viewHolder.txtYear.setText(vehicle.get(i).getYear());
        if(vehicle.get(i).getPremiumFacility()!=null)
        {
            if(vehicle.get(i).getPremiumFacility().toLowerCase().contains("wi-fi") || vehicle.get(i).getPremiumFacility().toLowerCase().contains("luxury seats")
            || vehicle.get(i).getPremiumFacility().toLowerCase().contains("t.v."))
            {
                viewHolder.txtSeatNumber.setText("8");
            }
            else if(vehicle.get(i).getSeatNo().equalsIgnoreCase("9"))
            {
                viewHolder.txtSeatNumber.setText("8+");
            }
            else
            {
                viewHolder.txtSeatNumber.setText(vehicle.get(i).getSeatNo());
            }
        }
        else {
            if(vehicle.get(i).getSeatNo().equalsIgnoreCase("9"))
            {
                viewHolder.txtSeatNumber.setText("8+");
            }
            else {
                viewHolder.txtSeatNumber.setText(vehicle.get(i).getSeatNo());
            }
        }
        try {
            Glide.with(context).load(vehicle.get(i).getCarPic()).apply(new RequestOptions().error(R.drawable.taxi_new)).into(viewHolder.vehicle_pic);
        } catch (Exception e) {

        }

//        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//               // if(viewHolder.checkBox.isChecked()){
//                if(b)
//                    checkInterface.onCheckClicked(vehicle.get(i).getVehicleDetailId(), b, "1");
//                else
//                    checkInterface.onCheckClicked(vehicle.get(i).getVehicleDetailId(), b, "2");
//              //  }
//            }
//        });

        if(vehicle.get(i).getStatus().equalsIgnoreCase("1")) {
            viewHolder.checkBox.setChecked(true);
            lastChecked = viewHolder.checkBox;
            lastCheckedPos = viewHolder.getAdapterPosition();
        } else {
            Log.e("Check status",vehicle.get(i).getStatus());
            viewHolder.checkBox.setChecked(false);
        }
        viewHolder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVehicleInterface.onItemClicked("", vehicle.get(i).getModelName(),
                        vehicle.get(i).getVehicleType(),
                        vehicle.get(i).getVehicleNo(),
                        vehicle.get(i).getColor(),
                        vehicle.get(i).getYear(),
                        vehicle.get(i).getVehicleDetailId(),vehicle.get(i).getSeatNo());

            }
        });

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInterface.onCheckClicked(vehicle.get(i).getVehicleDetailId(),true,"3");
            }
        });

//        if(vehicle.size()==1) {
//            viewHolder.checkBox.setChecked(true);
//        }

//        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cb = (CheckBox)v;
//                int clickedPos = viewHolder.getAdapterPosition();
//
//                if(cb.isChecked()) {
//                    if(lastChecked != null) {
//                        lastChecked.setChecked(false);
//                    }
//                    lastChecked = cb;
//                    lastCheckedPos = clickedPos;
//                } else
//                    lastChecked = null;
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return vehicle.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtVehicleBrand, txtVehicleModel, txtVehicleNo, txtVehicleType, txtVehicleColor,txtYear,txtSeatNumber;
        private ImageView btnUpdate, btnDelete,vehicle_pic;
        private CheckBox checkBox;


        public ViewHolder(View view) {
            super(view);

            txtVehicleBrand = (TextView) view.findViewById(R.id.txtBrandName);
            txtVehicleModel = (TextView) view.findViewById(R.id.txtModelName);
            txtVehicleNo = (TextView) view.findViewById(R.id.txtVehicleNo);
            txtVehicleType = (TextView) view.findViewById(R.id.txtVehicleType);
            txtVehicleColor = (TextView) view.findViewById(R.id.txtVehicleColor);
            txtYear = (TextView) view.findViewById(R.id.txtVehicleYear);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            txtSeatNumber = (TextView) view.findViewById(R.id.txt_seat_number);
            btnUpdate = view.findViewById(R.id.btnUpdate);
            btnDelete = view.findViewById(R.id.btnDelete);
            vehicle_pic = view.findViewById(R.id.vehicle_pic);

            checkBox.setOnClickListener(e->{
                checkInterface.onCheckClicked(vehicle.get(getAdapterPosition()).getVehicleDetailId(), checkBox.isChecked(), "");
            });


        }
    }
}