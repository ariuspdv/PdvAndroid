package SwipeListView;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arius on 04/09/2017.
 */

public class SwipeMenu {
    private Context mContext;
    private List<SwipeMenuItem> mItems;
    private int mViewType;

    public SwipeMenu(Context context) {
        mContext = context;
        mItems = new ArrayList<SwipeMenuItem>();
    }

    public Context getContext() {
            return mContext;
        }

    public void addMenuItem(SwipeMenuItem item) {
        //O item delete, tem que ser sempre o ultimo para aparecer como primeiro na liste
        //e como é feito interno dentro do AriusListView, aqui é reorganizado
        SwipeMenuItem vAux = null;

        if (item.getId() != SwipeMenuItem.getBtn_delete())
            for (SwipeMenuItem sitem : mItems)
                if (sitem.getId() == SwipeMenuItem.getBtn_delete()){
                    vAux = sitem;
                    mItems.remove(sitem);
                }
        mItems.add(item);

        if (vAux != null)
            mItems.add(vAux);
    }

    public void removeMenuItem(SwipeMenuItem item) {
            mItems.remove(item);
        }

    public List<SwipeMenuItem> getMenuItems() {
            return mItems;
        }

    public SwipeMenuItem getMenuItem(int index) {
            return mItems.get(index);
        }

    public void setViewType(int viewType) {
            this.mViewType = viewType;
        }

    public int getViewType() {
        return mViewType;
    }

}
