package com.github.natureini.inicondlib.api.command;

import com.github.natureini.inicondlib.api.InicondLibProfile;
import com.google.common.collect.HashMultimap;
import io.netty.handler.logging.LogLevel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Comparator;

public class CommandHandler {

    private final InicondLibProfile profile;
    // キーは ((サブネームの長さ) + (引数の長さ) as int) + (メソッド名)
    private final HashMultimap<String, CommandWrapper> commands = HashMultimap.create();

    public CommandHandler(InicondLibProfile profile) {
        this.profile = profile;
    }

    public CommandHandler(InicondLibProfile profile, CommandContainer... containers) {
        this(profile);
        Arrays.stream(containers).forEach(this::register);
    }

    /**
     * CommandContainer に宣言されているコマンドを取得・登録する
     *
     * @param container CommandContainer
     */
    public void register(CommandContainer container) {
        Arrays.stream(container.getClass().getMethods())
                .filter(m -> m.getAnnotation(CommandOption.class) != null)
                .map(m -> new CommandWrapper(container, m))
                .forEach(c -> commands.put((c.getSubNames().length + c.getParamLen()) + c.getName(), c));
    }

    public boolean handle(CommandSender sender, Command command, String[] args) {

        CommandWrapper wrapper = commands.get(args.length + command.getName())
                .stream()
                .filter(w -> {
                    for (int i = 0; i < w.getSubNames().length; i++) {
                        if (!w.getSubNames()[i].equalsIgnoreCase(args[i]))
                            return false;
                    }
                    return true;
                })
                .max(Comparator.comparingInt(w -> w.getSubNames().length))
                .orElse(null);

        if (!profile.getMessage().checkWithMessage(sender, "command.unknown", wrapper != null)
                || !profile.getMessage().checkWithMessage(sender, "command.permission-denied", profile.hasPermission(sender, wrapper.getPermission()))) {
            return false;
        }

        try {
            wrapper.perform(profile, sender, wrapper.getParamLen() == args.length ? args : Arrays.copyOfRange(args, wrapper.getSubNames().length, args.length));
        }

        catch (IllegalAccessException e) {
            System.out.println(e);
        }

        return true;

    }

}
