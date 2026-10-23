package dev.auroraveil;

import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Four glow particles, one per aurora colour. They reuse the vanilla end-rod behaviour
 * (soft drift, fade-out), but with our own animated textures.
 */
public final class AuroraParticles {
	public static final String[] NAMES = {"aurora_green", "aurora_cyan", "aurora_violet", "aurora_pink"};
	public static final SimpleParticleType[] TYPES = new SimpleParticleType[NAMES.length];

	private AuroraParticles() {
	}

	public static void register() {
		for (int i = 0; i < NAMES.length; i++) {
			TYPES[i] = FabricParticleTypes.simple();
			Registry.register(BuiltInRegistries.PARTICLE_TYPE, AuroraVeil.id(NAMES[i]), TYPES[i]);
			ParticleProviderRegistry.getInstance().register(TYPES[i], EndRodParticle.Provider::new);
		}
	}

	/** Wraps around, so any int is a valid colour index. */
	public static SimpleParticleType color(int index) {
		return TYPES[Math.floorMod(index, TYPES.length)];
	}
}
