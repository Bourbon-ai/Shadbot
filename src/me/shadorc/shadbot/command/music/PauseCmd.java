package me.shadorc.shadbot.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import discord4j.core.spec.EmbedCreateSpec;
import me.shadorc.shadbot.core.command.AbstractCommand;
import me.shadorc.shadbot.core.command.CommandCategory;
import me.shadorc.shadbot.core.command.Context;
import me.shadorc.shadbot.core.command.annotation.Command;
import me.shadorc.shadbot.core.command.annotation.RateLimited;
import me.shadorc.shadbot.music.GuildMusic;
import me.shadorc.shadbot.utils.BotUtils;
import me.shadorc.shadbot.utils.embed.HelpBuilder;
import me.shadorc.shadbot.utils.object.Emoji;
import reactor.core.publisher.Mono;

@RateLimited
@Command(category = CommandCategory.MUSIC, names = { "pause", "unpause", "resume" })
public class PauseCmd extends AbstractCommand {

	@Override
	public Mono<Void> execute(Context context) {
		final GuildMusic guildMusic = context.requireGuildMusic();
		final AudioPlayer audioPlayer = guildMusic.getScheduler().getAudioPlayer();
		audioPlayer.setPaused(!audioPlayer.isPaused());

		return context.getAuthorName()
				.map(username -> {
					if(audioPlayer.isPaused()) {
						return String.format(Emoji.PAUSE + " Music paused by **%s**.", username);
					} else {
						return String.format(Emoji.PLAY + " Music resumed by **%s**.", username);
					}
				})
				.flatMap(message -> {
					return BotUtils.sendMessage(message, context.getChannel());
				})
				.then();
	}

	@Override
	public Mono<EmbedCreateSpec> getHelp(Context context) {
		return new HelpBuilder(this, context)
				.setDescription("Pause current music. Use this command again to resume.")
				.build();
	}
}