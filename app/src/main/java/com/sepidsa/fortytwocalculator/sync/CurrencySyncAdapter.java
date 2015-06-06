package com.sepidsa.fortytwocalculator.sync;

/**
 * Created by Farshid on 6/6/2015.
 */
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.sepidsa.fortytwocalculator.R;
import com.sepidsa.fortytwocalculator.data.CurrencyContract;
import java.util.Vector;
import com.jaunt.Elements;
import com.jaunt.JauntException;
import com.jaunt.UserAgent;

public class CurrencySyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = CurrencySyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 30;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public CurrencySyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        try {

            UserAgent userAgent = new UserAgent();                       //create new userAgent (headless browser).
            userAgent.visit("http://www.eghtesadi.org/");             //visit a url

            getCurrencyDataFromHTML(userAgent);

        }
        catch(JauntException e){         //if an HTTP/connection error occurs, handle JauntException.
            System.err.println(e);
            e.printStackTrace();

        }
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getCurrencyDataFromHTML(UserAgent userAgent) {

        String[] currencies = new String[] { "dollar", "eur" , "gbp", "try", "aed", "cad", "cny", "dkk", "hkd" , "myr", "nok", "pkr", "rub", "sar"};
        Vector<ContentValues> cVVector = new Vector<ContentValues>(currencies.length);
        int index = 1;
        for(String currency:currencies) {
            String value;
            ContentValues currencyValues = new ContentValues();
            Elements elements = userAgent.doc.findEvery("<tr id=\"f-price_" + currency + "\">");
            elements = elements.findEvery("<td class=\"nf\">");
            value = elements.innerText();
            currencyValues.put(CurrencyContract.CurrencyEntry.COLUMN_KEY, currency);
            currencyValues.put(CurrencyContract.CurrencyEntry.COLUMN_VALUE, value);
            currencyValues.put(CurrencyContract.CurrencyEntry.COLUMN_TYPE, 1);
            currencyValues.put(CurrencyContract.CurrencyEntry.COLUMN_SELECTED, 1);
            currencyValues.put(CurrencyContract.CurrencyEntry.COLUMN_PRIORITY, index++);
            cVVector.add(currencyValues);
            Log.d(LOG_TAG, "Syncing... " + currency + " + " + value + " Inserted");
        }

        // add to database
        if ( cVVector.size() > 0 ) {
            getContext().getContentResolver().delete(
                    CurrencyContract.CurrencyEntry.CONTENT_URI,
                    null,
                    null
            );
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
//            for(ContentValues contentValues : cvArray) {
//                getContext().getContentResolver().insert(CurrencyContract.CurrencyEntry.CONTENT_URI,contentValues);
//            }
            getContext().getContentResolver().bulkInsert(CurrencyContract.CurrencyEntry.CONTENT_URI, cvArray);

            // delete old data so we don't build up an endless history

        }

        Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");

    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority_currency);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority_currency), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        CurrencySyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority_currency), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}