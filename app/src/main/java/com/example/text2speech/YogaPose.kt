package com.example.text2speech

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import com.example.text2speech.steps.PoseStep
import com.example.text2speech.steps.Step
import java.text.SimpleDateFormat
import java.util.*

class YogaPose(yogaName:String,mTTS: TextToSpeech,camera:CameraHelper) {
    var yogaName = yogaName
    var start_step = PoseStep("hi", 0, mTTS)
    var curr_step:Step = PoseStep("hi", 0, mTTS)
    var state:Int = 0


    fun start(){
        state = 1
        while (!curr_step.isEndStep()) {
            curr_step.action()
            curr_step = curr_step.next()!!
        }
    }



    fun restart(){
        curr_step = start_step
    }

}