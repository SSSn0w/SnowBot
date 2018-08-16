package snowbot.Commands;

public class Ping extends Command{
    public Ping() {
        commandName = "Ping";
        commandCall = "ping";
        commandDescription = "Pings SnowBot";
        commandUsage = "sb!ping";
        commandArgs = 0;
        commandReturn = "Pong! :ping_pong:";
    }
}
