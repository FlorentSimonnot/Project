package com.example.sport

import com.example.project.R

enum class Sport() {
    FOOTBALL {
        override fun getLogoSport(): Int {
            return R.drawable.ic_football
        }

        override fun getNameSport(): String {
            return this.toString().toLowerCase().capitalize()
        }
    },
    BASKETBALL{
        override fun getLogoSport(): Int {
            return R.drawable.ic_basketball
        }

        override fun getNameSport(): String {
            return this.toString().toLowerCase().capitalize()
        }
    },
    CROSSFIT{
        override fun getLogoSport(): Int {
            return R.drawable.ic_crossfit
        }

        override fun getNameSport(): String {
            return this.toString().toLowerCase().capitalize()
        }
    },
    HANDBALL{
        override fun getLogoSport(): Int {
            return R.drawable.ic_handball
        }

        override fun getNameSport(): String {
            return this.toString().toLowerCase().capitalize()
        }
    },
    CANOE{
        override fun getLogoSport(): Int {
            return R.drawable.ic_canoe
        }

        override fun getNameSport(): String {
            return this.toString().toLowerCase().capitalize()
        }
    },
    GOLF{
        override fun getLogoSport(): Int {
            return R.drawable.ic_golf
        }

        override fun getNameSport(): String {
            return this.toString().toLowerCase().capitalize()
        }
    },
    MUSCULATION{
        override fun getLogoSport(): Int {
            return R.drawable.ic_musculation
        }

        override fun getNameSport(): String {
            return this.toString().toLowerCase().capitalize()
        }
    },
    TENNISDETABLE{
        override fun getLogoSport(): Int {
            return R.drawable.ic_ping_pong
        }

        override fun getNameSport(): String {
            return "Tennis de table"
        }
    },
    TENNIS{
        override fun getLogoSport(): Int {
            return R.drawable.ic_tennis
        }

        override fun getNameSport(): String {
            return this.toString().toLowerCase().capitalize()
        }
    },
    TRAIL{
        override fun getLogoSport(): Int {
            return R.drawable.ic_trail
        }

        override fun getNameSport(): String {
            return this.toString().toLowerCase().capitalize()
        }
    },
    BASEBALL{
        override fun getNameSport(): String {
            return this.toString().toLowerCase().capitalize()
        }

        override fun getLogoSport(): Int {
            return R.drawable.ic_baseball
        }

    },
    VOLLEYBALL{
        override fun getNameSport(): String {
            return "Volley ball"
        }

        override fun getLogoSport(): Int {
            return R.drawable.ic_volleyball
        }

    },
    BADMINTON{
        override fun getNameSport(): String {
            return this.toString().toLowerCase().capitalize()
        }

        override fun getLogoSport(): Int {
            return R.drawable.ic_badminton
        }

    },
    NATATION{
        override fun getNameSport(): String {
            return this.toString().toLowerCase().capitalize()
        }

        override fun getLogoSport(): Int {
            return R.drawable.ic_swimming
        }

    },
    PETANQUE{
        override fun getNameSport(): String {
            return this.toString().toLowerCase().capitalize()
        }

        override fun getLogoSport(): Int {
            return R.drawable.ic_petanque
        }

    },
    RUGBY{
        override fun getNameSport(): String {
            return this.toString().toLowerCase().capitalize()
        }

        override fun getLogoSport(): Int {
            return R.drawable.ic_rugby
        }

    },
    AMERICANFOOTBALL{
        override fun getNameSport(): String {
            return "American Football"
        }

        override fun getLogoSport(): Int {
            return R.drawable.ic_american_football
        }

    },
    CYCLISME{
        override fun getNameSport(): String {
            return "Cyclisme"
        }

        override fun getLogoSport(): Int {
            return R.drawable.ic_cyclist
        }
    },
    ESCALADE{
        override fun getNameSport(): String {
            return this.toString().toLowerCase().capitalize()
        }

        override fun getLogoSport(): Int {
            return R.drawable.ic_climber
        }
    },
    INIT{
        override fun getLogoSport(): Int {
            return R.drawable.ic_not_found
        }

        override fun getNameSport(): String {
            return this.toString().toLowerCase().capitalize()
        }
    }; //Add for instantiate sport and use whichSport

    fun getLogo() : Int{
        return when(this){
            FOOTBALL -> R.drawable.ic_football
            BASKETBALL -> R.drawable.ic_basketball
            HANDBALL -> R.drawable.ic_handball
            CROSSFIT -> R.drawable.ic_crossfit
            GOLF -> R.drawable.ic_golf
            CANOE -> R.drawable.ic_canoe
            MUSCULATION -> R.drawable.ic_musculation
            TENNIS -> R.drawable.ic_tennis
            TRAIL -> R.drawable.ic_trail
            TENNISDETABLE -> R.drawable.ic_ping_pong
            else -> R.drawable.ic_not_found
        }
    }

    abstract fun getNameSport() : String
    abstract fun getLogoSport() : Int
}