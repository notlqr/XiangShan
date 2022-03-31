/***************************************************************************************
* Copyright (c) 2020-2021 Institute of Computing Technology, Chinese Academy of Sciences
* Copyright (c) 2020-2021 Peng Cheng Laboratory
*
* XiangShan is licensed under Mulan PSL v2.
* You can use this software according to the terms and conditions of the Mulan PSL v2.
* You may obtain a copy of Mulan PSL v2 at:
*          http://license.coscl.org.cn/MulanPSL2
*
* THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
* EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
* MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
*
* See the Mulan PSL v2 for more details.
***************************************************************************************/

package utils
import chipsalliance.rocketchip.config._
import chisel3._
import chisel3.util._
import chipsalliance.rocketchip.config.Parameters
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.util.HasRocketChipStageUtils
import xiangshan.XSBundle

trait DontOmitGraphML { this: LazyModule =>
  override def omitGraphML: Boolean = false //this.omitGraphML
}

/*
 *
 * 
 * 
 */

class DecoupledBuffer[T <: Data](implicit p: Parameters) extends LazyModule with DontOmitGraphML {
  val node = BundleBridgeNexusNode[DecoupledIO[T]]()
  val flushNode = BundleBridgeSink[Bool]()
  val blockNode = BundleBridgeSink[Bool]()
  lazy val module = new LazyModuleImp(this){
    require(node.in.size == node.out.size)
    val width = node.in.size
    require(flushNode.in.size <= 1)
    require(blockNode.in.size <= 1)
    val input = node.in.map(_._1)
    val output = node.out.map(_._1)

    val hasFlushSource = flushNode.in.size == 1
    val flush = if (hasFlushSource) flushNode.in.head._1 else false.B

    val hasBlockSource = blockNode.in.size == 1
    val block = if (hasBlockSource) blockNode.in.head._1 else false.B

    val valid = RegInit(VecInit(width, false.B))
    for (i <- 0 until width) {
      valid.suggestName("pipeline_valid_$(i)")
      val leftFire = input(i).valid && output(i).ready && !block
      when (output(i).ready) { valid(i) := false.B }
      when (leftFire) { valid(i) := true.B }
      when (flush) { valid(i) := false.B }

      input(i).ready := output(i).ready && !block
      output(i).bits := RegEnable(input(i).bits, leftFire)
      output(i).valid := valid(i) //&& !isFlush
    }
  }
}