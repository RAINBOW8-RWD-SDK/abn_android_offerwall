package com.rainbow.abndemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.rainbow.abn.AbnOfferwallActivity
import com.rainbow.abn.AbnOfferwallListener
import com.rainbow.abn.AbnOfferwallView
import com.rainbow.abn.AbnSession
import com.rainbow.abndemo.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity() {

    private var abnOfferwallView: AbnOfferwallView? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(getLayoutInflater())
        setContentView(binding.getRoot())

        AbnSession.shared.initInstance(this)
        AbnSession.shared.setUserName("test_user")
        AbnSession.shared.setAdid("00000000-0000-0000-0000-000000000000")
        AbnSession.shared.setAgreePrivacyPolicy(false)

        // View 추가
        abnOfferwallView = AbnOfferwallView(this)
        abnOfferwallView?.setListener(object: AbnOfferwallListener {
            override fun onLoaded() {
                // 광고가 성공적으로 로드되었을 때 실행되는 코드
            }

            override fun onClose() {
                // 광고를 닫았을 때 실행되는 코드
                finish()
            }

            override fun onAdItemClicked(campaignId: Int, campaignName: String) {
                // 광고 항목이 클릭되었을 때 실행되는 코드
            }
        })

        // OfferwallActivity 실행
        binding.btnActivity.setOnClickListener {
            val intent = Intent(this, AbnOfferwallActivity::class.java)
            intent.putExtra("title", "무료충전소") // 상단 타이틀을 "무료충전소"로 설정
            startActivity(intent)
        }

        binding.btnView.setOnClickListener {
            // 광고 뷰를 특정 레이아웃(Frame)에 추가
            binding.frame.addView(abnOfferwallView)
            // 광고 데이터 로드
            abnOfferwallView?.loadData()
        }

        AbnSession.shared.querypoint { point ->
            Log.d("point", "$point")
        }
    }

    override fun onBackPressed() {
        if (abnOfferwallView != null) {
            abnOfferwallView?.handleBackPressed {
                if(!it) {
                    binding.frame.removeView(abnOfferwallView)
                    abnOfferwallView = null
                }
            }
        } else {
            super.onBackPressed()
        }
    }
}