package com.ridesharedriver.app.adapter;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ridesharedriver.app.R;
import com.ridesharedriver.app.acitivities.HomeActivity;
import com.ridesharedriver.app.custom.Utils;
import com.ridesharedriver.app.fragement.PaymentDetail;
import com.ridesharedriver.app.pojo.PendingRequestPojo;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by android on 8/3/17.
 */

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.Holder> {
    List<PendingRequestPojo> list;

    public PaymentAdapter(List<PendingRequestPojo> list) {
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final PendingRequestPojo pojo = list.get(position);
        holder.from_add.setText(pojo.getPickup_adress());
        holder.to_add.setText(pojo.getDrop_address());
        try {
            if(pojo.getTip_amount()!=null)
            {
                holder.tip_amount.setVisibility(View.GONE);
                holder.tip_amount.setText(String.format("Tip Amount: $%s", pojo.getTip_amount()));
            }
            else
            {
                holder.tip_amount.setVisibility(View.GONE);
            }

        }
        catch (Exception ex)
        {
            holder.tip_amount.setVisibility(View.GONE);
        }
        holder.status.setText(String.format("Amount: $%s", pojo.getAmount()));
        holder.time.setText(Utils.getformattedTime(pojo.getTime()));
        holder.distance.setText(String.format("%s miles", pojo.getDistance()));
        holder.driverName.setText(pojo.getDriver_name());
        Utils utils = new Utils();
        holder.date.setText(pojo.getTime());
//        holder.date.setText(utils.getCurrentDateInSpecificFormat(pojo.getTime()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", pojo);
               /* bundle.putString("pickupaddress", list.get(holder.getAdapterPosition()).getPickup_adress());
                bundle.putString("dropaddress", list.get(holder.getAdapterPosition()).getDrop_address());

                bundle.putString("amount", list.get(holder.getAdapterPosition()).getAmount());

                bundle.putString("distance", list.get(holder.getAdapterPosition()).getDistance());
                bundle.putString("status", list.get(holder.getAdapterPosition()).getStatus());
                bundle.putString("payment_status", list.get(holder.getAdapterPosition()).getPayment_status());
                bundle.putString("time", list.get(holder.getAdapterPosition()).getTime());
*/
                PaymentDetail detailFragment = new PaymentDetail();
                detailFragment.setArguments(bundle);
                ((HomeActivity) holder.itemView.getContext()).changeFragment(detailFragment, "Payment Detail");
            }
        });
//        BookFont(holder, holder.f);
//        BookFont(holder, holder.t);
//        BookFont(holder, holder.dn);
//        BookFont(holder, holder.dt);
//        BookFont(holder, holder.distanceTitle);
//        BookFont(holder, holder.driverTitle);
//
//        MediumFont(holder, holder.from_add);
//        MediumFont(holder, holder.to_add);
//        MediumFont(holder, holder.date);
//        MediumFont(holder, holder.distance);
//        MediumFont(holder, holder.status);
//        MediumFont(holder, holder.driverName);


    }

    public void setData(List<PendingRequestPojo> pendingRequestPojoList)
    {
        list = pendingRequestPojoList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {


        TextView from, to, status, from_add, to_add, date, time,distance,distanceTitle, driverTitle, driverName, tip_amount;
        TextView f, t, dn, dt;

        public Holder(View itemView) {
            super(itemView);

            f = (TextView) itemView.findViewById(R.id.from);
            t = (TextView) itemView.findViewById(R.id.to);

            dn = (TextView) itemView.findViewById(R.id.status);
            dt = (TextView) itemView.findViewById(R.id.datee);
            distance = (TextView) itemView.findViewById(R.id.distance_value);
            distanceTitle = (TextView)itemView.findViewById(R.id.distance_title);
            driverName = (TextView) itemView.findViewById(R.id.driver);
            driverTitle = (TextView) itemView.findViewById(R.id.driver_title);
            status = (TextView) itemView.findViewById(R.id.txt_status);
            from_add = (TextView) itemView.findViewById(R.id.txt_from_add);
            to_add = (TextView) itemView.findViewById(R.id.txt_to_add);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            tip_amount = (TextView) itemView.findViewById(R.id.txt_tip_amount);
        }
    }

    public void BookFont(Holder holder, TextView view1) {
        Typeface font1 = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/montserrat_bold.ttf");
        view1.setTypeface(font1);
    }

    public void MediumFont(Holder holder, TextView view) {
        Typeface font = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/montserrat_regular.ttf");
        view.setTypeface(font);
    }

}
