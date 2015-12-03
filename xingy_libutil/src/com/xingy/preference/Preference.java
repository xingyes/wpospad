package com.xingy.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.amap.api.maps.model.LatLng;
import com.xingy.util.MyApplication;

public final class Preference 
{
	
	public static final int NEED_ACCESS = 0;
	public static final int ACCESSED	= 1;
	public static final String  DEFAULT_INTERVAL = "60"; //mintue

    /**
     *
     * @return
     */
	public synchronized static Preference getInstance()
	{
		if( null == mSelf )
		{
			mSelf = new Preference();
			mSelf.setContext(MyApplication.app);
		}
		
		return mSelf;
	}
	
	
	public void setConfigTag(String strTag) {
		this.setValue(PREF_CONFIG_TIMETAG, strTag);
	}
	
	public String getConfigTag() {
		return this.getString(PREF_CONFIG_TIMETAG);
	}



	/**
	 * Check whether push message is enabled.
	 * @return
	 */
	public boolean pushMessageEnabled() {
		return 1 == this.getValue(PREF_PUSH_MESSAGE);
	}
	
	public void setPushMessageEnabled(boolean bEnable) {
		this.setValue(PREF_PUSH_MESSAGE, bEnable ? 1 : 0);
	}
	
	public void setPushInterval(int nMinutes) {
		if( nMinutes > 0 ) {
			this.setValue(PREF_PUSH_INTERVAL, nMinutes);
		}
	}
	
	public int getPushInterval() {
		return this.getValue(PREF_PUSH_INTERVAL);
	}


	
	/**
	 * 
	* method Name:setProjVersion    
	* method Description:  
	* @param aVersion   
	* void  
	* @exception   
	* @since  1.0.0
	 */
	public void setProjVersion(int aVersion)
	{
		this.setValue(PREF_PROJ_VERSION, aVersion);
	}
	
	public int getProjVersion()
	{
		return getValue(PREF_PROJ_VERSION);
	}
	

	public void setLastUID(String last_uid)
	{
		this.setValue(PREF_LAST_UID, last_uid);
	}
	public long getLastUID()
	{
		String lastuid = getString(PREF_LAST_UID);
		long uid;
		uid = lastuid.equals("")? 0 : Long.valueOf(lastuid);
		return uid;
	}

    public void setBranchNum(String bn)
    {
        this.setValue(PREF_STOCK_BRANCHNUM, bn);
    }
    public String getBranchNum()
    {
        return getString(PREF_STOCK_BRANCHNUM);
    }

    public void setMPosDevAddress(String addr){this.setValue(PREF_MPOS_DEV_ADDRESS,addr);}
    public String getMPosDevAddress(){return getString(PREF_MPOS_DEV_ADDRESS);}

    public void setBarcodeAccess(int accessValue)
	{
		this.setValue(PREF_BARCODE_ACESS, accessValue);
	}
	public Boolean needToBarcodeAccess()
	{
		return NEED_ACCESS == getValue(PREF_BARCODE_ACESS)? true : false;
	}
	
	public void setCallAccess(int accessValue)
	{
		this.setValue(PREF_CALL_ACESS, accessValue);
	}
	public Boolean needCallAccess()
	{
		return NEED_ACCESS == getValue(PREF_CALL_ACESS)? true : false;
	}
	
	/**
	* method Name:restoreDefault    
	* method Description:  
	* void  
	* @exception   
	* @since  1.0.0
	 */
	public void restoreDefault()
	{
		if ( null == mValMap )
			return ;
		
		for(int nIndex = 0; nIndex < MUTABLE_SIZE; nIndex++)
		{
			mValMap.setValue(PROPERTIES[nIndex], DEFAULTS[nIndex]);
		}
	}
	
	/**
	 * savePreference
	 * Save preference
	 */
	public void savePreference()
	{
		if ( null != mPreferences )
		{
			// Save the items to local preference.
			final int nSize = (null != mValMap ? mValMap.size() : 0);
			if ( nSize > 0 )
			{
				SharedPreferences.Editor pEditor = mPreferences.edit();
				
				for ( int nIdx = 0; nIdx < nSize; nIdx++ )
				{
					ValueMap.Element pElement = mValMap.elementAt(nIdx);
					
					if( null != pElement )
					{
						// Save to local storage.
						pEditor.putString(pElement.getKey(), pElement.getValue());
					}
				}
				
				// Commit it.
				pEditor.commit();
			}
		}
	}
	
	private void setValue(String aKey, String strVal) {
		if( null == mValMap)
			return ;
		
		if(strVal == null) {
			strVal = "";
		}
		mValMap.setValue(aKey, strVal);
	}
	
