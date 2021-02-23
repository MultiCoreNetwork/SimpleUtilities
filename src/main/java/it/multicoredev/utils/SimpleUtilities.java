package it.multicoredev.utils;

import it.multicoredev.mbcore.spigot.Chat;
import it.multicoredev.mbcore.spigot.config.ConfigManager;
import it.multicoredev.utils.commands.GmCommand;
import it.multicoredev.utils.commands.ReloadCommand;
import it.multicoredev.utils.commands.SetspawnCommand;
import it.multicoredev.utils.commands.SpawnCommand;
import it.multicoredev.utils.listeners.chat.*;
import it.multicoredev.utils.listeners.spawn.*;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

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
public class SimpleUtilities extends JavaPlugin {
    public static File configFile;
    private Config config;
    private Storage storage;
    public static boolean VAULT;
    public static boolean LUCKPERMS;
    public static boolean PLACEHOLDERAPI;

    public static Permission permissions;
    public static LuckPerms luckPerms;

    @Override
    public void onEnable() {
        configFile = new File(getDataFolder(), "config.json");

        if (!initConfig()) {
            onDisable();
            return;
        }

        initVault();
        initLuckPerms();
        initPlaceholderAPI();

        getCommand("simplereload").setExecutor(new ReloadCommand(this, config.STRINGS));

        if (config.MODULES.gm) {
            GmCommand command = new GmCommand(config.STRINGS);
            getCommand("gm").setExecutor(command);
            getCommand("gm").setTabCompleter(command);
        }
        if (config.MODULES.chat) {
            Listener chatListener;
            switch (config.CHAT.eventPriority) {
                case "LOWEST":
                    chatListener = new ChatListenerLowest(config.CHAT);
                    break;
                case "LOW":
                    chatListener = new ChatListenerLow(config.CHAT);
                    break;
                case "HIGH":
                    chatListener = new ChatListenerHigh(config.CHAT);
                    break;
                case "HIGHEST":
                    chatListener = new ChatListenerHighest(config.CHAT);
                    break;
                case "MONITOR":
                    chatListener = new ChatListenerMonitor(config.CHAT);
                    break;
                case "NORMAL":
                default:
                    chatListener = new ChatListenerNormal(config.CHAT);
                    break;
            }

            getServer().getPluginManager().registerEvents(chatListener, this);
        }
        if (config.MODULES.spawn || config.MODULES.welcome) {
            try {
                storage = new Storage(this);
            } catch (SQLException e) {
                e.printStackTrace();
                onDisable();
                return;
            }

            if (config.MODULES.spawn) {
                getCommand("setspawn").setExecutor(new SetspawnCommand(this, config));

                SpawnCommand command = new SpawnCommand(config);
                getCommand("spawn").setExecutor(command);
                getCommand("spawn").setExecutor(command);
            }

            Listener spawnListener;
            switch (config.SPAWN.eventPriority) {
                case "LOWEST":
                    spawnListener = new SpawnListenerLowest(config, storage);
                    break;
                case "LOW":
                    spawnListener = new SpawnListenerLow(config, storage);
                    break;
                case "HIGH":
                    spawnListener = new SpawnListenerHigh(config, storage);
                    break;
                case "HIGHEST":
                    spawnListener = new SpawnListenerHighest(config, storage);
                    break;
                case "MONITOR":
                    spawnListener = new SpawnListenerMonitor(config, storage);
                    break;
                case "NORMAL":
                default:
                    spawnListener = new SpawnListenerNormal(config, storage);
                    break;
            }

            getServer().getPluginManager().registerEvents(spawnListener, this);
        }

        Chat.info("&3SimpleUtilities loaded and &2enabled&3.");
    }

    @Override
    public void onDisable() {
        Chat.info("&3SipleUtilities &4disabled&3.");
    }

    private boolean initConfig() {
        if (!getDataFolder().exists() || !getDataFolder().isDirectory()) {
            if (!getDataFolder().mkdir()) return false;
        }

        if (!configFile.exists() || !configFile.isFile()) {
            try {
                config = new Config();
                ConfigManager.saveConfig(configFile, config);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            try {
                config = ConfigManager.loadConfig(configFile, Config.class);
                if (config.completeMissing()) ConfigManager.saveConfig(configFile, config);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    private void initVault() {
        VAULT = getServer().getPluginManager().getPlugin("Vault") != null;

        if (VAULT) {
            Chat.info("&3Vault found! Enabling vault modules.");

            RegisteredServiceProvider<Permission> serviceProvider = getServer().getServicesManager().getRegistration(Permission.class);
            if (serviceProvider == null) {
                VAULT = false;
                Chat.warning("&cVault found but cannot load Permissions service.");
                return;
            }

            permissions = serviceProvider.getProvider();

            if (permissions == null) {
                VAULT = false;
                Chat.warning("&cVault found but cannot load Permissions service.");
            }
        }
    }

    private void initLuckPerms() {
        LUCKPERMS = getServer().getPluginManager().getPlugin("LuckPerms") != null;

        if (LUCKPERMS) {
            Chat.info("&3LuckPerms found! Enabling LuckPerms support.");

            RegisteredServiceProvider<LuckPerms> serviceProvider = getServer().getServicesManager().getRegistration(LuckPerms.class);
            if (serviceProvider == null) {
                LUCKPERMS = false;
                Chat.warning("&cLuckPerms found but cannot load API.");
                return;
            }

            luckPerms = serviceProvider.getProvider();

            if (luckPerms == null) {
                LUCKPERMS = false;
                Chat.warning("&cVault found but cannot load API.");
            }
        }
    }

    private void initPlaceholderAPI() {
        PLACEHOLDERAPI = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

        if (PLACEHOLDERAPI) {
            Chat.info("&3PlaceholderAPI found! Enabling Placeholders API.");
        }
    }
}
