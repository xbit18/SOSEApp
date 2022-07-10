package com.example.soseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cz.msebera.android.httpclient.Header;

public class WeatherByMatch extends AppCompatActivity {
    private static AsyncHttpClient client = new AsyncHttpClient();
    private Context context = this;
    private String link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_by_match);
        Button button = findViewById(R.id.button2);
        button.setOnClickListener(v->openLink());
        getMatch();
    }

    public void getMatch(){
        Intent intent = getIntent();
        String query = intent.getStringExtra("query").replace(" ","");
        String[] strings = query.split("-");
        String localTeam = strings[0];
        String visitorTeam = strings[1];
        client.setMaxRetriesAndTimeout(2,1000);
        RequestParams params = new RequestParams();
        params.add("localTeamName",localTeam);
        params.add("visitorTeamName",visitorTeam);
        client.get(context,"http://192.168.1.152:8083/football-weather/weather-by-match", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("Successful request");
                try {
                    String data = new String(responseBody);
                    System.out.println(data);
                    /**
                     * Parsing xml response
                     */
                    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(data)));
                    doc.getDocumentElement().normalize();
                    Weather weather = ReadXmlDomParser.parseWeather(doc);

                    TextView gameDet = findViewById(R.id.gameDet);
                    gameDet.setText(localTeam + " - " + visitorTeam);

                    TextView city = findViewById(R.id.cityDet);
                    city.setText(weather.getCity());
                    TextView conditionDet = findViewById(R.id.conditionDet);
                    conditionDet.setText(weather.getWeather());
                    TextView temperature = findViewById(R.id.tempDet);
                    temperature.setText(weather.getTemperature() + "°C");

                    link = "http://www.google.com/search?q=weather%20" + weather.getCity();

                    String imgName = weather.getWeather().toLowerCase().replace(" ", "");
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

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        }).setTag("request");
    }

    public void openLink(){
        Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}