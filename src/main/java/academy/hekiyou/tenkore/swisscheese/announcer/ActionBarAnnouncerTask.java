package academy.hekiyou.tenkore.swisscheese.announcer;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionBarAnnouncerTask extends AnnouncerTask {
    
    public ActionBarAnnouncerTask(){
        super();
    }
    
    @Override
    public void init(){
        isActive = true;
    }
    
    @Override
    public void updateDisplay(String message){
        for(Player player : Bukkit.getOnlinePlayers())
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
    
    @Override
    public void cleanup(){
        isActive = false;
    }
    
}
