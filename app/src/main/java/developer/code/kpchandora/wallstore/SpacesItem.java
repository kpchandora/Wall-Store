package developer.code.kpchandora.wallstore;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by kpchandora on 2/2/18.
 */

public class SpacesItem extends RecyclerView.ItemDecoration {

    private int space;

    public SpacesItem(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = space;
    }
}
