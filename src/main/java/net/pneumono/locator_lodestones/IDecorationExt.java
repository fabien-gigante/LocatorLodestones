package net.pneumono.locator_lodestones;

import java.util.Optional;

import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public interface IDecorationExt {
    Optional<Text> getName();
    void setName(Optional<Text> name);
    Optional<RegistryKey<World>> getDimension();
    void setDimension(Optional<RegistryKey<World>> dimension);
}
