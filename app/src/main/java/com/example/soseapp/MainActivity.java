package com.example.soseapp;

import androidx.appcompat.app.AppCompatActivity;
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
    public static final String EXTRA_MESSAGE = "com.example.soseapp.MESSAGE";
    /**
     *  Making async http request
     *  */
    private static AsyncHttpClient client = new AsyncHttpClient();
    ArrayList<MatchWithWeather> matchesWithWeather = new ArrayList<>();
    ArrayList<MatchWithBet> matchesWithBets = new ArrayList<>();
    LinearLayout linearLayout;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** normal setup operations */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = findViewById(R.id.mainLayout);
        Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choices, R.layout.spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        //getMatchesWithWeather(context);
        spinner.setOnItemSelectedListener(this);
        /** Creation of toggle button and onClickListener */
        /*ToggleButton b = new ToggleButton(context);
        b.setTextOn("Check Odds");
        b.setTextOff("Check Weather");
        b.setChecked(true);
        b.setHighlightColor(Color.RED);
        linearLayout.addView(b);
        b.setOnClickListener(v -> {
            if(b.isChecked()){
                linearLayout.removeViews(1,4);
                getMatchesWithWeather(context);
            } else {
                linearLayout.removeViews(1,4);
                getMatchesWithBet(context);
            }
        });*/
    }

    public void getMatchesWithWeather(Context context){
        client.get("http://192.168.1.152:8083/cities/get", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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
                    int idCounter = 0;
                    for (MatchWithWeather m : matchesWithWeather) {
                        try {
                            LinearLayout linLayout = new LinearLayout(context);
                            linLayout.setId(idCounter);
                            ContextThemeWrapper newContext = new ContextThemeWrapper(context, androidx.appcompat.R.style.Widget_AppCompat_Button_Colored);
                            Button btnTag = new Button(newContext);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            btnTag.setLayoutParams(params);
                            String buttonText = m.getLocalTeam().getName() +
                                    " " + m.getLocalTeamScore() +
                                    " - " + m.getVisitorTeamScore() +
                                    " " + m.getVisitorTeam().getName() +
                                    "\n " + m.getCity() + " - " + m.getWeather() + ", " + m.getTemperature() + "°C";
                            btnTag.setText(buttonText);
                            btnTag.setId(idCounter);
                            btnTag.setLineSpacing(50, 1);

                            String imgName = m.getWeather().toLowerCase().replace(" ", "");
                            Uri uri = Uri.parse("android.resource://com.example.soseapp/drawable/" + imgName);
                            Drawable resizableImg = null;
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(uri);
                                Drawable myImage = Drawable.createFromStream(inputStream, uri.toString());
                                Bitmap bitmap = ((BitmapDrawable) myImage).getBitmap();
                                resizableImg = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 150, 150, true));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
                            btnTag.setCompoundDrawablesWithIntrinsicBounds(null, null, resizableImg, null);
                            btnTag.setPadding(50, 80, 50, 80);
                            btnTag.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    openMatchWithWeather(m);
                                }
                            });
                            idCounter++;

                            //btnTag.setInsetBottom(2);
                            //btnTag.setInsetTop(0);
                            //btnTag.setCornerRadius(0);
                            linLayout.addView(btnTag);
                            linearLayout.addView(linLayout);
                            //linLayout.setBackgroundColor(Color.BLACK);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                System.out.println("FAIL");
                String resp = new String(responseBody);
                System.out.println(resp);
            }
        });
    }
    public void getMatchesWithBet(Context context){
        client.get("http://192.168.1.152:8084/matches-with-bets/get", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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
                        try {
                            LinearLayout linLayout = new LinearLayout(context);
                            linLayout.setId(idCounter);
                            Button btnTag = new Button(context);
                            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            DecimalFormat dfor = new DecimalFormat("#.##");
                            dfor.setRoundingMode(RoundingMode.DOWN);
                            String formattedlocalTeamQuote = dfor.format(m.getLocalTeamQuote());
                            String formattedvisitorTeamQuote = dfor.format(m.getVisitorTeamQuote());
                            String buttonText = m.getLocalTeam().getName() +
                                    " " + m.getLocalTeamScore() +
                                    " - " + m.getVisitorTeamScore() +
                                    " " + m.getVisitorTeam().getName() +
                                    "\n Home Team: " + formattedlocalTeamQuote + " - Away Team: " + formattedvisitorTeamQuote;
                            btnTag.setText(buttonText);
                            btnTag.setId(idCounter);
                            btnTag.setLineSpacing(50, 1);
                            btnTag.setPadding(50, 80, 50, 80);
                            idCounter++;
                            btnTag.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    openMatchWithBet(m);
                                }
                            });
                            linLayout.addView(btnTag);
                            linearLayout.addView(linLayout);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                System.out.println("FAIL");
                //String resp = new String(responseBody);
                //System.out.println(resp);
            }
        });
    }

    public void openMatchWithWeather(MatchWithWeather m) {
        Intent intent = new Intent(this, WeatherMatch.class);
        String gameDets = m.getLocalTeam().getName() + " " + m.getLocalTeamScore() + " - " + m.getVisitorTeamScore() + "  " + m.getVisitorTeam().getName();
        String weatherDets = m.getWeather() + ", " + m.getTemperature() + "°C";
        String weatherCity = m.getCity();
        String coordinates = m.getCoordinates();
        intent.putExtra("Gamedets",gameDets);
        intent.putExtra("Weatherdets",weatherDets);
        intent.putExtra("Weathercity", weatherCity);
        intent.putExtra("Coordinates",coordinates);
        String imgName = m.getWeather().toLowerCase().replace(" ", "");
        intent.putExtra("imgPath", imgName);
        startActivity(intent);
    }

    public void openMatchWithBet(MatchWithBet m) {
        Intent intent = new Intent(this, BetsMatch.class);
        String gameDets = m.getLocalTeam().getName() + " " + m.getLocalTeamScore() + " - " + m.getVisitorTeamScore() + "  " + m.getVisitorTeam().getName();
        String coordinates = m.getCoordinates();
        intent.putExtra("Gamedets",gameDets);
        DecimalFormat dfor = new DecimalFormat("#.##");
        dfor.setRoundingMode(RoundingMode.DOWN);
        String formattedlocalTeamQuote = dfor.format(m.getLocalTeamQuote());
        String formattedvisitorTeamQuote = dfor.format(m.getVisitorTeamQuote());
        String formattedtieQuote = dfor.format(m.getTieQuote());
        intent.putExtra("localQuote",formattedlocalTeamQuote);
        intent.putExtra("tieQuote",formattedtieQuote);
        intent.putExtra("visitorQuote",formattedvisitorTeamQuote);
        intent.putExtra("Coordinates",coordinates);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String selection = (String) parent.getItemAtPosition(pos);
        if(selection.equals("Weather")){
            if(linearLayout.getChildAt(1)!=null){
                linearLayout.removeViews(1,4);
            }
            getMatchesWithWeather(context);
        } else {
            linearLayout.removeViews(1,4);
            getMatchesWithBet(context);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}