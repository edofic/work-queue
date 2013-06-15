package com.edofic.workqueue

import util.Try
import concurrent.{Future,Promise,ExecutionContext}
import akka.actor._

class ActorWorkQueue(creator: ActorRefFactory) extends WorkQueue {
  import ActorWorkQueue._
  
  val actor = creator.actorOf(Props(new WorkActor))
  
  def close(): Unit = actor ! PoisonPill
  
  def unit(work: => Unit): Unit = actor ! WorkItem( () => work)
  
  def future[A](work: => A): Future[A] = {
    val promise = Promise[A]
    unit {
      promise complete Try(work)
    }
    promise.future
  }  
}

object ActorWorkQueue {
  case class WorkItem(work: () => Unit)
  
  class WorkActor extends Actor {
    def receive = {
      case WorkItem(work) => work()
    } 
  }
}
