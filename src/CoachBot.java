import org.jibble.pircbot.PircBot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class CoachBot extends PircBot {
    private Timer inactivityTimer;
    private Timer reminderTimer;
    private Map<String, List<Long>> senderMessageTimestamps;
    private Map<String, List<Long>> questionTimestamps;

    public CoachBot() {
        this.setName("haraldbotspl");
        startInactivityTimer();
        senderMessageTimestamps = new HashMap<>();
        questionTimestamps = new HashMap<>();
    }

    /*
    private static final List<String> TRIGGER_WORDS = List.of("highlight", "clip", "video", "aufnahme");
    private static final String TWITCH_CLIENT_ID = "gp762nuuoqcoxypju8c569th9wz7q5";
    private static final String TWITCH_OAUTH_TOKEN = "3yr7z9erqgs28idjtfzydsugzkaj80";
    private static final String BROADCASTER_ID = "1230080320";
    */

    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        resetInactivityTimer(channel);
        long currentTime = System.currentTimeMillis();

        if (message.contains("?")) {
            // Update the sender's message timestamps
            senderMessageTimestamps.putIfAbsent(sender, new ArrayList<>());
            List<Long> timestamps = senderMessageTimestamps.get(sender);
            timestamps.add(currentTime);

            // Remove timestamps older than 1 minute
            timestamps.removeIf(timestamp -> currentTime - timestamp > 60 * 1000);

            // Check if the sender has sent 3 messages containing "?" within the last minute
            if (timestamps.size() >= 3) {
                sendMessage(channel, "Frage!!!");
                timestamps.clear(); // Reset the timestamps after sending the message
            }

            // Convert message to lowercase for case-insensitive comparison
            String lowerCaseMessage = message.toLowerCase();

            // Update the question timestamps
            questionTimestamps.putIfAbsent(lowerCaseMessage, new ArrayList<>());
            List<Long> questionTimes = questionTimestamps.get(lowerCaseMessage);
            questionTimes.add(currentTime);

            // Remove question timestamps older than 1 minute
            questionTimes.removeIf(timestamp -> currentTime - timestamp > 60 * 1000);

            // Check if the same question has been asked by different users within the last minute
            if (questionTimes.size() >= 3) {
                sendMessage(channel, "Es wurde oft gefragt: \"" + message + "\"");
                questionTimes.clear(); // Reset the question timestamps after sending the message
            }
        }

        if (message.toLowerCase().contains("hello")) {
            sendMessage(channel, "Hello there!");
        }
        /*
        if (containsTrigger(message)) { //Clip testen
            sendMessage(channel, "Clip the last 30 seconds");
            String clipUrl = createClip();
            if (clipUrl != null) {
                sendMessage(channel, "Clip created: " + clipUrl);
            } else {
                sendMessage(channel, "Failed to create clip.");
            }
        }
        */
    }

    /*
    private static boolean containsTrigger(String input) {
        for (String trigger : TRIGGER_WORDS) {
            if (input.toLowerCase().contains(trigger)) {
                return true;}}
        return false;}

    private String createClip() {
        try {
            URL url = new URL("https://api.twitch.tv/helix/clips?broadcaster_id=" + BROADCASTER_ID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", TWITCH_OAUTH_TOKEN);
            connection.setRequestProperty("Client-Id", TWITCH_CLIENT_ID);
            connection.setDoOutput(true);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_ACCEPTED) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // Parse the response to get the clip URL
                // Assuming the response contains a JSON object with a "data" array containing the clip URL
                String responseBody = response.toString();
                // Extract the clip URL from the response (this is a simplified example)
                String clipUrl = responseBody.substring(responseBody.indexOf("url\":\"") + 6, responseBody.indexOf("\","));
                return clipUrl;
            } else {
                System.out.println("Failed to create clip: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    */

    private void startInactivityTimer() {
        inactivityTimer = new Timer();
        reminderTimer = new Timer();

        inactivityTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessage("#rogup23", "Ganz schön ruhig hier");
                resetInactivityTimer("#rogup23");
            }
        }, 5 * 60 * 1000, 5 * 60 * 1000);

        reminderTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessage("#rogup23", "Hast du schon etwas getrunken?");
                analyzeChat();
                resetReminderTimer("#rogup23");
            }
        }, 2 * 60 * 1000, 2 * 60 * 1000);
    }
    //Nach Timer Ablauf: TTS Nachricht Chat Inaktiv

    private void resetInactivityTimer(String channel) {
        inactivityTimer.cancel();
        startInactivityTimer();
    }

    private void resetReminderTimer(String channel) {
        reminderTimer.cancel();
        startInactivityTimer();
    }

    private void analyzeChat() {
        System.out.println("Analyzing chat...");

        // Example: Get the number of viewers (this is a placeholder, replace with actual logic to get viewers)
        int numberOfViewers = getNumberOfViewers();

        // Count the number of messages in the last minute
        long currentTime = System.currentTimeMillis();
        int messageCount = 0;
        for (List<Long> timestamps : senderMessageTimestamps.values()) {
            for (Long timestamp : timestamps) {
                if (currentTime - timestamp <= 60 * 1000) {
                    messageCount++;
                }
            }
        }

        // Print the number of viewers and messages
        System.out.println("Number of viewers: " + numberOfViewers);
        System.out.println("Messages in the last minute: " + messageCount);

        // Print the appropriate comment
        if (messageCount > numberOfViewers) {
            System.out.println("Oh, more comments than viewers in the last minute! Nice!");
        } else {
            System.out.println("Less comments than viewers in the last minute...");
        }
    }

    // Placeholder method to get the number of viewers
    private int getNumberOfViewers() {
        return 10;
    }

    //TTS? TTS API, z.B. Google Text-to-Speech

    //Laune erkennen, Chat inaktiv? Formel?
}