package ca.hermeslogistics.itservices.petasosexpress;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.google.android.material.snackbar.Snackbar;


/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            restoreLastFragment();  // Add this line to restore the last active fragment
        }


        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();


        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                //Snackbar.make(drawerLayout, getResources().getString(R.string.app_name) + ", " + getResources().getString(R.string.navigation_drawer_open), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                //DisplayToast( getResources().getString(R.string.app_name) + ", " + getResources().getString(R.string.navigation_drawer_close));
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            showExitAlertDialog();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (item.getItemId() == R.id.home) {
            Home home = new Home();
            fragmentManager.beginTransaction().replace(R.id.main_frame_layout, home).commit();
            return true;
        } if (item.getItemId() == R.id.Settings_button) {
            AppSettings settings = new AppSettings();
            fragmentManager.beginTransaction().replace(R.id.main_frame_layout, settings).commit();
            return true;
        }if (item.getItemId() == R.id.location_button) {
            SensorGPS gps = new SensorGPS();
            fragmentManager.beginTransaction().replace(R.id.main_frame_layout, gps).commit();
            return true;
        }else if (item.getItemId() == R.id.help) {
            initiateCall();
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    private void showExitAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher_foreground);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.alert_exit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void configureToolBar(){
        this.toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
    }


    private void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
    private void configureNavigationView(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint({"NonConstantResourceId", "ResourceType"})
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragmentToLoad = null;
                String fragmentTag = "";

                if (itemId == R.id.home_screen) {
                    fragmentToLoad = new Home();
                    fragmentTag = getString(R.string.home_tag);
                } else if (itemId == R.id.gps_sensor) {
                    fragmentToLoad = new SensorGPS();
                    fragmentTag = getString(R.string.gps_tag);
                } else if (itemId == R.id.distance_sensor) {
                    fragmentToLoad = new SensorDistance();
                    fragmentTag = getString(R.string.distance_tag);
                } else if (itemId == R.id.proximity_sensor) {
                    fragmentToLoad = new SensorProximity();
                    fragmentTag = getString(R.string.proximity_tag);
                } else if (itemId == R.id.balance_sensor) {
                    fragmentToLoad = new SensorBalance();
                    fragmentTag = getString(R.string.balance_tag);
                } else if (itemId == R.id.motors_sensors) {
                    fragmentToLoad = new SensorMotors();
                    fragmentTag = getString(R.string.motors_tag);
                } else if (itemId == R.id.AppSettings) {
                    fragmentToLoad = new AppSettings();
                    fragmentTag = getString(R.string.settings_tag);
                }

                if (fragmentToLoad != null) {
                    fragmentManager.beginTransaction().replace(R.id.main_frame_layout, fragmentToLoad).commit();
                    saveCurrentFragment(fragmentTag); // Save the current fragment's tag
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
    private void DisplayToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    private void initiateCall() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 123);
            return;
        }
        callPhoneNumber();
    }
    private void callPhoneNumber() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(getString(R.string.tel_1234567890))); // Adjust with your phone number
        startActivity(callIntent);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(drawerLayout, R.string.permission_granted, Snackbar.LENGTH_SHORT).show();
                callPhoneNumber();
            } else {
                Snackbar.make(drawerLayout, R.string.permission_denied, Snackbar.LENGTH_SHORT).show();
            }
        }
    }
    // This method saves the current fragment's tag to shared preferences
    private void saveCurrentFragment(String fragmentTag) {
        getSharedPreferences(getString(R.string.app_settings), MODE_PRIVATE).edit().putString(getString(R.string.current_fragment), fragmentTag).apply();
    }

    // This method retrieves the saved fragment's tag from shared preferences
    private String getSavedFragment() {
        return getSharedPreferences(getString(R.string.app_settings), MODE_PRIVATE).getString(getString(R.string.current_fragment), getString(R.string.empty_space));
    }

    // This method is used to restore the last active fragment from shared preferences
    private void restoreLastFragment() {
        String fragmentTag = getSavedFragment();
        Fragment fragmentToLoad = null;

        switch (fragmentTag) {
            case "home":
                fragmentToLoad = new Home();
                break;
            case "gps":
                fragmentToLoad = new SensorGPS();
                break;
            case "distance":
                fragmentToLoad = new SensorDistance();
                break;
            case "proximity":
                fragmentToLoad = new SensorProximity();
                break;
            case "balance":
                fragmentToLoad = new SensorBalance();
                break;
            case "motors":
                fragmentToLoad = new SensorMotors();
                break;
            case "settings":
                fragmentToLoad = new AppSettings();
                break;
            default:
                fragmentToLoad = new Home();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, fragmentToLoad).commit();
    }

}