

package com.displayfort.textutils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;


/**
 * @author husains
 */
public class TypefaceEditTextBrandenReg extends EditText {

    public TypefaceEditTextBrandenReg(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TypefaceEditTextBrandenReg(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TypefaceEditTextBrandenReg(Context context) {
        super(context);
        init();
    }

    private void init() {
        try {

            if (!isInEditMode()) {
                Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/futura_medium_bt.ttf");
                setTypeface(tf);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//

}
