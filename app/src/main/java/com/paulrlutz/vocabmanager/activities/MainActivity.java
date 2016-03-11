package com.paulrlutz.vocabmanager.activities;

/*
    TODO: Master TODOList
    1. Replace XML text elements with Strings
    2. Implement definition lookup. Maybe with a little bit of this: http://www.programmableweb.com/api/oxford-english-dictionary
    3. Add some navigation to the app name in the ActionBar
    4. Add animations to fragments.
 */

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.paulrlutz.vocabmanager.R;
import com.paulrlutz.vocabmanager.data.VocabWord;
import com.paulrlutz.vocabmanager.data.VocabWordDatabaseAccessObject;
import com.paulrlutz.vocabmanager.fragments.AddEditVocabWordFragment;
import com.paulrlutz.vocabmanager.fragments.MainListFragment;
import com.paulrlutz.vocabmanager.fragments.VocabWordFragment;
import com.paulrlutz.vocabmanager.interfaces.AddEditVocabWordFragmentInterface;
import com.paulrlutz.vocabmanager.interfaces.MainListFragmentInterface;

import java.util.ArrayList;

/**
 * This activity has two purposes:
 * 1. Provide easy navigation between the fragments that handle the vocabulary word data.
 * 2. Provide easy access to the vocabulary word data to the Fragments.
 *
 * Each Fragment will provide an interface that MainActivity will implement.
 * These interfaces will allow the MainActivity to provide the Fragments with the functionality they need to operate.
 * Most of the interfaces will revolve around access to the vocabulary data. Probably.
 *
 */
public class MainActivity extends AppCompatActivity implements MainListFragmentInterface, AddEditVocabWordFragmentInterface {
    FragmentManager fragmentManager;

    MainListFragment mainListFragment = null;
    VocabWordFragment vocabWordFragment = null;
    AddEditVocabWordFragment addEditVocabWordFragment = null;

    VocabWordDatabaseAccessObject vocabWordDAO = null;

    private static final String TAG = "Main Activity";
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Retrieves the boolean that determines if dark mode is on, and applies the appropriate theme.
         * TODO Maybe wrap Activity or something. Then this doesn't have to be copy/pasted every Activity. "ThemedActivity"
         */
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean darkTheme = prefs.getBoolean(getString(R.string.preference_dark_mode_on_key), false);

