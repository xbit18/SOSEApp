package com.example.soseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.material.button.MaterialButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.io.StringReader;
import cz.msebera.android.httpclient.Header;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static AsyncHttpClient client = new AsyncHttpClient();
    ArrayList<MatchWithWeather> matchesWithWeather = new ArrayList<>();
    ArrayList<MatchWithBet> matchesWithBets = new ArrayList<>();
    LinearLayout linearLayout;
    ConstraintLayout progress;
    Context context = this;
    static int idCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /**
         * normal setup operations
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = findViewById(R.id.mainLayout);
        progress = findViewById(R.id.loadingConstraint);

        /**
         * Setup of spinner element on top of page
         */
        Spinner spinner = findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choices, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void getMatchesWithWeather(Context context){
        /**
         * Making request to server
         */
        client.get(context,"http://192.168.1.152:8083/cities/get", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ConstraintLayout loading = findViewById(R.id.loadingConstraint);
                int children = linearLayout.getChildCount();
                if(children>1){
                    linearLayout.removeView(loading);
                }
                try {
                    String data = new String(responseBody);
                    /**
                     * Parsing xml response
                     */
                    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(data)));
                    doc.getDocumentElement().normalize();
                    matchesWithWeather = ReadXmlDomParser.parseMatchWithWeather(doc);

                    /**
                     * Dynamically creating buttons for every match
                     */
                    for (MatchWithWeather m : matchesWithWeather) {
                        Button button = createButtonWithWeather(m);
                        linearLayout.addView(button);
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
                ProgressBar progress = findViewById(R.id.progressBar2);
                ConstraintLayout loading = findViewById(R.id.loadingConstraint);
                loading.removeView(progress);
                TextView errorMessage = findViewById(R.id.textView2);
                errorMessage.setText("Sorry, something went wrong. Try again later!");
            }
        }).setTag("request");
    }
    public void getMatchesWithBet(Context context){
        client.get(context,"http://192.168.1.152:8084/matches-with-bets/get", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                int children = linearLayout.getChildCount();
                if(children>1){
                    linearLayout.removeView(progress);
                }
                try {
                    String data = new String(responseBody);
                    /**
                     * Parsing xml response
                     */
                    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(data)));
                    doc.getDocumentElement().normalize();
                    matchesWithBets = ReadXmlDomParser.parseMatchWithBet(doc);

                    /**
                     * Dynamically creating buttons for every match
                     */
                    int idCounter = 0;
                    for (MatchWithBet m : matchesWithBets) {
                        Button button = createButtonWithBet(m);
                        linearLayout.addView(button);
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
                ProgressBar progress = findViewById(R.id.progressBar2);
                ConstraintLayout loading = findViewById(R.id.loadingConstraint);
                loading.removeView(progress);
                TextView errorMessage = findViewById(R.id.textView2);
                errorMessage.setText("Sorry, something went wrong. Try again later!");
            }
        }).setTag("request");
    }

    public void openMatchWithWeather(MatchWithWeather m) {
        Intent intent = new Intent(this, WeatherMatch.class);
        String gameDets = m.getLocalTeam().getName() + " " + m.getLocalTeamScore() + " - " + m.getVisitorTeamScore() + "  " + m.getVisitorTeam().getName();
        String weatherDets = m.getWeather() + ", " + m.getTemperature() + "°C";
        String weatherCity = m.getCity();
        String coordinates = m.getCoordinates();
        String imgName = m.getWeather().toLowerCase().replace(" ", "");
        intent.putExtra("Gamedets",gameDets);
        intent.putExtra("Weatherdets",weatherDets);
        intent.putExtra("Weathercity", weatherCity);
        intent.putExtra("Coordinates",coordinates);
        intent.putExtra("imgPath", imgName);
        startActivity(intent);
    }

    public void openMatchWithBet(MatchWithBet m) {
        Intent intent = new Intent(this, BetsMatch.class);
        String gameDets = m.getLocalTeam().getName() + " " + m.getLocalTeamScore() + " - " + m.getVisitorTeamScore() + "  " + m.getVisitorTeam().getName();
        String coordinates = m.getCoordinates();
        DecimalFormat dfor = new DecimalFormat("#.##");
        dfor.setRoundingMode(RoundingMode.DOWN);
        String formattedlocalTeamQuote = dfor.format(m.getLocalTeamQuote());
        String formattedvisitorTeamQuote = dfor.format(m.getVisitorTeamQuote());
        String formattedtieQuote = dfor.format(m.getTieQuote());
        intent.putExtra("Gamedets",gameDets);
        intent.putExtra("localQuote",formattedlocalTeamQuote);
        intent.putExtra("tieQuote",formattedtieQuote);
        intent.putExtra("visitorQuote",formattedvisitorTeamQuote);
        intent.putExtra("Coordinates",coordinates);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        /**
         * Cancel possible pending requests when switching view
         */
        client.cancelRequests(context,true);

        String selection = (String) parent.getItemAtPosition(pos);
        if(linearLayout.getChildAt(1).getId()!=R.id.loadingConstraint){
            if(linearLayout.getChildCount()>1){
                linearLayout.removeViews(1,linearLayout.getChildCount()-1);
            }
            linearLayout.addView(progress);
        }
        if(selection.equals("Weather")){
            getMatchesWithWeather(context);
        } else {
            getMatchesWithBet(context);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //NOT USED
    }

    public Button createButtonWithWeather(MatchWithWeather m){
        /**
         * Creation of button
         */
        Button btnTag = new Button(context);
        try {
            /**
             * Setup of button layout parameters
             */
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            btnTag.setLayoutParams(params);

            /**
             * Setup of button text and other options
             */
            String buttonText = m.getLocalTeam().getName() +
                    " " + m.getLocalTeamScore() +
                    " - " + m.getVisitorTeamScore() +
                    " " + m.getVisitorTeam().getName() +
                    "\n " + m.getCity() + " - " + m.getWeather() + ", " + m.getTemperature() + "°C";
            btnTag.setText(buttonText);
            btnTag.setLineSpacing(50, 1);
            btnTag.setPadding(50, 80, 50, 80);
            btnTag.setOnClickListener(v -> openMatchWithWeather(m));

            /**
             * Setup of image inside button
             */
            String imgName = m.getWeather().toLowerCase().replace(" ", "");
            Uri uri = Uri.parse("android.resource://com.example.soseapp/drawable/" + imgName);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Drawable myImage = Drawable.createFromStream(inputStream, uri.toString());
            Bitmap bitmap = ((BitmapDrawable) myImage).getBitmap();
            Drawable resizableImg = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 150, 150, true));
            btnTag.setCompoundDrawablesWithIntrinsicBounds(null, null, resizableImg, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return btnTag;
    }

    public Button createButtonWithBet(MatchWithBet m){
        /**
         * Creation of button
         */
        Button btnTag = new Button(context);
        try {
            /**
             * Setup of button layout parameters
             */
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            btnTag.setLayoutParams(params);

            /**
             * Formatting of quote strings
             */
            DecimalFormat dfor = new DecimalFormat("#.##");
            dfor.setRoundingMode(RoundingMode.DOWN);
            String formattedlocalTeamQuote = dfor.format(m.getLocalTeamQuote());
            String formattedvisitorTeamQuote = dfor.format(m.getVisitorTeamQuote());

            /**
             * Setup of button text and other options
             */
            String buttonText = m.getLocalTeam().getName() +
                                " " + m.getLocalTeamScore() +
                                " - " + m.getVisitorTeamScore() +
                                " " + m.getVisitorTeam().getName() +
                                "\n Home Team: " + formattedlocalTeamQuote +
                                " - Away Team: " + formattedvisitorTeamQuote;
            btnTag.setText(buttonText);
            btnTag.setLineSpacing(50, 1);
            btnTag.setPadding(50, 80, 50, 80);
            btnTag.setOnClickListener(v -> openMatchWithBet(m));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return btnTag;
    }
}