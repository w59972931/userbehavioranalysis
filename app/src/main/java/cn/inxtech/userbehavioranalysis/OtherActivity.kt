package cn.inxtech.userbehavioranalysis

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.inxcore.analytics.UserBehaviorAnalysisActivity
import com.inxcore.analytics.UserBehaviorAnalysisElement


class OtherActivity : AppCompatActivity(),
    UserBehaviorAnalysisActivity {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun getPageName(): String {
        return "InfoPage"
    }

    override fun getElementList(): MutableList<UserBehaviorAnalysisElement> {
        return ArrayList()
    }
}