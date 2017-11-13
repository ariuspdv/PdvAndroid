package br.com.arius.pdvarius;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Arius on 09/11/2017.
 */

public class TesteGroupNavigation extends ViewGroup {

    private class ItemNavigator extends AppCompatTextView implements View.OnClickListener {

        private Object object;

        public ItemNavigator(Context context) {
            super(context);
            init();
        }

        public ItemNavigator(Context context, Object object) {
            super(context);
            this.object = object;
            init();
        }

        public ItemNavigator(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public ItemNavigator(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init(){
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lparams.gravity = Gravity.CENTER;
            lparams.weight = 1;
            lparams.setMargins(100,100,100,100);
            this.setLayoutParams(lparams);
            this.setTypeface(Typeface.DEFAULT_BOLD);
            this.setPadding(dp2px(10),0,dp2px(10),0);
            this.setTextSize(dp2px(15));
            this.setTextColor(Color.WHITE);
            this.setBackgroundResource(R.drawable.button_orange_event);
            setOnClickListener(this);
        }

        public int dp2px(int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.getResources().getDisplayMetrics());
        }

        @Override
        public void onClick(View v) {
            TesteGroupNavigation pnlNavigator = (TesteGroupNavigation) this.getParent();
            for(int i = pnlNavigator.getChildCount()-1; i >= 0; i--){
                if (!((TextView) v).getText().toString().equals(((TextView) pnlNavigator.getChildAt(i)).getText().toString())){
                    if (itemNavigatorAcoes != null)
                        itemNavigatorAcoes.onClickItemNavigator(object);

                    pnlNavigator.removeView(pnlNavigator.getChildAt(i));
                } else
                    break;
            }
        }
    }

    private ItemNavigatorAcoes itemNavigatorAcoes;
    private int line_height;

    public void setItemNavigatorAcoes(ItemNavigatorAcoes itemNavigatorAcoes) {
        this.itemNavigatorAcoes = itemNavigatorAcoes;
    }

    public static class LayoutParams extends MarginLayoutParams {

        public final int horizontal_spacing;
        public final int vertical_spacing;

        /**
         * @param horizontal_spacing Pixels between items, horizontally
         * @param vertical_spacing Pixels between items, vertically
         */
        public LayoutParams(int horizontal_spacing, int vertical_spacing) {
            super(0, 0);
            this.horizontal_spacing = horizontal_spacing;
            this.vertical_spacing = vertical_spacing;
        }
    }

    public interface ItemNavigatorAcoes{
        void onClickItemNavigator(Object object);
    };

    public void limparItemNavigator(){
        for(int i = getChildCount()-1; i >= 0; i--) {
            removeView(getChildAt(i));
        };
    };

    public TesteGroupNavigation(Context context) {
        super(context);
    }

    public TesteGroupNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);

        final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        final int count = getChildCount();
        int line_height = 0;

        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        int childHeightMeasureSpec;
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
        } else {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = new LayoutParams(10,10);
                child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), childHeightMeasureSpec);
                final int childw = child.getMeasuredWidth();
                line_height = Math.max(line_height, child.getMeasuredHeight() + lp.vertical_spacing);

                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }

                xpos += childw + lp.horizontal_spacing;
            }
        }
        this.line_height = line_height;

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = ypos + line_height;

        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            if (ypos + line_height < height) {
                height = ypos + line_height;
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(1, 1); // default of 1px spacing
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return true;
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int width = r - l;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                final LayoutParams lp = new LayoutParams(10,10);
                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                xpos += childw + lp.horizontal_spacing;
            }
        }
    }

    public void incluirItemNavegacao(String label, Object object){
        ItemNavigator item = new ItemNavigator(getContext(),object);
        item.setText(label);
        this.addView(item);
    }

}
