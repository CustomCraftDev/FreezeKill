
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FreezeKill extends JavaPlugin implements Listener{
	private String noperm;
	protected String prefix;
	
	protected boolean toggle = true;
	protected boolean debug = false;
	protected boolean update = false;
	
	protected FileConfiguration config;
	private ArrayList<Player> frozen;
	private List<String> cmds;

	
	// PLUGIN MAIN FUNCTIONS ------------------------------------------------------------------------------------------------------------------
	
	
	public void onEnable() {
		loadconfig();
		new Updater(this);
		
		frozen = new ArrayList<Player>();
    	getServer().getPluginManager().registerEvents(this, this);
	}


	private void loadconfig(){
		config = getConfig();
		config.options().copyDefaults(true);
		saveConfig();
		
		debug = config.getBoolean("debug");
		noperm = ChatColor.translateAlternateColorCodes('&', config.getString("msg.noperm"));
		prefix = ChatColor.translateAlternateColorCodes('&', config.getString("msg.prefix"));
		
		cmds = config.getStringList("cmd");
	}
	
	
	// ON COMMAND ------------------------------------------------------------------------------------------------------------------
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		boolean isplayer = false;
		Player p = null;
		
		if ((sender instanceof Player)) {
			p = (Player)sender;
			isplayer = true;
		}
			
		if(cmd.getName().equalsIgnoreCase("freezekill") && args.length == 1){
			
			// reload
			if(args[0].equalsIgnoreCase("reload")){
				if(isplayer){
					if(p.hasPermission("freezekill.reload")){
						p.sendMessage(prefix + " Config reloaded.");
						loadconfig();
					return true;
				}
					else{
						p.sendMessage(noperm);
						return true;
					}
				}else {
					loadconfig();
					System.out.println(ChatColor.stripColor(prefix + " Config reloaded."));
					return true;
				}
			}
			
			// unfreeze
			if(args[0].equalsIgnoreCase("unfreeze")){
				if(isplayer){
					if(p.hasPermission("freezekill.use")){
						unfreeze(p);
					return true;
				}
					else{
						p.sendMessage(noperm);
						return true;
					}
				}
			}
			
			// time
			if(isInteger(args[0])){
				if(isplayer){
					if(p.hasPermission("freezekill.use")){
						freeze(p, Integer.parseInt(args[0]));
					return true;
				}
					else{
						p.sendMessage(noperm);
						return true;
					}
				}
			}
		}
		
		// nothing to do here \o/
		return false;
	}
	
	
	// EVENTS ------------------------------------------------------------------------------------------------------------------
	
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
    	Player p = e.getPlayer();
    	unfreeze(p);
	}
	
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
	    if (e.getEntity() instanceof Player){
	    	Player p = (Player)e.getEntity();
	    	unfreeze(p);
	    }
	}
	
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(isfrozen(e.getPlayer())) {
	        Player p = e.getPlayer();
	        Location from = e.getFrom();
		        double xfrom = e.getFrom().getX();
		        double yfrom = e.getFrom().getY();
		        double zfrom = e.getFrom().getZ();
		        double xto = e.getTo().getX();
		        double yto = e.getTo().getY();
		        double zto = e.getTo().getZ();
	        if (!(xfrom == xto && yfrom == yto && zfrom == zto)) {
	            p.teleport(from);
	        }
		}
	}
	
	
	// FUNCTIONS ------------------------------------------------------------------------------------------------------------------
	
	
	protected boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}

	
	protected void freeze(final Player p, final int seconds) {
		frozen.add(p);
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				int time = seconds;
				while(time > 0) {
					if(!isfrozen(p)) {return;}
					p.setLevel(time);
					time--;
						try {
							Thread.sleep(1000);
						}catch(Exception e) {}
				}
				execute(p);
			}
		});
		t.start();
	}

	
	protected void execute(Player p) {
		for(String cmd : cmds) {
			cmd = cmd.replace("%player%",p.getName());
			String[] call = cmd.split(",");
			if(Boolean.parseBoolean(call[0])) {
				getServer().dispatchCommand(getServer().getConsoleSender(), call[1]);
			}else {
				getServer().dispatchCommand(p, call[1]);
			}
		}
	}


	protected void unfreeze(Player p) {
		frozen.remove(p);
		p.setLevel(0);
	}

	
	protected boolean isfrozen(Player p) {
		if(frozen.contains(p)) {
			return true;
		}
		return false;
	}
	
	
	// UPDATER ------------------------------------------------------------------------------------------------------------------
	
	protected void say(Player p, boolean b) {
		if(b) {
			System.out.println(ChatColor.stripColor(prefix + "------------------------------------------------"));
			System.out.println(ChatColor.stripColor(prefix + " FreezeKill is outdated. Get the new version here:"));
			System.out.println(ChatColor.stripColor(prefix + " http://www.pokemon-online.xyz/plugin"));
			System.out.println(ChatColor.stripColor(prefix + "------------------------------------------------"));
		}else {
		   	p.sendMessage(prefix + "------------------------------------------------");
		   	p.sendMessage(prefix + " FreezeKill is outdated. Get the new version here:");
		   	p.sendMessage(prefix + " http://www.pokemon-online.xyz/plugin");
		   	p.sendMessage(prefix + "------------------------------------------------");
		}
	}

	
}
