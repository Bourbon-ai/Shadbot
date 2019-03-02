package me.shadorc.shadbot.command.owner;

import java.util.List;
import java.util.function.Consumer;

import discord4j.core.spec.EmbedCreateSpec;
import me.shadorc.shadbot.Shadbot;
import me.shadorc.shadbot.core.command.BaseCmd;
import me.shadorc.shadbot.core.command.CommandCategory;
import me.shadorc.shadbot.core.command.CommandPermission;
import me.shadorc.shadbot.core.command.Context;
import me.shadorc.shadbot.data.premium.Relic;
import me.shadorc.shadbot.data.premium.Relic.RelicType;
import me.shadorc.shadbot.exception.CommandException;
import me.shadorc.shadbot.utils.DiscordUtils;
import me.shadorc.shadbot.utils.FormatUtils;
import me.shadorc.shadbot.utils.StringUtils;
import me.shadorc.shadbot.utils.Utils;
import me.shadorc.shadbot.utils.embed.help.HelpBuilder;
import me.shadorc.shadbot.utils.object.Emoji;
import reactor.core.publisher.Mono;

public class GenerateRelicCmd extends BaseCmd {

	public GenerateRelicCmd() {
		super(CommandCategory.OWNER, CommandPermission.OWNER, List.of("generate_relic", "generate-relic", "generaterelic"));
	}

	@Override
	public Mono<Void> execute(Context context) {
		final String arg = context.requireArg();

		final RelicType type = Utils.getEnum(RelicType.class, context.getArg().get());
		if(type == null) {
			throw new CommandException(String.format("`%s` in not a valid type. %s",
					arg, FormatUtils.options(RelicType.class)));
		}

		final Relic relic = Shadbot.getPremium().generateRelic(type);
		return context.getChannel()
				.flatMap(channel -> DiscordUtils.sendMessage(String.format(Emoji.CHECK_MARK + " %s relic generated: **%s**",
						StringUtils.capitalize(type.toString()), relic.getId()), channel))
				.then();
	}

	@Override
	public Consumer<EmbedCreateSpec> getHelp(Context context) {
		return new HelpBuilder(this, context)
				.setDescription("Generate a relic.")
				.addArg(RelicType.values(), false)
				.build();
	}
}
