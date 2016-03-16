package com.paulrlutz.vocabmanager.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paulrlutz.vocabmanager.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * DefinitionLookupActivity uses an AsyncTask to search for definitions of a given word using Pearson dictionary API.
 */

public class DefinitionLookupActivity extends AppCompatActivity {

    private static final String TAG = "DefLookupActivity";

    EditText editSearchWord;
    ProgressBar progressBar;
    ListView lvDefinitions;

    SharedPreferences prefs;

    // TODO Searching when the EditText brings up the definition for adagio. Why...?
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Retrieves the boolean that determines if dark mode is on, and applies the appropriate theme.
         * TODO Maybe wrap Activity or something. Then this doesn't have to be copy/pasted every Activity. "ThemedActivity"
         */
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean darkTheme = prefs.getBoolean(getString(R.string.preference_dark_mode_on_key), false);
        boolean instantSearch = prefs.getBoolean(getString(R.string.preference_instant_search_on_key), false);

        if(darkTheme) {
            setTheme(R.style.AppTheme_NoActionBar);
        }
        else {
            setTheme(R.style.AppTheme_Light_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definition_lookup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        lvDefinitions = (ListView) findViewById(R.id.lvDefinitions);
        editSearchWord = (EditText) findViewById(R.id.editSearchWord);

        // If there is a WORD in the extras, add it to editSearchWord
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String word = null;
        if(extras!=null) {
            word = extras.getString("WORD");
        }
        if(word!=null) {
            editSearchWord.setText(word);
            editSearchWord.setSelection(editSearchWord.getText().length());
            SearchDictionaryAPITask task = new SearchDictionaryAPITask();
            task.execute(editSearchWord.getText() + "");
        }

        // If instant search is on, update the ListView every time editSearchWord changes.
        if(instantSearch) {
            editSearchWord.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    SearchDictionaryAPITask task = new SearchDictionaryAPITask();
                    task.execute(s + "");
                }
            });
        }
        editSearchWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((actionId == EditorInfo.IME_ACTION_SEARCH)||(event != null && event.getAction() == KeyEvent.ACTION_DOWN)) {

                SearchDictionaryAPITask task = new SearchDictionaryAPITask();
                task.execute(v.getText() + "");

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editSearchWord.getWindowToken(), 0);
                return true;
            }
                return false;
            }
        });

    }

    /**
     * Takes the result String from the pearson API search and fills the ListView with the received definitions.
     *
     * TODO Scrolling to bottom of lvDefinitions should load more definitions, if there are more to load.
     */
    private void populateList(String jsonResults) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonResults);

        int resultCount = jsonObj.getInt("count");
        if(resultCount<1) {
            setWarning("No results found for: " + editSearchWord.getText());
            Log.e(TAG, "NO RESULTS FOUND");
            return;
        }

        JSONArray resultsArr = jsonObj.getJSONArray("results");

        final List<String> definitions = new ArrayList<String>();

        for(int i = 0; i < resultsArr.length(); i++) {
            String tempDef;
            try {
                tempDef = resultsArr.getJSONObject(i).getJSONArray("senses").getJSONObject(0).getString("definition");
            } catch (JSONException e) {
                Log.e(TAG, "Problem getting a definition from results");
                continue;
            }
            if(tempDef!=null) {
                definitions.add(tempDef);
                Log.d(TAG, "Definition " + i + ": " + tempDef);
            }
        }

        setListViewContent(definitions);

    }

    private void setWarning(String warning) {
        final List<String> warningList = new ArrayList<String>();
        warningList.add(warning);

        setListViewContent(warningList);
    }

    private void setListViewContent(final List<String> contentList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, contentList);
        lvDefinitions.setAdapter(adapter);

        lvDefinitions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String definition = contentList.get(position);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("DEFINITION",definition);
                setResult(AppCompatActivity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }

    // TODO Add proper timeout.

    /**
     * SearchDictionaryAPITask gets definitions for a given word, using Pearson's dictionaries API.
     *
     * pre execute: make the loading icon visible.
     *
     * post execute: hide loading icon, pass results to populateList()
     *
     * do in background: Access Pearson's dictionary API
     *
     */
    class SearchDictionaryAPITask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            ConnectivityManager cm =
                    (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if(isConnected) {
                progressBar.setVisibility(View.VISIBLE);
            }
            else {
                setWarning("No internet connection");
                cancel(true);
            }
        }

        protected String doInBackground(String... words) {
            String word = words[0];
            // Do some validation here
            final String API_URL = "https://api.pearson.com/v2/dictionaries/laad3/entries?";

            final String API_KEY = getResources().getText(R.string.dictionary_api_key)+"";

            try {
                URL url = new URL(API_URL + "headword=" + word + "&apiKey=" + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            try {
                populateList(response);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Problem parsing JSON results");// TODO Maybe add a Toast to warn the user.
            }
        }
    }
}
