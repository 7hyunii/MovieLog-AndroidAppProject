package com.example.termproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class DetailActivity : AppCompatActivity() {

    private lateinit var imagePoster: ImageView
    private lateinit var textTitle: TextView
    private lateinit var textYear: TextView
    private lateinit var textPlot: TextView
    private lateinit var labelRating: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var textRatingValue: TextView
    private lateinit var editReview: EditText
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var btnYoutube: ImageView

    private var documentId: String? = null
    private var movieId: Int? = null
    private var trailerKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        imagePoster = findViewById(R.id.imagePoster)
        textTitle = findViewById(R.id.textTitle)
        textYear = findViewById(R.id.textYear)
        textPlot = findViewById(R.id.textPlot)
        labelRating = findViewById(R.id.labelRating)
        ratingBar = findViewById(R.id.ratingBar)
        textRatingValue = findViewById(R.id.textRatingValue)
        editReview = findViewById(R.id.editReview)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)
        btnYoutube = findViewById(R.id.btnYoutube)

        documentId = intent.getStringExtra("title")
        if (documentId == null) {
            Toast.makeText(this, "영화 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val db = FirebaseFirestore.getInstance()

        db.collection("user1")
            .document(documentId!!)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val title = document.getString("title") ?: ""
                    val year = document.getLong("year")?.toInt() ?: 0
                    val plot = document.getString("plot") ?: ""
                    val review = document.getString("review") ?: ""
                    val posterUrl = document.getString("posterUrl") ?: ""
                    val rating = document.getDouble("rating")?.toFloat() ?: 0f
                    movieId = document.getLong("movieId")?.toInt()
                    trailerKey = document.getString("trailerKey")

                    textTitle.text = title
                    textYear.text = year.toString()
                    textPlot.text = plot
                    editReview.setText(review)

                    // 별/숫자 동기화
                    ratingBar.rating = rating
                    textRatingValue.text = String.format("%.1f", rating)

                    Glide.with(this)
                        .load(posterUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .into(imagePoster)

                    imagePoster.setOnClickListener {
                        if (movieId != null) {
                            val url = "https://www.themoviedb.org/movie/$movieId"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "TMDB 링크를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    btnYoutube.setOnClickListener {
                        if (!trailerKey.isNullOrEmpty()) {
                            val url = "https://www.youtube.com/watch?v=$trailerKey"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "예고편 링크가 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "문서를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "불러오기 실패", Toast.LENGTH_SHORT).show()
                finish()
            }

        // 별이 바뀔 때 숫자 텍스트도 즉시 갱신
        ratingBar.setOnRatingBarChangeListener { _, newRating, _ ->
            textRatingValue.text = String.format("%.1f", newRating)
        }

        btnUpdate.setOnClickListener {
            val newReview = editReview.text.toString().trim()
            val newRating = ratingBar.rating

            if (newReview.isEmpty()) {
                Toast.makeText(this, "후기를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("user1")
                .document(documentId!!)
                .update(mapOf("review" to newReview, "rating" to newRating))
                .addOnSuccessListener {
                    Toast.makeText(this, "리뷰/별점 수정 완료!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "수정 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnDelete.setOnClickListener {
            db.collection("user1")
                .document(documentId!!)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "삭제 완료!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "삭제 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}






