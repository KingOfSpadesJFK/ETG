package kos.evolutionterraingenerator.world.biome;

import kos.evolutionterraingenerator.core.EvolutionTerrainGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public class BiomeList
{
	public final static Identifier PLAINS = BiomeKeys.PLAINS.getValue();
	public final static Identifier OCEAN = BiomeKeys.OCEAN.getValue();
	
	public final static Identifier FOREST = BiomeKeys.FOREST.getValue();
	public final static Identifier MOUNTAINS = BiomeKeys.MOUNTAINS.getValue();
	public final static Identifier WOODED_MOUNTAINS = BiomeKeys.WOODED_MOUNTAINS.getValue();
	public final static Identifier TAIGA = BiomeKeys.TAIGA.getValue();
	public final static Identifier JUNGLE = BiomeKeys.JUNGLE.getValue();
	public final static Identifier DESERT = BiomeKeys.DESERT.getValue();
	public final static Identifier SWAMP = BiomeKeys.SWAMP.getValue();
	public final static Identifier SAVANNA = BiomeKeys.SAVANNA.getValue();
	public final static Identifier DARK_FOREST = BiomeKeys.DARK_FOREST.getValue();
	public final static Identifier BIRCH_FOREST = BiomeKeys.BIRCH_FOREST.getValue();
	public final static Identifier GIANT_TREE_TAIGA = BiomeKeys.GIANT_TREE_TAIGA.getValue();
	public final static Identifier BADLANDS = BiomeKeys.BADLANDS.getValue();
	public final static Identifier WOODED_BADLANDS_PLATEAU = BiomeKeys.WOODED_BADLANDS_PLATEAU.getValue();
	public final static Identifier SNOWY_TUNDRA = BiomeKeys.SNOWY_TUNDRA.getValue();
	public final static Identifier SNOWY_TAIGA = BiomeKeys.SNOWY_TAIGA.getValue();

	public final static Identifier SUNFLOWER_PLAINS = BiomeKeys.SUNFLOWER_PLAINS.getValue();
	public final static Identifier FLOWER_FOREST = BiomeKeys.FLOWER_FOREST.getValue();
	public final static Identifier GRAVELLY_MOUNTAINS = BiomeKeys.GRAVELLY_MOUNTAINS.getValue();
	public final static Identifier TALL_BIRCH_FOREST = BiomeKeys.TALL_BIRCH_FOREST.getValue();
	public final static Identifier SHATTERED_SAVANNA = BiomeKeys.SHATTERED_SAVANNA.getValue();
	public final static Identifier GIANT_SPRUCE_TAIGA = BiomeKeys.GIANT_SPRUCE_TAIGA.getValue();
	public final static Identifier ICE_SPIKES = BiomeKeys.ICE_SPIKES.getValue();
	public static final Identifier MUSHROOM_FIELDS = BiomeKeys.MUSHROOM_FIELDS.getValue();
	public static final Identifier BAMBOO_JUNGLE = BiomeKeys.BAMBOO_JUNGLE.getValue();

	public final static Identifier FROZEN_OCEAN = BiomeKeys.FROZEN_OCEAN.getValue();
	public final static Identifier DEEP_FROZEN_OCEAN = BiomeKeys.DEEP_FROZEN_OCEAN.getValue();
	public final static Identifier COLD_OCEAN = BiomeKeys.COLD_OCEAN.getValue();
	public final static Identifier DEEP_COLD_OCEAN = BiomeKeys.DEEP_COLD_OCEAN.getValue();
	public final static Identifier DEEP_OCEAN = BiomeKeys.DEEP_OCEAN.getValue();
	public final static Identifier LUKEWARM_OCEAN = BiomeKeys.LUKEWARM_OCEAN.getValue();
	public final static Identifier DEEP_LUKEWARM_OCEAN = BiomeKeys.DEEP_LUKEWARM_OCEAN.getValue();
	public final static Identifier WARM_OCEAN = BiomeKeys.WARM_OCEAN.getValue();
	public final static Identifier DEEP_WARM_OCEAN = BiomeKeys.DEEP_WARM_OCEAN.getValue();

	public final static Identifier BEACH = BiomeKeys.BEACH.getValue();
	public final static Identifier SNOWY_BEACH = BiomeKeys.BEACH.getValue();
	public final static Identifier STONE_BEACH = BiomeKeys.BEACH.getValue();
	
	public final static Identifier RIVER = BiomeKeys.RIVER.getValue();
	
	public final static Identifier GRAVEL_BEACH = id("gravel_beach");
	public final static Identifier SNOWY_GRAVEL_BEACH = id("snowy_gravel_beach");
	public final static Identifier DRY_GRAVEL_BEACH = id("dry_gravel_beach");
	public final static Identifier DRY_BEACH = id("dry_beach");
	
	public final static Identifier SNOWY_GIANT_TREE_TAIGA = id("snowy_giant_tree_taiga");
	public final static Identifier SNOWY_GIANT_SPRUCE_TAIGA = id("snowy_giant_spruce_taiga");
	
	private static Identifier id(String name) {
		return new Identifier(EvolutionTerrainGenerator.MODID, name);
	}
}
