package academy.hekiyou.tenkore.swisscheese;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TabColor implements Listener {

    private static final ChatColor[] COLORS = ChatColor.values();
    private static final String[] COLOR_NODES;
    static {
        COLOR_NODES = new String[COLORS.length];
        for(int i=0; i < COLORS.length; i++)
            COLOR_NODES[i] = String.format("tabcolors.color.%c", COLORS[i].getChar());
    }
    
    @EventHandler
    public void handleTabColorOnJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        for(int i=0; i < COLOR_NODES.length; i++)
            if(player.hasPermission(COLOR_NODES[i]))
                player.setPlayerListName(COLORS[i] + player.getName());
    }

}
