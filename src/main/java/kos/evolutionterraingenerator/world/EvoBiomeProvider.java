package kos.evolutionterraingenerator.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.gen.feature.structure.Structure;

import com.google.common.collect.Sets;

import kos.evolutionterraingenerator.util.NoiseGeneratorOpenSimplex;
import kos.evolutionterraingenerator.world.biome.EvoBiome;
import kos.evolutionterraingenerator.world.biome.EvoBiomes;
import kos.evolutionterraingenerator.world.biome.NewBiomes;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public class EvoBiomeProvider extends BiomeProvider
{
    private NoiseGeneratorOpenSimplex tempOctave;
    private NoiseGeneratorOpenSimplex humidOctave;
    private NoiseGeneratorOpenSimplex landOctave;
	private NoiseGeneratorOpenSimplex landOctave2;
    private NoiseGeneratorOpenSimplex biomeChanceOctave;
    private NoiseGeneratorOpenSimplex noiseOctave;
    private NoiseGeneratorOpenSimplex mushroomOctave;
    private NoiseGeneratorOpenSimplex riverOctave;
    private NoiseGeneratorOpenSimplex riverOctave2;
    private final Biome[] biomes = new Biome[]{Biomes.OCEAN,
    		Biomes.PLAINS,
    		Biomes.DESERT,
    		Biomes.FOREST,
    		Biomes.TAIGA,
    		Biomes.SWAMP,
    		Biomes.RIVER,
    		Biomes.FROZEN_OCEAN,
    		Biomes.FROZEN_RIVER,
    		Biomes.SNOWY_TUNDRA,
    		Biomes.SNOWY_MOUNTAINS,
    		Biomes.MUSHROOM_FIELDS,
    		Biomes.MUSHROOM_FIELD_SHORE,
    		Biomes.BEACH,
    		Biomes.DESERT_HILLS,
    		Biomes.WOODED_HILLS,
    		Biomes.TAIGA_HILLS,
    		Biomes.MOUNTAIN_EDGE,
    		Biomes.JUNGLE,
    		Biomes.JUNGLE_HILLS,
    		Biomes.JUNGLE_EDGE,
    		Biomes.DEEP_OCEAN,
    		Biomes.STONE_SHORE,
    		Biomes.SNOWY_BEACH,
    		Biomes.BIRCH_FOREST,
    		Biomes.BIRCH_FOREST_HILLS,
    		Biomes.DARK_FOREST,
    		Biomes.SNOWY_TAIGA,
    		Biomes.SNOWY_TAIGA_HILLS,
    		Biomes.GIANT_TREE_TAIGA,
    		Biomes.GIANT_TREE_TAIGA_HILLS,
    		Biomes.WOODED_MOUNTAINS,
    		Biomes.SAVANNA,
    		Biomes.SAVANNA_PLATEAU,
    		Biomes.BADLANDS,
    		Biomes.WOODED_BADLANDS_PLATEAU,
    		Biomes.BADLANDS_PLATEAU,
    		Biomes.WARM_OCEAN,
    		Biomes.LUKEWARM_OCEAN,
    		Biomes.COLD_OCEAN,
    		Biomes.DEEP_WARM_OCEAN,
    		Biomes.DEEP_LUKEWARM_OCEAN,
    		Biomes.DEEP_COLD_OCEAN,
    		Biomes.DEEP_FROZEN_OCEAN,
    		Biomes.SUNFLOWER_PLAINS,
    		Biomes.DESERT_LAKES,
    		Biomes.GRAVELLY_MOUNTAINS,
    		Biomes.FLOWER_FOREST,
    		Biomes.TAIGA_MOUNTAINS,
    		Biomes.SWAMP_HILLS,
    		Biomes.ICE_SPIKES,
    		Biomes.MODIFIED_JUNGLE,
    		Biomes.MODIFIED_JUNGLE_EDGE,
    		Biomes.TALL_BIRCH_FOREST,
    		Biomes.TALL_BIRCH_HILLS,
    		Biomes.DARK_FOREST_HILLS,
    		Biomes.SNOWY_TAIGA_MOUNTAINS,
    		Biomes.GIANT_SPRUCE_TAIGA,
    		Biomes.GIANT_SPRUCE_TAIGA_HILLS,
    		Biomes.MODIFIED_GRAVELLY_MOUNTAINS,
    		Biomes.SHATTERED_SAVANNA,
    		Biomes.SHATTERED_SAVANNA_PLATEAU,
    		Biomes.ERODED_BADLANDS,
    		Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU,
    		Biomes.MODIFIED_BADLANDS_PLATEAU,
    		NewBiomes.TUNDRA,
    		NewBiomes.GRAVELLY_TUNDRA,
    		NewBiomes.TUNDRA_WOODED,
    		NewBiomes.GRAVEL_BEACH,
    		NewBiomes.SNOWY_GRAVEL_BEACH,
    		NewBiomes.SNOWY_GIANT_TREE_TAIGA,
    		NewBiomes.SNOWY_GIANT_SPRUCE_TAIGA,
    		NewBiomes.RAINFOREST};
    /** A list of biomes that the player can spawn in. */
	
	public static final double SNOW_TEMP = 0.125;
	public static final double COLD_TEMP = 0.375;
	public static final double WARM_TEMP = 0.625;
	public static final double HOT_TEMP = 0.875;

	public EvoBiomeProvider(OverworldBiomeProviderSettings settingsProvider, IWorld worldIn) {
		super();
        
        Random rand = new Random(worldIn.getSeed());
        tempOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 4);
		humidOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 4);
		landOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 8);
		landOctave2 = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 8);
		biomeChanceOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 2);
		noiseOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 2);
		mushroomOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 2);
		riverOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 8);
		riverOctave2 = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 8);
		EvoBiomes.init();
	}

    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height)
    {
    	return getBiomesForGeneration(biomes, x, z, width, height, 1, 1);
    }

    public Biome getBiome(int x, int z) {
    	return generateBiome((double)x, (double)z);
    }
    
    public double getTemperature(double x, double z)
    {
    	double noise = noiseOctave.getNoise((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	return MathHelper.clamp((tempOctave.getNoise(x * (0.0045 / biomeScale), z * (0.0045 / biomeScale)) * 0.1 + 0.5) * 0.99 + noise * 0.01, 0.0, 1.0);
    }
    
    public double getHumidity(double x, double z)
    {
    	double noise = noiseOctave.getNoise((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	return MathHelper.clamp((humidOctave.getNoise((double)x * (0.035 / biomeScale), (double)z * (0.035 / biomeScale)) * 0.1 + 0.5) * 0.95 + noise * 0.05, 0.0, 1.0);
    }
    
    public Biome func_222366_b(int x, int z) {
    	return generateBiome(x, z);
     }
	
    public static final double biomeScale = 3.0;
    public static final double oceanScale = 0.025;
    public static final double oceanThreshold = 4.0;
    public static final double beachThreshold = 0.0175;
    public static final double deepThreshold = 0.375;
    
    public static final double riverThreshold = 0.025;
    public static final double riverMidPoint = 0.0;
    public static final double riverScale = 6.0;
    
    public boolean getRiver(int x, int z)
    {
    	double riverChance = riverOctave.getNoise((double)x * (0.025 / riverScale), (double)z * (0.025 / riverScale)) * 0.125;
    	double riverChance2 = riverOctave2.getNoise((double)x * (0.025 / riverScale), (double)z * (0.025 / riverScale)) * 0.125;
    	return (riverChance >= riverMidPoint - riverThreshold && riverChance <= riverMidPoint + riverThreshold) ||
    			(riverChance2 >= riverMidPoint - riverThreshold && riverChance2 <= riverMidPoint + riverThreshold);
    }
    
    public Biome generateBiome(double x, double z)
    {
    	double noise = noiseOctave.getNoise((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	double temperature = getTemperature(x, z);
    	double humidity = getHumidity(x, z);
    	double landmass1 = landOctave.getNoise((double)x * (0.00125 / oceanScale), (double)z * (0.00125 / oceanScale))  * 0.125 + 0.875;
    	double landmass2 = landOctave2.getNoise((double)x * (0.00125 / oceanScale), (double)z * (0.00125 / oceanScale)) * 0.125 + 0.875;
    	double biomeChance = (biomeChanceOctave.getNoise((double)x * 0.0025, (double)z * 0.0025) * 0.25 + 0.5) * 0.99 + noise * 0.01;
    	double mushroomChance = mushroomOctave.getNoise(x * (0.00375 / biomeScale), z * (0.00375 / biomeScale)) * 0.25 + 0.5;
    	
    	temperature = MathHelper.clamp(temperature, 0.0, 1.0);
		humidity = MathHelper.clamp(humidity, 0.0, 1.0);
		biomeChance = MathHelper.clamp(biomeChance, 0.0, 1.0);

		Biome biome = getLandBiome(temperature, humidity, biomeChance);
		if (landmass1 < oceanThreshold && landmass2 < oceanThreshold)
		{
			if (mushroomChance > 1.05 && landmass1 < oceanThreshold * 0.85 && landmass2 < oceanThreshold * 0.85)
			{
				if (mushroomChance >= 1.0)
					biome = Biomes.MUSHROOM_FIELDS;
				else
					biome = getOcean(temperature, humidity, false);
			}
			else
			{
				if (landmass1 > oceanThreshold - beachThreshold / oceanScale || landmass2 > oceanThreshold - beachThreshold / oceanScale)
				{
					if (!(biome.equals(Biomes.BADLANDS) | biome.equals(Biomes.DESERT)))
						biome = getBeach(temperature, humidity, landmass1 <= oceanThreshold - 0.025 / oceanScale);
				}
				else
					biome = getOcean(temperature, humidity, landmass1 < oceanThreshold * deepThreshold && landmass2 < oceanThreshold * deepThreshold);
			}
		}
		
    	return biome;
    }
    
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height, int xScale, int zScale)
    {
        if (biomes == null || biomes.length < width * height)
            biomes = new Biome[width * height];
        
    	for (int i = 0; i < width; i++)
    	{
   			for (int j = 0; j < height; j++)
   			{
   	    		int k = j * height + i;
   	    		biomes[k] = generateBiome((double)x * xScale, (double)z * zScale);
   			}
   		}
   		return biomes;
    }

    private Biome getLandBiome(double temp, double humid, double chance)
    {
    	EvoBiome[] arr = EvoBiomes.WARM_BIOMES;
    	
		if (temp < SNOW_TEMP)
			arr = EvoBiomes.SNOWY_BIOMES;
		else if (temp < COLD_TEMP)
			arr = EvoBiomes.COLD_BIOMES;
		else if (temp < WARM_TEMP)
			arr = EvoBiomes.WARM_BIOMES;
		else if (temp < HOT_TEMP)
			arr = EvoBiomes.HOT_BIOMES;
		else
			arr = EvoBiomes.ARID_BIOMES;
		
		return arr[(int)((arr.length - 1) * humid)].getBiome(chance);
    }


    private Biome getBeach(double temp, double humid, boolean isGravel)
    {
    	if (temp < SNOW_TEMP)
    	{
    		if (isGravel)
    			return NewBiomes.SNOWY_GRAVEL_BEACH;
    		return Biomes.SNOWY_BEACH;
    	}
    	if (isGravel)
    		return NewBiomes.GRAVEL_BEACH;
    	return Biomes.BEACH;
    }
    
    private Biome getOcean(double temp, double humid, boolean deep)
    {
    	if (deep)
    	{		
    		if (temp < SNOW_TEMP)
    			return Biomes.DEEP_FROZEN_OCEAN;
    		else if (temp < COLD_TEMP)
    			return Biomes.DEEP_COLD_OCEAN;
    		else if (temp < WARM_TEMP)
    			return Biomes.DEEP_LUKEWARM_OCEAN;
    		else
    			return Biomes.DEEP_WARM_OCEAN;
    	}
		if (temp < SNOW_TEMP)
			return Biomes.FROZEN_OCEAN;
		else if (temp < COLD_TEMP)
			return Biomes.COLD_OCEAN;
		else if (temp < WARM_TEMP)
			return Biomes.LUKEWARM_OCEAN;
		else
			return Biomes.WARM_OCEAN;
    }
    
    public Set<Biome> getBiomesInSquare(int centerX, int centerZ, int sideLength) {
        int i = centerX - sideLength >> 2;
        int j = centerZ - sideLength >> 2;
        int k = centerX + sideLength >> 2;
        int l = centerZ + sideLength >> 2;
        int i1 = k - i + 1;
        int j1 = l - j + 1;
        Set<Biome> set = Sets.newHashSet();
        Collections.addAll(set, getBiomesForGeneration(null, i, j, i1, j1, 4, 4));
        return set;
     }

    @Nullable
    public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random)
    {
        int i = x - range >> 2;
        int j = z - range >> 2;
        int k = x + range >> 2;
        int l = z + range >> 2;
        int i1 = k - i + 1;
        int j1 = l - j + 1;
        Biome[] abiome = getBiomesForGeneration(null, i, j, i1, j1, 4, 4);
        BlockPos blockpos = null;
        int k1 = 0;

        for(int l1 = 0; l1 < i1 * j1; ++l1) {
           int i2 = i + l1 % i1 << 2;
           int j2 = j + l1 / i1 << 2;
           if (biomes.contains(abiome[l1])) {
              if (blockpos == null || random.nextInt(k1 + 1) == 0) {
                 blockpos = new BlockPos(i2, 0, j2);
              }

              ++k1;
           }
        }

        return blockpos;
    }

	public Biome[] getBiomes(int x, int z, int width, int length, boolean cacheFlag) {
		return getBiomesForGeneration(null, x, z, width, length);
	}

	@Override
	public boolean hasStructure(Structure<?> structureIn) 
	{
		return this.hasStructureCache.computeIfAbsent(structureIn, (p_205006_1_) -> 
		{
			for(Biome biome : this.biomes)
			{
	            if (biome.hasStructure(p_205006_1_))
	            {
	               return true;
	            }
	         }

	         return false;
	      });
	   }

	@Override
	   public Set<BlockState> getSurfaceBlocks() {
	      if (this.topBlocksCache.isEmpty()) {
	         for(Biome biome : this.biomes) {
	            this.topBlocksCache.add(biome.getSurfaceBuilderConfig().getTop());
	         }
	      }

	      return this.topBlocksCache;
	   }
}
