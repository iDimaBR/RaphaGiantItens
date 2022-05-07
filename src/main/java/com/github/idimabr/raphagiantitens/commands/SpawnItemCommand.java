package com.github.idimabr.raphagiantitens.commands;

import com.github.idimabr.raphagiantitens.enums.CardinalDirection;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class SpawnItemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if(!player.hasPermission("swezy.itemadd")){
            player.sendMessage("§cSem permissão!");
            return false;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("remover")) {
            boolean removed = remove(player);
            if (removed) {
                player.sendMessage("§aItem gigante removido!");
            } else {
                player.sendMessage("§cItem gigante naõ encontrado!");
            }
        }

        if(args.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (String s : args)
                if (!s.contains(args[0]))
                    sb.append(s + " ");

            if (args[0].equalsIgnoreCase("criar")) {
                if (player.getItemInHand().getType() == Material.AIR) {
                    player.sendMessage("§cSegure um item na mão");
                    return false;
                }

                spawn(player, player.getItemInHand(), sb.toString());
                player.sendMessage("§aItem gigante criado!");
                return true;
            }else if (args[0].equalsIgnoreCase("alterar")) {
                boolean changed = changeCommand(player, sb.toString());
                if (changed) {
                    player.sendMessage("§aComando do Item Gigante alterado para: §f/" + sb.toString());
                } else {
                    player.sendMessage("§cItem gigante naõ encontrado!");
                }
                return true;
            }
        }else {
            player.sendMessage("§cComandos:");
            player.sendMessage("");
            player.sendMessage("§7/giantitem criar <comando>");
            player.sendMessage("§7/giantitem alterar <comando>");
            player.sendMessage("§7/giantitem remover <opcional:todos>");
        }

        if(args.length == 2 && args[1].equalsIgnoreCase("todos")){
                for(World world : Bukkit.getWorlds()){
                    world.getEntities().forEach(entity -> {
                        if (entity instanceof Slime) {
                            Slime slime = (Slime) entity;
                            if(slime.getCustomName().startsWith("GiantItem")) slime.remove();
                        }
                        if (entity instanceof Giant) {
                            Giant giant = (Giant) entity;
                            if(giant.getCustomName().startsWith("GiantItem")) giant.remove();
                        }
                        if (entity instanceof ArmorStand) {
                            ArmorStand stand = (ArmorStand)entity;
                            if (stand.getPassenger() instanceof Giant) {
                                Giant giant = (Giant)stand.getPassenger();
                                if (stand.getCustomName().startsWith("GiantItem"))
                                    giant.remove();
                                    stand.remove();
                            }
                        }
                    });
                }
        }
        return false;
    }


    public boolean changeCommand(Player player, String command){
        boolean changed = false;
        List<Entity> entities = player.getNearbyEntities(5.0D, 12.0D, 5.0D);
        for (Entity entity : entities) {
            if(entity.getType() == EntityType.ARMOR_STAND || entity.getType() == EntityType.GIANT || entity.getType() == EntityType.SLIME && entity.getCustomName().startsWith("GiantItem")){
                entity.setCustomName("GiantItem Command:" + command);
                changed = true;
            }
        }
        return changed;
    }

    public static void spawn(Player player, ItemStack item, String command) {
        Location loc = player.getLocation();
        Giant giant = loc.getWorld().spawn(loc, Giant.class);
        removeAI(giant);
        giant.getEquipment().setItemInHand(item);
        giant.setCustomName("GiantItem Command:" + command);
        giant.setCustomNameVisible(false);
        giant.getEquipment().setItemInHandDropChance(0.0F);
        giant.setCanPickupItems(false);
        giant.setRemoveWhenFarAway(false);
        giant.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2147483647, 999));
        ArmorStand stand = loc.getWorld().spawn(getNewLocation(loc.clone().subtract(0.0D, 9.0D, 0.0D), CardinalDirection.getCardinalDirection(player)), ArmorStand.class);
        stand.setCustomName("GiantItem Command:" + command);
        stand.setCustomNameVisible(false);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setRemoveWhenFarAway(false);
        stand.setPassenger(giant);

        Slime slime = loc.getWorld().spawn(loc, Slime.class);
        removeAI(slime);
        slime.setSize(8);
        slime.setCustomName("GiantItem Command:" + command);
        slime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2147483647, 999));
        slime.setCustomNameVisible(false);
        slime.getEquipment().setItemInHandDropChance(0.0F);
        slime.setCanPickupItems(false);
        slime.setRemoveWhenFarAway(false);
    }

    public static void removeAI(Entity entity) {
        net.minecraft.server.v1_8_R3.Entity nms = ((CraftEntity) entity).getHandle();
        NBTTagCompound comp = new NBTTagCompound();
        nms.c(comp);
        comp.setByte("NoAI", (byte) 1);
        nms.f(comp);
        nms.b(true);
    }

    public static boolean remove(Player p) {
        boolean removed = false;
        List<Entity> entities = p.getNearbyEntities(5.0D, 12.0D, 5.0D);
        for (Entity entity : entities) {
            if (entity instanceof Slime) {
                Slime slime = (Slime) entity;
                if(slime.getCustomName().startsWith("GiantItem")) slime.remove();
            }
            if (entity instanceof ArmorStand) {
                ArmorStand stand = (ArmorStand)entity;
                if (stand.getPassenger() instanceof Giant) {
                    Giant giant = (Giant)stand.getPassenger();
                    if (stand.getCustomName().startsWith("GiantItem"))
                        if (p.getLocation().getY() > stand.getLocation().getY()) {
                            giant.remove();
                            stand.remove();
                            removed = true;
                        }
                }
            }
        }
        return removed;
    }

    public static Location getNewLocation(Location location, CardinalDirection direction) {
        switch (direction) {
            case WEST:
                return location.clone().add(4, 0, 2);
            case SOUTH:
                return location.clone().add(2, 0, -4);
            case EAST:
                return location.clone().add(-4, 0, -2);
            default:
                return location.clone().add(-2, 0, 4);
        }
    }
}
