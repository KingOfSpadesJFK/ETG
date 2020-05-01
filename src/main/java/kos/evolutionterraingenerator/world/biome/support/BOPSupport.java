package kos.evolutionterraingenerator.world.biome.support;

import java.util.ArrayList;

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
		Biome[] snowyPlains = {Biomes.SNOWY_TUNDRA, Biomes.SNOWY_TUNDRA, Biomes.ICE_SPIKES, 
				Biomes.SNOWY_TUNDRA, Biomes.SNOWY_TUNDRA, 
				BOPBiomes.cold_desert.get()};
		temp.add(new EvoBiome(Biomes.SNOWY_TAIGA, snowyPlains));
		Biome[] snowyForests = {Biomes.SNOWY_TAIGA, 
				BOPBiomes.snowy_forest.get(),
				BOPBiomes.snowy_fir_clearing.get(),
				Biomes.SNOWY_TAIGA, 
				BOPBiomes.snowy_forest.get(), 
				BOPBiomes.snowy_fir_clearing.get(),
				Biomes.SNOWY_TAIGA, 
				BOPBiomes.snowy_forest.get(), 
				BOPBiomes.snowy_fir_clearing.get(),};
		temp.add(new EvoBiome(Biomes.SNOWY_TAIGA, snowyForests));
		Biome[] lushSnowyForests = {Biomes.GIANT_TREE_TAIGA,
				BOPBiomes.snowy_coniferous_forest.get(),
				Biomes.GIANT_TREE_TAIGA, 
				BOPBiomes.snowy_coniferous_forest.get(),
				Biomes.GIANT_TREE_TAIGA, 
				BOPBiomes.snowy_coniferous_forest.get(),
				Biomes.GIANT_TREE_TAIGA, 
				BOPBiomes.snowy_coniferous_forest.get(),
				Biomes.GIANT_SPRUCE_TAIGA, 
				Biomes.GIANT_SPRUCE_TAIGA};
		temp.add(new EvoBiome(Biomes.GIANT_TREE_TAIGA, lushSnowyForests));
		EvoBiomes.SNOWY_BIOMES = EvoBiomes.toArray(temp);
		temp.clear();
		
		//Cold Biomes
		Biome[] tundraArr = {BOPBiomes.tundra.get(),
				BOPBiomes.tundra.get(),
				BOPBiomes.tundra.get(),
				BOPBiomes.tundra.get(), 
				BOPBiomes.cherry_blossom_grove.get()};
		temp.add(new EvoBiome(BOPBiomes.shrubland.get(), tundraArr));
		Biome[] coldHumid = {NewBiomes.TUNDRA_WOODED, 
				Biomes.TAIGA, 
				BOPBiomes.boreal_forest.get(),
				BOPBiomes.dead_forest.get(), BOPBiomes.maple_woods.get(), BOPBiomes.boreal_forest.get()};
		temp.add(new EvoBiome(Biomes.TAIGA, coldHumid));
		Biome[] giantTrees = {Biomes.GIANT_TREE_TAIGA, 
				BOPBiomes.coniferous_forest.get(), 
				Biomes.GIANT_SPRUCE_TAIGA, 
				BOPBiomes.coniferous_forest.get(), 
				Biomes.GIANT_TREE_TAIGA, 
				BOPBiomes.coniferous_forest.get(), 
				Biomes.GIANT_SPRUCE_TAIGA, 
				BOPBiomes.coniferous_forest.get()};
		temp.add(new EvoBiome(Biomes.GIANT_SPRUCE_TAIGA, giantTrees));
		EvoBiomes.COLD_BIOMES = EvoBiomes.toArray(temp);
	}
}
