package com.example.sport

import android.content.Context
import com.example.project.R

enum class Sport() {
    FOOTBALL {
        override fun getLogoForMarker(): Int {
            return R.drawable.ic_football
        }

        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_football
        }

        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.football)
        }

        override fun getBackgroundEvent(): Int {
            return R.drawable.foot
        }
    },
    BASKETBALL{
        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_basketball
        }

        override fun getLogoForMarker(): Int {
            return R.drawable.ic_basketball_map_marker
        }

        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.basketball)
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }
    },
    CROSSFIT{
        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_crossfit
        }
        override fun getLogoForMarker(): Int {
            return R.drawable.ic_football
        }

        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.crossfit)
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }
    },
    HANDBALL{
        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_handball
        }
        override fun getLogoForMarker(): Int {
            return R.drawable.ic_handball
        }

        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.handball)
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }
    },
    CANOE{
        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_canoe
        }
        override fun getLogoForMarker(): Int {
            return R.drawable.ic_football
        }

        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.football)
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }
    },
    GOLF{
        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_golf
        }
        override fun getLogoForMarker(): Int {
            return R.drawable.ic_golf_marker_map
        }

        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.golf)
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }
    },
    MUSCULATION{
        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_musculation
        }
        override fun getLogoForMarker(): Int {
            return R.drawable.ic_football
        }

        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.bodybuilding)
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }
    },
    TENNISDETABLE{
        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_ping_pong
        }

        override fun getLogoForMarker(): Int {
            return R.drawable.ic_football
        }
        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.table_tennis)
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }
    },
    TENNIS{
        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_tennis
        }

        override fun getLogoForMarker(): Int {
            return R.drawable.ic_tennis
        }

        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.tennis)
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }
    },
    TRAIL{
        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_trail
        }

        override fun getLogoForMarker(): Int {
            return R.drawable.ic_football
        }

        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.football)
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }
    },
    BASEBALL{
        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.baseball)
        }

        override fun getLogoForMarker(): Int {
            return R.drawable.ic_baseball_for_marker
        }

        override fun getLogoSport(size: Int): Int {
            if(size == 50){
                return R.drawable.ic_baseball_50dp
            }
            return R.drawable.ic_baseball
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }

    },
    VOLLEYBALL{
        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.volley_ball)
        }

        override fun getLogoForMarker(): Int {
            return R.drawable.ic_volleyball_marker_map
        }

        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_volleyball
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }

    },
    BADMINTON{
        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.badminton)
        }

        override fun getLogoForMarker(): Int {
            return R.drawable.ic_badminton
        }

        override fun getLogoSport(size: Int): Int {
            return when(size){
                24 -> R.drawable.ic_badminton
                70 -> R.drawable.ic_badminton_70dp
                else -> R.drawable.ic_badminton
            }
            //return R.drawable.ic_badminton
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }

    },
    NATATION{
        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.swimming)
        }

        override fun getLogoForMarker(): Int {
            return R.drawable.ic_football
        }

        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_swimming
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }

    },
    PETANQUE{
        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.football)
        }

        override fun getLogoForMarker(): Int {
            return R.drawable.ic_football
        }

        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_petanque
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }

    },
    RUGBY{
        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.rugby)
        }

        override fun getLogoForMarker(): Int {
            return R.drawable.ic_football
        }

        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_rugby
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }

    },
    AMERICANFOOTBALL{
        override fun getNameSport(context: Context): String {
            return context.getString(R.string.us_football)
        }

        override fun getLogoForMarker(): Int {
            return R.drawable.ic_american_football_marker_map
        }

        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_american_football
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }

    },
    CYCLISME{
        override fun getNameSport(context: Context): String {
            return context.getString(R.string.cycling)
        }

        override fun getLogoForMarker(): Int {
            return R.drawable.ic_cyclist
        }

        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_cyclist
        }

        override fun getBackgroundEvent(): Int {
            return -1
        }
    },
    ESCALADE{
        override fun getNameSport(context: Context): String {
            return context.resources.getString(R.string.climbing)
        }
        override fun getLogoForMarker(): Int {
            return R.drawable.ic_football
        }

        override fun getLogoSport(size: Int): Int {
            return when(size){
                24 -> R.drawable.ic_climber
                70 -> R.drawable.ic_climber_70dp
                else -> R.drawable.ic_climber
            }
        }

        override fun getBackgroundEvent(): Int {
            return R.drawable.climbing
        }
    },
    INIT{
        override fun getLogoSport(size: Int): Int {
            return R.drawable.ic_not_found
        }

        override fun getLogoForMarker(): Int {
            return R.drawable.ic_not_found
        }

        override fun getNameSport(context: Context): String {
            return "OOPS"
        }

        override fun getBackgroundEvent(): Int {
            return -1
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

    fun getString(context: Context, string : String) : Sport{
        return when(string){
            context.resources.getString(R.string.football) -> FOOTBALL
            context.resources.getString(R.string.handball) -> HANDBALL
            context.resources.getString(R.string.basketball) -> BASKETBALL
            context.resources.getString(R.string.crossfit) -> CROSSFIT
            context.resources.getString(R.string.table_tennis) -> TENNISDETABLE
            context.resources.getString(R.string.tennis) -> TENNIS
            context.resources.getString(R.string.climbing) -> ESCALADE
            else -> INIT
        }
    }

    abstract fun getNameSport(context : Context) : String
    abstract fun getLogoSport(size : Int = 24) : Int
    abstract fun getLogoForMarker() : Int
    abstract fun getBackgroundEvent() : Int
}