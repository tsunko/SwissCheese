package academy.hekiyou.tenkore.swisscheese.announcer;


import academy.hekiyou.tenkore.swisscheese.SwissCheesePlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public abstract class AnnouncerTask implements Runnable {
    
    private static final int SBA_DELAY = 3;
    private static final int SBA_LENGTH = 32;
    
    private Queue<String> queue = new LinkedList<>();
    private String currentMessage = null;
    private int pos = 0;
    
    boolean isActive = false;
    
    @Override
    public void run(){
        if(currentMessage == null){
            currentMessage = queue.poll();
            // check if the new message is not null, then we have a new message
            if(currentMessage != null){
                pos = 0;
                init();
            } else {
                return;
            }
        }
        
        int start = Math.max(0, Math.min(pos, currentMessage.length()));
        int end = Math.min(pos + SBA_LENGTH, currentMessage.length());
        String disp = currentMessage.substring(start, end);
        if(disp.trim().isEmpty()){
            currentMessage = null;
            cleanup();
        } else {
            pos++;
        }
        updateDisplay(disp);
        Bukkit.getScheduler().scheduleSyncDelayedTask((JavaPlugin) SwissCheesePlugin.getTenkore(),
                this, SBA_DELAY);
    }
    
    /**
     * Adds the message to the queue for display
     * @param message The message to display
     */
    public void addToQueue(String message){
        queue.add(padSBAMessage(message));
    }
    
    /**
     * Checks if the announcer is currently active i.e displaying a message
     * @return {@code true} if we are currently active,
     *         {@code false} otherwise
     */
    public boolean isActive(){
        return isActive;
    }
    
    /**
     * Perform any initialization
     */
    public void init(){}
    
    /**
     * Perform any cleanup
     */
    public void cleanup(){}
    
    /**
     * Update the internal display with the message
     * @param message The message to display
     */
    public abstract void updateDisplay(String message);
    
    /**
     * Pads the message out with spaces
     * @param message The message to pad
     * @return A {@link String} who has {@link AnnouncerTask#SBA_LENGTH} - 1 spaces in the front and
     *         {@link AnnouncerTask#SBA_LENGTH} spaces at its end.
     */
    private String padSBAMessage(String message){
        String padding = String.join("", Collections.nCopies(SBA_LENGTH - 1," "));
        // throw an extra space at the end so we can find out when the string is empty
        return padding + message + padding + " ";
    }
    
}