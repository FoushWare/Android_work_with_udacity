package com.example.android.pets.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.pets.data.PetContract.PetEntry;
/**
 * Created by tom on 8/25/17.
 */

public class PetDbHelper extends SQLiteOpenHelper {
    /*Making constants for database version and database name if i change the schema of the database i should increment the version*/
    public static final String DATABASE_NAME="shelter.db";
    public static final int DATABASE_VERSION=1;

    /*start variable section of create and delete sql commands*/
        private static final String SQl_CREATE_PET_ENTRY=
                "CREATE TABLE "+PetEntry.TABLE_NAME+"("+
                 PetEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                 PetEntry.COLUMN_PET_BREED+" TEXT,"+
                 PetEntry.COLUMN_PET_GENDER+" INTEGER NOT NULL DEFAULT 0,"+
                 PetEntry.COLUMN_PET_NAME+" TEXT NOT NULL,"+
                 PetEntry.COLUMN_PET_WEIGHT+" INTEGER NOT NULL"+")";
        private static final String SQL_DELETE_PET_ENTRIES=
                "DROP IF EXISTS "+PetEntry.TABLE_NAME;

    /*End variable section of create and delete sql commands*/


    public  PetDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate (SQLiteDatabase db){
        db.execSQL(SQl_CREATE_PET_ENTRY);

    }
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_PET_ENTRIES);
        onCreate(db);

    }

}
