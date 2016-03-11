package com.paulrlutz.vocabmanager.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.paulrlutz.vocabmanager.R;

import java.util.ArrayList;

public class VocabWordArrayAdapter extends ArrayAdapter<VocabWord> {

    public VocabWordArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public VocabWordArrayAdapter(Context context, int resource, ArrayList<VocabWord> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row_layout_vocab_word, null);
        }

        VocabWord vocabWord = getItem(position);

        if (vocabWord != null) {
            TextView tvVocabWordName = (TextView) v.findViewById(R.id.tvVocabWordName);
            TextView tvVocabWordCategory = (TextView) v.findViewById(R.id.tvVocabWordCategory);

            if (tvVocabWordName != null) {
                tvVocabWordName.setText(vocabWord.getName());
            }
            if (tvVocabWordCategory != null) {
                tvVocabWordCategory.setText(vocabWord.getCategory());
            }

        }

        return v;
    }

}