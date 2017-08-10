package me.shadorc.discordbot.music;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Timer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;

import me.shadorc.discordbot.events.AudioEventListener;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

public class GuildMusicManager {

	public final static AudioPlayerManager PLAYER_MANAGER = new DefaultAudioPlayerManager();
	private final static Map<Long, GuildMusicManager> MUSIC_MANAGERS = new HashMap<>();

	private final IGuild guild;
	private final AudioPlayer player;
	private final TrackScheduler scheduler;
	private final AudioEventListener audioEventListener;
	private final Timer leaveTimer;
	private IChannel channel;

	private GuildMusicManager(IGuild guild, AudioPlayerManager manager) {
		this.guild = guild;
		this.player = manager.createPlayer();
		this.scheduler = new TrackScheduler(player);
		this.audioEventListener = new AudioEventListener(guild, scheduler);
		this.player.addListener(audioEventListener);
		this.leaveTimer = new Timer(60 * 1000, e -> {
			this.scheduler.stop();
			this.leave();
		});
	}

	public void scheduleLeave() {
		leaveTimer.start();
	}

	public void cancelLeave() {
		leaveTimer.stop();
	}

	public void leave() {
		guild.getClient().getOurUser().getVoiceStateForGuild(guild).getChannel().leave();
		leaveTimer.stop();
	}

	public void setChannel(IChannel channel) {
		this.channel = channel;
		this.audioEventListener.setChannel(channel);
	}

	public AudioProvider getAudioProvider() {
		return new AudioProvider(player);
	}

	public AudioPlayer getAudioPlayer() {
		return player;
	}

	public TrackScheduler getScheduler() {
		return scheduler;
	}

	public IChannel getChannel() {
		return channel;
	}

	public boolean isCancelling() {
		return leaveTimer.isRunning();
	}

	public static synchronized GuildMusicManager getGuildAudioPlayer(IGuild guild) {
		GuildMusicManager musicManager = MUSIC_MANAGERS.get(guild.getLongID());

		if(musicManager == null) {
			musicManager = new GuildMusicManager(guild, PLAYER_MANAGER);
			MUSIC_MANAGERS.put(guild.getLongID(), musicManager);
		}

		guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());

		return musicManager;
	}
}
