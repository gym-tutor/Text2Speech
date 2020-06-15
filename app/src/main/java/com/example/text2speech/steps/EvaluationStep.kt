package com.example.text2speech.steps

import android.speech.tts.TextToSpeech

class EvaluationStep(pose:String,id:Int,speaker: TextToSpeech):Step(pose,id,speaker){
    override fun action() {
        this.speak("Evaluation step begins.")
        Thread.sleep(1000)
        this.speak("Evaluation done.")
        this.speak("You did a good job.")
        if (this.repeat)
            this.setNextStep(PoseStep(pose, this.curr_id,this.speaker))
        else {
            if(curr_id==4){
                this.setNextStep(EndStep(pose, this.curr_id + 1, this.speaker))
            }
            this.setNextStep(PoseStep(pose, this.curr_id + 1, this.speaker))
        }
    }
}