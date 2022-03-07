package icu.dnddl.plugin.genshin.mirai

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.message.nextMessage

class MessageContext(
    private val messageEvent: MessageEvent,
) {
    val sender get() = messageEvent.sender
    val subject get() = messageEvent.subject
    val message get() = messageEvent.message

    suspend infix fun send(message: Message): MessageReceipt<Contact> =
        subject.sendMessage(message)

    suspend infix fun send(plain: String) = subject.sendMessage(plain)


    suspend infix fun sendWithResult(plain: String): Result<MessageReceipt<Contact>> {
        val result = kotlin.runCatching {
            subject.sendMessage(plain)
        }

        return result
    }

    suspend fun receive(plain: String, timeoutMillis: Long = -1, tryLimit: Int = 1): Result<MessageChain> {
        var times = 0
        return kotlin.runCatching {
            messageEvent.nextMessage(timeoutMillis) {
                if (times > tryLimit) throw IllegalStateException()
                times++
                this.message.content == plain
            }
        }
    }

    suspend fun receiveWithResult(plain: String, timeoutMillis: Long = -1, tryLimit: Int = 1): Result<MessageChain> {
        var times = 0
        val result = kotlin.runCatching {
            messageEvent.nextMessage(timeoutMillis) {
                if (times > tryLimit) throw IllegalStateException()
                times++
                this.message.content == plain
            }
        }

        return result
    }
}