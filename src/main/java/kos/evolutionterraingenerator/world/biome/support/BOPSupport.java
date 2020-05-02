package kos.evolutionterraingenerator.world.biome.support;

import java.util.ArrayList;
import com.google.common.collect.Sets;
import java.util.Set;

import biomesoplenty.api.biome.BOPBiomes;

import kos.evolutionterraingenerator.world.biome.EvoBiomes;
import kos.evolutionterraingenerator.world.biome.NewBiomes;
import kos.evolutionterraingenerator.world.biome.EvoBiome;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class BOPSupport 
{
	public static final String BOP_MODID = "biomesoplenty";
	
	public static void setup()
	{
		//Snowy Biomes
		ArrayList<EvoBiome> temp = new ArrayList<EvoBiome>();
		Set<Biome> tempA = Sets.newHashSet(Biomes.SNOWY_TUNDRA,
				Biomes.SNOWY_TUNDRA,
				BOPBiomes.snowy_fir_clearing.get(),
				BOPBiomes.snowy_fir_clearing.get(),
				Biomes.ICE_SPIKES, 
				BOPBiomes.snowy_fir_clearing.get(),
				BOPBiomes.snowy_fir_clearing.get(),
				Biomes.SNOWY_TUNDRA, 
				Biomes.SNOWY_TUNDRA, 
				BOPBiomes.cold_desert.get());
		temp.add(new EvoBiome(Biomes.SNOWY_TAIGA, tempA.toArray(new Biome[tempA.size()])));
		tempA = Sets.newHashSet(Biomes.SNOWY_TAIGA, 
				BOPBiomes.snowy_forest.get(),
				Biomes.SNOWY_TAIGA, 
				BOPBiomes.snowy_forest.get(), 
				Biomes.SNOWY_TAIGA, 
				BOPBiomes.snowy_forest.get());
		temp.add(new EvoBiome(Biomes.SNOWY_TAIGA, tempA.toArray(new Biome[tempA.size()])));
		tempA = Sets.newHashSet(NewBiomes.SNOWY_GIANT_TREE_TAIGA,
				NewBiomes.SNOWY_GIANT_SPRUCE_TAIGA,
				BOPBiomes.snowy_coniferous_forest.get());
		temp.add(new EvoBiome(Biomes.GIANT_TREE_TAIGA, tempA.toArray(new Biome[tempA.size()])));
		EvoBiomes.SNOWY_BIOMES = EvoBiomes.toArray(temp);
		temp.clear();
		
		//Cold Biomes
		tempA = Sets.newHashSet(BOPBiomes.tundra.get(),
				BOPBiomes.tundra.get(),
				BOPBiomes.tundra.get(),
				BOPBiomes.tundra.get(), 
				BOPBiomes.tundra.get(),
				BOPBiomes.cherry_blossom_grove.get());
		temp.add(new EvoBiome(BOPBiomes.tundra.get(), tempA.toArray(new Biome[tempA.size()])));
		tempA = Sets.newHashSet(Biomes.TAIGA, 
				BOPBiomes.boreal_forest.get(),
				BOPBiomes.dead_forest.get(), 
				BOPBiomes.maple_woods.get(), 
				BOPBiomes.boreal_forest.get(),
				BOPBiomes.shield.get(),
				BOPBiomes.seasonal_forest.get());
		temp.add(new EvoBiome(Biomes.TAIGA, tempA.toArray(new Biome[tempA.size()])));
		tempA = Sets.newHashSet(Biomes.GIANT_TREE_TAIGA, 
				Biomes.GIANT_SPRUCE_TAIGA, 
				BOPBiomes.coniferous_forest.get(),
				BOPBiomes.redwood_forest.get(),
				BOPBiomes.ominous_woods.get());
		temp.add(new EvoBiome(Biomes.GIANT_SPRUCE_TAIGA, tempA.toArray(new Biome[tempA.size()])));
		EvoBiomes.COLD_BIOMES = EvoBiomes.toArray(temp);
		temp.clear();
		
		//Warm Biomes
		tempA = Sets.newHashSet(Biomes.PLAINS,
				BOPBiomes.grassland.get(),
				BOPBiomes.shrubland.get(),
				BOPBiomes.flower_meadow.get(),
				Biomes.SUNFLOWER_PLAINS);
		temp.add(new EvoBiome(Biomes.PLAINS, tempA.toArray(new Biome[tempA.size()])));
		tempA = Sets.newHashSet(BOPBiomes.meadow.get(),
				BOPBiomes.lavender_field.get());
		temp.add(new EvoBiome(BOPBiomes.meadow.get(), tempA.toArray(new Biome[tempA.size()])));
		tempA = Sets.newHashSet(Biomes.BIRCH_FOREST,
				BOPBiomes.grove.get(),
				BOPBiomes.orchard.get(),
				Biomes.TALL_BIRCH_FOREST);
		temp.add(new EvoBiome(Biomes.BIRCH_FOREST, tempA.toArray(new Biome[tempA.size()])));
		temp.add(EvoBiomes.FOREST);
		tempA = Sets.newHashSet(Biomes.DARK_FOREST,
				Biomes.DARK_FOREST,
				Biomes.DARK_FOREST,
				BOPBiomes.temperate_rainforest.get(),
				BOPBiomes.temperate_rainforest.get(),
				BOPBiomes.temperate_rainforest.get(),
				BOPBiomes.mystic_grove.get());
		temp.add(new EvoBiome(Biomes.DARK_FOREST, tempA.toArray(new Biome[tempA.size()])));
		EvoBiomes.WARM_BIOMES = EvoBiomes.toArray(temp);
		temp.clear();
		
		//Hot Biomes
		tempA = Sets.newHashSet(Biomes.SAVANNA,
				Biomes.SAVANNA,
				Biomes.SAVANNA,
				BOPBiomes.brushland.get(),
				BOPBiomes.brushland.get(),
				BOPBiomes.brushland.get(),
				Biomes.SHATTERED_SAVANNA);
		temp.add(new EvoBiome(Biomes.SAVANNA, tempA.toArray(new Biome[tempA.size()])));
		tempA = Sets.newHashSet(Biomes.PLAINS,
				BOPBiomes.chaparral.get(),
				BOPBiomes.prairie.get(),
				BOPBiomes.pasture.get());
		temp.add(new EvoBiome(Biomes.PLAINS, tempA.toArray(new Biome[tempA.size()])));
		tempA = Sets.newHashSet(Biomes.FOREST,
				Biomes.FOREST,
				BOPBiomes.woodland.get(),
				BOPBiomes.woodland.get(),
				Biomes.FLOWER_FOREST);
		temp.add(new EvoBiome(Biomes.FOREST, tempA.toArray(new Biome[tempA.size()])));
		tempA = Sets.newHashSet(Biomes.DARK_FOREST,
				BOPBiomes.rainforest.get(),
				BOPBiomes.overgrown_cliffs.get(),
				BOPBiomes.rainforest.get(),
				Biomes.DARK_FOREST);
		temp.add(new EvoBiome(Biomes.DARK_FOREST, tempA.toArray(new Biome[tempA.size()])));
		EvoBiomes.HOT_BIOMES = EvoBiomes.toArray(temp);
		temp.clear();
		
		//Arid Biomes
		tempA = Sets.newHashSet(Biomes.DESERT,
				BOPBiomes.outback.get(),
				Biomes.BADLANDS);
		temp.add(new EvoBiome(Biomes.DESERT, tempA.toArray(new Biome[tempA.size()])));
		tempA = Sets.newHashSet(Biomes.SAVANNA,
				BOPBiomes.brushland.get(),
				Biomes.SHATTERED_SAVANNA);
		temp.add(new EvoBiome(Biomes.SAVANNA, tempA.toArray(new Biome[tempA.size()])));
		tempA = Sets.newHashSet(Biomes.PLAINS,
				BOPBiomes.lush_grassland.get(),
				BOPBiomes.chaparral.get(),
				BOPBiomes.prairie.get(),
				BOPBiomes.pasture.get());
		temp.add(new EvoBiome(Biomes.PLAINS, tempA.toArray(new Biome[tempA.size()])));
		tempA = Sets.newHashSet(Biomes.DARK_FOREST,
				BOPBiomes.rainforest.get(),
				BOPBiomes.overgrown_cliffs.get());
		temp.add(new EvoBiome(Biomes.DARK_FOREST, tempA.toArray(new Biome[tempA.size()])));
		tempA = Sets.newHashSet(Biomes.JUNGLE,
				Biomes.BAMBOO_JUNGLE);
		temp.add(new EvoBiome(Biomes.JUNGLE, tempA.toArray(new Biome[tempA.size()])));
		EvoBiomes.ARID_BIOMES = EvoBiomes.toArray(temp);
		temp.clear();
	}
}
