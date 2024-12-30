package BoothMultiplier

import chisel3._
import chisel3.util._

class ArithBundle extends Bundle {
  val in = Input(new ArithBundle_in)
  val out = Output(new ArithBundle_out)
}

// Input Bundle
class ArithBundle_in extends Bundle {
  val valid = Bool()    //Multiplier only work when valid is high
  val num_1 = SInt(32.W) 
  val num_2 = SInt(32.W) 
}

// Output Bundle
class ArithBundle_out extends Bundle {
  val ready = Bool()
  val result = SInt(64.W)
}
