package com.zahid.tecnovoicecontrol

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 50, 30, 30)

        val title = TextView(this)
        title.text = "Tecno Voice Control"
        title.textSize = 28f

        statusText = TextView(this)
        statusText.text = "বস, প্রস্তুত আছি।"
        statusText.textSize = 18f
        statusText.setPadding(0, 30, 0, 30)

        val voiceButton = Button(this)
        voiceButton.text = "🎙 কথা বলুন"

        voiceButton.setOnClickListener {
            startVoiceRecognition()
        }

        val accessibilityButton = Button(this)
        accessibilityButton.text = "Accessibility চালু করুন"

        accessibilityButton.setOnClickListener {
            startActivity(
                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            )
        }

        layout.addView(title)
        layout.addView(statusText)
        layout.addView(voiceButton)
        layout.addView(accessibilityButton)

        setContentView(layout)

        textToSpeech = TextToSpeech(this, this)

        speechRecognizer =
            SpeechRecognizer.createSpeechRecognizer(this)

        speechRecognizer.setRecognitionListener(
            object : RecognitionListener {

                override fun onReadyForSpeech(params: Bundle?) {
                    statusText.text = "বস, শুনছি..."
                }

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {}

                override fun onError(error: Int) {
                    statusText.text = "বস, আবার চেষ্টা করুন।"
                }

                override fun onResults(results: Bundle?) {

                    val list =
                        results?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )

                    val command =
                        list?.firstOrNull()?.lowercase(Locale.getDefault())
                            ?: ""

                    statusText.text =
                        "আপনি বলেছেন:\n$command"

                    processCommand(command)
                }

                override fun onPartialResults(
                    partialResults: Bundle?
                ) {}

                override fun onEvent(
                    eventType: Int,
                    params: Bundle?
                ) {}
            }
        )

        if (
            checkSelfPermission(
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                100
            )
        }
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            textToSpeech.language =
                Locale("bn", "BD")

            textToSpeech.setSpeechRate(0.9f)
        }
    }

    private fun speak(message: String) {

        textToSpeech.speak(
            message,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "tecno_voice"
        )
    }

    private fun startVoiceRecognition() {

        val intent =
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "bn-BD"
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        speechRecognizer.startListening(intent)
    }

    private fun processCommand(command: String) {

        if (
            !command.contains("tecno") &&
            !command.contains("টেকনো")
        ) {

            speak(
                "বস, আগে Tecno বলে তারপর কমান্ড দিন।"
            )

            return
        }

        when {

            command.contains("facebook") ||
            command.contains("ফেসবুক") -> {

                openApp(
                    "com.facebook.katana",
                    "Facebook"
                )
            }

            command.contains("youtube") ||
            command.contains("ইউটিউব") -> {

                openApp(
                    "com.google.android.youtube",
                    "YouTube"
                )
            }

            command.contains("whatsapp") ||
            command.contains("হোয়াটসঅ্যাপ") -> {

                openApp(
                    "com.whatsapp",
                    "WhatsApp"
                )
            }

            command.contains("camera") ||
            command.contains("ক্যামেরা") -> {

                try {

                    val cameraIntent =
                        Intent(
                            "android.media.action.IMAGE_CAPTURE"
                        )

                    startActivity(cameraIntent)

                    speak(
                        "ক্যামেরা খুলছি বস।"
                    )

                } catch (e: Exception) {

                    speak(
                        "বস, ক্যামেরা খুলতে পারছি না।"
                    )
                }
            }

            command.contains("settings") ||
            command.contains("সেটিং") -> {

                startActivity(
                    Intent(Settings.ACTION_SETTINGS)
                )

                speak(
                    "সেটিংস খুলছি বস।"
                )
            }

            command.contains("ভলিউম") ||
            command.contains("volume") -> {

                val audioManager =
                    getSystemService(AUDIO_SERVICE)
                            as AudioManager

                if (
                    command.contains("কম") ||
                    command.contains("down") ||
                    command.contains("নিচে")
                ) {

                    audioManager.adjustVolume(
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_SHOW_UI
                    )

                } else {

                    audioManager.adjustVolume(
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_SHOW_UI
                    )
                }

                speak(
                    "ভলিউম ঠিক করছি বস।"
                )
            }

            command.contains("হোমে") ||
            command.contains("হোম") ||
            command.contains("home") -> {

                speak(
                    "হোমে যাচ্ছি বস।"
                )
            }

            command.contains("পিছনে") ||
            command.contains("ব্যাক") ||
            command.contains("back") -> {

                speak(
                    "পিছনে যাচ্ছি বস।"
                )
            }

            command.contains("গুগল") ||
            command.contains("google") -> {

                val query =
                    extractGoogleText(command)

                if (query.isNotEmpty()) {

                    val url =
                        "https://www.google.com/search?q=" +
                                java.net.URLEncoder
                                    .encode(
                                        query,
                                        "UTF-8"
                                    )

                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            android.net.Uri.parse(url)
                        )
                    )

                    speak(
                        "গুগলে খুঁজছি বস।"
                    )

                } else {

                    speak(
                        "বস, কী খুঁজব সেটা বলুন।"
                    )
                }
            }

            else -> {

                speak(
                    "বস, এই কমান্ডটি এখনো শেখানো হয়নি।"
                )
            }
        }
    }

    private fun extractGoogleText(
        command: String
    ): String {

        val words = listOf(
            "গুগলে",
            "গুগল এ",
            "google",
            "google এ",
            "google-এ"
        )

        for (word in words) {

            val index =
                command.indexOf(word)

            if (index >= 0) {

                return command
                    .substring(index + word.length)
                    .trim()
                    .removePrefix("লিখো")
                    .removePrefix("লিখ")
                    .trim()
            }
        }

        return ""
    }

    private fun openApp(
        packageName: String,
        appName: String
    ) {

        val intent =
            packageManager
                .getLaunchIntentForPackage(
                    packageName
                )

        if (intent != null) {

            startActivity(intent)

            speak(
                "$appName খুলছি বস।"
            )

        } else {

            speak(
                "বস, $appName ফোনে ইনস্টল করা নেই।"
            )
        }
    }

    override fun onDestroy() {

        speechRecognizer.destroy()

        textToSpeech.stop()
        textToSpeech.shutdown()

        super.onDestroy()
    }
}
