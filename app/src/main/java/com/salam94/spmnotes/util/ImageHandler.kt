package com.salam94.spmnotes.util

import com.salam94.spmnotes.R

class ImageHandler {

    fun imageSubjectHandler(title: String):Int{
        return if(title.contains("biology", ignoreCase = true)){
            R.drawable.biology
        } else if (title.contains("chemistry", ignoreCase = true)){
            R.drawable.chemical
        } else if(title.contains("cina", ignoreCase = true)){
            R.drawable.chinese
        } else if(title.contains("bm", ignoreCase = true)){
            R.drawable.malaysia
        } else if(title.contains("english", ignoreCase = true)){
            R.drawable.english
        } else if(title.contains("matematik", ignoreCase = true)){
            R.drawable.math
        } else if(title.contains("physics", ignoreCase = true)){
            R.drawable.physics
        } else{
            R.drawable.exam
        }
    }
}