package pers.neige.neigeitems.item

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.entity.Item
import pers.neige.neigeitems.NeigeItems
import pers.neige.neigeitems.utils.PlayerUtils.getMetadataEZ

/**
 * 用于实现掉落物隐藏功能
 */
class ItemHider {
    init {
        val protocolManager = ProtocolLibrary.getProtocolManager()
        protocolManager.addPacketListener(object : PacketAdapter(
            NeigeItems.getInstance(), ListenerPriority.LOWEST, PacketType.Play.Server.ENTITY_METADATA
        ) {
            override fun onPacketSending(event: PacketEvent) {
                val receiver = event.player
                val id = event.packet.integers.read(0)
                if (id < 0) return
                val entity = kotlin.runCatching {
                    protocolManager.getEntityFromID(receiver.world, id)
                }.getOrNull()
                if (entity !is Item || !entity.hasMetadata("NI-Owner")) return
                // 获取归属者
                val owner = entity.getMetadataEZ("NI-Owner", "") as String
                // 是否隐藏掉落物
                val hide = entity.getMetadataEZ("NI-Hide", 0.toByte()) as Byte

                // 检测拾取者是否是拥有者以及是否隐藏掉落物
                if (receiver.name != owner && hide == 1.toByte()) {
                    // 隐藏掉落物
                    event.isCancelled = true
                }
            }
        })
    }
}