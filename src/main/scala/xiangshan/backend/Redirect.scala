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

package xiangshan.backend
import chipsalliance.rocketchip.config.Parameters
import chisel3._
import chisel3.util._
import difftest._
import freechips.rocketchip.diplomacy.{LazyModule, LazyModuleImp}
import utils._
import xiangshan._

class DiplomaticRedirectGenerator(implicit p: Parameters) extends XSModule with HasCircularQueuePtrHelper {
  val backendNode = new Object
  val ftqNode = new Object
  val mispredictNode = BundleBridgeSink[Valid[ExuOutput]]()
  val loadReplayNode = BundleBridgeSink[Valid[Redirect]]()
}