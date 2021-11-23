# Peter McCarthy: King Backend Code Test

---
## Build and Run
The project is built in **Java 11** using Gradle. 

To build, run `./gradlew build`. 

Then, run `java -jar ./build.lib/king-high-score-1.0-SNAPSHOT.jar`. (there is also a jar package with the zip) 

The server will start up and listen on address `localhost:8000`.

To test, run `./gradlew test`

### Example request
`curl localhost:8000/45/login`
 
## Caching
There is a simple caching mechanism in place for the `GetHighScores` resource. This could be improved, such as a cleaner 
way to clear the map of stale values, and initialising it, but for the purposes of this it works well.

## Notes on Data modelling
The project models the data (User, Level and Score) in a relational way. There is essentially a scoreId which is a 
combination of a userId and a levelId. In a relational sense, both the Primary Keys for User and Login are needed to 
index a Score. 

A scoreId represents a list of scores, representing a specific user and level. Users and Levels also have the IDs of the 
scores they associate with. For example a User 1 can have many scores for Level 2, so User 1 will have the scoreId 
`u1l2` as part of its state. 

In a real-world application, these DataStores would be backed by actual Database solutions. 

## Concurrency
All collections (except the list of scores for each scoreId) uses a `ConcurrentHashMap`, making reads/writes thread safe.
The list of scores sits behind a synchronized block for writing. A set of 100 executor threads is used to handle concurrent 
requests, with a backlog of 200. 

## Changes in real-world appication
If this were a production system, these would be some improvements I would make:
 - Use an HTTP server framework like Spring or Javalin
   - This would also allow for the use of exceptions instead of `Response` objects
 - Use real datastore implementations and structure the data in a relational way if possible
 - Make application as stateless as possible to allow new instances to easily be spun-up 
   - This would also allow new instances to easily be integrated with load balancers
 - Add monitoring for requests/operations to be able to inspect performance and bottlenecks
 
## Key generation
Session keys are generated using a string of alphanumeric characters and randomly picking 10 of them. In a real-world 
scenario, this could potentially cause a key conflict, so a more robust solution would be implemented.


