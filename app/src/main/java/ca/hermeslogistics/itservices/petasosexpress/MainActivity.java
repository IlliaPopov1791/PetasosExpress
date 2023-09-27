/*
 * Names: Illia M. Popov, William Margalik, Dylan Ashton, Ahmad Aljawish
 * Student ID: n01421791, n01479878, n01442206, n01375348
 * Section: B
 */

package ca.hermeslogistics.itservices.petasosexpress;

/*
 * Illia M. Popov | ID: n01421791
 *  William Margalik | ID: n01479878
 * Dylan Ashton | ID: n01442206
 *  Ahmad Aljawish | ID: n01375348
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Home HomeFragment = new Home();
        fragmentTransaction.add(R.id.main_drawer_layout, HomeFragment);
        fragmentTransaction.commit();
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                Snackbar.make(drawerLayout, getResources().getString(R.string.app_name) + ", " + getResources().getString(R.string.navigation_drawer_open), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                DisplayToast( getResources().getString(R.string.app_name) + ", " + getResources().getString(R.string.navigation_drawer_close));
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

    private void showExitAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            @SuppressLint("NonConstantResourceId")//Cannot extract string from here as value MUST be constant
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home_screen) {
                    Home home = new Home();
                    fragmentManager.beginTransaction().replace(R.id.main_frame_layout, home).commit();
                } else if (itemId == R.id.distance_sensor) {
                    SensorDistance down = new SensorDistance();
                    fragmentManager.beginTransaction().replace(R.id.main_frame_layout, down).commit();
                } else if (itemId == R.id.proximity_sensor) {
                    SensorProximity weat = new SensorProximity();
                    fragmentManager.beginTransaction().replace(R.id.main_frame_layout, weat).commit();
                } else if (itemId == R.id.balance_sensor) {
                    SensorBalance data = new SensorBalance();
                    fragmentManager.beginTransaction().replace(R.id.main_frame_layout, data).commit();
                } else if (itemId == R.id.motors_sensors) {
                    SensorMotors mail = new SensorMotors();
                    fragmentManager.beginTransaction().replace(R.id.main_frame_layout, mail).commit();
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
}