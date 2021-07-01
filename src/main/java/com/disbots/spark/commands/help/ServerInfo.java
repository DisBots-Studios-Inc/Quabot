/*
 * Copyright (C) 2021 Game Glide
 *
 * This file is part of Spark.
 *
 * Spark is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser general Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 *  Spark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.disbots.spark.commands.help;

import com.disbots.spark.util.embeds.EmbedColorPalette;
import com.disbots.spark.util.embeds.EmbedMaker;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.Nameable;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.util.logging.ExceptionLogger;

public class ServerInfo implements CommandExecutor
{
    @Command(aliases = {"ServerInfo", "SInfo", "ServerI"}, description = "Displays information about the current server.", usage = "ServerInfo")
    public void onCommand(MessageCreateEvent message)
    {
        Server currentServer = message.getServer().get();

        EmbedBuilder Embed = new EmbedBuilder()
                .setAuthor(message.getMessage().getAuthor().getName(), "", message.getMessage().getAuthor().getAvatar())
                .setTitle("Information on " + currentServer.getName())
                .addField("Server Owner", currentServer.getOwner().get().getDiscriminatedName())
                .addField("Server created at", currentServer.getCreationTimestamp().toString())
                .addField("Members", "Users: " + currentServer.getMembers().stream().filter(u -> !u.isBot()).count() + "\n" + "Bot: " + currentServer.getMembers().stream().filter(User::isBot).count() + "\n" + "Total: " + currentServer.getMembers().size())
                .addField("Channels", "Text: " + currentServer.getTextChannels().size() + "\n" + "Voice: " + currentServer.getVoiceChannels().size())
                .addField("Server Boosts: ", "Boost count: " + currentServer.getBoostCount() + "\n" + "Boost level: " + currentServer.getBoostLevel())
                .addField("Roles: ", Integer.toString(currentServer.getRoles().size()))
                .addField("Verification Level:", currentServer.getVerificationLevel().toString())
                .setColor(EmbedColorPalette.NEUTRAL.getCode())
                .setThumbnail(currentServer.getIcon().get().getUrl().toString())
                .setFooter("Designed by DisBots Studios Inc.", "");

        message.getChannel().sendMessage(Embed);
    }
}
