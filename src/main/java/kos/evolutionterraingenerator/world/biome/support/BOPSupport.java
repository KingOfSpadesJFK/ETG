package kos.evolutionterraingenerator.world.biome.support;

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
		EvoBiomes.SNOWY_BIOMES.clear();
		EvoBiomes.SNOWY_BIOMES.add(new EvoBiome(BOPBiomes.cold_desert.get(), new Biome[]
				{
					Biomes.ICE_SPIKES, 
					BOPBiomes.snowy_fir_clearing.get(),
					BOPBiomes.snowy_fir_clearing.get(),
					Biomes.SNOWY_TUNDRA,
					Biomes.SNOWY_TUNDRA, 
					BOPBiomes.snowy_fir_clearing.get(),
					BOPBiomes.snowy_fir_clearing.get(),
					BOPBiomes.cold_desert.get()
				}));
		EvoBiomes.SNOWY_BIOMES.add(new EvoBiome(Biomes.SNOWY_TAIGA, new Biome[]
				{
					Biomes.SNOWY_TAIGA, 
					BOPBiomes.snowy_forest.get(),
					Biomes.SNOWY_TAIGA, 
					BOPBiomes.snowy_forest.get(), 
					Biomes.SNOWY_TAIGA, 
					BOPBiomes.snowy_forest.get()
				}));
		EvoBiomes.SNOWY_BIOMES.add(new EvoBiome(Biomes.GIANT_TREE_TAIGA, new Biome[]
				{
					NewBiomes.SNOWY_GIANT_TREE_TAIGA,
					NewBiomes.SNOWY_GIANT_SPRUCE_TAIGA,
					BOPBiomes.snowy_coniferous_forest.get()
				}));
		//EvoBiomes.SNOWY_BIOMES.sort(null);
		
		//Cold Biomes
		EvoBiomes.COLD_BIOMES.clear();
		EvoBiomes.COLD_BIOMES.add(new EvoBiome(BOPBiomes.tundra.get(), new Biome[]
				{
					BOPBiomes.tundra.get(),
					BOPBiomes.tundra.get(),
					BOPBiomes.tundra.get(),
					BOPBiomes.tundra.get(), 
					BOPBiomes.tundra.get(),
					BOPBiomes.cherry_blossom_grove.get()
				}));
		EvoBiomes.COLD_BIOMES.add(new EvoBiome(Biomes.TAIGA, new Biome[]
				{
					Biomes.TAIGA,
					BOPBiomes.fir_clearing.get(),
					BOPBiomes.shield.get(),
					BOPBiomes.dead_forest.get()
				}));
		EvoBiomes.COLD_BIOMES.add(new EvoBiome(Biomes.TAIGA, new Biome[]
				{
					Biomes.TAIGA, 
					BOPBiomes.boreal_forest.get(),
					BOPBiomes.maple_woods.get(), 
					BOPBiomes.seasonal_forest.get()
				}));
		EvoBiomes.COLD_BIOMES.add(new EvoBiome(Biomes.GIANT_SPRUCE_TAIGA, new Biome[]
				{
						Biomes.GIANT_TREE_TAIGA, 
					Biomes.GIANT_SPRUCE_TAIGA, 
					BOPBiomes.coniferous_forest.get(),
					BOPBiomes.redwood_forest.get(),
					BOPBiomes.ominous_woods.get()
				}));
		//EvoBiomes.COLD_BIOMES.sort(null);
		
		//Warm Biomes
		EvoBiomes.WARM_BIOMES.clear();
		EvoBiomes.WARM_BIOMES.add(new EvoBiome(Biomes.PLAINS, new Biome[]
				{
					BOPBiomes.grassland.get(),
					BOPBiomes.shrubland.get(),
					Biomes.PLAINS,
					BOPBiomes.highland_moor.get(),
					Biomes.SUNFLOWER_PLAINS
				}));
		EvoBiomes.WARM_BIOMES.add(new EvoBiome(BOPBiomes.meadow.get(), new Biome[]
				{
					BOPBiomes.meadow.get(),
					BOPBiomes.flower_meadow.get(),
					BOPBiomes.lavender_field.get()
				}));
		EvoBiomes.WARM_BIOMES.add(new EvoBiome(Biomes.BIRCH_FOREST, new Biome[]
				{
					Biomes.BIRCH_FOREST,
					BOPBiomes.grove.get(),
					BOPBiomes.orchard.get(),
					Biomes.TALL_BIRCH_FOREST
				}));
		EvoBiomes.WARM_BIOMES.add(EvoBiomes.FOREST);
		EvoBiomes.WARM_BIOMES.add(new EvoBiome(Biomes.DARK_FOREST, new Biome[]
				{
					Biomes.DARK_FOREST,
					Biomes.DARK_FOREST,
					Biomes.DARK_FOREST,
					BOPBiomes.temperate_rainforest.get(),
					BOPBiomes.temperate_rainforest.get(),
					BOPBiomes.temperate_rainforest.get(),
					BOPBiomes.mystic_grove.get()
				}));
		EvoBiomes.WARM_BIOMES.sort(null);
		
		//Hot Biomes
		EvoBiomes.HOT_BIOMES.clear();
		EvoBiomes.HOT_BIOMES.add(new EvoBiome(Biomes.SAVANNA, new Biome[]
				{
					Biomes.SAVANNA,
					Biomes.SAVANNA,
					Biomes.SAVANNA,
					Biomes.SHATTERED_SAVANNA
				}));
		EvoBiomes.HOT_BIOMES.add(new EvoBiome(Biomes.PLAINS, new Biome[]
				{
					Biomes.PLAINS,
					BOPBiomes.chaparral.get(),
					BOPBiomes.prairie.get(),
					BOPBiomes.pasture.get()
				}));
		EvoBiomes.HOT_BIOMES.add(new EvoBiome(Biomes.FOREST, new Biome[]
				{
					Biomes.FOREST,
					Biomes.FOREST,
					BOPBiomes.woodland.get(),
					BOPBiomes.woodland.get(),
					Biomes.FLOWER_FOREST
				}));
		EvoBiomes.HOT_BIOMES.add(new EvoBiome(Biomes.DARK_FOREST, new Biome[]
				{
					Biomes.DARK_FOREST,
					BOPBiomes.rainforest.get(),
					BOPBiomes.overgrown_cliffs.get(),
					BOPBiomes.rainforest.get(),
					Biomes.DARK_FOREST
				}));
		EvoBiomes.HOT_BIOMES.sort(null);
		
		//Arid Biomes
		EvoBiomes.ARID_BIOMES.clear();
		EvoBiomes.ARID_BIOMES.add(new EvoBiome(Biomes.DESERT, new Biome[]
				{
					Biomes.DESERT,
					Biomes.DESERT,
					BOPBiomes.outback.get(),
					BOPBiomes.outback.get(),
					Biomes.BADLANDS,
					Biomes.BADLANDS,
					BOPBiomes.wasteland.get()
				}));
		EvoBiomes.ARID_BIOMES.add(new EvoBiome(Biomes.SAVANNA, new Biome[]
				{
					Biomes.SAVANNA,
					Biomes.SAVANNA,
					BOPBiomes.brushland.get(),
					BOPBiomes.brushland.get(),
					Biomes.SHATTERED_SAVANNA
				}));
		EvoBiomes.ARID_BIOMES.add(new EvoBiome(BOPBiomes.chaparral.get(), new Biome[]
				{
					BOPBiomes.lush_grassland.get(),
					BOPBiomes.chaparral.get(),
					BOPBiomes.prairie.get(),
					BOPBiomes.pasture.get()
				}));
		EvoBiomes.ARID_BIOMES.add(new EvoBiome(Biomes.DARK_FOREST, new Biome[]
				{
					Biomes.DARK_FOREST,
					BOPBiomes.tropical_rainforest.get(),
					BOPBiomes.overgrown_cliffs.get()
				}));
		EvoBiomes.ARID_BIOMES.add(new EvoBiome(Biomes.JUNGLE, new Biome[]
				{
					Biomes.JUNGLE,
					Biomes.BAMBOO_JUNGLE
				}));
		EvoBiomes.ARID_BIOMES.sort(null);
		
		//Cold Swamps
		EvoBiomes.COLD_SWAMP = new EvoBiome(Biomes.SWAMP, new Biome[]
				{
					Biomes.SWAMP,
					BOPBiomes.bog.get(),
					Biomes.SWAMP,
					BOPBiomes.marsh.get()
				});
		
		//Warm Swamps
		EvoBiomes.WARM_SWAMP = new EvoBiome(Biomes.SWAMP, new Biome[]
				{
					Biomes.SWAMP,
					BOPBiomes.mangrove.get(),
					Biomes.SWAMP,
					BOPBiomes.bayou.get()
				});

		//Hot Swamps
		EvoBiomes.HOT_SWAMP = new EvoBiome(BOPBiomes.lush_swamp.get(), new Biome[]
				{
					BOPBiomes.lush_swamp.get(),
					BOPBiomes.bayou.get(),
					BOPBiomes.lush_swamp.get(),
					BOPBiomes.bayou.get()
				});
	}
}
