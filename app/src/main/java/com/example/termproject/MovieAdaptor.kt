package com.example.termproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MovieAdapter(
    private val context: Context,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var movies: List<Movie> = listOf()

    fun setMovies(movies: List<Movie>) {
        this.movies = movies
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.titleText.text = movie.title ?: "제목 없음"
        holder.yearText.text = "${movie.year ?: "-"}"
        holder.reviewText.text = "${movie.review ?: "없음"}"

        holder.ratingSmall.rating = movie.rating ?: 0f

        if (!movie.posterUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(movie.posterUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imagePoster)
        } else {
            holder.imagePoster.setImageResource(R.drawable.placeholder_image)
        }

        holder.itemView.setOnClickListener { onItemClick(movie) }
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagePoster: ImageView = itemView.findViewById(R.id.imagePoster)
        val titleText: TextView = itemView.findViewById(R.id.textTitle)
        val yearText: TextView = itemView.findViewById(R.id.textYear)
        val reviewText: TextView = itemView.findViewById(R.id.textReview)
        val ratingSmall: RatingBar = itemView.findViewById(R.id.ratingSmall)
    }
}


