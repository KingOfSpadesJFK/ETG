package kos.evolutionterraingenerator.world.biome.container;

import java.util.ArrayList;
import java.util.List;

import kos.evolutionterraingenerator.world.biome.BiomeList;

public class DefaultBiomeContainers 
{
	public static List<BiomeContainer> containers;
	
	public static void createContainers() {
		containers = new ArrayList<BiomeContainer>();
		
		//Plains
		containers.add(new BiomeContainer(BiomeList.PLAINS, 0.5, 0.12, 0.5));
		containers.add(new BiomeContainer(BiomeList.SUNFLOWER_PLAINS, 0.5, 0.12, 0.9));
		
		//Forest
		containers.add(new BiomeContainer(BiomeList.FOREST, 0.5, 0.5, 0.5));
		containers.add(new BiomeContainer(BiomeList.FLOWER_FOREST, 0.5, 0.5, 0.235));
		
		//Mountains
		containers.add(new BiomeContainer(BiomeList.MOUNTAINS, 0.25, 0.125, 0.5));
		containers.add(new BiomeContainer(BiomeList.WOODED_MOUNTAINS, 0.25, 0.25, 0.5));
		containers.add(new BiomeContainer(BiomeList.GRAVELLY_MOUNTAINS, 0.25, 0.1, 0.75));
		
		//Taiga
		containers.add(new BiomeContainer(BiomeList.TAIGA, 0.325, 0.5, 0.5));
		
		//Snowy Tundra
		containers.add(new BiomeContainer(BiomeList.SNOWY_TUNDRA, 0.1, 0.1, 0.5));
		containers.add(new BiomeContainer(BiomeList.ICE_SPIKES, 0.085, 0.05, 0.8));
		
		//Snowy Taiga
		containers.add(new BiomeContainer(BiomeList.SNOWY_TAIGA, 0.125, 0.5, 0.5));
		
		//Desert
		containers.add(new BiomeContainer(BiomeList.DESERT, 0.9, 0.1, 0.5));
		
		//Jungle
		containers.add(new BiomeContainer(BiomeList.JUNGLE, 0.875, 0.9, 0.5));
		containers.add(new BiomeContainer(BiomeList.BAMBOO_JUNGLE, 0.875, 0.9, 0.8));
		
		//Badlands
		containers.add(new BiomeContainer(BiomeList.BADLANDS, 0.75, 0.1, 0.5));
		
		//Dark Forest
		containers.add(new BiomeContainer(BiomeList.DARK_FOREST, 0.65, 0.75, 0.5));
		
		//Savanna
		containers.add(new BiomeContainer(BiomeList.SAVANNA, 0.75, 0.235, 0.5));
		containers.add(new BiomeContainer(BiomeList.SHATTERED_SAVANNA, 0.75, 0.235, 0.9));
		
		//Birch Forest
		containers.add(new BiomeContainer(BiomeList.BIRCH_FOREST, 0.45, 0.5, 0.5));
		containers.add(new BiomeContainer(BiomeList.TALL_BIRCH_FOREST, 0.45, 0.5, 0.124));
		
		//Giant Tree Taiga
		containers.add(new BiomeContainer(BiomeList.GIANT_TREE_TAIGA, 0.3275, 0.765, 0.5));
		containers.add(new BiomeContainer(BiomeList.GIANT_SPRUCE_TAIGA, 0.3275, 0.765, 0.12));
		
		//Snowy Giant Tree Taiga
		containers.add(new BiomeContainer(BiomeList.SNOWY_GIANT_TREE_TAIGA, 0.125, 0.765, 0.5));
		containers.add(new BiomeContainer(BiomeList.SNOWY_GIANT_SPRUCE_TAIGA, 0.125, 0.765, 0.9));
	}
}
