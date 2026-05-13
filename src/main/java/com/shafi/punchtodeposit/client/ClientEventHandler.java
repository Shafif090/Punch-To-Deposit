package com.shafi.punchtodeposit.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShears;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import com.shafi.punchtodeposit.PunchToDepositConfig;

public class ClientEventHandler {
    private static final int LEFT_MOUSE_BUTTON = 0;
    private static final int SHIFT_CLICK_MODE = 1;
    private static final int DEPOSIT_TIMEOUT_TICKS = 10;
    private static final int LABEL_RANGE_BLOCKS = 14;

    private PendingDeposit pendingDeposit;
    private int pendingTicks;

    @SubscribeEvent
    public void onMouse(MouseEvent event) {
        if (!PunchToDepositConfig.enabled) {
            return;
        }

        if (event.button != LEFT_MOUSE_BUTTON || !event.buttonstate) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null || minecraft.theWorld == null || minecraft.currentScreen != null) {
            return;
        }

        MovingObjectPosition hit = minecraft.objectMouseOver;
        if (hit == null || hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return;
        }

        BlockPos blockPos = hit.getBlockPos();
        Block block = minecraft.theWorld.getBlockState(blockPos).getBlock();
        if (!isDepositChest(block)) {
            return;
        }

        ItemStack heldStack = minecraft.thePlayer.inventory.getCurrentItem();
        if (!shouldDeposit(heldStack)) {
            return;
        }

        if (!isWithinReach(minecraft, hit)) {
            return;
        }

        event.setCanceled(true);
        pendingDeposit = new PendingDeposit(minecraft.thePlayer.inventory.currentItem);
        pendingTicks = 0;
        ItemStack stackInHand = minecraft.thePlayer.inventory.getCurrentItem();
        minecraft.playerController.onPlayerRightClick(
            minecraft.thePlayer,
            minecraft.theWorld,
            stackInHand,
            blockPos,
            (EnumFacing) hit.sideHit,
            hit.hitVec
        );
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!PunchToDepositConfig.enabled) {
            clearPending();
            return;
        }

        if (event.phase != TickEvent.Phase.END || pendingDeposit == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null || minecraft.theWorld == null) {
            clearPending();
            return;
        }

        if (minecraft.currentScreen instanceof GuiChest) {
            depositIntoOpenChest(minecraft);
            clearPending();
            return;
        }

        pendingTicks++;
        if (pendingTicks > DEPOSIT_TIMEOUT_TICKS) {
            clearPending();
        }
    }

    private void depositIntoOpenChest(Minecraft minecraft) {
        if (!(minecraft.currentScreen instanceof GuiChest) || minecraft.thePlayer == null) {
            return;
        }

        ItemStack heldStack = minecraft.thePlayer.inventory.getCurrentItem();
        if (!shouldDeposit(heldStack)) {
            return;
        }

        GuiChest guiChest = (GuiChest) minecraft.currentScreen;
        Container container = guiChest.inventorySlots;
        int hotbarSlot = findHotbarSlot(container, minecraft.thePlayer.inventory, pendingDeposit.hotbarIndex);
        if (hotbarSlot >= 0) {
            PlayerControllerMP controller = minecraft.playerController;
            controller.windowClick(container.windowId, hotbarSlot, 0, SHIFT_CLICK_MODE, minecraft.thePlayer);
        }

        minecraft.thePlayer.closeScreen();
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!PunchToDepositConfig.enabled || !PunchToDepositConfig.hoverLabelEnabled) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null || minecraft.theWorld == null) {
            return;
        }

        Vec3 playerEyes = minecraft.thePlayer.getPositionEyes(event.partialTicks);
        int playerX = net.minecraft.util.MathHelper.floor_double(playerEyes.xCoord);
        int playerY = net.minecraft.util.MathHelper.floor_double(playerEyes.yCoord);
        int playerZ = net.minecraft.util.MathHelper.floor_double(playerEyes.zCoord);

        int searchRadius = LABEL_RANGE_BLOCKS;
        for (int x = playerX - searchRadius; x <= playerX + searchRadius; x++) {
            for (int y = playerY - searchRadius; y <= playerY + searchRadius; y++) {
                for (int z = playerZ - searchRadius; z <= playerZ + searchRadius; z++) {
                    BlockPos blockPos = new BlockPos(x, y, z);
                    Block block = minecraft.theWorld.getBlockState(blockPos).getBlock();
                    if (!isDepositChest(block)) {
                        continue;
                    }

                    Vec3 chestCenter = new Vec3(blockPos.getX() + 0.5D, blockPos.getY() + 1.1D, blockPos.getZ() + 0.5D);
                    if (playerEyes.distanceTo(chestCenter) > searchRadius) {
                        continue;
                    }

                    renderFloatingLabel(minecraft, chestCenter.xCoord, chestCenter.yCoord, chestCenter.zCoord, event.partialTicks);
                }
            }
        }
    }

    private void renderFloatingLabel(Minecraft minecraft, double x, double y, double z, float partialTicks) {
        double camX = minecraft.getRenderManager().viewerPosX;
        double camY = minecraft.getRenderManager().viewerPosY;
        double camZ = minecraft.getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) (x - camX), (float) (y - camY), (float) (z - camZ));
        GlStateManager.rotate(-minecraft.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(minecraft.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        RenderHelper.disableStandardItemLighting();

        String textTop = "PUNCH TO";
        String textBottom = "DEPOSIT";
        int topWidth = minecraft.fontRendererObj.getStringWidth(textTop);
        int bottomWidth = minecraft.fontRendererObj.getStringWidth(textBottom);
        int baseY = -4;

        minecraft.fontRendererObj.drawStringWithShadow(textTop, -topWidth / 2, baseY, 0xFFFFFF);
        minecraft.fontRendererObj.drawStringWithShadow(textBottom, -bottomWidth / 2, baseY + 9, 0xFFFFFF);

        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private int findHotbarSlot(Container container, InventoryPlayer inventoryPlayer, int hotbarIndex) {
        for (Object slotObject : container.inventorySlots) {
            Slot slot = (Slot) slotObject;
            if (slot.inventory == inventoryPlayer && slot.getSlotIndex() == hotbarIndex) {
                return slot.slotNumber;
            }
        }

        return -1;
    }

    private boolean isDepositChest(Block block) {
        return block instanceof BlockChest || block instanceof BlockEnderChest;
    }

    private boolean isWithinReach(Minecraft minecraft, MovingObjectPosition hit) {
        double reachDistance = minecraft.playerController.getBlockReachDistance();
        Vec3 eyes = minecraft.thePlayer.getPositionEyes(1.0F);
        return hit.hitVec != null && eyes.distanceTo(hit.hitVec) <= reachDistance + 0.25D;
    }

    private boolean shouldDeposit(ItemStack stack) {
        if (stack == null) {
            return false;
        }

        Item item = stack.getItem();
        if (item instanceof ItemTool) {
            return false;
        }
        if (item instanceof ItemSword) {
            return false;
        }
        if (item instanceof ItemBow) {
            return false;
        }
        if (item instanceof ItemShears) {
            return false;
        }

        return true;
    }

    private void clearPending() {
        pendingDeposit = null;
        pendingTicks = 0;
    }

    private static final class PendingDeposit {
        private final int hotbarIndex;

        private PendingDeposit(int hotbarIndex) {
            this.hotbarIndex = hotbarIndex;
        }
    }
}
