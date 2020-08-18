package org.arshdeep.financemanagement

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class HomeScreenActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        findViewById<ImageButton>(R.id.imageButton2).setOnClickListener {
            startActivity(Intent(this@HomeScreenActivity,
                MortgageActivity::class.java))
        }
    }
}