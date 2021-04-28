package com.disbots.spark.commands.system;

import com.disbots.spark.util.ColorPalette;
import com.disbots.spark.util.CommandHandler;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Ping extends CommandHandler
{
    public Ping()
    {
        super("ping");
    }

    @Override
    protected void runCommand(MessageCreateEvent message, Server server, ServerTextChannel channel, User user, String[] args)
    {
        /* Sending The embed and checking for errors in calculating the latency. */
        try
        {
            SendEmbed(message);
        }
        catch (InterruptedException e)
        {
            EmbedBuilder ErrorEmbed = new EmbedBuilder()
                    .setDescription("There was an error evaluating the latency! Please contact DisBots Inc.")
                    .setFooter("", message.getMessageAuthor().getAvatar())
                    .setColor(ColorPalette.ERROR.getCode());
            message.getChannel().sendMessage(ErrorEmbed);
        }
    }

    private void SendEmbed(MessageCreateEvent message) throws InterruptedException
    {
        /* Sending and editing the embed. */

        long GatewayLatency = message.getApi().getLatestGatewayLatency().toMillis();
        CompletableFuture<Void> RESTLatency = message.getApi().measureRestLatency().thenAccept(Time -> {
            EmbedBuilder InitialPing = new EmbedBuilder()
                    .setDescription(":ping_pong: Testing Ping...")
                    .setColor(ColorPalette.NEUTRAL.getCode())
                    .setFooter("", message.getMessageAuthor().getAvatar());

            EmbedBuilder PingEmbed;
            PingEmbed = new EmbedBuilder()
                    .setTitle(":ping_pong: Pong!")
                    .setDescription(
                            "Bot Latency: " + "**"+GatewayLatency+"**" + "ms\n" +
                                    "Rest latency: " + "**"+Time.toMillis()+"**" + "ms\n")
                    .setFooter(message.getMessageAuthor().getDisplayName(), message.getMessageAuthor().getAvatar())
                    .setColor(ColorPalette.NEUTRAL.getCode());

            EmbedBuilder finalPingEmbed = PingEmbed;
            message.getChannel().sendMessage(InitialPing).thenAccept(MessageToBeEdited -> MessageToBeEdited.getApi().getThreadPool().getScheduler().schedule(() -> {
                MessageToBeEdited.edit(finalPingEmbed);
            }, 1, TimeUnit.SECONDS));
        });
    }
}
