package com.fuyuaki.r_wilderness.mixin.client;

import com.fuyuaki.r_wilderness.world.item.RCreativeModeTabs;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.client.gui.CreativeTabsScreenPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {

    @Shadow private CreativeTabsScreenPage currentPage;

    public CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Shadow protected abstract int getTabX(CreativeModeTab tab);

    @Shadow protected abstract void selectTab(CreativeModeTab tab);

    @Shadow private static CreativeModeTab selectedTab;

    @Inject(method = "renderTabButton", at = @At("HEAD"), cancellable = true)
    private void renderTabButtonWilderness(GuiGraphics guiGraphics, int p_470648_, int p_470660_, CreativeModeTab creativeModeTab, CallbackInfo ci){
        if (creativeModeTab == RCreativeModeTabs.MAIN_TAB.get()){
            boolean flag = creativeModeTab == selectedTab;
            boolean flag1 = currentPage.isTop(creativeModeTab);
            int i = currentPage.getColumn(creativeModeTab);
            int j = this.leftPos + this.getTabX(creativeModeTab);
            int k = this.topPos - (flag1 ? 28 : -(this.imageHeight - 4));
            Identifier location = creativeModeTab.getTabsImage();
            if (!flag && p_470648_ > j && p_470660_ > k && p_470648_ < j + 26 && p_470660_ < k + 32) {
                guiGraphics.requestCursor(CursorTypes.POINTING_HAND);
            }
            //PATCH 1.20.2: Deal with custom tab backgrounds, and deal with transparency.
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, location, j, k, 26, 32);
            int l = j + 13 - 8;
            int i1 = k + 16 - 8 + (flag1 ? 1 : -1);
            guiGraphics.renderItem(creativeModeTab.getIconItem(), l, i1);
            ci.cancel();
        }
    }
}
