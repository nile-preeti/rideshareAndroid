package com.ridesharedriver.app.fragement;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.ridesharedriver.app.R;
import com.ridesharedriver.app.acitivities.HomeActivity;
import com.ridesharedriver.app.connection.ApiClient;
import com.ridesharedriver.app.connection.ApiNetworkCall;
import com.ridesharedriver.app.pojo.driverDestination.DriverDestination;
import com.ridesharedriver.app.session.SessionManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static android.content.ContentValues.TAG;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;


//set Destination
public class SetDestinationFragment extends Fragment {
    View rootView;
    public SetDestinationFragment() {

    }

    //setting destination
    public static SetDestinationFragment newInstance(String param1, String param2) {
        return new SetDestinationFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.myswitch);
        item.setVisible(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_destination, container, false);
        setHasOptionsMenu(true);
        ((HomeActivity) getActivity()).fontToTitleBar("Set Destination");

        String apiKey = getString(R.string.google_android_map_api_key);


        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getContext());
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.getView().setBackgroundResource(R.drawable.form_outline_background);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                String destLatLong = place.getLatLng().toString().replaceAll("lat/lng: ()", "");
                String destinationLatLong = destLatLong.replace("(", "");
                destinationLatLong = destinationLatLong.replace(")", "");
                Log.i(TAG, "Place: " + place.getName() + ", " + destinationLatLong);
                String lat = destinationLatLong.split(",")[0];
                String lng = destinationLatLong.split(",")[1];
                setDriverDestination(lat, lng);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        return  rootView;
    }

    //setting driver destination
    public void setDriverDestination(String lat, String lng) {
        final Dialog dialog = new Dialog(getApplicationContext());
        Map<String, String> details = new HashMap<>();
        details.put("destination_lat", lat);
        details.put("destination_long", lng);
        String token = "Bearer " + SessionManager.getKEY();
        Log.d("token", token);

        ApiNetworkCall apiService = ApiClient.getApiService();
        Call<DriverDestination> call = apiService.setDriverDestination(token, details);
        call.enqueue(new Callback<DriverDestination>() {
            @Override
            public void onResponse(Call<DriverDestination> call, retrofit2.Response<DriverDestination> response) {
                DriverDestination jsonResponse = response.body();
                if (jsonResponse.getStatus()) {
                    dialog.cancel();
                    Toast.makeText(getContext(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    changeFragment(new HomeFragment(), getString(R.string.home));
                } else {
                    Toast.makeText(getContext(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    dialog.cancel();
                }
            }

            @Override
            public void onFailure(Call<DriverDestination> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
                dialog.cancel();
            }
        });
    }

    //screen change
    public void changeFragment(final Fragment fragment, final String fragmenttag) {
        try {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
            fragmentTransaction.replace(R.id.frame, fragment, fragmenttag);
            fragmentTransaction.commit();
        } catch (Exception e) {
        }
    }
}