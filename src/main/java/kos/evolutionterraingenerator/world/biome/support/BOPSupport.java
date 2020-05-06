package kos.evolutionterraingenerator.world.biome.support;

import biomesoplenty.api.biome.BOPBiomes;

import kos.evolutionterraingenerator.world.biome.EvoBiomes;
import kos.evolutionterraingenerator.world.biome.EvoBiome;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class BOPSupport 
{
	public static final String BOP_MODID = "biomesoplenty";
	
	public static void setup()
	{
		//Snowy Biomes
		EvoBiomes.SNOWY_BIOMES.remove(EvoBiomes.ICE_PLAINS);
		EvoBiomes.SNOWY_BIOMES.add(new EvoBiome(Biomes.SNOWY_TUNDRA, 0.125F, new Biome[]
				{
					Biomes.ICE_SPIKES, 
					Biomes.SNOWY_TUNDRA,
					Biomes.SNOWY_TUNDRA,
					Biomes.SNOWY_TUNDRA, 
					BOPBiomes.cold_desert.get()
				}));
		EvoBiomes.SNOWY_BIOMES.add(new EvoBiome(BOPBiomes.fir_clearing.get(), 0.275F, null));
		EvoBiomes.SNOWY_BIOMES.remove(EvoBiomes.SNOWY_TAIGA);
		EvoBiomes.SNOWY_BIOMES.add(new EvoBiome(Biomes.SNOWY_TAIGA, new Biome[]
				{
					Biomes.SNOWY_TAIGA,
					BOPBiomes.snowy_forest.get(),
				}));
		EvoBiomes.SNOWY_BIOMES.add(new EvoBiome(BOPBiomes.snowy_coniferous_forest.get(), 0.9F));
		EvoBiomes.SNOWY_BIOMES.sort(null);
		
		//Cold Biomes
		EvoBiomes.COLD_BIOMES.remove(EvoBiomes.TUNDRA);
		EvoBiomes.COLD_BIOMES.remove(EvoBiomes.WOODED_TUNDRA);
		EvoBiomes.COLD_BIOMES.add(new EvoBiome(BOPBiomes.tundra.get(), 0.03F, new Biome[]
				{
					BOPBiomes.tundra.get(),
					BOPBiomes.tundra.get(),
					BOPBiomes.tundra.get(),
					BOPBiomes.tundra.get(), 
					BOPBiomes.tundra.get(),
					BOPBiomes.cherry_blossom_grove.get()
				}));
		EvoBiomes.COLD_BIOMES.add(new EvoBiome(BOPBiomes.fir_clearing.get(), 0.45F, new Biome[]
				{
					BOPBiomes.dead_forest.get(),
					BOPBiomes.fir_clearing.get(),
					BOPBiomes.fir_clearing.get(),
					BOPBiomes.fir_clearing.get(),
					BOPBiomes.dead_forest.get(),
				}));
		EvoBiomes.COLD_BIOMES.add(new EvoBiome(BOPBiomes.shield.get(), 0.625F));
		EvoBiomes.COLD_BIOMES.remove(EvoBiomes.TAIGA);
		EvoBiomes.COLD_BIOMES.add(new EvoBiome(Biomes.TAIGA, new Biome[]
				{
					Biomes.TAIGA, 
					BOPBiomes.boreal_forest.get(),
					BOPBiomes.maple_woods.get(), 
					BOPBiomes.seasonal_forest.get()
				}));
		EvoBiomes.COLD_BIOMES.add(new EvoBiome(BOPBiomes.coniferous_forest.get(), 0.85F));
		EvoBiomes.COLD_BIOMES.add(new EvoBiome(BOPBiomes.redwood_forest.get(), 0.95F, new Biome[]
				{
					BOPBiomes.ominous_woods.get(),
					BOPBiomes.redwood_forest.get(),
					BOPBiomes.redwood_forest.get(),
					BOPBiomes.redwood_forest.get(),
					BOPBiomes.ominous_woods.get()
				}));
		EvoBiomes.COLD_BIOMES.sort(null);
		
		//Warm Biomes
		EvoBiomes.WARM_BIOMES.add(new EvoBiome(BOPBiomes.grassland.get(), 0.5F));
		EvoBiomes.WARM_BIOMES.add(new EvoBiome(BOPBiomes.highland.get(), 0.475F, new Biome[]
				{
					BOPBiomes.highland.get(),
					BOPBiomes.highland_moor.get(),
					BOPBiomes.highland.get(),
					BOPBiomes.highland_moor.get(),
					BOPBiomes.highland.get(),
					BOPBiomes.highland_moor.get(),
				}));
		EvoBiomes.WARM_BIOMES.add(new EvoBiome(BOPBiomes.meadow.get(), 0.45F, new Biome[]
				{
					BOPBiomes.lavender_field.get(),
					BOPBiomes.meadow.get(),
					BOPBiomes.meadow.get(),
					BOPBiomes.flower_meadow.get()
				}));
		EvoBiomes.WARM_BIOMES.add(new EvoBiome(BOPBiomes.orchard.get(), 0.55F));
		EvoBiomes.WARM_BIOMES.add(new EvoBiome(BOPBiomes.grove.get(), 0.575F));
		EvoBiomes.WARM_BIOMES.add(new EvoBiome(BOPBiomes.temperate_rainforest.get(), new Biome[]
				{
					BOPBiomes.temperate_rainforest.get(),
					BOPBiomes.temperate_rainforest.get(),
					BOPBiomes.temperate_rainforest.get(),
					BOPBiomes.mystic_grove.get()
				}));
		EvoBiomes.WARM_BIOMES.sort(null);
		
		//Hot Biomes
		EvoBiomes.HOT_BIOMES.add(new EvoBiome(BOPBiomes.shrubland.get(), 0.55F));
		EvoBiomes.HOT_BIOMES.add(new EvoBiome(BOPBiomes.chaparral.get(), 0.65F));
		EvoBiomes.HOT_BIOMES.add(new EvoBiome(BOPBiomes.prairie.get(), 0.5F, new Biome[]
				{
					BOPBiomes.prairie.get(),
					BOPBiomes.pasture.get(),
					BOPBiomes.prairie.get(),
					BOPBiomes.pasture.get(),
					BOPBiomes.prairie.get(),
					BOPBiomes.pasture.get(),
				}));
		EvoBiomes.HOT_BIOMES.add(new EvoBiome(BOPBiomes.overgrown_cliffs.get(), 0.7F));
		EvoBiomes.HOT_BIOMES.add(new EvoBiome(BOPBiomes.woodland.get(), 0.75F));
		EvoBiomes.HOT_BIOMES.add(new EvoBiome(BOPBiomes.rainforest.get(), 0.9F));
		EvoBiomes.HOT_BIOMES.remove(EvoBiomes.RAINFOREST);
		EvoBiomes.HOT_BIOMES.sort(null);
		
		//Arid Biomes
		EvoBiomes.ARID_BIOMES.remove(EvoBiomes.DESERT);
		EvoBiomes.ARID_BIOMES.add(new EvoBiome(Biomes.DESERT, 0.0F, new Biome[]
				{
					Biomes.BADLANDS,
					BOPBiomes.outback.get(),
					BOPBiomes.xeric_shrubland.get(),
					Biomes.DESERT,
					Biomes.DESERT,
					BOPBiomes.xeric_shrubland.get(),
					BOPBiomes.outback.get(),
					BOPBiomes.wasteland.get()
				}));
		EvoBiomes.ARID_BIOMES.remove(EvoBiomes.SAVANNA);
		EvoBiomes.ARID_BIOMES.add(new EvoBiome(Biomes.SAVANNA, 0.015F, new Biome[]
				{
					Biomes.SAVANNA,
					BOPBiomes.brushland.get(),
					Biomes.SAVANNA,
					BOPBiomes.brushland.get(),
					Biomes.SHATTERED_SAVANNA,
				}));
		EvoBiomes.ARID_BIOMES.remove(EvoBiomes.PLAINS);
		EvoBiomes.ARID_BIOMES.add(new EvoBiome(BOPBiomes.shrubland.get(), 0.3F, new Biome[] 
				{
					BOPBiomes.shrubland.get(),
					Biomes.PLAINS,
					BOPBiomes.shrubland.get(),
					Biomes.PLAINS,
				}));
		EvoBiomes.ARID_BIOMES.add(new EvoBiome(BOPBiomes.chaparral.get(), 0.4F));
		EvoBiomes.ARID_BIOMES.add(new EvoBiome(BOPBiomes.lush_grassland.get(), 0.5F, new Biome[]
				{
					BOPBiomes.overgrown_cliffs.get(),
					BOPBiomes.lush_grassland.get(),
					BOPBiomes.overgrown_cliffs.get(),
					BOPBiomes.lush_grassland.get(),
				}));
		EvoBiomes.ARID_BIOMES.remove(EvoBiomes.RAINFOREST_ROOFED);
		EvoBiomes.ARID_BIOMES.add(new EvoBiome(BOPBiomes.tropical_rainforest.get(), 0.8F));
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
					BOPBiomes.mangrove.get(),
					BOPBiomes.lush_swamp.get(),
					BOPBiomes.bayou.get()
				});

		//Hot Swamps
		EvoBiomes.ISLAND_BIOMES = new EvoBiome(BOPBiomes.lush_swamp.get(), new Biome[]
				{
					Biomes.PLAINS,
					BOPBiomes.volcano.get(),
					Biomes.FOREST,
					BOPBiomes.tropics.get(),
					Biomes.JUNGLE,
					BOPBiomes.origin_hills.get(),
				});
	}
}
