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

class LaunchesAdapter @Inject constructor(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<LaunchesAdapter.LaunchesViewHolder>() {

    private var list: MutableList<Launch> = mutableListOf()

    fun setNewData(newData: List<Launch>) {
        val diffCallback = DiffUtilCallbackLaunches(list, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        list.clear()
        list.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    fun interface OnItemClickListener {
        fun onItemClicked(item: Launch)
    }

    inner class LaunchesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.profile_image)
        var title: TextView = itemView.findViewById(R.id.title_text_view)
        var date: TextView = itemView.findViewById(R.id.date_text_view)
        var itemLayout: ConstraintLayout = itemView.findViewById(R.id.item_layout)

        fun bind(item: Launch) {
            // Setting the image
            item.links?.patch?.large?.let { imageUrl ->
                Picasso.get().load(Uri.parse(imageUrl))
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.ship)
                    .into(imageView)
            }

            // Setting the title
            title.text = item.name

            // Setting the date
            val dateFormatted = item.date_unix.fromUnixToFormatted()
            date.text = dateFormatted

            // Sending the clicked item as callback
            itemLayout.setOnClickListener {
                listener.onItemClicked(item)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaunchesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_holder, parent, false)
        return LaunchesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LaunchesViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
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

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
