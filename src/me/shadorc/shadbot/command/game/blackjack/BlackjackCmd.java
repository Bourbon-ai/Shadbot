package me.shadorc.shadbot.command.game.blackjack;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;
import me.shadorc.shadbot.core.command.BaseCmd;
import me.shadorc.shadbot.core.command.CommandCategory;
import me.shadorc.shadbot.core.command.Context;
import me.shadorc.shadbot.utils.DiscordUtils;
import me.shadorc.shadbot.utils.Utils;
import me.shadorc.shadbot.utils.embed.help.HelpBuilder;
import me.shadorc.shadbot.utils.object.Emoji;
import reactor.core.publisher.Mono;

public class BlackjackCmd extends BaseCmd {

	protected static final ConcurrentHashMap<Snowflake, BlackjackManager> MANAGERS = new ConcurrentHashMap<>();

	private static final int MAX_BET = 250_000;

	public BlackjackCmd() {
		super(CommandCategory.GAME, List.of("blackjack"), "bj");
		this.setGameRateLimiter();
	}

	@Override
	public Mono<Void> execute(Context context) {
		final String arg = context.requireArg();

		final Integer bet = Utils.requireBet(context.getMember(), arg, MAX_BET);

		BlackjackManager blackjackManager = MANAGERS.putIfAbsent(context.getChannelId(), new BlackjackManager(context));
		if(blackjackManager == null) {
			blackjackManager = MANAGERS.get(context.getChannelId());
			blackjackManager.start();
		}

		if(blackjackManager.addPlayerIfAbsent(context.getAuthorId(), bet)) {
			return blackjackManager.computeResultsOrShow();
		} else {
			return context.getChannel()
					.flatMap(channel -> DiscordUtils.sendMessage(String.format(Emoji.INFO + " (**%s**) You're already participating.",
							context.getUsername()), channel))
					.then();
		}
	}

	@Override
	public Consumer<EmbedCreateSpec> getHelp(Context context) {
		return new HelpBuilder(this, context)
				.setDescription("Start or join a blackjack game.")
				.addArg("bet", false)
				.addField("Info", "**double down** - increase the initial bet by 100% in exchange for committing to stand"
						+ " after receiving exactly one more card", false)
				.build();
	}
}
