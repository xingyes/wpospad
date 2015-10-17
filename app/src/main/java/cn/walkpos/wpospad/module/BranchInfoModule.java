package cn.walkpos.wpospad.module;

import com.xingy.lib.model.BaseModel;

import org.json.JSONObject;

/**
 * Created by xingyao on 15-9-26.
 */
public class BranchInfoModule extends BaseModel {
    public String store_bn;
    public String logo;
    public String store_name;
    public String tel;
    public String addr;
    public String web_url;
    public String brief;
    public boolean print;


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
        print = false;
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
        print = json.optBoolean("print");
    }
}
