package com.ridesharedriver.app.custom;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ridesharedriver.app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GetDumaWorkManager extends Worker {
    String driverEmail = "";
    private static final String TAG = "workManager";

    public GetDumaWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildNotification();
        }

    }



    @NonNull
    @Override
    public Result doWork() {
        String rideID = getInputData().getString("ride_id");
        String driverEmail = getInputData().getString("driver_email");
        String driverName = getInputData().getString("driver_name");
        Log.d(TAG,rideID+", "+driverName+", "+", "+driverEmail);
        createuser(driverEmail, rideID, driverName);
            return Result.success();
    }

    private void createuser(String email, String rideId, String name) {

        String password =getApplicationContext(). getString(R.string.firebase_password);
        Log.d("createFirebase", "Fire" + email);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "firebase auth success");
                    //loginToFirebase();
                    requestLocationUpdates(name, rideId);
                } else {
                    Log.d(TAG, "firebase auth failed");
                    loginToFirebase(email, rideId, name);
                }
            }
        });
    }

    private void loginToFirebase(String email, String rideId, String name) {
        // Authenticate with Firebase, and request location updates
        // String email = getString(R.string.firebase_email);
        String password = getApplicationContext().getString(R.string.firebase_password);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "firebase auth success");
                    requestLocationUpdates(name, rideId);
                } else {
                    Log.d(TAG, "firebase auth failed");
                    Log.e(TAG, "onComplete: auth Failed=" + task.getException().getMessage());

                }
            }
        });
    }


    private void requestLocationUpdates(String name, String rideId) {
        LocationRequest request = new LocationRequest();
        request.setInterval(60000);
        request.setFastestInterval(60000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        final String path = name + rideId + "/" + rideId;

        //  final String path = getString(R.string.firebase_path) + "/" + getString(R.string.transport_id);
        int permission = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Log.d(TAG, "location update " + location);

                        ref.setValue(location);

                    }
                }
            }, null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildNotification() {
        // Create the persistent notification
        String NOTIFICATION_CHANNEL_ID = "com.ridesharedriver.app";
        String channelName = "RideShare";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getApplicationContext(). getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.img_logo)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

    }


    @Override
    public void onStopped() {
        super.onStopped();
    }
}
