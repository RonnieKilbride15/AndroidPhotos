package com.example.androidphotos82;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import DialogFragments.AddEditDialogFragment;
import objects.Album;

import static com.example.androidphotos82.MainActivity.*;


public class SearchActivity extends AppCompatActivity {

    ArrayList<Album> albums;
    EditText tagTypeField;
    EditText tagValueField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Search");
        Button b = (Button) findViewById(R.id.findResultsButton);
        b.setText("Search");
        setSupportActionBar(toolbar);
        albums = new ArrayList<Album>();
        try{
            File file = new File(getFilesDir(), "data.ser");
            FileInputStream fis = openFileInput("data.ser");
            ObjectInputStream in = new ObjectInputStream(fis);
            albums = (ArrayList<Album>) in.readObject();
        } catch(Exception e){}

        tagTypeField = (EditText)findViewById(R.id.searchTagTypeTextField);
        tagValueField = (EditText)findViewById(R.id.searchTagValueTextField);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void cancel(View view){
        setResult(RESULT_CANCELED);
        finish();
    }

    public void search(View view){

        Bundle bundle = new Bundle();
        int code;
        ArrayList<Integer> searchResults = new ArrayList<Integer>();
        String tagType = tagTypeField.getText().toString().trim().toLowerCase();
        String tagValue = tagValueField.getText().toString().trim().toLowerCase();
        if(tagType.equals("location") && !tagValue.isEmpty()){
            for(int i = 0; i < albums.size(); i++){
                for(int j = 0; j < albums.get(i).getPhotos().size(); j++){
                    for(int k = 0; k < albums.get(i).getPhotos().get(j).getTags().size(); k++){
                        if(albums.get(i).getPhotos().get(j).getTags().get(k).tagType.equals("location")
                                && albums.get(i).getPhotos().get(j).getTags().get(k).tagValue.contains(tagValue)){
                            searchResults.add(albums.get(i).getPhotos().get(j).getPhotoID());
                            break;
                        }
                    }
                }
            }
        }else if(tagType.equals("person") && !tagValue.isEmpty()){
            for(int i = 0; i < albums.size(); i++){
                for(int j = 0; j < albums.get(i).getPhotos().size(); j++){
                    for(int k = 0; k < albums.get(i).getPhotos().get(j).getTags().size(); k++){
                        if(albums.get(i).getPhotos().get(j).getTags().get(k).tagType.equals("person")
                                && albums.get(i).getPhotos().get(j).getTags().get(k).tagValue.contains(tagValue)){
                            searchResults.add(albums.get(i).getPhotos().get(j).getPhotoID());
                            break;
                        }
                    }
                }
            }
        }else{
            if(tagType.isEmpty() || tagValue.isEmpty()){
                bundle.putString(AddEditDialogFragment.MESSAGE_KEY,
                        "Fields Cannot Be Empty");
                DialogFragment newFragment = new AddEditDialogFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "badfields");
                return;
            }else if(!tagType.equals("person") && !tagType.equals("location")){
                bundle.putString(AddEditDialogFragment.MESSAGE_KEY,
                        "Tag Type Must Be \"Location\" Or \"Person\"");
                DialogFragment newFragment = new AddEditDialogFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "badfields");
                return;
            }
        }

        bundle.putIntegerArrayList("photos", searchResults);
        bundle.putString("filter", tagType);
        Intent intent = new Intent(getApplicationContext(), SearchResults.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, SEARCH_CODE);
    }


}
