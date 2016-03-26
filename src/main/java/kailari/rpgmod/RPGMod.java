package kailari.rpgmod;

import kailari.rpgmod.api.common.stats.Stats;
import kailari.rpgmod.api.common.stats.attributes.Attributes;
import kailari.rpgmod.common.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = RPGMod.MODID, version = RPGMod.VERSION)
public class RPGMod {
	public static final String MODID = "rpgmod";
	public static final String VERSION = "0.1.0";

	@Instance(RPGMod.MODID)
	public static RPGMod instance;

	@SidedProxy(clientSide = "kailari.rpgmod.client.ClientProxy", serverSide = "kailari.rpgmod.common.CommonProxy")
	public static CommonProxy proxy;

	public static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();


		// Ensure that static classes' members are initialized properly.
		new Stats();
		new Attributes();
		// -------------------------------------------------------------

		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
	}
}
