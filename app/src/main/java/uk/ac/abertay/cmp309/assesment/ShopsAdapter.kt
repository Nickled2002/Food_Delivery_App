package uk.ac.abertay.cmp309.assesment
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors

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
        if (shop.Image != null) {
            val executor = Executors.newSingleThreadExecutor()
            holder.imageView.setImageURI(Uri.parse(shop.Image))
            val handler = Handler(Looper.getMainLooper())
            var image: Bitmap? = null
            executor.execute {

                // Image URL
                //val imageURL = shop.Image

                // Tries to get the image and post it in the ImageView
                // with the help of Handler
                try {
                    val `in` = java.net.URL(shop.Image).openStream()
                    image = BitmapFactory.decodeStream(`in`)

                    // Only for making changes in UI
                    handler.post {
                        holder.imageView.setImageBitmap(image)
                    }
                }

                // If the URL doesnot point to
                // image or any other kind of failure
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // sets the text to the textview from our itemHolder class
        holder.textViewName.text = shop.Name
        holder.textViewRating.text = shop.Rating.toString()
        holder.textViewDistance.text = shop.Distance
        holder.textViewTRating.text = "/5"
        //holder.btnStore.setOnClickListener {}



    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text

}
