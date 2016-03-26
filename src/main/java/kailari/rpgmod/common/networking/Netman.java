package kailari.rpgmod.common.networking;

import kailari.rpgmod.RPGMod;
import kailari.rpgmod.common.networking.messages.ReSyncCharacterStatsMessage;
import kailari.rpgmod.common.networking.messages.SyncStatVariableMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Helps keeping main mod file clean by storing and handling all network stuff here
 */
public class Netman {
	private Netman() {
	}

	public static SimpleNetworkWrapper channel_0;

	public static void initialize() {
		createChannels();

		registerMessages();
	}

	private static void createChannels() {
		channel_0 = NetworkRegistry.INSTANCE.newSimpleChannel(RPGMod.MODID + "_channel_0");
	}

	private static void registerMessages() {
		// HACK: Start at -1 and increment before passing the variable to prevent "variable value unused" -type of warnings
		int discriminator = -1;

		// channel_0
		channel_0.registerMessage(SyncStatVariableMessage.Handler.class, SyncStatVariableMessage.class, ++discriminator, Side.CLIENT);
		channel_0.registerMessage(ReSyncCharacterStatsMessage.Handler.class, ReSyncCharacterStatsMessage.class, ++discriminator, Side.CLIENT);


		// ...
	}
}
