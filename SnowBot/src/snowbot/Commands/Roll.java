package snowbot.Commands;

import java.awt.Color;
import java.util.Random;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Roll extends Command{ 
    public Roll() {
        commandName = "Roll";
        commandCall = "roll";
        commandDescription = "Rolls a dice (default is 100)";
        commandUsage = "sb!role <max-value>";
        commandArgs = 2000;
        commandReturn = roll(100);
    }
    
    public MessageEmbed roll(int max) {
        EmbedBuilder embed = new EmbedBuilder();
        return embed.setColor(new Color(0x3598db))
                .setTitle("You rolled: " + (new Random().nextInt(max) + 1), null)
                .setAuthor("Roll", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .setFooter("Max value: " + max, null)
                .build();
    }
    
    @Override
    public void Args(MessageReceivedEvent commandEvent, String[] args, int numArgs) {
        int max = 0;
        
        if(args.length > (numArgs + 1)) {
            commandEvent.getChannel().sendMessage("Incorrect usage!").queue();
            return;
        }
        
        for(int i = 1; i < args.length; i++) {
            if(i > numArgs) {
                break;
            }
            
            if(i > 1 && !args[1].matches("-?\\d+") && !args[i].matches("-?\\d+")) {
                commandReturn = roll(100);
            }
            else if(args[1].matches("-?\\d+")) {
                max = Integer.parseInt(args[1]);
                commandReturn = roll(max);
            }
        }
        
        commandEvent.getChannel().sendMessage((MessageEmbed)commandReturn).queue();
    }
}
