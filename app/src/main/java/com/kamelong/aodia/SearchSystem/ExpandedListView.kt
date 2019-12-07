package com.kamelong.aodia.SearchSystem

import android.content.Context
import android.view.ViewGroup
import android.view.View.MeasureSpec
import android.opengl.ETC1.getWidth
import android.util.AttributeSet
import android.widget.ListView


class ExpandedListView : ListView {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setListViewHeightBasedOnChildren(this)
    }

    companion object {

        fun setListViewHeightBasedOnChildren(listView: ListView) {
            val listAdapter = listView.getAdapter() ?: return

            var totalHeight = 0
            val desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST)
            for (i in 0 until listAdapter.getCount()) {
                val listItem = listAdapter.getView(i, null, listView)
                if (listItem != null) {
                    listItem!!.measure(desiredWidth, MeasureSpec.UNSPECIFIED)
                    totalHeight += listItem!!.getMeasuredHeight()
                }
            }

            val params = listView.getLayoutParams()
            params.height = totalHeight + listView.getDividerHeight() * (listAdapter.getCount() - 1)
            listView.setLayoutParams(params)
            listView.requestLayout()
        }
    }
}