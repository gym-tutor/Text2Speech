package com.example.text2speech


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.text2speech.steps.PoseStep
import com.example.text2speech.steps.Step
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var mTTS:TextToSpeech
    lateinit var speechRecognizer:SpeechRecognizer
    private val RECORD_REQUEST_CODE=101
    private val TAG = "RecognitionListener"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTTS = TextToSpeech(applicationContext,TextToSpeech.OnInitListener{
            status -> if(status!=TextToSpeech.ERROR){
                mTTS.language= Locale.UK
            }
        })

        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_REQUEST_CODE)
        }
        else{
            Toast.makeText(this, "Record Audio Granted", Toast.LENGTH_SHORT).show()
        }

        speakBtn.setOnClickListener{
            val toSpeak = textEdit.text.toString()
            if(toSpeak==""){
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,toSpeak,Toast.LENGTH_SHORT).show()
                mTTS.setSpeechRate(0.5f)
                mTTS.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null)
            }
        }
        stopBtn.setOnClickListener {
            if (mTTS.isSpeaking) {
                mTTS.stop()
//                mTTs.shutdown()
            } else {
                Toast.makeText(this, "Not Speaking", Toast.LENGTH_SHORT).show()
            }
        }
        startListenBtn.setOnClickListener {

            startListeningBtnAction(mTTS)
        }
        yogaStartBtn.setOnClickListener{
            try {
                GlobalScope.launch {
                    var curr_step: Step = PoseStep("hi", 3, mTTS)
                    while (!curr_step.isEndStep()) {
                        curr_step.action()
                        curr_step = curr_step.next()!!
                    }
                }
            }
            catch (ex: Exception){
                print(ex.message)
            }
        }
    }

    override fun onPause() {
        if (mTTS.isSpeaking) {
            mTTS.stop()}
        super.onPause()
    }

    private fun startListeningBtnAction(mTTS:TextToSpeech){
        textTv.text=""
        mTTS.speak("Start Listening what you say",TextToSpeech.QUEUE_FLUSH,null,null)
        while(mTTS.isSpeaking()){
            continue
        }
        var intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(object: RecognitionListener{
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "onReadyForSpeech")
            }

            override fun onRmsChanged(rmsdB: Float) {
                Log.d(TAG, "onRmsChanged")
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d(TAG, "onBufferReceived")
            }

            override fun onPartialResults(partialResults: Bundle) {
                var result = partialResults.getStringArrayList(
                    android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
                textTv.text = result?.elementAt(0).toString()

            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d(TAG, "onEvent")
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech")
            }

            override fun onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech")
            }

            override fun onError(error: Int) {
                Log.d(TAG, "onError")
                var errorCode = ""
                when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> errorCode = "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> errorCode = "Other client side errors"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> errorCode = "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> errorCode = "Network related errors"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> errorCode = "Network operation timed out"
                    SpeechRecognizer.ERROR_NO_MATCH -> errorCode = "No recognition result matched"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> errorCode = "RecognitionService busy"
                    SpeechRecognizer.ERROR_SERVER -> errorCode = "Server sends error status"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> errorCode = "No speech input"
                }
                Log.d("RecognitionListener", "onError:" + errorCode)

            }

            override fun onResults(results: Bundle) {
                var result = results.getStringArrayList(
                    android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
                var best_result = result?.elementAt(0).toString()
                if(best_result == "stop"){
                    mTTS.speak("Ok, I will stop",TextToSpeech.QUEUE_FLUSH,null,null)
                    speechRecognizer.cancel()
                    return
                }
                else if(best_result.contains("count")){
                    try{
                        var number = best_result.filter { it.isDigit() }.toInt()
                        countdown(number,mTTS)
                    } catch(e:Exception){
                        countdown(10,mTTS)
                    }

                }
                else{
                    mTTS.speak(best_result,TextToSpeech.QUEUE_FLUSH,null,null)
                }
                while(mTTS.isSpeaking()) continue

                try {
                    GlobalScope.launch {
                        runOnUiThread {
                            textTv.text = result?.toString()
                        }
                        runOnUiThread {
                            speechRecognizer?.startListening(intent)
                        }
                    }
                } catch (ex: Exception) {

                }
            }

        })
        speechRecognizer.startListening(intent)


    }
    private fun countdown(number:Int, mTTS:TextToSpeech ){
        mTTS.speak("Let me count",TextToSpeech.QUEUE_FLUSH,null,null)
        while(mTTS.isSpeaking()) continue
        for(i in 0..number){
            mTTS.speak(i.toString(),TextToSpeech.QUEUE_FLUSH,null,null)
            while(mTTS.isSpeaking()) continue
            Thread.sleep(1000)
        }
        mTTS.speak("Finish",TextToSpeech.QUEUE_FLUSH,null,null)
        while(mTTS.isSpeaking()) continue
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.isEmpty() || grantResults[0]!=PackageManager.PERMISSION_GRANTED) {
                speechRecognizer.startListening(intent)
            } else {
                print(grantResults)
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

}
