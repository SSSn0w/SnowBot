package snowbot.Commands;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.*;
import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

public class Play extends Command{ 
    public Play() {
        commandName = "Play";
        commandCall = "play";
        commandDescription = "Plays a song";
        commandUsage = "sb!play <song>";
        commandArgs = 1;
        commandReturn = "No song selected!";
    }
    
    /*public MessageEmbed roll(int max) {
        EmbedBuilder embed = new EmbedBuilder();
        return embed.setColor(new Color(0x3598db))
                .setTitle("You rolled: " + (new Random().nextInt(max) + 1), null)
                .setAuthor("Roll", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .setFooter("Max value: " + max, null)
                .build();
    }*/
    
    public MessageEmbed test(MessageReceivedEvent commandEvent) {
        EmbedBuilder embed = new EmbedBuilder();
        
        MessageReceivedEvent event = commandEvent;
        VoiceChannel connectedChannel = event.getMember().getVoiceState().getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.openAudioConnection(connectedChannel);
        
        return embed.setColor(new Color(0x3598db))
                .setTitle("Now playing ", null)
                .setAuthor("Play", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .build();
    }
    
    public MessageEmbed play(String song) {
        EmbedBuilder embed = new EmbedBuilder();
        
        //audio code
        
        return embed.setColor(new Color(0x3598db))
                .setTitle("Now playing " + song, null)
                .setAuthor("Play", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .build();
    }
    
    @Override
    public void Args(MessageReceivedEvent commandEvent, String[] args, int numArgs) {
        String song = "";
        
        if(args.length > (numArgs + 1)) {
            commandEvent.getChannel().sendMessage("Incorrect usage!").queue();
            return;
        }
        
        for(int i = 1; i < args.length; i++) {
            if(i > numArgs) {
                break;
            }
            
            song = args[1];
            //commandReturn = play(song);
            commandReturn = test(commandEvent);
        }
        
        commandEvent.getChannel().sendMessage((MessageEmbed)commandReturn).queue();
    }
}

