package revolhope.splanes.com.mygrocery.helpers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import revolhope.splanes.com.mygrocery.helpers.database.model.Preferences;

public class AppDatabase extends SQLiteOpenHelper {

    private static final String dbName = "APP_DB";
    private static int dbVersion = 1;

    private static final String tablePreferences = "PREFERENCES";
    private static final String preferencesId = "PREFERENCES_ID";
    private static final String preferencesEmail = "PREFERENCES_EMAIL";
    private static final String preferencesPwd = "PREFERENCES_PWD";
    private static final String preferencesDefaultTarget = "PREFERENCES_TARGET";
    private static final String preferencesIv = "PREFERENCES_IV";
    private static final String preferencesTLength = "PREFERENCES_TLENGTH";
    private static final String[] columnsPreferences = new String[]
            { preferencesId, preferencesEmail, preferencesPwd, preferencesDefaultTarget,
              preferencesIv, preferencesTLength };
    private static final String createTablePreferences = "" +
            "CREATE TABLE " + tablePreferences + "(" +
            preferencesId + " VARCHAR(100) NOT NULL, " +
            preferencesEmail + " VARCHAR(100) NOT NULL," +
            preferencesPwd + " BLOB NOT NULL," +
            preferencesDefaultTarget + " VARCHAR(100) DEFAULT NULL," +
            preferencesIv + " BLOB NOT NULL," +
            preferencesTLength + " INTEGER NOT NULL)";

    public AppDatabase(@Nullable Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTablePreferences);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tablePreferences);
        dbVersion = newVersion;
        onCreate(db);
    }

    public void selectPreferences(AppDatabaseCallback.Select selectCallback) {
        new SelectPreferencesAsync(getReadableDatabase(), selectCallback).execute();
    }

    public void insertPreferences(Preferences pref, AppDatabaseCallback.Modify modifyCallback) {
        new InsertPreferencesAsync(getWritableDatabase(), modifyCallback).execute(pref);
    }

    public void removePreferences() {
        new RemovePreferencesAsync(getWritableDatabase()).execute();
    }

    private static class SelectPreferencesAsync extends AsyncTask<Void, Void, Void>{

        private SQLiteDatabase db;
        private AppDatabaseCallback.Select callback;

        private SelectPreferencesAsync(SQLiteDatabase db, AppDatabaseCallback.Select callback) {
            this.db = db;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try (Cursor cursor = db.query(tablePreferences, columnsPreferences, null,
                    null, null, null, null)) {
                if (cursor.getCount() == 0 || !cursor.moveToFirst()) {
                    callback.selected(null);
                    return null;
                }

                String id = cursor.getString(cursor.getColumnIndex(preferencesId));
                String email = cursor.getString(cursor.getColumnIndex(preferencesEmail));
                byte[] pwd = cursor.getBlob(cursor.getColumnIndex(preferencesPwd));
                String defaultTarget = cursor.getString(cursor
                        .getColumnIndex(preferencesDefaultTarget));
                byte[] iv = cursor.getBlob(cursor.getColumnIndex(preferencesIv));
                int tlen = cursor.getInt(cursor.getColumnIndex(preferencesTLength));
                Cryptography cryptography = Cryptography.getInstance();
                if (cryptography != null) {
                    String plain = cryptography.decrypt(new Cryptography.CryptographyObject(pwd,
                            iv, tlen));
                    callback.selected(new Preferences(id, email, plain, defaultTarget));
                    db.close();
                    return null;
                }
                else {
                    callback.selected(null);
                    db.close();
                    return null;
                }
            }
            finally {
                if (db.isOpen()) db.close();
            }
        }
    }

    private static class InsertPreferencesAsync extends AsyncTask<Preferences, Void, Void>{

        private SQLiteDatabase db;
        private AppDatabaseCallback.Modify callback;

        private InsertPreferencesAsync(SQLiteDatabase db, AppDatabaseCallback.Modify callback) {
            this.db = db;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Preferences... preferences) {

            Preferences pref = preferences[0];
            Cryptography cryptography = Cryptography.getInstance();
            if (cryptography == null) return null;
            Cryptography.CryptographyObject cryptoObject =
                        cryptography.encrypt(pref.getPwd().getBytes());
            if (cryptoObject == null) return null;

            ContentValues values = new ContentValues();
            values.put(preferencesId, pref.getId());
            values.put(preferencesEmail, pref.getEmail());
            values.put(preferencesPwd, cryptoObject.data);
            values.put(preferencesDefaultTarget, pref.getTarget());
            values.put(preferencesIv, cryptoObject.iv);
            values.put(preferencesTLength, cryptoObject.tLength);
            long id = db.insert(tablePreferences, null, values);
            callback.modified(id == -1 ? null : 1);
            return null;
        }
    }

    private static class RemovePreferencesAsync extends AsyncTask<Void, Void, Void> {

        private SQLiteDatabase db;

        private RemovePreferencesAsync(SQLiteDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            db.delete(tablePreferences, null, null);
            db.close();
            return null;
        }
    }
}
