package com.example.termproject

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.termproject.api.RetrofitInstance
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class AddMovieActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editYear: EditText
    private lateinit var btnSearch: Button

    private lateinit var imgPoster: ImageView
    private lateinit var textTitle: TextView
    private lateinit var textYear: TextView
    private lateinit var textPlot: TextView

    // ⭐ 별점 UI
    private lateinit var ratingGroup: LinearLayout
    private lateinit var ratingBar: RatingBar
    private lateinit var textRatingValue: TextView
    private lateinit var labelRating: TextView

    private lateinit var editReview: EditText
    private lateinit var btnSave: Button

    private var selectedTitle: String? = null
    private var selectedPlot: String? = null
    private var selectedYear: Int? = null
    private var selectedPosterPath: String? = null
    private var selectedMovieId: Int? = null
    private var selectedTrailerKey: String? = null

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_movie)

        editTitle = findViewById(R.id.editTitle)
        editYear = findViewById(R.id.editYear)
        btnSearch = findViewById(R.id.btnSearch)

        imgPoster = findViewById(R.id.imgPoster)
        textTitle = findViewById(R.id.textTitle)
        textYear = findViewById(R.id.textYear)
        textPlot = findViewById(R.id.textPlot)

        // 별점 관련 뷰 바인딩
        ratingGroup = findViewById(R.id.ratingGroup)
        ratingBar = findViewById(R.id.ratingBar)
        textRatingValue = findViewById(R.id.textRatingValue)
        labelRating = findViewById(R.id.labelRating)

        editReview = findViewById(R.id.editReview)
        btnSave = findViewById(R.id.btnSave)

        val TMDB_API_KEY = BuildConfig.TMDB_API_KEY

        // 별 변경 시 숫자 동기화
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            textRatingValue.text = String.format("%.1f", rating)
        }

        btnSearch.setOnClickListener {
            val title = editTitle.text.toString().trim()
            val year = editYear.text.toString().trim()

            if (TMDB_API_KEY.isBlank()) {
                Toast.makeText(this, "TMDB API 키가 없습니다. local.properties 확인!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (title.isEmpty()) {
                Toast.makeText(this, "영화 제목을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = RetrofitInstance.api.searchMovie(TMDB_API_KEY, title, year)
                    val movie = response.results.firstOrNull()

                    if (movie != null) {
                        selectedTitle = movie.title
                        selectedPlot = movie.overview
                        selectedYear = movie.release_date.take(4).toIntOrNull()
                        selectedPosterPath = movie.poster_path
                        selectedMovieId = movie.id
                        selectedTrailerKey = fetchTrailerKey(movie.id, TMDB_API_KEY)

                        textTitle.text = movie.title
                        textYear.text = movie.release_date.take(4)
                        textPlot.text = movie.overview

                        Glide.with(this@AddMovieActivity)
                            .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
                            .into(imgPoster)


                        imgPoster.visibility = View.VISIBLE
                        textTitle.visibility = View.VISIBLE
                        textYear.visibility = View.VISIBLE
                        textPlot.visibility = View.VISIBLE
                        ratingGroup.visibility = View.VISIBLE

                        // 초기 점수 텍스트 동기화
                        textRatingValue.text = String.format("%.1f", ratingBar.rating)

                        editReview.visibility = View.VISIBLE
                        btnSave.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(this@AddMovieActivity, "영화를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@AddMovieActivity, "API 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnSave.setOnClickListener {
            val review = editReview.text.toString().trim()
            val rating = ratingBar.rating

            if (selectedTitle.isNullOrEmpty() || selectedPlot.isNullOrEmpty() || selectedYear == null || review.isEmpty()) {
                Toast.makeText(this, "모든 정보를 입력/선택해야 저장됩니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveToFirebase(review, rating)
        }
    }

    private suspend fun fetchTrailerKey(movieId: Int, apiKey: String): String? = withContext(Dispatchers.IO) {
        val url = "https://api.themoviedb.org/3/movie/$movieId/videos?api_key=$apiKey"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val json = response.body?.string() ?: return@withContext null

        try {
            val array = JSONObject(json).getJSONArray("results")
            for (i in 0 until array.length()) {
                val video = array.getJSONObject(i)
                if (video.getString("site") == "YouTube" && video.getString("type") == "Trailer") {
                    return@withContext video.getString("key")
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun saveToFirebase(review: String, rating: Float) {
        val posterUrl = "https://image.tmdb.org/t/p/w500${selectedPosterPath ?: ""}"

        val movie = hashMapOf(
            "title" to selectedTitle,
            "plot" to selectedPlot,
            "year" to selectedYear,
            "review" to review,
            "posterUrl" to posterUrl,
            "movieId" to selectedMovieId,
            "trailerKey" to selectedTrailerKey,
            "rating" to rating
        )

        FirebaseFirestore.getInstance()
            .collection("user1")
            .document(selectedTitle!!)
            .set(movie)
            .addOnSuccessListener {
                Toast.makeText(this, "저장 성공!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}










