package uk.ac.abertay.cmp309.assesment
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors

class ShopsAdapter(private val mList: ArrayList<Shop>, val onClickListener: (View,Shop) -> Unit) : RecyclerView.Adapter<ShopsAdapter.ViewHolder>() {

    //Inner class with initialised values
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val imageView: ImageView = itemView.findViewById(R.id.Shop_Image)
        val textViewName: TextView = itemView.findViewById(R.id.ShopName_textView)
        val textViewRating: TextView = itemView.findViewById(R.id.ShopRating_textView)
        val textViewDistance : TextView = itemView.findViewById(R.id.ShopDist_textView)
        val textViewTRating : TextView = itemView.findViewById(R.id.ShopTRating_textView)
        val textViewTDistance : TextView = itemView.findViewById(R.id.ShopTDist_textView)

    }
    //Create Views(Use inflate)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shop_layout, parent, false)

        return ViewHolder(view)
    }

    //Populate data in recycler
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val shop: Shop = mList[position]

        //Gets image from firebase and input it to the image view
        if (shop.Image != null) {
            val executor = Executors.newSingleThreadExecutor()
            holder.imageView.setImageURI(Uri.parse(shop.Image))
            val handler = Handler(Looper.getMainLooper())
            var image: Bitmap?
            executor.execute {

                //get image
                try {
                    val `in` = java.net.URL(shop.Image).openStream()
                    image = BitmapFactory.decodeStream(`in`)

                    //and put original and change imageView
                    handler.post {
                        holder.imageView.setImageBitmap(image)
                    }
                }

                //Exception
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // sets the text to the textview from our itemHolder class
        holder.textViewName.text = shop.Name
        holder.textViewRating.text = shop.Rating.toString()
        holder.textViewDistance.text = shop.Distance.toString()
        holder.textViewTDistance.text = " km"
        holder.textViewTRating.text = "/5"
        holder.itemView.setOnClickListener {view -> onClickListener.invoke(view, shop)}




    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }



}
