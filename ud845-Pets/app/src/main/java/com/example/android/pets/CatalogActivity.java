package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

   private PetDbHelper mDbHelper;
    /*unique id for pet_loader*/
    private final int PET_LOADER=0;
    /*object of PetAdapter*/
    PetCursorAdapter mPetCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
                ListView petListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
                View emptyView = findViewById(R.id.empty_view);
                petListView.setEmptyView(emptyView);
        /**
         * initialize the cursorLoader
         * */
        /**
         *@1 param  id for the loader it's unique for each loader
         * @2 param  args it's not required if the loader is created
         * @3 param  callback function to create and change the status of the loader
         * callbacks like : onCreateLoader() ,onFinishLoader(), onResetLoader()
         * This is called when a new Loader needs to be created.
         */
        //setup adapter to create a list item for each row
        //this is not data yet until loader finish fetching data so pass null for cursor
        mPetCursorAdapter=new PetCursorAdapter(this,null);
        petListView.setAdapter(mPetCursorAdapter);

        //kick off the loader
        getLoaderManager().initLoader(PET_LOADER,null,this);

/**
 * make intent to the editor activity when pressing item form listitem  and go to update */
 petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
     @Override
     public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent=new Intent(CatalogActivity.this,EditorActivity.class);
         Uri currentPetUri= ContentUris.withAppendedId( PetEntry.CONTENT_URI,id);
         //set uri in the data field of the intent
         intent.setData(currentPetUri);
         startActivity(intent);
     }
 });


    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }
private void InsertPet(){
    ContentValues values=new ContentValues();
    values.put(PetEntry.COLUMN_PET_NAME,"Toto");
    values.put(PetEntry.COLUMN_PET_BREED,"Terrier");
    values.put(PetEntry.COLUMN_PET_WEIGHT,7);
    values.put(PetEntry.COLUMN_PET_GENDER,PetEntry.GENDER_MALE);
    //insert the row in the DB
    Uri uri_status =   getContentResolver().insert(PetEntry.CONTENT_URI,values);

}
private void delete(){
    getContentResolver().delete(PetEntry.CONTENT_URI,null,null);
}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                InsertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                delete();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        String[] projection={
                PetEntry._ID,  // id is necessary for cursorLoader
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
        };
        return new CursorLoader(this,       //parent activity
                PetEntry.CONTENT_URI,       //provider content uri to query
                projection,                 //columns to include in the request cursor
                null,                       //No select clause
                null,                       //No select args
                null                        //default order
        );

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //update {@link PetCursorAdapter} with new cursor contain updated pet
        mPetCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //callback when the data need to be deleted
        mPetCursorAdapter.swapCursor(null);

    }
}
