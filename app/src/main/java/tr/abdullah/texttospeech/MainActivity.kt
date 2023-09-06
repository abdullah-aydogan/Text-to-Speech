package tr.abdullah.texttospeech

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tr.abdullah.texttospeech.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tts: TextToSpeech

    private val listItems = arrayOf("Türkçe", "English", "Deutsch")
    private var speechRate: Float = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            tts = TextToSpeech(this@MainActivity, this@MainActivity)

            btnStart.setOnClickListener {

                val text = editText.text.toString()

                if(text.isEmpty())
                    Toast.makeText(this@MainActivity, getString(R.string.warning_text), Toast.LENGTH_SHORT).show()

                else
                    speak(text)
            }

            btnLanguage.setOnClickListener {

                changeLanguage()
            }

            btnLanguage.text = getString(R.string.language)

            btnSpeechRate.setOnClickListener {

                changeSpeechRate()
            }
        }
    }

    override fun onInit(status: Int) {

        if(status == TextToSpeech.SUCCESS) {

            val result = tts.setLanguage(Locale.forLanguageTag(getString(R.string.speech_language)))

            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

                Toast.makeText(this@MainActivity, R.string.not_supported, Toast.LENGTH_SHORT).show()
            }

            else {

                binding.btnStart.isEnabled = true
                tts.setSpeechRate(speechRate)
            }
        }

        else {

            Toast.makeText(this@MainActivity, R.string.error_text, Toast.LENGTH_SHORT).show()
        }
    }

    private fun speak(text: String) {

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun changeLanguage() {

        val builder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(this@MainActivity)
        val checkedItem: Int = getString(R.string.checked_item).toInt()

        builder.setTitle(getString(R.string.choose_language))

        builder.setSingleChoiceItems(listItems, checkedItem) { dialogInterface, i ->

            when (i) {

                0 -> {
                    setLanguage("TR")
                    recreate()
                }

                1 -> {
                    setLanguage("EN")
                    recreate()
                }

                2 -> {
                    setLanguage("DE")
                    recreate()
                }
            }

            dialogInterface.dismiss()
        }

        builder.setNegativeButton(R.string.cancel) { dialogInterface, i ->

            dialogInterface.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun changeSpeechRate() {

        Toast.makeText(this, R.string.very_soon, Toast.LENGTH_SHORT).show()
    }

    private fun setLanguage(language: String) {

        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration()
        config.locale = locale

        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    override fun onDestroy() {

        if(tts.isSpeaking)
            tts.stop()

        tts.shutdown()

        super.onDestroy()
    }
}