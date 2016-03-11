package com.paulrlutz.vocabmanager.contentproviders;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import com.paulrlutz.vocabmanager.data.VocabWordDBHelper;
import com.paulrlutz.vocabmanager.data.VocabWordDatabaseAccessObject;

/**
 * VocabWordSuggestionProvider is a ContentProvider that provides VocabWords based on a query.
 * The query should be a String, and the ContentProvider should search for VocabWords whose name contains the query.
 */
public class VocabWordSuggestionProvider extends ContentProvider {

    private static final String TAG = "VocabWordSuggestions";

    VocabWordDatabaseAccessObject vocabWordDAO = null;


    @Override
    public boolean onCreate() {
        Log.d(TAG, "Creating VocabWordSuggestionProvider");
        vocabWordDAO = VocabWordDatabaseAccessObject.getInstance(getContext());
        return true;
    }

    /**
     * Search the DB for VocabWords whose names contain the query.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Log.d(TAG, "selection: " + selection);
        Log.d(TAG, "selectionArgs: ");

        for(String selectionArg : selectionArgs) {
            Log.d(TAG, selectionArg);
        }


        Cursor c = null;
        String[] columns = new String[] {"_id", SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_INTENT_DATA }; // TODO Replace "_id" with something
        MatrixCursor matrixCursor= new MatrixCursor(columns);

        if (selectionArgs != null && selectionArgs.length > 0
                && selectionArgs[0].length() > 0) {

            c = vocabWordDAO.searchVocabWords(selection, selectionArgs);
            c.moveToFirst();

            Log.d(TAG, "cursor count is: " + c.getCount());
            if(c.getCount() < 1) {
                return null;
            }

            do {
                Integer rowID = c.getInt(c.getColumnIndex(VocabWordDBHelper.KEY_VOCAB_WORD_ID));
                String rowName = c.getString(c.getColumnIndex(VocabWordDBHelper.KEY_VOCAB_WORD_NAME));

                Log.d(TAG, "result found: " + rowName);

                matrixCursor.addRow(new Object[]{ rowID,rowName,rowID});
            } while(c.moveToNext());

        } else {
            return null;
        }
        return matrixCursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }


    /**
     * Don't try to actually edit the data with this Content Provider.
     * It's only purpose currently is Search.
     */

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
