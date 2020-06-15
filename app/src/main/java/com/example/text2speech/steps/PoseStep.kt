package com.example.text2speech.steps

import android.speech.tts.TextToSpeech

class PoseStep(pose:String,id:Int,speaker: TextToSpeech):Step(pose,id,speaker){

    override fun action(){
        var message = this.get_instruction()
        this.speak(message)
        this.setNextStep(PhotoStep(this.pose,this.curr_id,this.speaker))
    }
    override fun isPoseStep():Boolean {
        return true
    }
}