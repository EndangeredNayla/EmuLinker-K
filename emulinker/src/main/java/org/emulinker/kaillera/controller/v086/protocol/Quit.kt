package org.emulinker.kaillera.controller.v086.protocol

import java.nio.ByteBuffer
import org.emulinker.kaillera.controller.messaging.MessageFormatException
import org.emulinker.kaillera.controller.messaging.ParseException
import org.emulinker.kaillera.controller.v086.V086Utils.getNumBytes
import org.emulinker.kaillera.pico.AppModule
import org.emulinker.util.EmuUtil
import org.emulinker.util.UnsignedUtil.getUnsignedShort
import org.emulinker.util.UnsignedUtil.putUnsignedShort

abstract class Quit : V086Message() {
  /** NOTE: May be the empty string. */
  abstract val username: String
  abstract val userId: Int
  abstract val message: String

  override val bodyLength: Int
    get() = username.getNumBytes() + message.getNumBytes() + 4

  public override fun writeBodyTo(buffer: ByteBuffer) {
    EmuUtil.writeString(buffer, username, 0x00, AppModule.charsetDoNotUse)
    buffer.putUnsignedShort(userId)
    EmuUtil.writeString(buffer, message, 0x00, AppModule.charsetDoNotUse)
  }

  companion object {
    const val ID: Byte = 0x01

    @Throws(ParseException::class, MessageFormatException::class)
    fun parse(messageNumber: Int, buffer: ByteBuffer): Quit {
      if (buffer.remaining() < 5) throw ParseException("Failed byte count validation!")
      val userName = EmuUtil.readString(buffer, 0x00, AppModule.charsetDoNotUse)
      if (buffer.remaining() < 3) throw ParseException("Failed byte count validation!")
      val userID = buffer.getUnsignedShort()
      val message = EmuUtil.readString(buffer, 0x00, AppModule.charsetDoNotUse)
      return if (userName.isBlank() && userID == 0xFFFF) {
        Quit_Request(messageNumber, message)
      } else {
        Quit_Notification(messageNumber, userName, userID, message)
      }
    }
  }
}
