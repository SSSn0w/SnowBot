package snowbot;

import snowbot.Commands.*;
import snowbot.Music.*;

import java.util.ArrayList;
import java.util.Arrays;

import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandHandler extends ListenerAdapter {
    private String commandPrefix = "sb!";
    private String musicPrefix = "$";
    public static ArrayList<Command> commands = new ArrayList<Command>();
    
    private Music musicInst = new Music();
    private String[] args;
    
    public CommandHandler() {
        addCommands();
        
    }
    
    public void addCommands() {
        commands = new ArrayList<Command>();
        
        commands.add(new Ping());
        commands.add(new MoralSupport());
        commands.add(new Role());
        commands.add(new Roll());
        
        //Add help last
        commands.add(new Help());
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
            musicInst.setCommand(event.getMessage().getContentRaw().split("\\" + musicPrefix)[1]);
            String[] input = event.getMessage().getContentRaw().split("\\" + musicPrefix)[1].split(" ");
            
            if(input.length > 1) {
                args = Arrays.copyOfRange(input, 2, input.length);
                System.out.println(args.length);
                //event.getChannel().sendMessage(musicInst.runCommand(event, args)).queue();
            }
            else {
                event.getChannel().sendMessage(musicInst.runCommand(event)).queue();
            }
        }
    }
}
