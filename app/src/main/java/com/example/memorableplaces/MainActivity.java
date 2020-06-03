package com.example.memorableplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> places= new ArrayList<>();
    static ArrayList<LatLng> locations= new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView list=findViewById(R.id.list);

        places.add("Add a new place....");
        locations.add(new LatLng(0,0));
        arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,places);
        list.setAdapter(arrayAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(getApplicationContext(),MapsActivity.class);
                if (position != 0) {
                    intent.putExtra("Latitude", locations.get(position).latitude);
                    intent.putExtra("Longitude", locations.get(position).longitude);
                    intent.putExtra("Number",position);
                }
                startActivity(intent);
            }
        });
    }
}