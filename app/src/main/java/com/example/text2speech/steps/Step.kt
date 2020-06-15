package com.example.text2speech.steps

import android.speech.tts.TextToSpeech

abstract class Step (pose:String, id:Int, speaker: TextToSpeech){
    protected var pose = pose
    protected var curr_id = id
    protected var next_step:Step? = null
    protected var repeat = false
    protected var speaker = speaker


    //test
    private var message = ArrayList<String>(5)
    init {

        message.add(
            "Stand in Mountain Pose. With an exhalation, step or lightly jump your feet 3 1/2 to 4 feet apart. Raise your arms parallel to the floor and reach them actively out to the sides, shoulder blades wide, palms down.")
        message.add(
            "Turn your right foot slightly to the right and your left foot out to the left 90 degrees. Align the left heel with the right heel. Firm your thighs and turn your left thigh outward so that the center of the left knee cap is in line with the center of the left ankle."
        )
        message.add(
            "Exhale and bend your left knee over the left ankle, so that the shin is perpendicular to the floor. If possible, bring the left thigh parallel to the floor. Anchor this movement of the left knee by strengthening the right leg and pressing the outer right heel firmly to the floor."
        )
        message.add(
            "Stretch the arms away from the space between the shoulder blades, parallel to the floor. Don't lean the torso over the left thigh: Keep the sides of the torso equally long and the shoulders directly over the pelvis. Press the tailbone slightly toward the pubis. Turn the head to the left and look out over the fingers."

        )
        message.add(
            "Stay for 30 seconds to 1 minute. Inhale to come up. Reverse the feet and repeat for the same length of time to the left."
        )
    }

    private fun read(){
        return
    }

    private fun json_processing() {
        return
    }
    protected fun get_instruction():String{
        return this.message[this.curr_id]
    }
    fun next():Step?{
        return this.next_step
    }
    open fun isPhotoStep():Boolean{
        return false
    }

    open fun isPoseStep():Boolean {
        return false
    }
    open fun isEvalStep():Boolean{
        return false
    }
    open fun isEndStep():Boolean{
        return false
    }
    fun takePhoto(){
        return

    }
    //Current step repeating
    fun setNextStep(step:Step){
        this.next_step = step
    }

    abstract fun action();

    protected fun speak(message:String){
        speaker.speak(message,TextToSpeech.QUEUE_FLUSH,null,null)
        while(speaker.isSpeaking) continue
    }

}