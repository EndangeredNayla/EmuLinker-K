package org.emulinker.kaillera.controller.connectcontroller.protocol

import java.lang.NumberFormatException
import java.nio.ByteBuffer
import kotlin.Throws
import org.emulinker.kaillera.controller.messaging.MessageFormatException
import org.emulinker.kaillera.pico.AppModule
import org.emulinker.util.EmuUtil

/**
 * Server connection response that contains a port for a private server.
 *
 * See [ConnectMessage] for more documentation on the handshake. This message was formerly called
 * `ConnectMessage_HELLOD00D`.
 */
data class RequestPrivateKailleraPortResponse(val port: Int) : ConnectMessage() {
  override val iD = ID

  override val bodyBytesPlusMessageIdType = ID.length + port.toString().length + 1

  override fun writeTo(buffer: ByteBuffer) {
    buffer.put(AppModule.charsetDoNotUse.encode(ID))
    EmuUtil.writeString(buffer, port.toString(), 0x00, AppModule.charsetDoNotUse)
  }

  companion object {
    const val ID = "HELLOD00D"

    @Throws(MessageFormatException::class)
    fun parse(msg: String): ConnectMessage {

      require(msg.length >= ID.length + 2) { "Invalid message length: ${msg.length}" }
      require(msg.startsWith(ID)) { "Message ($msg) must start with ID ($ID)" }
      require(msg.last().code == 0x00) { "Missing stop byte 0x00!" }
      return try {
        val port = msg.substring(ID.length, msg.length - 1).toInt()
        RequestPrivateKailleraPortResponse(port)
      } catch (e: NumberFormatException) {
        throw MessageFormatException("Invalid port number!")
      }
    }
  }
}
