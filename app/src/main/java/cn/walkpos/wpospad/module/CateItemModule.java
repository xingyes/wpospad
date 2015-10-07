package cn.walkpos.wpospad.module;

import com.xingy.lib.model.BaseModel;

import java.util.ArrayList;

/**
 * Created by xingyao on 15-9-26.
 */
public class CateItemModule extends BaseModel {
    public String id;
    public String name;


    public CateItemModule()
    {
        clear();
    }

    public void clear()
    {
        id = "";
        name = "";
    }
}
