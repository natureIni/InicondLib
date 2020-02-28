package com.github.natureini.inicondlib.api;

import com.github.natureini.inicondlib.InicondLibMessage;
import org.bukkit.command.CommandSender;

public class InicondLibProfile {

    public final String prefix;
    private final String permissionPrefix;

    private InicondLibMessage message;

    public InicondLibProfile(String prefix) {

        this.prefix = prefix;
        this.permissionPrefix = prefix.toLowerCase() + ".";

        message = new InicondLibMessage(this);

    }

    public InicondLibMessage getMessage() {
        return message;
    }

    public boolean hasPermission(CommandSender sender, String name) {
        return sender.hasPermission(permissionPrefix + name);
    }

}
