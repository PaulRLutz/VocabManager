package com.paulrlutz.vocabmanager.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.paulrlutz.vocabmanager.R;
import com.paulrlutz.vocabmanager.activities.DefinitionLookupActivity;
import com.paulrlutz.vocabmanager.data.VocabWord;
import com.paulrlutz.vocabmanager.interfaces.AddEditVocabWordFragmentInterface;

import java.util.ArrayList;



public class AddEditVocabWordFragment extends Fragment {

    private static final String TAG = "AddVocabWord Fragment";

    Button btnCancel;
    Button btnSave;

    ImageButton btnDefinitionSearch;

    EditText editName;
    EditText editDefinition;
    EditText editNotes;

    Spinner spinnerCategory;

    VocabWord vocabWord = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_vocab_word, container, false);

        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnDefinitionSearch  = (ImageButton) view.findViewById(R.id.btnDefinitionSearch);

        editName = (EditText) view.findViewById(R.id.editName);
        editDefinition = (EditText) view.findViewById(R.id.editDefinition);
        editNotes = (EditText) view.findViewById(R.id.editNotes);

        spinnerCategory = (Spinner) view.findViewById(R.id.spinnerCategory);


        setupOnClickListeners();
        populateSpinner(null);
        populateFields();

        return view;
    }

    @Override
    public void onResume() {
        populateFields();
        super.onResume();
    }

    /**
     * Populates the views with the Views with the values of the VocabWord that is to be edited.
     */
    private void populateFields() {

        if(vocabWord != null) {
            String tempName = vocabWord.getName();
            String tempDefinition = vocabWord.getDefinition();
            String tempNotes = vocabWord.getNotes();
            String tempCategory = vocabWord.getCategory();

            if(tempName!=null&&editName!=null){
                editName.setText(tempName);
            }
            if(tempDefinition!=null&&editDefinition!=null){
                editDefinition.setText(tempDefinition);
            }
            if(tempNotes!=null&&editNotes!=null){
                editNotes.setText(tempNotes);
            }
            if(tempCategory!=null&&spinnerCategory!=null){
                spinnerCategory.setSelection(categoryNames.indexOf(tempCategory)); // TODO Add some checks to this.
            }
        }
    }

    /**
     * Sets the VocabWord word.
     * If this is called, then we are editing a VocabWord.
     */
    public void setVocabWord(VocabWord word) {
        vocabWord = word;
        if(this.isVisible()) { // This is unlikely, but you can never be too safe.
            populateFields();
        }
    }

    ArrayList<String> categoryNames;
    /**
     * Populates the categorySpinner with data from the activity.
     * This should really only be called when setting stuff up, or when adding a new category.
     * Technically a logic error, adding a second custom Category will remove the previous custom Category.
     * Categories are only saved when they are attached to a VocabWord.
     * Not really a huge deal, but it will be fixed when a CategoryDB is implemented.
     *
     * TODO Figure out the long click stuff to delete Categories. Probably will have to extend Spinner into a custom class. http://stackoverflow.com/questions/26578209/spinner-setonitemlongclicklistener-doesnt-work
     */
    private void populateSpinner(String additionalCategory) {
        ArrayAdapter<String> spinnerCategoryArrayAdapter;

        categoryNames = ((AddEditVocabWordFragmentInterface) getActivity()).getCategories(); // Get all the Categories.

        // Add the new custom Category if it isn't null.
        if(additionalCategory != null) {
            categoryNames.add(additionalCategory);
        }

        // If God forbid we get an empty ArrayList, instantiate it.
        if(categoryNames == null) {
            Log.d(TAG, "Received empty categoryNames");
            categoryNames = new ArrayList<String>();
        }

        // Add an "Add new Category" item.
        categoryNames.add("Add new Category"); // TODO Replace this with a String resource.

        String[] spinnerList = categoryNames.toArray(new String[0]);

        spinnerCategoryArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerList); //selected item will look like a spinner set from XML
        spinnerCategoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerCategoryArrayAdapter);

        /**
         * If the selected item is the last in the array, then it is the "Add new Category" item.
         */
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Spinner index selected: " + position);
                if(position == (categoryNames.size()-1)) {
                    showNewCategoryAlertDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Setting up the button click actions.
     *
     * Basically, Cancel clears everything and goes back a screen. Save calls addEditVocabWord().
     */
    private void setupOnClickListeners() {
        if(btnCancel != null) {
            // Clear the stuff if Cancel button is clicked.
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    editName.setText("");
                    editDefinition.setText("");
                    editNotes.setText("");

                    spinnerCategory.setSelection(0);


                    vocabWord = null;

                    getActivity().onBackPressed(); // TODO Feels like bad practice, should probably use the backstack. http://stackoverflow.com/questions/25550643/customanimation-when-calling-popbackstack-on-a-fragmentmanager
                }
            });
        }
        if(btnSave != null) {
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((editName.getText()+"").equals("")) {
                        Toast.makeText(getActivity(), "Name cannot be blank", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    addEditVocabWord();

                    editName.setText("");
                    editDefinition.setText("");
                    editNotes.setText("");

                    spinnerCategory.setSelection(0);

                    vocabWord = null;

                    getActivity().onBackPressed(); // TODO Feels like bad practice, should probably use the backstack.
                }
            });
        }
        if(btnDefinitionSearch != null) {
            btnDefinitionSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), DefinitionLookupActivity.class);

                    String searchWord = editName.getText()+"";
                    if(searchWord!=null&&searchWord.length()>0) {
                        intent.putExtra("WORD",searchWord);
                    }

                    startActivityForResult(intent, 1);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == AppCompatActivity.RESULT_OK){
                String result=data.getStringExtra("DEFINITION");
                if(result!=null){
                    editDefinition.setText(result);
                }
            }
            if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                Log.d(TAG, "Get def cancelled");
            }
        }
    }
    /**
     * Method to call when Save is clicked.
     *
     * If vocabWord is null, then create a new one with the EditText Name and Definition.
     * This will make a VocabWord with ID -1, so the data handler will make a new one.
     *
     * If vocabWord is not null, then update its fields.
     */
    private void addEditVocabWord() {
        if (vocabWord == null) {
            vocabWord = new VocabWord((editName.getText()+""),(editDefinition.getText()+""));
        }
        else {
            vocabWord.setName((editName.getText()+""));
            vocabWord.setDefinition((editDefinition.getText()+""));
        }
        vocabWord.setCategory(spinnerCategory.getSelectedItem().toString());
        vocabWord.setNotes(editNotes.getText()+"");

        ((AddEditVocabWordFragmentInterface) getActivity()).addOrUpdateVocabWord(vocabWord);
    }

    /**
     * Shows the AlertDialog that allows the user to input new categories.
     */
    private void showNewCategoryAlertDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final EditText editInput = new EditText(getActivity());

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // DON'T USE. THIS IS OVERRIDDEN ONCE THE DIALOG IS SHOWN.
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                spinnerCategory.setSelection(0);
            }
        });
        editInput.setHint("Category name");
        builder.setTitle("Add new Category");
        int editInputPadding = 24; // Technically magic number, but it's from Google's design specs for Dialogs: https://www.google.com/design/spec/components/dialogs.html#dialogs-specs
        builder.setView(editInput, editInputPadding, editInputPadding, editInputPadding, editInputPadding);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editInputText = editInput.getText()+"";
                if(!editInputText.equals("")) {
                    populateSpinner(editInputText);
                    spinnerCategory.setSelection(spinnerCategory.getAdapter().getCount()-2); // Set the spinner to the second to last item. Which is the newly added category.
                    alertDialog.dismiss();
                }
                else {
                    Toast.makeText(getActivity(), "Category name cannot be blank", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
