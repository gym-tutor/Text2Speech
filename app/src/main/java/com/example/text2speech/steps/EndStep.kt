package com.example.text2speech.steps

import android.speech.tts.TextToSpeech

class EndStep(pose:String,id:Int,speaker: TextToSpeech):Step(pose,id,speaker){
    override fun action() {
        this.speak("end")
    }
    override fun isEndStep():Boolean{
        return true
    }
}