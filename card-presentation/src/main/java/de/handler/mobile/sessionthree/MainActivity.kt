package de.handler.mobile.sessionthree


import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultTextView.movementMethod = ScrollingMovementMethod()

        Repository.getAllPokemonCards().observe(this, Observer { pokemonCards ->
            var resultString = ""
            pokemonCards.forEach {
                resultString = "$resultString  ${it?.name} \n"
            }
            resultTextView.text = resultString
        })
    }
}
