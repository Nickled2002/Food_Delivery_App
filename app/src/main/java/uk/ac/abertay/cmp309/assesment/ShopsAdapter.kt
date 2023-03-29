package uk.ac.abertay.cmp309.assesment
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val mList: List<Shop>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shop_layout, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val Shop = mList[position]

        // sets the image to the imageview from our itemHolder class

        holder.imageView.setImageURI(Uri.parse(Shop.Image))

        // sets the text to the textview from our itemHolder class
        holder.textViewName.text = Shop.Name
        holder.textViewRating.text = Shop.Rating
        holder.textViewDistance.text = Shop.Distance

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.Shop_Image)
        val textViewName: TextView = itemView.findViewById(R.id.ShopName_textView)
        val textViewRating: TextView = itemView.findViewById(R.id.ShopRating_textView)
        val textViewDistance : TextView = itemView.findViewById(R.id.ShopDist_textView)
    }
}
