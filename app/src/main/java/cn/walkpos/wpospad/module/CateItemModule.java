package cn.walkpos.wpospad.module;

import com.xingy.lib.model.BaseModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by xingyao on 15-9-26.
 */
public class CateItemModule extends BaseModel {
    public static final String CACHEKEY_CATEGORY = "cache_category";
    public String cat_id;
    public String cat_name;
    public ArrayList<CateItemModule> subCateArray;

    public CateItemModule()
    {
        clear();
    }

    public void clear()
    {
        cat_id = "";
        cat_name = "";
        if(subCateArray==null)
            subCateArray = new ArrayList<CateItemModule>();
        else
            subCateArray.clear();
    }

    public void parse(JSONObject json)
    {
        if(null == json)
            return;
        cat_id = json.optString("cat_id");
        cat_name = json.optString("cat_name");
        JSONArray items = json.optJSONArray("item");
        if(null!=items && items.length()>0)
        {
            for(int i=0; i < items.length(); i++)
            {
                CateItemModule sub = new CateItemModule();
                sub.parse(items.optJSONObject(i));
                subCateArray.add(sub);
            }
        }
    }

}
