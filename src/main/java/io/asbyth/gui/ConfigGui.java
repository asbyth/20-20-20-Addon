package io.asbyth.gui;

import cc.hyperium.Hyperium;
import cc.hyperium.event.EventBus;
import cc.hyperium.event.InvokeEvent;
import cc.hyperium.event.TickEvent;
import cc.hyperium.utils.ChatColor;
import io.asbyth.TTT;
import io.asbyth.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ConfigGui extends GuiScreen {

    private List<GuiButton> sliders = new ArrayList<>();
    private HashMap<GuiButton, Consumer<GuiButton>> clicks = new HashMap<>();
    private HashMap<GuiButton, Consumer<GuiButton>> ticks = new HashMap<>();

    private HashMap<Integer, Runnable> ids = new HashMap<>();
    private TTT mod;

    public ConfigGui(TTT mod) {
        this.mod = mod;
    }

    public static void drawScaledText(String text, int trueX, int trueY, double scaleFac, int color, boolean shadow, boolean centered) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scaleFac, scaleFac, scaleFac);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, (float) (((double) trueX) / scaleFac) - (centered ? Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) / 2 : 0), (float) (((double) trueY) / scaleFac), color, shadow);
        GlStateManager.scale(1 / scaleFac, 1 / scaleFac, 1 / scaleFac);
        GlStateManager.popMatrix();
    }

    public void show() {
        EventBus.INSTANCE.register(this);
    }

    @InvokeEvent
    public void tick(TickEvent event) {
        EventBus.INSTANCE.unregister(this);
        Minecraft.getMinecraft().displayGuiScreen(this);
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        super.confirmClicked(result, id);
        if (result) {
            Runnable runnable = ids.get(id);
            if (runnable != null) {
                runnable.run();
            }
        }

        Minecraft.getMinecraft().displayGuiScreen(this);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        Consumer<GuiButton> guiButtonConsumer = clicks.get(button);
        if (guiButtonConsumer != null) {
            guiButtonConsumer.accept(button);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        Config config = mod.getConfig();

        reg(new GuiButton(1, width / 2 - 100, 70, "Mod Status"), guiButton -> config.setEnabled(!config.isEnabled()), button -> button.displayString = ChatColor.YELLOW + "Mod Status: " + (config.isEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));

        regSlider(new GuiSlider(2, width / 4 - 100, 120, 200, 20, "", " Minutes", 1, 60, config.getInterval(), false, true), guiButton -> {
        }, guiButton -> config.setInterval(((GuiSlider) guiButton).getValueInt()));

        regSlider(new GuiSlider(3, width / 4 - 100, 170, 200, 20, "", " Seconds", 1, 60, config.getDuration(), false, true), guiButton -> {
        }, guiButton -> config.setDuration(((GuiSlider) guiButton).getValueInt()));

        reg(new GuiButton(4, width / 4 - 100, 220, "Chat Alerts"), guiButton -> config.setChat(!config.isChat()), button -> button.displayString = ChatColor.YELLOW + "Chat Alerts: " + (config.isChat() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));

        reg(new GuiButton(5, width / 4 * 3 - 100, 120, "Notification Corner"), guiButton -> config.setCorner(config.getCorner() < 4 ? config.getCorner() + 1 : 1), button -> button.displayString = ChatColor.YELLOW + "Notification Corner: " + (ChatColor.GREEN + getCornerName(config.getCorner())));

        reg(new GuiButton(6, width / 4 * 3 - 100, 170, "Ping When Ready"), guiButton -> config.setPingWhenReady(!config.isPingWhenReady()), button -> button.displayString = ChatColor.YELLOW + "Ping When Ready: " + (config.isPingWhenReady() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
        reg(new GuiButton(7, width / 4 * 3 - 100, 220, "Ping When Done"), guiButton -> config.setPingWhenDone(!config.isPingWhenDone()), button -> button.displayString = ChatColor.YELLOW + "Ping When Done: " + (config.isPingWhenDone() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, width, height, new Color(0, 0, 0, 100).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);

        drawScaledText(ChatColor.YELLOW + ChatColor.BOLD.toString() + ChatColor.UNDERLINE + "20 20 20", width / 2, 3, 3.0, Color.WHITE.getRGB(), true, true);
        drawScaledText(ChatColor.AQUA + "By Sk1er LLC", width / 2, 35, 2.0, Color.WHITE.getRGB(), true, true);
        drawScaledText("Notification Interval", width / 4, 100, 2.0, Color.WHITE.getRGB(), true, true);
        drawScaledText("Break Duration", width / 4, 150, 2.0, Color.WHITE.getRGB(), true, true);
        drawScaledText("Chat Alerts", width / 4, 200, 2.0, Color.WHITE.getRGB(), true, true);

        drawScaledText("Notification Corner", width / 4 * 3, 100, 2.0, Color.WHITE.getRGB(), true, true);
        drawScaledText("Ping When Ready", width / 4 * 3, 150, 2.0, Color.WHITE.getRGB(), true, true);
        drawScaledText("Ping When Done", width / 4 * 3, 200, 2.0, Color.WHITE.getRGB(), true, true);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        for (GuiButton guiButton : ticks.keySet()) {
            ticks.get(guiButton).accept(guiButton);
        }
    }

    private String getCornerName(int corner) {
        switch (corner) {
            case 1:
                return "Top Left";
            case 2:
                return "Top Right";
            case 3:
                return "Bottom Left";
            case 4:
                return "Bottom Right";
            default:
                return "Error";
        }
    }


    private void regSlider(GuiSlider slider, Consumer<GuiButton> buttonConsumer, Consumer<GuiButton> consumer) {
        reg(slider, buttonConsumer, consumer);
        sliders.add(slider);
    }

    private void reg(GuiButton button, Consumer<GuiButton> consumer, Consumer<GuiButton> consumer1) {
        buttonList.removeIf(button1 -> button1.id == button.id);
        clicks.keySet().removeIf(button1 -> button1.id == button.id);

        buttonList.add(button);
        clicks.put(button, consumer);
        ticks.put(button, consumer1);
    }

    @Override
    public void onGuiClosed() {
        Hyperium.CONFIG.save();
    }
}
