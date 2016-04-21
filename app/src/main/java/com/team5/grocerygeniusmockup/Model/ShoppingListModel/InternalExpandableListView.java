package com.team5.grocerygeniusmockup.Model.ShoppingListModel;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.widget.ExpandableListView;

/**
 * Created by user on 17/03/2016.
 */
public class InternalExpandableListView extends ExpandableListView {
    int intGroupPosition, intChildPosition, intGroupID;

    Context context;
    public InternalExpandableListView(Context context) {
        super(context);
        this.context = context;
    }

    /* http://developer.android.com/reference/android/view/View.MeasureSpec.html
     *
     * A MeasureSpec encapsulates the layout requirements passed from parent to child. Each
     * MeasureSpec represents a requirement for either the width or the height. A MeasureSpec is
     * comprised of a size and a mode.
     *
     * AT_MOST : The child can be as large as it wants up to the specified size.
     *
     * MeasureSpecs are implemented as ints to reduce object allocation. This class is provided to
     * pack and unpack the <size, mode> tuple into the int.
     */

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //widthMeasureSpec = MeasureSpec.makeMeasureSpec(960, MeasureSpec.AT_MOST);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(10000, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
