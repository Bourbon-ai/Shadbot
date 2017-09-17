package me.shadorc.discordbot.command.admin.setting;

import me.shadorc.discordbot.Emoji;
import me.shadorc.discordbot.MissingArgumentException;
import me.shadorc.discordbot.command.Context;
import me.shadorc.discordbot.data.Config;
import me.shadorc.discordbot.data.Storage;
import me.shadorc.discordbot.data.Storage.Setting;
import me.shadorc.discordbot.utils.BotUtils;
import me.shadorc.discordbot.utils.StringUtils;
import me.shadorc.discordbot.utils.Utils;
import sx.blah.discord.util.EmbedBuilder;

public class VolumeSettingCmd implements SettingCmd {

	private static final int MIN_VOLUME = 1;
	private static final int MAX_VOLUME = 50;

	@Override
	public void execute(Context context, String arg) throws MissingArgumentException {
		if(arg == null) {
			throw new MissingArgumentException();
		}

		if(!StringUtils.isPositiveInt(arg)) {
			BotUtils.sendMessage(Emoji.GREY_EXCLAMATION + " Invalid number, must be between " + MIN_VOLUME + " and " + MAX_VOLUME + ".", context.getChannel());
			return;
		}

		int vol = Integer.parseInt(arg);
		if(vol < MIN_VOLUME || vol > MAX_VOLUME) {
			BotUtils.sendMessage(Emoji.GREY_EXCLAMATION + " Default volume must be between " + MIN_VOLUME + " and " + MAX_VOLUME + ". ", context.getChannel());
			return;
		}

		Storage.saveSetting(context.getGuild(), Setting.DEFAULT_VOLUME, vol);
		BotUtils.sendMessage(Emoji.CHECK_MARK + " " + vol + "% is now the default volume for this server.", context.getChannel());
	}

	@Override
	public void showHelp(Context context) {
		EmbedBuilder builder = Utils.getDefaultEmbed()
				.withAuthorName("Help for setting: " + Setting.DEFAULT_VOLUME.toString())
				.appendDescription("**" + this.getDescription() + "**")
				.appendField("Argument", "**volume** - min: " + MIN_VOLUME + " / max: " + MAX_VOLUME + " / default: " + Config.DEFAULT_VOLUME, false)
				.appendField("Usage", "`" + context.getPrefix() + "settings " + Setting.DEFAULT_VOLUME.toString() + " <volume>`", false)
				.appendField("Example", "`" + context.getPrefix() + "settings " + Setting.DEFAULT_VOLUME.toString() + " 42`", false);
		BotUtils.sendEmbed(builder.build(), context.getChannel());
	}

	@Override
	public String getDescription() {
		return "Change music default volume.";
	}

}
