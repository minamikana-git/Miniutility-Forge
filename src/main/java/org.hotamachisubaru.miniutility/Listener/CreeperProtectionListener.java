package org.hotamachisubaru.miniutility.Listener;

import net.minecraft.world.entity.monster.Creeper;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.hotamachisubaru.miniutility.Miniutility;

@Mod.EventBusSubscriber
public class CreeperProtectionListener {
    private final Miniutility mod;
    private boolean creeperProtectionEnabled = true;

    public CreeperProtectionListener(Miniutility mod) {
        this.mod = mod;
    }

    @SubscribeEvent
    public void onCreeperExplode(ExplosionEvent.Detonate event) {
        if (!creeperProtectionEnabled) return;
        if (event.getExplosion().getSourceMob() instanceof Creeper) {
            event.getExplosion().clearToBlow();
        }
    }

    public boolean toggleCreeperProtection() {
        creeperProtectionEnabled = !creeperProtectionEnabled;
        return creeperProtectionEnabled;
    }

    public boolean isCreeperProtectionEnabled() {
        return creeperProtectionEnabled;
    }
}
