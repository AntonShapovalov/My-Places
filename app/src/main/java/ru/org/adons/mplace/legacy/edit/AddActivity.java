package ru.org.adons.mplace.legacy.edit;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import ru.org.adons.mplace.legacy.MConstants;
import ru.org.adons.mplace.legacy.Place;
import ru.org.adons.mplace.legacy.db.DBUpdateService;

public class AddActivity extends EditActivityBase implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient apiClient;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        apiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (apiClient.isConnected()) {
            apiClient.disconnect();
        }
    }

    /**
     * Google API Client: callbacks
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(MConstants.LOG_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(MConstants.LOG_TAG, "Connection suspended");
        apiClient.connect();
    }

    /**
     * Handle click Action Bar Button 'Done'
     */
    @Override
    public void done(MenuItem item) {
        final Place newPlace = getNewPlace();
        final Intent serviceIntent = new Intent(this, DBUpdateService.class)
                .setAction(MConstants.ACTION_ADD_PLACE)
                .putExtra(MConstants.EXTRA_PLACE, newPlace);
        if (location != null) {
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();
            Log.d(MConstants.LOG_TAG, "LATITUDE = " + latitude + "; LONGITUDE = " + longitude);
            serviceIntent.putExtra(MConstants.EXTRA_LATITUDE, latitude);
            serviceIntent.putExtra(MConstants.EXTRA_LONGITUDE, longitude);
        }
        startService(serviceIntent);
        setResult(RESULT_OK);
        finish();
    }

}
