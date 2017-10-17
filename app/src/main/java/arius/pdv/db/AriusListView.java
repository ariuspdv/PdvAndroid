package arius.pdv.db;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SwipeListView.SwipeMenu;
import SwipeListView.SwipeMenuItem;
import SwipeListView.SwipeMenuLayout;
import SwipeListView.SwipeMenuView;
import arius.pdv.core.AppContext;
import arius.pdv.core.Entity;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.core.GenericDao;
import br.com.arius.pdvarius.R;

/**
 * Created by Arius on 04/09/2017.
 */

public class AriusListView extends ListView implements SwipeMenuView.OnSwipeItemClickListener{

    private Entity entity;
    private GenericDao<?> genericDao;
    private int layout_arius;
    private Map<Integer, String> campos = new HashMap<>();
    private String entity_listview;
    private boolean montaAdapter = false;
    private List<?> dataSource = null;
    private boolean swipe_delete = false;

    private static final int TOUCH_STATE_NONE = 0;
    private static final int TOUCH_STATE_X = 1;
    private static final int TOUCH_STATE_Y = 2;

    public static final int DIRECTION_LEFT = 1;
    //public static final int DIRECTION_RIGHT = -1;
    private int mDirection = 1;//swipe from right to left by default

    private int MAX_Y = 5;
    private int MAX_X = 3;
    private float mDownX;
    private float mDownY;
    private int mTouchState;
    private int mTouchPosition;
    private SwipeMenuLayout mTouchView;
    private OnSwipeListener mOnSwipeListener;

    private SwipeMenu mMenu;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private OnMenuStateChangeListener mOnMenuStateChangeListener;
    private Interpolator mCloseInterpolator;
    private Interpolator mOpenInterpolator;


    public AriusListView(Context context) {
        super(context);
        init();
    }

    public AriusListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    public AriusListView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

        final TypedArray vAttr = getContext().obtainStyledAttributes(attrs, R.styleable.AriusListView);

        setSwipe_Delete(vAttr.getBoolean(R.styleable.AriusListView_swipe_delete,false));

        setEntity_listview(vAttr.getString(R.styleable.AriusListView_entity_listview));

        setLayout_arius(vAttr.getResourceId(R.styleable.AriusListView_layout_arius_listview,0));

        setSwipeDirection(DIRECTION_LEFT);

        int idCampos = vAttr.getResourceId(R.styleable.AriusListView_campos_exibir_listview,0);
        if (idCampos != 0) {
            TypedArray campos = vAttr.getResources().obtainTypedArray(idCampos);
            for (int i = 0; campos.length() > i; i++) {
                int idaux = campos.getResourceId(i, -1);
                if (idaux != 0) {
                    TypedArray tela = campos.getResources().obtainTypedArray(idaux);
                    String[] tabela = campos.getResources().getStringArray(idaux);
                    for (int t = 0; tela.length() > t; t = t + 2) {
                        this.campos.put(tela.getResourceId(t+1, -1), tabela[t]);
                    }
                }
            }
        }

        setMontaAdapter(vAttr.getBoolean(R.styleable.AriusListView_montaAdapter,false));

