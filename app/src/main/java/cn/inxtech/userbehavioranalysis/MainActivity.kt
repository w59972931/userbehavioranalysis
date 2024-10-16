package cn.inxtech.userbehavioranalysis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import cn.inxtech.userbehavioranalysis.databinding.ActivityMainBinding
import com.inxcore.analytics.AppInfo
import com.inxcore.analytics.UserBehaviorAnalysisActivity
import com.inxcore.analytics.UserBehaviorAnalysisElement

class MainActivity : Activity(),
    UserBehaviorAnalysisActivity {

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
        AppInfo.init(this)
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
            startActivity(Intent(this, AnotherActivity::class.java))
            true
        })
        setContentView(binding.root)
    }
}