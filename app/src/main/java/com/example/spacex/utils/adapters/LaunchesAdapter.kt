package com.example.spacex.utils.adapters


import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.spacex.R
import com.example.spacex.data.database.entities.Launch
import com.example.spacex.utils.fromUnixToFormatted
import com.squareup.picasso.Picasso
import javax.inject.Inject

class LaunchesAdapter @Inject constructor(private val listener: LaunchesAdapter.OnItemClickListener) :
    RecyclerView.Adapter<LaunchesAdapter.LaunchesViewHolder>() {

    var list: MutableList<Launch> = mutableListOf()

    fun interface OnItemClickListener{
        fun onItemClicked(
            item: Launch
        )
    }

    // add new data
    fun setNewData(newData: List<Launch>) {
        // passing the new and old list into the callback
        val diffCallback = DiffUtilCallbackLaunches(list, newData)
        // we get the result
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        // we clear the old list
        list.clear()
        // and replace it with the new list
        list.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    class LaunchesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageview: ImageView =
            itemView.findViewById(R.id.profile_image)
        var title: TextView =
            itemView.findViewById(R.id.title_text_view)
        var date: TextView =
            itemView.findViewById(R.id.date_text_view)
        var itemLayout: ConstraintLayout =
            itemView.findViewById(R.id.item_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaunchesViewHolder {
        return LaunchesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_view_holder,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: LaunchesViewHolder, position: Int) {

        val item = list[position]

        // --- Setting the image --- //
        val imageUrl = item.links?.patch?.large
        if (imageUrl != null) {
            Picasso.get().load(Uri.parse(imageUrl))
                .placeholder(R.drawable.loading)
                .error(R.drawable.ship)
                .into(holder.imageview)
        }

        // --- Setting the title --- //
        holder.title.text = item.name

        // --- Setting the date --- //
        val dateFormatted = item.date_unix.fromUnixToFormatted()
        holder.date.text = dateFormatted

        // -- Sending the clicked item as callback -- //
        holder.itemLayout.setOnClickListener {
            listener.onItemClicked(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}


class DiffUtilCallbackLaunches(
    private val oldList: List<Launch>,
    private val newList: List<Launch>
) :
    DiffUtil.Callback() {

    // old size
    override fun getOldListSize(): Int = oldList.size

    // new list size
    override fun getNewListSize(): Int = newList.size

    // if items are same
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.name == newItem.name
    }

    // check if contents are same
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return oldItem == newItem
    }
}

