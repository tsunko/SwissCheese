package academy.hekiyou.tenkore.swisscheese.action;

import org.bukkit.command.CommandSender;

public interface CooldownManager {
    
    boolean hasCooldown(CommandSender sender);
    
    void putOnCooldown(CommandSender sender);
    
}
