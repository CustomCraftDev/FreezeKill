import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Updater implements Listener{
	private FreezeKill plugin;
	private int version = 3;

	@SuppressWarnings("deprecation")
	public Updater(FreezeKill plugin) {
		this.plugin = plugin;

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		check();
		
		if(plugin.update) {
		    for (Player p : plugin.getServer().getOnlinePlayers()) {
		    	if(p.isOp() || p.hasPermission("freezekill.update")) {
		    		plugin.say(p, false);
		    	}
		    }
		}
	}

	@EventHandler
	public void login(PlayerJoinEvent e) {
		if(plugin.update) {
	    	if(e.getPlayer().isOp() || e.getPlayer().hasPermission("freezekill.update")) {
	    		plugin.say(e.getPlayer(), false);
	    	}
		}
	}

	private void check() {
		String sourceLine = null;
        try {
	        URL url = new URL("http://www.pokemon-online.xyz/plugin");
	        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	        String str;
	        while ((str = in.readLine()) != null) {
	            if(str.startsWith("freezekill:")) {
	            	sourceLine = str.split(":")[1];
	            	break;
	            }
	        }
        } catch (IOException e0) {}
        
	    if(sourceLine != null && Integer.parseInt(sourceLine) != version  ){
	    	plugin.update  = true;
	    	plugin.say(null, true);
	    }
	}

}
