package kos.evolutionterraingenerator.world.biome.container;

import java.util.ArrayList;
import java.util.List;

import kos.evolutionterraingenerator.world.biome.BiomeList;
import kos.evolutionterraingenerator.world.biome.container.BiomeContainer.Category;

public class DefaultBiomeContainers 
{
	public static List<BiomeContainer> containers = new ArrayList<BiomeContainer>();
	
	public static void createContainers() {
		containers = new ArrayList<BiomeContainer>();
		
		//Temperate Biomes
		containers.add(new BiomeContainer(BiomeList.PLAINS, 0.5, 0.125, 0.5));
		containers.add(new BiomeContainer(BiomeList.BIRCH_FOREST, 0.5, 0.375, 0.5));
		containers.add(new BiomeContainer(BiomeList.TALL_BIRCH_FOREST, 0.5, 0.375, 0.1));
		containers.add(new BiomeContainer(BiomeList.FOREST, 0.5, 0.625, 0.5));
		containers.add(new BiomeContainer(BiomeList.DARK_FOREST, 0.5, 0.875, 0.5));

		//Warm Biomes
		containers.add(new BiomeContainer(BiomeList.SAVANNA, 0.7, 0.125, 0.5));
		containers.add(new BiomeContainer(BiomeList.BADLANDS, 0.8, 0.0, 0.25).setPrimaryBeach(BiomeList.BADLANDS));
		containers.add(new BiomeContainer(BiomeList.PLAINS, 0.7, 0.375, 0.5));
		containers.add(new BiomeContainer(BiomeList.SUNFLOWER_PLAINS, 0.7, 0.375, 0.1));
		containers.add(new BiomeContainer(BiomeList.FOREST, 0.7, 0.625, 0.5));
		containers.add(new BiomeContainer(BiomeList.FLOWER_FOREST, 0.7, 0.625, 0.125));
		containers.add(new BiomeContainer(BiomeList.DARK_FOREST, 0.7, 0.875, 0.5));

		//Hot Biomes
		containers.add(new BiomeContainer(BiomeList.VOLCANO, 1.0, 0.0, 1.0).setWeirdnessRange(0.01).setPrimaryBeach(BiomeList.VOLCANO).setSecondaryBeach(BiomeList.VOLCANO));
		containers.add(new BiomeContainer(BiomeList.DESERT, 0.9, 0.125, 0.5).setPrimaryBeach(BiomeList.DRY_BEACH).setSecondaryBeach(BiomeList.DRY_GRAVEL_BEACH));
		containers.add(new BiomeContainer(BiomeList.SAVANNA, 0.9, 0.375, 0.5).setPrimaryBeach(BiomeList.DRY_BEACH).setSecondaryBeach(BiomeList.DRY_GRAVEL_BEACH));
		containers.add(new BiomeContainer(BiomeList.SHATTERED_SAVANNA, 0.9, 0.375, 0.9).setPrimaryBeach(BiomeList.DRY_BEACH).setSecondaryBeach(BiomeList.DRY_GRAVEL_BEACH) );
		containers.add(new BiomeContainer(BiomeList.DARK_FOREST, 0.9, 0.625, 0.55));
		containers.add(new BiomeContainer(BiomeList.RAINFOREST, 0.9, 0.625, 0.45));
		containers.add(new BiomeContainer(BiomeList.JUNGLE, 0.9, 0.875, 0.5));
		containers.add(new BiomeContainer(BiomeList.BAMBOO_JUNGLE, 0.9, 0.875, 0.1).setWeirdnessRange(0.05));

		//Cold Biomes
		containers.add(new BiomeContainer(BiomeList.MOUNTAINS, 0.3, 0.15, 0.5).setPrimaryBeach(BiomeList.STONE_SHORE));
		containers.add(new BiomeContainer(BiomeList.GRAVELLY_MOUNTAINS, 0.3, 0.15, 0.975).setPrimaryBeach(BiomeList.STONE_SHORE).setWeirdnessRange(0.1));
		containers.add(new BiomeContainer(BiomeList.WOODED_MOUNTAINS, 0.3, 0.25, 0.5).setPrimaryBeach(BiomeList.STONE_SHORE));
		containers.add(new BiomeContainer(BiomeList.TAIGA, 0.3, 0.45, 0.5));
		containers.add(new BiomeContainer(BiomeList.GIANT_TREE_TAIGA, 0.3, 0.85, 0.5));
		containers.add(new BiomeContainer(BiomeList.GIANT_SPRUCE_TAIGA, 0.3, 0.85, 0.05).setWeirdnessRange(0.5));

		//Snowy Biomes
		containers.add(new BiomeContainer(BiomeList.SNOWY_TUNDRA, 0.1, 0.15, 0.5).setPrimaryBeach(BiomeList.SNOWY_BEACH).setSecondaryBeach(BiomeList.SNOWY_GRAVEL_BEACH));
		containers.add(new BiomeContainer(BiomeList.ICE_SPIKES, 0.1, 0.15, 0.0125).setPrimaryBeach(BiomeList.ICE_SPIKES).setSecondaryBeach(BiomeList.ICE_SPIKES).setWeirdnessRange(0.125));
		containers.add(new BiomeContainer(BiomeList.SNOWY_TAIGA, 0.1, 0.45, 0.5).setPrimaryBeach(BiomeList.SNOWY_BEACH).setSecondaryBeach(BiomeList.SNOWY_GRAVEL_BEACH));
		containers.add(new BiomeContainer(BiomeList.SNOWY_GIANT_TREE_TAIGA, 0.1, 0.85, 0.5).setPrimaryBeach(BiomeList.SNOWY_BEACH).setSecondaryBeach(BiomeList.SNOWY_GRAVEL_BEACH));
		containers.add(new BiomeContainer(BiomeList.SNOWY_GIANT_SPRUCE_TAIGA, 0.1, 0.85, 0.975).setWeirdnessRange(0.5).setPrimaryBeach(BiomeList.SNOWY_BEACH).setSecondaryBeach(BiomeList.SNOWY_GRAVEL_BEACH));
		
		//Swamp Biomes
		containers.add(new BiomeContainer(BiomeList.SWAMP, 0.75, 0.5, 0.5).setCategory(Category.SWAMP));
		containers.add(new BiomeContainer(null, 0.25, 0.5, 0.5).setCategory(Category.SWAMP));
	}
	
	public static boolean isEmpty() {
		return containers.isEmpty();
	}
}
