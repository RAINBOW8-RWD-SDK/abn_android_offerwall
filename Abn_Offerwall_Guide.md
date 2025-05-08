# Abn Android Offerwall SDK

이 문서는 AbnOfferwall SDK를 Android 프로젝트에 연동하는 방법을 안내합니다.

## 1. SDK 설정하기

### 1-1. 라이브러리 등록
SDK는 `.aar` 파일로 제공됩니다. 아래 단계에 따라 프로젝트에 추가해 주세요.

#### 🔹 Step 1. `libs` 폴더 생성 및 파일 복사

1. Android 프로젝트의 `app` 모듈 아래에 `libs` 폴더가 없다면 생성해 주세요.

        Project/

            └── app/
    
                └── libs/
            
                    └── abnofferwall-sdk.aar

2. 전달받은 `abnofferwall-sdk.aar` 파일을 `libs` 폴더에 복사합니다.

#### 🔹 Step 2. `build.gradle` 설정

**`settings.gradle.kts`** 파일에 다음을 추가해 주세요:

```gradle
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        flatDir {
            dirs("libs") // 추가
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        flatDir {
            dirs("libs") 
        }
    }
}
```

**`app/build.gradle.kts`** 파일에 다음을 추가해 주세요:
```gradle
dependencies {
    // .aar 파일 추가
    implementation(files("libs/abnsdk.aar"))
}
```

### 1-2. Manifest 설정하기

#### 권한 설정

아래와 같이 권한 사용을 추가합니다.
```xml
<!-- 인터넷 -->
<uses-permission android:name="android.permission.INTERNET" />
<!-- 광고 아이디 -->
<uses-permission android:name="com.google.android.gms.permission.AD_ID"/>
```

#### Application ID 설정하기

AndroidMenifest.xml 파일의 application 태그 안에 아래와 같이 설정합니다.    
(*YOUR_ABN_APP_ID* 부분을 Abn 사이트에서 발급받은 실제 App ID 값으로 변경하세요.)


```xml
<application>

    <meta-data
            android:name="abn_app_id"
            android:value="YOUR_ABN_APP_ID" />

</application>
```



#### Activity tag 추가하기

광고 목록을 띄우기 위한 Activity를 <activity/>로 아래와 같이 설정합니다. 

```xml
<activity 
    android:name="com.rainbow.abn.AbnOfferwallActivity"
    android:exported="true"
    android:screenOrientation="portrait"/>
```

AndroidManifest.xml의 내용 예시 
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="com.google.android.gms.permission.AD_ID"/>


    // 인터넷
    <uses-permission android:name="android.permission.INTERNET" />
    // 광고 아이디 획득
    <uses-permission android:name="com.google.android.gms.permission.AD_ID"/>
 

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Abndemo"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <meta-data
            android:name="abn_app_id"
            android:value="df1e89ff-e986-4329-bda9-2fe5153ff828" />

        <activity android:name="com.rainbow.abn.AbnOfferwallActivity" android:exported="true"/>

    </application>
</manifest>
```
	

### Proguard 사용

Proguard 사용 시 Proguard 설정 파일에 아래 내용을 반드시 넣어주세요.

```
-keep class com.rainbow.** { *;}
```


## 2. 오퍼월 광고 목록 띄우기

다음과 같은 과정을 통해 광고 목록을 출력 하실 수 있습니다.

1) ABN SDK 초기화 

2) 유저 식별 설정

3) 광고 목록 출력

광고 목록 출력하는 Activity 예제 코드

kotlin
```kotlin
class MainActivity: AppCompatActivity() {

    private var abnOfferwallView: AbnOfferwallView? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(getLayoutInflater())
        setContentView(binding.getRoot())

        AbnSession.shared.initInstance(this)
        AbnSession.shared.setAdid("00000000-0000-0000-0000-000000000000") // ADID 설정
        AbnSession.shared.setUserName("user_name") // 유저 식별 설정

        // Abn Offerwall Activity 오픈
        binding.btnActivity.setOnClickListener {
            val intent = Intent(this, AbnOfferwallActivity::class.java)
            intent.putExtra("title", "무료충전소") // 상단 타이틀 설정
            startActivity(intent)
        }

        // 받을 수 있는 포인트 
        AbnSession.shared.queryPoint(context, object : AbnCallback<Int> { 
            override fun onReceive(context: Context, result: Int) {
               binding.tv.text = point
            }
        })

    }
}
```


### 유저 식별 값 및 ADID 설정

유저 식별 값 및 ADID를 설정하셔야 광고 목록를 볼 수 있으며, 유저마다의 포인트를 관리할 수 있습니다.

##### Method

- fun setUserName(name: String)

##### Parameters

| 파라미터명 | 내용                                                         |
| ------------- | ------------------------------------------------------------ |
| name      | 사용자 식별하기 위하여 사용하는 고유 ID값 (회원 ID)

### 앱에서 광고 목록 띄우기: `AbnOfferwallActivity` 실행

자신의 앱에서 **광고 목록**을 띄우기 위해서는 `AbnOfferwallActivity`를 실행해야 합니다.                             
이때, **"title"** 값을 넘겨줌으로써 상단에 표시될 타이틀을 설정할 수 있습니다.

---

#### title 파라미터 설명

- **title**: `AbnOfferwallActivity` 상단에 표시될 타이틀을 설정하는 값입니다.   
사용자가 광고 목록을 보는 화면의 상단에 표시됩니다. 이 값은 **문자열(String)** 형식으로 전달됩니다.

---

#### 적용 예시

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // AbnOfferwallActivity를 실행하는 인텐트 생성
    val intent = Intent(this, AbnOfferwallActivity::class.java)

    // 상단 타이틀을 "무료충전소"로 설정
    intent.putExtra("title", "무료충전소")

    // AbnOfferwallActivity 실행
    startActivity(intent)
}
```

