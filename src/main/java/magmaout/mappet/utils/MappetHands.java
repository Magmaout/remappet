package magmaout.mappet.utils;

import com.google.common.base.MoreObjects;
import magmaout.mappet.api.scripts.code.entities.ScriptPlayer;
import magmaout.mappet.capabilities.hand.Hand;
import magmaout.mappet.network.Dispatcher;
import magmaout.mappet.network.scripts.PacketCapability;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.render.RenderCustomModel;
import mchorse.blockbuster.core.transformers.RenderEntityItemTransformer;
import mchorse.metamorph.api.models.IMorphProvider;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MappetHands {
    private static Minecraft mc = Minecraft.getMinecraft();
    private static RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
    private static ItemRenderer itemRenderer = Minecraft.getMinecraft().getItemRenderer();
    private static ItemStack itemStackMainHand = ItemStack.EMPTY;
    private static ItemStack itemStackOffHand = ItemStack.EMPTY;
    private static ResourceLocation skinPath;
    private static float equippedProgressMainHand;
    private static float prevEquippedProgressMainHand;
    private static float equippedProgressOffHand;
    private static float prevEquippedProgressOffHand;

    public static void renderArms() {
        if (mc.gameSettings.thirdPersonView != 0 || mc.player.isPlayerSleeping() || mc.gameSettings.hideGUI || mc.playerController.isSpectator()) return;

        EntityRenderer entityRenderer = mc.entityRenderer;
        AbstractClientPlayer player = mc.player;
        float partialTicks = mc.getRenderPartialTicks();
        float f = player.getSwingProgress(partialTicks);
        EnumHand enumhand = MoreObjects.firstNonNull(player.swingingHand, EnumHand.MAIN_HAND);
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;

        rotateArroundXAndY(f1, f2);
        setLightmap();
        rotateArm(partialTicks);
        entityRenderer.enableLightmap();
        GlStateManager.enableRescaleNormal();

        float f3 = enumhand == EnumHand.MAIN_HAND ? f : 0.0F;
        float f5 = 1.0F - (prevEquippedProgressMainHand + (equippedProgressMainHand - prevEquippedProgressMainHand) * partialTicks);
        if (!net.minecraftforge.client.ForgeHooksClient.renderSpecificFirstPersonHand(EnumHand.MAIN_HAND, partialTicks, f1, f3, f5, itemStackMainHand))
            renderItemInFirstPerson(player, partialTicks, f1, EnumHand.MAIN_HAND, f3, itemStackMainHand, f5);

        float f4 = enumhand == EnumHand.OFF_HAND ? f : 0.0F;
        float f6 = 1.0F - (prevEquippedProgressOffHand + (equippedProgressOffHand - prevEquippedProgressOffHand) * partialTicks);
        if (!net.minecraftforge.client.ForgeHooksClient.renderSpecificFirstPersonHand(EnumHand.OFF_HAND, partialTicks, f1, f4, f6, itemStackOffHand))
            renderItemInFirstPerson(player, partialTicks, f1, EnumHand.OFF_HAND, f4, itemStackOffHand, f6);

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        entityRenderer.disableLightmap();
    }

    public static void renderItemInFirstPerson(AbstractClientPlayer player, float partialTicks, float deltaPitch, EnumHand side, float swingProgress, ItemStack stack, float equipedProgress) {
        EnumHandSide enumhandside = side == EnumHand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        Hand hand = Hand.get(player);
        GlStateManager.pushMatrix();
        float delta = player.getPrimaryHand() == EnumHandSide.RIGHT ? 1 : -1;

        if (side == EnumHand.MAIN_HAND) {
            GlStateManager.rotate((float) hand.mainRotations.x, 1, 0, 0);
            GlStateManager.rotate((float) hand.mainRotations.y * delta, 0, 1, 0);
            GlStateManager.rotate((float) hand.mainRotations.z * delta, 0, 0, 1);
            GlStateManager.translate(hand.mainPosition.x * delta, hand.mainPosition.y, hand.mainPosition.z);
        } else {
            GlStateManager.rotate((float) hand.offRotations.x, 1, 0, 0);
            GlStateManager.rotate((float) hand.offRotations.y * delta, 0, 1, 0);
            GlStateManager.rotate((float) hand.offRotations.z * delta, 0, 0, 1);
            GlStateManager.translate(hand.offPosition.x * delta, hand.offPosition.y, hand.offPosition.z);
        }

        if (stack.isEmpty()) {
            if (!player.isInvisible() && (side == EnumHand.MAIN_HAND ? hand.mainRender : hand.offRender))
                renderArmFirstPerson(equipedProgress, swingProgress, enumhandside, hand);
        } else {
            boolean flag1 = enumhandside == EnumHandSide.RIGHT;

            if (player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == side) {
                int j = flag1 ? 1 : -1;
                switch (stack.getItemUseAction()) {
                    case NONE:
                        transformSideFirstPerson(enumhandside, equipedProgress);
                        break;
                    case EAT:
                    case DRINK:
                        transformEatFirstPerson(partialTicks, enumhandside, stack);
                        transformSideFirstPerson(enumhandside, equipedProgress);
                        break;
                    case BLOCK:
                        transformSideFirstPerson(enumhandside, equipedProgress);
                        break;
                    case BOW:
                        transformSideFirstPerson(enumhandside, equipedProgress);
                        GlStateManager.translate((float)j * -0.2785682F, 0.18344387F, 0.15731531F);
                        GlStateManager.rotate(-13.935F, 1.0F, 0.0F, 0.0F);
                        GlStateManager.rotate((float)j * 35.3F, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate((float)j * -9.785F, 0.0F, 0.0F, 1.0F);
                        float f5 = (float)stack.getMaxItemUseDuration() - ((float) mc.player.getItemInUseCount() - partialTicks + 1.0F);
                        float f6 = f5 / 20.0F;
                        f6 = (f6 * f6 + f6 * 2.0F) / 3.0F;
                        f6 = f6 > 1.0F ? 1.0F : f6;
                        if (f6 > 0.1F) {
                            float f7 = MathHelper.sin((f5 - 0.1F) * 1.3F);
                            float f3 = f6 - 0.1F;
                            float f4 = f7 * f3;
                            GlStateManager.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
                        }
                        GlStateManager.translate(f6 * 0.0F, f6 * 0.0F, f6 * 0.04F);
                        GlStateManager.scale(1.0F, 1.0F, 1.0F + f6 * 0.2F);
                        GlStateManager.rotate((float)j * 45.0F, 0.0F, -1.0F, 0.0F);
                }
            } else {
                float f = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
                float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2F));
                float f2 = -0.2F * MathHelper.sin(swingProgress * (float)Math.PI);
                int i = flag1 ? 1 : -1;
                GlStateManager.translate((float)i * f, f1, f2);
                transformSideFirstPerson(enumhandside, equipedProgress);
                transformFirstPerson(enumhandside, swingProgress);
            }
            itemRenderer.renderItemSide(player, stack, flag1 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
        }
        GlStateManager.popMatrix();
    }

    private static void renderArmFirstPerson(float equipedProgress, float swingProgress, EnumHandSide side, Hand hand) {
        AbstractClientPlayer player = mc.player;
        boolean flag = side != EnumHandSide.LEFT;
        float f = flag ? 1.0F : -1.0F;
        float f1 = MathHelper.sqrt(swingProgress);
        float f2 = -0.3F * MathHelper.sin(f1 * (float)Math.PI);
        float f3 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
        float f4 = -0.4F * MathHelper.sin(swingProgress * (float)Math.PI);
        GlStateManager.translate(f * (f2 + 0.64000005F), f3 + -0.6F + equipedProgress * -0.6F, f4 + -0.71999997F);
        GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);
        float f5 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f6 = MathHelper.sin(f1 * (float)Math.PI);
        GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(f * -1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);

        RenderPlayer renderPlayer = new RenderPlayer(renderManager, hand.skinType.isEmpty() ? false : (hand.skinType.contains("slim") ? true : false));
        mc.getTextureManager().bindTexture(hand.skinPath.isEmpty() ? player.getLocationSkin() : new ResourceLocation(hand.skinPath));
        GlStateManager.disableCull();
        if (flag) {
            renderPlayer.renderRightArm(player);
        } else {
            renderPlayer.renderLeftArm(player);
        }
        GlStateManager.enableCull();
    }

    private static void rotateArroundXAndY(float angle, float angleY) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(angleY, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private static void setLightmap() {
        AbstractClientPlayer player = mc.player;
        int i = mc.world.getCombinedLight(new BlockPos(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ), 0);
        float f = (float)(i & 65535);
        float f1 = (float)(i >> 16);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
    }

    private static void rotateArm(float partialTicks) {
        EntityPlayerSP player = mc.player;
        float f = player.prevRenderArmPitch + (player.renderArmPitch - player.prevRenderArmPitch) * partialTicks;
        float f1 = player.prevRenderArmYaw + (player.renderArmYaw - player.prevRenderArmYaw) * partialTicks;
        GlStateManager.rotate((player.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((player.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
    }

    private static void transformEatFirstPerson(float partialTicks, EnumHandSide hand, ItemStack stack) {
        float f = (float) mc.player.getItemInUseCount() - partialTicks + 1.0F;
        float f1 = f / (float)stack.getMaxItemUseDuration();

        if (f1 < 0.8F)
        {
            float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float)Math.PI) * 0.1F);
            GlStateManager.translate(0.0F, f2, 0.0F);
        }

        float f3 = 1.0F - (float)Math.pow((double)f1, 27.0D);
        int i = hand == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate(f3 * 0.6F * (float)i, f3 * -0.5F, f3 * 0.0F);
        GlStateManager.rotate((float)i * f3 * 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((float)i * f3 * 30.0F, 0.0F, 0.0F, 1.0F);
    }

    private static void transformFirstPerson(EnumHandSide hand, float swingProgress) {
        int i = hand == EnumHandSide.RIGHT ? 1 : -1;
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        GlStateManager.rotate((float)i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
        float f1 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
        GlStateManager.rotate((float)i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
    }

    private static void transformSideFirstPerson(EnumHandSide hand, float equipedProgress) {
        int i = hand == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate((float) i * 0.56F, -0.52F + equipedProgress * -0.6F, -0.72F);
    }

    public static void updateEquippedItem() {
        prevEquippedProgressMainHand = equippedProgressMainHand;
        prevEquippedProgressOffHand = equippedProgressOffHand;
        EntityPlayerSP player = mc.player;
        ItemStack itemstack = player.getHeldItemMainhand();
        ItemStack itemstack1 = player.getHeldItemOffhand();

        if (player.isRowingBoat()) {
            equippedProgressMainHand = MathHelper.clamp(equippedProgressMainHand - 0.4F, 0.0F, 1.0F);
            equippedProgressOffHand = MathHelper.clamp(equippedProgressOffHand - 0.4F, 0.0F, 1.0F);
        } else {
            float f = player.getCooledAttackStrength(1.0F);

            boolean requipM = net.minecraftforge.client.ForgeHooksClient.shouldCauseReequipAnimation(itemStackMainHand, itemstack, player.inventory.currentItem);
            boolean requipO = net.minecraftforge.client.ForgeHooksClient.shouldCauseReequipAnimation(itemStackOffHand, itemstack1, -1);

            if (!requipM && !Objects.equals(itemStackMainHand, itemstack))
                itemStackMainHand = itemstack;
            if (!requipM && !Objects.equals(itemStackOffHand, itemstack1))
                itemStackOffHand = itemstack1;

            equippedProgressMainHand += MathHelper.clamp((!requipM ? f * f * f : 0.0F) - equippedProgressMainHand, -0.4F, 0.4F);
            equippedProgressOffHand += MathHelper.clamp((float)(!requipO ? 1 : 0) - equippedProgressOffHand, -0.4F, 0.4F);
        }

        if (equippedProgressMainHand < 0.1F)
            itemStackMainHand = itemstack;
        if (equippedProgressOffHand < 0.1F)
            itemStackOffHand = itemstack1;
    }

    public void resetEquippedProgress(EnumHand hand) {
        if (hand == EnumHand.MAIN_HAND) {
            equippedProgressMainHand = 0.0F;
        } else {
            equippedProgressOffHand = 0.0F;
        }
    }
}
