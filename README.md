# WorkQueue

Actor and thread based implementations of safe work queue. You need something like this when you have multiple threads(or actors) going on and you need to schedule multiple operations/jobs that must not happen in parallel. But you don't want to mess with locks. The go-to solution is to make an actor and a message that instructs it to do something and as (akka) actors are single-threaded you have a guarantee that your jobs will be processe sequentially.
But this leads to a bloat of one-off messages and actors. So here are generic solutions. 

## Usage
```scala    
import com.edofic.workqueue._
val safely: WorkQueue = ... //see below for instantiation

safely unit {
    //do some dangerous operation long running operation using 'value'
}

//and in other thread
val result: Future[Value] = safely future {
    val value = ... //read something from 'value'
    value
}
```
You know that these two blocks will never run at the same time - `safely` acts as a lock.
Or if you prefer working with `ExecutionContext` directly
```scala
implicit val ec = safely.executionContext
val f1 = Future( ... )
val f2 = Future( ...)
```
`f1` and `f2` will be executed sequentially.

Don't forget to clean up
```scala
safely.close()
```    

## Actor based

Use an existing `ActorSystem` and share it's thread pool. Queue is and actor's mailbox and some plumbing that makes for a more fluid API. Very lightweight.
Don't do blocking or long running operations with this since you *will* starve other actors in the system.
```scala
val safely = new ActorWorkQueue(system)
//or if creating from actor
val safely = new ActorWorkQueue(context)
```    
## Thread based

Create a thread(more precisely: a java single thread executor) to run on. Better suited for blocking or long running operations. But heavier on resources. You don't want thousands of theese running.
```scala
val safely = new SingleThreadExecutionContext()
```
