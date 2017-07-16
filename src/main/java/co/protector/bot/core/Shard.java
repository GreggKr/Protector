package co.protector.bot.core;

import co.protector.bot.Config;
import co.protector.bot.ExitStatus;
import co.protector.bot.Main;
import co.protector.bot.core.listener.CommandListener;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Shard extends ListenerAdapter {
    private final ShardContainer container;
    private final CommandListener commandListener;
    private final ExecutorService commandExecutor;
    private final int shardId;
    private final int totShards;
    private static JDA jda;

    Shard(ShardContainer container, int shardId, int totShards) {
        this.container = container;
        this.shardId = shardId;
        this.totShards = totShards;
        commandListener = new CommandListener();
        ThreadFactoryBuilder threadBuilder = new ThreadFactoryBuilder();
        threadBuilder.setNameFormat(String.format("shard-%02d-command-%%d", shardId));
        this.commandExecutor = Executors.newCachedThreadPool(threadBuilder.build());
    }

    public void reboot() throws RateLimitedException, InterruptedException {
        JDABuilder builder = new JDABuilder(AccountType.BOT).setToken(Config.discord_token);
        if (totShards > 1) {
            builder.useSharding(shardId, totShards);
        }
        try {
            jda = builder.buildBlocking();
            jda.addEventListener(new ModLog());
            jda.addEventListener(new AntiLink());
            jda.addEventListener(new MuteAvoidance());
            jda.addEventListener(this);
        } catch (LoginException e) {
            e.printStackTrace();
            Main.exit(ExitStatus.INVALID_CONFIG);
        }
    }

    public JDA getJda() {
        return jda;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) {
            return;
        }
        String prefix = CommandListener.getPrefix(e.getGuild());
        if (!commandListener.isCommand(e.getMessage().getContent(), prefix)) {
            //Send the servers prefix
            if (e.getMessage().getRawContent().equalsIgnoreCase(e.getJDA().getSelfUser().getAsMention())) {
                e.getChannel().sendMessage("**My prefix here is `" + CommandListener.getPrefix(e.getGuild()) + "`**").queue();
            }
            return;
        }
        commandExecutor.submit(() -> commandListener.execute(e.getGuild(), e.getChannel(), e.getAuthor(), e.getMember(), e.getMessage(), prefix));
    }
}
