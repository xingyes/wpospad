package cn.walkpos.wpospad.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by xingyao on 15-8-25.
 */
public class NoinputEditText extends EditText {

    public NoinputEditText(Context context) {
        super(context);
    }

    public NoinputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public NoinputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onCheckIsTextEditor()
    {
        return true;
    }

    @Override
    public boolean isTextSelectable(){
        return true;
    }

}
