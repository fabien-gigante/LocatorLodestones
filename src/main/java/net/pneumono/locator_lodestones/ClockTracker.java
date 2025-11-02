package net.pneumono.locator_lodestones;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.pneumono.locator_lodestones.config.ConfigManager;

public class ClockTracker extends AbstractTracker {
    private boolean hasClock = false;
    private boolean isNight = false;

    @Override
    public void reset() {
        super.reset();
        hasClock = false;   
    }

    @Override
    public void tick(MinecraftClient client) {
        super.tick(client);
        if (client.world == null || client.player == null) return;
        boolean isNight = client.world.isNight();
        if (hasClock && isNight && !this.isNight) {
            LocatorLodestones.LOGGER.info("It's night time!");
            client.player.playSound(ConfigManager.getConfig().clockSound());
        }
        this.isNight = isNight;
    }

    @Override
    public void update(MinecraftClient client) {
        List<ItemStack> stacks = getPlayerStacks(client.player, ConfigManager.getConfig().clockLocation());
        hasClock = stacks.stream().anyMatch(stack -> stack.isOf(Items.CLOCK));
    }
}