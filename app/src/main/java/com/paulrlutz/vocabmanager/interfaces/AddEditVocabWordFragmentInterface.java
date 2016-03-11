package com.paulrlutz.vocabmanager.interfaces;

import com.paulrlutz.vocabmanager.data.VocabWord;

import java.util.ArrayList;


public interface AddEditVocabWordFragmentInterface {
    /*
        Either adds or updates a VocabWord in whatever storage medium.
        If vocabWord's id == -1, then it's new and needs to be added to DB.
        If it's >0 then it already exists and needs to be updated.
    */
    public void addOrUpdateVocabWord(VocabWord vocabWord);

    // Gets the user's Categories.
    public ArrayList<String> getCategories();

    // Currently unused. Will be used when the Category DB is implemented.
    public void addCategory(String categoryName);
}
