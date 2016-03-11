package com.paulrlutz.vocabmanager.interfaces;


import com.paulrlutz.vocabmanager.data.VocabWord;

import java.util.ArrayList;

public interface MainListFragmentInterface {

    /*
        Returns the full list of VocabWords created by the user.
     */
    public ArrayList<VocabWord> getVocabWordArrayList();

    /*
        Displays the information for given word.
        The object implementing this Infterface can handle it in multiple ways, ie:
            a. Displaying the information in a Fragment.
            b. Displaying the information in an Activity.
            c. Displaying the information in a Dialog.
     */
    public void showVocabWordInformation(VocabWord word);

    /*
        Lets the implementing object know that the user wants to create a new VocabWord.
     */
    public void createNewVocabWord();
    /*
        Lets the implementing object know that the user wants to edit an existing VocabWord.
     */
    public void updateVocabWord(VocabWord vocabWord);
    /*
        Lets the implementing object know that the user wants to delete a VocabWord.
     */
    public void deleteVocabWord(VocabWord vocabWord);
}