        montaAdapter();
    }

    public SwipeMenu getMenu() {
        return mMenu;
    }

    public void setEntity_listview(String entity_listview) {
        this.entity_listview = entity_listview;

        try {

            entity = (Entity) Class.forName("arius.pdv.base." + entity_listview).newInstance();
            genericDao = (GenericDao<?>) Class.forName("arius.pdv.base." + entity_listview + "Dao").newInstance();

        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (InstantiationException x){
            x.printStackTrace();
        } catch (IllegalAccessException y){
            y.printStackTrace();
        }

        montaAdapter();
    }

    public void setLayout_arius(int layout_arius) {
        this.layout_arius = layout_arius;
        montaAdapter();
    }

    public void setSwipe_Delete(Boolean swipe_delete){
        if (swipe_delete){
            if (mMenu == null)
                mMenu = new SwipeMenu(getContext());

            for(SwipeMenuItem litem : mMenu.getMenuItems()){
                if (litem.getId() == SwipeMenuItem.getBtn_delete())
                    return;
            }

            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            // set item width
            deleteItem.setWidth(dp2px(90));
            // set a icon
            deleteItem.setIcon(R.mipmap.ic_action_discard);

            deleteItem.setId(SwipeMenuItem.getBtn_delete());

            // add to menu
            mMenu.addMenuItem(deleteItem);
        } else {
            if (mMenu != null)
                mMenu = null;
        }
    }

    public boolean isSwipe_delete() {
        return swipe_delete;
    }

    public void setCampos_Exibir(Map<Integer, String> campos_exibir) {
        this.campos = campos_exibir;
        montaAdapter();
    }

    public AriusCursorAdapter getAriusCursorAdapter() {
        return (AriusCursorAdapter) getAdapter();
    }

    public void setMontaAdapter(boolean montaAdapter) {
        this.montaAdapter = montaAdapter;
        montaAdapter();
    }

    public void setDataSource(List<?> dataSource) {
        this.dataSource = dataSource;
        montaAdapter();
    }

    public void setGenericDao(GenericDao<?> genericDao){
        this.genericDao = genericDao;
    }

    public void setEntity(Entity entity){
        this.entity = entity;
    }

    private void init() {
        MAX_X = dp2px(MAX_X);
        MAX_Y = dp2px(MAX_Y);
        mTouchState = TOUCH_STATE_NONE;
    }

    private void montaAdapter(){
        AriusCursorAdapter adapter = null;
        if ((genericDao != null && entity != null && this.layout_arius != 0 &&
                campos.size() > 0) || (genericDao == null && entity == null && this.layout_arius != 0 && campos.size() > 0)) {
            if (dataSource == null && montaAdapter) {
                //Criado o try abaixo para quando a tabela não pesquisar por cache buscar no banco.
                // a rotina abaixo é para listar todos os dados da tabela sem filtro
                try {
                    adapter = new AriusCursorAdapter(getContext(), this.layout_arius, 0,
                                campos,
                                AppContext.get().getDao(genericDao.getClass()).listCache(new FuncionaisFilters() {
                                    @Override
                                    public boolean test(Object p) {
                                        return true;
                                    }
                                }));
                } catch (Exception e) {
                        adapter = new AriusCursorAdapter(getContext(), this.layout_arius, 0,
                                campos,
                                AppContext.get().getDao(genericDao.getClass()).listDatabase("1=1"));
                }
            } else {
                if (this.dataSource != null)
                    adapter = new AriusCursorAdapter(getContext(), this.layout_arius, 0,
                            campos,
                            dataSource);
            }

            setAdapter(adapter);
        }
    }

    @Override
    public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
        if (index == SwipeMenuItem.getBtn_delete())
            itemClickDelete(view, menu, index);
        else
            if (mOnMenuItemClickListener != null) {
                mOnMenuItemClickListener.onMenuItemClick(view.getPosition(), menu, index);
            }
    }

    private void itemClickDelete(SwipeMenuView view, SwipeMenu menu, int index){
        if (index == SwipeMenuItem.getBtn_delete()){
            Entity entity = (Entity) (getAdapter().getItem(view.getPosition()));
            getAriusCursorAdapter().remove(getAdapter().getItem(view.getPosition()));
            if (getAriusCursorAdapter().getAfterDataControll() != null)
                getAriusCursorAdapter().getAfterDataControll().afterDelete(entity);
            if (genericDao != null)
                AppContext.get().getDao(genericDao.getClass()).delete(entity);

            if (isStackFromBottom() && getCount() < 4){
                setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
                setStackFromBottom(false);
            }
        }
    }

    public void setOnMenuItemClickListener(
            OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
//        super.setAdapter(new SwipeMenuAdapter(getContext(), adapter) {
//            @Override
//            public void createMenu(SwipeMenu menu) {
//                if (mMenuCreator != null) {
//                    mMenuCreator.create(menu);
//                }
//            }
//
//            @Override
//            public void onItemClick(SwipeMenuView view, SwipeMenu menu,
//                                    int index) {
//                boolean flag = false;
//                if (mOnMenuItemClickListener != null) {
//                    flag = mOnMenuItemClickListener.onMenuItemClick(
//                            view.getPosition(), menu, index);
//                }
//                if (mTouchView != null && !flag) {
//                    mTouchView.smoothCloseMenu();
//                }
//            }
//        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null)
            return super.onTouchEvent(ev);
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int oldPos = mTouchPosition;
                mDownX = ev.getX();
                mDownY = ev.getY();
                mTouchState = TOUCH_STATE_NONE;

                mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

                if (mTouchPosition == oldPos && mTouchView != null
                        && mTouchView.isOpen() && mMenu != null) {
                    mTouchState = TOUCH_STATE_X;
                    mTouchView.onSwipe(ev);
                    return true;
                }

                View view = getChildAt(mTouchPosition - getFirstVisiblePosition());

                if (mTouchView != null && mTouchView.isOpen()) {
                    mTouchView.smoothCloseMenu();
                    mTouchView = null;
                    // return super.onTouchEvent(ev);
                    // try to cancel the touch event
                    MotionEvent cancelEvent = MotionEvent.obtain(ev);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                    onTouchEvent(cancelEvent);
                    if (mOnMenuStateChangeListener != null) {
                        mOnMenuStateChangeListener.onMenuClose(oldPos);
                    }
                    return true;
                }
                if (view instanceof SwipeMenuLayout) {
                    mTouchView = (SwipeMenuLayout) view;
                    mTouchView.setSwipeDirection(mDirection);
                }
                if (mTouchView != null && mMenu != null) {
                    mTouchView.onSwipe(ev);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = Math.abs((ev.getY() - mDownY));
                float dx = Math.abs((ev.getX() - mDownX));
                if (mTouchState == TOUCH_STATE_X) {
                    if (mTouchView != null && mMenu != null) {
                        mTouchView.onSwipe(ev);
                    }
                    getSelector().setState(new int[]{0});
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                } else if (mTouchState == TOUCH_STATE_NONE) {
                    if (Math.abs(dy) > MAX_Y) {
                        mTouchState = TOUCH_STATE_Y;
                    } else if (dx > MAX_X) {
                        mTouchState = TOUCH_STATE_X;
                        if (mOnSwipeListener != null) {
                            mOnSwipeListener.onSwipeStart(mTouchPosition);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_X) {
                    if (mTouchView != null && mMenu != null) {
                        boolean isBeforeOpen = mTouchView.isOpen();
                        mTouchView.onSwipe(ev);
                        boolean isAfterOpen = mTouchView.isOpen();
                        if (isBeforeOpen != isAfterOpen && mOnMenuStateChangeListener != null) {
                            if (isAfterOpen) {
                                mOnMenuStateChangeListener.onMenuOpen(mTouchPosition);
                            } else {
                                mOnMenuStateChangeListener.onMenuClose(mTouchPosition);
                            }
                        }
                        if (!isAfterOpen) {
                            mTouchPosition = -1;
                            mTouchView = null;
                        }
                    }
                    if (mOnSwipeListener != null) {
                        mOnSwipeListener.onSwipeEnd(mTouchPosition);
                    }
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setCloseInterpolator(Interpolator interpolator) {
        mCloseInterpolator = interpolator;
    }

    public void setOpenInterpolator(Interpolator interpolator) {
        mOpenInterpolator = interpolator;
    }

    public Interpolator getOpenInterpolator() {
        return mOpenInterpolator;
    }

    public Interpolator getCloseInterpolator() {
        return mCloseInterpolator;
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

    public static interface OnSwipeListener {
        void onSwipeStart(int position);

        void onSwipeEnd(int position);
    }

    public static interface OnMenuItemClickListener {
        boolean onMenuItemClick(int position, SwipeMenu menu, int index);
    }

    public static interface OnMenuStateChangeListener {
        void onMenuOpen(int position);

        void onMenuClose(int position);
    }

    public void setSwipeDirection(int direction) {
        mDirection = direction;
    }
}
