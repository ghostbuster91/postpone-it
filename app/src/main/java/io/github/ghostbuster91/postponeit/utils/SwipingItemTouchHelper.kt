package io.github.ghostbuster91.postponeit.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import io.github.ghostbuster91.postponeit.R

class SwipingItemTouchHelper(context: Context, private val onSwiped: (Int) -> Unit) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val p = Paint().apply {
        color = ContextCompat.getColor(context, R.color.black)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) = onSwiped(viewHolder.adapterPosition)

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView
            if (dX < 0) {
                c.drawRect(itemView.right + dX, itemView.top.toFloat(),
                        itemView.right.toFloat(), itemView.bottom.toFloat(), p)
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
}