package kos.evolutionterraingenerator.world.biome.container;

import java.util.ArrayList;
import java.util.List;

import kos.evolutionterraingenerator.world.biome.BiomeList;
import kos.evolutionterraingenerator.world.biome.container.BiomeContainer.Category;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class DefaultBiomeContainers 
{
	public static List<BiomeContainer> containers = new ArrayList<BiomeContainer>();
	
	public static void createContainers() {
		containers = new ArrayList<BiomeContainer>();
		boolean bygEnabled = FabricLoader.getInstance().isModLoaded("byg");
		boolean noMods = !bygEnabled;
		
		//Temperate Biomes
		containers.add(new BiomeContainer(BiomeList.PLAINS, 0.5, 0.125, 0.5));
		containers.add(new BiomeContainer(BiomeList.BIRCH_FOREST, 0.5, 0.375, 0.5));
		containers.add(new BiomeContainer(BiomeList.TALL_BIRCH_FOREST, 0.5, 0.375, 0.1));
		containers.add(new BiomeContainer(BiomeList.FOREST, 0.5, 0.625, 0.5));
		containers.add(new BiomeContainer(BiomeList.DARK_FOREST, 0.5, 0.875, 0.5));
		if (bygEnabled) {
			containers.add(new BiomeContainer(BiomeList.BYG_GROVE, 0.5, 0.321, 0.45));
			containers.add(new BiomeContainer(BiomeList.BYG_ALLIUM_FIELDS, 0.5, 0.25, 0.841).setWeirdnessRange(0.0042));
			containers.add(new BiomeContainer(BiomeList.BYG_ORCHARD, 0.5, 0.375, 0.2));
			containers.add(new BiomeContainer(BiomeList.BYG_DECIDUOUS_FOREST, 0.5, 0.5, 0.372));
			containers.add(new BiomeContainer(BiomeList.BYG_CHERRY_BLOSSOM_FOREST, 0.5, 0.5, 0.675).setWeirdnessRange(0.005));
			containers.add(new BiomeContainer(BiomeList.BYG_ASPEN_FOREST, 0.5, 0.42, 0.315));
			containers.add(new BiomeContainer(BiomeList.BYG_REDWOOD_TROPICS, 0.5, 0.875, 0.85));
		}

		//Warm Biomes
		containers.add(new BiomeContainer(BiomeList.SAVANNA, 0.7, 0.125, 0.5));
		containers.add(new HeightBasedBiomeContainer(BiomeList.BADLANDS, 0.7, 0.0, 0.25,
				new Identifier[] {BiomeList.BADLANDS, BiomeList.WOODED_BADLANDS_PLATEAU},
				new int[] {0, 110}
		).setPrimaryBeach(BiomeList.BADLANDS));
		containers.add(new BiomeContainer(BiomeList.PLAINS, 0.7, 0.375, 0.5));
		containers.add(new BiomeContainer(BiomeList.SUNFLOWER_PLAINS, 0.7, 0.375, 0.1));
		containers.add(new BiomeContainer(BiomeList.FOREST, 0.7, 0.625, 0.5));
		containers.add(new BiomeContainer(BiomeList.FLOWER_FOREST, 0.7, 0.625, 0.125).setWeirdnessRange(0.01));
		containers.add(new BiomeContainer(BiomeList.DARK_FOREST, 0.7, 0.875, 0.65));
		containers.add(new BiomeContainer(BiomeList.RAINFOREST, 0.7, 0.875, 0.45));
		if (bygEnabled) {
			containers.add(new BiomeContainer(BiomeList.BYG_PRAIRIE, 0.7, 0.10, 0.9));
			containers.add(new BiomeContainer(BiomeList.BYG_AMARANTH_FIELDS, 0.7, 0.42, 0.2).setWeirdnessRange(0.002));
			containers.add(new BiomeContainer(BiomeList.BYG_ENCHANTED_GROVE, 0.7, 0.55, 0.95).setWeirdnessRange(0.001));
			containers.add(new BiomeContainer(BiomeList.BYG_RED_OAK_FOREST, 0.7, 0.575, 0.75));
			containers.add(new BiomeContainer(BiomeList.BYG_WOODLANDS, 0.7, 0.875, 0.315));
			containers.add(new BiomeContainer(BiomeList.BYG_EBONY_WOODS, 0.7, 0.875, 0.925));
			containers.add(new BiomeContainer(BiomeList.BYG_JACARANDA_FOREST, 0.7, 0.95, 0.45));
			containers.add(new BiomeContainer(BiomeList.BYG_ENCHANTED_FOREST, 0.7, 0.95, 0.95).setWeirdnessRange(0.001));
		}
		if (noMods) {
			
		}
		
		//Hot Biomes
		//containers.add(new BiomeContainer(BiomeList.VOLCANO, 1.0, 0.0, 0.0).setWeirdnessRange(0.001).setPrimaryBeach(BiomeList.VOLCANIC_EDGE).setSecondaryBeach(BiomeList.VOLCANIC_EDGE));
		containers.add(new BiomeContainer(BiomeList.DESERT, 0.9, 0.125, 0.5).setPrimaryBeach(BiomeList.DRY_BEACH).setSecondaryBeach(BiomeList.DRY_GRAVEL_BEACH));
		containers.add(new BiomeContainer(BiomeList.SAVANNA, 0.9, 0.375, 0.5).setPrimaryBeach(BiomeList.DRY_BEACH).setSecondaryBeach(BiomeList.DRY_GRAVEL_BEACH));
		containers.add(new BiomeContainer(BiomeList.SHATTERED_SAVANNA, 0.9, 0.375, 0.9).setWeirdnessRange(0.01).setPrimaryBeach(BiomeList.DRY_BEACH).setSecondaryBeach(BiomeList.DRY_GRAVEL_BEACH) );
		containers.add(new BiomeContainer(BiomeList.JUNGLE, 0.9, 0.875, 0.5));
		containers.add(new BiomeContainer(BiomeList.BAMBOO_JUNGLE, 0.9, 0.875, 0.1).setWeirdnessRange(0.005));
		if (bygEnabled) {
			containers.add(new BiomeContainer(BiomeList.BYG_BAOBAB_SAVANNA, 0.9, 0.25, 0.175));
			containers.add(new BiomeContainer(BiomeList.BYG_DUNES, 0.9, 0.0, 0.625));
			containers.add(new BiomeContainer(BiomeList.BYG_MOJAVE_DESERT, 0.9, 0.1, 0.8));
			containers.add(new BiomeContainer(BiomeList.BYG_RED_DESERT, 0.9, 0.0, 1.0));
			containers.add(new BiomeContainer(BiomeList.BYG_RED_ROCK_MOUNTAINS, 0.9, 0.15, 0.375));
			containers.add(new BiomeContainer(BiomeList.BYG_SHRUBLANDS, 0.9, 0.165, 0.435));
			containers.add(new BiomeContainer(BiomeList.BYG_SIERRA_VALLEY, 0.9, 0.125, 0.215));
			containers.add(new BiomeContainer(BiomeList.BYG_TROPICAL_RAINFOREST, 0.9, 0.75, 0.4125));
			containers.add(new BiomeContainer(BiomeList.BYG_TROPICAL_FUNGAL_RAINFOREST, 0.9, 0.75, 0.331).setWeirdnessRange(0.002));
			containers.add(new BiomeContainer(BiomeList.BYG_CRAG_GARDENS, 0.9, 0.75, 0.685).setWeirdnessRange(0.001).useDefaultSurfaceBuilder(true));
			containers.add(new BiomeContainer(BiomeList.BYG_GUIANA_SHIELD, 0.9, 0.785, 0.25));
		}
		if (noMods) {
			containers.add(new BiomeContainer(BiomeList.DARK_FOREST, 0.9, 0.625, 0.55));
			containers.add(new BiomeContainer(BiomeList.RAINFOREST, 0.9, 0.625, 0.45));
		}
		
		//Cold Biomes
		containers.add(new BiomeContainer(BiomeList.MOUNTAINS, 0.3, 0.15, 0.5).setPrimaryBeach(BiomeList.STONE_SHORE));
		containers.add(new BiomeContainer(BiomeList.GRAVELLY_MOUNTAINS, 0.3, 0.15, 0.975).setPrimaryBeach(BiomeList.STONE_SHORE).setWeirdnessRange(0.1));
		containers.add(new BiomeContainer(BiomeList.WOODED_MOUNTAINS, 0.3, 0.25, 0.5).setPrimaryBeach(BiomeList.STONE_SHORE));
		containers.add(new BiomeContainer(BiomeList.TAIGA, 0.3, 0.45, 0.5));
		containers.add(new BiomeContainer(BiomeList.GIANT_TREE_TAIGA, 0.3, 0.85, 0.5));
		containers.add(new BiomeContainer(BiomeList.GIANT_SPRUCE_TAIGA, 0.3, 0.85, 0.05).setWeirdnessRange(0.05));
		if (bygEnabled) {
			containers.add(new BiomeContainer(BiomeList.BYG_AUTUMNAL_VALLEY, 0.3, 0.125, 0.675));
			containers.add(new BiomeContainer(BiomeList.BYG_BLUE_TAIGA, 0.3, 0.45, 0.125));
			containers.add(new BiomeContainer(BiomeList.BYG_BLUFF_STEEPS, 0.3, 0.875, 0.95).setWeirdnessRange(0.05));
			containers.add(new BiomeContainer(BiomeList.BYG_BOREAL_FOREST, 0.3, 0.5, 0.6));
			containers.add(new BiomeContainer(BiomeList.BYG_CIKA_WOODS, 0.3, 0.95, 0.325));
			containers.add(new BiomeContainer(BiomeList.BYG_DOVER_MOUNTAINS, 0.3, 0.9, 0.85));
			containers.add(new BiomeContainer(BiomeList.BYG_EVERGREEN_TAIGA, 0.3, 0.525, 0.65));
			containers.add(new BiomeContainer(BiomeList.BYG_MAPLE_TAIGA, 0.3, 0.425, 0.1));
			containers.add(new BiomeContainer(BiomeList.BYG_SEASONAL_BIRCH_FOREST, 0.3, 0.3, 0.35));
			containers.add(new BiomeContainer(BiomeList.BYG_SEASONAL_FOREST, 0.3, 0.65, 0.35));
			containers.add(new BiomeContainer(BiomeList.BYG_SEASONAL_DECIDUOUS_FOREST, 0.3, 0.75, 0.35));
			containers.add(new BiomeContainer(BiomeList.BYG_SEASONAL_TAIGA, 0.3, 0.5, 0.35));
			containers.add(new BiomeContainer(BiomeList.BYG_THE_BLACK_FOREST, 0.3, 0.825, 0.125).setWeirdnessRange(0.05));
			containers.add(new BiomeContainer(BiomeList.BYG_WEEPING_WITCH_FOREST, 0.3, 0.4, 0.27).setWeirdnessRange(0.02));
			containers.add(new BiomeContainer(BiomeList.BYG_ZELKOVA_FOREST, 0.3, 0.4, 0.77).setWeirdnessRange(0.02));
		}

		//Snowy Biomes
		containers.add(new BiomeContainer(BiomeList.SNOWY_TUNDRA, 0.1, 0.15, 0.5).setPrimaryBeach(BiomeList.SNOWY_BEACH).setSecondaryBeach(BiomeList.SNOWY_GRAVEL_BEACH));
		containers.add(new BiomeContainer(BiomeList.ICE_SPIKES, 0.1, 0.15, 0.0125).setPrimaryBeach(BiomeList.ICE_SPIKES).setSecondaryBeach(BiomeList.ICE_SPIKES).setWeirdnessRange(0.125));
		containers.add(new BiomeContainer(BiomeList.SNOWY_TAIGA, 0.1, 0.45, 0.5).setPrimaryBeach(BiomeList.SNOWY_BEACH).setSecondaryBeach(BiomeList.SNOWY_GRAVEL_BEACH));
		containers.add(new BiomeContainer(BiomeList.SNOWY_GIANT_TREE_TAIGA, 0.1, 0.85, 0.5).setPrimaryBeach(BiomeList.SNOWY_BEACH).setSecondaryBeach(BiomeList.SNOWY_GRAVEL_BEACH));
		containers.add(new BiomeContainer(BiomeList.SNOWY_GIANT_SPRUCE_TAIGA, 0.1, 0.85, 0.975).setWeirdnessRange(0.05).setPrimaryBeach(BiomeList.SNOWY_BEACH).setSecondaryBeach(BiomeList.SNOWY_GRAVEL_BEACH));
		if (bygEnabled) {
			containers.add(new BiomeContainer(BiomeList.BYG_LUSH_TUNDRA, 0.1, 0.15, 0.75).setWeirdnessRange(0.065).setPrimaryBeach(BiomeList.SNOWY_BEACH).setSecondaryBeach(BiomeList.SNOWY_GRAVEL_BEACH));
			containers.add(new BiomeContainer(BiomeList.BYG_SHATTERED_GLACIER, 0.1, 0.0, 0.0).setWeirdnessRange(0.05).setPrimaryBeach(BiomeList.SNOWY_BEACH).setSecondaryBeach(BiomeList.SNOWY_GRAVEL_BEACH));
			containers.add(new BiomeContainer(BiomeList.BYG_SNOWY_BLUE_TAIGA, 0.1, 0.45, 0.875).setPrimaryBeach(BiomeList.SNOWY_BEACH).setSecondaryBeach(BiomeList.SNOWY_GRAVEL_BEACH));
			containers.add(new BiomeContainer(BiomeList.BYG_SNOWY_CONIFEROUS_FOREST, 0.1, 0.95, 0.5).setPrimaryBeach(BiomeList.SNOWY_BEACH).setSecondaryBeach(BiomeList.SNOWY_GRAVEL_BEACH));
			containers.add(new BiomeContainer(BiomeList.BYG_SNOWY_DECIDUOUS_FOREST, 0.1, 0.5, 0.75).setPrimaryBeach(BiomeList.SNOWY_BEACH).setSecondaryBeach(BiomeList.SNOWY_GRAVEL_BEACH));
			containers.add(new BiomeContainer(BiomeList.BYG_SNOWY_EVERGREEN_TAIGA, 0.1, 0.8, 0.25).setWeirdnessRange(0.065).setPrimaryBeach(BiomeList.SNOWY_BEACH).setSecondaryBeach(BiomeList.SNOWY_GRAVEL_BEACH));
		}
		
		//Ocean Biomes
		containers.add(new BiomeContainer(BiomeList.FROZEN_OCEAN, 0.1, 0.0, 0.5).setCategory(Category.OCEAN));
		containers.add(new BiomeContainer(BiomeList.DEEP_FROZEN_OCEAN, 0.1, 1.0, 0.5).setCategory(Category.OCEAN));
		containers.add(new BiomeContainer(BiomeList.COLD_OCEAN, 0.3, 0.0, 0.5).setCategory(Category.OCEAN));
		containers.add(new BiomeContainer(BiomeList.DEEP_COLD_OCEAN, 0.3, 1.0, 0.5).setCategory(Category.OCEAN));
		containers.add(new BiomeContainer(BiomeList.OCEAN, 0.5, 0.0, 0.5).setCategory(Category.OCEAN));
		containers.add(new BiomeContainer(BiomeList.DEEP_OCEAN, 0.5, 1.0, 0.5).setCategory(Category.OCEAN));
		containers.add(new BiomeContainer(BiomeList.LUKEWARM_OCEAN, 0.7, 0.0, 0.5).setCategory(Category.OCEAN));
		containers.add(new BiomeContainer(BiomeList.DEEP_LUKEWARM_OCEAN, 0.7, 1.0, 0.5).setCategory(Category.OCEAN));
		containers.add(new BiomeContainer(BiomeList.WARM_OCEAN, 0.9, 0.0, 0.5).setCategory(Category.OCEAN));
		containers.add(new BiomeContainer(BiomeList.DEEP_WARM_OCEAN, 0.9, 1.0, 0.5).setCategory(Category.OCEAN));
		
		//Swamp Biomes
		containers.add(new BiomeContainer(BiomeList.SWAMP, 0.75, 0.5, 0.5).setCategory(Category.SWAMP));
		containers.add(new BiomeContainer(null, 0.25, 0.5, 0.5).setCategory(Category.SWAMP));
		if (bygEnabled) {
			containers.add(new BiomeContainer(BiomeList.BYG_BAYOU, 0.785, 0.5, 0.23).setCategory(Category.SWAMP));
			containers.add(new BiomeContainer(BiomeList.BYG_CYPRESS_SWAMPLANDS, 0.785, 0.5, 0.95).setCategory(Category.SWAMP));
			containers.add(new BiomeContainer(BiomeList.BYG_MANGROVE_MARSHES, 0.75, 0.95, 0.45));
			containers.add(new BiomeContainer(BiomeList.BYG_VIBRANT_SWAMPLANDS, 0.65, 0.5, 0.23).setCategory(Category.SWAMP));
			containers.add(new BiomeContainer(BiomeList.BYG_GREAT_LAKES, 0.4, 0.5, 0.2).setCategory(Category.SWAMP));
			containers.add(new BiomeContainer(BiomeList.BYG_COLD_SWAMPLANDS, 0.325, 0.5, 0.5).setCategory(Category.SWAMP));
		}
		if (noMods) {
		}
		
		//Island Biomes
		containers.add(new BiomeContainer(BiomeList.SNOWY_TUNDRA, 0.1, 0.15, 0.5).setCategory(Category.ISLAND).setPrimaryBeach(BiomeList.SNOWY_BEACH).setSecondaryBeach(BiomeList.SNOWY_GRAVEL_BEACH));
		containers.add(new BiomeContainer(BiomeList.TAIGA, 0.3, 0.45, 0.5).setCategory(Category.ISLAND));
		containers.add(new BiomeContainer(BiomeList.PLAINS, 0.5, 0.125, 0.5).setCategory(Category.ISLAND));
		containers.add(new BiomeContainer(BiomeList.FOREST, 0.5, 0.625, 0.5).setCategory(Category.ISLAND));
		containers.add(new BiomeContainer(BiomeList.PLAINS, 0.7, 0.125, 0.5).setCategory(Category.ISLAND));
		containers.add(new BiomeContainer(BiomeList.FOREST, 0.7, 0.625, 0.5).setCategory(Category.ISLAND));
		containers.add(new BiomeContainer(BiomeList.PLAINS, 0.9, 0.125, 0.5).setCategory(Category.ISLAND));
		containers.add(new BiomeContainer(BiomeList.RAINFOREST, 0.7, 0.625, 0.45).setCategory(Category.ISLAND));
		containers.add(new BiomeContainer(BiomeList.JUNGLE, 0.9, 0.875, 0.5).setCategory(Category.ISLAND));
		containers.add(new BiomeContainer(BiomeList.VOLCANO, 1.0, 0.0, 1.0).setCategory(Category.ISLAND).setWeirdnessRange(0.075).setPrimaryBeach(BiomeList.VOLCANIC_EDGE).setSecondaryBeach(BiomeList.VOLCANIC_EDGE));
	}
	
	public static boolean isEmpty() {
		return containers == null || containers.isEmpty();
	}
}
