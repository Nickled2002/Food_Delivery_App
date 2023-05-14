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

class ItemsAdapter (private val mList: ArrayList<Item>, val onClickListener: (Item) -> Unit) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        //viewholder contains all views in the layout file
        val imageView: ImageView = itemView.findViewById(R.id.Item_Image)
        val textViewName: TextView = itemView.findViewById(R.id.ItemName_textView)
        val textViewDesc: TextView = itemView.findViewById(R.id.ItemDesc_textView)
        val textViewPrice: TextView = itemView.findViewById(R.id.ItemPrice_textView)
        val textViewPound: TextView = itemView.findViewById(R.id.ItemPound_textView)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsAdapter.ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)

        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ItemsAdapter.ViewHolder, position: Int) {

        val item: Item = mList[position]

        //Gets image from firebase and input it to the image view
        if (item.Image != null) {
            val executor = Executors.newSingleThreadExecutor()
            holder.imageView.setImageURI(Uri.parse(item.Image))
            val handler = Handler(Looper.getMainLooper())
            var image: Bitmap?
            executor.execute {

                //get image
                try {
                    val `in` = java.net.URL(item.Image).openStream()
                    image = BitmapFactory.decodeStream(`in`)

                    //Change image view
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

        holder.textViewName.text = item.Name
        holder.textViewDesc.text = item.Description
        holder.textViewPrice.text = item.Price.toString()
        holder.textViewPound.text = "€"
        holder.itemView.setOnClickListener {view -> onClickListener.invoke(item)}
    }

}