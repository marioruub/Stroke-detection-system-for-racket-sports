package com.example.strokeDetectionSystemForRacketRports.components.events.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.strokeDetectionSystemForRacketRports.R
import com.example.strokeDetectionSystemForRacketRports.components.events.model.EventInfoRecyclerView

/**
 * This class is used for displaying a list of events in a recyclerView.
 *
 */
class AllEventsAdapter(
    private val items: List<EventInfoRecyclerView>,
    private val onClickListener: ((event: EventInfoRecyclerView) -> Unit)
) : RecyclerView.Adapter<AllEventsAdapter.ViewHolder>() {

    /**
     * Allows to create a new viewHolder for the event item view.
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.events_list, parent, false)

        return ViewHolder(view, onClickListener)
    }

    /**
     * Returns the number of items in the adapter´s data set.
     *
     */
    override fun getItemCount() = items.size

    /**
     * Allows to bind the data to the viewHolder at a specific position.
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    /**
     * This class is used for holding view of an event item.
     *
     */
    class ViewHolder(
        private val view: View,
        private val onClickListener: ((event: EventInfoRecyclerView) -> Unit)
    ) : RecyclerView.ViewHolder(view) {

        /**
         * Allows to bind the data to the views in the viewHolder.
         *
         */
        fun bind(result: EventInfoRecyclerView) {
            view.findViewById<TextView>(R.id.eventNameText).text = result.name
            view.findViewById<TextView>(R.id.placeText).text = result.place
            view.findViewById<TextView>(R.id.dateText).text = result.date
            view.findViewById<TextView>(R.id.hourText).text = result.time
            view.findViewById<LinearLayout>(R.id.colorEvent).background = result.color

            view.setOnClickListener { onClickListener.invoke(result) }
        }
    }
}