package BoothMultiplier

import chisel3._
import chisel3.util._

class ArithBundle(len: Int = 32) extends Bundle {
  val in = Flipped(DecoupledIO(Vec(2, Output(SInt(len.W)))))
  val out = DecoupledIO(Output(SInt((2 * len).W)))
}

