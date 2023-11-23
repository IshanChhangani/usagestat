package com.example.usagestat


import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    lateinit var tvUsageStats:TextView
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvUsageStats = findViewById(R.id.tvUsageStats)

        // Check if permission to access usage stats is granted
        if (!hasUsageStatsPermission(this)) {
            requestUsageStatsPermission()
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            showUsageStats()
//        }
        // Start the service to track time
//        startService(Intent(this, TimeTrackerService::class.java))
    }
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showUsageStats(){
        val usageStatsManager: UsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//        val cal: Calendar = Calendar.getInstance()
//        cal.add(Calendar.DAY_OF_MONTH,-1)
        val rn = System.currentTimeMillis()
//        val queryUsageStats: List<UsageStats> = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,cal.timeInMillis, System.currentTimeMillis())
//        val rn = System.currentTimeMillis()
        val queryUsageStats: List<UsageStats> = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,rn - 24*60*60*1000, rn)
        var statsData:String = ""
        for(entry in queryUsageStats.indices){
                statsData = statsData + "package Name: " + queryUsageStats[entry].packageName + "\n" +
                        "Last Time Used: " + convertTime(queryUsageStats[entry].lastTimeUsed) + "\n" +
                        "First Time Stamp: " + convertTime(queryUsageStats[entry].firstTimeStamp) + "\n" +
                        "Last Time Stamp: " + convertTime(queryUsageStats[entry].lastTimeStamp) + "\n" +
                        "Last Time Foreground service used: " + convertTime(queryUsageStats[entry].lastTimeForegroundServiceUsed) + "\n" +
                        "Total Time Foreground service used: " + convertTime(queryUsageStats[entry].totalTimeForegroundServiceUsed) + "\n" +
                        "Last Time visible: " + convertTime(queryUsageStats[entry].lastTimeVisible) + "\n" +
                        "Total Time Visible: " + convertTime(queryUsageStats[entry].totalTimeVisible) + "\n" +
                        "Describe Contents: " + queryUsageStats[entry].describeContents() + "\n" +
                        "Total Time in Foreground: " + convertTime2(queryUsageStats[entry].totalTimeInForeground) + "\n\n"

                    Log.d("MainActivity",queryUsageStats[entry].packageName)
        }
        tvUsageStats.text = statsData
    }

    private fun convertTime(lastTimeUsed: Long): String {
        val date:Date = Date(lastTimeUsed)
        val format :SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a" , Locale.ENGLISH)
        return format.format(date)
    }

    private fun convertTime2(lastTimeUsed: Long): String {
        val date:Date = Date(lastTimeUsed)
        val format :SimpleDateFormat = SimpleDateFormat("hh:mm" , Locale.ENGLISH)
        return format.format(date)
    }
    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun requestUsageStatsPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        ActivityCompat.startActivityForResult(
            this,
            intent,
            0,
            null
        )
    }
}
