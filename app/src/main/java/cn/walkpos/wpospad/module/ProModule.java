package cn.walkpos.wpospad.module;

import com.xingy.lib.model.BaseModel;

/**
 * Created by xingyao on 15-9-26.
 */
public class ProModule extends BaseModel {
    public String code;
    public long   pricein;
    public long   priceout;
    public String title;
    public String title_s;
    public String imgurl;
    public int    stock;
    public int    minstock;

    public ProModule()
    {
        clear();
    }

    public void clear()
    {
        code = "";
        pricein = 0;
        priceout = 0;
        title="";
        title_s = "";
        imgurl = "";
        stock = 0;
        minstock = 0;
    }
}
