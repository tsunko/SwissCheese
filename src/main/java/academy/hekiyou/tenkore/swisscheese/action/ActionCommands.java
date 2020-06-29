package academy.hekiyou.tenkore.swisscheese.action;

import academy.hekiyou.door.annotations.BranchingCommand;
import academy.hekiyou.door.annotations.GlobAll;
import academy.hekiyou.door.annotations.Module;
import academy.hekiyou.door.annotations.RegisterCommand;
import academy.hekiyou.door.model.Command;
import academy.hekiyou.door.model.Invoker;
import academy.hekiyou.door.model.Register;
import academy.hekiyou.tenkore.swisscheese.SwissCheesePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Module
public class ActionCommands implements CooldownManager {
    
    private static final String CONFIG_DELIMITER = "|";
    private static final String CONFIG_FORMAT = "%s" + CONFIG_DELIMITER + "%s" + CONFIG_DELIMITER + "%s";
    private static final int COOLDOWN_IN_SECONDS = 3;
    
    private final Set<CommandSender> cooldownList = Collections.newSetFromMap(new WeakHashMap<>());
    private final Set<VerbCommand> verbCommands = new HashSet<>();
    
    public ActionCommands(){
        registerDefaultActions();
        loadActionsFile();
    }
    
    @RegisterCommand(
            permission = "swisscheese.command.action",
            description = "Who knows what this does ¯\\_(ツ)_/¯"
    )
    public void shrug(Invoker invoker, @GlobAll String message){
        invoker.as(Player.class).chat((message + " ¯\\_(ツ)_/¯"));
    }
    
    @RegisterCommand(
            permission = "swisscheese.command.manage-actions",
            description = "Modifies the list of available actions"
    )
    @BranchingCommand
    public void actions(Invoker invoker){}
    
    public void actions$add(Invoker invoker, String name, @GlobAll String action){
        addNewCommand(invoker, name, action, false);
    }
    
    public void actions$addTargeted(Invoker invoker, String name, @GlobAll String action){
        addNewCommand(invoker, name, action, true);
    }
    
    public void actions$remove(Invoker invoker, String name){
        if(!removeCommand(name)){
            invoker.sendMessage(ChatColor.RED + "Unable to remove command - either it didn't exist or wasn't an action");
            return;
        }
    
        saveActionsFile();
        invoker.sendMessage(ChatColor.GREEN + "Remove action \"%s\"", name);
    }
    
    public void actions$list(Invoker invoker){
        invoker.sendMessage(ChatColor.GREEN + "List of actions:");
        for(VerbCommand command : verbCommands){
            invoker.sendMessage(ChatColor.GREEN + " - " + command.getName());
        }
    }
    
    public void actions$reload(Invoker invoker){
        loadActionsFile();
        invoker.sendMessage(ChatColor.GREEN + "Reloaded actions.");
    }
    
    @Override
    public boolean hasCooldown(CommandSender sender){
        // let certain user groups bypass cooldowns entirely
        if(sender.hasPermission("swisscheese.command.action.bypass-cooldown"))
            return false;
        
        if(cooldownList.contains(sender)){
            sender.sendMessage(ChatColor.RED + "Please wait a bit before doing that!");
            return true;
        }
        
        return false;
    }
    
    @Override
    public void putOnCooldown(CommandSender sender){
        cooldownList.add(sender);
        // schedule task to remove the cooldown
        Bukkit.getScheduler().scheduleSyncDelayedTask((JavaPlugin) SwissCheesePlugin.getTenkore(), () -> {
            cooldownList.remove(sender);
        }, 20 * COOLDOWN_IN_SECONDS);
    }
    
    private void addNewCommand(Invoker invoker, String name, String action, boolean targeted){
        if(!registerCommand(name, action, targeted)){
            invoker.sendMessage(ChatColor.RED + "Command name conflict - maybe it's already an action?");
            return;
        }
    
        saveActionsFile();
        invoker.sendMessage(ChatColor.GREEN + "Created new command \"%s\".", name);
    }
    
    private void loadActionsFile() {
        // remove all previously registered commands
        if(!verbCommands.isEmpty()){
            Iterator<VerbCommand> iter = verbCommands.iterator();
            while(iter.hasNext()){
                removeCommand(iter.next(), false);
                iter.remove();
            }
        }
        
        Logger logger = Logger.getLogger(ActionCommands.class.getSimpleName());
        
        Path path = Paths.get("actions.conf");
        if(!Files.exists(path)){
            // load defaults up
            registerDefaultActions();
            saveActionsFile();
            return;
        }
        
        try(BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int lineNum = 1;
            String regexEscaped = "\\" + CONFIG_DELIMITER;
    
            while((line = reader.readLine()) != null){
                if(line.isEmpty()){
                    lineNum++;
                    continue;
                }
        
                if(!line.contains(CONFIG_DELIMITER)){
                    logger.warning("Ignoring invalid action line #" + lineNum++);
                    continue;
                }
        
                String[] data = line.split(regexEscaped, 3);
                registerCommand(data[0], data[1], Boolean.parseBoolean(data[2]));
                logger.info("Registered action \"" + data[0] + "\"");
                lineNum++;
            }
        } catch (IOException exc){
            logger.log(Level.SEVERE, "Failed to load actions config", exc);
        }
    }
    
    private void saveActionsFile(){
        Logger logger = Logger.getLogger(ActionCommands.class.getSimpleName());
        Path path = Paths.get("actions.conf");
        
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            for(VerbCommand command : verbCommands){
                writer.write(String.format(CONFIG_FORMAT,
                        command.getName(), command.getVerbed(), String.valueOf(command.doesRequiresTarget())));
                writer.newLine();
            }
        } catch (IOException exc){
            logger.log(Level.SEVERE, "Failed to save actions config", exc);
        }
    }
    
    private void registerDefaultActions(){
        registerCommand("cry", "cries", false);
        registerCommand("headpat", "headpats", true);
        registerCommand("hug", "hugs", true);
        registerCommand("highfive", "highfives", true);
    }
    
    private boolean registerCommand(String name, String verbed, boolean needsTarget){
        Register cmdRegister = SwissCheesePlugin.getTenkore().getCommandRegister();
        VerbCommand command = new VerbCommand(this, name, verbed, needsTarget);
        if(cmdRegister.isRegistered(name))
            return false;
        
        cmdRegister.register(command);
        verbCommands.add(command);
        return true;
    }
    
    private boolean removeCommand(String name){
        Register cmdRegister = SwissCheesePlugin.getTenkore().getCommandRegister();
        Command command = cmdRegister.getCommand(name);
        if(command instanceof VerbCommand){
            return removeCommand((VerbCommand)command, true);
        } else {
            return false;
        }
    }
    
    private boolean removeCommand(VerbCommand command, boolean updateSet){
        Register cmdRegister = SwissCheesePlugin.getTenkore().getCommandRegister();
        cmdRegister.unregister(command);
        if(updateSet)
            verbCommands.remove(command);
        return true;
    }
    
}
