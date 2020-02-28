package com.github.natureini.inicondlib.api.configuration;

import com.github.natureini.inicondlib.InicondLibMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * resources 以下の .yml ファイルを JavaPlugin の config のように扱うためのクラス
 */
public class ResourceConfiguration {

    private final JavaPlugin plugin;
    private final String fileName;

    private FileConfiguration config;

    public ResourceConfiguration(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        reloadConfig();
    }

    public FileConfiguration getConfig() {
        if (config == null)
            reloadConfig();
        return config;
    }

    public void reloadConfig() {

        config = YamlConfiguration.loadConfiguration(getFile());
        InputStream defConfigStream = plugin.getResource(fileName);

        if (defConfigStream == null)
            return;

        config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));

    }

    public void saveConfig() {

        if (config == null)
            return;

        try {
            getConfig().save(getFile());
        }

        catch (IOException e) {
            System.out.println(InicondLibMessage.FAILED_TO_READ_CONFIG + fileName);
            System.out.println(e);
        }

    }

    public void saveDefaultConfig() {
        if (!getFile().exists())
            plugin.saveResource(fileName, false);
    }

    public File getFile() {
        return new File(plugin.getDataFolder().getAbsolutePath(), fileName);
    }

}
