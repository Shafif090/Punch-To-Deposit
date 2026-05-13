package com.shafi.punchtodeposit;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public final class PunchToDepositConfig {
    private static Configuration configuration;
    private static boolean loaded;

    public static boolean enabled = true;
    public static boolean hoverLabelEnabled = true;

    private PunchToDepositConfig() {
    }

    public static void load(FMLPreInitializationEvent event) {
        File configFile = new File(event.getModConfigurationDirectory(), PunchToDepositMod.MODID + ".cfg");
        configuration = new Configuration(configFile);
        loaded = true;
        sync();
    }

    public static void sync() {
        if (configuration == null || !loaded) {
            return;
        }

        enabled = configuration.getBoolean("enabled", "general", true, "Master toggle for Punch To Deposit.");
        hoverLabelEnabled = configuration.getBoolean("hoverLabelEnabled", "general", true, "Shows the PUNCH TO DEPOSIT hover label on chests.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static void setEnabled(boolean value) {
        enabled = value;
        saveValues();
    }

    public static void setHoverLabelEnabled(boolean value) {
        hoverLabelEnabled = value;
        saveValues();
    }

    private static void saveValues() {
        if (configuration == null) {
            return;
        }

        configuration.get("general", "enabled", true).set(enabled);
        configuration.get("general", "hoverLabelEnabled", true).set(hoverLabelEnabled);

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
