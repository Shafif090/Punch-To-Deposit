package com.shafi.punchtodeposit;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
    modid = PunchToDepositMod.MODID,
    name = PunchToDepositMod.NAME,
    version = PunchToDepositMod.VERSION,
    acceptableRemoteVersions = "*",
    guiFactory = "com.shafi.punchtodeposit.client.PunchToDepositGuiFactory"
)
public class PunchToDepositMod {
    public static final String MODID = "punch_to_deposit";
    public static final String NAME = "Punch To Deposit";
    public static final String VERSION = "1.0.0";

    @SidedProxy(
        clientSide = "com.shafi.punchtodeposit.client.ClientProxy",
        serverSide = "com.shafi.punchtodeposit.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PunchToDepositConfig.load(event);
        proxy.register();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
    }
}
