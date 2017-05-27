package automod.bot.settings;

import automod.bot.core.settings.GuildSetting;
import automod.bot.core.settings.types.TextChannelSettingType;

public class ModLogChannelSetting extends GuildSetting<TextChannelSettingType> {
    @Override
    protected TextChannelSettingType getSettingsType() {
        return new TextChannelSettingType(true);
    }

    @Override
    public String getKey() {
        return "modlog";
    }

    @Override
    public String dbTable() {
        return "modlog";
    }

    @Override
    public String dbField() {
        return "value";
    }

    @Override
    public String getDefault() {
        return "";
    }

    @Override
    public String getDescription() {
        return "The channel you want modlog-events to go to \n\n" +
                "false -> disabled\n" +
                "#channelname -> set it to that channel";
    }
}