	private String getString(String aKey) {
		if(aKey == null) {
			aKey = "";
		}
		ValueMap.Element pElement = (null != mValMap ? mValMap.getElement(aKey) : null);
		
		return null != pElement ? pElement.getValue() : "";
	}
	
	/**
	* method Name:setValue    
	* method Description:  
	* @param aKey
	* @param nValue
	* void  
	* @exception   
	* @since  1.0.0
	 */
	private void setValue(String aKey, int nValue)
	{
		if ( null == mValMap )
			return ;
		
		mValMap.setValue(aKey, "" + nValue);
	}
	private void setValue(String aKey, float nValue)
	{
		if ( null == mValMap )
			return ;
		
		mValMap.setValue(aKey, "" + nValue);
	}

	/**
	* method Name:getValue    
	* method Description:  
	* @param aKey
	* @return   
	* boolean  
	* @exception   
	* @since  1.0.0
	 */
	private int getValue(String aKey)
	{
		ValueMap.Element pElement = (null != mValMap ? mValMap.getElement(aKey) : null);
		if ( null == pElement )
			return 0;
		
		return Integer.valueOf(pElement.getValue());
	}
	private float getFloat(String aKey)
	{
		ValueMap.Element pElement = (null != mValMap ? mValMap.getElement(aKey) : null);
		if ( null == pElement )
			return 0.0f;
		
		return Float.valueOf(pElement.getValue());
	}

	/**
	* Create a new Instance Preference.  
	*
	 */
	private Preference()
	{
		initialize();
	}
	
	/**
	* method Name:initialize    
	* method Description:     
	* void  
	* @exception   
	* @since  1.0.0
	 */
	private void initialize()
	{
		mPreferences = null;
		mValMap = null;
	}
	
	/**
	* method Name:setContext    
	* method Description:  
	* @param aContext   
	* void  
	* @exception   
	* @since  1.0.0
	 */
	private void setContext(Context aContext)
	{
		if ( null != aContext )
		{	
			// Load the preference.
			mPreferences = null;
			mPreferences = aContext.getSharedPreferences(PREF_NAME, 0);
			loadPref();
		}
	}
	
	/**
	* method Name:loadPref    
	* method Description:     
	* void  
	* @exception   
	* @since  1.0.0
	 */
	private void loadPref()
	{
		if ( (null == mPreferences) || (null != mValMap) )
			return ;
		
		mValMap = new ValueMap();
		final int nCount = PROPERTIES.length;
		for ( int nIdx = 0; nIdx < nCount; nIdx++ )
		{
			// Get the value from local storage.
			String strVal = mPreferences.getString(PROPERTIES[nIdx], DEFAULTS[nIdx]);
			
			// Save to memory.
			mValMap.addValue(PROPERTIES[nIdx], strVal);
		}
	}
	
	// Member instance.
	private static Preference mSelf = null;
	private SharedPreferences mPreferences;
	private ValueMap          mValMap;
	
	// Preference key.
	public  static final String PREF_NAME            = "ICSON_PREF";
	public  static final String PREF_PUSH_MESSAGE    = "PUSH_MESSAGE";
	public  static final String PREF_CONFIG_TIMETAG  = "CONFIG_TIMETAG";
	public  static final String PREF_PUSH_INTERVAL   = "PUSH_INTERVAL";
	public  static final String PREF_LAST_UID  		 = "LAST_UID";
	public  static final String PREF_BARCODE_ACESS   = "BARCODE_ACESS";
	public  static final String PREF_CALL_ACESS    	 = "CALL_ACESS";
	
	//Project_version code
	public  static final String PREF_PROJ_VERSION  = "PROJ_VERSION";

    public static final String  PREF_STOCK_BRANCHNUM = "STOCK_BRANCHNUM";
    public static final String  PREF_MPOS_DEV_ADDRESS = "MPOS_DEV_ADDRESS";


    private static final String[] PROPERTIES = {
												PREF_PUSH_MESSAGE,
												PREF_CONFIG_TIMETAG,

												PREF_PUSH_INTERVAL,
												PREF_PROJ_VERSION,

												PREF_LAST_UID,
												PREF_BARCODE_ACESS,
												PREF_CALL_ACESS,

                                                PREF_STOCK_BRANCHNUM,
                                                PREF_MPOS_DEV_ADDRESS

												};
	// Default values for the preference.
	private static final int      MUTABLE_SIZE = 0;

	private static final String[] DEFAULTS = {
		"1","",
		DEFAULT_INTERVAL,"0",
		"", "0", "0",
        "S55FFA78EC7F56",""
		};
}
