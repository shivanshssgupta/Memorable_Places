package com.example.memorableplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnownLocation;
    Intent intent;
    LatLng loc=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        intent= getIntent();
        loc= new LatLng(intent.getDoubleExtra("Latitude",0),intent.getDoubleExtra("Longitude",0));


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        /*locationListener= new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng position= new LatLng(location.getLatitude(),location.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(position).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,12));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            if(loc.latitude==0 && loc.longitude==0) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng lastKnownPosition = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(lastKnownPosition).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownPosition, 12));
            }
            else{
                mMap.addMarker(new MarkerOptions().position(loc).title("Your Selected Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12));
            }
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Geocoder geocoder= new Geocoder(getApplicationContext(), Locale.getDefault());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy  hh:mm");
                String address = simpleDateFormat.format(new Date());
                try {
                    List<Address> addresses= geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                    if (addresses!=null && addresses.size()>0)
                    {

                        if (addresses.get(0).getFeatureName()!=null)
                        {
                            address = addresses.get(0).getFeatureName() + ", ";
                        }
                        if (addresses.get(0).getLocality()!=null)
                        {
                            address += addresses.get(0).getLocality() + ", ";
                        }
                        if (addresses.get(0).getAdminArea()!=null)
                        {
                            address += addresses.get(0).getAdminArea() + ", ";
                        }

                    }
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(address)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MainActivity.places.add(address);
                MainActivity.locations.add(latLng);
                Toast.makeText(MapsActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
                MainActivity.arrayAdapter.notifyDataSetChanged();


            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                if(loc.latitude==0 && loc.longitude==0) {
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    LatLng lastKnownPosition = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(lastKnownPosition).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownPosition, 12));
                    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                }
                else{
                    mMap.addMarker(new MarkerOptions().position(loc).title("Your Selected Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12));
                }
            }
        }
    }
}