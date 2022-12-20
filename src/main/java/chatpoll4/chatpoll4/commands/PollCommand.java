package chatpoll4.chatpoll4.commands;

import chatpoll4.chatpoll4.ChatPoll4;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

public class PollCommand implements CommandExecutor {

    private Map<String, Integer> responses; // map of answer choices to number of responses
    private Set<String> votedPlayers; // set of players who have already voted


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] strings) {
        Player player = null;
        if (commandSender instanceof Player)
            player = (Player) commandSender;

        if (player != null) {
            Logger logger = Bukkit.getLogger(); // get the plugin's logger
            logger.info("sender is a player");
            if (command.getName().equalsIgnoreCase("poll")) {
                if (strings.length < 2) {
                    // not enough arguments
                    logger = Bukkit.getLogger(); // get the plugin's logger
                    logger.info("Command was not given enough Arguments");
                    return false;
                }
                String question = strings[0];
                List<String> choices = Arrays.asList(strings).subList(1, strings.length);

                // create a component for each answer choice
                logger = Bukkit.getLogger(); // get the plugin's logger
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
                logger = Bukkit.getLogger(); // get the plugin's logger
                logger.info("sending poll to player");

                // initialize the responses map and voted players set
                logger = Bukkit.getLogger(); // get the plugin's logger
                logger.info("Initializing Response map and voted players set");
                responses = new HashMap<>();
                votedPlayers = new HashSet<>();
                for (String choice : choices) {
                    responses.put(choice, 0);
                }
                logger = Bukkit.getLogger(); // get the plugin's logger
                logger.info("Initialized response map and voted players set");

                // schedule a task to display the results after 30 seconds
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // build the message
                        Logger logger = Bukkit.getLogger(); // get the plugin's logger
                        logger.info("Building Message");
                        StringBuilder message = new StringBuilder();
                        message.append(question).append("\n");
                        for (String choice : responses.keySet()) {
                            message.append(choice).append(": ").append(responses.get(choice)).append("\n");
                        }
                        // broadcast the message
                        Bukkit.broadcastMessage(message.toString());
                    }
                }.runTaskLater(ChatPoll4.getInstance(), 600L); // run the task in 30 seconds
                return true;
            } else if (command.getName().equalsIgnoreCase("answer")) {
                if (strings.length != 1) {
                    // incorrect number of arguments
                    logger = Bukkit.getLogger(); // get the plugin's logger
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
