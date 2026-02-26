package pers.neige.neigeitems.command.subcommand.mm

import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import pers.neige.colonel.context.Context
import pers.neige.colonel.node.impl.LiteralNode
import pers.neige.neigeitems.annotation.CustomField
import pers.neige.neigeitems.colonel.argument.command.FileNameArgument
import pers.neige.neigeitems.colonel.argument.command.MaybeMMItemIdArgument
import pers.neige.neigeitems.manager.HookerManager.mythicMobsHooker
import pers.neige.neigeitems.manager.ItemManager
import pers.neige.neigeitems.utils.ConfigUtils.getDirectoryOrCreate
import pers.neige.neigeitems.utils.ConfigUtils.getFileOrCreate
import pers.neige.neigeitems.utils.ItemUtils.getName
import pers.neige.neigeitems.utils.LangUtils.sendLang
import pers.neige.neigeitems.utils.SchedulerUtils.async
import java.io.File

/**
 * ni mm load指令
 */
object Load {
    @JvmStatic
    @CustomField(fieldType = "mm")
    val load = LiteralNode.literal<CommandSender, Unit>("load", "cover")
        .thenArgument("itemId", MaybeMMItemIdArgument.INSTANCE)
        .setNullExecutor { context ->
            save(context, context.getArgument("itemId"))
        }
        .thenArgument("path", FileNameArgument { getDirectoryOrCreate("Items") })
        .setNullExecutor { context ->
            save(context, context.getArgument("itemId"), context.getArgument("path"))
        }
        .rootNode()

    @JvmStatic
    @CustomField(fieldType = "mm")
    val loadAll = LiteralNode.literal<CommandSender, Unit>("loadAll")
        .thenArgument("path", FileNameArgument { getDirectoryOrCreate("Items") })
        .setNullExecutor { context ->
            saveAll(context, context.getArgument("path"))
        }
        .rootNode()

    private fun save(context: Context<CommandSender, Unit>, id: String, path: String = "$id.yml") {
        val file = getFileOrCreate("Items${File.separator}$path")
        val config = YamlConfiguration.loadConfiguration(file)
        save(context, id, file, config)
    }

    private fun saveAll(context: Context<CommandSender, Unit>, path: String) {
        val file = getFileOrCreate("Items${File.separator}$path")
        val config = YamlConfiguration.loadConfiguration(file)
        mythicMobsHooker!!.getItemIds().forEach {
            save(context, it, file, config)
        }
    }

    private fun save(context: Context<CommandSender, Unit>, id: String, file: File, config: YamlConfiguration) {
        val cover = context.getArgument<String>("load").equals("cover", true)
        val sender = context.source ?: return
        val itemStack = try {
            mythicMobsHooker!!.getItemStackSync(id) ?: let {
                sender.sendLang(
                    "Messages.unknownItem", mapOf(
                        Pair("{itemID}", id)
                    )
                )
                return
            }
        } catch (error: Throwable) {
            sender.sendLang(
                "Messages.invalidMMItem", mapOf(
                    Pair("{itemID}", id),
                )
            )
            error.printStackTrace()
            return
        }
        async {
            when (ItemManager.saveItem(itemStack, id, file, config, cover)) {
                // 保存成功
                ItemManager.SaveResult.SUCCESS -> {
                    sender.sendLang(
                        "Messages.successSaveInfo", mapOf(
                            Pair("{name}", itemStack.getName()),
                            Pair("{itemID}", id),
                            Pair("{path}", file.path)
                        )
                    )
                }
                // 已存在对应ID物品
                ItemManager.SaveResult.CONFLICT -> {
                    if (cover) return@async
                    sender.sendLang(
                        "Messages.existedKey", mapOf(
                            Pair("{itemID}", id)
                        )
                    )
                }
                // 你保存了个空气
                ItemManager.SaveResult.AIR -> sender.sendLang("Messages.airItem")
            }
        }
    }
}