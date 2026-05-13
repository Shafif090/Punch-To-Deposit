package com.shafi.punchtodeposit.client;

import com.shafi.punchtodeposit.client.gui.GuiPunchToDepositSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class ClientKeyHandler {
    public static final KeyBinding OPEN_SETTINGS = new KeyBinding(
        "key.punch_to_deposit.settings",
        Keyboard.KEY_P,
        "key.categories.punch_to_deposit"
    );

    private boolean wasOpenPressed;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null) {
            return;
        }

        boolean pressed = OPEN_SETTINGS.isKeyDown();
        if (pressed && !wasOpenPressed) {
            minecraft.displayGuiScreen(new GuiPunchToDepositSettings());
        }
        wasOpenPressed = pressed;
    }
}
