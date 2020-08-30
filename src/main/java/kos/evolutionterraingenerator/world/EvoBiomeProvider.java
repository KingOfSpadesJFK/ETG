package kos.evolutionterraingenerator.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.feature.structure.Structure;

import com.google.common.collect.Sets;

import biomesoplenty.api.biome.BOPBiomes;
import kos.evolutionterraingenerator.EvolutionTerrainGenerator;
import kos.evolutionterraingenerator.util.NoiseGeneratorOpenSimplex;
import kos.evolutionterraingenerator.world.biome.EvoBiome;
import kos.evolutionterraingenerator.world.biome.EvoBiomes;
import kos.evolutionterraingenerator.world.biome.NewBiomes;
import kos.evolutionterraingenerator.world.biome.support.BOPSupport;

import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;

public class EvoBiomeProvider extends OverworldBiomeProvider
{
    private NoiseGeneratorOpenSimplex tempOctave;
    private NoiseGeneratorOpenSimplex humidOctave;
    private NoiseGeneratorOpenSimplex landOctave;
	private NoiseGeneratorOpenSimplex landOctave2;
    private NoiseGeneratorOpenSimplex biomeChanceOctave;
    private NoiseGeneratorOpenSimplex noiseOctave;
    private NoiseGeneratorOpenSimplex mushroomOctave;
    private NoiseGeneratorOpenSimplex islandOctave;
    private NoiseGeneratorOpenSimplex riverOctave;
    private NoiseGeneratorOpenSimplex riverOctave2;
    private NoiseGeneratorOpenSimplex swampChance;
    private NoiseGeneratorOpenSimplex swampType;
    private double landOffset;
    private final Set<Biome> biomes = Sets.newHashSet(Biomes.OCEAN,
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
    		NewBiomes.GRAVEL_BEACH,
    		NewBiomes.DRY_GRAVEL_BEACH,
    		NewBiomes.SNOWY_GRAVEL_BEACH,
    		NewBiomes.DRY_BEACH,
    		NewBiomes.SNOWY_GIANT_TREE_TAIGA,
    		NewBiomes.SNOWY_GIANT_SPRUCE_TAIGA,
    		NewBiomes.RAINFOREST);
	
	public static final double SNOW_TEMP = 0.125;
	public static final double COLD_TEMP = 0.375;
	public static final double WARM_TEMP = 0.625;
	public static final double HOT_TEMP = 0.875;
	
	private double humidityScale = 1.0;
	private double chanceScale = 1.0;
	
	private EvoBiomeProviderSettings providerSettings;

	public EvoBiomeProvider(EvoBiomeProviderSettings settingsProvider) {
		super(settingsProvider);
        
        SharedSeedRandom rand = new SharedSeedRandom(settingsProvider.getWorldInfo().getSeed());
        this.landOctave = new NoiseGeneratorOpenSimplex(rand, oceanOctaves - 1, 0);
		this.landOctave2 = new NoiseGeneratorOpenSimplex(rand, oceanOctaves - 1, 0);
		this.riverOctave = new NoiseGeneratorOpenSimplex(rand, 7, 0);
		this.riverOctave2 = new NoiseGeneratorOpenSimplex(rand, 7, 0);
		this.tempOctave = new NoiseGeneratorOpenSimplex(rand, 7, 0);
        this.humidOctave = new NoiseGeneratorOpenSimplex(rand, 7, 0);
		this.biomeChanceOctave = new NoiseGeneratorOpenSimplex(rand, 3, 0);
		this.mushroomOctave = new NoiseGeneratorOpenSimplex(rand, 3, 0);
		this.islandOctave = new NoiseGeneratorOpenSimplex(rand, 3, 0);
		this.noiseOctave = new NoiseGeneratorOpenSimplex(rand, 1, 0);
        this.swampChance = new NoiseGeneratorOpenSimplex(rand, 3, 0);
        this.swampType = new NoiseGeneratorOpenSimplex(rand, 3, 0);
		this.providerSettings = settingsProvider;
		this.landOffset = 0.0;
		if (landOctave2.getNoise(0.0, 0.0) * 0.125 / (double)oceanOctaves < oceanThreshold)
		{
			while (landOctave.getNoise(0.0, landOffset, 0.0) * 0.125 / (double)oceanOctaves < oceanThreshold)
				landOffset += 1.0;
		}
		
		EvoBiomes.init();
		try
		{
			if (providerSettings.isUseBOPBiomes())
			{
				humidityScale = 1.0;
				chanceScale = 1.0;
				BOPSupport.setup(biomes);
			}
		}
		catch (Exception e)
		{
			humidityScale = 1.0;
			chanceScale = 1.0;
			EvoBiomes.init();
			this.providerSettings.setUseBOPBiomes(false);
			EvolutionTerrainGenerator.logger.error("ETG: Biomes O' Plenty support broken! Check if the right version of any of the mods are listed");
		}
	}

