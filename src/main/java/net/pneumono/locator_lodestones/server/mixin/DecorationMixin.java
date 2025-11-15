package net.pneumono.locator_lodestones.server.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.type.MapDecorationsComponent;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.world.World;
import net.pneumono.locator_lodestones.IDecorationExt;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(MapDecorationsComponent.Decoration.class)
public abstract class DecorationMixin implements IDecorationExt {
    @Shadow @Final @Mutable
    public static Codec<MapDecorationsComponent.Decoration> CODEC;

    private Optional<Text> name = Optional.empty();
    private Optional<RegistryKey<World>> dimension = Optional.empty();

    @Override
    public Optional<Text> getName() { return name; }
    @Override
    public void setName(Optional<Text> name) { this.name = name; }
    @Override
    public Optional<RegistryKey<World>> getDimension() { return dimension; }
    @Override
    public void setDimension(Optional<RegistryKey<World>> dimension) { this.dimension = dimension; }

    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                MapDecorationType.CODEC.fieldOf("type").forGetter(d -> d.type()),
                Codec.DOUBLE.fieldOf("x").forGetter(d -> d.x()),                
                Codec.DOUBLE.fieldOf("z").forGetter(d -> d.z()),
                Codec.FLOAT.fieldOf("rotation").forGetter(d -> d.rotation()),
                TextCodecs.CODEC.optionalFieldOf("name").forGetter(d -> ((DecorationMixin)(Object)d).name),
                World.CODEC.optionalFieldOf("dimension").forGetter(d -> ((DecorationMixin)(Object)d).dimension)
            ).apply(instance,
                (type, x, z, rotation, name, dimension) -> {
                    MapDecorationsComponent.Decoration decoration = new MapDecorationsComponent.Decoration(type, x, z, rotation);
                    ((DecorationMixin)(Object)decoration).name = name;
                    ((DecorationMixin)(Object)decoration).dimension = dimension;
                    return decoration;
                })
        );
    }
}