/*
*@author: Ogunleke Abiodun
* A ViewModel to display list of channels in the RecyclerView
 */
package xyz.oleke.oleketv

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycler_view_item_1.view.*


/*
    Class definition implementing the RecyclerView Adaper
 */
class ChannelAdapter(private val myDataset: List<Model.Channel>, val clickListener: (Model.Channel) -> Boolean) :
    RecyclerView.Adapter<ChannelAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val channelView: ViewGroup) : RecyclerView.ViewHolder(channelView) {
        fun bind(channel: Model.Channel, clickListener: (Model.Channel) -> Boolean) {
            channelView.textView1.text = channel.name
            //println(channel.getName())
            var logo = channel.logo
            if(logo=="")
                logo = "https://cdn2.iconfinder.com/data/icons/pictograms-3/512/25-512.png"
            Glide.with(channelView.context).load(logo).into(channelView.imageView1)
            channelView.setOnClickListener {
                clickListener(channel)
            }

        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChannelAdapter.MyViewHolder {
        // create a new view
        val channelView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item_1, parent, false) as ViewGroup

        return MyViewHolder(channelView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //println("Position: "+position)
        holder.bind(myDataset[position], clickListener)

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}