package com.example.android.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by tom on 8/25/17.
 */

public final class PetContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private PetContract(){}
    /**
     *making formula of the URI
     * URi => scheme,content Authority,type of data
     *      content://com.example.android.pets/pets
     */

    public static final String CONTENT_AUTHORITY="com.example.android.pets";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_PETS="pets";



    /*inner class to define the table constants */
    public static class PetEntry implements BaseColumns{
        public static final String TABLE_NAME="pets";

        public static final String _ID=BaseColumns._ID;
        public static final String COLUMN_PET_NAME="name";
        public static final String COLUMN_PET_BREED="breed";
        public static final String COLUMN_PET_GENDER="gender";
        public static final String COLUMN_PET_WEIGHT="pets";
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        public static String getColumnPetWeight() {
            return COLUMN_PET_WEIGHT;
        }

        /*possible values for the pets table*/
        public static final int GENDER_UNKNOWN=0;
        public static final int GENDER_MALE=1;
        public static final int GENDER_FEMALE=2;
        //content URI for this table
        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PETS);

        /**
         *check validation of gender
         *
         * */
        public static boolean isValidGender(int gender){
            if(gender ==GENDER_FEMALE||gender==GENDER_MALE||gender==GENDER_UNKNOWN){
                return true;
            }
            return false;

        }


    }




}
