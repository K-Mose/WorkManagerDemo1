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
* **One Time Work Request**
한 번 실행되는 작업을 처리합니다. Immediate를 포함하고 Long Running과 Deferrable도 포함됩니다. 
  * Immediate - `OneTimeWorkRequest`와 `Worker`로 호출합니다. 
```kotlin
val filteringRequest = OneTimeWorkRequest
      .Builder(FilteringWorker::class.java)
      .build()
```





# Ref.
https://developer.android.com/topic/libraries/architecture/workmanager#expedited
https://medium.com/@kaushik.rpk/lets-work-with-android-workmanager-using-two-deferrable-tasks-with-constraints-afac8b5fad05
