package ca.hermeslogistics.itservices.petasosexpress;
/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class SensorGPS extends Fragment implements OnMapReadyCallback {

    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private Marker currentLocationMarker;
    private String AssignedPetasos = "Petasos001";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sensor_gps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        //Checking if we have location permission
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        listenForLocationUpdates();
    }

    private void listenForLocationUpdates() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference locationRef = db.collection("PetasosRecord")
                .document(AssignedPetasos);

        // Listen for real-time updates
        locationRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                // Handle error
                return;
            }

            if ((snapshot != null && snapshot.exists() && isAdded())) {
                GeoPoint geoPoint = snapshot.getGeoPoint("Location");
                if (geoPoint != null) {
                    LatLng newLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                    updateMap(newLocation);
                }
            }
        });
    }

    private void updateMap(LatLng location) {
        if (mMap != null) {
            if (currentLocationMarker != null) {
                currentLocationMarker.remove();
            }
            int height = 200;
            int width = 200;

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(location)
                    .title("Current Location")
                    .icon(resizeMapIcons("petasos_location", width, height));

            currentLocationMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        }
    }


    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
    }
    private BitmapDescriptor resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getActivity().getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                //No marker :(
            }
        }
    }

    private void showQueueAlertDialog(String status) {
        if(status == "ready"){

        }
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.queue_alert_title))
                .setMessage(getString(R.string.queue_alert_message))
                .setPositiveButton(getString(R.string.ok), null)
                .setIcon(R.drawable.ic_cart_foreground)
                .show();
    }
}
