package cn.walkpos.wpospad.login;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xingy.lib.ui.CheckBox;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.util.BlueUtil;
import cn.walkpos.wpospad.util.WPosConfig;


public class VerifyDetailActivity extends BaseActivity implements OnSuccessListener<JSONObject>,
        BluetoothAdapter.LeScanCallback, AdapterView.OnItemClickListener {

    public static final String TAG = VerifyDetailActivity.class.getName();
    private final static String UUID_KEY_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private final static String UUID_KEY_TEST = "00001101-0000-1000-8000-00805f9b34fb";

    private BluetoothAdapter mBtAdapter;
    private Runnable stopLeRunnable;
    private Handler mHandler = new Handler();
    private boolean mScanning = false;

    public static final int SCAN_PERIOD = 1000 * 60 * 5;
    public static final int REQUEST_ENABLE_BT = 1001;

    private LeDevListAdapter mLeAdapter;
    private ListView mListV;

    private BluetoothGatt mBleGatt;
    private BluetoothGattCallback mGattCallback;

    private View coverView;
    private RadioGroup shopTypeRg;
    private static final String unitShop = "unit";
    private static final String businessShop = "business";
    private String shopTypestr;

    private EditText     fullNameEt;
    private EditText     idcardEt;
    private EditText     bankAccountEt;
    private EditText     bankNameEt;
    private EditText     industryEt;
    private Ajax         mAjax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_detail);
        loadNavBar(R.id.verify_detail_nav);
        findViewById(R.id.verify_next_btn).setOnClickListener(this);
        findViewById(R.id.bind_pos_btn).setOnClickListener(this);
        coverView = findViewById(R.id.tail_cover);
        coverView.setVisibility(View.VISIBLE);

        shopTypeRg = (RadioGroup)this.findViewById(R.id.shop_type_rg);
        shopTypeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId ==R.id.shop_unit)
                {
                    shopTypestr = unitShop;
                }
                else if(checkedId == R.id.shop_business)
                    shopTypestr = businessShop;
            }
        });
        shopTypeRg.check(R.id.shop_unit);
        fullNameEt = (EditText)this.findViewById(R.id.law_owner_name);
        idcardEt = (EditText)this.findViewById(R.id.law_owner_idcard);
        bankAccountEt = (EditText)this.findViewById(R.id.bank_account);
        bankNameEt = (EditText)this.findViewById(R.id.bank_name);
        industryEt = (EditText)this.findViewById(R.id.business_name);


        mLeAdapter = new LeDevListAdapter();
        mListV = (ListView) findViewById(R.id.device_list);
        mListV.setAdapter(mLeAdapter);
        mListV.setOnItemClickListener(this);
        mListV.setVisibility(View.GONE);

        mGattCallback = new BluetoothGattCallback() {

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.e(TAG, "Connected to GATT server.");
//                    Log.e(TAG, "Attempting to start service discovery:" +
                            mBleGatt.discoverServices();
//                    writeTest();


                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.e(TAG, "Disconnected from GATT server.");
                }
            }

            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    List<BluetoothGattService> alist = gatt.getServices();

                    displayGattServices(alist);
                } else {
                    Log.e(TAG, "onServicesDiscovered not succ received: " + status);
                }

            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
                                             int status) {
                android.util.Log.e("onCharacteristicRead", "" + status + ":" + characteristic.toString());
                super.onCharacteristicRead(gatt, characteristic, status);
            }


            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
                                              int status) {
                android.util.Log.e("onCharacteristicRead", "" + status + ":" + characteristic.toString());
                super.onCharacteristicWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt,
                                                BluetoothGattCharacteristic characteristic) {
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                         int status) {
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                          int status) {
            }
        };
    }

    private void writeTest() {

//        BluetoothGattCharacteristic chatic = new BluetoothGattCharacteristic(UUID_KEY_TEST,);
//        mBleGatt.writeCharacteristic(chatic);
    }

    @Override
    protected void onResume() {
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


    private void scan3Device() {

    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            if (stopLeRunnable == null) {
                stopLeRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mScanning = false;
                        mBtAdapter.stopLeScan(VerifyDetailActivity.this);
                    }
                };
            }

            mHandler.postDelayed(stopLeRunnable, SCAN_PERIOD);

            UiUtils.makeToast(this, "start Scan BlE for " + SCAN_PERIOD / 1000 + "s");

            mScanning = true;
            mBtAdapter.startLeScan(this);
        } else {
            mScanning = false;
            mBtAdapter.stopLeScan(this);
        }

    }

    private void verifyBasicInfo() {
        String fullnamestr = fullNameEt.getText().toString();
        if (TextUtils.isEmpty(fullnamestr)) {
            UiUtils.makeToast(this, "请输入有效法人全名");
            return;
        }
        String idcardstr = idcardEt.getText().toString();
        if (TextUtils.isEmpty(idcardstr)) {
            UiUtils.makeToast(this, "请输入有效法人身份证号");
            return;
        }
        String bankaccoutstr = bankAccountEt.getText().toString();
        if (TextUtils.isEmpty(bankaccoutstr)) {
            UiUtils.makeToast(this, "请输入有效银行账号");
            return;
        }
        String banknamestr = bankNameEt.getText().toString();
        if (TextUtils.isEmpty(banknamestr)) {
            UiUtils.makeToast(this, "请输入有效银行名");
            return;
        }
        String industrystr = industryEt.getText().toString();
        if (TextUtils.isEmpty(industrystr)) {
            UiUtils.makeToast(this, "请输入有效所属行业");
            return;
        }

        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_BUSINESS);
        if (null == mAjax)
            return;
        showLoadingLayer();

        mAjax.setId(WPosConfig.REQ_VERIFY_STOREINFO);
        mAjax.setData("method", "businescenter.verify");
        mAjax.setData("full_name", fullnamestr);
        mAjax.setData("card_number", idcardstr);
        mAjax.setData("bank_card", bankaccoutstr);
        mAjax.setData("account_bank", banknamestr);
        mAjax.setData("industry", industrystr);
        mAjax.setData("shop_type", shopTypestr);

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);

        mAjax.send();
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = null;
        switch (v.getId()) {
            case R.id.verify_next_btn:
                verifyBasicInfo();
                break;
            case R.id.bind_pos_btn:
//                mListV.setVisibility(View.VISIBLE);
                UiUtils.makeToast(this,"绑定成功");
                coverView.setVisibility(View.GONE);
//                initBlue();
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
                if (device != null && mLeAdapter != null) {
                    mLeAdapter.addLeDevice(device);
                    mLeAdapter.notifyDataSetChanged();
                }
            }
        });

    }


    protected void connect(BluetoothDevice device) {

        BluetoothSocket socket = null;
        OutputStream os = null;//输出流
        InputStream is = null;//输入流

        try {

            // socket = device.createRfcommSocketToServiceRecord(BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);

            socket = device.createRfcommSocketToServiceRecord(UUID.fromString(UUID_KEY_TEST));
            socket.connect();
            os = socket.getOutputStream();
            os.write("abc".getBytes());
            os.flush();
            os.close();

            //5处理客户端输入流
            is = socket.getInputStream();
            byte[] b = new byte[1024];
            is.read(b);
            UiUtils.makeToast(VerifyDetailActivity.this,"客户端接收到服务器传输的数据：" + new String(b));
            is.close();


        } catch (IOException ex) {
            ex.printStackTrace();

        } finally {

            if (null != socket)
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Object dev = mLeAdapter.getItem(position);
        List<BluetoothDevice> ali = null;
//        if(null!=mBleGatt)
//             ali = mBleGatt.getConnectedDevices();


        if (dev != null && dev instanceof BluetoothDevice) {
            ParcelUuid[] its = ((BluetoothDevice) dev).getUuids();
            connect(((BluetoothDevice)dev));
//            mListV.setVisibility(View.GONE);
//            coverView.setVisibility(View.GONE);
            if(null==mBleGatt || !mBleGatt.getDevice().equals(dev))
                mBleGatt = ((BluetoothDevice) dev).connectGatt(this, true, mGattCallback);
            mBleGatt.connect();
        }
    }

    @Override
    public void onBackPressed() {
        if (mListV.getVisibility() == View.VISIBLE) {
            mHandler.removeCallbacks(stopLeRunnable);
            mListV.setVisibility(View.GONE);
            mScanning = false;
            mBtAdapter.stopLeScan(VerifyDetailActivity.this);
        } else
            super.onBackPressed();
    }

    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {
        closeLoadingLayer();

        int errno = jsonObject.optInt("response_code",-1);
        if(errno!=0)
        {
            String msg = jsonObject.optString("res", getString(R.string.network_error));
            UiUtils.makeToast(this,msg);
            return;
        }

        JSONObject data = jsonObject.optJSONObject("data");
        Bundle bundle = new Bundle();
        if(null!=data) {
            bundle.putString(VerifyPicActivity.WEIXIN_URL, data.optString("weixin_url"));
        }
        UiUtils.makeToast(this,jsonObject.optString("res", "认证成功请继续上传照片资料"));
        UiUtils.startActivity(VerifyDetailActivity.this, VerifyPicActivity.class, bundle,true);
    }


    public class LeDevListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> devArray;

        public LeDevListAdapter() {
            devArray = new ArrayList<BluetoothDevice>();
        }

        public void addLeDevice(BluetoothDevice dev) {
            if (!devArray.contains(dev))
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


    }

    ;


    public class ItemHolder {
        public TextView tv;
    }


    /////////////////////////////////////////////////////////////////
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        for (BluetoothGattService gattService : gattServices) {
            //-----Service的字段信息-----//
            int type = gattService.getType();
            Log.e(TAG, "-->service type:" + BlueUtil.getServiceType(type));
            Log.e(TAG, "-->includedServices size:" + gattService.getIncludedServices().size());
            Log.e(TAG, "-->service uuid:" + gattService.getUuid());

            //-----Characteristics的字段信息-----//
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                Log.e(TAG, "---->char uuid:" + gattCharacteristic.getUuid());

                int permission = gattCharacteristic.getPermissions();
                Log.e(TAG, "---->char permission:" + BlueUtil.getCharPermission(permission));

                int property = gattCharacteristic.getProperties();
                Log.e(TAG, "---->char property:" + BlueUtil.getCharPropertie(property));

                byte[] data = gattCharacteristic.getValue();
                if (data != null && data.length > 0) {
                    Log.e(TAG, "---->char value:" + new String(data));
                }

                //UUID_KEY_DATA是可以跟蓝牙模块串口通信的Characteristic
                if (gattCharacteristic.getUuid().toString().equals(UUID_KEY_DATA)) {
                    //测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBleGatt.readCharacteristic(gattCharacteristic);
                        }
                    }, 500);

                    //接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    mBleGatt.setCharacteristicNotification(gattCharacteristic, true);
                    //设置数据内容
                    gattCharacteristic.setValue("send data->");
                    //往蓝牙模块写入数据
                    mBleGatt.writeCharacteristic(gattCharacteristic);
                }

                //-----Descriptors的字段信息-----//
                List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
                    Log.e(TAG, "-------->desc uuid:" + gattDescriptor.getUuid());
                    int descPermission = gattDescriptor.getPermissions();
                    Log.e(TAG, "-------->desc permission:" + BlueUtil.getDescPermission(descPermission));

                    byte[] desData = gattDescriptor.getValue();
                    if (desData != null && desData.length > 0) {
                        Log.e(TAG, "-------->desc value:" + new String(desData));
                    }
                }
            }
        }//

    }

    @Override
    protected void onDestroy()
    {
        if(null!=mBleGatt)
            mBleGatt.close();
        super.onDestroy();
    }

}
