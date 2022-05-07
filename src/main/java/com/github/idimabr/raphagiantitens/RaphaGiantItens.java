package com.github.idimabr.raphagiantitens;

import com.github.idimabr.raphagiantitens.commands.SpawnItemCommand;
import com.github.idimabr.raphagiantitens.listeners.DamageListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class RaphaGiantItens extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("giantitem").setExecutor(new SpawnItemCommand());
        Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
