package me.shadorc.shadbot.command.french;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import discord4j.core.spec.EmbedCreateSpec;
import me.shadorc.shadbot.core.command.AbstractCommand;
import me.shadorc.shadbot.core.command.CommandCategory;
import me.shadorc.shadbot.core.command.Context;
import me.shadorc.shadbot.core.command.annotation.Command;
import me.shadorc.shadbot.core.command.annotation.RateLimited;
import me.shadorc.shadbot.data.APIKeys;
import me.shadorc.shadbot.data.APIKeys.APIKey;
import me.shadorc.shadbot.utils.ExceptionUtils;
import me.shadorc.shadbot.utils.FormatUtils;
import me.shadorc.shadbot.utils.NetUtils;
import me.shadorc.shadbot.utils.Utils;
import me.shadorc.shadbot.utils.embed.EmbedUtils;
import me.shadorc.shadbot.utils.embed.HelpBuilder;
import me.shadorc.shadbot.utils.object.message.LoadingMessage;
import reactor.core.publisher.Mono;

@RateLimited
@Command(category = CommandCategory.FRENCH, names = { "dtc" })
public class DtcCmd extends AbstractCommand {

	@Override
	public void execute(Context context) {
		LoadingMessage loadingMsg = new LoadingMessage(context.getClient(), context.getChannelId());

		context.getAuthorAvatarUrl().subscribe(avatarUrl -> {
			try {
				String url = String.format("http://api.danstonchat.com/0.3/view/random?key=%s&format=json", APIKeys.get(APIKey.DTC_API_KEY));

				JSONArray array = new JSONArray(NetUtils.getJSON(url));
				JSONObject quoteObj = Utils.toList(array, JSONObject.class).stream()
						.filter(obj -> obj.getString("content").length() < 1000)
						.findAny()
						.get();

				String content = quoteObj.getString("content").replace("*", "\\*");

				EmbedCreateSpec embed = EmbedUtils.getDefaultEmbed()
						.setAuthor("Quote DansTonChat",
								String.format("https://danstonchat.com/%s.html", quoteObj.getString("id")),
								avatarUrl)
						.setThumbnail("https://danstonchat.com/themes/danstonchat/images/logo2.png")
						.setDescription(FormatUtils.format(content.split("\n"), this::format, "\n"));
				loadingMsg.send(embed);

			} catch (JSONException | IOException err) {
				loadingMsg.send(ExceptionUtils.handleAndGet("getting a quote from DansTonChat.com", context, err));
			}
		});
	}

	private String format(String line) {
		// Set the user name as bold
		if(line.contains(" ")) {
			int index = line.indexOf(' ');
			return "**" + line.substring(0, index) + "** " + line.substring(index + 1);
		}
		return line;
	}

	@Override
	public Mono<EmbedCreateSpec> getHelp(Context context) {
		return new HelpBuilder(this, context)
				.setDescription("Show a random quote from DansTonChat.com")
				.setSource("https://danstonchat.com")
				.build();
	}
}
