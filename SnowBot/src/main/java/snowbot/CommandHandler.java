package snowbot;

import java.io.IOException;
import snowbot.Commands.*;
import snowbot.Music.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandHandler extends ListenerAdapter {
    private final String commandPrefix = "sb!";
    private final String musicPrefix = "$";
    public static ArrayList<Command> commands = new ArrayList<Command>();
    
    private final Music musicInst = new Music();
    private String[] args;
    
    public CommandHandler() {
        addCommands();
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {  
        addCommands();
        if(event.getMessage().getContentDisplay().startsWith(commandPrefix)) {
            for(Command com : commands) {              
                if(event.getMessage().getContentDisplay().split(commandPrefix).length != 0 && event.getMessage().getContentDisplay().split(commandPrefix)[1].split(" ")[0].equals(com.commandCall)) {
                    if(event.getMessage().getContentDisplay().split(com.commandCall).length > 1) {
                        
                        com.Args(event, event.getMessage().getContentRaw().split(" "), com.commandArgs);
                        return;
                    }
                    else {
                        com.Reset();
                        com.Return(event);
                        return;
                    }
                }
            }
            
            event.getChannel().sendMessage("Sorry, I can't complete this action :disappointed_relieved:"
                + "\n\nType **sb!help** to see a list of commands!").queue();
        }
        else if(event.getMessage().getContentDisplay().startsWith(musicPrefix)) {
            musicInst.setCommand(event.getMessage().getContentRaw().split("\\" + musicPrefix)[1].split(" ")[0]);
            String[] input = event.getMessage().getContentRaw().split("\\" + musicPrefix)[1].split(" ");
            
            if(input.length > 1) {
                args = Arrays.copyOfRange(input, 1, input.length);
                try {
                    event.getChannel().sendMessage(musicInst.runCommand(event, args)).queue();
                } catch (IOException ex) {
                    Logger.getLogger(CommandHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else {
                event.getChannel().sendMessage(musicInst.runCommand(event)).queue();
            }
        }
    }
    
    public void addCommands() {
        commands = new ArrayList<>();
        
        commands.add(new Ping());
        commands.add(new MoralSupport());
        commands.add(new Role());
        commands.add(new Roll());
        
        //Add help last
        commands.add(new Help());
    }
}
