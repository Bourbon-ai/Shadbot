package me.shadorc.shadbot.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.spec.EmbedCreateSpec;
import me.shadorc.shadbot.core.command.BaseCmd;
import me.shadorc.shadbot.core.command.CommandCategory;
import me.shadorc.shadbot.core.command.Context;
import me.shadorc.shadbot.music.GuildMusic;
import me.shadorc.shadbot.object.help.HelpBuilder;
import me.shadorc.shadbot.utils.DiscordUtils;
import me.shadorc.shadbot.utils.FormatUtils;
import me.shadorc.shadbot.utils.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class PlaylistCmd extends BaseCmd {

    public PlaylistCmd() {
        super(CommandCategory.MUSIC, List.of("playlist"));
        this.setDefaultRateLimiter();
    }

    @Override
    public Mono<Void> execute(Context context) {
        final GuildMusic guildMusic = context.requireGuildMusic();

        final Consumer<EmbedCreateSpec> embedConsumer = DiscordUtils.getDefaultEmbed()
                .andThen(embed -> embed.setAuthor("Playlist", null, context.getAvatarUrl())
                        .setThumbnail("http://icons.iconarchive.com/icons/dtafalonso/yosemite-flat/512/Music-icon.png")
                        .setDescription(PlaylistCmd.formatPlaylist(guildMusic.getTrackScheduler().getPlaylist())));

        return context.getChannel()
                .flatMap(channel -> DiscordUtils.sendMessage(embedConsumer, channel))
                .then();
    }

    private static String formatPlaylist(Collection<AudioTrack> queue) {
        if (queue.isEmpty()) {
            return "**The playlist is empty.**";
        }

        final StringBuilder playlistStr = new StringBuilder(String.format("**%s in the playlist:**%n", StringUtils.pluralOf(queue.size(), "music")));

        int count = 1;
        for (final AudioTrack track : queue) {
            final String name = String.format("%n\t**%d.** %s", count, FormatUtils.trackName(track.getInfo()));
            if (playlistStr.length() + name.length() < 1800) {
                playlistStr.append(name);
            } else {
                playlistStr.append("\n\t...");
                break;
            }
            count++;
        }
        return playlistStr.toString();
    }

    @Override
    public Consumer<EmbedCreateSpec> getHelp(Context context) {
        return new HelpBuilder(this, context)
                .setDescription("Show current playlist.")
                .build();
    }
}