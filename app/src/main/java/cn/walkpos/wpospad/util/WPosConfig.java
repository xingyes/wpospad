package cn.walkpos.wpospad.util;

import com.xingy.util.Config;

public class WPosConfig extends Config {

    public static final String URL_API_ALL = "URL_API_ALL";

    //login
    public static final int REQ_LOGIN = 100;
    public static final int REQ_LOGIN_IDENTITY = 101;
    //register
    public static final int   REQ_REGISTER = 201;
    public static final int   REQ_RESET_PASSWD = 202;

    //storeinfo
    public static final int   REQ_BRANCH_INFO = 301;
    public static final int   REQ_MODIFY_BRANCH_INFO = 302;



    //////////Server api configuration.
    //login
    public static final String URL_LOGIN_IDENTITY = "URL_LOGIN_IDENTITY";
    public static final String URL_REGISTER = "URL_REGISTER";
    public static final String URL_CHECK_TOKEN = "URL_CHECK_TOKEN";
    public static final String URL_IMAGE_STREAM_UPLOAD = "URL_IMAGE_STREAM_UPLOAD";
    public static final String URL_CHANGE_NICKNAME = "URL_CHANGE_NICKNAME";
    public static final String URL_CHANGE_PHONE = "URL_CHANGE_PHONE";
    public static final String URL_VERIFYCODE_SMS = "URL_VERIFYCODE_SMS";
    public static final String URL_VERIFY_LOGIN = "URL_VERIFY_LOGIN";


    public static final String URL_GET_COLLECT = "URL_GET_COLLECT";
    public static final String URL_GET_BLOG = "URL_GET_BLOG";
    public static final String URL_GET_LOOK = "URL_GET_LOOK";

    //shopping
    public static final String URL_PRODUCT_DETAIL = "URL_PRODUCT_DETAIL";
    public static final String URL_CREATE_ORDER = "URL_CREATE_ORDER";
    //order
    public static final String URL_ORDER_LIST = "URL_ORDER_LIST";
    public static final String URL_ORDER_DETAIL = "URL_ORDER_DETAIL";
    public static final String URL_PAY_ORDER = "URL_PAY_ORDER";


    public static final String URL_BRAND_INFO = "URL_BRAND_INFO";

    public static final String URL_FULL_DISTRICT = "URL_FULL_DISTRICT";

    public static final String O2_SEARCH_PARAMS = "O2_SEARCH_PARAMS";
    public static final String URL_BASIC_PARAMS = "URL_BASIC_PARAMS";

    public static final String URL_SEARCH = "URL_SEARCH";
    public static final String URL_SET_INFO = "URL_SET_INFO";


    public static final String URL_QUERY_SUGGEST = "URL_QUERY_SUGGEST";

    //address
    public static final String URL_GET_ADDRESSLIST = "URL_GET_ADDRESSLIST";
    public static final String URL_EDIT_ADDRESS = "URL_EDIT_ADDRESS";
    public static final String URL_DEL_ADDRESS = "URL_DEL_ADDRESS";
    public static final String URL_ADD_ADDRESS = "URL_ADD_ADDRESS";

    //fav
    public static final String URL_MODIFY_FAV = "URL_MODIFY_FAV";
    public static final String URL_FAV_LIST = "URL_FAV_LIST";


    //coupon
    public static final String URL_GET_COUPON = "URL_GET_COUPON";
    public static final String URL_COUPON_LIST = "URL_COUPON_LIST";


    //weibo about
	public static final String WB_APP_KEY = "3721412749";
	public static final String WB_SCOPE = "email,direct_messages_read,direct_messages_write,"
				+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read," 
				+ "follow_app_official_microblog," + "invitation_write";
	public static final String REDIRECT_URL = "http://api.hello-app.cn";
	
	
}
