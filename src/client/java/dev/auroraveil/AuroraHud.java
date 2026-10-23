package dev.auroraveil;

import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Animated aurora curtains drawn along the top of the screen.
 *
 * It only uses graphics.fill(), which is the one drawing call the Fabric docs show for 26.x,
 * and fakes the gradients with many thin, translucent strips.
 */
public final class AuroraHud {
	private static final int COLUMN_WIDTH = 3;
	private static final int BANDS = 10;

	/** 0 = hidden, 1 = fully visible; eased so the sky fades in and out smoothly. */
	private static float strength;
	private static long lastMillis;

	private AuroraHud() {
	}

	public static void extract(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		long now = Util.getMillis();
		float dt = lastMillis == 0 ? 0f : Math.min(0.1f, (now - lastMillis) / 1000f);
		lastMillis = now;

		float target = AuroraVeilClient.mode == AuroraMode.FULL ? 1f : 0f;
		strength += (target - strength) * Math.min(1f, dt * 3f);
		if (strength < 0.01f) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		int width = mc.getWindow().getGuiScaledWidth();
		int height = mc.getWindow().getGuiScaledHeight();
		double time = now / 1000.0;

		for (int x = 0; x < width; x += COLUMN_WIDTH) {
			double u = x / (double) width;

			// Three layered sine waves make the curtain's lower edge ripple like real aurora.
			double wave = 0.5
					+ 0.25 * Math.sin(u * 7.0 + time * 0.9)
					+ 0.15 * Math.sin(u * 17.0 - time * 1.7)
					+ 0.10 * Math.sin(u * 31.0 + time * 2.6);
			int curtainHeight = (int) (height * (0.10 + 0.22 * wave) * strength);

			// Hue slides between green (0.33), cyan (0.5) and violet (0.75) across the screen.
			double hue = 0.33 + 0.42 * (0.5 + 0.5 * Math.sin(u * 3.0 + time * 0.4));
			double shimmer = 0.75 + 0.25 * Math.sin(u * 40.0 + time * 3.0);

			for (int band = 0; band < BANDS; band++) {
				int y0 = curtainHeight * band / BANDS;
				int y1 = curtainHeight * (band + 1) / BANDS;
				if (y1 <= y0) {
					continue;
				}
				// Faint at the top of the screen, brightest along the curtain's lower edge.
				double f = (band + 0.5) / BANDS;
				double alpha = strength * 0.42 * shimmer * (0.20 + 0.80 * f * f);
				graphics.fill(x, y0, x + COLUMN_WIDTH, y1, hsvToArgb(hue, 0.75, 1.0, alpha));
			}
		}
	}

	private static int hsvToArgb(double hue, double saturation, double value, double alpha) {
		double h = (hue - Math.floor(hue)) * 6.0;
		int sector = (int) h;
		double fraction = h - sector;
		double p = value * (1.0 - saturation);
		double q = value * (1.0 - saturation * fraction);
		double t = value * (1.0 - saturation * (1.0 - fraction));

		double r;
		double g;
		double b;
		switch (sector) {
			case 0 -> { r = value; g = t; b = p; }
			case 1 -> { r = q; g = value; b = p; }
			case 2 -> { r = p; g = value; b = t; }
			case 3 -> { r = p; g = q; b = value; }
			case 4 -> { r = t; g = p; b = value; }
			default -> { r = value; g = p; b = q; }
		}

		int a = clamp255(alpha * 255.0);
		return (a << 24) | (clamp255(r * 255.0) << 16) | (clamp255(g * 255.0) << 8) | clamp255(b * 255.0);
	}

	private static int clamp255(double v) {
		return Math.max(0, Math.min(255, (int) v));
	}
}
