class Config {
    public static void main(String[] args) throws Exception {

        CoachBot coachBot = new CoachBot();
        coachBot.setVerbose(true);
        try {
            coachBot.connect("irc.twitch.tv", 6667, "oauth:3yr7z9erqgs28idjtfzydsugzkaj80");
            coachBot.joinChannel("#rogup23");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}