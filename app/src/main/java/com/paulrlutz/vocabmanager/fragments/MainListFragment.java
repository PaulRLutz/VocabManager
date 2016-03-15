package com.paulrlutz.vocabmanager.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.paulrlutz.vocabmanager.R;
import com.paulrlutz.vocabmanager.data.VocabWord;
import com.paulrlutz.vocabmanager.data.VocabWordArrayAdapter;
import com.paulrlutz.vocabmanager.interfaces.MainListFragmentInterface;

import java.util.ArrayList;


public class MainListFragment extends Fragment {

    ListView mainListView;
    FloatingActionButton fabNewVocabWord;

    VocabWordArrayAdapter vocabWordArrayAdapter = null;

    private static final String TAG = "MainListFrag";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_main_list, container, false);

        mainListView = (ListView) view.findViewById(R.id.mainListView);
        fabNewVocabWord = (FloatingActionButton) view.findViewById(R.id.fabNewVocabWord);

        setArrayAdapter(((MainListFragmentInterface) getActivity()).getVocabWordArrayList());

        setupOnClickListeners();

        return view;

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

        super.onCreateOptionsMenu(menu,inflater);
    }

    // Setup the ListView with the given VocabWord ArrayList
    public void setArrayAdapter(ArrayList<VocabWord> vocabList) {

        if((vocabList == null)||(vocabList.size() < 1)) { // Don't do anything if the vocabList is null or empty.
            Log.e(TAG, "VocabWord list is null.");
            return;
        }
        else if(mainListView == null) { // Don't do anything if the MainListView is null.
            Log.e(TAG, "mainListView is null.");
            return;
        }

        vocabWordArrayAdapter = new VocabWordArrayAdapter(getActivity(), R.layout.row_layout_vocab_word, vocabList);

        mainListView.setAdapter(vocabWordArrayAdapter);
    }

    /**
     * Handle click on an item.
     * This should notify the activity to switch to the appropriate Fragment.
     */
    private void setupOnClickListeners() {

        // The FAB should just call createNewVocabWord.
        if(fabNewVocabWord != null) {
            fabNewVocabWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainListFragmentInterface) getActivity()).createNewVocabWord(); // Tell activity that the given word was clicked.
                }
            });
        }

        if(mainListView != null) {

            // Clicking an item will call the Interface's showVocabWordInformation, and pass the VocabWord that was clicked.
            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // If an item is clicked and not null, tell the main activity
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Object uncastTempVocabWord = mainListView.getItemAtPosition(position);

                    if (uncastTempVocabWord != null) { // Only continue if the object is not null.
                        VocabWord tempWord = null;
                        try { // Try to cast the Object. Return if it fails.
                            tempWord = ((VocabWord) uncastTempVocabWord);
                        } catch (ClassCastException e) {
                            return;
                        }

                        Log.d(TAG, "Clicked item with ID: " + tempWord.getID());


                        ((MainListFragmentInterface) getActivity()).showVocabWordInformation(tempWord); // Tell the activity that the given word was clicked.
                    }

                }
            });

            /**
             * Handle long click on an item.
             * On long click, show a pop-up menu with options Edit and Delete.
             * Edit should tell the activity to switch to an Edit Fragment, and provide the VocabWord ID.
             * Delete should give a Delete/Cancel Dialog.
             * If Delete is clicked again, tell the activity to delete the VocabWord with given ID. Also remove it from the current list.
             */
            mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                               int position, long id) {
                    PopupMenu p = new PopupMenu(getActivity(), view);
                    p.getMenuInflater().inflate(R.menu.edit_delete_popup, p.getMenu());

                    final int selectedPos = position;

                    p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    Log.d(TAG, "EDIT item " + selectedPos);
                                    Log.d(TAG, "Vocab word to edit: " + vocabWordArrayAdapter.getItem(selectedPos).getName());
                                    ((MainListFragmentInterface) getActivity()).updateVocabWord(vocabWordArrayAdapter.getItem(selectedPos)); // Tell the activity that the given word was clicked.
                                    return true;
                                case R.id.delete:
                                    // TODO add an "Are you sure?" Dialog
                                    VocabWord selectedWord = (VocabWord)mainListView.getAdapter().getItem(selectedPos);
                                    Log.d(TAG, "DELETE word : " + selectedWord.getName() + " w/ ID: " + selectedWord.getID());

                                    ((MainListFragmentInterface) getActivity()).deleteVocabWord(selectedWord);
                                    setArrayAdapter(((MainListFragmentInterface) getActivity()).getVocabWordArrayList());
                                    return true;
                            }
                            return true;
                        }
                    });

                    p.show();
                    return true;
                }
            });
        }
    }


}