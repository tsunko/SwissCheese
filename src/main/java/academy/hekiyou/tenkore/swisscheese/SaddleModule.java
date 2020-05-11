package academy.hekiyou.tenkore.swisscheese;

import academy.hekiyou.door.annotations.Module;
import academy.hekiyou.door.annotations.RegisterCommand;
import academy.hekiyou.door.annotations.optional.OptionalInteger;
import academy.hekiyou.door.model.Invoker;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Module
public class SaddleModule implements Listener {
    
    @RegisterCommand(
            permission = "swisscheese.function.saddle",
            description = "Ejects any entity that is mounted on you"
    )
    public void eject(Invoker invoker){
        Player player = invoker.as(Player.class);
        if(player.eject()){
            invoker.sendMessage(ChatColor.GREEN + "Ejected.");
        } else {
            invoker.sendMessage(ChatColor.RED + "No passenger was ejected.");
        }
    }
    
    @RegisterCommand(
            permission = "swisscheese.function.saddle",
            description = "Finds an entity by the given ID and mounts you atop it"
    )
    public void mount(Invoker invoker, int entityId){
        Player player = invoker.as(Player.class);
        // there's no direct way to get an entity by its ID...
        for(Entity entity : player.getWorld().getEntities()){
            if(entity.getEntityId() == entityId){
                player.teleport(entity);
                entity.addPassenger(player);
                return;
            }
        }
    }
    
    @RegisterCommand(
            permission = "swisscheese.function.saddle",
            description = "Gives you a list of entities nearby"
    )
    public void nearbyEntities(Invoker invoker, @OptionalInteger(5) int range){
        Player player = invoker.as(Player.class);
        StringBuilder builder = new StringBuilder(ChatColor.YELLOW.toString());
        for(Entity entity : player.getNearbyEntities(range, range, range))
            builder.append(entity.getEntityId()).append(" (").append(entity.getType()).append("), ");
        
        String result = builder.toString();
        if(!result.isEmpty()){
            player.sendMessage(builder.substring(0, builder.length() - 2));
        } else {
            player.sendMessage(ChatColor.RED + "There isn't any entities nearby you");
        }
    }
    
    @EventHandler
    public void saddleListenerEvent(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        Entity clicked = event.getRightClicked();
        
        if(player.getPassengers().contains(clicked) || clicked == player.getVehicle()) return;
    
        ItemStack inHand = player.getInventory().getItemInMainHand();
        if(inHand.getType() == Material.SADDLE){
            if(!player.hasPermission("swisscheese.function.saddle"))
                return;
            
            // sneaking = put this on my head
            // no sneak = put me on that head
            if(player.isSneaking()){
                player.addPassenger(clicked);
            } else {
                clicked.addPassenger(player);
            }
        }
    }
    
    // need to eject passengers and from vehicle if we're riding
    // else we crash the server
    @EventHandler
    public void saddleDisconnect(PlayerQuitEvent event){
        Player player = event.getPlayer();
        List<Entity> passengers = player.getPassengers();
        if(!passengers.isEmpty())
            for(Entity entity : passengers)
                entity.eject();
        player.eject();
    }
    
}
