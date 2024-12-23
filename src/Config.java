class Config {
    public static void main(String[] args) throws Exception {

        CoachBot coachBot = new CoachBot();
        coachBot.setVerbose(true);
        try {
            coachBot.connect("irc.twitch.tv", 6667, "oauth:38phogl79u4ygowjnjvsklo0gc9ols");
            coachBot.joinChannel("#rogup23");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}