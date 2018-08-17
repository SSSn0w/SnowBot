package snowbot.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.awt.Color;
import java.io.IOException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

public class Music {
    private String command = "";
    private final String musicPrefix = "$";
    private final AudioPlayerManager playerManager;
    private final AudioPlayer player;
    private final TrackScheduler trackScheduler;
    
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
            case "now":
                return Now(event);
            case "skip":
                return Skip(event);
            default:
                return null;
        }
    }
    
    public MessageEmbed runCommand(MessageReceivedEvent event, String[] args) throws IOException {
        switch (command) {
            case "play":
                return Play(event, String.join(" ", args));
            case "end":
                return End(event);
            case "now":
                return Now(event);
            case "skip":
                return Skip(event);
            default:
                return null;
        }
    }
    
    private MessageEmbed Play(MessageReceivedEvent commandEvent) {
        EmbedBuilder embed = new EmbedBuilder();
        
        return embed.setColor(new Color(0x3598db))
                .setTitle("Now playing", null)
                .setAuthor("Music", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .setDescription("Nothing, because no song was entered :angry:")
                .build();
    }
    
    private MessageEmbed Play(MessageReceivedEvent commandEvent, String song) throws IOException {
        EmbedBuilder embed = new EmbedBuilder();
        
        MessageReceivedEvent event = commandEvent;
        
        YoutubeSearch ytSearch = new YoutubeSearch(song);
        String songID = ytSearch.returnID();
        
        VoiceChannel connectedChannel = event.getMember().getVoiceState().getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();
        
        audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
        playerManager.loadItem(songID, new AudioLoadResultHandler() {
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
                .setTitle("Track Queued", null)
                .setDescription(ytSearch.returnTitle())
                .setAuthor("Music", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .build();
    }
    
    private MessageEmbed End(MessageReceivedEvent commandEvent) {
        EmbedBuilder embed = new EmbedBuilder();

        MessageReceivedEvent event = commandEvent;
        AudioManager audioManager = event.getGuild().getAudioManager();
        player.destroy();
        audioManager.closeAudioConnection();
        
        return embed.setColor(new Color(0x3598db))
                .setTitle("Queue ended", null)
                .setAuthor("Music", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .build();
    }
    
    private MessageEmbed Now(MessageReceivedEvent commandEvent) {
        EmbedBuilder embed = new EmbedBuilder();
        
        return embed.setColor(new Color(0x3598db))
                .setTitle("Now Playing", null)
                .setDescription(player.getPlayingTrack().getInfo().title)
                .setAuthor("Music", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .build();
    }
    
    private MessageEmbed Skip(MessageReceivedEvent commandEvent) {
        try {
            trackScheduler.nextTrack();
            return Now(commandEvent);
        } catch(Exception e) {
            return End(commandEvent);
        }
    }
}
