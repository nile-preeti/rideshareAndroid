package com.ridesharedriver.app.tracker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ridesharedriver.app.R;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static com.ridesharedriver.app.session.SessionManager.getUserEmail;
import static com.ridesharedriver.app.session.SessionManager.initialize;

import java.lang.reflect.InvocationTargetException;

//Tracking service
public class TrackingService extends Service {

    private static final String TAG = TrackingService.class.getSimpleName();
    public static  boolean IS_ACTIVITY_RUNNING = false;

    String driverEmail = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IS_ACTIVITY_RUNNING = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildNotification();
        }

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        initialize(getApplicationContext());
        if(intent==null){
            return START_STICKY;
        }
        String ride_id = intent.getStringExtra("ride_id");
        String driver_email = intent.getStringExtra("driver_email");
        String driver_name = intent.getStringExtra("driver_name");


        driverEmail = getUserEmail();
        createuser(driver_email, ride_id, driver_name);
        return START_STICKY;
    }

    //building notification
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_IMMUTABLE);
        // Create the persistent notification
        String NOTIFICATION_CHANNEL_ID = "com.ridesharedriver.app";
        String channelName = "RideShare";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        /*Code By Rohit*/
        // Show Background Running Notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.img_logo)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    //stoping broadcast
    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    //creating user on firebase
    private void createuser(String email, String rideId, String name) {
        String memail = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);
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

    //login in firebase
    private void loginToFirebase(String email, String rideId, String name) {
        // Authenticate with Firebase, and request location updates
        // String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);
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


    //requesting for location
    private void requestLocationUpdates(String name, String rideId) {
        try {
            LocationRequest request = new LocationRequest();
            request.setInterval(300 * 1000); //5mint
            request.setFastestInterval(300 * 1000); //5mint
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
            final String path = "rides/" + name + rideId + "/" + rideId; //"rides/"+name+rideId+"/"+rideId;       ///name + rideId + "/" + rideId;

            //  final String path = getString(R.string.firebase_path) + "/" + getString(R.string.transport_id);
            int permission = ContextCompat.checkSelfPermission(this,
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
                            try{
                                Log.d(TAG, "location update " + location);
                                Log.d("ref", ref.toString());

                                ref.setValue(location);
                            }catch (Exception e){
                                Log.e("InvocationException",e.getMessage());
                            }
                        }
                    }
                }, null);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IS_ACTIVITY_RUNNING = false;
    }

}

