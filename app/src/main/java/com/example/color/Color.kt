package com.example.color

enum class Color {

    RED{
        override fun getDarkColor(opacity: Int): String {
            return when(opacity){
                100 -> {
                   "#cb9ca1"
                }
                200 -> {
                    "#ba6b6c"
                }
                300 -> {
                    "#af4448"
                }
                400 ->{
                    "#b61827"
                }
                500 -> {
                    "#ba000d"
                }
                600 -> {
                    "#ab000d"
                }
                700 -> {
                    "#9a0007"
                }
                800 -> {
                    "#8e0000"
                }
                900 -> {
                    "#7f0000"
                }
                else -> ""
            }
        }

        override fun getHexa(opacity: Int): String {
            return when(opacity){
                100 -> {
                    "#ffcdd2"
                }
                200 -> {
                    "#ef9a9a"
                }
                300 -> {
                    "#e57373"
                }
                400 -> {
                    "#ef5350"
                }
                500 -> {
                    "#f44336"
                }
                600 -> {
                    "#e53935"
                }
                700 -> {
                    "#d32f2f"
                }
                800 -> {
                    "#c62828"
                }
                900 -> {
                    "#b71c1c"
                }
                else -> ""
            }
        }
    },
    CYAN{
        override fun getDarkColor(opacity: Int): String {
            return when(opacity){
                100 -> {
                    "#81b9bf"
                }
                200 -> {
                    "#4bacb8"
                }
                300 -> {
                    "#009faf"
                }
                400 -> {
                    "#0095a8"
                }
                500 -> {
                    "#008ba3"
                }
                600 -> {
                    "#007c91"
                }
                700 -> {
                    "#006978"
                }
                800 -> {
                    "#005662"
                }
                900 -> {
                    "#00363a"
                }
                else -> {
                   ""
                }
            }
        }

        override fun getHexa(opacity: Int): String {
            return when(opacity){
                100 -> {
                    "#b2ebf2"
                }
                200 -> {
                    "#80deea"
                }
                300 -> {
                    "#4dd0e1"
                }
                400 -> {
                    "#26c6da"
                }
                500 -> {
                    "#00bcd4"
                }
                600 -> {
                    "#00acc1"
                }
                700 -> {
                    "#0097a7"
                }
                800 -> {
                    "#00838f"
                }
                900 -> {
                    "#006064"
                }
                else -> ""
            }
        }
    },
    WHITE{
        override fun getDarkColor(opacity: Int): String {
            return "#fff"
        }

        override fun getHexa(opacity: Int): String {
            return "#fff"
        }
    },
    BLUEGREY{
        override fun getDarkColor(opacity: Int): String {
            return when(opacity){
                100 -> {
                    "#9ea7aa"
                }
                200 -> {
                    "#808e95"
                }
                300 -> {
                    "#62757f"
                }
                400 -> {
                    "#4b636e"
                }
                500 -> {
                    "#34515e"
                }
                600 -> {
                    "#29434e"
                }
                700 -> {
                    "#1c313a"
                }
                800 -> {
                    "#102027"
                }
                900 -> {
                    "#000a12"
                }
                else -> ""
            }
        }

        override fun getHexa(opacity: Int): String {
            return when(opacity){
                100 -> {
                    "#cfd8dc"
                }
                200 -> {
                    "#b0bec5"
                }
                300 -> {
                    "#90a4ae"
                }
                400 -> {
                    "#78909c"
                }
                500 -> {
                    "#607d8b"
                }
                600 -> {
                    "#546e7a"
                }
                700 -> {
                    "#455a64"
                }
                800 -> {
                    "#37474f"
                }
                900 -> {
                    "#263238"
                }
                else -> ""
            }
        }
    },
    ORANGE{
        override fun getDarkColor(opacity: Int): String {
            return when(opacity){
                100 -> {
                    "#cbae82"
                }
                200 -> {
                    "#ca9b52"
                }
                300 -> {
                    "#c88719"
                }
                400 -> {
                    "#c77800"
                }
                500 -> {
                    "#c66900"
                }
                600 -> {
                    "#c25e00"
                }
                700 -> {
                    "#bb4d00"
                }
                800 -> {
                    "#b53d00"
                }
                900 -> {
                    "#ac1900"
                }
                else -> ""
            }
        }

        override fun getHexa(opacity: Int): String {
            return when(opacity){
                100 -> {
                    "#ffe0b2"
                }
                200 -> {
                    "#ffcc80"
                }
                300 -> {
                    "#ffb74d"
                }
                400 -> {
                    "#ffa726"
                }
                500 -> {
                    "#ff9800"
                }
                600 -> {
                    "#fb8c00"
                }
                700 -> {
                    "#f57c00"
                }
                800 -> {
                    "#ef6c00"
                }
                900 -> {
                    "#e65100"
                }
                else -> ""
            }
        }
    }
    ;

    abstract fun getHexa(opacity : Int) : String
    abstract fun getDarkColor(opacity: Int) : String

}