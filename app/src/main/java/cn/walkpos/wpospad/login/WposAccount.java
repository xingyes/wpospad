package cn.walkpos.wpospad.login;

import org.json.JSONObject;

/**
 * Created by xingyao on 15-10-16.
 */
public class WposAccount {
    public String user_id;
    public String store_id;
    public boolean status;
    public String name;
    public String passwd;
    public String bn;   //店员编号
    public String logo;
    public String mobile;
    public String  card_number;
    public boolean bdiscount;
    public boolean bmanage;
    public String token;

    public boolean bSuperAdmin; //管理员

    public WposAccount(){clear();}

    public void clear()
    {
        user_id = "";
        store_id= "";
        status= false;
        name= "";
        bn= "";   //店员编号
        logo= "";
        mobile= "";
        card_number= "";
        bdiscount= false;
        bmanage = false;
        token= "";
        passwd="";

        bSuperAdmin = false;
    }


    public void parse(JSONObject json)
    {
        if(json==null)
            return;
        bSuperAdmin = json.optBoolean("super");
        token = json.optString("token");

        user_id = json.optString("user_id");
        store_id = json.optString("store_id");
        name = json.optString("name");
        card_number = json.optString("card_number");
        bn = json.optString("bn");
        logo = json.optString("logo");
        mobile = json.optString("mobile");
        card_number = json.optString("card_number");

        status = json.optBoolean("status");

        bdiscount = json.optBoolean("discount");
        bmanage = json.optBoolean("manage");

        passwd = json.optString("passwd");

    }
}
