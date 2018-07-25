package snowbot.Commands;

import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Command<T> {
    public String commandName;
    public String commandCall;
    public String commandDescription;
    public String commandUsage;
    public int commandArgs;
    public T commandReturn;
    
    public void Return(MessageReceivedEvent commandEvent) {
        if(commandReturn.getClass() == String.class) {
            commandEvent.getChannel().sendMessage((String)commandReturn).queue();
        }
        else {
            commandEvent.getChannel().sendMessage((MessageEmbed)commandReturn).queue();
        }
    }
    
    public void Args(MessageReceivedEvent commandEvent, String[] args, int numArgs) {
        if(args.length > (numArgs + 1)) {
            commandEvent.getChannel().sendMessage("Incorrect usage!").queue();
            return;
        }
        
        commandEvent.getChannel().sendMessage((String)commandReturn).queue();
    }
    
    public void Reset() {
        
    }
}
