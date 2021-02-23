package it.multicoredev.utils.listeners.chat;

import it.multicoredev.mbcore.spigot.Chat;
import it.multicoredev.utils.Config;
import me.clip.placeholderapi.PlaceholderAPI;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import static it.multicoredev.utils.SimpleUtilities.*;

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
public class ChatListener {

    static void onChat(AsyncPlayerChatEvent event, Config.Chat chat) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (!VAULT && !LUCKPERMS) {
            String format = getDefFormat(player, message, null, chat);
            if (format != null) event.setFormat(format);
        } else {
            String format;

            if (LUCKPERMS) format = getLPFormat(player, message, chat);
            else format = getVaultFormat(player, message, chat);

            if (format != null) event.setFormat(format);
        }
    }

    private static String formatReplacer(String format, Player player, String message, String group, boolean resetColor) {
        format = format.replace("{DISPLAYNAME}", resetColor ? Chat.getDiscolored(player.getDisplayName()) : player.getDisplayName())
                .replace("{NAME}", Chat.getDiscolored(player.getName()))
                .replace("{GROUP}", Chat.getDiscolored(group != null ? group : ""));
        if (PLACEHOLDERAPI) {
            format = PlaceholderAPI.setPlaceholders(player, format);
            if (player.hasPermission("simpleutils.chat-placeholders")) {
                message = PlaceholderAPI.setPlaceholders(player, message);
            }
        }

        format = format.replaceAll("%[^%]+%", "");
        message = message.replaceAll("%[^%]+%", "");
        format = format.replace("%", "");
        message = message.replace("%", "");

        return Chat.getTranslated(format)
                .replace("{MESSAGE}", Chat.getTranslated(message, player, "simpletuils.chat-colors"));
    }

    private static String getDefFormat(Player player, String message, String group, Config.Chat chat) {
        String format = null;
        if (group != null) format = chat.getGroupFormat("default", null);
        if (format == null) format = chat.defChatFormat;
        if (format == null) return null;

        return formatReplacer(format, player, message, group, chat.resetDisplayNamesColors);
    }

    private static List<Group> getLPGroups(User user) {
        List<Group> groups = new ArrayList<>();
        List<String> names = user.getNodes(NodeType.INHERITANCE).stream().map(InheritanceNode::getGroupName).collect(Collectors.toList());

        GroupManager manager = luckPerms.getGroupManager();
        names.forEach(name -> {
            Group group = manager.getGroup(name);
            if (group != null) groups.add(group);
        });

        groups.sort((o1, o2) -> {
            OptionalInt w1 = o1.getWeight();
            OptionalInt w2 = o2.getWeight();

            if (w1.isPresent() && w2.isPresent()) {
                return Integer.compare(w1.getAsInt(), w2.getAsInt());
            } else {
                if (w1.isPresent()) return 1;
                else if (w2.isPresent()) return -1;
                else return 0;
            }
        });

        return groups;
    }

    private static String getLPFormat(Player player, String message, Config.Chat chat) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());

        String group = "";
        String format = null;

        if (user != null) {
            List<Group> groups = getLPGroups(user);

            for (Group g : groups) {
                format = chat.getGroupFormat(g.getName(), null);
                if (format != null) {
                    group = g.getDisplayName() != null ? g.getDisplayName() : g.getName();
                    break;
                }
            }
        }

        if (format == null) return getDefFormat(player, message, group, chat);
        return formatReplacer(format, player, message, group, chat.resetDisplayNamesColors);
    }

    private static boolean containsGroup(String group, String[] groups) {
        for (String g : groups) {
            if (group.equalsIgnoreCase(g)) return true;
        }

        return false;
    }

    private static String getVaultFormat(Player player, String message, Config.Chat chat) {
        try {
            String[] groups = permissions.getPlayerGroups(player);
            if (groups.length == 0) return null;

            String group = "";
            String format = null;

            for (String g : chat.getGroups()) {
                if (containsGroup(g, groups)) {
                    group = g;
                    format = chat.getGroupFormat(g, null);
                    break;
                }
            }

            if (format == null) return getDefFormat(player, message, group, chat);
            return formatReplacer(format, player, message, group, chat.resetDisplayNamesColors);
        } catch (UnsupportedOperationException ignored) {
            return getDefFormat(player, message, null, chat);
        }
    }
}
