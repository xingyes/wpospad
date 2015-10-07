package cn.walkpos.wpospad.util;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by xingyao on 15-10-4.
 */
public class BlueUtil {

    public static final String ServiceDiscoveryServerServiceClassID_UUID = "00001000-0000-1000-8000-00805F9B34FB";
    public static final String BrowseGroupDescriptorServiceClassID_UUID = "00001001-0000-1000-8000-00805F9B34FB";
    public static final String PublicBrowseGroupServiceClass_UUID = "00001002-0000-1000-8000-00805F9B34FB";

//     蓝牙串口服务
    public static final String SerialPortServiceClass_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    public static final String LANAccessUsingPPPServiceClass_UUID = "00001102-0000-1000-8000-00805F9B34FB";

//            拨号网络服务
    public static final String DialupNetworkingServiceClass_UUID = "00001103-0000-1000-8000-00805F9B34FB";

//    信息同步服务
    public static final String IrMCSyncServiceClass_UUID = "00001104-0000-1000-8000-00805F9B34FB";

    public static final String SDP_OBEXObjectPushServiceClass_UUID = "00001105-0000-1000-8000-00805F9B34FB";

//            文件传输服务
    public static final String OBEXFileTransferServiceClass_UUID = "00001106-0000-1000-8000-00805F9B34FB";

    public static final String IrMCSyncCommandServiceClass_UUID = "00001107-0000-1000-8000-00805F9B34FB";
    public static final String SDP_HeadsetServiceClass_UUID = "00001108-0000-1000-8000-00805F9B34FB";
    public static final String CordlessTelephonyServiceClass_UUID = "00001109-0000-1000-8000-00805F9B34FB";
    public static final String SDP_AudioSourceServiceClass_UUID = "0000110A-0000-1000-8000-00805F9B34FB";
    public static final String SDP_AudioSinkServiceClass_UUID = "0000110B-0000-1000-8000-00805F9B34FB";
    public static final String SDP_AVRemoteControlTargetServiceClass_UUID = "0000110C-0000-1000-8000-00805F9B34FB";
    public static final String SDP_AdvancedAudioDistributionServiceClass_UUID = "0000110D-0000-1000-8000-00805F9B34FB";
    public static final String SDP_AVRemoteControlServiceClass_UUID = "0000110E-0000-1000-8000-00805F9B34FB";
    public static final String VideoConferencingServiceClass_UUID = "0000110F-0000-1000-8000-00805F9B34FB";
    public static final String IntercomServiceClass_UUID = "00001110-0000-1000-8000-00805F9B34FB";

            //蓝牙传真服务
    public static final String FaxServiceClass_UUID = "00001111-0000-1000-8000-00805F9B34FB";

    public static final String HeadsetAudioGatewayServiceClass_UUID = "00001112-0000-1000-8000-00805F9B34FB";
    public static final String WAPServiceClass_UUID = "00001113-0000-1000-8000-00805F9B34FB";
    public static final String WAPClientServiceClass_UUID = "00001114-0000-1000-8000-00805F9B34FB";

            //个人局域网服务
    public static final String PANUServiceClass_UUID = "00001115-0000-1000-8000-00805F9B34FB";
    public static final String NAPServiceClass_UUID = "00001116-0000-1000-8000-00805F9B34FB";
    public static final String GNServiceClass_UUID = "00001117-0000-1000-8000-00805F9B34FB";

    public static final String DirectPrintingServiceClass_UUID = "00001118-0000-1000-8000-00805F9B34FB";
    public static final String ReferencePrintingServiceClass_UUID = "00001119-0000-1000-8000-00805F9B34FB";
    public static final String ImagingServiceClass_UUID = "0000111A-0000-1000-8000-00805F9B34FB";
    public static final String ImagingResponderServiceClass_UUID = "0000111B-0000-1000-8000-00805F9B34FB";
    public static final String ImagingAutomaticArchiveServiceClass_UUID = "0000111C-0000-1000-8000-00805F9B34FB";
    public static final String ImagingReferenceObjectsServiceClass_UUID = "0000111D-0000-1000-8000-00805F9B34FB";
    public static final String SDP_HandsfreeServiceClass_UUID = "0000111E-0000-1000-8000-00805F9B34FB";
    public static final String HandsfreeAudioGatewayServiceClass_UUID = "0000111F-0000-1000-8000-00805F9B34FB";
    public static final String DirectPrintingReferenceObjectsServiceClass_UUID = "00001120-0000-1000-8000-00805F9B34FB";
    public static final String ReflectedUIServiceClass_UUID = "00001121-0000-1000-8000-00805F9B34FB";
    public static final String BasicPringingServiceClass_UUID = "00001122-0000-1000-8000-00805F9B34FB";
    public static final String PrintingStatusServiceClass_UUID = "00001123-0000-1000-8000-00805F9B34FB";

