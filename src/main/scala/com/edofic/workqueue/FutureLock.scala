package com.edofic.workqueue

import concurrent.{Future,ExecutionContext}


class Locker {
  private[this] var lock: Future[Any] = Future successful ()

  def unit(work: => Unit)(implicit ec: ExecutionContext): Unit = future(work)
  def future[A](work: => A)(implicit ec: ExecutionContext): Future[A] = lock synchronized {
    val f = lock map (_ => work)
    lock = f
    f
  }
}

