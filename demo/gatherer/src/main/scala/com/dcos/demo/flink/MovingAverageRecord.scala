package com.dcos.demo.flink

/**
  * Created by wenlock on 5/26/17.
  */


case class MovingAverageRecord (label: String, amount: Long, amounts: List[Long]) {

  def getCount(): Long = { return this.amounts.length}
  def getTotal(): Long = {
    var sum:Long = 0
    amounts.foreach( sum += _ )
    return sum
  }
  def getAverage(): Long = {
    return (this.getTotal().toFloat/amounts.length.toFloat).toLong
  }

  def getStdv(): Long = {
    val m = this.getAverage()
    var distance: List[Long] = List[Long]()
    for ( x <- this.amounts ) { distance = distance ++ List[Long]((x - m) * (x - m)) }
    var sum:Long = 0
    distance.foreach( sum += _ )
    return (scala.math.sqrt(sum.toFloat/(distance.length).toFloat)).toLong
  }

  def getCofVar(): Float = {
    return ((this.getStdv().toFloat/this.getAverage().toFloat) * 100 )
  }

  override
  def toString(): String = {
    return (this.getCount().toString() + " " +
            label + " " +
            amount.toString() + " " +
            this.getTotal().toString() + " " +
            this.getAverage().toString() + " " +
            this.getStdv().toString() + " " +
            this.getCofVar().toString
      )
  }
}

object MovingAverageRecord {
  implicit def toMovingAverageRecord(record: MovingAverageRecord): MovingAverageRecord =
    new MovingAverageRecord(
      record.label,
      record.amount,
      record.amounts
    )
}
