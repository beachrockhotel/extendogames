package com.example.extendogames.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.extendogames.R
import com.example.extendogames.api.models.TournamentItem
import java.text.SimpleDateFormat
import java.util.*

class TournamentAdapter(private val context: Context, private val tournamentList: ArrayList<TournamentItem>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<TournamentAdapter.TournamentViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TournamentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tournament_item, parent, false)
        return TournamentViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: TournamentViewHolder, position: Int) {
        val tournament = tournamentList[position]
        holder.bind(context, tournament)
    }

    override fun getItemCount() = tournamentList.size

    class TournamentViewHolder(itemView: View, private val listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tournamentTitle)
        private val address: TextView = itemView.findViewById(R.id.tournamentAddress)
        private val startTime: TextView = itemView.findViewById(R.id.tournamentStartTime)
        private val endTime: TextView = itemView.findViewById(R.id.tournamentEndTime)
        private val discipline: TextView = itemView.findViewById(R.id.tournamentDiscipline)
        private val prizePool: TextView = itemView.findViewById(R.id.tournamentPrizePool)
        private val image: ImageView = itemView.findViewById(R.id.tournamentImage)
        private val description: TextView = itemView.findViewById(R.id.tournamentDescription)

        fun bind(context: Context, tournament: TournamentItem) {
            title.text = tournament.title
            address.text = "Место проведения: ${tournament.location}"

            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("dd.MM.yyyy 'в' HH:mm", Locale.getDefault())

            val parsedStartTime = inputDateFormat.parse(tournament.start_time)
            val formattedStartTime = outputDateFormat.format(parsedStartTime!!)
            startTime.text = "Начало: $formattedStartTime"

            val parsedEndTime = inputDateFormat.parse(tournament.end_time)
            val formattedEndTime = outputDateFormat.format(parsedEndTime!!)
            endTime.text = "Конец: $formattedEndTime"

            discipline.text = "Дисциплина: ${tournament.discipline}"
            prizePool.text = "Призовой фонд: ${tournament.prize_pool} рублей"
            description.text = tournament.description

            if (tournament.image_url != null) {
                Glide.with(context).load(tournament.image_url).into(image)
            }

            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }
}
