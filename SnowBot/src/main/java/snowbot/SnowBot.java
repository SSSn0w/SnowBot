package snowbot;

import java.io.*;
import java.nio.file.*;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

public class SnowBot {
    private static final double VERSION = 1.5;
    public static JDA jda;
    
    public static void main(String[] args) throws LoginException, RateLimitedException, InterruptedException {
        new SnowBot(Read("config.txt"));
    }
    
    public static String Read(String filePath) {
        Path file = Paths.get(filePath);
        String text = null;
        
        InputStream input = null;
        
        try {
            input = Files.newInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            text = reader.readLine();
            input.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
        
        return text;
    }
    
    public SnowBot(String botToken) {
        try {
            SnowBot.jda = new JDABuilder(AccountType.BOT)
                    .setGame(Game.playing("16 Bit Hero | sb!help"))
                    .setToken(botToken)
                    .addEventListener(new snowbot.CommandHandler())
                    .buildAsync();
        }
        catch(LoginException e) {
            System.out.println(e);
        }
    }
}
