package com.example.termproject

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.firebase.firestore.FirebaseFirestore

class MovieListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter
    private lateinit var sortToggle: MaterialButtonToggleGroup

    // Firestore에서 불러온 원본을 보관
    private val moviesCache = mutableListOf<Movie>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_movie_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerMovies)
        val btnAdd: ImageButton = view.findViewById(R.id.btnAdd)
        sortToggle = view.findViewById(R.id.sortToggle)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MovieAdapter(requireContext()) { movie ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("title", movie.title)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // 기본 선택: 평점순
        view.findViewById<View>(R.id.btnSortRating)?.let { btn ->
            sortToggle.check(btn.id)
        }

        // 토글 변경 시 정렬 적용
        sortToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) applySort(checkedId)
        }

        btnAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddMovieActivity::class.java))
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        fetchMovies()
    }

    private fun fetchMovies() {
        val db = FirebaseFirestore.getInstance()

        db.collection("user1")
            .get()
            .addOnSuccessListener { result ->
                moviesCache.clear()
                for (document in result) {
                    val movie = document.toObject(Movie::class.java)
                    moviesCache.add(movie)
                }
                // 현재 토글 상태로 정렬 반영
                applySort(sortToggle.checkedButtonId)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "불러오기 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun applySort(checkedId: Int) {
        val sorted = when (checkedId) {
            R.id.btnSortYear -> {
                // 최신순
                moviesCache.sortedWith(
                    compareByDescending<Movie> { it.year ?: 0 }
                        .thenByDescending { it.rating ?: 0f }
                )
            }
            else -> {
                // 평점순(기본)
                moviesCache.sortedWith(
                    compareByDescending<Movie> { it.rating ?: 0f }
                        .thenByDescending { it.year ?: 0 }
                )
            }
        }
        adapter.setMovies(sorted)
    }
}