### 광고 뷰 로드 및 실행: `AbnOfferwallView`

앱 내에서 **광고 목록**을 띄우기 위해 `AbnOfferwallView`를 설정하고 데이터를 로드하여 광고를 표시하는 방법을 설명합니다. 이 과정에서 광고 뷰가 **frame**에 추가되고, 광고와 관련된 다양한 이벤트를 처리할 수 있는 리스너를 설정합니다.

---

#### 코드 설명

```kotlin
// AbnOfferwallView 인스턴스를 생성
override fun onCreate(savedInstanceState: Bundle?) {

    abnOfferwallView = AbnOfferwallView(this)

    // 광고 이벤트 리스너 설정
    abnOfferwallView?.setListener(object: AbnOfferwallListener {
        override fun onLoaded() {
            // 광고가 성공적으로 로드되었을 때 실행되는 코드
        }

        override fun onAdItemClicked(campaignId: Int, campaignName: String) {
            // 광고 항목이 클릭되었을 때 실행되는 코드
        }
        
        override fun onOfferwallRemoved() {
            // AbnOfferwallView가 닫히면 호출
        }
    })

    // 광고 뷰를 특정 레이아웃(Frame)에 추가
    binding.frame.addView(abnOfferwallView)

    // 광고 데이터 로드
    abnOfferwallView?.loadData()

    override fun onBackPressed() {
        if (abnOfferwallView != null) {
            // AbnOfferwallView가 존재하는 경우, 광고 뷰에서 뒤로 가기 처리
            abnOfferwallView?.handleBackPressed { handled ->
                if (!handled) {
                    // 광고 뷰가 뒤로 가기 이벤트를 처리하지 않으면, 광고 뷰를 레이아웃에서 제거
                    binding.frame.removeView(abnOfferwallView)
                    abnOfferwallView = null  // 광고 뷰 객체를 null로 설정하여 메모리에서 해제
                    // SDK 사용자 앱 상황에 따라 변경.
                }
            }
        } else {
            super.onBackPressed()
        }
    }

}
```

## 3.SDK API 사용 예시

이 문서는 SDK에서 제공하는 API를 사용하는 방법에 대해 설명합니다. 사용자는 API를 통해 포인트 조회, 광고 참여 수 조회, 게시 상태 확인 등을 할 수 있습니다. 각 API는 **비동기**와 **동기** 호출 방식을 제공하며, 필요에 따라 선택하여 사용할 수 있습니다.

---

### 1. 포인트 조회

`querypoint` API를 사용하여 사용자가 보유한 포인트를 조회할 수 있습니다.

#### 비동기 방식 호출

```kotlin
AbnSession.shared.queryPoint(context, object : AbnCallback<Int> {
   override fun onReceive(context: Context, result: Int) {
      tvPoint.text = "포인트: $result"
   }
})
```

#### 동기 방식 호출

```kotlin
Thread {
   val point = AbnSession.shared.queryPoint()
   Handler(Looper.getMainLooper()).post {
      tvPoint.text = "포인트: $point"
   }
}.start()
```

### 2. 적립 가능한 광고수, 포인트

`queryadvertisecount` API를 사용하여 사용자가 보유한 포인트를 조회할 수 있습니다.

#### 비동기 방식 호출

```kotlin
AbnSession.shared.queryAdvertiseCount(context, object : AbnCallback<IntArray> {
   override fun onReceive(context: Context, result: IntArray) {
      val adCount = result.getOrNull(0) ?: 0
      val point = result.getOrNull(1) ?: 0
      tvAdCount.text = "참여 가능 광고수: $adCount"
      tvPoint.text = "적립 가능한 포인트: $point"
   }
})
```

#### 동기 방식 호출

```kotlin
Thread {
    val result = AbnSession.shared.queryadvertisecount()
    val adCount = result?.getOrNull(0) ?: 0
    val point = result?.getOrNull(1) ?: 0
    Handler(Looper.getMainLooper()).post {
        tvAdCount.text = "참여 가능 광고수: $adCount"
        tvPoint.text = "적립 가능한 포인트: $point"
    }
}.start()
```

### 3. 퍼블리셔 앱 게시 상태

`querypublishstate` API를 사용하여 사용자가 보유한 포인트를 조회할 수 있습니다.

#### 비동기 방식 호출

```kotlin
AbnSession.shared.queryPublishState(context, object : AbnCallback<Boolean> {
   override fun onReceive(context: Context, result: Boolean) {
      tvPublishState.text = if (result) "게시 상태 ON" else "게시 상태 OFF"
   }
})
```

#### 동기 방식 호출

```kotlin
Thread {
   val isPublished = AbnSession.shared.queryPublishState()
   Handler(Looper.getMainLooper()).post {
      tvPublishState.text = if (isPublished) "게시 상태 ON" else "게시 상태 OFF"
   }
}.start()
```

### 4. 개인정보 수집동의 여부 설정

개인정보 수집 동의 여부를 설정하는 메소드입니다. `true`로 설정하면, 오퍼월에서 개인정보 수집 동의 팝업이 표시되지 않습니다.

#### Method

```kotlin
fun setAgreePrivacyPolicy(agree: Boolean)
