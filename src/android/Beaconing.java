package com.jtech.Beaconing;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.AsyncTask;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.Intent;

import org.altbeacon.beacon.*;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.*;
// import java.util.Arrays.ArrayList;
// import java.util.HashMap;
// import java.util.Map;

public class Beaconing extends CordovaPlugin implements BeaconConsumer {

    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;
    private CallbackContext callbackContext;
    private JSONObject data = new JSONObject();
    private CordovaInterface cordova;

    private Region theRegion;
    // Dictionary (HashMap) of CallbackContexts
    private Map < String, CallbackContext > cbContexts = new HashMap < String, CallbackContext > ();

    public Beaconing() {

    }

    // at the initialize function, we can configure the tools we want to use later, like the sensors
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        // setup callback names for delegates
        // create a null callback and add it to each value
        CallbackContext nullContext = null;
        // add key values
        this.cbContexts.put("rangeBeaconId", nullContext);
        this.cbContexts.put("monitorBeaconId", nullContext);
        this.cbContexts.put("enteredRegionId", nullContext);
        this.cbContexts.put("leftRegionId", nullContext);


        beaconManager = BeaconManager.getInstanceForApplication(cordova.getActivity());

        // setting the IBeacon Layout, since the AltBeacon library doesn't have it by default
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        beaconManager.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cordova.getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(cordova.getActivity());
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        cordova.getActivity().requestPermissions(new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        }, 1);
                    }
                });
                builder.show();
            }
        }
    }

    // this is the main part of the plugin, we have to handle all of the actions sent from the js
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        // methods go here
        if (action.equals("monitorBeacons")) {
            // the arguments give us the string for the beacon region
            this.monitorBeacons(args.getString(0), callbackContext);
            return true;
        } else if (action.equals("rangeBeacons")) {
            // the arguments give us the string for the beacon region
            this.rangeBeacons(args.getString(0), callbackContext);
            return true;
        } else if (action.equals("monitorBeaconIdListener")) {
            this.monitorBeaconIdListener(callbackContext);
        } else if (action.equals("enteredRegionIdListener")) {
            this.enteredRegionIdListener(callbackContext);
        } else if (action.equals("leftRegionIdListener")) {
            this.leftRegionIdListener(callbackContext);
        } else if (action.equals("rangeBeaconIdListener")) {
            this.rangeBeaconIdListener(callbackContext);
        } else {
            return false; // Returning false results in a "MethodNotFound" error.
        }

        return false;


    }

    // @Override
    // protected void onCreate(Bundle savedInstanceState) {
    //     super.onCreate(savedInstanceState);

    // }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        beaconManager.unbind(this);
//    }

    // range beacons
    private void rangeBeaconIdListener(final CallbackContext callbackContext) {
        Log.i(TAG, "monitor region listener active!");
        Log.i(TAG, callbackContext.getCallbackId());

        cbContexts.put("rangeBeaconId", callbackContext);

        rangeManager(callbackContext);
        PluginResult result = new PluginResult(PluginResult.Status.OK);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);

    }

    // monitor beacons
    private void monitorBeaconIdListener(final CallbackContext callbackContext) {
        Log.i(TAG, "monitor region listener active! Listening to enter and exit region");
        Log.i(TAG, callbackContext.getCallbackId());

        cbContexts.put("monitorBeaconId", callbackContext);

        monitorManager(callbackContext);
        PluginResult result = new PluginResult(PluginResult.Status.OK);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);

    }

    // enter beacon region
    private void enteredRegionIdListener(final CallbackContext callbackContext) {
        Log.i(TAG, "entered region listener active!");
        Log.i(TAG, callbackContext.getCallbackId());

        cbContexts.put("enteredRegionId", callbackContext);

        PluginResult result = new PluginResult(PluginResult.Status.OK);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    // exited beacon region
    private void leftRegionIdListener(final CallbackContext callbackContext) {
        Log.i(TAG, "left region listener active!");
        Log.i(TAG, callbackContext.getCallbackId());

        cbContexts.put("leftRegionId", callbackContext);

        PluginResult result = new PluginResult(PluginResult.Status.OK);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }


//    @Override
    public void rangeManager(final CallbackContext callbackContext) {

        beaconManager.addRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(final Collection < Beacon > beacons, final Region region) {
                Log.i(TAG, "Ranging beacons " + beacons.size());
                Log.i(TAG, beacons.toString());

                Log.i(TAG, region.getUniqueId());
                Log.i(TAG, region.getId1().toString());

                try {
                    PluginResult result = new PluginResult(PluginResult.Status.OK, beacons.toString());
                    result.setKeepCallback(true);

                    // it's actually the context, not the id
                    cbContexts.get("rangeBeaconId").sendPluginResult(result);

                } catch (Exception e) {
                    Log.i(TAG, "'rangingDidFailForRegion' exception" + e.getCause());
                }

            }
        });
    }

