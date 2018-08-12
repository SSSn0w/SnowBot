package snowbot.Music;

import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

public class Music {
    private String command = "";
    
    public Music() {
        
    }
    
    public void setCommand(String cmd) {
        command = cmd;
    }
    
    public MessageEmbed runCommand(MessageReceivedEvent event) {
        if(command.equals("play")) {
            return Play(event);
        }
        else if(command.equals("end")) {
            return End(event);
        }
        else {
            return null;
        }
    }
    
    public MessageEmbed runCommand(MessageReceivedEvent event, String[] args) {
        if(command.equals("play")) {
            return Play(event);
        }
        else if(command.equals("end")) {
            return End(event);
        }
        else {
            return null;
        }
    }
    
    public MessageEmbed Play(MessageReceivedEvent commandEvent) {
        EmbedBuilder embed = new EmbedBuilder();
        
        MessageReceivedEvent event = commandEvent;
        VoiceChannel connectedChannel = event.getMember().getVoiceState().getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.openAudioConnection(connectedChannel);
        
        return embed.setColor(new Color(0x3598db))
                .setTitle("Now playing ", null)
                .setAuthor("Music", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .build();
    }
    
    public MessageEmbed End(MessageReceivedEvent commandEvent) {
        EmbedBuilder embed = new EmbedBuilder();
        
        MessageReceivedEvent event = commandEvent;
        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.closeAudioConnection();
        
        return embed.setColor(new Color(0x3598db))
                .setTitle("Queue ended", null)
                .setAuthor("Music", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .build();
    }
}
