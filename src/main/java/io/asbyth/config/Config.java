package io.asbyth.config;

import cc.hyperium.config.ConfigOpt;

public class Config {

    @ConfigOpt
    private int interval = 20;
    @ConfigOpt
    private int duration = 20;
    @ConfigOpt
    private int corner = 1;
    @ConfigOpt
    private boolean enabled = true;
    @ConfigOpt
    private boolean chat = true;
    @ConfigOpt
    private boolean pingWhenDone = true;
    @ConfigOpt
    private boolean pingWhenReady = true;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCorner() {
        return corner;
    }

    public void setCorner(int corner) {
        this.corner = corner;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isChat() {
        return chat;
    }

    public void setChat(boolean chat) {
        this.chat = chat;
    }

    public boolean isPingWhenDone() {
        return pingWhenDone;
    }

    public void setPingWhenDone(boolean pingWhenDone) {
        this.pingWhenDone = pingWhenDone;
    }

    public boolean isPingWhenReady() {
        return pingWhenReady;
    }

    public void setPingWhenReady(boolean pingWhenReady) {
        this.pingWhenReady = pingWhenReady;
    }
}
