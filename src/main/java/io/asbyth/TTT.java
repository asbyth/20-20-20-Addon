package io.asbyth;

import cc.hyperium.Hyperium;
import cc.hyperium.event.EventBus;
import cc.hyperium.event.InitializationEvent;
import cc.hyperium.event.InvokeEvent;
import cc.hyperium.event.RenderHUDEvent;
import cc.hyperium.event.TickEvent;
import cc.hyperium.handlers.handlers.keybinds.HyperiumBind;
import cc.hyperium.internal.addons.IAddon;
import cc.hyperium.utils.ChatColor;
import cc.hyperium.utils.Multithreading;
import io.asbyth.command.TTTCommand;
import io.asbyth.config.Config;
import io.asbyth.gui.ConfigGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TTT extends HyperiumBind implements IAddon {

    private int breakTicks = 0;
    private Config config;
    private int ticks = 0;
    private boolean breaking = false;
    private boolean timeForBreak = false;
    private final ResourceLocation texture = new ResourceLocation("textures/20/break.png");
    private int warnedTicks = 0;

    public TTT() {
        super("20 20 20 Addon", Keyboard.KEY_J);
    }

    @InvokeEvent
    public void init(InitializationEvent event) {
        Hyperium.INSTANCE.getHandlers().getKeybindHandler().registerKeyBinding(this);
        Hyperium.INSTANCE.getHandlers().getCommandHandler().registerCommand(new TTTCommand(this));

        if (config == null) {
            config = new Config();
        }

        Hyperium.CONFIG.register(config);
    }

    @Override
    public void onLoad() {
        EventBus.INSTANCE.register(this);
    }

    @InvokeEvent
    public void tick(TickEvent event) {
        if (!config.isEnabled()) {
            return;
        }

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null)
            return;
        timeForBreak = ++ticks >= config.getInterval() * 20 * 60;
        if (timeForBreak) {
            if (warnedTicks % (20 * 30) == 0 && config.isChat()) {
                Hyperium.INSTANCE.getHandlers().getGeneralChatHandler().sendMessage(
                        ChatColor.GRAY + "(" + ChatColor.BLUE + "20 20 20" + ChatColor.GRAY + ") " +
                                ChatColor.GRAY + "Time to take a break. Press " + getKey() + " to start.", false);
                if (config.isPingWhenReady()) {
                    ping();
                }
            }
            warnedTicks++;
        }

        if (breaking) {
            timeForBreak = false;
            breakTicks++;
            if (breakTicks > config.getDuration() * 20) {
                breaking = false;
                if (config.isPingWhenDone()) {
                    ping();
                }
            }
        }
    }

    private void ping() {
        SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
        if (Minecraft.getMinecraft().theWorld != null) {
            Multithreading.runAsync(() -> {
                long[] times = {0, 50, 50, 50, 400, 100, 100};
                for (long time : times) {
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }

                    handler.playSound(PositionedSoundRecord.create(new ResourceLocation("note.pling"), (float) Minecraft.getMinecraft().thePlayer.posX, (float) Minecraft.getMinecraft().thePlayer.posY, (float) Minecraft.getMinecraft().thePlayer.posZ));
                }
            });
        }
    }

    @InvokeEvent
    public void renderTick(RenderHUDEvent event) {
        if (!config.isEnabled()) {
            return;
        }

        if (timeForBreak || Minecraft.getMinecraft().currentScreen instanceof ConfigGui) {
            GlStateManager.pushMatrix();
            ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
            int corner = config.getCorner();
            double width = 64;
            double height = 64;

            GlStateManager.enableTexture2D();
            double v = 4000D;
            long l = System.currentTimeMillis() % (int) v;
            double per = ((double) l / v);

            float animationFactor = (float) ((1F - Math.cos(per * Math.PI * 2)) / 2F);

            width += animationFactor * width * .25D;
            height += animationFactor * height * .25D;

            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

            if (corner == 2) { //Top right
                GlStateManager.translate(resolution.getScaledWidth() - width / resolution.getScaleFactor(), 0, 0);
            } else if (corner == 3) { //bottom left
                GlStateManager.translate(0, resolution.getScaledHeight() - height / resolution.getScaleFactor(), 0);
            } else if (corner == 4) { //bottom right
                GlStateManager.translate(resolution.getScaledWidth() - width / resolution.getScaleFactor(), resolution.getScaledHeight() - height / resolution.getScaleFactor(), 0);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, (float) (.4 + .6 * animationFactor));
            GlStateManager.scale(1.0 + .25D * animationFactor, 1.0 + .25D * animationFactor, 0);
            Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, 128, 128, 16, 16, 128, 128);
            GlStateManager.popMatrix();
        } else if (breaking) {
            int totalTime = 20 * config.getDuration();
            double percent = (double) breakTicks / (double) totalTime;
            ScaledResolution current = new ScaledResolution(Minecraft.getMinecraft());
            float radius = current.getScaledHeight() * 2 / 5;
            int centerY = current.getScaledHeight() / 2;
            int centerX = current.getScaledWidth() / 2;

            GlStateManager.pushMatrix();
            GL11.glEnable(3042);
            GL11.glDisable(3553);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2848);
            GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
            GL11.glBegin(6);
            GlStateManager.resetColor();
            GL11.glVertex3d(centerX, centerY, 0);

            float startTheta = 0;
            float endTheta = (float) (Math.PI * 2);
            float diff = endTheta - startTheta;

            int i = 150;
            for (float j = 0; j <= i; j++) {
                Color tmp = new Color(97, 132, 249, percent > j / (float) i ? 50 : 255);
                GlStateManager.color(tmp.getRed() / 255F, tmp.getGreen() / 255F, tmp.getBlue() / 255F, tmp.getAlpha() / 255F);
                float x = centerX + radius * MathHelper.sin(startTheta + (diff * j / ((float) i)));
                float y = centerY + radius * MathHelper.cos(startTheta + (diff * j / ((float) i)));
                GL11.glVertex2f(x, y);
            }

            GL11.glEnd();
            GL11.glEnable(3553);
            GL11.glDisable(3042);
            GL11.glDisable(2848);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            ConfigGui.drawScaledText(String.valueOf((config.getDuration() * 20 - breakTicks) / 20), centerX, centerY - 10, 2.0, Color.YELLOW.getRGB(), true, true);
            ConfigGui.drawScaledText("Press " + getKey() + " to cancel.", current.getScaledWidth() / 2, 5, 2, Color.WHITE.getRGB(), true, true);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void onClose() {
        Hyperium.CONFIG.save();
    }

    @Override
    public void sendDebugInfo() {
    }

    private String getKey() {
        return Keyboard.getKeyName(Hyperium.INSTANCE.getHandlers().getKeybindHandler().getBinding("20 20 20 Addon").getKeyCode());
    }

    @Override
    public void onPress() {
        if (breaking) {
            breaking = false;
        } else {
            ticks = 0;
            breaking = true;
            breakTicks = 0;
            warnedTicks = 0;
        }
    }

    public Config getConfig() {
        return config;
    }
}
