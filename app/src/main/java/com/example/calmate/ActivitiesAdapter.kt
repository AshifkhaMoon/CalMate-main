package com.example.calmate

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ActivitiesAdapter(private var activities: MutableList<ActivityItem>, private val onActivityClickListener: OnActivityClickListener) : RecyclerView.Adapter<ActivitiesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.textViewActivityName)
        val caloriesTextView: TextView = view.findViewById(R.id.textViewCaloriesPerHour)
    }

    interface OnActivityClickListener {
        fun onActivityClicked(activityItem: ActivityItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = activities[position]
        holder.nameTextView.text = activity.name
        holder.caloriesTextView.text = "${activity.caloriesBurned} cal/h"
        holder.itemView.setOnClickListener {
            onActivityClickListener.onActivityClicked(activity)
        }
    }

    override fun getItemCount() = activities.size
    fun updateData(newActivities: List<ActivityItem>) {
        activities.clear()
        activities.addAll(newActivities)
        notifyDataSetChanged()
    }
}
