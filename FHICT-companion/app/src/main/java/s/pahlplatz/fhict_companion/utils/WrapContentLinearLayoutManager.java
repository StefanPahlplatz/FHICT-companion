package s.pahlplatz.fhict_companion.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by Stefan on 2-12-2016.
 * <p>
 * Custom LinearLayoutManager to prevent error:
 * <p>
 * java.lang.IndexOutOfBoundsException: Inconsistency detected.
 * Invalid view holder adapter positionViewHolder.
 */

public class WrapContentLinearLayoutManager extends LinearLayoutManager {
    private static final String TAG = WrapContentLinearLayoutManager.class.getSimpleName();

    public WrapContentLinearLayoutManager(final Context context, final int orientation, final boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(final RecyclerView.Recycler recycler, final RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException ex) {
            Log.e(TAG, "java.lang.IndexOutOfBoundsException: Inconsistency detected.");
        }
    }
}
