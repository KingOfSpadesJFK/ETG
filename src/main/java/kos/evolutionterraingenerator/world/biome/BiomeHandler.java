package kos.evolutionterraingenerator.world.biome;

import kos.evolutionterraingenerator.EvolutionTerrainGenerator;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeHills;
import net.minecraft.world.biome.BiomeTaiga;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(EvolutionTerrainGenerator.MODID)
public class BiomeHandler
{
	private static final String MODID = EvolutionTerrainGenerator.MODID;
	
    @ObjectHolder("snowy_redwood_taiga")
	public static final Biome SNOWY_REDWOOD_TAIGA = new BiomeTaiga(BiomeTaiga.Type.MEGA, (new Biome.BiomeProperties("Snowy Mega Taiga")).setTemperature(-0.5F).setRainfall(0.9F).setSnowEnabled()).setRegistryName(MODID, "snowy_redwood_taiga");
    @ObjectHolder("mutated_snowy_redwood_taiga")
	public static final Biome MUTATED_SNOWY_REDWOOD_TAIGA = new BiomeTaiga(BiomeTaiga.Type.MEGA_SPRUCE, (new Biome.BiomeProperties("Snowy Mega Spruce Taiga")).setTemperature(-0.5F).setRainfall(0.9F).setSnowEnabled()).setRegistryName(MODID, "mutated_snowy_redwood_taiga");
    //Instead of using the old Extreme Hills biome, I use a modification of this just to raise the snow level 
    @ObjectHolder("tundra")
    public static final Biome TUNDRA = new BiomeHills(BiomeHills.Type.NORMAL, (new Biome.BiomeProperties("Tundra")).setTemperature(0.25F).setRainfall(0.3F)).setRegistryName(MODID, "tundra");
    @ObjectHolder("gravelly_tundra")
    public static final Biome GRAVELLY_TUNDRA = new BiomeHills(BiomeHills.Type.MUTATED, (new Biome.BiomeProperties("Gravelly Tundra")).setTemperature(0.25F).setRainfall(0.3F)).setRegistryName(MODID, "gravelly_tundra");
    @ObjectHolder("gravel_beach")
    public static final Biome GRAVEL_BEACH = new BiomeGravelBeach((new Biome.BiomeProperties("Gravel Beach")).setBaseHeight(0.0F).setHeightVariation(0.025F).setTemperature(0.8F).setRainfall(0.4F)).setRegistryName(MODID, "gravel_beach");
    @ObjectHolder("snowy_gravel_beach")
    public static final Biome SNOWY_GRAVEL_BEACH = new BiomeGravelBeach((new Biome.BiomeProperties("Snowy Gravel Beach")).setBaseHeight(0.0F).setHeightVariation(0.025F).setTemperature(0.05F).setRainfall(0.3F).setSnowEnabled()).setRegistryName(MODID, "snowy_gravel_beach");
	
	public BiomeHandler()
	{
		registerOverworldBiomes(SNOWY_REDWOOD_TAIGA, BiomeType.ICY, false, 5);
		registerOverworldBiomes(MUTATED_SNOWY_REDWOOD_TAIGA, BiomeType.COOL, false, 5);
		registerOverworldBiomes(TUNDRA, BiomeType.COOL, false, 5);
		registerOverworldBiomes(GRAVELLY_TUNDRA, BiomeType.COOL, false, 5);
		registerOverworldBiomes(GRAVEL_BEACH, BiomeType.WARM, false, 5);
		registerOverworldBiomes(SNOWY_GRAVEL_BEACH, BiomeType.ICY, false, 5);
	}

	//For testing: /tp -733 71 -432 on Weird Fishes
	private static void registerOverworldBiomes(Biome biome, BiomeType type, boolean isSpawnBiome, int weight) 
	{
		BiomeManager.addBiome(type, new BiomeEntry(biome, weight));
	    if(isSpawnBiome)
	    {
	    	BiomeManager.addSpawnBiome(biome);
	    }
	    BiomeManager.addStrongholdBiome(biome);
	} 
	
	@SubscribeEvent
	public void registerBiomes(RegistryEvent.Register<Biome> event) {
		System.out.println("Does it do it?");
		
		System.out.println(SNOWY_REDWOOD_TAIGA.getRegistryName());
		System.out.println(MUTATED_SNOWY_REDWOOD_TAIGA.getRegistryName());
		System.out.println(TUNDRA.getRegistryName());
		System.out.println(SNOWY_REDWOOD_TAIGA.getRegistryName());
		event.getRegistry().register(SNOWY_REDWOOD_TAIGA);
		System.out.println("SNOWY_REDWOOD_TAIGA: " + Biome.getIdForBiome(SNOWY_REDWOOD_TAIGA));
		event.getRegistry().register(MUTATED_SNOWY_REDWOOD_TAIGA);
		System.out.println("MUTATED_SNOWY_REDWOOD_TAIGA: " + Biome.getIdForBiome(MUTATED_SNOWY_REDWOOD_TAIGA));
		event.getRegistry().register(TUNDRA);
		System.out.println("TUNDRA: " + Biome.getIdForBiome(TUNDRA));
		event.getRegistry().register(GRAVELLY_TUNDRA);
		System.out.println("GRAVELLY_TUNDRA: " + Biome.getIdForBiome(GRAVELLY_TUNDRA));
		event.getRegistry().register(GRAVEL_BEACH);
		System.out.println("GRAVEL_BEACH: " + Biome.getIdForBiome(GRAVEL_BEACH));
		event.getRegistry().register(SNOWY_GRAVEL_BEACH);
		System.out.println("SNOWY_GRAVEL_BEACH: " + Biome.getIdForBiome(SNOWY_GRAVEL_BEACH));
	}
}
