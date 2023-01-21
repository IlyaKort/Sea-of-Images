package com.code.korti.seaofimages.presentation.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.SharedPrefsKey
import com.code.korti.seaofimages.data.repository.Token
import com.code.korti.seaofimages.presentation.main.MainActivity

class AuthActivity : AppCompatActivity(R.layout.activity_auth) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.isSharedPrefs(SharedPrefsKey.KEY_TOKEN)) {
            Token.ACCESS_TOKEN = viewModel.getSharedPrefs(SharedPrefsKey.KEY_TOKEN)!!
            val mainIntent = Intent(this@AuthActivity, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
    }
}