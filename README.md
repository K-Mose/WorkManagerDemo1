# WorkManagerDemo1
New Android background tasks management system - WorkManager
Use WorkManager to run deferrable that mean capable of being post-ponded background works.  

## WorkManager
FireBase의 Jobdispatcher, Job Scheduler and Alarm Manager 등 많은 작업들을 background에서 수행합니다. 
이런 일련의 작업들은 UI 없이 수행되고 심지어 사용자와의 상호작용도 없이 작동합니다. 또 아무때나 사진이나 동영상을 업로드 하지 않고 wi-fi가 있을 때만 업로드 합니다. 
심지어 휴대폰이 충전중이거나 wi-fi 연결됨을 알고 자동으로 play store에서 앱들이 업데이트 됩니다. 

위의 시나리오들을 가능케 하기 위해서 WorkManager를 이용하면 됩니다. 

### What is WorkManager
WorkManager는 android jetpack에서 제공하는 background task managment system 입니다. WorkManager는 system이 연기되는 작업임을 보증하고 관리합니다. 
* WorkManager는 특정 시간이 아닌 미래에 일어날 작업을 수행하도록 설계되었습니다. 
* WorkManager는 사용자가 앱이나 기기를 사용하지 않아도 특정 제약 상황이나 다양한 상황속에서 시작할 수 있는 로직을 관리할 것입니다.

**※ WorkManager는 API level 23 이상에서 작동합니다. 23 아래에서는 Work Mange에는 BroadcastReceiver와 AlarmManager 등으로 선택됩니다.**

### benefit of WorkManager 
* 주기적 실행을 위한 작업 배치는 WorkManager에서는 비동기 작업으로 코드 조각들을 주기적으로 실행시킬지를 정할 수 있습니다.
* 저장공간, 네트워크 상태, 충전 상태 등 다양한 제약조건들을 지원합니다. 
* 병렬처리가 진행되는동안 다중 작업 요청을 지원합니다. 
* Google play service 없이 작업을 수행합니다. 


### WorkManager background work
background로 작동하기 원하는 일련의 작업들은 Worker 클래스를 상속하고, `doWork()` 함수를 Overrid 해야 합니다. 
이 메소드 안에는 background에서 시작해서 background에서 끝나는 작업을 작성 할 수 있습니다. 
`WorkRequest()`는 작업에 대한 제약조건과 작업에 대한 요청을 정의할 수 있습니다. 

**doWork()** <br>
* `doWork()`는 추가적인 작업 요청 없이 default 스레드로 background 작업을 수행합니다. 
* 사용자의 기기는 `doWork()`가 수행되는 동안 일깨워집니다. 우리는 작업이 진행되는 것에 대해 걱정할 필요가 없습니다. 
* `doWork()`는 listener 객체를 반환하고, 작업 완료에 대한 실패 또는 성공에 대한 응답을 알려줍니다. 

### Type of Work Request
<a href="https://developer.android.com/topic/libraries/architecture/workmanager#types"><img src="https://developer.android.com/images/guide/background/workmanager_main.svg" width="800px"/></a>
WorkManager는 아래와 같은 지속적 작업을 다룹니다. 

* **Periodic Work Request** - 
지속적으로 반복되는 작업을 처리합니다. Long Running, Deferrable이 여기 포함됩니다. 
  * Long Runnging - `WorkRequest` 또는 `Wroker`에서 `setForeground()`를 호출합니다.
  * Deferrable - `PeriodicWorkRequest`와 `Worker`로 호출합니다. 
```
val periodicWorkerRequest = PeriodicWorkRequest
      .Builder(DownLoadingWorker::class.java, 16, TimeUnit.MINUTES)
      .build()
```
* **One Time Work Request** - 
한 번 실행되는 작업을 처리합니다. Immediate를 포함하고 Long Running과 Deferrable도 포함됩니다. 
  * Immediate - `OneTimeWorkRequest`와 `Worker`로 호출합니다. 
```kotlin
val filteringRequest = OneTimeWorkRequest
      .Builder(FilteringWorker::class.java)
      .build()
```

### Four steps for scheduling
1. Create a subclass of the Worker class
```kotlin
class FilteringWorker(context: Context, params: WorkerParameters)
    : Worker(context, params){
    override fun doWork(): Result {
        try {
            …
            return Result.success()
        } catch (e:Exception) {
            return Result.failure()
        }
    }
}
```

2. Create a WorkRequest
```kotlin
val filteringRequest = OneTimeWorkRequest
      .Builder(FilteringWorker::class.java)
      .build()
```

3. Enqueue the request 
```
val workManager = WorkManager.getInstance(applicationContext)
workManager.enqueue(uploadRequest)
```

