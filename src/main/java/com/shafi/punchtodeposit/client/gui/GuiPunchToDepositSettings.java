package com.shafi.punchtodeposit.client.gui;

import com.shafi.punchtodeposit.PunchToDepositConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

public class GuiPunchToDepositSettings extends GuiScreen {
    private final GuiScreen parentScreen;
    private GuiButton masterToggleButton;
    private GuiButton hoverToggleButton;
    private GuiButton doneButton;

    public GuiPunchToDepositSettings() {
        this(null);
    }

    public GuiPunchToDepositSettings(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.buttonList.clear();
        this.masterToggleButton = new GuiButton(1, centerX - 100, centerY - 30, 200, 20, "");
        this.hoverToggleButton = new GuiButton(2, centerX - 100, centerY - 4, 200, 20, "");
        this.doneButton = new GuiButton(3, centerX - 100, centerY + 30, 200, 20, I18n.format("gui.done"));

        this.buttonList.add(this.masterToggleButton);
        this.buttonList.add(this.hoverToggleButton);
        this.buttonList.add(this.doneButton);
        refreshLabels();
    }

    private void refreshLabels() {
        this.masterToggleButton.displayString = "Mod: " + (PunchToDepositConfig.enabled ? "On" : "Off");
        this.hoverToggleButton.displayString = "Hover text: " + (PunchToDepositConfig.hoverLabelEnabled ? "On" : "Off");
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            PunchToDepositConfig.setEnabled(!PunchToDepositConfig.enabled);
            refreshLabels();
            return;
        }

        if (button.id == 2) {
            PunchToDepositConfig.setHoverLabelEnabled(!PunchToDepositConfig.hoverLabelEnabled);
            refreshLabels();
            return;
        }

        if (button.id == 3) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(this.fontRendererObj, "Punch To Deposit", this.width / 2, this.height / 2 - 70, 16777215);
        drawCenteredString(this.fontRendererObj, "Settings", this.width / 2, this.height / 2 - 56, 10526880);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
