package arius.pdv.db;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import br.com.arius.pdvarius.R;

/**
 * Created by Arius on 09/11/2017.
 */

public class AriusProdutoCategoriaNavigator extends AppCompatTextView{

    int layout_width;
    int layout_height;
    int layout_marginBottom;
    int layout_marginTop;

    public AriusProdutoCategoriaNavigator(Context context) {
        super(context);
        init();
    }

    public AriusProdutoCategoriaNavigator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

        String height = attrs.getAttributeValue("android", "layout_height");

        String height2 = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");

        String margem = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_margin");

        String margemtop = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_marginBottom");

//        //check attributes you need, for example all paddings
//        int [] attributes = new int [] {android.R.attr.layout_width,
//                                        android.R.attr.layout_height,
//                                        android.R.attr.layout_marginBottom,
//                                        android.R.attr.layout_marginTop};
//
//        //then obtain typed array
//        TypedArray arr = context.obtainStyledAttributes(attrs, attributes);
//
//        //and get values you need by indexes from your array attributes defined above
//        layout_width = arr.getDimensionPixelOffset(0, -1);
////        /layout_height = arr.getDimensionPixelOffset(1, -1);
//        layout_marginBottom = arr.getDimensionPixelOffset(2, -1);
//        layout_marginTop = arr.getDimensionPixelOffset(3, -1);
//
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AriusProdutoCategoriaNavigator);
//        float padding = a.getDimension(R.styleable.AriusProdutoCategoriaNavigator_android_layout_marginBottom, 0);
//
//        boolean hasPadding = a.hasValue(R.styleable.AriusProdutoCategoriaNavigator_android_layout_marginBottom);

    }

    public AriusProdutoCategoriaNavigator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LinearLayout.LayoutParams lparams =
                new LinearLayout.LayoutParams(dp2px(100), dp2px(100));
                //ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lparams.gravity = Gravity.CENTER;
        lparams.weight = 1;
        this.setLayoutParams(lparams);
        this.setTypeface(Typeface.DEFAULT_BOLD);
        this.setPadding(dp2px(10),0,dp2px(10),0);
        this.setTextSize(dp2px(15));
        this.setTextColor(Color.WHITE);
        this.setBackgroundResource(R.drawable.button_orange_event);
    }

    public int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.getResources().getDisplayMetrics());
    }

}
