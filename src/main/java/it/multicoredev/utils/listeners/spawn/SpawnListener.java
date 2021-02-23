package it.multicoredev.utils.listeners.spawn;

import it.multicoredev.mbcore.spigot.Chat;
import it.multicoredev.utils.Config;
import it.multicoredev.utils.Storage;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import static it.multicoredev.utils.SimpleUtilities.PLACEHOLDERAPI;

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
public class SpawnListener {

    static void onJoin(PlayerJoinEvent event, Config config, Storage storage) {
        if (config.MODULES.spawn) {
            Player player = event.getPlayer();

            if (storage.isPlayerRegistered(player) && !config.SPAWN.spawnOnJoin) return;

            Location spawn = config.SPAWN.getSpawnLocation();
            if (spawn == null) return;

            player.teleport(config.SPAWN.getSpawnLocation());
        }

        if (config.MODULES.welcome) {
            Player player = event.getPlayer();

            if (storage.isPlayerRegistered(player)) {
                String msg = config.STRINGS.joinMsg.replace("{DISPLAYNAME}", player.getDisplayName())
                        .replace("{NAME}", player.getName());
                if (PLACEHOLDERAPI) PlaceholderAPI.setPlaceholders(player, msg);
                msg = msg.replaceAll("%[^%]+%", "");

                Chat.broadcast(Chat.getTranslated(msg));
                event.setJoinMessage(null);
            } else {
                String msg = config.STRINGS.joinFirstMsg.replace("{DISPLAYNAME}", player.getDisplayName())
                        .replace("{NAME}", player.getName());
                if (PLACEHOLDERAPI) PlaceholderAPI.setPlaceholders(player, msg);
                msg = msg.replaceAll("%[^%]+%", "");

                Chat.broadcast(Chat.getTranslated(msg));
                event.setJoinMessage(null);

                if (config.STRINGS.joinFirstMsgs != null && !config.STRINGS.joinFirstMsgs.isEmpty()) {
                    for (String m : config.STRINGS.joinFirstMsgs) {
                        m = m.replace("{DISPLAYNAME}", player.getDisplayName())
                                .replace("{NAME}", player.getName());
                        if (PLACEHOLDERAPI) PlaceholderAPI.setPlaceholders(player, m);
                        m = m.replaceAll("%[^%]+%", "");

                        Chat.send(m, player);
                    }
                }

                storage.registerPlayer(player);
            }
        }
    }

    static void onQuit(PlayerQuitEvent event, Config config) {
        if (!config.MODULES.welcome) return;

        Player player = event.getPlayer();

        String msg = config.STRINGS.quitMsg.replace("{DISPLAYNAME}", player.getDisplayName()
                .replace("{NAME}", player.getName()));
        if (PLACEHOLDERAPI) PlaceholderAPI.setPlaceholders(player, msg);
        msg = msg.replaceAll("%[^%]+%", "");

        event.setQuitMessage(Chat.getTranslated(msg));
    }

    static void onRespawn(PlayerRespawnEvent event, Config config) {
        if (!config.MODULES.spawn || !config.SPAWN.respawnOverride) return;

        Player player = event.getPlayer();

        Location spawn = config.SPAWN.getSpawnLocation();
        if (spawn == null) return;

        player.teleport(config.SPAWN.getSpawnLocation());
    }
}
