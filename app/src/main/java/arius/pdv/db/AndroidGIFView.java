package arius.pdv.db;

import java.io.InputStream;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import br.com.arius.pdvarius.R;

public class AndroidGIFView extends View {

    public Movie mMovie;
    public long movieStart;

    public AndroidGIFView(Context context) {
        super(context);
        initializeView();
    }

    public AndroidGIFView(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray vAttr = getContext().obtainStyledAttributes(attrs, R.styleable.AndroidGIFView);
        gifId = vAttr.getResourceId(R.styleable.AndroidGIFView_src,0);
        initializeView();
    }

    public AndroidGIFView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray vAttr = getContext().obtainStyledAttributes(attrs, R.styleable.AndroidGIFView);
        gifId = vAttr.getResourceId(R.styleable.AndroidGIFView_src,0);
        initializeView();
    }

    private void initializeView() {
//R.drawable.loader - our animated GIF
        InputStream is = getContext().getResources().openRawResource(gifId);
        mMovie = Movie.decodeStream(is);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);
        long now = android.os.SystemClock.uptimeMillis();
        if (movieStart == 0) {
            movieStart = now;
        }
        if (mMovie != null) {
            int relTime = (int) ((now - movieStart) % mMovie.duration());
            mMovie.setTime(relTime);
            mMovie.draw(canvas,
                    (getWidth() / 2) - ((mMovie.width() / 2)),
                    (getHeight() / 2) - (mMovie.height() / 2));
            this.invalidate();
        }
    }

    private int gifId;
    public void setGIFResource(int resId) {
        this.gifId = resId;
        initializeView();
    }

    public int getGIFResource() {
        return this.gifId;
    }
}
