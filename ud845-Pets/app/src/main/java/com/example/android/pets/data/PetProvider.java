package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;

import com.example.android.pets.data.PetContract.PetEntry;

import static com.example.android.pets.data.PetContract.PetEntry.isValidGender;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private EditText mBreedDefault;

    /**
     * Initialize the provider and the database helper object.
     */

         /**
                 *every uriMatcher return code represents the uri code
                 * i.e(100 for the uri represent the whole table  And 101 for the uri represent row in the table )
                 * let do that   public static final int PETS=100  and PETS_ID=101
                 * private static final UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
                 * before sUriMatcher and switch case that give us control to specific implementation
                 * we will register every uri pattern by using Method called [addURI]
                 * [addURI]
                 * sUriMatcher.addURI(contentAuthority,path,return code)
                 *
                 * */

    /*Database helper object*/
    private PetDbHelper mDbhelper;
    /*return code of uri represents specific uri type if  it for all  table of for specific row */
    private static final int PETS=100;
    private static final int PETS_ID=101;
//Make the UriMatcher
    private static final UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    static {//Expected uri patterns
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS,PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS+"/#",PETS_ID);
    }

    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
           mDbhelper=new PetDbHelper(getContext());
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
      //get connection to the database
        SQLiteDatabase database=mDbhelper.getReadableDatabase();
        Cursor cursor=null;

        int match=sUriMatcher.match(uri);
        switch (match)  {

            case PETS:

                cursor=database.query(PetEntry.TABLE_NAME,projection,null,null,null,null,sortOrder);
                break;
            case PETS_ID:
                selection=PetEntry._ID+"=?";
                selectionArgs =new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor=database.query(PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);

                break;
            default:
                throw new IllegalArgumentException("can't query Unknown URI"+uri);

        }
        //if there is change in the database table it will reload the change
    cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }


    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
       final int match=sUriMatcher.match(uri);
        switch (match){
            case PETS:
                 return InsertPet(uri,contentValues);
            default:
                throw new IllegalArgumentException("pet can't inserted with this URI"+uri);
        }



    }
    private Uri InsertPet(Uri uri,ContentValues values){
       SQLiteDatabase database=mDbhelper.getWritableDatabase();

        String name=values.getAsString(PetEntry.COLUMN_PET_NAME);
        if(name.isEmpty() ) {
            throw new IllegalArgumentException("the name can't be empty");
        }


        Integer gender=values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
       if(!isValidGender(gender) || gender == null){

           throw new IllegalArgumentException("the Gender is not valid");
       }

        Integer weight=values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if(weight != null && weight<=0){

            throw new IllegalArgumentException("the weight can't be negative or zero");
        }


        long id=database.insert(PetEntry.TABLE_NAME,null,values);
        if(id == -1){
            Log.e(LOG_TAG,"Failed to insert row for "+uri);
            return null;
        }
        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

       return ContentUris.withAppendedId(uri,id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     *
     */
    private int updatePet(Uri uri,ContentValues values,String selection,String[] selectionArgs){
       //Get connection to the database
        SQLiteDatabase database=mDbhelper.getWritableDatabase();
        //add sanity check
        if(values.containsKey(PetEntry.COLUMN_PET_NAME)){
            String name=values.getAsString(PetEntry.COLUMN_PET_NAME);
            if(name.isEmpty()){
                throw new IllegalArgumentException("The name should not be empty");
            }
        }
        if(values.containsKey(PetEntry.COLUMN_PET_BREED)){

            String breed=values.getAsString(PetEntry.COLUMN_PET_BREED);
        }

        if(values.containsKey(PetEntry.COLUMN_PET_WEIGHT)){
            Integer weight=values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if(weight !=null &&weight<=0){

                throw new IllegalArgumentException("The weight should not be negative");
            }
        }
        if(values.containsKey(PetEntry.COLUMN_PET_GENDER)){

            Integer gender=values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if(!PetEntry.isValidGender(gender)||gender ==null){

                throw new IllegalArgumentException("The Gender is not be valid");
            }
        }

        //check if there is not change in the values no need to do database operation and return 0 row effect
        if(values.size()==0){
            return 0;
        }


        return database.update(PetEntry.TABLE_NAME,values,selection,selectionArgs);
    }
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match=sUriMatcher.match(uri);
        switch (match){
            case PETS:
                int Row_Updated=updatePet( uri, contentValues,selection,selectionArgs);
                if(Row_Updated !=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return Row_Updated;

            case PETS_ID:
                selection=PetEntry._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};

                 Row_Updated=updatePet( uri, contentValues,selection,selectionArgs);
                if(Row_Updated !=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return Row_Updated;

            default:
                throw new IllegalArgumentException("Update not supported for this URI: "+uri);
        }

    }
/*private int DeletePet(Uri uri,String selection,String[] selectionArgs){
    SQLiteDatabase database=mDbhelper.getWritableDatabase();
    return database.delete(PetEntry.TABLE_NAME,selection,selectionArgs);
}*/
    /**
     * Delete the data at the given selection and selection arguments.
     */
    /*@Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
       final int match=sUriMatcher.match(uri);
        switch (match){
            case PETS:
                int Row_Deleted=DeletePet(uri,selection,selectionArgs);
                if(Row_Deleted != 0){

                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return Row_Deleted;
            case PETS_ID:
                selection=PetEntry._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                 Row_Deleted=DeletePet(uri,selection,selectionArgs);
                if(Row_Deleted != 0){

                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return Row_Deleted;

            default:
                throw new IllegalArgumentException("Delete not supported for Uri: "+uri);
        }

    }
    */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbhelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PETS_ID:
                // Delete a single row given by the ID in the URI
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }


    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PETS_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
