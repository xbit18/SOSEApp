package com.example.soseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.io.StringReader;
import cz.msebera.android.httpclient.Header;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//TODO Refactor and add comments to code
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    /**
     * Initialization of variables
     */
    private static AsyncHttpClient client = new AsyncHttpClient();
    private ArrayList<MatchWithWeather> matchesWithWeather = new ArrayList<>();
    private ArrayList<MatchWithBet> matchesWithBets = new ArrayList<>();
    private LayoutInflater inflater = null;
    private LinearLayout linearLayout;
    private LinearLayout buttonLayout;
    private ConstraintLayout progressButtonContainer;
    private ProgressBar progress;
    private Context context = this;
    private ConstraintLayout textLayout;
    private TextInputLayout focused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Normal setup operations
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = findViewById(R.id.mainLayout);
        focused = findViewById(R.id.outlinedTextField);
        inflater = LayoutInflater.from(context);
        buttonLayout = findViewById(R.id.buttonLayout);
        progressButtonContainer = findViewById(R.id.progressButtonContainer);
        progress = findViewById(R.id.progressBar);

        /**
         * Setup of centered textview layout
         */
        textLayout = (ConstraintLayout) inflater.inflate(R.layout.centered_textview, null, false);
        textLayout.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT));

        /**
         * Fix for bug of text input
         */
        linearLayout.setOnClickListener(v->focused.clearFocus());

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

    /**
     * Setup for search on nav bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    /**
     * Listener for weather or bets selector, calls getMatchesWithWeather or getMatchesWithBets
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        /**
         * Cancel possible pending requests when switching view
         */
        client.cancelRequests(context,true);

        String selection = (String) parent.getItemAtPosition(pos);
        if(!(progressButtonContainer.getChildAt(0) instanceof ProgressBar)){
            progressButtonContainer.removeView(textLayout);
            progressButtonContainer.addView(progress,0);
        }
        if(buttonLayout.getChildCount()>0){
            buttonLayout.removeAllViews();
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

    /**
     * Makes request to football-weather-prosumer
     */
    public void getMatchesWithWeather(Context context){
        /**
         * Making request to server
         */
        client.setMaxRetriesAndTimeout(2,1000);
        client.get(context,"http://10.0.2.2/football-weather/matches-with-weather", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    progressButtonContainer.removeView(progress);
                    String data = new String(responseBody);
                    /**
                     * Parsing xml response
                     */
                    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(data)));
                    doc.getDocumentElement().normalize();
                    matchesWithWeather = ReadXmlDomParser.parseMatchWithWeather(doc);
                    /**
                     * Dynamically creating buttons for every match and adding them to layout
                     */
                    if(matchesWithWeather.isEmpty()){
                        progressButtonContainer.addView(textLayout);
                    }else{
                        for (MatchWithWeather m : matchesWithWeather) {
                            Button button = createButtonWithWeather(m);
                            buttonLayout.addView(button);
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
                progressButtonContainer.removeView(progress);
                progressButtonContainer.addView(textLayout);
            }
        }).setTag("request");
    }

    /**
     * Makes request to football-bets-prosumer
     */
    public void getMatchesWithBet(Context context){
        client.get(context,"http://10.0.2.2:8084/matches-with-bets", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    progressButtonContainer.removeView(progress);

                    String data = new String(responseBody);
                    /**
                     * Parsing xml response
                     */
                    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(data)));
                    doc.getDocumentElement().normalize();
                    matchesWithBets = ReadXmlDomParser.parseMatchWithBet(doc);

                    /**
                     * Dynamically creating buttons for every match adding them to layout
                     */
                    if(matchesWithBets.isEmpty()){
                        progressButtonContainer.addView(textLayout);
                    }else {
                        for (MatchWithBet m : matchesWithBets) {
                            Button button = createButtonWithBet(m);
                            buttonLayout.addView(button);
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
                progressButtonContainer.removeView(progress);
                progressButtonContainer.addView(textLayout);
            }
        }).setTag("request");
    }

    /**
     * Creates buttons with weather info
     */
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
            btnTag.setOnClickListener(v -> openSingleMatch(m));

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

    /**
     * Creates buttons with bets info
     */
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
            btnTag.setOnClickListener(v -> openSingleMatch(m));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return btnTag;
    }

    /**
     * Opens single match activity
     */
    public void openSingleMatch(Match m){
        Intent intent = new Intent(this, SingleMatchPageActivity.class);
        String localTeamName = m.getLocalTeam().getName();
        String visitorTeamName = m.getVisitorTeam().getName();
        intent.putExtra("localTeamName",localTeamName);
        intent.putExtra("visitorTeamName",visitorTeamName);
        startActivity(intent);
    }

    /**
     * Opens search of weather by match
     */
    public void openWeatherByMatch(View view){
        TextInputLayout inputText = findViewById(R.id.outlinedTextField);
        String value = inputText.getEditText().getText().toString();
        Intent intent = new Intent(this, WeatherByMatch.class);
        intent.putExtra("query", value);
        startActivity(intent);
    }
}