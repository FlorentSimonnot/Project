package com.example.status

class Status(
) {
    lateinit var s : String
    lateinit var status : String

    constructor(s : String, status : String) : this() {
        this.s = s
        this.status = status
    }
}