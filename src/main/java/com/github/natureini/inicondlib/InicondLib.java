package com.github.natureini.inicondlib;

import com.github.natureini.inicondlib.api.InicondLibProfile;
import com.github.natureini.inicondlib.api.command.CommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class InicondLib extends JavaPlugin {

    private static InicondLib instance;
    private static InicondLibProfile profile;
    private static CommandHandler handler;

    @Override
    public void onEnable() {
        instance = this;
        profile = new InicondLibProfile("InicondLib");
        handler = new CommandHandler(profile, new InicondLibCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return handler.handle(sender, command, args);
    }

    public static InicondLib getInstance() {
        return instance;
    }

    public static InicondLibProfile getProfile() {
        return profile;
    }

}
