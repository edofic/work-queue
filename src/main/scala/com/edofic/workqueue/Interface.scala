package com.edofic.workqueue

import concurrent.{Future, ExecutionContext}

trait WorkQueue {
  def close(): Unit
  def unit(work: => Unit): Unit 
  def future[A](work: => A): Future[A] 
  val executionContext: ExecutionContext = new WorkQueueExecutionContext(this)
}

class WorkQueueExecutionContext(queue: WorkQueue) extends ExecutionContext{
  def execute(runnable: Runnable): Unit = queue unit runnable.run()
  def reportFailure(t: Throwable): Unit = println(t)
}
