package com.example.strokeDetectionSystemForRacketRports.components.strokes.viewmodel

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.strokeDetectionSystemForRacketRports.R
import com.example.strokeDetectionSystemForRacketRports.components.strokes.classes.StrokeType
import com.example.strokeDetectionSystemForRacketRports.components.strokes.model.Stroke

/**
 * This class is used for displaying a list of strokes in a recyclerView.
 *
 */
class StrokesAdapter(private val strokeList: MutableList<Stroke>) : RecyclerView.Adapter<StrokesAdapter.StrokeViewHolder>() {

    /**
     * This class contains the reference from the view of text and image of the stroke
     *
     */
    class StrokeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val strokeTextView: TextView = itemView.findViewById(R.id.stroke_text_view)
        val strokeImageView: ImageView = itemView.findViewById(R.id.stroke_image_view)
    }

    /**
     * Allows to create a new viewHolder for the event item view.
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StrokeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.strokes_list, parent, false)
        return StrokeViewHolder(itemView)
    }

    /**
     * Allows to bind the data to the viewHolder at a specific position.
     *
     */
    override fun onBindViewHolder(holder: StrokeViewHolder, position: Int) {
        val currentItem = strokeList[position]
        holder.strokeTextView.text = currentItem.name
        if(currentItem.name == StrokeType.DERECHA.toString()){
            holder.strokeImageView.setImageResource(R.drawable.derecha)
        }
        else if(currentItem.name == StrokeType.REVÉS.toString()){
            holder.strokeImageView.setImageResource(R.drawable.reves)
        }
    }

    /**
     * Returns the number of items in the adapter´s data set.
     *
     */
    override fun getItemCount() = strokeList.size

    /**
     * Allows to add a specific stroke to the list and notifies it.
     *
     */
    fun addStroke(stroke: Stroke) {
        Handler(Looper.getMainLooper()).post {
            strokeList.add(0, stroke)
            notifyItemInserted(0)
        }
    }
}