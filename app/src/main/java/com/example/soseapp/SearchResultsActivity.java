package com.example.soseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.SearchManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.cache.Resource;

public class SearchResultsActivity extends AppCompatActivity {
private Context context = this;
private static AsyncHttpClient client = new AsyncHttpClient();
private ArrayList<CompleteMatch> completeMatches;
private LayoutInflater inflater = null;
String link = "";
LinearLayout layout = null;
CompleteMatch match = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_match_page);
        inflater = LayoutInflater.from(context);
        layout = findViewById(R.id.mainLayout);
        ConstraintLayout loading = (ConstraintLayout) inflater.inflate(R.layout.loading_screen, null, false);
        loading.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT));
        layout.addView(loading);
        Intent intent = getIntent();
        String teamName = intent.getStringExtra(SearchManager.QUERY);

        getCompleteMatches(context, teamName);

    }

    public void getCompleteMatches(Context context, String teamName){
        if(!(teamName.matches("[a-zA-Z]+"))){
            layout.removeAllViews();
            ConstraintLayout textLayout = (ConstraintLayout) inflater.inflate(R.layout.centered_textview, null, false);
            textLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT));
            TextView text = (TextView) textLayout.getViewById(R.id.errorMessage);
            text.setText("Wrong Syntax! Type team name or \"Home team - Away team\" and try again!");
            layout.addView(textLayout);
            return;
        }
        client.get(context,"http://192.168.0.126:8086/aggregator/get-complete-matches", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("Successful request");
                try {
                    /**
                     * Inflating match layout
                     */
                    ConstraintLayout mainPage = (ConstraintLayout) inflater.inflate(R.layout.singlematch_layout, null, false);
                    mainPage.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT));

                    /**
                     * Parsing xml response
                     */
                    String data = new String(responseBody);
                    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(data)));
                    doc.getDocumentElement().normalize();
                    completeMatches = ReadXmlDomParser.parseCompleteMatch(doc);
                    /**
                     * Finding right match
                     */
                    for(CompleteMatch m : completeMatches){
                        String localTeam = m.getLocalTeam().getName().toLowerCase(Locale.ROOT);
                        String visitorTeam = m.getVisitorTeam().getName().toLowerCase(Locale.ROOT);
                        String search = teamName.toLowerCase(Locale.ROOT).replace(" ","");
                        String wholeMatch = localTeam + "-" + visitorTeam;
                        if(localTeam.equals(search)|| visitorTeam.equals(search) || wholeMatch.equals(search)){
                            match = m;
                            System.out.println("team found");
                            break;
                        }
                    }

                    /**
                     * If match not found, display error message
                     */
                    if(match==null){
                        layout.removeAllViews();

                        ConstraintLayout textLayout = (ConstraintLayout) inflater.inflate(R.layout.centered_textview, null, false);
                        textLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT));
                        layout.addView(textLayout);
                    }
                    /**
                     * Otherwise display populated match layout
                     */
                    else {
                        layout.removeAllViews();
                        layout.addView(mainPage);

                        /**
                         * Setting game result
                         */
                        TextView gameDet = findViewById(R.id.gameDet);
                        String gameDets = match.getLocalTeam().getName() + " " + match.getLocalTeamScore() + " - " + match.getVisitorTeamScore() + "  " + match.getVisitorTeam().getName();
                        gameDet.setText(gameDets);

                        /**
                         * Setting and formatting quotes values
                         */
                        TextView localTeam = findViewById(R.id.localTeam);
                        localTeam.setText(match.getLocalTeam().getName() + " wins");

                        TextView visitorTeam = findViewById(R.id.visitorTeam);
                        visitorTeam.setText(match.getVisitorTeam().getName() + " wins");

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

                        /**
                         * Setting weather details
                         */
                        TextView city = findViewById(R.id.city);
                        city.setText(match.getCity());

                        TextView weatherDets = findViewById(R.id.weatherDets);
                        String string = match.getWeather() + ", " + match.getTemperature() + "°C";
                        weatherDets.setText(string);

                        TextView seeGoogle = findViewById(R.id.seeOnGoogle);
                        link = "http://www.google.com/search?q=weather%20" + match.getCity();
                        seeGoogle.setPaintFlags(seeGoogle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

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
                layout.removeAllViews();
                ConstraintLayout textLayout = (ConstraintLayout) inflater.inflate(R.layout.centered_textview, null, false);
                textLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT));
                layout.addView(textLayout);
            }
        }).setTag("request");


    }

    /**
     * Method for opening weather on Google
     */
    public void openLink(View view){
        Uri uri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    /**
     * Method for opening GoogleMaps activity
     */
    public void openGoogle(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("Coordinates", match.getCoordinates());
        intent.putExtra("City", match.getCity());
        startActivity(intent);
    }
}