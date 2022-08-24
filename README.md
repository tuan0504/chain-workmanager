# chain-workmanager
Custom WorkManager - make it simpler - no library - pure java 

Custom WorkManage of google : 
* Support : 
    * Can use to manage chain tasks  
    * Support run parallel and sequential tasks
    * Run tasks in backgroud
    * Support retry 
    * Support receive & wait for result of task 
    * Support merge data for input 
* Non Support : 
    * not schedule work run in future 
    * Not save task after app is killed 
    * Register Task ( broadcast) - await for receiver 
* Re-use : 
    * Interface of Workmanager 
    * Interface of WorkContinuation 
    * Interface of WorkRequest 
    * Interface of Worker 
    * Interface of OneTimeWorkRequest 
    * Class InputMerge to merge data of prerequisites worker 
    * Runnable to run in background : 
        * Enqueue 
        * Start work 
        * Stop work 
        * Cancel work 
* Custom : 
    * Use AppExecutors to run runnable 
    * Use variables to save  data  
        * HashMap 
        * List 
        * Concurrency 
