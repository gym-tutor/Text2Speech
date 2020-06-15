package com.example.text2speech.steps

import android.speech.tts.TextToSpeech

class PhotoStep(pose:String,id:Int,speaker: TextToSpeech):Step(pose,id,speaker){

    override fun action() {
        Thread.sleep(1000)
        this.speak("one")
        Thread.sleep(1000)
        this.speak("two")
        this.takePhoto()

        Thread.sleep(1000)
        this.speak("three")
        Thread.sleep(1000)
        this.speak("very good!")
        this.setNextStep(EvaluationStep(this.pose,this.curr_id,this.speaker))
        Thread.sleep(1000)
    }

}