package com.ridesharedriver.app.fragement;

import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;


import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.ridesharedriver.app.acitivities.HomeActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ridesharedriver.app.R;
import com.ridesharedriver.app.custom.Utils;
import com.ridesharedriver.app.pojo.PendingRequestPojo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * Created by android on 15/4/17.
 */

//payment details
public class PaymentDetail extends Fragment implements OnMapReadyCallback {
    View view;
    PendingRequestPojo pojo;
    com.google.android.gms.maps.MapView mMapView;
    private SupportMapFragment mSupportMapFragment;
    GoogleMap mMap;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.myswitch);
        item.setVisible(false);
    }


    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
//                    Toast.makeText(getActivity(), "Back press", Toast.LENGTH_SHORT).show();

                    AcceptedRequestFragment   acceptedRequestFragment = new AcceptedRequestFragment();
                    if(pojo.getScreen().equalsIgnoreCase("payment"))
                    {

                        ((HomeActivity) requireContext()).changeFragment(new PaymentHistory(), getString(R.string.payment_history));
                    }
                    else {
                        Bundle bundle = new Bundle();
                        bundle.putString("status", pojo.getStatus());
                        acceptedRequestFragment.setArguments(bundle);
                        ((HomeActivity) requireContext()).changeFragment(acceptedRequestFragment, getString(R.string.requests));
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.payment_detail, container, false);
        setHasOptionsMenu(true);
        BindView();
        // mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.main_branch_map);
        mMapView = (com.google.android.gms.maps.MapView) view.findViewById(R.id.mapview_payment_detail);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        mMapView.setClickable(false);
        mMapView.setActivated(false);
        mMapView.setEnabled(false);
        mMapView.onResume();

        return view;
    }

    //on map is ready
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if(!pojo.getStatus().isEmpty()){
            mMapView.setVisibility(View.GONE);
            return;
        }
        try {
            mMap = googleMap;
//            LatLngBounds bounds = new LatLngBounds(new LatLng(Double.parseDouble(pojo.getPickup_lat()), Double.parseDouble(pojo.getPickup_long())),
//                    new LatLng(Double.parseDouble(pojo.getDrop_lat()), Double.parseDouble(pojo.getDrop_long())));
//            mMap.setLatLngBoundsForCameraTarget(bounds);
            setCustomMapStyle();
            mMap.getUiSettings().setScrollGesturesEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(false);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            LatLng coordinates = new LatLng(Double.parseDouble(pojo.getPickup_lat()), Double.parseDouble(pojo.getPickup_long()));
           // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 10));
            CameraUpdate center=
                    CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(pojo.getDrop_lat()), Double.parseDouble(pojo.getDrop_long())));

            CameraUpdate zoom=CameraUpdateFactory.zoomTo(17);

           // mMap.moveCamera(center);
         //   mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,15));

            double dummy_radius = distance(Double.parseDouble(pojo.getPickup_lat()),Double.parseDouble(pojo.getPickup_long()),
                    Double.parseDouble(pojo.getDrop_lat()),Double.parseDouble(pojo.getDrop_long()));

            double circleRad = dummy_radius*3000;//multiply by 1000 to make units in KM

            float zoomLevel = getZoomLevel(circleRad);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, zoomLevel));

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MAP_ERROR", e.toString());
        }
        if (pojo != null && mMap != null) {
            mMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(pojo.getPickup_lat()),
                                    Double.parseDouble(pojo.getPickup_long()))).snippet(pojo.getPickup_adress()).
                            title(getString(R.string.pick_up_location)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.addMarker(
                    new MarkerOptions().snippet(pojo.getDrop_address())
                            .position(new LatLng(Double.parseDouble(pojo.getDrop_lat()),
                                    Double.parseDouble(pojo.getDrop_long()))).title(getString(R.string.drop_up_location)).
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(Double.parseDouble(pojo.getPickup_lat()), Double.parseDouble(pojo.getPickup_long())),
                            new LatLng(Double.parseDouble(pojo.getDrop_lat()), Double.parseDouble(pojo.getDrop_long())))
                    .width(5)
                    .color(Color.BLUE));
        }
    }


    //zoom on map
    private int getZoomLevel(double radius){
        double scale = radius / 500;
        return ((int) (16 - Math.log(scale) / Math.log(2)));
    }

    //getting distcnce between locations
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void BindView() {
        TextView pickupaddress = (TextView) view.findViewById(R.id.txt_pickupaddress);
        TextView dropaddress = (TextView) view.findViewById(R.id.txt_dropaddress);
        TextView distance = (TextView) view.findViewById(R.id.txt_distance);
        TextView status = (TextView) view.findViewById(R.id.txt_status);
        TextView amount = (TextView) view.findViewById(R.id.txt_amount);
        TextView payment_status = (TextView) view.findViewById(R.id.txt_paymentstatus);
        TextView time = (TextView) view.findViewById(R.id.txt_datetime);
        TextView txt_riderName = (TextView) view.findViewById(R.id.txt_riderName);
        TextView tipAmount = view.findViewById(R.id.txt_tip_amount);

        ImageView imageView = view.findViewById(R.id.car_image);
        Bundle bundle = getArguments();
        if (bundle != null) {
            pojo = (PendingRequestPojo) bundle.getSerializable("data");
            if (pojo != null) {
                pickupaddress.setText(pojo.getPickup_adress());
                dropaddress.setText(pojo.getDrop_address());
                distance.setText(pojo.getDistance() +" miles.");
                status.setText(pojo.getStatus());
                if(pojo.getAmount()!=null) {
//                amount.setText(DecimalFormat.getCurrencyInstance(Locale.US).format(Double.parseDouble(pojo.getAmount())));
                    amount.setText(String.format("$%s", pojo.getAmount()));
                }

                if(pojo.getTip_amount()!=null) {
                    if( pojo.getTip_amount().equalsIgnoreCase("null")) {
                        tipAmount.setText("$0.0");
                    }
                    else
                    {
                        tipAmount.setText(String.format("$%s", pojo.getTip_amount()));
                    }
                }
                else
                {
                    tipAmount.setText("$0.0");
                }
//                mMap.addMarker(
//                        new MarkerOptions().position(new LatLng(Double.parseDouble(pojo.getPickup_lat()),Double.parseDouble(pojo.getPickup_long()))).snippet(pojo.getPickup_adress()).
//                                title(getString(R.string.pick_up_location)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//                mMap.addMarker(
//                        new MarkerOptions().snippet(pojo.getDrop_address())
//                                .position(new LatLng(Double.parseDouble(pojo.getDrop_lat()),Double.parseDouble(pojo.getDrop_long()))).title(getString(R.string.drop_up_location)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                if (pojo.getPayment_status() != null && !pojo.getPayment_status().equals("")) {
                    payment_status.setText(pojo.getPayment_status());

                } else {
                    payment_status.setText(getString(R.string.unpaid));
                }
                Utils utils = new Utils();
                String binded = (utils.getCurrentDateInSpecificFormat(pojo.getTime()) + " , " + Utils.getformattedTime(pojo.getTime()));
                time.setText(pojo.getTime());
                txt_riderName.setText(pojo.getUser_lastname());
            }
        }
    }

    private void setCustomMapStyle() {
        try {
            // Load the custom map style from the raw resource
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
            if (!success) {
                // Handle the case if the custom style couldn't be loaded
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

}