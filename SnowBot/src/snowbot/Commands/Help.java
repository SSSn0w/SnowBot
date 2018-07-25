package snowbot.Commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.Color;

public class Help extends Command {
    public Help() {
        commandName = "Help";
        commandCall = "help";
        commandDescription = "Brings up the help menu";
        commandUsage = "sb!help | sb!help <command>";
        commandArgs = 1;
        
        EmbedBuilder embed = new EmbedBuilder();
        String description = "";
        
        for(Command com : snowbot.CommandHandler.commands) {
            description += "\n-----------------------\n**" + com.commandCall + "** - " + com.commandDescription;
        }
        
        commandReturn = embed.setColor(new Color(0x3598db))
                .setTitle("Commands", null)
                .setAuthor("Help", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .setDescription(description)
                .build();
    }
    
    @Override
    public void Args(MessageReceivedEvent commandEvent, String[] args, int numArgs) {    
        if(args.length > (numArgs + 1)) {
            commandEvent.getChannel().sendMessage("Incorrect usage!").queue();
            return;
        }
        
        EmbedBuilder embedArgs = new EmbedBuilder();
        String description = "";
        
        for(Command com : snowbot.CommandHandler.commands) {
            if(com.commandCall.equals(args[1])) {
                description += "**" + com.commandName + "**\n" + com.commandDescription +"\n\nUsage: " + com.commandUsage;
                break;
            }
        }
        
        if(description.equals("")) {
            description += "Command selected does not exist!";
        }
        
        embedArgs.setColor(new Color(0x3598db));
        embedArgs.setTitle("Commands", null);
        embedArgs.setAuthor("Help", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png");
        embedArgs.setDescription(description);
        commandReturn = embedArgs.build();
        
        commandEvent.getChannel().sendMessage((MessageEmbed)commandReturn).queue();
    }
    
    @Override
    public void Reset() {
        EmbedBuilder embed = new EmbedBuilder();
        String description = "";
        
        for(Command com : snowbot.CommandHandler.commands) {
            description += "\n-----------------------\n**" + com.commandCall + "** - " + com.commandDescription;
        }
        
        commandReturn = embed.setColor(new Color(0x3598db))
                .setTitle("Commands", null)
                .setAuthor("Help", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .setDescription(description)
                .build();
    }
}
