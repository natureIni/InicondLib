package com.github.natureini.inicondlib.api.command;

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
     * @return 引数の型が誤っているか、メソッド呼出そのものに失敗した場合 false
     */
    public boolean perform(CommandSender sender, String[] args) throws IllegalAccessException {

        try {

            if (paramLen == 0)
                command.invoke(container, sender);

            else {

                Class<?>[] types = command.getParameterTypes();

                Object[] param = new Object[args.length + 1];
                param[0] = sender;

                for (int i = 0; i < args.length; i++) {
                    // キャストして配列に入れる
                    try {
                        switch (types[i + 1].getSimpleName()) {
                            case "boolean":
                                param[i + 1] = Boolean.parseBoolean(args[i]);
                                break;
                            case "byte":
                                param[i + 1] = Byte.parseByte(args[i]);
                                break;
                            case "short":
                                param[i + 1] = Short.parseShort(args[i]);
                                break;
                            case "int":
                                param[i + 1] = Integer.parseInt(args[i]);
                                break;
                            case "long":
                                param[i + 1] = Long.parseLong(args[i]);
                                break;
                            case "float":
                                param[i + 1] = Float.parseFloat(args[i]);
                                break;
                            case "double":
                                param[i + 1] = Double.parseDouble(args[i]);
                                break;
                            default:
                                param[i + 1] = args[i];
                                break;
                        }
                    }

                    catch (Exception e) {
                        return false;
                    }
                }

                command.invoke(container, param);

            }

            return true;

        }

        catch (IllegalAccessException e) {
            throw e;
        }

        catch (InvocationTargetException e) {
            System.out.println(e);
            System.out.println(e.getTargetException());
        }

        return false;

    }

}
