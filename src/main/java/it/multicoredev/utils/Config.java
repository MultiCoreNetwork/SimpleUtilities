package it.multicoredev.utils;

import com.google.gson.annotations.SerializedName;
import it.multicoredev.mbcore.spigot.config.JsonConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
public class Config extends JsonConfig {
    @SerializedName("modules")
    public Modules MODULES;
    @SerializedName("chat")
    public Chat CHAT;
    @SerializedName("spawn")
    public Spawn SPAWN;
    @SerializedName("strings")
    public Strings STRINGS;

    public Config() {
        init();
    }

    @Override
    protected void init() {
        if (MODULES == null) MODULES = new Modules();
        if (CHAT == null) CHAT = new Chat();
        if (SPAWN == null) SPAWN = new Spawn();
        if (STRINGS == null) STRINGS = new Strings();
    }

    public static class Modules extends JsonConfig {
        public boolean chat;
        public boolean spawn;
        public boolean gm;
        public boolean welcome;

        public Modules() {
            init();
        }

        @Override
        protected void init() {
            chat = true;
            spawn = true;
            gm = true;
            welcome = true;
        }
    }

    public static class Chat extends JsonConfig {
        @SerializedName("event_priority")
        public String eventPriority;
        @SerializedName("reset_displaynames_colors")
        public Boolean resetDisplayNamesColors;
        @SerializedName("default_chat_format")
        public String defChatFormat;
        @SerializedName("group_formats")
        private Map<String, String> groupChatFormats;

        public Chat() {
            init();
        }

        @Override
        protected void init() {
            if (eventPriority == null) eventPriority = "LOWEST";
            if (resetDisplayNamesColors == null) resetDisplayNamesColors = false;
            if (defChatFormat == null) defChatFormat = "&f<&7{DISPLAYNAME}&f>&r {MESSAGE}";
            if (groupChatFormats == null) {
                groupChatFormats = new LinkedHashMap<>();
                groupChatFormats.put("admin", "&7[&4{GROUP}&7] &c{DISPLAYNAME} &f>&r {MESSAGE}");
                groupChatFormats.put("mod", "&7[&6{GROUP}&7] &e{DISPLAYNAME} &f>&r {MESSAGE}");
                groupChatFormats.put("default", "&7[&2{GROUP}&7] &a{DISPLAYNAME} &f>&r {MESSAGE}");
            }
        }

        public List<String> getGroups() {
            return new ArrayList<>(groupChatFormats.keySet());
        }

        public String getGroupFormat(String group, String def) {
            for (Map.Entry<String, String> entry : groupChatFormats.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(group)) return entry.getValue();
            }

            for (Map.Entry<String, String> entry : groupChatFormats.entrySet()) {
                if (entry.getKey().equalsIgnoreCase("default")) return entry.getValue();
            }

            return def;
        }
    }

    public static class Spawn extends JsonConfig {
        @SerializedName("event_priority")
        public String eventPriority;
        @SerializedName("respawn_override")
        public Boolean respawnOverride;
        @SerializedName("spawn_on_join")
        public Boolean spawnOnJoin;
        @SerializedName("spawn_location")
        private SpawnLocation spawnLocation;

        public Spawn() {
            init();
        }

        @Override
        protected void init() {
            if (eventPriority == null) eventPriority = "LOWEST";
            if (respawnOverride == null) respawnOverride = false;
            if (spawnOnJoin == null) spawnOnJoin = false;
        }

        public Location getSpawnLocation() {
            try {
                return new Location(Bukkit.getWorld(spawnLocation.world), spawnLocation.x, spawnLocation.y, spawnLocation.z, spawnLocation.yaw, spawnLocation.pitch);
            } catch (Exception ignored) {
                return null;
            }
        }

        public void setSpawnLocation(Location spawn) {
            spawnLocation = new SpawnLocation(spawn.getWorld().getName(), spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());
        }

        private static class SpawnLocation {
            private String world;
            private double x;
            private double y;
            private double z;
            private float yaw;
            private float pitch;

            public SpawnLocation(String world, double x, double y, double z, float yaw, float pitch) {
                this.world = world;
                this.x = x;
                this.y = y;
                this.z = z;
                this.yaw = yaw;
                this.pitch = pitch;
            }
        }
    }

    public static class Strings extends JsonConfig {
        @SerializedName("gamemode_set")
        public String gamemodeSet;
        @SerializedName("incorrect_usage")
        public String incorrectUsage;
        @SerializedName("insufficient_permissions")
        public String insufficientPerms;
        @SerializedName("invalid_gamemode")
        public String invalidGamemode;
        @SerializedName("join_message")
        public String joinMsg;
        @SerializedName("first_join_message")
        public String joinFirstMsg;
        @SerializedName("not_player")
        public String notPlayer;
        @SerializedName("player_not_found")
        public String playerNotFound;
        @SerializedName("player_teleported_to_spawn")
        public String playerTeleportedToSpawn;
        @SerializedName("quit_message")
        public String quitMsg;
        public String reload;
        @SerializedName("spawn_not_set")
        public String spawnNotSet;
        @SerializedName("spawn_point_set")
        public String spawnPointSet;
        @SerializedName("spawn_teleport")
        public String spawnTeleport;

         @SerializedName("first_join_messages")
         public List<String> joinFirstMsgs;

        public Strings() {
            init();
        }

        @Override
        protected void init() {
            if (gamemodeSet == null) gamemodeSet = "&3Gamemode set to {gamemode} for player {player}.";
            if (incorrectUsage == null) incorrectUsage = "&cIncorrect usage! Usage: &e{usage}";
            if (insufficientPerms == null) insufficientPerms = "&cInsufficient permissions!";
            if (invalidGamemode == null) invalidGamemode = "&cInvalid gamemode.";
            if (joinMsg == null) joinMsg = "&e{DISPLAYNAME} joined the game!";
            if (joinFirstMsg == null) joinFirstMsg = "&e{DISPLAYNAME} joined the game for the first time!";
            if (notPlayer == null) notPlayer = "&cYou are not a player.";
            if (playerNotFound == null) playerNotFound = "&cPlayer not found.";
            if (playerTeleportedToSpawn == null) playerTeleportedToSpawn = "&3Player teleported to spawn point.";
            if (quitMsg == null) quitMsg = "&c{DISPLAYNAME} left the game!";
            if (reload == null) reload = "&3Plugin reloaded.";
            if (spawnNotSet == null) spawnNotSet = "&3Spawn not set.";
            if (spawnPointSet == null) spawnPointSet = "&3Spawn point set";
            if (spawnTeleport == null) spawnTeleport = "&3You have been teleported to spawn point.";

            if (joinFirstMsgs == null) joinFirstMsgs = new ArrayList<>();
        }
    }
}
