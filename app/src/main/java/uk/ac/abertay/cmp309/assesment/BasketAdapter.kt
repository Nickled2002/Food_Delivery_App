package uk.ac.abertay.cmp309.assesment


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class BasketAdapter (private val mList: ArrayList<Basket>, val onClickListener: (Basket) -> Unit) : RecyclerView.Adapter<BasketAdapter.ViewHolder>()
    {
        inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

            val textViewName: TextView = itemView.findViewById(R.id.BasketName_textView)
            val textViewPrice: TextView = itemView.findViewById(R.id.BasketPrice_textView)
            val textViewEuro : TextView = itemView.findViewById(R.id.BasketMoney_textView)
            val removeButton : ImageButton = itemView.findViewById(R.id.Basket_Remove)


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketAdapter.ViewHolder {
            // inflates the card_view_design view
            // that is used to hold list item
            val view = LayoutInflater.from(parent.context).inflate(R.layout.basket_layout, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: BasketAdapter.ViewHolder, position: Int) {

            val basket: Basket = mList[position]

            // sets the text to the textview from our itemHolder class
                holder.textViewName.text = basket.Name
                holder.textViewPrice.text = basket.Price.toString()
                holder.textViewEuro.text = "€"
                holder.removeButton.setOnClickListener { view -> onClickListener.invoke(basket) }
        }

        override fun getItemCount(): Int {
            return mList.size
        }
    }