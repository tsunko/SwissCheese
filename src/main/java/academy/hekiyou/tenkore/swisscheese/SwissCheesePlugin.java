package academy.hekiyou.tenkore.swisscheese;

import academy.hekiyou.door.FrontDoor;
import academy.hekiyou.tenkore.Tenkore;
import academy.hekiyou.tenkore.plugin.TenkorePlugin;
import academy.hekiyou.tenkore.swisscheese.action.ActionCommands;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SwissCheesePlugin extends TenkorePlugin {
    
    private static Tenkore core;
    
    @Override
    public void enable(){
        core = getCore();
        
        FrontDoor.load(EndUserCommands.class);
        FrontDoor.load(ActionCommands.class);
        SaddleModule saddle = FrontDoor.load(SaddleModule.class);
    
        Bukkit.getPluginManager().registerEvents(new ChatMemeListener(), (JavaPlugin)getCore());
        Bukkit.getPluginManager().registerEvents(saddle, (JavaPlugin)getCore());
    }
    
    @Override
    public void disable(){
        core = null;
    }
    
    @NotNull
    public static Tenkore getTenkore(){
        return core;
    }
    
    
}
