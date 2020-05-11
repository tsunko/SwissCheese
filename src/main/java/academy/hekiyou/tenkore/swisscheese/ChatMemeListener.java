package academy.hekiyou.tenkore.swisscheese;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class ChatMemeListener implements Listener {

    @EventHandler
    public void listeningForMemes(AsyncPlayerChatEvent event){
        String message = event.getMessage();
        String lowerMessage = message.toLowerCase();
        
        if(message.startsWith(">") && event.getPlayer().hasPermission("swisscheese.function.greentext"))
            message = ChatColor.GREEN + message;
    
        if(message.startsWith("<") && event.getPlayer().hasPermission("swisscheese.function.greentext"))
            message = ChatColor.LIGHT_PURPLE + mock(message);
    
        if(lowerMessage.contains("hunger games"))
            message = message.replaceAll("(?i)hunger games", "HANGER GAEMZ");
        
        if(lowerMessage.contains("swag"))
            message = message.replaceAll("(?i)swag", "SWEGZ");
        
        event.setMessage(message);
    }
    
    private @NotNull String mock(String message){
        StringBuilder builder = new StringBuilder();
        boolean cap = false;
        for(char c : message.toCharArray()){
            if(Character.isAlphabetic(c)){
                builder.append(cap ? Character.toUpperCase(c) : Character.toLowerCase(c));
                cap = !cap;
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
    
}
