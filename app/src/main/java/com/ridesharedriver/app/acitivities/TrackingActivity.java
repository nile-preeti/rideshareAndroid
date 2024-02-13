package com.ridesharedriver.app.acitivities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ridesharedriver.app.tracker.TrackingService;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//Tracking Activity
public class TrackingActivity extends Activity {

    private static final int PERMISSIONS_REQUEST = 1;
    String ride_id="",driver_email="",driver_name="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        ride_id = intent.getStringExtra("ride_id");
        driver_email = intent.getStringExtra("driver_email");
        driver_name = intent.getStringExtra("driver_name");

        try {
            // Check GPS is enabled
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
                finish();
            }

            // Check location permission is granted - if it is, start
            // the service, otherwise request the permission
            int permission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (permission == PackageManager.PERMISSION_GRANTED) {
                startTrackerService();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //starting tracking
    private void startTrackerService() {
        try {
            Intent serviceIntent = new Intent(this, TrackingService.class);
            Log.d("trackID", ride_id.toString());
            serviceIntent.putExtra("ride_id", ride_id);
            serviceIntent.putExtra("driver_email", driver_email);
            serviceIntent.putExtra("driver_name", driver_name);
//        startService(serviceIntent);
            ContextCompat.startForegroundService(this, serviceIntent);
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService();
        } else {
            finish();
        }
    }
}
