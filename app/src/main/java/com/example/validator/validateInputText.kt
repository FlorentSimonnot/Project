package com.example.validator

class validateInputText : ValidateField {
    private var text : String = ""
    private var size : Int = 0

    constructor(text : String, size: Int){
        this.text = text
        this.size = size
    }

    override fun sizeIsValid() : Boolean{
        return text.length <= size
    }

    override fun isNotEmpty(): Boolean {
        return text.isNotEmpty()
    }

    override fun fieldIsValid(): Boolean {
        return sizeIsValid() && isNotEmpty()
    }


}