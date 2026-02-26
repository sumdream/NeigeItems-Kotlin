package pers.neige.neigeitems.command.subcommand

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pers.neige.colonel.arguments.impl.StringArgument
import pers.neige.colonel.node.impl.LiteralNode
import pers.neige.neigeitems.annotation.CustomField
import pers.neige.neigeitems.colonel.argument.command.IntegerArgument
import pers.neige.neigeitems.colonel.argument.command.MaybeEditorIdArgument
import pers.neige.neigeitems.colonel.argument.command.PlayerArgument
import pers.neige.neigeitems.manager.ItemEditorManager

/**
 * ni editHand/editOffHand/editSlot指令
 */
object Editor {
    @JvmStatic
    @CustomField(fieldType = "root")
    val editHand = LiteralNode.literal<CommandSender, Unit>("editHand")
        .thenArgument("player", PlayerArgument.NULLABLE)
        .thenArgument("editor", MaybeEditorIdArgument.INSTANCE)
        .thenArgument("content", StringArgument.builder<CommandSender, Unit>().readAll(true).build())
        .setNullExecutor { context ->
            val player = context.getArgument<Player>("player")
            val editorId = context.getArgument<String>("editor")
            val content = context.getArgument<String>("content")
            ItemEditorManager.runEditor(
                editorId,
                content,
                player.inventory.itemInMainHand,
                player
            )
        }

    @JvmStatic
    @CustomField(fieldType = "root")
    val editOffHand = LiteralNode.literal<CommandSender, Unit>("editOffHand")
        .thenArgument("player", PlayerArgument.NULLABLE)
        .thenArgument("editor", MaybeEditorIdArgument.INSTANCE)
        .thenArgument("content", StringArgument.builder<CommandSender, Unit>().readAll(true).build())
        .setNullExecutor { context ->
            val player = context.getArgument<Player>("player")
            val editorId = context.getArgument<String>("editor")
            val content = context.getArgument<String>("content")
            ItemEditorManager.runEditor(
                editorId,
                content,
                player.inventory.itemInOffHand,
                player
            )
        }

    @JvmStatic
    @CustomField(fieldType = "root")
    val editSlot = LiteralNode.literal<CommandSender, Unit>("editSlot")
        .thenArgument("player", PlayerArgument.NULLABLE)
        .thenArgument("slot", IntegerArgument.SLOT)
        .thenArgument("editor", MaybeEditorIdArgument.INSTANCE)
        .thenArgument("content", StringArgument.builder<CommandSender, Unit>().readAll(true).build())
        .setNullExecutor { context ->
            val player = context.getArgument<Player>("player")
            val slot = context.getArgument<Int?>("slot")!!
            val itemStack = player.inventory.getItem(slot) ?: return@setNullExecutor
            val editorId = context.getArgument<String>("editor")
            val content = context.getArgument<String>("content")
            ItemEditorManager.runEditor(
                editorId,
                content,
                itemStack,
                player
            )
        }
}