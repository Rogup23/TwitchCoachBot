import org.jibble.pircbot.PircBot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

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


    //private static final List<String> TRIGGER_WORDS = List.of("highlight", "clip", "video", "aufnahme");
    //private static final String TWITCH_CLIENT_ID = "gp762nuuoqcoxypju8c569th9wz7q5";
    //private static final String TWITCH_OAUTH_TOKEN = "2srx37kvgw5gu2khzgopqy15tayjnb";
    //private static final String BROADCASTER_ID = "143799166";


    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        resetInactivityTimer(channel);
        long currentTime = System.currentTimeMillis();

        if (message.contains("?")) {
            // Timestamps von Chat Nachrichten aktualisieren
            senderMessageTimestamps.putIfAbsent(sender, new ArrayList<>());
            List<Long> timestamps = senderMessageTimestamps.get(sender);
            timestamps.add(currentTime);

            // Alte Nachrichten Timestamps entfernen (nach einer Minute)
            timestamps.removeIf(timestamp -> currentTime - timestamp > 60 * 1000);

            // Wurde in der letzten Minute 3-mal eine Frage gestellt? Dann soll der Bot den Streamer darauf hinweisen.
            if (timestamps.size() >= 3) {
                sendMessage(channel, "Frage!!!");
                timestamps.clear(); // Reset the timestamps after sending the message
            }

            // Nachricht in Kleinbuchstaben umwandeln
            String lowerCaseMessage = message.toLowerCase();

            // Timestamps von Fragen aktualisieren
            questionTimestamps.putIfAbsent(lowerCaseMessage, new ArrayList<>());
            List<Long> questionTimes = questionTimestamps.get(lowerCaseMessage);
            questionTimes.add(currentTime);

            // Timestamps von Fragen entfernen (nach einer Minute)
            questionTimes.removeIf(timestamp -> currentTime - timestamp > 60 * 1000);

            // Wurde die gleiche Frage 3-mal in letzter Zeit gestellt? Dann soll der Bot den Streamer darauf hinweisen.
            if (questionTimes.size() >= 3) {
                sendMessage(channel, "Es wurde oft gefragt: \"" + message + "\"");
                questionTimes.clear(); // Reset the question timestamps after sending the message
            }
        }

        //Test Nachricht
        if (message.toLowerCase().contains("hallo")) {
            sendMessage(channel, "Hallo ich bin haraldbotspl!");
        }
        //Clip funktion verworfen
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

    //Weist dem Streamer darauf hin, dass in den letzten 5 Minuten im Chat keine Nachricht geschrieben wurde.
    private void startInactivityTimer() {
        inactivityTimer = new Timer();

        inactivityTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessage("#rogup23", "Ganz schön ruhig hier");
                resetInactivityTimer("#rogup23");
            }
        }, 2 * 60 * 1000, 2 * 60 * 1000);

        resetReminderTimer("#rogup23");
    }

    //Setzt den Inaktivitätstimer zurück
    private void resetInactivityTimer(String channel) {
        inactivityTimer.cancel();
        startInactivityTimer();
    }

    //Setzt den Erinnerungstimer zurück
    private void resetReminderTimer(String channel) {
        reminderTimer = new Timer();
        reminderTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessage(channel, "Hast du schon etwas getrunken?"); //Erinnerung an den Streamer, etwas zu trinken
                //analyzeChat();
                resetReminderTimer("#rogup23");
            }
        }, 3 * 60 * 1000, 3 * 60 * 1000);
    }

    //gestrichen, eine Stimmungsanalyse mithilfe von KI wäre sinnvoller.
    /*
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

    private int getNumberOfViewers() {
        try {
            URL url = new URL("https://api.twitch.tv/helix/streams?user_id=" + BROADCASTER_ID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Client-Id", TWITCH_CLIENT_ID);
            connection.setRequestProperty("Authorization", "Bearer " + TWITCH_OAUTH_TOKEN);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the response to get the viewer count
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray data = jsonResponse.getJSONArray("data");
                if (data.length() > 0) {
                    JSONObject stream = data.getJSONObject(0);
                    return stream.getInt("viewer_count");
                } else {
                    System.out.println("No active stream found.");
                    return 0;
                }
            } else {
                System.out.println("Failed to get viewer count: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    */

}