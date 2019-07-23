package com.example.autocompleteAdapterCustom

import android.content.Context
import android.widget.ArrayAdapter
import com.example.sport.ItemSport
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import androidx.annotation.Nullable
import com.example.project.R




class AutocompleteSportAdapter(context: Context, sports : List<ItemSport>) : ArrayAdapter<ItemSport>(context, 0, sports) {
    private val suggestions = ArrayList<ItemSport>()
    private val items = sports
    private val itemsAll = items

    override fun getItem(position: Int): ItemSport? {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {
        return sportFilter
    }

    override fun getView(position: Int, @Nullable convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                R.layout.sports_autocomplete_row, parent, false
            )
        }

        val itemSport : ItemSport = items[position]
        if(itemSport != null){
            val textViewName : TextView = convertView!!.findViewById(R.id.text_view_name)
            val imageViewFlag : ImageView = convertView.findViewById(R.id.image_view_sport)

            val sport : ItemSport? = getItem(position)

            if (sport != null) {
                textViewName.text = sport.getSportName()
                imageViewFlag.setImageResource(sport.getSportLogo())
            }
        }

        return convertView!!
    }

    private var sportFilter : Filter =  object : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            if (p0 != null){
                suggestions.clear()
                val filterPattern = p0.toString().toLowerCase().trim()

                for (item in sports) {
                    if (item.getSportName().toLowerCase().startsWith(filterPattern)) {
                        suggestions.add(item)
                    }
                }
                val results : FilterResults = FilterResults()
                results.values = suggestions
                results.count = suggestions.size
                return results
            }
            return FilterResults()
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            if(p1 != null && p1.count > 0) {
                clear()
                val filterList: ArrayList<ItemSport> = p1.values as ArrayList<ItemSport>
                filterList.forEach {
                    add(it)
                }
            }
            else{
                clear()
                addAll(itemsAll)
            }
            notifyDataSetChanged();
        }

        override fun convertResultToString(resultValue: Any): CharSequence {
            return (resultValue as ItemSport).getSportName()
        }

    }
}