package snowbot.Commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MoralSupport extends Command{
    public MoralSupport() {
        commandName = "Moral Support";
        commandCall = "moralsupport";
        commandDescription = "Gives moral support";
        commandUsage = "sb!moralsupport | sb!moralsupport <user>";
        commandArgs = 1;
        commandReturn = "You can do it! I believe in you!";
    }
    
    @Override
    public void Args(MessageReceivedEvent commandEvent, String[] args, int numArgs) {
        String message = "";
        
        if(args.length > (numArgs + 1)) {
            commandEvent.getChannel().sendMessage("Incorrect usage!").queue();
            return;
        }
        
        for(int i = 1; i < args.length; i++) {
            if(i > numArgs) {
                break;
            }
            message += args[i] + " ";
        }
        
        commandEvent.getChannel().sendMessage(message + (String)commandReturn).queue();
    }
}
