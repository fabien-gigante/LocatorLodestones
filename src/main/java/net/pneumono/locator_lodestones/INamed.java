package net.pneumono.locator_lodestones;

import java.util.Optional;
import net.minecraft.text.Text;

public interface INamed {
    Optional<Text> getName();
    void setName(Optional<Text> name);
}
