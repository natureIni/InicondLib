package com.github.natureini.inicondlib;

import com.github.natureini.inicondlib.api.command.CommandContainer;
import com.github.natureini.inicondlib.api.command.CommandOption;
import org.bukkit.command.CommandSender;

public class InicondLibCommand implements CommandContainer {

    @CommandOption(permission = "info")
    public void inicondlib(CommandSender sender) {
        sender.sendMessage(InicondLib.getInstance().getDescription().getVersion());
    }

}
