package com.paulrlutz.vocabmanager.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulrlutz.vocabmanager.R;
import com.paulrlutz.vocabmanager.data.VocabWord;


public class VocabWordFragment extends Fragment {

    VocabWord vocabWord = null;

    TextView tvName = null;
    TextView tvCategory = null;
    TextView tvDefinition = null;
    TextView tvNotes = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vocab_word, container, false);

        tvName = (TextView) view.findViewById(R.id.tvName);
        tvCategory = (TextView) view.findViewById(R.id.tvCategory);
        tvDefinition = (TextView) view.findViewById(R.id.tvDefinition);
        tvNotes = (TextView) view.findViewById(R.id.tvNotes);

        updateViewWithVocabWord(vocabWord);

        return view;
    }

    /**
     * Sets the word to whatever VocabWord should be displayed.
     */
    public void setVocabWord(VocabWord word) {
        vocabWord = word;
        if(this.isVisible()) {
            updateViewWithVocabWord(word);
        }
    }

    /**
     * Fills the Views with the data in the given VocabWord.
     */
    public void updateViewWithVocabWord(VocabWord word) {
        if(tvName != null) {
            String name = word.getName();
            if(name != null) {
                tvName.setText(name);
            }
            else {
                tvName.setText("");
            }
        }

        if(tvCategory != null) {
            String category = word.getCategory();
            if(category != null) {
                tvCategory.setText(category);
            }
            else {
                tvCategory.setText("");
            }
        }

        if(tvDefinition != null) {
            String definition = word.getDefinition();
            if(definition != null) {
                tvDefinition.setText(definition);
            }
            else {
                tvDefinition.setText("");
            }
        }


        if(tvNotes != null) {
            String notes = word.getNotes();
            if(notes != null) {
                tvNotes.setText(notes);
            }
            else {
                tvNotes.setText("");
            }
        }


    }
}
