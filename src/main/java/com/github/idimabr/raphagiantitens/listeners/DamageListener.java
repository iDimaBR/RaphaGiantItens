package com.github.idimabr.raphagiantitens.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DamageListener implements Listener {

    private Cache<UUID, Long> delay = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(e.getPlayer().getName().equals("iDimaBR")) e.getPlayer().sendMessage("§aEsse servidor utiliza o plugin: §fRaphaGiantItens");
    }

    @EventHandler
    public void giantDamageCancel(EntityDamageByEntityEvent e){
        if(e.getEntity() == null) return;
        if(e.getDamager() == null) return;

        if(e.getEntity() instanceof Player) {
            if (e.getDamager() instanceof Giant) {
                Giant giant = (Giant) e.getDamager();
                if (giant.getCustomName().startsWith("GiantItem")){
                    e.setCancelled(true);
                }
            }
            if (e.getDamager() instanceof Slime) {
                Slime slime = (Slime) e.getDamager();
                if (slime.getCustomName().startsWith("GiantItem")){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void giantDamageCancellation(EntityDamageEvent e) {
        if (e.getEntity() instanceof Giant) {
            Giant giant = (Giant) e.getEntity();
            if (giant.getCustomName().startsWith("GiantItem"))
                e.setCancelled(true);
        }
        if (e.getEntity() instanceof Slime) {
            Slime slime = (Slime) e.getEntity();
            if (slime.getCustomName().startsWith("GiantItem"))
                e.setCancelled(true);
        }
        if (e.getEntity() instanceof ArmorStand) {
            ArmorStand giant = (ArmorStand) e.getEntity();
            if (giant.getCustomName().startsWith("GiantItem"))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent e) {
        Entity entity = e.getRightClicked();
        if(entity == null) return;

        if (entity.getCustomName().startsWith("GiantItem")) {
            String command = entity.getCustomName().split("Command:")[1];
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", e.getPlayer().getName()));
        }
    }

    public void giantClick(PlayerMoveEvent e){
        Player player = e.getPlayer();

        Location from = e.getFrom();
        Location to = e.getTo();
        if ((from.getX() != to.getX()) && (from.getY() != to.getY()) && (from.getZ() != to.getZ())) {
            if(!delay.asMap().containsKey(player.getUniqueId())){
                delay.put(player.getUniqueId(), System.currentTimeMillis() + 10000);
            }
            if(delay.asMap().containsKey(player.getUniqueId()) && delay.asMap().get(player.getUniqueId()) > System.currentTimeMillis()) {
                player.getNearbyEntities(3, 3, 3).forEach(entity -> {
                    if (entity.getCustomName().startsWith("GiantItem")) {
                        String command = entity.getCustomName().split("Command:")[1];
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                        player.sendMessage("§aExecutou: " + command);
                        delay.put(player.getUniqueId(), System.currentTimeMillis());
                    }
                });
            }
        }
    }
}