	public static final int oceanOctaves = 8;
    public static final double biomeScale = 3.0;
    public static final double oceanScale = 0.0375;
    public static final double oceanThreshold = 0.5;
    public static final double beachThreshold = 0.01;
    public static final double deepThreshold = 0.05;
    
    public static final double riverThreshold = 0.025;
    public static final double riverMidPoint = 0.0;
    public static final double riverScale = 4.0;
    
    public double getTemperature(double x, double z)
    {
    	return getTemperature(x, z, true);
    }
    
    public double getHumidity(double x, double z)
    {
    	return getHumidity(x, z, true);
    }
    
    public double getTemperature(double x, double z, boolean useNoise)
    {
    	double noise = 1.0;
    	if (useNoise)
    		noise = noiseOctave.getNoise((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	return MathHelper.clamp((tempOctave.getNoise(x * (0.0875 / biomeScale), z * (0.0875 / biomeScale)) * 0.00625 + 0.5) * 0.99 + noise * 0.01, 0.0, 1.0);
    }
    
    public double getHumidity(double x, double z, boolean useNoise)
    {
    	double noise = 1.0;
    	if (useNoise)
    		noise = noiseOctave.getNoise((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	return MathHelper.clamp((humidOctave.getNoise((double)x * (0.3 / biomeScale / humidityScale), (double)z * (0.3 / biomeScale / humidityScale)) * 0.0075 + 0.5) * 0.95 + noise * 0.05, 0.0, 1.0);
    }
    
    public boolean getRiver(int x, int z)
    {
    	double riverChance = riverOctave.getNoise((double)x * (0.025 / riverScale), (double)z * (0.025 / riverScale)) * 0.125;
    	double riverChance2 = riverOctave2.getNoise((double)x * (0.025 / riverScale), (double)z * (0.025 / riverScale)) * 0.125;
    	return (riverChance >= riverMidPoint - riverThreshold && riverChance <= riverMidPoint + riverThreshold) ||
    			(riverChance2 >= riverMidPoint - riverThreshold && riverChance2 <= riverMidPoint + riverThreshold);
    }
    
    protected double[] getLandmass(double x, double z)
    {
    	double landmass1 = landOctave.getNoise((double)x * (0.00125 / oceanScale), landOffset, (double)z * (0.00125 / oceanScale))  * 0.125 / (double)oceanOctaves;
    	double landmass2 = landOctave2.getNoise((double)x * (0.00125 / oceanScale), (double)z * (0.00125 / oceanScale)) * 0.125 / (double)oceanOctaves;
    	double mushroomChance = (mushroomOctave.getNoise(x * (0.00375 / biomeScale), z * (0.00375 / biomeScale)) - 8.0) * 2.0 / (double)oceanOctaves;
    	double islandChance = (islandOctave.getNoise(x * (0.02 / biomeScale), z * (0.02 / biomeScale)) - 3.25) / (double)oceanOctaves;

		double domLand = landmass1;
		if (domLand < landmass2)
			domLand = landmass2;
		if (domLand < islandChance)
			domLand = islandChance;
		if (domLand < mushroomChance)
			domLand = mushroomChance;
		
    	return new double[]{landmass1, landmass2, islandChance, mushroomChance, domLand};
    }
    
    protected double getBiomeChance(double x, double z, boolean useNoise)
    {
    	double noise = 1.0;
    	if (useNoise)
    		noise = noiseOctave.getNoise((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	return MathHelper.clamp((biomeChanceOctave.getNoise((double)x * 0.005 / chanceScale, (double)z * 0.005 / chanceScale) * 0.05 + 0.5) * 0.99 + noise * 0.01, 0.0, 1.0);
    }
    
    public Biome generateLandBiome(double x, double z, boolean useNoise)
    {
    	double temperature = getTemperature(x, z, useNoise);
    	double humidity = getHumidity(x, z, useNoise);
    	double biomeChance = getBiomeChance(x, z, useNoise);

		return getLandBiome(temperature, humidity, biomeChance);
    }
    
    public Biome generateBiome(double x, double z, boolean useNoise)
    {
    	double temperature = getTemperature(x, z, useNoise);
    	double humidity = getHumidity(x, z, useNoise);
    	double landmass1 = landOctave.getNoise((double)x * (0.00125 / oceanScale), landOffset, (double)z * (0.00125 / oceanScale))  * 0.125 / (double)oceanOctaves;
    	double landmass2 = landOctave2.getNoise((double)x * (0.00125 / oceanScale), (double)z * (0.00125 / oceanScale)) * 0.125 / (double)oceanOctaves;
    	double biomeChance = getBiomeChance(x, z, useNoise);

		Biome biome = getLandBiome(temperature, humidity, biomeChance);
		if (landmass1 < oceanThreshold && landmass2 < oceanThreshold)
		{
	    	double mushroomChance = (mushroomOctave.getNoise(x * (0.00375 / biomeScale), z * (0.00375 / biomeScale)) - 8.0) * 2.0 / (double)oceanOctaves;
	    	double islandChance = (islandOctave.getNoise(x * (0.02 / biomeScale), z * (0.02 / biomeScale)) - 3.25) / (double)oceanOctaves;
			if (mushroomChance > oceanThreshold - beachThreshold / (double)oceanOctaves / oceanScale)
			{
				biome = Biomes.MUSHROOM_FIELDS;
			} 
			else if (islandChance > oceanThreshold - beachThreshold / (double)oceanOctaves / oceanScale)
			{
				if (temperature < SNOW_TEMP)
					biome = EvoBiomes.COLD_ISLANDS.getBiome(biomeChance);
				else if (temperature < HOT_TEMP)
					biome = EvoBiomes.ISLAND_BIOMES.getBiome(biomeChance);
				else
					biome = EvoBiomes.HOT_ISLANDS.getBiome(biomeChance);
				if (islandChance < oceanThreshold)
				{
					if (this.providerSettings.isUseBOPBiomes())
					{
						if (biome == BOPBiomes.origin_hills.get())
							biome = BOPBiomes.origin_beach.get();
						if (biome == BOPBiomes.volcano.get())
							biome = BOPBiomes.volcano_edge.get();
						if (biome == BOPBiomes.tropics.get() || biome == Biomes.JUNGLE)
							biome = BOPBiomes.white_beach.get();
					}
					else
						biome = Biomes.BEACH;
				}
			}
			else if (landmass1 > oceanThreshold - beachThreshold / (double)oceanOctaves / oceanScale || 
					landmass2 > oceanThreshold - beachThreshold / (double)oceanOctaves  / oceanScale)
			{
				if (canBeBeach(x, z))
				{
					biome = getBeach(temperature, 
								humidity, 
								landmass1 <= oceanThreshold - beachThreshold / (double)oceanOctaves / oceanScale, 
								biome.getDownfall() <= 0.0);
				}
			}
			else
				biome = getOcean(temperature, landmass1 < deepThreshold &&
						landmass2 < deepThreshold &&
						islandChance < deepThreshold &&
						mushroomChance < deepThreshold);
		}
		return biome;
    }
    
	private static final int BEACH_SAMPLES = 4;
	private static final double BEACH_SEARCH_SCALE = 2.0;
	public boolean canBeBeach(double x, double z)
	{
    	double landmass1 = 0;
    	double landmass2 = 0;
		double xO = 0;
		double zO = 0;
		
    	for (int i = 0; i <= BEACH_SAMPLES; i++)
    	{
    		for (int j = 0; j <= BEACH_SAMPLES; j ++)
    		{
    			xO = (double)(i - BEACH_SAMPLES / 2) * BEACH_SEARCH_SCALE + x;
    			zO = (double)(j - BEACH_SAMPLES / 2) * BEACH_SEARCH_SCALE + z;
    	    	landmass1 = landOctave.getNoise(xO * (0.00125 / oceanScale), landOffset, zO * (0.00125 / oceanScale))
    	    			* 0.125 / (double)oceanOctaves;
    	    	landmass2 = landOctave2.getNoise(xO * (0.00125 / oceanScale), zO * (0.00125 / oceanScale)) 
    	    			* 0.125 / (double)oceanOctaves;
    	    	
    	    	if (landmass1 < oceanThreshold - beachThreshold / (double)oceanOctaves / oceanScale && 
    	    			landmass2 < oceanThreshold - beachThreshold / (double)oceanOctaves / oceanScale)
    	    		return true;
    		}
    	}
    	
    	return false;
	}

    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height)
    {
    	return getBiomesForGeneration(biomes, x, z, width, height, 1, 1, true);
    }

    @Override
    public Biome getNoiseBiome(int x, int y, int z) 
    {
    	return getNoiseBiome(x << 2, y << 2, z << 2, true); 
    }
    
    public Biome getNoiseBiome(int x, int y, int z, boolean useNoise) 
    {
    	Biome biome = generateLandBiome((double)x, (double)z, useNoise);
    	return setBiomebyHeight(biome, x, y, z, useNoise);
    }
    
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height, int xScale, int zScale, boolean useNoise)
    {
   		return getBiomesForGeneration(biomes, x, z, width, height, xScale, zScale, useNoise, false);
    }
    
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height, int xScale, int zScale, boolean useNoise, boolean landOnly)
    {
        if (biomes == null || biomes.length < width * height)
            biomes = new Biome[width * height];
        
    	for (int i = 0; i < width; i++)
    	{
   			for (int j = 0; j < height; j++)
   			{
   	    		int k = j * height + i;
   	    		if (landOnly)
   	   	    		biomes[k] = generateLandBiome((double)(x + i) * xScale, (double)(z + j) * zScale, useNoise);
   	    		else
   	    			biomes[k] = generateBiome((double)(x + i) * xScale, (double)(z + j) * zScale, useNoise);
   			}
   		}
   		return biomes;
    }

    public Biome getLandBiome(double temp, double humid, double chance)
    {
    	EvoBiome[] arr = EvoBiomes.WARM_BIOMES.toArray(new EvoBiome[EvoBiomes.WARM_BIOMES.size()]);
    	
		if (temp < SNOW_TEMP)
			arr = EvoBiomes.SNOWY_BIOMES.toArray(new EvoBiome[EvoBiomes.SNOWY_BIOMES.size()]);
		else if (temp < COLD_TEMP)
			arr = EvoBiomes.COLD_BIOMES.toArray(new EvoBiome[EvoBiomes.COLD_BIOMES.size()]);
		else if (temp < WARM_TEMP)
			arr = EvoBiomes.WARM_BIOMES.toArray(new EvoBiome[EvoBiomes.WARM_BIOMES.size()]);
		else if (temp < HOT_TEMP)
			arr = EvoBiomes.HOT_BIOMES.toArray(new EvoBiome[EvoBiomes.HOT_BIOMES.size()]);
		else
			arr = EvoBiomes.ARID_BIOMES.toArray(new EvoBiome[EvoBiomes.ARID_BIOMES.size()]);
		
		return arr[(int)((arr.length - 1) * humid)].getBiome(chance);
    }

	public Biome getBeach(double x, double z)
	{
		double temp = getTemperature(x, z);
		double humid = getHumidity(x, z);
		double chance = getBiomeChance(x, z, true);
		double[] landmass = getLandmass(x, z);
		return getBeach(temp, humid, 
				landmass[0] < landmass[1], 
				getLandBiome(temp, humid, chance).getDownfall() <= 0.0F);
	}

    public Biome getBeach(double temp, double humid, boolean isGravel, boolean noDownfall)
    {
    	if (temp < SNOW_TEMP)
    	{
    		if (isGravel)
    			return NewBiomes.SNOWY_GRAVEL_BEACH;
    		return Biomes.SNOWY_BEACH;
    	}
    	if (noDownfall)
    	{
        	if (isGravel)
        		return NewBiomes.DRY_GRAVEL_BEACH;
    		return NewBiomes.DRY_BEACH;
    	}
    	if (isGravel)
    		return NewBiomes.GRAVEL_BEACH;
    	return Biomes.BEACH;
    }
    
    public Biome getOcean(double temp, boolean deep)
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

    private Biome setBiomebyHeight(Biome biome, int x, int y, int z, boolean useNoise)
    {
        int seaLevel = this.providerSettings.getSeaLevel();
        double temperature = this.getTemperature(x, z);
        double humidity = this.getHumidity(x, z);
   	 	double[] landmass = this.getLandmass(x, z);
   	 	double beachThreshold = EvoBiomeProvider.oceanThreshold - EvoBiomeProvider.beachThreshold / (double)EvoBiomeProvider.oceanOctaves / EvoBiomeProvider.oceanScale;
		boolean isOcean = landmass[4] < beachThreshold;
		boolean isBeach = !isOcean && (landmass[4] < EvoBiomeProvider.oceanThreshold) && this.canBeBeach(x, z);
		boolean isSpecialIsland = landmass[0] < EvoBiomeProvider.oceanThreshold && landmass[1] < EvoBiomeProvider.oceanThreshold;
		
    	if (landmass[2] == landmass[4] && isSpecialIsland)
    	{
    		double biomeChance = this.getBiomeChance(x, z, useNoise);
			if (temperature < EvoBiomeProvider.SNOW_TEMP)
				biome = EvoBiomes.COLD_ISLANDS.getBiome(biomeChance);
			else if (temperature < EvoBiomeProvider.HOT_TEMP)
				biome = EvoBiomes.ISLAND_BIOMES.getBiome(biomeChance);
			else
				biome = EvoBiomes.HOT_ISLANDS.getBiome(biomeChance);
    	}
    	if (landmass[3] == landmass[4] && isSpecialIsland)
			biome = Biomes.MUSHROOM_FIELDS;
        
        if (isBeach || isOcean)
        {
        	if (y < seaLevel - 3)
        		return this.getOcean(temperature, y < 40);
        	if (y < seaLevel + 3)
        	{
        		if (this.getSettings().isUseBOPBiomes() && 
        				(landmass[0] == landmass[4] || landmass[2] == landmass[4]) && 
        				(biome.equals(Biomes.JUNGLE) ||
        						biome.equals(Biomes.BAMBOO_JUNGLE) ||
        						biome.equals(BOPBiomes.tropics.get()) ||
        						biome.equals(BOPBiomes.tropical_rainforest.get()) )
        				)
        			return BOPBiomes.white_beach.get();
        		if (this.getSettings().isUseBOPBiomes() && biome.equals(BOPBiomes.volcano.get()))
        			return BOPBiomes.volcano_edge.get();
        		if (this.getSettings().isUseBOPBiomes() && biome.equals(BOPBiomes.origin_hills.get()))
        			return BOPBiomes.origin_beach.get();
	    		if (!biome.equals(Biomes.BADLANDS) && 
	    				!biome.equals(Biomes.MUSHROOM_FIELDS) && 
	    				!biome.equals(Biomes.DESERT) && 
	    				!(this.getSettings().isUseBOPBiomes() && 
	    						(biome.equals(BOPBiomes.outback.get()) || 
	    								biome.equals(BOPBiomes.xeric_shrubland.get()) || 
	    								biome.equals(BOPBiomes.wasteland.get()) || 
	    								biome.equals(BOPBiomes.cold_desert.get()) )
	    						)
	    				)
	    			return this.getBeach(x, z);
        	}
        }
        
        if (isSpecialIsland && landmass[2] == landmass[4] || landmass[3] == landmass[4])
        	return biome;

        double swampChance = this.swampChance.getNoise((double)x * 0.0125, (double)z * 0.0125);
        swampChance = MathHelper.clamp(swampChance, 0.0, 1.0);
    	if (temperature > 0.5 && humidity > 0.675 && swampChance < 0.375 - 0.25 * ((MathHelper.clamp(temperature, 0.5, 1.0) - 0.5) * 2.0) && y <= seaLevel + 3)
    	{
            double swampType = this.swampType.getNoise((double)x * 0.0125, (double)z * 0.0125) * 0.125 + 0.5;
            swampType = MathHelper.clamp(swampType, 0.0, 1.0);
            Biome swamp = null;
            if (temperature < EvoBiomeProvider.WARM_TEMP)
            	swamp = EvoBiomes.COLD_SWAMP.getBiome(swampType);
            else if (temperature < EvoBiomeProvider.HOT_TEMP)
              	swamp = EvoBiomes.WARM_SWAMP.getBiome(swampType);
            else
            	swamp = EvoBiomes.HOT_SWAMP.getBiome(swampType);
            
            if (swamp != null)
            	biome = swamp;
    	}
    	if (biome.equals(Biomes.BADLANDS))
    	{
    		if (y >= seaLevel + 50)
        		biome = Biomes.WOODED_BADLANDS_PLATEAU;
    	}
    	if (this.providerSettings.isUseBOPBiomes() && temperature < EvoBiomeProvider.SNOW_TEMP && !biome.equals(Biomes.ICE_SPIKES))
    	{
    		if (y >= seaLevel + 65)
    			biome = BOPBiomes.alps.get();
    		else if (y >= seaLevel + 50)
    			biome = BOPBiomes.alps_foothills.get();
    	}
    	return biome;
    }

	public Biome[] getBiomes(int x, int z, int width, int length, boolean cacheFlag) {
		return getBiomesForGeneration(null, x, z, width, length);
	}
	


	   /**
	    * Returns the set of biomes contained in cube of side length 2 * radius + 1 centered at (xIn, yIn, zIn)
	    */
	   public Set<Biome> getBiomes(int xIn, int yIn, int zIn, int radius) {
	      int i = xIn - radius;
	      int j = yIn - radius;
	      int k = zIn - radius;
	      int l = xIn + radius;
	      int i1 = yIn + radius;
	      int j1 = zIn + radius;
	      int k1 = l - i + 1;
	      int l1 = i1 - j + 1;
	      int i2 = j1 - k + 1;
	      Set<Biome> set = Sets.newHashSet();

	      for(int j2 = 0; j2 < i2; ++j2) {
	         for(int k2 = 0; k2 < k1; ++k2) {
	            for(int l2 = 0; l2 < l1; ++l2) {
	               int i3 = i + k2;
	               int j3 = j + l2;
	               int k3 = k + j2;
	               set.add(this.getNoiseBiome(i3, j3, k3, false));
	            }
	         }
	      }

	      return set;
	   }

	   @Nullable
	   public BlockPos func_225531_a_(int xIn, int yIn, int zIn, int radiusIn, List<Biome> biomesIn, Random randIn) {
	      int i = xIn - radiusIn;
	      int j = zIn - radiusIn;
	      int k = xIn + radiusIn;
	      int l = zIn + radiusIn;
	      int i1 = k - i + 1;
	      int j1 = l - j + 1;
	      int k1 = yIn;
	      BlockPos blockpos = null;
	      int l1 = 0;

	      for(int i2 = 0; i2 < j1; ++i2) {
	         for(int j2 = 0; j2 < i1; ++j2) {
	            int k2 = i + j2;
	            int l2 = j + i2;
	            if (biomesIn.contains(this.getNoiseBiome(k2, k1, l2, false))) {
	               if (blockpos == null || randIn.nextInt(l1 + 1) == 0) {
	                  blockpos = new BlockPos(k2, yIn, l2);
	               }

	               ++l1;
	            }
	         }
	      }

	      return blockpos;
	   }

	@Override
	public boolean hasStructure(Structure<?> structureIn) 
	{
		return this.hasStructureCache.computeIfAbsent(structureIn, (p_205006_1_) -> 
		{
			for(Biome biome : this.biomes)
			{
	            if (biome.hasStructure(p_205006_1_))
	               return true;
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
	
	public EvoBiomeProviderSettings getSettings() 
	{
		return this.providerSettings;
	}
}
