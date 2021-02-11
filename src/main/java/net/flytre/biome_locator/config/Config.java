package net.flytre.biome_locator.config;

public class Config {


    private final ClientConfig client;
    private final ServerConfig server;

    public Config() {
        this.client = new ClientConfig();
        this.server = new ServerConfig();
    }

    public ClientConfig getClient() {
        return client;
    }

    public ServerConfig getServer() {
        return server;
    }

    public static class ClientConfig {
        private final UILocation uiLocation;

        public ClientConfig() {
            this.uiLocation = UILocation.TOP_LEFT;
        }

        public UILocation getUiLocation() {
            return uiLocation;
        }
    }

    public static class ServerConfig {
        private final int maxBiomeDistance;
        private final String requiredAdvancement;
        private final float chanceAsLoot;

        public ServerConfig() {
            this.maxBiomeDistance = 6400;
            this.requiredAdvancement = "none";
            this.chanceAsLoot = 0.16f;
        }

        public float getChanceAsLoot() {
            return chanceAsLoot;
        }

        public int getMaxBiomeDistance() {
            return maxBiomeDistance;
        }

        public String getRequiredAdvancement() {
            return requiredAdvancement;
        }
    }
}
