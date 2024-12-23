import org.jibble.pircbot.PircBot;

public class CoachBot extends PircBot {
    public CoachBot() {
        this.setName("HaraldBot");
    }

    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        //sendMessage(channel, "Hallo ich bin Harald!");
        if (message.toLowerCase().contains("hello")) {
            sendMessage(channel, "Hello there!");
        }
        if (message.equalsIgnoreCase("test")) {
            sendMessage(channel, "Das ist ein Test!");
        }
    }
}