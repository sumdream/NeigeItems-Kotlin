package pers.neige.neigeitems.command.subcommand

import com.alibaba.fastjson2.parseObject
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pers.neige.colonel.arguments.impl.StringArgument
import pers.neige.colonel.node.impl.LiteralNode
import pers.neige.neigeitems.action.ActionContext
import pers.neige.neigeitems.annotation.CustomField
import pers.neige.neigeitems.colonel.argument.command.FunctionArgument
import pers.neige.neigeitems.colonel.argument.command.PlayerArgument

/**
 * ni function指令
 */
object Function {
    @JvmStatic
    @CustomField(fieldType = "root")
    val function = LiteralNode.literal<CommandSender, Unit>("function")
        .thenArgument("player", PlayerArgument.NULLABLE)
        .thenArgument("function", FunctionArgument.INSTANCE)
        .thenArgument("data", StringArgument.builder<CommandSender, Unit>().readAll(true).build().setDefaultValue(null))
        .setNullExecutor { context ->
            val player = context.getArgument<Player>("player")
            val functionContainer = context.getArgument<FunctionArgument.FunctionContainer>("function")
            val data = context.getArgument<String>("data")
            val params = when (data) {
                null -> HashMap()
                else -> data.parseObject<HashMap<String, Any>>()
            }
            functionContainer.function!!.eval(ActionContext(player, params, params))
        }
}