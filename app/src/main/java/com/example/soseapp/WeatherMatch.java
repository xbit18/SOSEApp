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

public class WeatherMatch extends AppCompatActivity implements OnMapReadyCallback {
    Double lat = 0.0;
    Double lng = 0.0;
    String link = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_match);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String gameDets = intent.getStringExtra("Gamedets");
        String weatherDets = intent.getStringExtra("Weatherdets");
        String weatherCity = intent.getStringExtra("Weathercity");
        String coordinates = intent.getStringExtra("Coordinates");
        String[] parts = coordinates.split(",");
        lat = Double.parseDouble(parts[0]); // 004
        lng = Double.parseDouble(parts[1]); // 034556
        String imgName = intent.getStringExtra("imgPath");

        Uri uri = Uri.parse("android.resource://com.example.soseapp/drawable/" + imgName);
        Drawable resizableImg = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Drawable myImage = Drawable.createFromStream(inputStream, uri.toString());
            Bitmap bitmap = ((BitmapDrawable) myImage).getBitmap();
            resizableImg = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 150, 150, true));
            ImageView img = findViewById(R.id.weatherImg);
            img.setImageDrawable(resizableImg);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Capture the layout's TextView and set the string as its text
        TextView gameDet = findViewById(R.id.gameDet);
        gameDet.setText(gameDets);
        TextView weatherDet = findViewById(R.id.localQuote);
        weatherDet.setText(weatherDets);
        TextView weatherCityText = findViewById(R.id.quoteTeam);
        weatherCityText.setText(weatherCity);
        link = "http://www.google.com/search?q=weather%20" + weatherCity;
        TextView seeGoogle = findViewById(R.id.weatherSite);
        seeGoogle.setPaintFlags(seeGoogle.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng city = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions()
                .position(city));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng),11));
        //googleMap.moveCamera(CameraUpdateFactory.zoomTo(4));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void openLink(View view){
        Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}