package uk.ac.abertay.cmp309.assesment
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShopsAdapter(private val mList: ArrayList<Shop>) : RecyclerView.Adapter<ShopsAdapter.ViewHolder>() {

    //Inner class with initialised values
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.Shop_Image)
        val textViewName: TextView = itemView.findViewById(R.id.ShopName_textView)
        val textViewRating: TextView = itemView.findViewById(R.id.ShopRating_textView)
        val textViewDistance : TextView = itemView.findViewById(R.id.ShopDist_textView)
        val textViewTRating : TextView = itemView.findViewById(R.id.ShopTRating_textView)
    }
    //Create Views(Use inflate)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shop_layout, parent, false)

        return ViewHolder(view)
    }

    //Populate daa in recycler
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val shop: Shop = mList[position]

        // sets the image to the imageview from our itemHolder class
        holder.imageView.setImageURI(Uri.parse(shop.Image))

        // sets the text to the textview from our itemHolder class
        holder.textViewName.text = shop.Name
        holder.textViewRating.text = shop.Rating.toString()
        holder.textViewDistance.text = shop.Distance
        holder.textViewTRating.text = "/5"

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text

}
