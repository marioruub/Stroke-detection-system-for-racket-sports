/*
 * Copyright 2019 Punch Through Design LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.strokeDetectionSystemForRacketRports.components.strokes.viewmodel

import android.bluetooth.le.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.strokeDetectionSystemForRacketRports.R

/**
 * This class is used for displaying a list of BLE scan results in a recyclerView.
 *
 */
class ScanResultAdapter(
    private val items: List<ScanResult>,
    private val onClickListener: ((device: ScanResult) -> Unit)
) : RecyclerView.Adapter<ScanResultAdapter.ViewHolder>() {

    /**
     * Allows to create a new viewHolder for the event item view.
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_scan_result, parent, false)

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
        private val onClickListener: ((device: ScanResult) -> Unit)
    ) : RecyclerView.ViewHolder(view) {

        /**
         * Allows to bind the data to the views in the viewHolder.
         *
         */
        fun bind(result: ScanResult) {
            try{
                view.findViewById<TextView>(R.id.device_name).text = result.device.name ?: "Unnamed"
            }catch (e: SecurityException){}
            view.findViewById<TextView>(R.id.mac_address).text = result.device.address
            view.findViewById<TextView>(R.id.signal_strength).text = "${result.rssi} dBm"
            view.setOnClickListener { onClickListener.invoke(result) }
        }
    }
}