    //人机输入服务
    public static final String HumanInterfaceDeviceServiceClass_UUID = "00001124-0000-1000-8000-00805F9B34FB";

    public static final String HardcopyCableReplacementServiceClass_UUID = "00001125-0000-1000-8000-00805F9B34FB";

    //蓝牙打印服务
    public static final String HCRPrintServiceClass_UUID = "00001126-0000-1000-8000-00805F9B34FB";

    public static final String HCRScanServiceClass_UUID = "00001127-0000-1000-8000-00805F9B34FB";
    public static final String CommonISDNAccessServiceClass_UUID = "00001128-0000-1000-8000-00805F9B34FB";
    public static final String VideoConferencingGWServiceClass_UUID = "00001129-0000-1000-8000-00805F9B34FB";
    public static final String UDIMTServiceClass_UUID = "0000112A-0000-1000-8000-00805F9B34FB";
    public static final String UDITAServiceClass_UUID = "0000112B-0000-1000-8000-00805F9B34FB";
    public static final String AudioVideoServiceClass_UUID = "0000112C-0000-1000-8000-00805F9B34FB";
    public static final String SIMAccessServiceClass_UUID = "0000112D-0000-1000-8000-00805F9B34FB";
    public static final String PnPInformationServiceClass_UUID = "00001200-0000-1000-8000-00805F9B34FB";
    public static final String GenericNetworkingServiceClass_UUID = "00001201-0000-1000-8000-00805F9B34FB";
    public static final String GenericFileTransferServiceClass_UUID = "00001202-0000-1000-8000-00805F9B34FB";
    public static final String GenericAudioServiceClass_UUID = "00001203-0000-1000-8000-00805F9B34FB";
    public static final String GenericTelephonyServiceClass_UUID = "00001204-0000-1000-8000-00805F9B34FB";

    public static final UUID [] BlueUuids=
    {
        UUID.fromString(ServiceDiscoveryServerServiceClassID_UUID),
                UUID.fromString(BrowseGroupDescriptorServiceClassID_UUID),
                UUID.fromString(PublicBrowseGroupServiceClass_UUID),
                UUID.fromString(SerialPortServiceClass_UUID),
                UUID.fromString(LANAccessUsingPPPServiceClass_UUID),
                UUID.fromString(DialupNetworkingServiceClass_UUID),
                UUID.fromString(IrMCSyncServiceClass_UUID),
                UUID.fromString(SDP_OBEXObjectPushServiceClass_UUID),
                UUID.fromString(OBEXFileTransferServiceClass_UUID),
                UUID.fromString(IrMCSyncCommandServiceClass_UUID),
                UUID.fromString(SDP_HeadsetServiceClass_UUID),
                UUID.fromString(CordlessTelephonyServiceClass_UUID),
                UUID.fromString(SDP_AudioSourceServiceClass_UUID),
                UUID.fromString(SDP_AudioSinkServiceClass_UUID),
                UUID.fromString(SDP_AVRemoteControlTargetServiceClass_UUID),
                UUID.fromString(SDP_AdvancedAudioDistributionServiceClass_UUID),
                UUID.fromString(SDP_AVRemoteControlServiceClass_UUID),
                UUID.fromString(VideoConferencingServiceClass_UUID),
                UUID.fromString(IntercomServiceClass_UUID),
                UUID.fromString(FaxServiceClass_UUID),
                UUID.fromString(HeadsetAudioGatewayServiceClass_UUID),
                UUID.fromString(WAPServiceClass_UUID),
                UUID.fromString(WAPClientServiceClass_UUID),
                UUID.fromString(PANUServiceClass_UUID),
                UUID.fromString(NAPServiceClass_UUID),
                UUID.fromString(GNServiceClass_UUID),
                UUID.fromString(DirectPrintingServiceClass_UUID),
                UUID.fromString(ReferencePrintingServiceClass_UUID),
                UUID.fromString(ImagingServiceClass_UUID),
                UUID.fromString(ImagingResponderServiceClass_UUID),
                UUID.fromString(ImagingAutomaticArchiveServiceClass_UUID),
                UUID.fromString(ImagingReferenceObjectsServiceClass_UUID),
                UUID.fromString(SDP_HandsfreeServiceClass_UUID),
                UUID.fromString(HandsfreeAudioGatewayServiceClass_UUID),
                UUID.fromString(DirectPrintingReferenceObjectsServiceClass_UUID),
                UUID.fromString(ReflectedUIServiceClass_UUID),
                UUID.fromString(BasicPringingServiceClass_UUID),
                UUID.fromString(PrintingStatusServiceClass_UUID),

                UUID.fromString(HumanInterfaceDeviceServiceClass_UUID),

                UUID.fromString(HardcopyCableReplacementServiceClass_UUID),

                UUID.fromString(HCRPrintServiceClass_UUID),

                UUID.fromString(HCRScanServiceClass_UUID),
                UUID.fromString(CommonISDNAccessServiceClass_UUID),
                UUID.fromString(VideoConferencingGWServiceClass_UUID),
                UUID.fromString(UDIMTServiceClass_UUID),
                UUID.fromString(UDITAServiceClass_UUID),
                UUID.fromString(AudioVideoServiceClass_UUID),
                UUID.fromString(SIMAccessServiceClass_UUID),
                UUID.fromString(PnPInformationServiceClass_UUID),
                UUID.fromString(GenericNetworkingServiceClass_UUID),
                UUID.fromString(GenericFileTransferServiceClass_UUID),
                UUID.fromString(GenericAudioServiceClass_UUID),
                UUID.fromString(GenericTelephonyServiceClass_UUID)
    };


