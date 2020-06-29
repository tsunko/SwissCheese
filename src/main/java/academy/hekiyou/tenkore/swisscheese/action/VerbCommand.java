package academy.hekiyou.tenkore.swisscheese.action;

import academy.hekiyou.door.FrontDoor;
import academy.hekiyou.door.annotations.RegisterCommand;
import academy.hekiyou.door.exception.BadInterpretationException;
import academy.hekiyou.door.interp.Interpreters;
import academy.hekiyou.door.model.Channel;
import academy.hekiyou.door.model.Command;
import academy.hekiyou.door.model.Invoker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * This is an _extremely_ hacky solution to generating commands on the fly
 * In essence, we can take advantage (read: abuse) the fact that annotations can be "implemented", as they
 * are derived from interfaces.
 *
 * Thus, we can avoid generating an anonymous class at runtime, which would pollute the class loader and
 * waste memory.
 */
public class VerbCommand implements Command, RegisterCommand {
    
    private static final Parameter COMMAND_SENDER_PARAMETER = borrowParameterObject();
    
    private final CooldownManager cooldownManager;
    private final String name;
    private final String verbed;
    private final boolean requiresTarget;
    
    VerbCommand(CooldownManager manager, String name, String verb, boolean target){
        this.name = name;
        this.verbed = verb;
        this.cooldownManager = manager;
        this.requiresTarget = target;
    }
    
    public String getVerbed(){
        return verbed;
    }
    
    public boolean doesRequiresTarget(){
        return requiresTarget;
    }
    
    // this code is very ugly and defeats the handling of command invocation that door provides
    @Override
    public void execute(@NotNull String commandName, @NotNull Invoker invoker,
                        @NotNull Channel channel, @NotNull String[] arguments){
        if(!invoker.hasPermission(permission())){
            invoker.sendMessage(FrontDoor.getSettings().getPermissionError(), permission());
            return;
        }
        
        if(requiresTarget && arguments.length < 1){
            invoker.sendMessage(ChatColor.RED + "You need to target someone with this action!");
            return;
        }
        
        CommandSender sender = invoker.as(CommandSender.class);
        if(cooldownManager.hasCooldown(sender))
            return;
        
        try {
            String message;
            if(requiresTarget){
                CommandSender target = Objects.requireNonNull(Interpreters.of(CommandSender.class)).apply(arguments[0]);
                message = ChatColor.GREEN + sender.getName() + " " + verbed + " " + target.getName();
            } else {
                message = ChatColor.GREEN + sender.getName() + " " + verbed;
            }
            Bukkit.broadcastMessage(message);
            cooldownManager.putOnCooldown(sender);
        } catch (BadInterpretationException exc){
            // the only line that can cause this is when we're getting "target".
            invoker.sendMessage(ChatColor.RED + "That player doesn't exist!");
        }
    }
    
    @Override
    public @NotNull String getName(){
        return name;
    }
    
    @Override
    public @NotNull RegisterCommand getMetadata(){
        return this;
    }
    
    @Override
    public @NotNull String getOwningClass(){
        return ActionCommands.class.getName();
    }
    
    @Override
    public @NotNull Parameter[] getParameters(){
        if(requiresTarget){
            return new Parameter[]{ COMMAND_SENDER_PARAMETER };
        } else {
            return new Parameter[0];
        }
    }
    
    @Override
    public @NotNull String[] getUsage(){
        if(requiresTarget){
            return new String[]{"<player>"};
        } else {
            return new String[0];
        }
    }
    
    @Override
    public String permission(){
        return "swisscheese.command.action";
    }
    
    @Override
    public String description(){
        return "An action command.";
    }
    
    @Override
    public String[] usage(){
        return getUsage();
    }
    
    @Override
    public String[] alias(){
        return new String[0];
    }
    
    @Override
    public boolean override(){
        return false;
    }
    
    @Override
    public boolean requiresChannelSupport(){
        return false;
    }
    
    @Override
    public Class<? extends Annotation> annotationType(){
        return RegisterCommand.class;
    }
    
    private static Parameter borrowParameterObject(){
        try {
            return VerbCommand.class.getDeclaredMethod("dummyCommand", CommandSender.class).getParameters()[0];
        } catch (ReflectiveOperationException exc){
            // if we hit this, then we have other issues
            throw new RuntimeException(exc);
        }
    }
    
    public static void dummyCommand(CommandSender dummy){}
    
}
