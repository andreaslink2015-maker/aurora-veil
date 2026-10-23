package dev.auroraveil;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

/** Spawns the particle effects around the local player. Called once per client tick. */
public final class AuroraEffects {
	private static int ticks;

	private AuroraEffects() {
	}

	public static void tick(Minecraft client, AuroraMode mode) {
		LocalPlayer player = client.player;
		ClientLevel level = client.level;
		if (player == null || level == null || client.isPaused() || mode == AuroraMode.OFF) {
			return;
		}
		ticks++;

		if (mode.atLeast(AuroraMode.TRAIL)) {
			spawnRibbon(level, player);
		}
		if (mode.atLeast(AuroraMode.AURA)) {
			spawnSpiral(level, player);
			spawnCrown(level, player);
		}
	}

	/** A shimmering ribbon that is denser the faster you move (elytra flight looks great). */
	private static void spawnRibbon(ClientLevel level, LocalPlayer player) {
		ThreadLocalRandom rng = ThreadLocalRandom.current();
		Vec3 motion = player.getDeltaMovement();
		double speed = Math.sqrt(motion.x * motion.x + motion.y * motion.y + motion.z * motion.z);

		int count = (int) Math.min(7, speed * 14.0);
		if (count == 0 && ticks % 4 == 0) {
			count = 1; // idle shimmer
		}

		for (int i = 0; i < count; i++) {
			// Colour drifts along the ribbon: green -> cyan -> violet -> pink -> repeat
			int colour = ticks / 4 + i;
			double x = player.getX() + (rng.nextDouble() - 0.5) * 0.5;
			double y = player.getY() + 0.1 + rng.nextDouble() * 1.0;
			double z = player.getZ() + (rng.nextDouble() - 0.5) * 0.5;
			level.addParticle(
					AuroraParticles.color(colour),
					x, y, z,
					(rng.nextDouble() - 0.5) * 0.02,
					0.01 + rng.nextDouble() * 0.03,
					(rng.nextDouble() - 0.5) * 0.02);
		}
	}

	/** Two particle streams winding up around your body like a double helix. */
	private static void spawnSpiral(ClientLevel level, LocalPlayer player) {
		double t = ticks * 0.28;
		double rise = (ticks % 24) / 24.0 * 2.0;
		double radius = 0.75 + 0.1 * Math.sin(t * 0.5);

		for (int strand = 0; strand < 2; strand++) {
			double angle = t + strand * Math.PI;
			level.addParticle(
					AuroraParticles.color(strand * 2 + ticks / 30),
					player.getX() + Math.cos(angle) * radius,
					player.getY() + rise,
					player.getZ() + Math.sin(angle) * radius,
					0.0, 0.015, 0.0);
		}
	}

	/** A small ring of light that pulses above the head every so often. */
	private static void spawnCrown(ClientLevel level, LocalPlayer player) {
		if (ticks % 8 != 0) {
			return;
		}
		int points = 10;
		double offset = ticks * 0.05;
		for (int i = 0; i < points; i++) {
			double angle = offset + i * (Math.PI * 2.0 / points);
			level.addParticle(
					AuroraParticles.color(i),
					player.getX() + Math.cos(angle) * 0.45,
					player.getY() + player.getBbHeight() + 0.35,
					player.getZ() + Math.sin(angle) * 0.45,
					0.0, 0.004, 0.0);
		}
	}
}
