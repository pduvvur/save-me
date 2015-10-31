package com.pduvvur.saveme;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.pduvvur.saveme.guardian.Guardian;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LocationSMSService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private GoogleApiClient m_googleApiClient;
    private List<Guardian> m_guardiansList;

    public LocationSMSService() {}

    public LocationSMSService(Context context, List<Guardian> guardiansList)
    {
        m_googleApiClient =  new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        m_guardiansList = guardiansList;
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        Location lastLocation = LocationServices.FusedLocationApi.
                getLastLocation(m_googleApiClient);
        double longitude = lastLocation.getLongitude();
        double latitude = lastLocation.getLatitude();

        System.out.println("latitude = " + latitude);
        System.out.println("longitude = " + longitude);

        for (Guardian guardian : m_guardiansList) {
            System.out.println(guardian.getPhoneNumber());
            sendTextMessage(guardian.getPhoneNumber());
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        System.out.println("connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        System.out.println("Connection Failed");
    }

    private void sendTextMessage(String phoneNumber)
    {
        String message = "Save me test message"; // TODO Get current location and also the text message should be configurable through yaml.
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            m_googleApiClient.blockingConnect(2000, TimeUnit.MILLISECONDS);
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                10); // Process.THREAD_PRIORITY_BACKGROUND
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    @Override
    public void onDestroy()
    {
        Toast.makeText(this, "Location SMS service completed", Toast.LENGTH_SHORT).show();
    }


    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
