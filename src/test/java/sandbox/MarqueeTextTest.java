package sandbox;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

public class MarqueeTextTest {
    
    private static int len = 16;
    
    public static void main(String[] args){
        ScoreboardAnnouncerRunnable asdf = new ScoreboardAnnouncerRunnable();
        while(!asdf.done)
            asdf.run();
    }
    
    private static class ScoreboardAnnouncerRunnable extends BukkitRunnable {
        
        private String currentMessage = padMessage("HELLO THERE", len);
        private int pos = 0;
        private boolean done = false;
        
        @Override
        public void run(){
            if(done)
                return;
            int start = Math.max(0, Math.min(pos, currentMessage.length()));
            int end = Math.min(pos + len, currentMessage.length());
            String substr = currentMessage.substring(start, end);
            if(substr.trim().isEmpty()){
                done = true;
                return;
            }
            System.out.println(substr);
            pos++;
            
        }
    }
    
    private static String padMessage(String str, int len){
        String padding = String.join("", Collections.nCopies(len - 1," "));
        return padding + str + padding + " ";
    }
    
}
