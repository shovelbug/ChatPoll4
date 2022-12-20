package chatpoll4.chatpoll4;

import chatpoll4.chatpoll4.commands.PollCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ChatPoll4 extends JavaPlugin {

    private static ChatPoll4 instance;

    public static ChatPoll4 getInstance() {
        return instance == null ? instance = new ChatPoll4() : instance;
    }

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("poll")).setExecutor(new PollCommand());
    }

    @Override
    public void onDisable() {
    }


}