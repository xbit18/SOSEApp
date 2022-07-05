package com.example.soseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class BetsMatch extends AppCompatActivity implements OnMapReadyCallback {
    Double lat = 0.0;
    Double lng = 0.0;
    String link = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bets_match);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String gameDets = intent.getStringExtra("Gamedets");
        String localQuoteExtra = intent.getStringExtra("localQuote");
        String tieQuoteExtra = intent.getStringExtra("tieQuote");
        String visitorQuoteExtra = intent.getStringExtra("visitorQuote");
        String coordinates = intent.getStringExtra("Coordinates");
        String[] parts = coordinates.split(",");
        lat = Double.parseDouble(parts[0]); // 004
        lng = Double.parseDouble(parts[1]); // 034556

        // Capture the layout's TextView and set the string as its text
        TextView gameDet = findViewById(R.id.gameDet);
        gameDet.setText(gameDets);
        TextView localQuote = findViewById(R.id.localQuote);
        localQuote.setText(localQuoteExtra);
        TextView tieQuote = findViewById(R.id.tieQuote);
        tieQuote.setText(tieQuoteExtra);
        TextView visitorQuote = findViewById(R.id.visitorQuote);
        visitorQuote.setText(visitorQuoteExtra);


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng city = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions()
                .position(city));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city,11));
        //googleMap.moveCamera(CameraUpdateFactory.zoomTo(4));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}