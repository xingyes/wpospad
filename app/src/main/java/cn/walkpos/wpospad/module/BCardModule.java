package cn.walkpos.wpospad.module;

import com.xingy.lib.model.BaseModel;

import org.json.JSONObject;

/**
 * Created by xingyao on 15-9-26.
 */
public class BCardModule extends BaseModel {

    public String bank_card; //卡号
    public String account_bank; //银行
    public String mobile;

    public String iconurl;
    public String usrname;
    public String cardtype;

    public BCardModule()
    {
        clear();
    }

    public void clear()
    {
        bank_card = "";
        account_bank = "";
        mobile="";

        iconurl = "";
        usrname = "";
        cardtype = "";
    }

    public void  parse(JSONObject json)
    {
        if(json==null)
            return;
        bank_card = json.optString("bank_card");
        account_bank = json.optString("account_bank");
        mobile = json.optString("mobile");

        usrname = json.optString("short_name");
        iconurl = json.optString("iconurl");

    }
}
