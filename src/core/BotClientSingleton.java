package core;

import BotClient.BotClient;

public class BotClientSingleton extends BotClient {
    private static BotClientSingleton instance;
    
    
    public BotClientSingleton() {
        super(Config.BOTCLIENT_HOST, Config.BOTCLIENT_TOKEN, false);
    }
    
    public static BotClientSingleton getInstance() {
        if (instance == null) {
            instance = new BotClientSingleton();
        }
        return instance;
    }
    
    public void pendOnStartup() {
        // Spin until BC tells us the game has started
        while (!this.gameStarted());
    }
}
