package com.github.natureini.inicondlib;

import com.github.natureini.inicondlib.api.InicondLibProfile;
import com.github.natureini.inicondlib.api.configuration.ResourceConfiguration;
import io.netty.handler.logging.LogLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class InicondLibMessage {

    public static final String FAILED_TO_READ_CONFIG = "設定ファイルの保存に失敗しました: ";
    private static final ResourceConfiguration config = new ResourceConfiguration(InicondLib.getInstance(), "messages.yml");

    private final InicondLibProfile profile;

    public InicondLibMessage(InicondLibProfile profile) {
        this.profile = profile;
        config.saveDefaultConfig();
        config.reloadConfig();
    }

    public static ResourceConfiguration getConfig() {
        return config;
    }

    public static String getConfigMessage(String path) {
        return config.getConfig().getString(path);
    }

    public void sendConfigMessage(CommandSender sender, LogLevel level, String path) {
        sendMessage(sender, level, getConfigMessage(path));
    }

    public void sendMessage(CommandSender sender, LogLevel level, String message) {
        sender.sendMessage(getPrefix(level) + message);
    }

    public boolean checkWithMessage(CommandSender sender, String messagePath, boolean value) {
        if (!value)
            sendConfigMessage(sender, LogLevel.ERROR, messagePath);
        return value;
    }

    public void broadcast(LogLevel level, String message) {
        Bukkit.broadcastMessage(getPrefix(level) + message);
    }

    public void debug(Object message) {
        broadcast(LogLevel.DEBUG, message.toString());
    }

    private String getPrefix(LogLevel level) {
        switch (level) {
            case TRACE:
                return "§8[" + profile.prefix + "] ";
            case DEBUG:
                return "§7[" + profile.prefix + "] ";
            case INFO:
                return "§a[" + profile.prefix + "] ";
            case WARN:
                return "§c[" + profile.prefix + "] ";
            case ERROR:
                return "§4[" + profile.prefix + "] ";
        }
        throw new IllegalArgumentException();
    }

}
