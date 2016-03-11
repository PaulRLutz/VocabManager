package com.paulrlutz.vocabmanager.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * The VocabWordDatabaseAccessObject separates the rest of the app from whatever object accesses the DB.
 *
 * Currently just uses an implementation of SQLiteOpenHelper.
 */
public class VocabWordDatabaseAccessObject {

    private static VocabWordDatabaseAccessObject instance = null;


    private static final String TAG = "Vocab Word DAO";

    VocabWordDBHelper dbHelper = null;

    protected VocabWordDatabaseAccessObject(Context context) {
        dbHelper = VocabWordDBHelper.getInstance(context);
    }

    /**
     * Gets or creates an instance of VocabWordDatabaseAccessObject.
     */
    public static synchronized VocabWordDatabaseAccessObject getInstance(Context context) {
        if(instance == null) {
            instance = new VocabWordDatabaseAccessObject(context);
        }
        return instance;
    }


    /**
     * The possible types of sort that getVocabWordList can return.
     * DEFAULT: No sort applied to vocabWordList.
     * ALPHABETICAL_NAME: Sort by name, alphabetically.
     * ALPHABETICAL_CATEGORY: Sort by category, alphabetically.
     * ALPHABETICAL_CATEGORY_NAME: Sort by category alphabetically, then name alphabetically.
     */
    public enum SortType {
        DEFAULT, ALPHABETICAL_NAME, ALPHABETICAL_CATEGORY, ALPHABETICAL_CATEGORY_NAME;

        /**
         * Converts an integer to a SortType.
         */
        public static SortType fromInteger(int x) {
            switch (x) {
                case 0:
                    return DEFAULT;
                case 1:
                    return ALPHABETICAL_NAME;
                case 2:
                    return ALPHABETICAL_CATEGORY;
                case 3:
                    return ALPHABETICAL_CATEGORY_NAME;
                default:
                    return DEFAULT;
            }
        }

        /**
         * Converts a SortType. Is this necessary? Probably not.
         */
        public static int toInteger(SortType x) {
            switch (x) {
                case DEFAULT:
                    return 0;
                case ALPHABETICAL_NAME:
                    return 1;
                case ALPHABETICAL_CATEGORY:
                    return 2;
                case ALPHABETICAL_CATEGORY_NAME:
                    return 3;
                default:
                    return 0;
            }
        }
    }

    /**
     * Returns the full list of VocabWords.
     */
    public ArrayList<VocabWord> getVocabWordList(SortType sortType) {

        List<VocabWord> vocabWordList = dbHelper.getAllVocabWords();

        List<VocabWord> returnVocabWordList = new ArrayList<VocabWord>(vocabWordList);

        if(returnVocabWordList.size()>1) {
            switch (sortType) {
                case DEFAULT:
                    break;
                case ALPHABETICAL_NAME:
                    Collections.sort(returnVocabWordList, VocabWord.Comparators.NAME);
                    break;
                case ALPHABETICAL_CATEGORY:
                    Collections.sort(returnVocabWordList, VocabWord.Comparators.CATEGORY);
                    break;
                case ALPHABETICAL_CATEGORY_NAME:
                    Collections.sort(returnVocabWordList, VocabWord.Comparators.CATEGORY_NAME);
                    break;
                default:
                    break;
            }
        }
        else {
            Log.d(TAG, "List is too small or too empty to sort. Size: " + returnVocabWordList.size());
        }

        return new ArrayList<VocabWord>(returnVocabWordList);
    }


    /**
     * Returns the VocabWord with the given ID.
     * Returns null if there is no VocabWord with the given ID.
     */
    public VocabWord getVocabWordByID(Integer id) {
        return dbHelper.getVocabWordByID(id);
    }

    /**
     * Deletes the VocabWord with the given ID.
     * Returns true if successful.
     */
    public void deleteVocabWordWithID(Integer id) {
        dbHelper.deleteVocabWordWithID(id);
    }

    /**
     * Handles editing or updating a given VocabWord.
     *
     * If ID == -1, then it's a new VocabWord.
     * Otherwise, ID = the index of the VocabWord in the DB.
     */
    public void addOrUpdateVocabWord(VocabWord word) {
        if(word.getID().equals(-1)) {
            Log.d(TAG, "Adding new VocabWord w/ ID: " + word.getID());
            addVocabWord(word);

        }
        else {
            Log.d(TAG, "Updating VocabWord w/ ID: " + word.getID());
            updateVocabWord(word);
        }
    }

    /**
     * Replaces the VocabWord with same ID as the given VocabWord, with the given VocabWord.
     */
    public void updateVocabWord(VocabWord word) {
        dbHelper.updateVocabWord(word);
    }

    /**
     * Adds a new VocabWord.
     */
    public void addVocabWord(VocabWord word) {
        dbHelper.addVocabWord(word);
    }


    /**
     * Returns an ArrayList of the available Categories.
     */
    public ArrayList<String> getCategoryNames() {
        ArrayList<String> categoryNames = dbHelper.getAllCategories();
        return categoryNames;
    }

    /**
     * Adds the given Category to categoryNames.
     * If the given Category is already present, don't add it and return false.
     *
     * Currently unused.
     */
    public void addCategory(String tempCategory) {
        ArrayList<String> categoryNames = dbHelper.getAllCategories();
        if(categoryNames.contains(tempCategory)) {
            return;
        }
        categoryNames.add(tempCategory);
    }


    public Cursor searchVocabWords(String selection, String[] selectionArgs) {
        return dbHelper.searchVocabWordNames(selectionArgs[0]);
    }
}