    private static HashMap<Integer, String> serviceTypes = new HashMap();
    static {
        // Sample Services.
        serviceTypes.put(BluetoothGattService.SERVICE_TYPE_PRIMARY, "PRIMARY");
        serviceTypes.put(BluetoothGattService.SERVICE_TYPE_SECONDARY, "SECONDARY");
    }

    public static String getServiceType(int type){
        return serviceTypes.get(type);
    }


    //-------------------------------------------
    private static HashMap<Integer, String> charPermissions = new HashMap();
    static {
        charPermissions.put(0, "UNKNOW");
        charPermissions.put(BluetoothGattCharacteristic.PERMISSION_READ, "READ");
        charPermissions.put(BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED, "READ_ENCRYPTED");
        charPermissions.put(BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM, "READ_ENCRYPTED_MITM");
        charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE, "WRITE");
        charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED, "WRITE_ENCRYPTED");
        charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM, "WRITE_ENCRYPTED_MITM");
        charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED, "WRITE_SIGNED");
        charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM, "WRITE_SIGNED_MITM");
    }

    public static String getCharPermission(int permission){
        return getHashMapValue(charPermissions,permission);
    }
    //-------------------------------------------
    private static HashMap<Integer, String> charProperties = new HashMap();
    static {

        charProperties.put(BluetoothGattCharacteristic.PROPERTY_BROADCAST, "BROADCAST");
        charProperties.put(BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS, "EXTENDED_PROPS");
        charProperties.put(BluetoothGattCharacteristic.PROPERTY_INDICATE, "INDICATE");
        charProperties.put(BluetoothGattCharacteristic.PROPERTY_NOTIFY, "NOTIFY");
        charProperties.put(BluetoothGattCharacteristic.PROPERTY_READ, "READ");
        charProperties.put(BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE, "SIGNED_WRITE");
        charProperties.put(BluetoothGattCharacteristic.PROPERTY_WRITE, "WRITE");
        charProperties.put(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE, "WRITE_NO_RESPONSE");
    }

    public static String getCharPropertie(int property){
        return getHashMapValue(charProperties,property);
    }

    //--------------------------------------------------------------------------
    private static HashMap<Integer, String> descPermissions = new HashMap();
    static {
        descPermissions.put(0, "UNKNOW");
        descPermissions.put(BluetoothGattDescriptor.PERMISSION_READ, "READ");
        descPermissions.put(BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED, "READ_ENCRYPTED");
        descPermissions.put(BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED_MITM, "READ_ENCRYPTED_MITM");
        descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE, "WRITE");
        descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED, "WRITE_ENCRYPTED");
        descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED_MITM, "WRITE_ENCRYPTED_MITM");
        descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED, "WRITE_SIGNED");
        descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED_MITM, "WRITE_SIGNED_MITM");
    }

    public static String getDescPermission(int property){
        return getHashMapValue(descPermissions,property);
    }


    private static String getHashMapValue(HashMap<Integer, String> hashMap,int number){
        String result =hashMap.get(number);
        if(TextUtils.isEmpty(result)){
            List<Integer> numbers = getElement(number);
            result="";
            for(int i=0;i<numbers.size();i++){
                result+=hashMap.get(numbers.get(i))+"|";
            }
        }
        return result;
    }

    /**
     * 位运算结果的反推函数10 -> 2 | 8;
     */
    static private List<Integer> getElement(int number){
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < 32; i++){
            int b = 1 << i;
            if ((number & b) > 0)
                result.add(b);
        }

        return result;
    }


    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
