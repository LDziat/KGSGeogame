package com.example.kgsgeogame;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Layer;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;
import com.google.maps.android.data.geojson.GeoJsonPoint;
import com.google.maps.android.data.geojson.GeoJsonPointStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyBV5lzbE4pR2lkGDknb76O0NnFXXW1m27U";
    private ImageButton cameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        cameraButton = (ImageButton) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        LatLng nb = new LatLng(37.772584, -83.6843577);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(nb));
        try {
            // Load in the trailheads for the parks
            final GeoJsonLayer trailhd = new GeoJsonLayer(gmap, R.raw.trailheads, getApplicationContext());
            trailhd.addLayerToMap();
            // Set the styling
            GeoJsonPointStyle headStyle = trailhd.getDefaultPointStyle();
            headStyle.setAlpha((float) 0.5);
            // Set the label when pressed
            trailhd.setOnFeatureClickListener(new Layer.OnFeatureClickListener() {
                @Override
                public void onFeatureClick(Feature feature) {
                    TextView label = (TextView) findViewById(R.id.trailLabel);
                    label.setText(feature.getProperty("TRAIL_NAME"));
                }
            });

            // Load in the trail paths for the parks
            GeoJsonLayer trailpt = new GeoJsonLayer(gmap, R.raw.trailpaths, getApplicationContext());
            trailpt.addLayerToMap();
            // Style the trails to match the existing color scheme
            GeoJsonLineStringStyle trailstyle = trailpt.getDefaultLineStringStyle();
            trailstyle.setColor(R.color.buttonFill);

            // Load in park anchors
            // NOTE: These should not be drawn in production and exist simply to calculate the nearest park
            GeoJsonLayer prk = new GeoJsonLayer(gmap, R.raw.parks, getApplicationContext());
            //prk.addLayerToMap();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // This loads our camera activity "MakePhotoActivity"
    public void openCamera(){
        Intent intent = new Intent(this, MakePhotoActivity.class);
        startActivity(intent);
    }

}
