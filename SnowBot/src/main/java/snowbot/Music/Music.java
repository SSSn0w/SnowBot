package snowbot.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

public class Music extends AudioEventAdapter {
    private String command = "";
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();
    private Guild guild;
    
    public Music() {
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }
    
    public void setCommand(String cmd) {
        command = cmd;
    }
    
    public MessageEmbed runCommand(MessageReceivedEvent event) {
        guild = event.getGuild();
        
        switch (command) {
            case "play":
                return Play(event);
            case "end":
                return End(event);
            case "now":
                return Now(event);
            case "skip":
                return Skip(event);
            case "queue":
                return Queue(event);
            default:
                return null;
        }
    }
    
    public MessageEmbed runCommand(MessageReceivedEvent event, String[] args) throws IOException {
        guild = event.getGuild();
        
        switch (command) {
            case "play":
                return Play(event, String.join(" ", args));
            case "end":
                return End(event);
            case "now":
                return Now(event);
            case "skip":
                return Skip(event);
            case "queue":
                return Queue(event);
            default:
                return null;
        }
    }
    
    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
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
        
        GuildMusicManager musicManager = getGuildAudioPlayer(commandEvent.getTextChannel().getGuild());
        
        MessageReceivedEvent event = commandEvent;
        
        YoutubeSearch ytSearch = new YoutubeSearch(song);
        String songID = ytSearch.returnID();
        
        VoiceChannel connectedChannel = event.getMember().getVoiceState().getChannel();
        
        if(connectedChannel == null) {
            return embed.setColor(new Color(0x3598db))
                    .setTitle("No Voice Channel!", null)
                    .setDescription("You must be in a voice channel to play a song!")
                    .setAuthor("Music", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                    .build();
        }
        else {
            AudioManager audioManager = guild.getAudioManager();

            playerManager.loadItemOrdered(musicManager, songID, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    musicManager.scheduler.queue(track);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    for (AudioTrack track : playlist.getTracks()) {
                        musicManager.scheduler.queue(track);
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
    }
    
    private MessageEmbed End(MessageReceivedEvent commandEvent) {
        EmbedBuilder embed = new EmbedBuilder();

        MessageReceivedEvent event = commandEvent;
        AudioManager audioManager = event.getGuild().getAudioManager();
        getGuildAudioPlayer(guild).player.destroy();
        audioManager.closeAudioConnection();
        
        return embed.setColor(new Color(0x3598db))
                .setTitle("Queue ended", null)
                .setAuthor("Music", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .build();
    }
    
    private MessageEmbed Now(MessageReceivedEvent commandEvent) {
        EmbedBuilder embed = new EmbedBuilder();
        
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        
        return embed.setColor(new Color(0x3598db))
                .setTitle("Now Playing", null)
                .setDescription(musicManager.player.getPlayingTrack().getInfo().title)
                .setAuthor("Music", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .build();
    }
    
    private MessageEmbed Skip(MessageReceivedEvent commandEvent) {
        try {
            GuildMusicManager musicManager = getGuildAudioPlayer(guild);
            musicManager.scheduler.nextTrack();
            return Now(commandEvent);
        } catch(Exception e) {
            return End(commandEvent);
        }
    }
    
    private MessageEmbed Queue(MessageReceivedEvent commandEvent) {
        EmbedBuilder embed = new EmbedBuilder();
        String list = "";
        
        ArrayList<AudioTrack> arrayQueue = new ArrayList(queue);
        for(int i = 0; i < arrayQueue.size(); i++) {
            list += (i + 1) + ". " + arrayQueue.get(i).getInfo().title + "\n";
        }
        
        return embed.setColor(new Color(0x3598db))
                .setTitle("Queue", null)
                .setDescription(list)
                .setAuthor("Music", null, "https://cdn.discordapp.com/avatars/430694465372684288/92de5decd5352f64de3f2ce73ee7aa24.png")
                .build();
    }
}
