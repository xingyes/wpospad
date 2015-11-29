package cn.walkpos.wpospad.BlueBle;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.walktech.mposlib.mposService;
import com.xingy.lib.ui.UiUtils;

import cn.walkpos.wpospad.R;

public class ConnectBlueActivity extends Activity {
    // Debugging
    private static final String TAG = "WM31 MainActivity";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Layout Views
    private ListView mConversationView;

    private Button mButtonCMD1;
    private Button mButtonCMD2;

    private static boolean sendmsg_dbg = false;
    private Button mButtonDBG;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    private mposService mmposService = null;

    private Thread mStateThread = new Thread(new StateThread());

    RecvThread recv = new RecvThread();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        setContentView(R.layout.bluemain);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            UiUtils.makeToast(this, "Bluetooth is not available");
            finish();
            return;
        }

        mStateThread.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
        else {
            if (mmposService == null)
            {
                setup();
            }
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mmposService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mmposService.getState() == mposService.STATE_NONE) {

                if(D) Log.e(TAG, "+ BluetoothService.STATE_NONE +");
            }
        }
    }

    private void setup() {
        Log.d(TAG, "setup()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the send button with a listener that for click events

        mButtonCMD1 = (Button) findViewById(R.id.button_cmd1);
        mButtonCMD1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send cmd1 to mpos
                sendCMD1();
            }
        });

        mButtonCMD2 = (Button) findViewById(R.id.button_cmd2);
        mButtonCMD2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send cmd2 to mpos
                sendCMD2();
            }
        });


        mButtonDBG = (Button) findViewById(R.id.sendmsg_dbg);
        mButtonDBG.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setMSGDBG();
            }
        });

        // Initialize the mmposService to perform bluetooth connections
        mmposService = new mposService(this);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop mmposService
        if (mmposService != null) mmposService.stop();

        mStateThread.interrupt();

        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    /**
     * Send CMD1.
     *
     */
    private void sendCMD1() {
        // Check that we're actually connected before trying anything
        if (mmposService.getState() != mposService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] send = {(byte)0xAA,(byte) 0xBB,0x09,0x00,0x04,0x01,0x01,(byte)0xCC,(byte) 0xDD,0x1C};

        mmposService.sendMSG(send.length, send);

        Thread recv1 = new Thread(recv, "CMD1");

        recv1.start();

        mHandler.obtainMessage(ConnectBlueActivity.MESSAGE_WRITE, 1, -1, send)
                .sendToTarget();
    }

    /**
     * Send CMD2.
     *
     */
    private void sendCMD2() {
        // Check that we're actually connected before trying anything
        if (mmposService.getState() != mposService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] send = {(byte) 0xAA,(byte) 0xBB,0x0B,0x00,0x04,0x01,0x01,(byte) 0xCC,(byte) 0xDD,0x1E};

        mmposService.sendMSG(send.length, send);

        Thread recv2 = new Thread(recv, "CMD2");

        recv2.start();

        mHandler.obtainMessage(ConnectBlueActivity.MESSAGE_WRITE, 2, -1, send)
                .sendToTarget();
    }

    /**
     * Set dbg for SEND TO MPOS.
     *
     */
    private void setMSGDBG() {
        if (mmposService == null) {
            return;
        }

        mmposService.setMSGDBG(sendmsg_dbg);

        Toast.makeText(this, "Set SendMSG DBG " + sendmsg_dbg, Toast.LENGTH_SHORT).show();

        sendmsg_dbg = !sendmsg_dbg;
    }

    private final void setStatus(int resId) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subTitle);
    }

    public static String bytes2HexString(byte[] b, int length) {
        String ret = "";
        for (int i = 0; i < length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case mposService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            break;
                        case mposService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case mposService.STATE_DISCONNECTED:
                            setStatus(R.string.title_disconnected);
                            break;
                        case mposService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = bytes2HexString(writeBuf, writeBuf.length);

                    mConversationArrayAdapter.add("HOST: CMD"+ msg.arg1 + " " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = bytes2HexString(readBuf, msg.arg1);

                    mConversationArrayAdapter.add("MPOS: " + readMessage);

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so setup a session to mpos
                    setup();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device

        if(mmposService == null)
        {
            Log.e(TAG, " + mBluetoothService NULL + ");
            return;
        }
        mmposService.connect(device);

        mConnectedDeviceName = device.getName();
    }

    public class RecvThread implements Runnable {

        @Override
        public void run() {
            byte[] in = new byte[1024];
            int ret = 0;
            String cmd = Thread.currentThread().getName();

            ret = mmposService.recvMSG(in, 1000);

            Log.e(TAG, cmd + " read return: " + ret);

            if(ret <= 0)
            {
                return;
            }

            mHandler.obtainMessage(ConnectBlueActivity.MESSAGE_READ, ret, -1, in)
                    .sendToTarget();
        }
    }

    public class StateThread implements Runnable {

        @Override
        public void run() {

            int state;
            int lastState = -1;

            Log.v(TAG,"StateThread run");

            // TODO Auto-generated method stub
            while (true)
            {
                if(mmposService != null)
                {
                    state = mmposService.getState();
                    if(state != lastState)
                    {
                        lastState = state;
                        mHandler.obtainMessage(ConnectBlueActivity.MESSAGE_STATE_CHANGE, state, -1)
                                .sendToTarget();
                    }

                }

                if(Thread.interrupted() == true)
                {
                    return;
                }
                else
                {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        Log.e(TAG,"StateThread sleep InterruptedException: " + e);
                        return;
                    }
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.connect:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;

        }
        return false;
    }
}
