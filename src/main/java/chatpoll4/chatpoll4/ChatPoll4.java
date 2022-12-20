package chatpoll4.chatpoll4;
import java.lang.*;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Set;
import java.util.HashSet;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public class ChatPoll4 extends JavaPlugin implements CommandExecutor {
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private Map<String, Integer> responses; // map of answer choices to number of responses
    private Set<String> votedPlayers; // set of players who have already voted

    public boolean onCommand(@Nonnull final CommandSender commandSender, @Nonnull final Command command, @Nonnull final String s, @Nonnull final String[] strings) {
        Player player = null;
        if (commandSender instanceof Player)
            player = (Player) commandSender;

        if (player != null) {
            Logger logger = getLogger(); // get the plugin's logger
            logger.info("sender is a player");
            if (command.getName().equalsIgnoreCase("poll")) {
                if (strings.length < 2) {
                    // not enough arguments
                    logger = getLogger(); // get the plugin's logger
                    logger.info("Command was not given enough Arguments");
                    return false;
                }
                String question = strings[0];
                List<String> choices = Arrays.asList(strings).subList(1, strings.length);

                // create a component for each answer choice
                logger = getLogger(); // get the plugin's logger
                logger.info("creating Choice components");
                ComponentBuilder builder = new ComponentBuilder(question).append("\n");
                for (String choice : choices) {
                    builder.append(choice).color(ChatColor.AQUA).event(
                                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/answer " + choice))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to vote for " + choice).create()))
                            .append(" ");
                }

                // send the poll to the player
                player.spigot().sendMessage(builder.create());
                logger = getLogger(); // get the plugin's logger
                logger.info("sending poll to player");

                // initialize the responses map and voted players set
                logger = getLogger(); // get the plugin's logger
                logger.info("Initializing Response map and voted players set");
                responses = new HashMap<>();
                votedPlayers = new HashSet<>();
                for (String choice : choices) {
                    responses.put(choice, 0);
                }
                logger = getLogger(); // get the plugin's logger
                logger.info("Initialized response map and voted players set");

                // schedule a task to display the results after 30 seconds
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // build the message
                        Logger logger = getLogger(); // get the plugin's logger
                        logger.info("Building Message");
                        StringBuilder message = new StringBuilder();
                        message.append(question).append("\n");
                        for (String choice : responses.keySet()) {
                            message.append(choice).append(": ").append(responses.get(choice)).append("\n");
                        }
                        // broadcast the message
                        Bukkit.broadcastMessage(message.toString());
                    }
                }.runTaskLater(this, 600L); // run the task in 30 seconds
                return true;
            } else if (command.getName().equalsIgnoreCase("answer")) {
                if (strings.length != 1) {
                    // incorrect number of arguments
                    logger = getLogger(); // get the plugin's logger
                    logger.info("Incorrect number of arguments for /answer command");
                    return false;
                }

                // check if the player has already voted
                String choice = strings[0];
                if (votedPlayers.contains(player.getName())) {
                    player.sendMessage("You have already voted in this poll!");
                    return true;
                }

                // add the player to the voted players set
                votedPlayers.add(player.getName());

                // update the responses map
                if (responses.containsKey(choice)) {
                    int currentCount = responses.get(choice);
                    responses.put(choice, currentCount + 1);
                } else {
                    player.sendMessage("Invalid choice: " + choice);
                }
                return true;
            }
        }
        return false;
    }
}