package cn.walkpos.wpospad.module;

import com.xingy.lib.model.BaseModel;

import org.json.JSONObject;

/**
 * Created by xingyao on 15-9-26.
 */
public class GoodsModule extends BaseModel {
    public String goods_id;
    public String bn;
    public String   pricein;
    public String   priceout;
    public String name;
    public String name_s;
    public String img_src;
    public int    stock;
    public int    up_warn;
    public int    down_warn;
    public double  discount;
    public boolean marketable;
    public String  cat_id;
    public String  cat_name;

    public GoodsModule()
    {
        clear();
    }

    public void clear()
    {
        goods_id = "";
        bn = "";
        pricein = "";
        priceout = "";
        name="";
        name_s = "";
        img_src = "";
        stock = 0;
        up_warn = 0;
        down_warn = 0;
        discount = 1.0f;
        marketable = true;
        cat_id = "";
        cat_name = "";

    }

    /**
     * "goods_id":"97",
     "bn":"G4CB2BBD0A037E",
     "name":"\u82b3\u8349\u96c6 \u7eff\u8336\u5c0f\u7c73\u8349\u7f8e\u773c\u80f6 20g",
     "price":"39.000",
     "store":"99",
     "marketable":"true",
     "img_src":"http:\/\/116.247.87.237:10080\/http:\/\/pic.shopex.cn\/pictures\/eimages\/ec09010dbdb3d7233d4c6711cf22ad89fd664cb910.png"
     * @param json
     */
    public void  parse(JSONObject json)
    {
        if(json==null)
            return;
        goods_id = json.optString("goods_id");
        bn = json.optString("bn");
        name = json.optString("name");
        name_s = json.optString("short_name");
        cat_id = json.optString("cat_id");

        priceout = json.optString("price");
        stock = json.optInt("store");
        discount = json.optDouble("discount",1.0);

        up_warn = json.optInt("up_warn");
        down_warn = json.optInt("down_warn");

        img_src = json.optString("img_src");

        marketable = json.optBoolean("marketable");
        cat_name = json.optString("cat_name");

    }
}
