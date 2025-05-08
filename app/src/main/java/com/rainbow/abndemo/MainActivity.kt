package com.rainbow.abndemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rainbow.abn.AbnCallback
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
        AbnSession.shared.setUserName("rwd_test")
        AbnSession.shared.setAdid("7ad2f9f6-adf4-4849-91ba-927a4ed97d43")
        AbnSession.shared.setAgreePrivacyPolicy(false)

        initOfferwallView()

        // OfferwallActivity 실행
        binding.btnActivity.setOnClickListener {
            val intent = Intent(this, AbnOfferwallActivity::class.java)
            intent.putExtra("title", "AOS 무료충전소") // 상단 타이틀을 "무료충전소"로 설정
            startActivity(intent)
            binding.btnView.setOnClickListener {
                // 광고 뷰를 특정 레이아웃(Frame)에 추가
        }

            if(abnOfferwallView == null) initOfferwallView()
            binding.frame.addView(abnOfferwallView)
            // 광고 데이터 로드
            abnOfferwallView?.loadData()
        }

        AbnSession.shared.queryPoint(this, object: AbnCallback<Int> {
            override fun onReceive(context: Context, result: Int) {
                Log.d("point", "$result")
            }
        })

        AbnSession.shared.queryAdvertiseCount(this, object: AbnCallback<IntArray> {
            override fun onReceive(context: Context, result: IntArray) {
                val adCount = result.getOrNull(0) ?: 0
                val point = result.getOrNull(1) ?: 0
            }
        })

        AbnSession.shared.queryPublishState(this, object : AbnCallback<Boolean> {
            override fun onReceive(context: Context, result: Boolean) {
            }
        })

        AbnSession.shared.queryPoint()
    }

    fun initOfferwallView() {
        abnOfferwallView = AbnOfferwallView(this)
        abnOfferwallView?.setListener(object: AbnOfferwallListener {
            override fun onLoaded() {
                // 광고가 성공적으로 로드되었을 때 실행되는 코드
            }

            override fun onAdItemClicked(campaignId: Int, campaignName: String) {
                // 광고 항목이 클릭되었을 때 실행되는 코드
            }

            override fun onOfferwallRemoved() {
                // AbnOfferwallView가 닫히면 호출
                Toast.makeText(applicationContext, "remove", Toast.LENGTH_SHORT).show()
            }

        })
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