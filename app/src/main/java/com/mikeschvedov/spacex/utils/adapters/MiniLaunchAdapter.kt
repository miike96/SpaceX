package com.mikeschvedov.spacex.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mikeschvedov.spacex.R
import com.mikeschvedov.spacex.data.database.entities.Launch
import com.mikeschvedov.spacex.data.database.entities.Ship
import javax.inject.Inject

class MiniLaunchAdapter @Inject constructor(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<MiniLaunchAdapter.MiniViewHolder>() {

    var list: MutableList<Launch> = mutableListOf()

    fun interface OnItemClickListener{
        fun onItemClicked(
            item: Launch
        )
    }

    // add new data
    fun submitList(newData: List<Launch>) {
        // we clear the old list
        list.clear()
        // and replace it with the new list
        list.addAll(newData)
        notifyDataSetChanged()
    }

    class MiniViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView =
            itemView.findViewById(R.id.name_text_view)
        var itemLayout: ConstraintLayout =
            itemView.findViewById(R.id.mini_item_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiniViewHolder {
        return MiniViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.single_mini_view_holder,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MiniViewHolder, position: Int) {

        val item = list[position]

        // --- Setting the title --- //
        holder.title.text = item.name

        // -- Sending the clicked item as callback -- //
        holder.itemLayout.setOnClickListener {
            listener.onItemClicked(item)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}
