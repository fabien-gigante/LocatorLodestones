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
        if (lastUpdateTime <= player.age && player.age < lastUpdateTime + UPDATE_COOLDOWN) return;
        lastUpdateTime = player.age;
        dirty = false;
        update(client);
    }

    public abstract void update(MinecraftClient client);

    protected static List<ItemStack> getPlayerStacks(PlayerEntity player, Config.HoldingLocation location) {
        List<ItemStack> stacks = new ArrayList<>();

        // Compute stacks based on specified location
        switch(location) {
            case Config.HoldingLocation.NONE:
                return stacks;
            case Config.HoldingLocation.HANDS:
                stacks.add(player.getInventory().getSelectedStack());
                break;
            case Config.HoldingLocation.HOTBAR: 
                for (int slot = 0; slot < PlayerInventory.getHotbarSize(); slot++)
                    stacks.add( player.getInventory().getStack(slot));
                break;
            case Config.HoldingLocation.INVENTORY:
            default:
                stacks.addAll(player.getInventory().getMainStacks());
        }
        stacks.add(player.getOffHandStack());

        // If configured (and location not NONE), include contents of bundles
        if (ConfigManager.getConfig().holdingBundles()) {
            ListIterator<ItemStack> it = stacks.listIterator();
            while (it.hasNext()) {
                BundleContentsComponent contentsComponent = it.next().get(DataComponentTypes.BUNDLE_CONTENTS);
                if (contentsComponent != null) contentsComponent.stream().forEach(it::add);
            }
        }
        return stacks;
    }    
}