package co.automod.bot.core;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntiLink {
    private static final Pattern discordURL = Pattern.compile("discord\\.(?:me|io|gg)\\/.{0,4}\\w+|discordapp\\.com.{1,4}(?:invite|oauth2).{0,5}\\/");

    @SubscribeEvent
    public void handleMessage(GuildMessageReceivedEvent e) {
        if (e.getAuthor().getId().equals(e.getJDA().getSelfUser().getId())) return;
        //Check if guild is in db and anti link is enabled.
        //Check if he has permissions and return
        String content = e.getMessage().getRawContent();
        content = content.replace("\u200b", "");
        if (!content.contains("discord")) return;
        Matcher m = discordURL.matcher(content);
        if (m.find()) {
            e.getMessage().delete().queue(a -> e.getChannel().sendMessage(String.format("%s \u26D4 **Advertising is not allowed!**", e.getAuthor().getAsMention())).queue());
        }
    }
}