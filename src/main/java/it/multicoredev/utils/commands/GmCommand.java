package it.multicoredev.utils.commands;

import it.multicoredev.mbcore.spigot.Chat;
import it.multicoredev.mbcore.spigot.util.TabCompleterUtil;
import it.multicoredev.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright © 2021 by Lorenzo Magni
 * This file is part of SimpleUtilities.
 * SimpleUtilities is under "The 3-Clause BSD License", you can find a copy <a href="https://opensource.org/licenses/BSD-3-Clause">here</a>.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
public class GmCommand implements CommandExecutor, TabExecutor {
    private final Config.Strings strings;

    public GmCommand(Config.Strings strings) {
        this.strings = strings;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("simpleutils.gm")) {
            Chat.send(strings.insufficientPerms, sender);
            return true;
        }

        if (args.length < 1) {
            Chat.send(strings.incorrectUsage.replace("{usage}", "/gm <gamemode> [player]"), sender);
            return true;
        }

        if (args.length < 2 && !(sender instanceof Player)) {
            Chat.send(strings.incorrectUsage.replace("{usage}", "/gm <gamemode> <player>"), sender);
            return true;
        }

        String gm = args[0];
        String player = args.length == 2 ? args[1] : sender.getName();

        GameMode gameMode = null;
        switch (gm.toLowerCase()) {
            case "0":
            case "s":
                if (!hasPermission(sender, 0)) {
                    Chat.send(strings.insufficientPerms, sender);
                    return true;
                }

                gameMode = GameMode.SURVIVAL;
                break;
            case "1":
            case "c":
                if (!hasPermission(sender, 1)) {
                    Chat.send(strings.insufficientPerms, sender);
                    return true;
                }

                gameMode = GameMode.CREATIVE;
                break;
            case "2":
            case "a":
                if (!hasPermission(sender, 2)) {
                    Chat.send(strings.insufficientPerms, sender);
                    return true;
                }

                gameMode = GameMode.ADVENTURE;
                break;
            case "3":
            case "g":
                if (!hasPermission(sender, 3)) {
                    Chat.send(strings.insufficientPerms, sender);
                    return true;
                }

                gameMode = GameMode.SPECTATOR;
                break;
        }

        if (gameMode == null) {
            Chat.send(strings.invalidGamemode, sender);
            return true;
        }

        Player target = Bukkit.getPlayer(player);
        if (target == null) {
            Chat.send(strings.playerNotFound, sender);
            return true;
        }

        target.setGameMode(gameMode);
        if (target != sender) {
            Chat.send(strings.gamemodeSet.replace("{gamemode}", gameMode.name()).replace("{player}", target.getDisplayName()), sender);
        }
        return true;
    }

    private boolean hasPermission(CommandSender sender, int gm) {
        return sender.hasPermission("simpleutils.gm." + gm) || sender.hasPermission("simpleutils.gm.*");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();

            if (hasPermission(sender, 0)) {
                completions.add("0");
                completions.add("s");
            }
            if (hasPermission(sender, 1)) {
                completions.add("1");
                completions.add("c");
            }
            if (hasPermission(sender, 2)) {
                completions.add("2");
                completions.add("a");
            }
            if (hasPermission(sender, 3)) {
                completions.add("3");
                completions.add("g");
            }

            return TabCompleterUtil.getCompletions(args[0], completions);
        } else if (args.length == 2) {
            return TabCompleterUtil.getPlayers(args[1], sender.hasPermission("simpleutils.see-vanish"));
        }

        return null;
    }
}
