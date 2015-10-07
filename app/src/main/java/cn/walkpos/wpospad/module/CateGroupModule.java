package cn.walkpos.wpospad.module;

import com.xingy.lib.model.BaseModel;

import java.util.ArrayList;

/**
 * Created by xingyao on 15-9-26.
 */
public class CateGroupModule extends BaseModel {
    public String id;
    public String name;
    public ArrayList<CateItemModule> subCateArray;

    public CateGroupModule()
    {
        clear();
    }

    public void clear()
    {
        id = "";
        name = "";
        if(subCateArray==null)
            subCateArray = new ArrayList<CateItemModule>();
        else
            subCateArray.clear();
    }

}
