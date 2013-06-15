package com.edofic.workqueue

import concurrent.{Future,ExecutionContext}

class SingleThreadExecutionContext extends ExecutionContext {
  private val executor = java.util.concurrent.Executors.newSingleThreadExecutor()

  def execute(runnable: Runnable): Unit = executor execute runnable
  
  def reportFailure(t: Throwable): Unit = println(t)
  
  def close(): Unit = executor.shutdown()
  
  val workQueue = new ThreadWorkQueue(this)
}

class ThreadWorkQueue(context: SingleThreadExecutionContext = new SingleThreadExecutionContext) extends WorkQueue {
  def close(): Unit = context.close()
  
  def unit(work: => Unit): Unit = context execute new Runnable {
    def run(){
      work
    }
  }
  
  def future[A](work: => A): Future[A] = Future(work)(context)
  
  override val executionContext: ExecutionContext = context
}
