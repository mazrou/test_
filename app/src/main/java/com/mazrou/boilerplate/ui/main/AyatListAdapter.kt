package com.mazrou.boilerplate.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mazrou.boilerplate.R
import com.mazrou.boilerplate.model.ui.Ayat
import com.mazrou.boilerplate.model.ui.Racine
import kotlinx.android.synthetic.main.ayat_layout.view.*

class AyatListAdapter (
    private var interaction: SearchListAdapter.Interaction? = null,
)   : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Ayat>() {

        override fun areItemsTheSame(
            oldItem: Ayat,
            newItem: Ayat
        ): Boolean {
            return oldItem.text == newItem.text
        }

        override fun areContentsTheSame(
            oldItem: Ayat,
            newItem: Ayat
        ): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AyatViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.ayat_layout,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AyatViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(list: List<Ayat>) {
        differ.submitList(list)
    }

    inner class AyatViewHolder constructor(
        itemView: View,
        private val interaction: SearchListAdapter.Interaction?,
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Ayat) = with(itemView) {
            surah_name_txt_view.text = "سورة${item.surahName}"
            ayat_txt_view.text = item.text + " (${item.ayatNumber})"

            setOnClickListener {
                interaction?.onItemClicked(item , adapterPosition)
            }
        }
    }


}