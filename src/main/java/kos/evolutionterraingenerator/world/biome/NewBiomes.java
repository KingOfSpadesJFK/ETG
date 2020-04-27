package kos.evolutionterraingenerator.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ObjectHolder;
import kos.evolutionterraingenerator.EvolutionTerrainGenerator;

@ObjectHolder(EvolutionTerrainGenerator.MODID)
public class NewBiomes {
	public static final Biome SNOWY_GIANT_TREE_TAIGA = register("snowy_giant_tree_taiga", new SnowyGiantTreeTaigaBiome());
	public static final Biome SNOWY_GIANT_SPRUCE_TAIGA = register("snowy_giant_spruce_taiga", new SnowyGiantSpruceTaiga());

	public static final Biome GRAVEL_BEACH = register("gravel_beach", new GravelBeachBiome());
	public static final Biome SNOWY_GRAVEL_BEACH = register("snowy_gravel_beach", new SnowyGravelBeachBiome());

	public static final Biome TUNDRA = register("tundra", new TundraBiome());
	public static final Biome TUNDRA_WOODED = register("tundra_wooded", new TundraWooded());
	public static final Biome GRAVELLY_TUNDRA = register("gravelly_tundra", new GravellyTundra());
	
	public static final Biome[] NEW_BIOMES = {
			NewBiomes.SNOWY_GIANT_TREE_TAIGA, 
			NewBiomes.SNOWY_GIANT_SPRUCE_TAIGA, 
			NewBiomes.GRAVEL_BEACH, 
			NewBiomes.SNOWY_GRAVEL_BEACH, 
			NewBiomes.TUNDRA, 
			NewBiomes.TUNDRA_WOODED, 
			NewBiomes.GRAVELLY_TUNDRA};

	private static Biome register(String name, Biome biome)
	{
		return biome.setRegistryName(EvolutionTerrainGenerator.MODID, name);
	}
}
