package cn.walkpos.wpospad.module;

import com.xingy.lib.model.BaseModel;

import org.json.JSONObject;

/**
 * Created by xingyao on 15-9-26.
 */
public class BranchInfoModule extends BaseModel {
    public static final int TYPE_SMALL = 0;
    public static final int TYPE_STORE = 1;
    public static String [] opt = {"小店","店铺"};
    public String store_bn;
    public String logo;
    public String store_name;
    public String tel;
    public String addr;
    public String web_url;
    public String brief;
    public boolean tel_print;
    public boolean web_print;
    public boolean brief_print;
    public int type_id;


    public BranchInfoModule()
    {
        clear();
    }

    public void clear()
    {
        store_bn = "";
        logo = "";
        store_name = "";
        tel="";
        addr = "";
        web_url = "";
        brief = "";
        tel_print = true;
        web_print = true;
        brief_print = true;
        type_id = -1;
    }

    public void parse(JSONObject json)
    {
        if(json == null)
            return;
        store_bn = json.optString("store_bn");
        logo = json.optString("logo");
        store_name = json.optString("store_name");
        tel = json.optString("tel");
        addr = json.optString("addr");
        web_url = json.optString("web_url");
        brief = json.optString("brief");
        tel_print = json.optBoolean("tel_print");
        web_print = json.optBoolean("web_print");
        brief_print = json.optBoolean("brief_print");
        type_id = json.optInt("type_id",-1);
    }
}
