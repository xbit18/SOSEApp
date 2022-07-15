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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cz.msebera.android.httpclient.Header;

public class SingleMatchPageActivity extends AppCompatActivity {
    /**
     * Initialization of variables
     */
    private Context context = this;
    private static AsyncHttpClient client = new AsyncHttpClient();
    private ArrayList<CompleteMatch> completeMatches;
    String link = "";
    LinearLayout layout = null;
    ConstraintLayout loading = null;
    CompleteMatch match = null;
    ConstraintLayout textLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Normal setup operations
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_match_page);
        layout = findViewById(R.id.mainLayout);
        LayoutInflater inflater = LayoutInflater.from(context);

        /**
         * Setup of progress bar layout
         */
        loading = (ConstraintLayout) inflater.inflate(R.layout.loading_screen, null, false);
        loading.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT));
        layout.addView(loading);
        /**
         * Setup of centered textview layout
         */
        textLayout = (ConstraintLayout) inflater.inflate(R.layout.centered_textview, null, false);
        textLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT));

        Intent intent = getIntent();
        String localTeamName = intent.getStringExtra("localTeamName");
        String visitorTeamName = intent.getStringExtra("visitorTeamName");

        getCompleteMatches(context, localTeamName, visitorTeamName);
    }

    /**
     * Get complete match details
     */
    public void getCompleteMatches(Context context, String localTeamName, String visitorTeamName){
        /**
         * Making request to server
         */
        client.setMaxRetriesAndTimeout(2,1000);
        client.get(context,"http://10.0.2.2:8086/aggregator/get-complete-matches", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("Successful request");
                try {
                    LayoutInflater inflater = LayoutInflater.from(context);
                    ConstraintLayout mainPage = (ConstraintLayout) inflater.inflate(R.layout.singlematch_layout, null, false);
                    mainPage.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT));
                    String data = new String(responseBody);
                    /**
                     * Parsing xml response
                     */
                    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(data)));
                    doc.getDocumentElement().normalize();
                    completeMatches = ReadXmlDomParser.parseCompleteMatch(doc);
                    /**
                     * Finding right match
                     */
                    for(CompleteMatch m : completeMatches){
                        if(m.getLocalTeam().getName().equals(localTeamName) && m.getVisitorTeam().getName().equals(visitorTeamName)){
                           match = m;
                           break;
                        }
                    }

                    layout.removeAllViews();
                    layout.addView(mainPage);

                    /**
                     * Setting up complete match details in view
                     */
                    TextView gameDet = findViewById(R.id.gameDet);
                    String gameDets = match.getLocalTeam().getName() + " " + match.getLocalTeamScore() + " - " + match.getVisitorTeamScore() + "  " + match.getVisitorTeam().getName();
                    gameDet.setText(gameDets);

                    TextView localTeam = findViewById(R.id.localTeam);
                    localTeam.setText(match.getLocalTeam().getName() + " wins");

                    TextView visitorTeam = findViewById(R.id.visitorTeam);
                    visitorTeam.setText(match.getVisitorTeam().getName() + " wins");

                    /**
                     * Formatting quotes
                     */
                    DecimalFormat dfor = new DecimalFormat("#.##");
                    dfor.setRoundingMode(RoundingMode.DOWN);

                    TextView quote1 = findViewById(R.id.quote1);
                    String formattedlocalQuote = dfor.format(match.getLocalTeamQuote());
                    quote1.setText(formattedlocalQuote);

                    TextView quoteX = findViewById(R.id.quotex);
                    String formattedtieQuote = dfor.format(match.getTieQuote());
                    quoteX.setText(formattedtieQuote);

                    TextView quote2 = findViewById(R.id.quote2);
                    String formattedvisitorQuote = dfor.format(match.getVisitorTeamQuote());
                    quote2.setText(formattedvisitorQuote);

                    TextView city = findViewById(R.id.city);
                    city.setText(match.getCity());

                    TextView weatherDets = findViewById(R.id.weatherDets);
                    String string = match.getWeather() + ", " + match.getTemperature() + "°C";
                    weatherDets.setText(string);

                    /**
                     * Setting google weather link
                     */
                    TextView seeGoogle = findViewById(R.id.seeOnGoogle);
                    link = "http://www.google.com/search?q=weather%20" + match.getCity();
                    seeGoogle.setPaintFlags(seeGoogle.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

                    /**
                     * Setting weather image
                     */
                    String imgName = match.getWeather().toLowerCase().replace(" ", "");
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

            /**
             * On failure display error message
             */
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                layout.removeView(loading);
                layout.addView(textLayout);
            }
        }).setTag("request");


    }

    /**
     * Opens weather on Google
     */
    public void openLink(View view){
        Uri uri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    /**
     * Opens google maps page
     */
    public void openGoogle(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("Coordinates", match.getCoordinates());
        intent.putExtra("City", match.getCity());
        startActivity(intent);
    }

}