package com.paulrlutz.vocabmanager.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * VocabWord is the object that the app revolves around.
 * Each VocabWord must include: ID, Name, Definition
 * Each VocabWord may contain Category, Example, Notes, CompareWith, Links, and Tags
 */
public class VocabWord implements Parcelable{

    Integer id;

    String name;
    String definition;
    String category;

    String notes;

    /**
     * Constructors
     */
    public VocabWord(String mName, String mDefinition) {
        this(-1, mName, mDefinition);
    }

    protected VocabWord() {

    }

    protected VocabWord(Integer mID, String mName, String mDefinition) {
        this.setID(mID);
        this.name = mName;
        this.definition = mDefinition;

        notes = "";

    }




    /**
     * Comparators decide how VocabWords should be sorted.
     *
     * NAME: Sort by name, alphabetically.
     * CATEGORY: Sort by category, alphabetically.
     * CATEGORY_NAME: Sort by category alphabetically, then name alphabetically.
     */
    public static class Comparators {
        public static Comparator<VocabWord> NAME = new Comparator<VocabWord>() {
            @Override
            public int compare(VocabWord vw1, VocabWord vw2) {
                return vw1.getName().compareTo(vw2.getName());
            }
        };
        public static Comparator<VocabWord> CATEGORY = new Comparator<VocabWord>() {
            @Override
            public int compare(VocabWord vw1, VocabWord vw2) {
                return vw1.getCategory().compareTo(vw2.getCategory());
            }
        };
        public static Comparator<VocabWord> CATEGORY_NAME = new Comparator<VocabWord>() {
            @Override
            public int compare(VocabWord vw1, VocabWord vw2) {
                int copmare1 = vw1.getCategory().compareTo(vw2.getCategory());
                if (copmare1 != 0) {
                    return copmare1;
                }
                else {
                    return vw1.getName().compareTo(vw2.getName());
                }
            }
        };
    }



    /**
     * Setters
     *
     * Nothing outside of the data package should be able to manually change the ID.
     */

    protected void setID(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    /**
     * Getters.
     *
     * If ID is null, then it should return -1.
     * If Name or Category are null, they should return null.
     * If anything else is null, just return an empty object.
     */

    public Integer getID() {
        if(id == null) {
            return -1;
        }
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDefinition() {
        if(definition == null) {
            return "";
        }
        return definition;
    }

    public String getNotes() {
        if(notes == null) {
            return "";
        }
        return notes;
    }


    /**
     * Parcelable junk
     */


    public VocabWord(Parcel in) {
        this.id = in.readInt();

        this.name = in.readString();
        this.definition = in.readString();
        this.category = in.readString();

        this.notes = in.readString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);

        dest.writeString(name);
        dest.writeString(definition);
        dest.writeString(category);

        dest.writeString(notes);
    }


    @Override
    public int describeContents() {
        return 0;
    }


    public static final Parcelable.Creator<VocabWord> CREATOR
            = new Parcelable.Creator<VocabWord>() {

        @Override
        public VocabWord createFromParcel(Parcel in) {
            return new VocabWord(in);
        }

        @Override
        public VocabWord[] newArray(int size) {
            return new VocabWord[size];
        }
    };

}