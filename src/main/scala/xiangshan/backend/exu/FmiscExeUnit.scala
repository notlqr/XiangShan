package xiangshan.backend.exu

import chisel3._
import chisel3.util._
import utils._
import xiangshan.backend.exu.Exu.fmiscExeUnitCfg
import xiangshan.backend.fu.fpu._

class FmiscExeUnit extends Exu(fmiscExeUnitCfg) {

  val frm = IO(Input(UInt(3.W)))

  val fus = supportedFunctionUnits.map(fu => fu.asInstanceOf[FPUSubModule])

  val input = io.fromFp
  val isRVF = input.bits.uop.ctrl.isRVF
  val instr_rm = input.bits.uop.ctrl.fpu.rm
  val (src1, src2) = (input.bits.src1, input.bits.src2)

  supportedFunctionUnits.foreach { module =>
    module.io.in.bits.src(0) := src1
    module.io.in.bits.src(1) := src2
    module.asInstanceOf[FPUSubModule].rm := Mux(instr_rm =/= 7.U, instr_rm, frm)
  }

  io.out.bits.fflags := MuxCase(
    0.U,
    fus.map(x => x.io.out.fire() -> x.fflags)
  )
  val fpOutCtrl = io.out.bits.uop.ctrl.fpu
  io.out.bits.data := box(arb.io.out.bits.data, fpOutCtrl.typeTagOut)
}
