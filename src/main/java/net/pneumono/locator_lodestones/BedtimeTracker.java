package net.pneumono.locator_lodestones;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.pneumono.locator_lodestones.config.Config.HoldingLocation;
import net.pneumono.locator_lodestones.config.ConfigManager;

public class BedtimeTracker extends AbstractTracker {
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
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value());
        }
        this.isNight = isNight;
    }

    @Override
    public void update(MinecraftClient client) {
        if (ConfigManager.getConfig().bedtimeClock())
            hasClock = getPlayerStacks(client.player, HoldingLocation.INVENTORY).stream().anyMatch(stack -> stack.isOf(Items.CLOCK));
    }
}