//    @Override
    public void monitorManager(final CallbackContext callbackContext) {

        beaconManager.addMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: " + state);

                Log.i(TAG, region.getUniqueId());
                Log.i(TAG, region.getId1().toString());
                Log.i(TAG, region.getIdentifier(0).toString());
                try {
                    PluginResult result = new PluginResult(PluginResult.Status.OK, "monitor activated");
                    result.setKeepCallback(true);
                    cbContexts.get("monitorBeaconId").sendPluginResult(result);
                } catch (Exception e) {
                    Log.i(TAG, "'monitoringDidFailForRegion' exception" + e.getCause());
                }

            }

            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw a beacon for the first time!");
                Log.d(TAG, region.getUniqueId());
                Log.i(TAG, region.getIdentifier(0).toString());
                try {
                    PluginResult result = new PluginResult(PluginResult.Status.OK, "entered");
                    result.setKeepCallback(true);
                    cbContexts.get("enteredRegionId").sendPluginResult(result);
                } catch (Exception e) {
                    Log.i(TAG, "'monitoringDidFailForRegion' exception" + e.getCause());
                }
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
                Log.i(TAG, region.getIdentifier(0).toString());
                try {
                    PluginResult result = new PluginResult(PluginResult.Status.OK, "exited");
                    result.setKeepCallback(true);
                    cbContexts.get("leftRegionId").sendPluginResult(result);
                } catch (Exception e) {
                    Log.i(TAG, "'monitoringDidFailForRegion' exception" + e.getCause());
                }
            }
        });
    }


    public void monitorBeacons(String beaconString, final CallbackContext callbackContext) {

        Log.i(TAG, beaconString);

        List < String > beaconArray = new ArrayList < String > (Arrays.asList(beaconString.split("\\s*,\\s*")));

        theRegion = new Region(
                beaconArray.get(3), // "beaconName"
            Identifier.parse(beaconArray.get(0)), // "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"
            Identifier.parse(beaconArray.get(1)), // "XXXX"
            Identifier.parse(beaconArray.get(2)) // "XXXX"
        );

        try {
            Log.i(TAG, "Trying to monitor beacon!");
            this.beaconManager.startMonitoringBeaconsInRegion(theRegion);
            callbackContext.success("monitoring beacons success :D");
            Log.i(TAG, "Monitoring has begun!");

        } catch (RemoteException e) {
            Log.i(TAG, "There was an exception with the beacon region!");
            callbackContext.error("rangning beacons err :(");
        }
    }


    public void rangeBeacons(String beaconString, final CallbackContext callbackContext) {

        Log.i(TAG, beaconString);

        List < String > beaconArray = new ArrayList < String > (Arrays.asList(beaconString.split("\\s*,\\s*")));

        theRegion = new Region(
                beaconArray.get(3), // "beaconName"
            Identifier.parse(beaconArray.get(0)), // "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"
            Identifier.parse(beaconArray.get(1)), // "XXXX"
            Identifier.parse(beaconArray.get(2)) // "XXXX"
        );

        try {
            Log.i(TAG, "Trying to monitor beacon!");
            this.beaconManager.startRangingBeaconsInRegion(theRegion);
            callbackContext.success("ranging beacons success :D");
            Log.i(TAG, "Monitoring has begun!");

        } catch (RemoteException e) {
            Log.i(TAG, "There was an exception with the beacon region!");
            callbackContext.error("rangning beacons err :(");
        }
    }

    //////// IBeaconConsumer implementation /////////////////////

    @Override
    public void onBeaconServiceConnect() {
        Log.i(TAG, "Connected to IBeacon service");
    }

    @Override
    public Context getApplicationContext() {
        return cordova.getActivity();
    }

    @Override
    public void unbindService(ServiceConnection connection) {
        Log.i(TAG, "Unbind from IBeacon service");
        cordova.getActivity().unbindService(connection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection connection, int mode) {
        Log.i(TAG, "Bind to IBeacon service");
        return cordova.getActivity().bindService(intent, connection, mode);
    }

}
