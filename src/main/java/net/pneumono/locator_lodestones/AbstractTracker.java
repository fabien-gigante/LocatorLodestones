package net.pneumono.locator_lodestones;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.pneumono.locator_lodestones.config.Config;
import net.pneumono.locator_lodestones.config.ConfigManager;

public abstract class AbstractTracker {
    protected static int UPDATE_COOLDOWN = 10;
    private boolean dirty = false;
    private long lastUpdateTime = 0;

    public void init() {}

    public void markDirty() {
        dirty = true;
    }

    public void reset() {
        lastUpdateTime = 0; 
        markDirty();
    }
    
    public void tick(MinecraftClient client) { 
        ClientPlayerEntity player = client.player;
        if (!dirty || player == null || !player.isLoaded()) return;
        if (lastUpdateTime + UPDATE_COOLDOWN > player.age && lastUpdateTime < player.age) return;
        lastUpdateTime = player.age;
        dirty = false;
        update(client);
    }

    public abstract void update(MinecraftClient client);

    protected static List<ItemStack> getPlayerStacks(PlayerEntity player, Config.HoldingLocation location) {
        List<ItemStack> stacks = new ArrayList<>();
        switch(location) {
            case Config.HoldingLocation.HANDS:
                ItemStack selectedStack = player.getInventory().getSelectedStack();
                if (selectedStack != null) stacks.add(selectedStack);
                break;
            case Config.HoldingLocation.HOTBAR: 
                for (int slot = 0; slot < PlayerInventory.getHotbarSize(); slot++) {
                    ItemStack stack = player.getInventory().getStack(slot);
                    if (stack != null) stacks.add(stack);
                }
                break;
            default:
                DefaultedList<ItemStack> mainStacks = player.getInventory().getMainStacks();
                if (mainStacks != null) stacks.addAll(mainStacks);
        }
        ItemStack offHandStack = player.getOffHandStack();
        if (offHandStack != null) stacks.add(offHandStack);

        if (ConfigManager.getConfig().showBundled()) {
            ListIterator<ItemStack> it = stacks.listIterator();
            while (it.hasNext()) {
                BundleContentsComponent contentsComponent = it.next().get(DataComponentTypes.BUNDLE_CONTENTS);
                if (contentsComponent != null) contentsComponent.stream().forEach(stack -> it.add(stack));
            }
        }
        return stacks;
    }    
}