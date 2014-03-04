package com.comze_instancelabs.emeraldmoney;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	public HashMap<String, Integer> pemeralds = new HashMap<String, Integer>();
	
	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	public int getBalance(String p){
		if(!getConfig().isSet(p + ".money")){
			getConfig().set(p + ".money", 0);
			return 0;
		}
		return getConfig().getInt(p + ".money");
	}
	
	public void addToBalance(String p, int c){
		if(!getConfig().isSet(p + ".money")){
			getConfig().set(p + ".money", c);
		}
		getConfig().set(p + ".money", getConfig().getInt(p + ".money") + c);
	}
	
	public void removeFromBalance(String p, int c){
		if(!getConfig().isSet(p + ".money")){
			getConfig().set(p + ".money", 0);
		}
		getConfig().set(p + ".money", getConfig().getInt(p + ".money") - c);
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if(event.getItemDrop().getItemStack().getType() == Material.EMERALD){
			//event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), event.getItemDrop().getItemStack());
			removeFromBalance(event.getPlayer().getName(), event.getItemDrop().getItemStack().getAmount());
			updatePlayerEmeralds(event.getPlayer());
			//event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if(event.getItem().getItemStack().getType() == Material.EMERALD){
			addToBalance(event.getPlayer().getName(), event.getItem().getItemStack().getAmount());
			updatePlayerEmeralds(event.getPlayer());
			event.getItem().remove();
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		final Player p = event.getPlayer();
		Bukkit.getScheduler().runTaskLater(this, new Runnable(){
			public void run(){
				updatePlayerEmeralds(p);
			}
		}, 10L);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
		updatePlayerEmeralds(event.getPlayer());
	}
	
	/*@EventHandler
	public void onPlayerInventoryEvent(InventoryClickEvent event)
	{
		addToBalance(event.getViewers().get(0).getName(), getInventoryItemsCount(event.getInventory(), Material.EMERALD));
		updatePlayerEmeralds((Player)event.getViewers().get(0));
		event.getViewers().get(0).setItemInHand(null);
	}*/
	
	public void updatePlayerEmeralds(Player p){
		removeInventoryItems(p.getInventory(), Material.EMERALD, 64);
	    ItemStack item = new ItemStack(Material.EMERALD, 1);
	    ItemMeta im = item.getItemMeta();
	    im.setDisplayName(ChatColor.GREEN + "Money");
	    im.setLore(new ArrayList<String>(Arrays.asList(ChatColor.RED + Integer.toString(getBalance(p.getName())))));
	    item.setItemMeta(im);
	    p.getInventory().addItem(item);
	    p.updateInventory();
	}

	public static void removeInventoryItems(Inventory inv, Material type, int amount) {
		for (ItemStack is : inv.getContents()) {
			if (is != null && is.getType() == type) {
				int newamount = is.getAmount() - amount;
				if (newamount > 0) {
					is.setAmount(newamount);
					break;
				} else {
					inv.remove(is);
					amount = -newamount;
					if (amount == 0)
						break;
				}
			}
		}
	}
	
	public int getInventoryItemsCount(Inventory inv, Material type) {
		int ret = 0;
		for (ItemStack is : inv.getContents()) {
			if (is != null && is.getType() == type) {
				ret += is.getAmount();
			}
		}
		return ret - 1;
	}
}
