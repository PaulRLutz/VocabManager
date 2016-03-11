package com.paulrlutz.vocabmanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// TODO Add some clarifying comments in here.

public class VocabWordDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "VocabWordDBHelper";

    // Database Info
    private static final String DATABASE_NAME = "vocabWordDatabase";
    private static final int DATABASE_VERSION = 3;

    // Table Names
    private static final String TABLE_VOCAB_WORDS = "vocabwords";

    // VocabWord Table Columns
    public static final String KEY_VOCAB_WORD_ID = "id";
    public static final String KEY_VOCAB_WORD_NAME = "name";
    public static final String KEY_VOCAB_WORD_DEFINITION = "definition";
    public static final String KEY_VOCAB_WORD_CATEGORY = "category";
    public static final String KEY_VOCAB_WORD_NOTES = "notes";

    private static VocabWordDBHelper sInstance;

    public static synchronized VocabWordDBHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new VocabWordDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private VocabWordDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_VOCAB_WORD_TABLE = "CREATE TABLE " + TABLE_VOCAB_WORDS +
                "(" +
                KEY_VOCAB_WORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_VOCAB_WORD_NAME + " TEXT," +
                KEY_VOCAB_WORD_DEFINITION + " TEXT," +
                KEY_VOCAB_WORD_CATEGORY + " TEXT," +
                KEY_VOCAB_WORD_NOTES + " TEXT" +
                ")";

        try {
            db.execSQL(CREATE_VOCAB_WORD_TABLE);
        } catch (SQLException e) {
            Log.d(TAG, "Problem creating table.");
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOCAB_WORDS);
            onCreate(db);
        }
    }

    // Insert a VocabWord into the database
    public void addVocabWord(VocabWord word) {

        String name = word.getName();
        String definition = word.getDefinition() ;
        String category = word.getCategory();
        String notes = word.getNotes();

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_VOCAB_WORD_NAME, name);
            values.put(KEY_VOCAB_WORD_DEFINITION, definition);
            values.put(KEY_VOCAB_WORD_CATEGORY, category);
            values.put(KEY_VOCAB_WORD_NOTES, notes);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            long newWordID = db.insertOrThrow(TABLE_VOCAB_WORDS, null, values);

            Log.d(TAG, "New VocabWord created with ID: " + newWordID);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add vocab word to database");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void updateVocabWord(VocabWord word) {

        Integer id = word.getID();
        String name = word.getName();
        String definition = word.getDefinition() ;
        String category = word.getCategory();
        String notes = word.getNotes();

        ContentValues values = new ContentValues();
        values.put(KEY_VOCAB_WORD_NAME, name);
        values.put(KEY_VOCAB_WORD_DEFINITION, definition);
        values.put(KEY_VOCAB_WORD_CATEGORY, category);
        values.put(KEY_VOCAB_WORD_NOTES, notes);

        SQLiteDatabase db = getWritableDatabase();

        db.update(TABLE_VOCAB_WORDS, values, KEY_VOCAB_WORD_ID + "=" + id, null);
    }


    public List<VocabWord> getAllVocabWords() {
        List<VocabWord> vocabWords = new ArrayList<>();

        // SELECT * FROM VOCAB_WORDS
        String VOCAB_WORDS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_VOCAB_WORDS);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(VOCAB_WORDS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    VocabWord word = new VocabWord();

                    word.setID(cursor.getInt(cursor.getColumnIndex(KEY_VOCAB_WORD_ID)));
                    word.setName(cursor.getString(cursor.getColumnIndex(KEY_VOCAB_WORD_NAME)));
                    word.setDefinition(cursor.getString(cursor.getColumnIndex(KEY_VOCAB_WORD_DEFINITION)));
                    word.setCategory(cursor.getString(cursor.getColumnIndex(KEY_VOCAB_WORD_CATEGORY)));
                    word.setNotes(cursor.getString(cursor.getColumnIndex(KEY_VOCAB_WORD_NOTES)));

                    vocabWords.add(word);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get vocab words from database");
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return vocabWords;
    }

    public VocabWord getVocabWordByID(int id) {
        VocabWord word = new VocabWord();

        // SELECT * FROM VOCAB_WORDS WHERE id = 'id'
        String VOCAB_WORD_SELECT_QUERY =
                String.format("SELECT * FROM %s WHERE %s = %s",
                        TABLE_VOCAB_WORDS,
                        KEY_VOCAB_WORD_ID,
                        id);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(VOCAB_WORD_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {

                word.setID(cursor.getInt(cursor.getColumnIndex(KEY_VOCAB_WORD_ID)));
                word.setName(cursor.getString(cursor.getColumnIndex(KEY_VOCAB_WORD_NAME)));
                word.setDefinition(cursor.getString(cursor.getColumnIndex(KEY_VOCAB_WORD_DEFINITION)));
                word.setCategory(cursor.getString(cursor.getColumnIndex(KEY_VOCAB_WORD_CATEGORY)));
                word.setNotes(cursor.getString(cursor.getColumnIndex(KEY_VOCAB_WORD_NOTES)));

            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get a vocab word from database");
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return word;
    }

    public void deleteVocabWordWithID(int id) {
        Log.d(TAG, "Deleting VocabWord w/ ID: " + id);
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = KEY_VOCAB_WORD_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        db.delete(TABLE_VOCAB_WORDS, whereClause, whereArgs);
    }

    // TODO This is super lame. Technically no Category should exist without a VocabWord, but these should still be in another table.
    public ArrayList<String> getAllCategories() {
        ArrayList<String> categoriesList = new ArrayList<String>();

        String UNIQUE_CATEGORY_QUERY =
                String.format("SELECT DISTINCT %s FROM %s",
                        KEY_VOCAB_WORD_CATEGORY,
                        TABLE_VOCAB_WORDS);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(UNIQUE_CATEGORY_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    categoriesList.add(cursor.getString(cursor.getColumnIndex(KEY_VOCAB_WORD_CATEGORY)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get categories from database");
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        if(categoriesList.indexOf("General")<0) { // TODO Replace this with a String resource.
            categoriesList.add("General");
        }

        Log.d(TAG, "Found " + categoriesList.size() + " categories:");

        for(String category : categoriesList) {
            Log.d(TAG, category);
        }

        return categoriesList;
    }

    /**
     * Searches the DB for a VocabWord
     */
    public Cursor searchVocabWordNames(String searchText) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_VOCAB_WORDS, new String[] {KEY_VOCAB_WORD_ID, KEY_VOCAB_WORD_NAME},
                KEY_VOCAB_WORD_NAME + " LIKE ?", new String[] {"%" + searchText + "%"},
                null, null, null);

        return cursor;
    }
}
