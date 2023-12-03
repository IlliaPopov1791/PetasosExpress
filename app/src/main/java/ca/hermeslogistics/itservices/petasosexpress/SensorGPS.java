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
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.Timestamp;

import java.util.Arrays;

public class SensorGPS extends Fragment implements OnMapReadyCallback {

    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private Marker currentLocationMarker;
    private String AssignedPetasos = "Petasos000";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.sensor_gps, container, false);

        // Initialize the map fragment and set up the map asynchronously
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Initialize the Google Map
        mMap = googleMap;

        // Check for location permission and enable my location if granted
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Check for an assigned order and start listening for location updates
        checkAssignedOrder();
        listenForLocationUpdates();
    }

    private void listenForLocationUpdates() {
        // Get the Firestore instance and the reference to the Petasos record
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference locationRef = db.collection("PetasosRecord").document(AssignedPetasos);

        // Listen for real-time updates on the Petasos location
        locationRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                // Handle error
                return;
            }

            if ((snapshot != null && snapshot.exists() && isAdded())) {
                // Extract the GeoPoint from the snapshot and update the map
                GeoPoint geoPoint = snapshot.getGeoPoint("Location");
                if (geoPoint != null) {
                    LatLng newLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                    updateMap(newLocation);
                }
            }
        });
    }

    private void updateMap(LatLng location) {
        // Update the map with the current location marker
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
        // Request location permission
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
    }

    private BitmapDescriptor resizeMapIcons(String iconName, int width, int height) {
        // Resize the map icon to a specified width and height
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getActivity().getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Handle the result of the location permission request
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                // No marker :(
            }
        }
    }

    private void checkAssignedOrder() {
        // Check if there is an assigned order for the current user
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            // Query the ordered orders for the current user
            Query orderedQuery = db.collection("orderRecord")
                    .whereEqualTo("User", userEmail)
                    .whereEqualTo("status", "ordered")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .limit(1); // Only fetch the oldest one

            orderedQuery.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    // Found the oldest 'ordered' order, handle it
                    DocumentSnapshot orderedDocument = task.getResult().getDocuments().get(0);
                    handleOrderedOrder(orderedDocument);
                } else {
                    // If no 'ordered' orders found, check for 'in a queue' orders
                    Query inQueueQuery = db.collection("orderRecord")
                            .whereEqualTo("User", userEmail)
                            .whereEqualTo("status", "in a queue");

                    inQueueQuery.get().addOnCompleteListener(queueTask -> {
                        if (queueTask.isSuccessful() && !queueTask.getResult().isEmpty()) {
                            // If 'in a queue' orders found, show a queue alert
                            showQueueAlertDialog();
                        } else {
                            // If no orders found at all, show a no order alert
                            showNoOrderAlertDialog();
                        }
                    });
                }
            });
        }
    }

    private void handleOrderedOrder(DocumentSnapshot document) {
        // Extract Petasos ID from the delivery reference and start tracking
        DocumentReference deliveryRef = (DocumentReference) document.get("delivery");
        String petasosId = deliveryRef.getId();
        AssignedPetasos = petasosId;
        Toast.makeText(getContext(), "Tracking your oldest order", Toast.LENGTH_LONG).show();
        listenForLocationUpdates();
    }

    private void showQueueAlertDialog() {
        // Show an alert dialog for orders 'in a queue'
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.queue_alert_title))
                .setMessage(getString(R.string.queue_alert_message))
                .setPositiveButton(getString(R.string.ok), null)
                .setIcon(R.drawable.ic_cart_foreground)
                .show();
    }

    private void showNoOrderAlertDialog() {
        // Show an alert dialog for no orders found
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.no_order_alert_title))
                .setMessage(getString(R.string.no_order_alert_message))
                .setPositiveButton(getString(R.string.ok), null)
                .setIcon(R.drawable.ic_cart_foreground)
                .show();
    }
}
