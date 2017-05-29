package com.dcos.demo.flink

/**
  * Created by wenlock on 5/26/17.
  */


case class VolatilityRecord(label: String,sample: Long, confindex: Long, mean: Long, cofv: Float) {

  def getVolatility(): String = return if (this.getCofV() > this.confindex) "high"
  else "low"

  def getMean(): Long = { return (this.mean/this.sample) }
  def getCofV(): Float = { return (this.cofv/this.sample.toFloat) }

  override
  def toString(): String = {
    return (this.label + " " +
            this.sample + " " +
            this.confindex + " " +
            this.getMean() + " " +
            this.getCofV() + " " +
            this.getVolatility().toString
      )
  }
}


object VolatilityRecord {
  implicit def toVolatilityRecord(record: VolatilityRecord): VolatilityRecord =
    new VolatilityRecord(
      record.label,
      record.sample,
      record.confindex,
      record.mean,
      record.cofv
    )
}
