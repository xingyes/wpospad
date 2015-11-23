package cn.walkpos.wpospad.module;

import com.xingy.lib.model.BaseModel;

import org.json.JSONObject;

/**
 * Created by xingyao on 15-9-26.
 */
public class BCardModule extends BaseModel {

    public String bank_card;
//    public String card_id;
    public String iconurl;
    public String account_bank;
    public String usrname;
    public String cardtype;

    public BCardModule()
    {
        clear();
    }

    public void clear()
    {
        bank_card = "";
//        card_id = "";
        iconurl = "";
        account_bank = "";
        usrname = "";
        cardtype = "";
    }

    public void  parse(JSONObject json)
    {
        if(json==null)
            return;
        bank_card = json.optString("bank_card");
        account_bank = json.optString("account_bank");

//        card_id = json.optString("bn");
        usrname = json.optString("short_name");

    }
}
