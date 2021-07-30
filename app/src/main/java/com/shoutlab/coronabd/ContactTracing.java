package com.shoutlab.coronabd;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ContactTracing extends AppCompatActivity {

    TextView status;
    TextView scan, let;
    PreferenceManager preferenceManager;
    ImageView backbutton;
    BluetoothAdapter adapter;


    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.e("status", "off");
                        scan.setBackground(getDrawable(R.drawable.cornarboxred));
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.e("status", "turning off");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.e("status", "on");
                        scan.setBackground(getDrawable(R.drawable.cornarbox));
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.e("status", "turning on");
                        break;
                }

            }
        }
    };

    void playRingtone() {

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just incase
            if (alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        r = RingtoneManager.getRingtone(getApplicationContext(), alert);
        if (r.isPlaying()) {
            r.stop();
        } else {
            r.play();
        }
    }
    Ringtone r;

    private final BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                Log.e("device", "found");

                BluetoothDevice deviceaddress = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (deviceaddress != null) {
                    Log.e("device address", deviceaddress.toString());
                    if (deviceaddress.getName() != null) {
                        Log.e("device name", deviceaddress.getName());
                        if (deviceaddress.getName().contains("Covid_19_Positive")) {
                            covidpositivedialogue.show();
                            playRingtone();
                        }

                    }
                    if (deviceaddress.getAddress() != null) {
                        Log.e("device name", deviceaddress.getAddress());

                    }

                }

            }
            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Log.e("status", "Discovery Started ...");
            }
            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Log.e("status", "Discovery Finished ...");
                if(!covidpositivedialogue.isShowing()){
                    Toast.makeText(context, "No Patient Found!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        }
    };
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.e("scanmode", "connectable discoverable");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.e("scanmode", "connectable");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.e("scanmode", "none");
                        break;
                }
            }
        }
    };


    AlertDialog dialog,covidpositivedialogue;
    AlertDialog.Builder builder,builder1;


    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver1, filter1);


        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter2.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter2.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2, filter2);

        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter3.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter3.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBroadcastReceiver3, intentFilter3);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver3);
    }

    void changeBTName() {

        if (Boolean.parseBoolean(preferenceManager.getStatus().toLowerCase())) {
            final String sNewName = "Covid_19_Positive_" + preferenceManager.getUUID();

            if (adapter.getState() == BluetoothAdapter.STATE_ON) {
                adapter.setName(sNewName);
                Log.e("adaptername", adapter.getName());
            }
        }


    }

    private boolean setBluetoothScanMode() {
        Method method = null;

        if (!adapter.isEnabled()) {
            Log.d("LCAT", "BT adapter is off, turning on");
            adapter.enable();
        }

        try {
            method = adapter.getClass().getMethod("setScanMode", int.class);
        } catch (SecurityException | NoSuchMethodException e) {
            return false;
        }

        try {
            method.invoke(adapter, 23);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_tracing);

        scan = findViewById(R.id.scan);
        let = findViewById(R.id.let);
        backbutton = findViewById(R.id.back_button);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog.isShowing()){
                    dialog.dismiss();
                }else {
                    onBackPressed();
                    finish();
                }

            }
        });
        preferenceManager = new PreferenceManager(this);

        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        if (manager.getAdapter() != null) {
            adapter = manager.getAdapter();
        } else {
            adapter = BluetoothAdapter.getDefaultAdapter();
        }


        if (adapter.isEnabled()) {
            scan.setBackground(getDrawable(R.drawable.cornarbox));
        }


        if (!Boolean.parseBoolean(preferenceManager.getStatus().toLowerCase())) {
            let.setVisibility(View.GONE);
        }

        let.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.isEnabled()) {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
                    startActivity(discoverableIntent);
                } else {
                    Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(i, 5);
                }

            }
        });

        builder = new AlertDialog.Builder(ContactTracing.this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        View vi = getLayoutInflater().inflate(R.layout.searchlayout, null, false);
        builder.setView(vi);
        dialog = builder.create();


        builder1 = new AlertDialog.Builder(ContactTracing.this);
        View vi2 = getLayoutInflater().inflate(R.layout.showcovidpositive,null,false);

        TextView cancle = vi2.findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(covidpositivedialogue.isShowing()){
                    covidpositivedialogue.dismiss();

                }
                if (r!= null && r.isPlaying()){
                    r.stop();
                }
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });
        builder1.setView(vi2);

        covidpositivedialogue = builder1.create();



        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialog.show();


                if (adapter != null && adapter.isDiscovering()) {
                    Toast.makeText(ContactTracing.this, "Device is Already Searching!", Toast.LENGTH_SHORT).show();
                } else {


                    if (!adapter.isEnabled()) {
                        Toast.makeText(ContactTracing.this, "Starting Patient Search!", Toast.LENGTH_SHORT).show();
                        scan.setBackground(getDrawable(R.drawable.cornarbox));
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 1);
                    } else {
                        Toast.makeText(ContactTracing.this, "Starting Patient Search!", Toast.LENGTH_SHORT).show();
                        scan.setBackground(getDrawable(R.drawable.cornarbox));
                        if (ContextCompat.checkSelfPermission(ContactTracing.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                            changeBTName();
                            if (Settings.System.canWrite(ContactTracing.this)) {
                                setBluetoothScanMode();
                            } else {
                                Log.e("try", "cant");
                                requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 3);
                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("adapter mode", String.valueOf(adapter.getScanMode()));

                                    adapter.startDiscovery();
                                }
                            }, 5000);

                        } else {
                            ActivityCompat.requestPermissions(ContactTracing.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    2);
                        }
                    }


                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                changeBTName();
                if (Settings.System.canWrite(this)) {
                    setBluetoothScanMode();
                } else {
                    Log.e("try", "cant");
                    requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 3);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("adapter mode", String.valueOf(adapter.getScanMode()));

                        adapter.startDiscovery();
                    }
                }, 10000);

            } else {
                ActivityCompat.requestPermissions(ContactTracing.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        2);
            }

        }

        if (requestCode == 5 && resultCode == RESULT_OK) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                changeBTName();

                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
                startActivity(discoverableIntent);

            } else {
                ActivityCompat.requestPermissions(ContactTracing.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        2);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}