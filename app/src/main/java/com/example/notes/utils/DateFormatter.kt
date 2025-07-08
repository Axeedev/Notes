package com.example.notes.utils

import android.icu.text.SimpleDateFormat
import java.text.DateFormat
import java.util.concurrent.TimeUnit


object DateFormatter : DateFormatInterface{

    private val millisInHour = TimeUnit.HOURS.toMillis(1)
    private val millisInDay = TimeUnit.DAYS.toMillis(1)
    private val formatter = SimpleDateFormat.getDateInstance(DateFormat.SHORT)
    val currentDate: String
        get() = formatter.format(System.currentTimeMillis()).replace(".","/")

    override fun format(date: Long) : String {
        val currentDate = System.currentTimeMillis()
        val sum = currentDate - date
        return when{
            sum < millisInHour -> "Just now"
            sum < millisInDay -> {
                val hours = TimeUnit.MILLISECONDS.toHours(sum)
                "$hours h ago"
            }
            else ->{
                formatter.format(date)
            }
        }
    }
}
fun interface DateFormatInterface{
    fun format(date: Long) : String

}