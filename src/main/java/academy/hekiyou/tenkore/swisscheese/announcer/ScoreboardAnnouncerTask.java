package academy.hekiyou.tenkore.swisscheese.announcer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class ScoreboardAnnouncerTask extends AnnouncerTask {
    
    private Map<Player, Scoreboard> oldScoreboards = new WeakHashMap<>();
    private Scoreboard scoreboard;
    private Objective display;
    
    public ScoreboardAnnouncerTask(){
        super();
        scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        display = scoreboard.registerNewObjective("display", "dummy", "");
        display.setDisplaySlot(DisplaySlot.SIDEBAR);
        display.getScore(ChatColor.RED + "666").setScore(666);
    }
    
    @Override
    public void updateDisplay(String message){
        display.setDisplayName(message);
    }
    
    @Override
    public void init(){
        isActive = true;
        for(Player player : Bukkit.getOnlinePlayers()){
            oldScoreboards.put(player, player.getScoreboard());
            player.setScoreboard(scoreboard);
        }
    }
    
    @Override
    public void cleanup(){
        for(Player player : Bukkit.getOnlinePlayers()){
            if(!oldScoreboards.containsKey(player))
                continue;
            player.setScoreboard(oldScoreboards.get(player));
        }
        isActive = false;
    }
    
}
