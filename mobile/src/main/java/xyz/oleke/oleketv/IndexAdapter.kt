package xyz.oleke.oleketv

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.recycler_view_item_1.view.*
import kotlinx.android.synthetic.main.side_bar.view.*

class IndexAdapter<T> (private val myDataset: List<T>, val clickListener: (T) -> Boolean) :
    RecyclerView.Adapter<IndexAdapter.MyViewHolder<T>>()  {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder<T>(val listView: View) : RecyclerView.ViewHolder(listView) {
        fun bind(channel: T, clickListener: (T) -> Boolean) {
            listView.textview_adpater.text = channel.toString()
            listView.setOnClickListener {
                clickListener(channel)
            }

        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IndexAdapter.MyViewHolder<T> {
        // create a new view
        val channelView = LayoutInflater.from(parent.context)
            .inflate(R.layout.side_bar, parent, false)

        return MyViewHolder(channelView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder<T>, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //println("Position: "+position)
        holder.bind(myDataset[position], clickListener)

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}