package com.ridesharedriver.app.custom;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.ridesharedriver.app.R;
import com.ridesharedriver.app.pojo.CountryCode;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<CountryCode> {

    private LayoutInflater inflater;
    private List<CountryCode> countryCodeList;
    private Context mContext;

    public CustomSpinnerAdapter(Context context, List<CountryCode> countryCodeList) {
        super(context, 0, countryCodeList);
        this.countryCodeList = countryCodeList;
        inflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_country_code, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.textCountryName);
        TextView textViewCode = convertView.findViewById(R.id.textCountryCode);
        TextView textViewPhoneCode = convertView.findViewById(R.id.textCountryPhoneCode);
        ImageView flegIcon = convertView.findViewById(R.id.flage);

        CountryCode countryCode = countryCodeList.get(position);

        textViewName.setText(countryCode.getName());
        textViewCode.setText(countryCode.getCode());
        textViewPhoneCode.setText(countryCode.getPhone_code());

        String svgUrl = countryCode.getFlag();
        flegIcon.setImageURI(Uri.parse(svgUrl));

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_country_code, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.textCountryName);
        TextView textViewCode = convertView.findViewById(R.id.textCountryCode);
        TextView textViewPhoneCode = convertView.findViewById(R.id.textCountryPhoneCode);
        ImageView flegIcon = convertView.findViewById(R.id.flage);


        CountryCode countryCode = countryCodeList.get(position);

        textViewName.setText(countryCode.getName());
        textViewCode.setText(countryCode.getCode());
        textViewPhoneCode.setText(countryCode.getPhone_code());

        String svgURl = countryCode.getFlag().toString();
        flegIcon.setImageURI(Uri.parse(svgURl));


        return convertView;
    }
}
