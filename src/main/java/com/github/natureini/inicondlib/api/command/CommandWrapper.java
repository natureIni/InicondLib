package com.github.natureini.inicondlib.api.command;

import com.github.natureini.inicondlib.api.InicondLibProfile;
import io.netty.handler.logging.LogLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class CommandWrapper {

    private CommandContainer container;
    private final Method command;

    private final String name;
    private final String[] subNames; // サブネーム: 例えば、"time set 0" の "set" の部分
    private final int paramLen; // 引数: 例えば、"time set 0" の "0" の部分
    private final boolean playerOnly;
    private final String permission;

    public CommandWrapper(CommandContainer container, Method command) {

        if (command.getAnnotation(CommandOption.class) == null)
            throw new IllegalArgumentException();

        this.container = container;
        this.command = command;

        String[] names = command.getName().split("_");

        this.name = names[0];
        this.subNames = Arrays.copyOfRange(names, 1, names.length);
        this.paramLen = this.command.getParameterCount() - 1; // CommandSender の分は除外
        this.playerOnly = this.command.getParameterTypes()[0].isAssignableFrom(Player.class);
        this.permission = this.command.getAnnotation(CommandOption.class).permission();

    }

    public String getName() {
        return name;
    }

    public String[] getSubNames() {
        return subNames;
    }

    public int getParamLen() {
        return paramLen;
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }

    public String getPermission() {
        return permission;
    }

    /**
     * コマンドを実行する
     * @param sender 実行者
     * @param args コマンドの引数 (サブネームは含まない)
     */
    public void perform(InicondLibProfile profile, CommandSender sender, String[] args) throws IllegalAccessException {

        if (profile.getMessage().checkWithMessage(sender, "command.playerOnly", !playerOnly || sender instanceof Player))
            return;

        try {

            if (paramLen == 0)
                command.invoke(container, sender);

            if (paramLen == 1) {

                Object[] params = new Object[2];
                params[0] = sender;

                try {
                    params[1] = castInternally(args[0], command.getParameterTypes()[1]);
                }

                catch (Exception e) {
                    profile.getMessage().sendConfigMessage(sender, LogLevel.ERROR, "command.unknown");
                    return;
                }

                command.invoke(container, params);

            }

            // 引数の個数が 2 以上なら一般に処理
            else {

                Class<?>[] types = command.getParameterTypes();

                Object[] params = new Object[args.length + 1];
                params[0] = sender;

                for (int i = 0; i < args.length; i++) {
                    try {
                        params[i + 1] = castInternally(args[i], types[i + 1]);
                    }
                    catch (Exception e) {
                        profile.getMessage().sendConfigMessage(sender, LogLevel.ERROR, "command.unknown");
                        return;
                    }
                }

                command.invoke(container, params);

            }

        }

        catch (IllegalAccessException e) {
            throw e;
        }

        catch (InvocationTargetException e) {
            System.out.println(e);
            System.out.println(e.getTargetException());
        }

    }

    private static Object castInternally(String string, Class<?> type) throws Exception {
        switch (type.getSimpleName()) {
            case "boolean":
                return Boolean.parseBoolean(string);
            case "byte":
                return Byte.parseByte(string);
            case "short":
                return Short.parseShort(string);
            case "int":
                return Integer.parseInt(string);
            case "long":
                return Long.parseLong(string);
            case "float":
                return Float.parseFloat(string);
            case "double":
                return Double.parseDouble(string);
            default:
                return string;
        }
    }

}
