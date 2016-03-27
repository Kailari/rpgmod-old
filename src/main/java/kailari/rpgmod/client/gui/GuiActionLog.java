package kailari.rpgmod.client.gui;

import kailari.rpgmod.api.client.actionlog.ActionLog;
import kailari.rpgmod.api.client.actionlog.entries.ActionLogEntryBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides a action log to the lower right corner of the screen
 */
@SideOnly(Side.CLIENT)
public class GuiActionLog extends Gui {

	private Minecraft mc;

	public GuiActionLog() {
		this.mc = Minecraft.getMinecraft();
	}

	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Post event) {

		if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
			return;
		}

		// TODO: replace System.currentTimeMillis() calls with world timer
		long time = System.currentTimeMillis();

		int n = 1;
		for (ActionLogEntryBase entry : ActionLog.getEntries()) {
			if (entry.hasExpired(time)) {
				break;
			}

			GL11.glPushMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); // TODO: is this necessary?
			GL11.glScalef(0.5f, 0.5f, 0.5f);
			GL11.glDisable(GL11.GL_LIGHTING);

			String message = entry.getMessage();
			int offsX = (2 * event.getResolution().getScaledWidth()) - mc.fontRendererObj.getStringWidth(message) - 5;
			int offsY = (2 * event.getResolution().getScaledHeight()) - (mc.fontRendererObj.FONT_HEIGHT) * n++;

			// TODO: Get color from entry
			this.drawString(mc.fontRendererObj, message, offsX, offsY, Color.WHITE.getRGB());

			GL11.glScalef(2.0f, 2.0f, 2.0f);

			GL11.glPopMatrix();
		}
	}

	public enum LogType {
		KILLED_MOB("Slayed %1$s, +%2$d exp"),;            // String, Integer

		private final String message;

		LogType(String params) {
			this.message = params;
		}

		public String toString(Object... params) {
			return String.format(message, params);
		}
	}
}

