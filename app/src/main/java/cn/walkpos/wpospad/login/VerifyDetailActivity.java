package cn.walkpos.wpospad.login;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;
import java.util.UUID;

import cn.walkpos.wpospad.BlueBle.BluetoothLeClass;
import cn.walkpos.wpospad.R;


public class VerifyDetailActivity extends BaseActivity implements BluetoothAdapter.LeScanCallback, AdapterView.OnItemClickListener {


    private BluetoothAdapter mBtAdapter;
    private Runnable stopLeRunnable;
    private Handler mHandler = new Handler();
    private boolean mScanning = false;

    public static final int SCAN_PERIOD = 1000*60*5;
    public static final int REQUEST_ENABLE_BT = 1001;

    private LeDevListAdapter   mLeAdapter;
    private ListView           mListV;

    private BluetoothGatt mBleGatt;
    private BluetoothGattCallback  mGattCallback;

    private View     coverView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_detail);
        loadNavBar(R.id.verify_detail_nav);
        findViewById(R.id.submit_btn).setOnClickListener(this);
        findViewById(R.id.bind_pos_btn).setOnClickListener(this);
        coverView = findViewById(R.id.tail_cover);
        coverView.setVisibility(View.VISIBLE);

        mLeAdapter = new LeDevListAdapter();
        mListV = (ListView)findViewById(R.id.device_list);
        mListV.setAdapter(mLeAdapter);
        mListV.setOnItemClickListener(this);
        mListV.setVisibility(View.GONE);

        mGattCallback = new BluetoothGattCallback(){

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                int newState) {
                android.util.Log.e("onConnectionStateChange", "" + status + "->" + newState);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
                                             int status)
            {
                android.util.Log.e("onCharacteristicRead", "" + status + ":" + characteristic.toString());
                super.onCharacteristicRead(gatt,characteristic,status);
            }


            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
            int status) {
                android.util.Log.e("onCharacteristicRead", "" + status + ":" + characteristic.toString());
                super.onCharacteristicWrite(gatt,characteristic,status);
            }
        };
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    private void initBlue() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            UiUtils.makeToast(this, "Unsupport BLE");
            finish();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = bluetoothManager.getAdapter();
//        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBtAdapter.isEnabled()) {
            UiUtils.makeToast(this, "Enable Bluetooth ing...");

            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            scanLeDevice(true);
//            scan3Device();
        }
    }


    private void scan3Device()
    {

    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            if(stopLeRunnable==null) {
                stopLeRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mScanning = false;
                        mBtAdapter.stopLeScan(VerifyDetailActivity.this);
                    }
                };
            }

            mHandler.postDelayed(stopLeRunnable,SCAN_PERIOD);

            UiUtils.makeToast(this, "start Scan BlE for " + SCAN_PERIOD/1000 + "s");

            mScanning = true;
            mBtAdapter.startLeScan(this);
        } else {
            mScanning = false;
            mBtAdapter.stopLeScan(this);
        }

    }




    @Override
    public void onClick(View v)
    {
        Bundle  bundle = null;
        switch (v.getId()) {
            case R.id.submit_btn:
                UiUtils.startActivity(VerifyDetailActivity.this,VerifyPicActivity.class,bundle,true);
                break;
            case R.id.bind_pos_btn:
                mListV.setVisibility(View.VISIBLE);
                initBlue();
            default:
                super.onClick(v);
                break;
        }

    }

    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(device!=null && mLeAdapter!=null) {
                    mLeAdapter.addLeDevice(device);
                    mLeAdapter.notifyDataSetChanged();
                }
            }
        });

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        int x = test();

        Object  dev = mLeAdapter.getItem(position);
        if(dev !=null && dev instanceof BluetoothDevice)
        {
            UiUtils.makeToast(this, "" + ((BluetoothDevice) dev).getUuids() + "," + ((BluetoothDevice) dev).getAddress() + "," +
                    ((BluetoothDevice) dev).getBondState());

            ParcelUuid[]  uuids = ((BluetoothDevice) dev).getUuids();
            mListV.setVisibility(View.GONE);
            coverView.setVisibility(View.GONE);
            mBleGatt = ((BluetoothDevice)dev).connectGatt(this, true, mGattCallback);
            if(null!=mBleGatt)
            {
                boolean succ = mBleGatt.connect();


            }
        }
    }

    private int test()
    {
        int i=0;
        int j=0;
        try{
            i++;

            i = i/j;
        }catch (Exception e)
        {
            i+=2;
            return i;
        }finally {
            i+=2;
            return i;
        }
    }

    @Override
    public void onBackPressed()
    {
        if(mListV.getVisibility()==View.VISIBLE)
        {
            mHandler.removeCallbacks(stopLeRunnable);
            mListV.setVisibility(View.GONE);
            mScanning = false;
            mBtAdapter.stopLeScan(VerifyDetailActivity.this);
        }
        else
            super.onBackPressed();
    }

    public class LeDevListAdapter extends BaseAdapter
    {
        private ArrayList<BluetoothDevice> devArray;

        public LeDevListAdapter()
        {
            devArray = new ArrayList<BluetoothDevice>();
        }

        public void addLeDevice(BluetoothDevice dev)
        {
            if(!devArray.contains(dev))
                devArray.add(dev);
        }

        @Override
        public int getCount() {
            return (null == devArray ? 0 : devArray.size());
        }

        @Override
        public Object getItem(int position) {
            return (null == devArray ? null : devArray.get(position));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder = null;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_tv, null);
                holder = new ItemHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.info);

                convertView.setTag(holder);
            } else {
                holder = (ItemHolder) convertView.getTag();
            }

            BluetoothDevice dev = devArray.get(position);
            holder.tv.setText(dev.getName());
            return convertView;
        }


    };


    public class ItemHolder
    {
        public TextView tv;
    }
}
