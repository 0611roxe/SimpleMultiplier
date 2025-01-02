package BoothMultiplier

import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage

class BoothMultiplier extends Module {
  val io = IO(new ArithBundle(32))

  val multiplicandReg = RegInit(0.U(64.W))
  val multiplierReg = RegInit(0.U(33.W)) // One more bit
  val resultReg = RegInit(0.U(64.W))

  val shiftCounter = RegInit(0.U(8.W)) // Shift counter
  val busy = (multiplierReg =/= 0.U(33.W) && shiftCounter < 16.U(8.W)).asBool

  when(io.in.valid && ~busy) {
    resultReg := 0.U(64.W)
    shiftCounter := 0.U(8.W)
    multiplicandReg := io.in.bits(0).asTypeOf(SInt(64.W)).asUInt // Signed extend to 64 bit
    multiplierReg := Cat(io.in.bits(1).asUInt, 0.U(1.W)) // Add one more 0 bit right next to it
  }.otherwise {
    when(busy) {
      resultReg := resultReg + MuxCase(0.U(64.W), Seq(
        (multiplierReg(2, 0) === "b000".U) -> 0.U(64.W),
        (multiplierReg(2, 0) === "b001".U) -> multiplicandReg,
        (multiplierReg(2, 0) === "b010".U) -> multiplicandReg,
        (multiplierReg(2, 0) === "b011".U) -> (multiplicandReg << 1.U),
        (multiplierReg(2, 0) === "b100".U) -> (-(multiplicandReg << 1.U)),
        (multiplierReg(2, 0) === "b101".U) -> (-multiplicandReg),
        (multiplierReg(2, 0) === "b110".U) -> (-multiplicandReg),
        (multiplierReg(2, 0) === "b111".U) -> 0.U(64.W)
      ))
      multiplicandReg := multiplicandReg << 2.U
      multiplierReg := multiplierReg >> 2.U
      shiftCounter := shiftCounter + 1.U(8.W)
    }.otherwise {
      resultReg := resultReg
      multiplicandReg := multiplicandReg
      multiplierReg := multiplierReg
      shiftCounter := shiftCounter
    }
  }
  io.out.bits := resultReg.asSInt
  io.out.valid := !busy
  io.in.ready := !busy
}

object BoothMultiplierApp extends App {
  ChiselStage.emitSystemVerilogFile(
    new BoothMultiplier,
    firtoolOpts = Array("-o", "./build/systemverilog/BoothMultiplier.sv", "-disable-all-randomization", "-strip-debug-info")
  )
}