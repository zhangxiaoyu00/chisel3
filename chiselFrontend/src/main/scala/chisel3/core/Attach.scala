// See LICENSE for license details.

package chisel3.core

import chisel3.internal._
import chisel3.internal.Builder.pushCommand
import chisel3.internal.firrtl._
import chisel3.internal.sourceinfo.{SourceInfo}

object attach {  // scalastyle:ignore object.name
  // Exceptions that can be generated by attach
  case class AttachException(message: String) extends ChiselException(message)
  def ConditionalAttachException =
    AttachException(": Conditional attach is not allowed!")

  // Actual implementation
  private[core] def impl(elts: Seq[Analog], contextModule: UserModule)(implicit sourceInfo: SourceInfo): Unit = {
    if (Builder.whenDepth != 0) Builder.exception(ConditionalAttachException)

    // TODO Check that references are valid and can be attached

    pushCommand(Attach(sourceInfo, elts.map(_.lref)))
  }

  /** Create an electrical connection between [[Analog]] components
    *
    * @param elts The components to attach
    *
    * @example
    * {{{
    * val a1 = Wire(Analog(32.W))
    * val a2 = Wire(Analog(32.W))
    * attach(a1, a2)
    * }}}
    */
  def apply(elts: Analog*)(implicit sourceInfo: SourceInfo): Unit = {
    try {
      impl(elts, Builder.forcedUserModule)
    } catch {
      case AttachException(message) =>
        Builder.exception(elts.mkString("Attaching (", ", ", s") failed @$message"))
    }
  }
}

