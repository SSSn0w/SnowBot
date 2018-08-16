package snowbot.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

public class Music {
    private String command = "";
    private final String musicPrefix = "$";
    private AudioPlayerManager playerManager;
    private AudioPlayer player;
    private TrackScheduler trackScheduler;
    
    public Music() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        
        player = playerManager.createPlayer();
        trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);
    }
    
    public void setCommand(String cmd) {
        command = cmd;
    }
    
    public MessageEmbed runCommand(MessageReceivedEvent event) {
        switch (command) {
            case "play":
                return Play(event);
            case "end":
                return End(event);
            default:
                return null;
        }
    }
    
    public MessageEmbed runCommand(MessageReceivedEvent event, String[] args) {
        switch (command) {
            case "play":
                return Play(event, String.join(" ", args));
            case "end":
                return End(event);
            default:
                return null;
        }
    }
    
    public MessageEmbed Play(MessageReceivedEvent commandEvent) {
        EmbedBuilder embed = new EmbedBuilder();
        
        return embed.setColor(new Color(0x3598db))
                .setTitle("Now playing", null)
                .setAuthor("Music", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .setDescription("Nothing, because no song was entered :angry:")
                .build();
    }
    
    public MessageEmbed Play(MessageReceivedEvent commandEvent, String song) {
        EmbedBuilder embed = new EmbedBuilder();
        
        MessageReceivedEvent event = commandEvent;
        
        VoiceChannel connectedChannel = event.getMember().getVoiceState().getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();
        
        audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
        playerManager.loadItem(song, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                trackScheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    trackScheduler.queue(track);
                }
            }

            @Override
            public void noMatches() {
                // Notify the user that we've got nothing
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                // Notify the user that everything exploded
            }
        });
        
        audioManager.openAudioConnection(connectedChannel);
        
        return embed.setColor(new Color(0x3598db))
                .setTitle("Now playing", null)
                .setDescription(song)
                .setAuthor("Music", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .build();
    }
    
    public MessageEmbed End(MessageReceivedEvent commandEvent) {
        EmbedBuilder embed = new EmbedBuilder();
        
        MessageReceivedEvent event = commandEvent;
        AudioManager audioManager = event.getGuild().getAudioManager();
        playerManager.shutdown();
        audioManager.closeAudioConnection();
        
        return embed.setColor(new Color(0x3598db))
                .setTitle("Queue ended", null)
                .setAuthor("Music", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .build();
    }
}