4. Observe&&Get the status updates
```
workManager.getWorkInfoByIdLiveData(filteringRequest.id)
            .observe(this, Observer {
               …   
            })
```

## Structure 
```
WorkManagerDemo1
└─ app
   ├─ .gitignore
   ├─ libs
   ├─ proguard-rules.pro
   └─ src
      └─ main
         └─ AndroidManifest.xml
         └─ java
            └─ com
               └─ example
                  └─ workmanagerdemo1
                     ├─ CompressingWorker.kt
                     ├─ DownLoadingWorker.kt
                     ├─ FilteringWorker.kt
                     ├─ MainActivity.kt
                     └─ UploadWorker.kt
```
각 `*Worker.kt` 클래스들은 테스트 시나리오에 사용될 다양한 종류의 Worker subclass입니다. 

### OneTimeWorkRequest
특정 파일의 업로딩을 백그라운드에서 작업한다고 가정해 봅시다. 

[Four steps for scheduling](#four-steps-for-scheduling)에 따라서 우선 업로딩에 사용될 worker subclass를 작성합니다. 
```kotlin
class UploadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        try {
            // … 업로딩 작업 …
            return Result.success(outputData)
        } catch (e:Exception) {
            return Result.failure()
        }
    }
}
```
그리고 MainActivity.kt 에서 WorkRequest를 작성합니다. 위에 작성된 worker 클래스를 Builder의 인자 값으로 넣어줍니다.  
```kotlin 
val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
      .build()
```
생성한 Request를 WorkManager에 추가합니다. 
```kotlin
val workerManager = WorkerManager.getInstance(applicationContext)
workerManager.enqueue(uploadRequest)
```
마지막으로 현재 work에대한 정보가 들어있는 WorkInfo의 observer를 통해서 현재 work의 상태를 받아옵니다. 
`getWorkInfoByIdLiveData()`는 이름에도 나타나듯 정보를 가져올 Reqeust의 id를 필요로 합니다. 
```kotlin
workManager.getWorkInfoByIdLiveData(uploadRequest.id)
      .observe(this, Observer {
      // … observation 작업 …
      })
```
#### WorkInfo Object 
Work의 <a href="https://developer.android.com/reference/androidx/work/WorkInfo.State#summary">States</a>
 * BLOCKED : 작업이 작업 체이닝에 의해 막혔을 떄 
 * ENQUEUE : 채이닝의 다음 작업으로 적합할 때 
 * RUNNGING : 작업 활성화
 * SUCCEED : 작업이 성공적으로 마침

### Set Constraints 
코드를 다양한 특정 조건하에 실행시키기 위해 제약조건(Constraint)을 사용합니다. 배터리의 상태, 충전 여부, 저장공간의 상태, 휴대폰이 유휴 상태인지, 인터넷 연결이 있는지 등등 여러가지 조건을 설정 할 수 있습니다. 다양한 제약 조건은 <a href="https://developer.android.com/reference/androidx/work/Constraints">여기(androidx.work.Constraints)</a>에서 확인 가능합니다. 

제약조건을 설정하기 위해 우선 제약조건 Builder를 객체를 생성합니다. 그리고 사용할 조건을 설정합니다. 
```kotlin
val constraints = Constraints.Builder()
    .setRequiresCharging(true)
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()
```
위에는 충전 상태의 유무와 네트워크 타입을 설정한 예 입니다. 

Request에 제약조건을 추가하기 위해서 Request 생성 객체에 작성한 constraints 객체를 추가합니다. 
```kotlin 
val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
    .setConstraints(constraints)
    .build()
```

*※ Android Studio 에뮬레이터에서 배터리 상태는 아래에서 설정 할 수 있습니다.*
<img src="https://user-images.githubusercontent.com/55622345/161433775-37524de9-41de-46b1-9b8a-b1c2f6f41948.png" width="600px"/>

### Set Input & Output Data
WorkMaanager로 작업을 할 떄 인자 값을 넘겨줘야 할 때가 있습니다. 그럴떄는 Data 객체를 생성하여 넘겨주면 됩니다. 

**MainActivity → Work** <br>
Data 객체로 값을 넘겨줄 떄는 상수 key값과 전송할 값을 넘겨줘야 합니다. 
```kotlin
// MainActivity.kt
import androidx.work.Data
…  
const val KEY_COUNT_VALUE = "key_count" 
…  
val data:Data = Data.Builder()
    .putInt(KEY_COUNT_VALUE, 1234)
    .build()
…      
```

이제 Request에 Data 객체를 추가하면 됩니다. 
```kotlin
val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
    .setInputData(data)
    .build()
```
이제 데이터를 전송할 준비가 끝났습니다. 

Woker 클래스로 가서 데이터를 받을 차레입니다. 여기서는 Data 객체의 getter 함수에 key를 줘서 값을 받아오면 됩니다. 
```kotlin 
class UploadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
            …  
            val count:int = inputData.getInt(MainActivity.KEY_COUNT_VALUE)
            …
    }
}
```

**Work → MainActivity** <br>
이제 Work에서 다시 Acitivity로 output data를 넘겨봅시다. 작업이 끝나고 완료된 시간을 Activity에 알려보겠습니다. 

우선 데이터를 받은 것과 마찬가지로 key를 설정합니다. 그리고 작업이 완료되는 시점에서 Acitivity로 전송할 데이터를 생성합니다. 
마지막으로 반환하는 Result states에 인자 값으로 outputData를 같이 보내면 됩니다. 
```kotlin
companion object {
    const val KEY_WORKER = "key_worker"
}
…  
class UploadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
            …  
            val count:int = inputData.getInt(MainActivity.KEY_COUNT_VALUE)
            …
            val time = SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
            val currentDate = time.format(Date())
            
            val outputData = Data.Builder()
                .putString(KEY_WORKER, currentDate)
                .build()            
            return Result.success(outputData)                
    }
}
```

전송된 output data는 위에서 작성했던 `getWorkInfoByIdLiveData`의 oberver로 받을 수 있습니다. 

우선 작업의 상태가 종료되었는지 확인합니다. 그리고 Data 객체를 생성해서 outputData를 받은 후 전송에 사용했던 key로 데이터를 찾습니다. 
마지막으로 (여기서는)가시적으로 나타내기위해 Toast로 메시지를 띄웁니다. 
```kotlin
workManager.getWorkInfoByIdLiveData(uploadRequest.id)
      .observe(this, Observer {
          …
          if (it.state.isFinished) {
              val data = it.outputData
              val message = data.getString(UploadWorker.KEY_WORKER)
              Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
          }
      })
```


### Chaining Workers 
여러 작업들을 연쇄적으로 또는 병렬적으로 처리해 봅시다. 

**Chaining** <br>
사진을 서버에 올릴 떄 필터하고 압축 후 서버에 올린다고 생각해봅시다. 
[UploadWorker](#onetimeworkrequest)를 만든 것 처럼 FilteringWorker와 CompressingWorker를 만들어 봅시다. 
```kotlin 
class FilteringWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        …
    }
}

class CompressingWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        …
    }
}
```
그리고 Activity에서 worker를 생성합니다. 
```kotlin
val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
    .build()
val filteringRequest = OneTimeWorkRequest.Builder(FilteringWorker::class.java)
    .build()
val compressingRequest = OneTimeWorkRequest.Builder(CompressingWorker::class.java)
    .build()    
```
연쇄 작업은 아래와 같이 설정합니다. 

`beginWith()` 함수를 통해서 workerManager에 chaining의 시작을 알립니다. 
그리고 `then()` 함수로 연속으로 이어질 작업을 추가한 후 `enqueue()`로 workerManager에 작업을 등록합니다. 
```kotlin
workManager
    .beginWith(filteringRequest) 
    .then(compressingRequest)
    .then(uploadRequest)
    .enqueue()
```


**Parallel Chaining** <br>
사진을 업로드 하면서 다른 이미지들을 다운로드 한다고 가정해 봅시다. 두 작업이 동시에 일어나기 위해서는 사진을 올리는 첫 작업인 Filtering과 사진을 다운받는 Downloading 작업이 병렬로 이뤄지도록 해보겠습니다. 

우선 다른 Woker와 마찬가지로 Downloading worker를 생성합니다. 
```kotlin 
class DownloadingWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        …
    }
}
```
그리고 Activity에서 Request를 생성합니다. 
```kotlin
val downloadingRequest = OneTimeWorkRequest.Builder(DownloadingWorker::class.java)
    .build()    
```
이제 두 작업을 동시에 처리하기위해 `MutableList`를 만들고 작업을 추가합니다. 
```kotlin
val parallelWorks = mutableListOf<OneTimeWorkRequest>()
parallelWorks.add(downloadingRequest)
parallelWorks.add(filteringRequest) 
```
마지막으로 Chaining의 시작점을 parallelworkers로 사용합니다. 
```kotlin
workManager
    .beginWith(parallelWorks) 
    .then(compressingRequest)
    .then(uploadRequest)
    .enqueue()
```


# Ref.
https://developer.android.com/topic/libraries/architecture/workmanager#expedited
https://medium.com/@kaushik.rpk/lets-work-with-android-workmanager-using-two-deferrable-tasks-with-constraints-afac8b5fad05
