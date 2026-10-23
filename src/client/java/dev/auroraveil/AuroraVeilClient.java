package dev.auroraveil;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

public class AuroraVeilClient implements ClientModInitializer {
	/** Current effect level. Starts on FULL so you see everything the first time you load in. */
	public static volatile AuroraMode mode = AuroraMode.FULL;

	private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(AuroraVeil.id("main"));

	private static KeyMapping cycleKey;

	@Override
	public void onInitializeClient() {
		AuroraParticles.register();

		cycleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.aurora_veil.cycle_mode",
				InputConstants.Type.KEYSYM,
				InputConstants.KEY_V,
				CATEGORY));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (cycleKey.consumeClick()) {
				mode = mode.next();
				if (client.player != null) {
					client.player.sendSystemMessage(Component.literal("Aurora Veil: " + mode.label));
				}
			}
			AuroraEffects.tick(client, mode);
		});

		// Draw the aurora curtains just before the chat layer, so chat stays readable on top.
		HudElementRegistry.attachElementBefore(
				VanillaHudElements.CHAT,
				AuroraVeil.id("aurora_curtains"),
				AuroraHud::extract);
	}
}
