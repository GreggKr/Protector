package co.protector.bot.commands.useful;

import co.protector.bot.core.UserData;
import co.protector.bot.core.listener.command.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class CriminalHistoryCommand extends Command {
    private String idPattern = "^[a-zA-Z0-9]{18}$";

    @Override
    public String getTrigger() {
        return "history";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"criminalhistory", "his", "ch"};
    }

    @Override
    public String getDescription() {
        return "Check criminal history on a user";
    }

    @Override
    public String getUsage() {
        return "";
    }


    @Override
    public void execute(Message trigger, String args) {
        User checking = null;
        List<User> mentions = trigger.getMentionedUsers();
        if (!mentions.isEmpty()) {
            checking = mentions.get(0);
        } else {
            String id = args.split("\\s+")[0];
            if (id.matches(idPattern)) {
                checking = trigger.getJDA().getUserById(id);
                if (checking == null) {
                    checking = trigger.getAuthor();
                }
            } else {
                if (id.matches("^.*#\\d{4}$")) {
                    String[] nameParts = id.split("#"); // 0: name (Gregg), 1: discrim (4040)
                    Optional<User> oChecking = trigger.getJDA().getUsersByName(nameParts[0], true).stream().filter(u -> u.getDiscriminator().equals(nameParts[1])).findFirst();

                    checking = oChecking.orElseGet(trigger::getAuthor);

                } else {
                    checking = trigger.getAuthor();
                }
            }
        }

        long bans = UserData.getBans(checking.getId());
        long links = UserData.getLinks(checking.getId());
        String user = checking.getName() + "#" + checking.getDiscriminator();
        trigger.getChannel().sendMessage(new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setDescription("__**Criminal History for " + user +
                        "**__\n\n\n**User was banned** __**" + bans + "** __**times**\n\n" +
                        "**Sent** __**" + links + "**__ " + "**discord links** (And I deleted them)")
                .build()).queue();
    }
}
