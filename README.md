# resource deltas app

## Glossary

* **Resource Snapshot**: a list of resources available at a certain instant.
* **Delta**: Derived from two consecutive *resource snapshots*. Represents the resources added and removed during a polling interval.
 

### Build the Project
```
./gradlew clean build
```

#### Run the tool

```
 java -jar build/libs/demo-0.0.1-SNAPSHOT.jar --datafeed.source.uri=<resource uri>
```


## API for Recent changes 

* URL: /resources
* Method: GET
* URL Params:
   
  Optional: 
  
  * *span* : time span in seconds to look for changes. By default is 30 seconds. 
  Note: If the span includes the initial instant, the resources of the initial snapshot are considered all as additions.   

* Sample: http://localhost:8080/resource?span=120
    
  Recovers the changes during the last 120 seconds. 
    

## Scalability issues

There are several potential bottlenecks:

* Data loss. The *deltas* are stored in memory, thus if the process dies for some reason all the data up to that point is lost.
The solution would be to use a data store with persistence to disk or even memory replication would be an improvement.

* Clients overwhelming the query endpoint:
    * Number of queries exceeding the capacity of one single OS process. As this process is not stateless, because the
    *deltas* are stored in memory this process cannot be replicated unless this stated is externalized in a data store. 
    * Results size might impact the bandwidth available. Two solutions comes to mind that trade bandwidth for cpu and storage, 
    the lazy would be to forbid results larger than a certain count. The elaborate one is to use pagination. with a wrinkle, 
    the query results should be stored on the side.

* Overflowing storage capacity. In this implementation we have established any limitation regarding the time span. Allowing
this kind of queries forces us to store the complete history of *deltas* from the beginning of time, which poses the problem 
of exceeding storage capacity. One workaround to this situation is to merge *deltas* and losing time granularity. It is best 
to start compressing the oldest data as it's not as relevant as the data close to the current instant. This discussion assumes 
a persistent storage which is cheap compared to local memory.

* The data feed to big to consume. The polling consumer might not be able to handle a certain size of resources. As this size 
is proportionally correlated to the surveyed area we can adjust it according to our capacity.



