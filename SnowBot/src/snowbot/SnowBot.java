package snowbot;

import java.util.Scanner;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

public class SnowBot {
    private static final double VERSION = 0.1;
    public static JDA jda;
    
    public static void main(String[] args) throws LoginException, RateLimitedException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter token: ");
        String botToken = sc.next();
        
        new SnowBot(botToken);
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
