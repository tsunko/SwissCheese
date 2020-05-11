package academy.hekiyou.tenkore.swisscheese;

import academy.hekiyou.door.annotations.GlobAll;
import academy.hekiyou.door.annotations.Module;
import academy.hekiyou.door.annotations.RegisterCommand;
import academy.hekiyou.door.model.Invoker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

@Module
public class ActionCommands {
    
    private final Set<CommandSender> COOLDOWN_LIST = Collections.newSetFromMap(new WeakHashMap<>());
    private final int COOLDOWN_IN_SECONDS = 3;
    
    public ActionCommands(){
    
    }
    
    @RegisterCommand(
            permission = "swisscheese.command.action",
            description = "High-fives someone!",
            alias = "hf"
    )
    public void highfive(Invoker invoker, CommandSender target){
        if(checkCooldown(invoker.as(CommandSender.class))) return;
        Bukkit.broadcastMessage(ChatColor.GREEN + invoker.getName() + " highfived " + target.getName());
    }
    
    @RegisterCommand(
            permission = "swisscheese.command.action",
            description = "Hugs someone!"
    )
    public void hug(Invoker invoker, CommandSender target){
        if(checkCooldown(invoker.as(CommandSender.class))) return;
        Bukkit.broadcastMessage(ChatColor.GREEN + invoker.getName() + " hugged " + target.getName());
    }
    
    @RegisterCommand(
            permission = "swisscheese.command.action",
            description = "Headpats someone!"
    )
    public void headpat(Invoker invoker, CommandSender target){
        if(checkCooldown(invoker.as(CommandSender.class))) return;
        Bukkit.broadcastMessage(ChatColor.GREEN + invoker.getName() + " headpats " + target.getName());
    }
    
    @RegisterCommand(
            permission = "swisscheese.command.action",
            description = "Cries a river."
    )
    public void cry(Invoker invoker){
        if(checkCooldown(invoker.as(CommandSender.class))) return;
        Bukkit.broadcastMessage(ChatColor.GREEN + invoker.getName() + " cries.");
    }
    
    @RegisterCommand(
            permission = "swisscheese.command.action",
            description = "Who knows what this does ¯\\_(ツ)_/¯"
    )
    public void shrug(Invoker invoker, @GlobAll String message){
        invoker.as(Player.class).chat((message + " ¯\\_(ツ)_/¯"));
    }
    
    private boolean checkCooldown(CommandSender sender){
        // let certain user groups bypass cooldowns entirely
        if(sender.hasPermission("swisscheese.command.action.bypass-cooldown"))
            return false;
        
        if(COOLDOWN_LIST.contains(sender)){
            sender.sendMessage(ChatColor.RED + "Please wait a bit before doing that!");
            return true;
        }
        
        COOLDOWN_LIST.add(sender);
        Bukkit.getScheduler().scheduleSyncDelayedTask((JavaPlugin)SwissCheesePlugin.getTenkore(), () -> {
            COOLDOWN_LIST.remove(sender);
        }, 20 * COOLDOWN_IN_SECONDS);
        return false;
    }
    
}
