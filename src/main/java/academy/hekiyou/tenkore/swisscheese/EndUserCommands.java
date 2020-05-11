package academy.hekiyou.tenkore.swisscheese;

import academy.hekiyou.door.annotations.GlobAll;
import academy.hekiyou.door.annotations.Module;
import academy.hekiyou.door.annotations.RegisterCommand;
import academy.hekiyou.door.annotations.optional.OptionalBoolean;
import academy.hekiyou.door.annotations.optional.OptionalDouble;
import academy.hekiyou.door.annotations.optional.OptionalObject;
import academy.hekiyou.door.model.Invoker;
import academy.hekiyou.tenkore.swisscheese.announcer.ActionBarAnnouncerTask;
import academy.hekiyou.tenkore.swisscheese.announcer.AnnouncerTask;
import academy.hekiyou.tenkore.swisscheese.announcer.ScoreboardAnnouncerTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Module
public class EndUserCommands {
    
    // initialize scoreboardAnnouncer later due to caveat in Bukkit.getScoreboard()
    private AnnouncerTask actionBarAnnouncer = new ActionBarAnnouncerTask();
    private AnnouncerTask scoreboardAnnouncer = null;
    
    @RegisterCommand(
            permission = "swisscheese.command.ping",
            description = "Retrieves your ping, according to Minecraft.",
            alias = {"latency", "ms"},
            override = true
    )
    public void ping(Invoker invoker,
                     @OptionalObject("self") Player target){
        if(target == null)
            target = invoker.as(Player.class);
        
        int ping = getPing(target);
        if(ping == -1){
            invoker.sendMessage(ChatColor.RED + "Failed to get ping.");
            return;
        }
        
        invoker.sendMessage(ChatColor.YELLOW + "Ping: %dms", ping);
    }
    
    @RegisterCommand(
            permission = "swisscheese.command.kill",
            description = "Sets your health to 0, hopefully killing you.",
            override = true
    )
    public void kill(Invoker invoker){
        invoker.as(Player.class).setHealth(0D);
    }
    
    @RegisterCommand(
            permission = "swisscheese.command.hat",
            description = "Places the item in-hand on your head."
    )
    public void hat(Invoker invoker){
        PlayerInventory inventory = invoker.as(Player.class).getInventory();
        ItemStack currentHelmet = inventory.getHelmet();
        inventory.setHelmet(inventory.getItemInMainHand());
        inventory.setItemInMainHand(currentHelmet);
    }
    
    @RegisterCommand(
            permission = "swisscheese.command.forcechat",
            description = "Forces a player to say something"
    )
    public void forcechat(Invoker invoker, Player target, @GlobAll String message){
        target.chat(message);
    }
    
    @RegisterCommand(
            permission = "swisscheese.command.actionbarannounce",
            description = "Uses the action bar messages to broadcast an announcement.",
            alias = "aba"
    )
    public void actionbarAnnounce(Invoker invoker, @GlobAll String message){
        queueMessage(invoker, actionBarAnnouncer, message);
    }
    
    @RegisterCommand(
            permission = "swisscheese.command.scoreboardannounce",
            description = "Uses the side scoreboard to broadcast an announcement.",
            alias = "sba"
    )
    public void scoreboardAnnounce(Invoker invoker, @GlobAll String message){
        // initialize here because getScoreboard() can return null if no world is loaded
        if(scoreboardAnnouncer == null)
            scoreboardAnnouncer = new ScoreboardAnnouncerTask();
        queueMessage(invoker, scoreboardAnnouncer, message);
    }
    
    @RegisterCommand(
            permission = "swisscheese.command.slap",
            description = "Slaps a player"
    )
    public void slap(Invoker invoker,
                     Player target,
                     @OptionalDouble(2) double hardness,
                     @OptionalBoolean(false) boolean silent){
        Vector velocity = generateRandomVector(hardness, true);
        target.setVelocity(velocity);
        if(!silent)
            Bukkit.broadcastMessage(ChatColor.YELLOW + invoker.getName() + " slapped " + target.getName() + "!");
    }
    
    @RegisterCommand(
            permission = "swisscheese.command.slap",
            description = "Slaps a player"
    )
    public void rocket(Invoker invoker,
                     Player target,
                     @OptionalDouble(2) double hardness,
                     @OptionalBoolean(false) boolean silent){
        Vector velocity = generateRandomVector(hardness, false);
        target.setVelocity(velocity);
        if(!silent)
            Bukkit.broadcastMessage(ChatColor.YELLOW + invoker.getName() + " rocketed " + target.getName() + "!");
    }
    
    private Vector generateRandomVector(double hardness, boolean randomizeXZ){
        Random rng = ThreadLocalRandom.current();
        if(randomizeXZ){
            return new Vector(-hardness + rng.nextDouble() * 2d * hardness / 32D,
                              2 + (hardness * rng.nextDouble()) / 4D,
                              -hardness + rng.nextDouble() * 2d * hardness / 32D);
        } else {
            return new Vector(0, 2 + (hardness * rng.nextDouble()) / 4D, 0);
        }
    }
    
    private int getPing(Player player){
        try {
            Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
            return (int)nmsPlayer.getClass().getField("ping").get(nmsPlayer);
        } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException exc){
            exc.printStackTrace();
            return -1;
        }
    }
    
    private void queueMessage(Invoker invoker, AnnouncerTask announcer, String message){
        announcer.addToQueue(message);
        if(announcer.isActive()){
            invoker.sendMessage(ChatColor.YELLOW + "There's an active announcement; your message was queued instead.");
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask((JavaPlugin)SwissCheesePlugin.getTenkore(), actionBarAnnouncer);
        }
    }
    
}
