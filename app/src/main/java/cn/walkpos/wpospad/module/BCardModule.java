package cn.walkpos.wpospad.module;

import com.xingy.lib.model.BaseModel;

import org.json.JSONObject;

/**
 * Created by xingyao on 15-9-26.
 */
public class BCardModule extends BaseModel {

    public String cardcode;
    public String card_id;
    public String iconurl;
    public String bandname;
    public String usrname;
    public String cardtype;

    public BCardModule()
    {
        clear();
    }

    public void clear()
    {
        cardcode = "";
        card_id = "";
        iconurl = "";
        bandname = "";
        usrname = "";
        cardtype = "";
    }

    public void  parse(JSONObject json)
    {
        if(json==null)
            return;
        cardcode = json.optString("cardcode");
        card_id = json.optString("bn");
        bandname = json.optString("name");
        usrname = json.optString("short_name");

    }
}
