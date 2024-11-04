package cn.inxtech.userbehavioranalysis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import cn.inxtech.userbehavioranalysis.databinding.ActivityMainBinding
import com.inxcore.analytics.UserBehaviorAnalysis
import com.inxcore.analytics.UserBehaviorAnalysisActivity
import com.inxcore.analytics.UserBehaviorAnalysisElement
import kotlin.concurrent.thread

class MainActivity : Activity(), UserBehaviorAnalysisActivity {

    private val elementList = ArrayList<UserBehaviorAnalysisElement>()

    override fun getPageName(): String {
        return "HomePage"
    }

    override fun getElementList(): MutableList<UserBehaviorAnalysisElement> {
        return elementList
    }

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        elementList.add(
            UserBehaviorAnalysisElement(
                "elementName1", binding.et1, null, null, null
            )
        )
        elementList.add(
            UserBehaviorAnalysisElement(
                "elementName2", binding.et2, null, null, null
            )
        )
        elementList.add(UserBehaviorAnalysisElement(
            "button1", binding.btn1
        ) { _, _ ->
            startActivity(Intent(this, OtherActivity::class.java))
            true
        })
        elementList.add(UserBehaviorAnalysisElement(
            "button2", binding.btn2
        ) { _, _ ->
//            startActivity(Intent(this, AnotherActivity::class.java))

            object : Thread() {
                override fun run() {
                    super.run()
                    var data = UserBehaviorAnalysis.getAnalysisData(this@MainActivity)
                    Log.d(
                        "okhttps",
                        "=${data.code}====${Thread.currentThread().name}=======getAnalysisData=====" + data.analysis.fileName
                    )

                }
            }.start()

//            thread {
//                var data = UserBehaviorAnalysis.getAnalysisData(this)
//                Log.d(
//                    "okhttps",
//                    "=${data.code}====${Thread.currentThread().name}=======getAnalysisData=====" + data.analysis.fileName
//                )
//            }.start()

            true
        })

        binding.btn3.setOnClickListener {
            thread {
                var data = UserBehaviorAnalysis.getAnalysisData(this)
                UserBehaviorAnalysis.resetAnalysisData(this, data.analysis);
            }.start()

            object : Thread() {
                override fun run() {
                    super.run()
                    var data = UserBehaviorAnalysis.getAnalysisData(this@MainActivity)
                    UserBehaviorAnalysis.resetAnalysisData(this@MainActivity, data.analysis);

                }
            }.start()

        }
    }
}