        if(darkTheme) {
            setTheme(R.style.AppTheme_NoActionBar);
        }
        else {
            setTheme(R.style.AppTheme_Light_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup the SupportActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize objects
        fragmentManager = getSupportFragmentManager();

        mainListFragment = new MainListFragment();
        vocabWordFragment = new VocabWordFragment();
        addEditVocabWordFragment = new AddEditVocabWordFragment();

        vocabWordDAO = VocabWordDatabaseAccessObject.getInstance(this);

        // Set fragment to the MainListFragment.
        setCurrentFragment(mainListFragment);

        // Handle the Intent (only relevant for Search intents)
        handleIntent(getIntent());
    }

    /**
     * If a new Intent is delivered, and Activity is already created.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }


    /**
     * Handles intents.
     * Probable intents are:
     * 1. Intent.ACTION_SEARCH: Currently unused. Eventually redirect query to Search Fragment/Activity.
     * 2. Intent.ACTION_VIEW: Received when a Search Suggestion is clicked. Displays the appropriate VocabWord info.
     */
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) { // TODO implement advanced search
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "Search Received: " + query);

        }
        else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // Retrieve data from URI.
            Uri data = intent.getData();
            Log.d(TAG, "Data received: " + data);

            Integer receivedID = null;

            try {
                receivedID = Integer.parseInt(""+data); // This just feels wrong. That's not how you get data out of a URI. TODO Fix this URI.
            }
            catch(Exception e) {
                // So many things could go wrong it's hard to really pin down a specific exception.
            }

            if (receivedID!=null) { // If the try up there failed, then the URI is probably not a valid integer. So do nothing.
                VocabWord word = vocabWordDAO.getVocabWordByID(Integer.parseInt(""+data));
                showVocabWordInformation(word);
            }

        }
    }







    /**
     * Sets the current Fragment to the given targetFragment.
     * Calls the setCurrentFragment with Animations, just without animations.
     */
    public void setCurrentFragment(Fragment targetFragment) {

        setCurrentFragment(targetFragment, -1, -1);

    }

    /**
     * Sets the current Fragment to the given targetFragment.
     * Includes  animations.
     * In use but the animations aren't utilized yet.
     */
    public void setCurrentFragment(Fragment targetFragment, int animA, int animB) {

        FragmentTransaction ft = fragmentManager.beginTransaction();

        if(animA!=-1&&animB!=-1){
            ft.setCustomAnimations(animA, animB);
        }

        ft.replace(R.id.currentFragment, targetFragment).addToBackStack( "tag" );

        ft.commit();
    }

    /**
     * The button presses for the ActionBar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Launch SettingsActivity if Settings button is clicked.
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.menuSortName) { // Change the default Sort method to Name and updates the MainList.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(getString(R.string.preference_sort_key), VocabWordDatabaseAccessObject.SortType.toInteger(VocabWordDatabaseAccessObject.SortType.ALPHABETICAL_NAME));
            editor.commit();

            mainListFragment.setArrayAdapter(getVocabWordArrayList());
        }
        else if (id == R.id.menuSortCategory) { // Change the default Sort method to Category and updates the MainList.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(getString(R.string.preference_sort_key), VocabWordDatabaseAccessObject.SortType.toInteger(VocabWordDatabaseAccessObject.SortType.ALPHABETICAL_CATEGORY_NAME));
            editor.commit();

            mainListFragment.setArrayAdapter(getVocabWordArrayList());
        }


        return super.onOptionsItemSelected(item);
    }


    /**
     * Intercepts the back button, and exits the app if in the MainListFragment.
     * TODO Is this bad practice? It feels like bad practice.
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "BACK BUTTON PRESSED");

        Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.currentFragment);

        if(curFragment instanceof MainListFragment) {
            Log.d(TAG, "Close app");
            finish();
        }

        super.onBackPressed();
    }




    /**
     * MainListFragmentInterface methods.
     */

    @Override
    public ArrayList<VocabWord> getVocabWordArrayList() {
        int sortType = prefs.getInt(getString(R.string.preference_sort_key), VocabWordDatabaseAccessObject.SortType.toInteger(VocabWordDatabaseAccessObject.SortType.ALPHABETICAL_NAME));
        return vocabWordDAO.getVocabWordList(VocabWordDatabaseAccessObject.SortType.fromInteger(sortType));
    }

    @Override
    public void showVocabWordInformation(VocabWord word) {

        getFragmentManager().findFragmentById(R.id.currentFragment);

        vocabWordFragment.setVocabWord(word);

        //setCurrentFragment(fragment, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        setCurrentFragment(vocabWordFragment);
    }

    @Override
    public void createNewVocabWord() {
        addEditVocabWordFragment.setVocabWord(null); // Let the addEditFragment know that it is creating a VocabWord instead of editing one.
        setCurrentFragment(addEditVocabWordFragment);
    }

    @Override
    public void updateVocabWord(VocabWord vocabWord) {
        addEditVocabWordFragment.setVocabWord(vocabWord);
        setCurrentFragment(addEditVocabWordFragment);
    }

    @Override
    public void deleteVocabWord(VocabWord vocabWord) {
        vocabWordDAO.deleteVocabWordWithID(vocabWord.getID());
    }

    /**
     * AddEditVocabWordFragmentInterface methods.
     */

    @Override
    public void addOrUpdateVocabWord(VocabWord vocabWord) {
        vocabWordDAO.addOrUpdateVocabWord(vocabWord);
    }

    @Override
    public ArrayList<String> getCategories() {
        return vocabWordDAO.getCategoryNames();
    }

    @Override
    public void addCategory(String categoryName) {
        return;
    }

}
