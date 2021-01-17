package kos.evolutionterraingenerator.world.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import kos.evolutionterraingenerator.util.OctaveOpenSimplexSampler;
import kos.evolutionterraingenerator.world.gen.layer.ClimateLayerSampler;
import kos.evolutionterraingenerator.world.gen.layer.LayerBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;

import java.util.List;

public class EvoBiomeProvider extends BiomeSource
{
    public static final Codec<EvoBiomeProvider> CODEC = RecordCodecBuilder.create((builder) ->
    {
        return builder.group(
            Codec.LONG.fieldOf("seed").stable().forGetter((biomeProvider) -> biomeProvider.seed),
            RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter((biomeProvider) -> biomeProvider.lookupRegistry)
        ).apply(builder, builder.stable(EvoBiomeProvider::new));
    });
    
    private OctaveOpenSimplexSampler tempOctave;
    private OctaveOpenSimplexSampler humidOctave;
    private OctaveOpenSimplexSampler landOctave;
	private OctaveOpenSimplexSampler landOctave2;
    private OctaveOpenSimplexSampler biomeChanceOctave;
    private OctaveOpenSimplexSampler noiseOctave;
    private OctaveOpenSimplexSampler mushroomOctave;
    private OctaveOpenSimplexSampler islandOctave;
    private OctaveOpenSimplexSampler swampChance;
    private OctaveOpenSimplexSampler swampType;
    private double landOffset;
    private final long seed;
    
	public static final double SNOW_TEMP = 0.125;
	public static final double COLD_TEMP = 0.375;
	public static final double WARM_TEMP = 0.625;
	public static final double HOT_TEMP = 0.875;
	
	private double humidityScale = 1.0;
	private double chanceScale = 1.0;
	
	private EvoBiomeProviderSettings providerSettings;
	private final Registry<Biome> lookupRegistry;
	private static final List<RegistryKey<Biome>> BIOMES;
	private EvoBiomeLookup evoBiomes;
	private BiomeMap biomeMap;
	
	public EvoBiomeProvider(long seed, Registry<Biome> lookupRegistry)
	{
		this(new EvoBiomeProviderSettings(), seed, lookupRegistry);
	}
    
	public EvoBiomeProvider(EvoBiomeProviderSettings settingsProvider, long seed, Registry<Biome> lookupRegistry)
	{
		super(BIOMES.stream().map((registryKey) -> {
			return () -> {
				return (Biome)lookupRegistry.getOrThrow(registryKey);
			};
		}));
		
		this.evoBiomes = new EvoBiomeLookup(lookupRegistry);
		this.biomeMap = new BiomeMap(lookupRegistry);
		this.lookupRegistry = lookupRegistry;
		this.seed = seed;
        ChunkRandom rand = new ChunkRandom(seed);
        this.landOctave = new OctaveOpenSimplexSampler(rand, oceanOctaves);
		this.landOctave2 = new OctaveOpenSimplexSampler(rand, oceanOctaves);
		this.tempOctave = new OctaveOpenSimplexSampler(rand, 8);
        this.humidOctave = new OctaveOpenSimplexSampler(rand, 8);
		this.biomeChanceOctave = new OctaveOpenSimplexSampler(rand, 4);
		this.mushroomOctave = new OctaveOpenSimplexSampler(rand, 4);
		this.islandOctave = new OctaveOpenSimplexSampler(rand, 4);
		this.noiseOctave = new OctaveOpenSimplexSampler(rand, 2);
        this.swampChance = new OctaveOpenSimplexSampler(rand, 4);
        this.swampType = new OctaveOpenSimplexSampler(rand, 4);
		this.providerSettings = settingsProvider;
		this.landOffset = 0.0;
		if (landOctave2.sample(0.0, 0.0) * 0.125 / (double)oceanOctaves < oceanThreshold)
		{
			while (landOctave.sample(0.0, landOffset, 0.0) * 0.125 / (double)oceanOctaves < oceanThreshold)
				landOffset += 1.0;
		}
		
		evoBiomes.init();
		/*
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
		*/
	}

	public static final int oceanOctaves = 8;
    public static final double biomeScale = 2.0;
    public static final double oceanScale = 0.0375;
    public static final double oceanThreshold = 0.5;
    public static final double beachThreshold = 0.01;
    public static final double deepThreshold = 0.05;
    
    public static final double riverThreshold = 0.025;
    public static final double riverMidPoint = 0.0;
    public static final double riverScale = 4.0;
    
    public double[] getTemperature(int x, int z)
    {
    	double noise = noiseOctave.sample((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	double d0 = (tempOctave.sample((double)x * (0.0875 / biomeScale), (double)z * (0.0875 / biomeScale)) * 0.00625 + 0.5) * 0.99;
    	double[] arr =
    		{
    				MathHelper.clamp(d0 + 0.01, 0.0, 1.0),
    				MathHelper.clamp(d0 + noise * 0.01, 0.0, 1.0),
    		};
    	return arr;
    }
    
    public double[] getHumidity(int x, int z)
    {
    	double noise = noiseOctave.sample((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	double d0 = (humidOctave.sample((double)x * (0.3 / biomeScale / humidityScale), (double)z * (0.3 / biomeScale / humidityScale)) * 0.0075 + 0.5) * 0.95;
    	double[] arr =
    		{
				MathHelper.clamp(d0 + 0.05, 0.0, 1.0),
    			MathHelper.clamp(d0 + noise * 0.05, 0.0, 1.0),
    		};
    	return arr;
    }
    
    protected double[] getBiomeChance(double x, double z)
    {
    	double noise = noiseOctave.sample((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	double d0 = (biomeChanceOctave.sample((double)x * 0.005 / chanceScale, (double)z * 0.005 / chanceScale) * 0.05 + 0.5) * 0.99;
    	double[] arr =
    		{
    				MathHelper.clamp(d0 + 0.01, 0.0, 1.0),
    				MathHelper.clamp(d0 + noise * 0.01, 0.0, 1.0),
    		};
    	return arr;
    }
    
    public double[] getLandmass(double x, double z)
    {
    	double landmass1 = landOctave.sample((double)x * (0.00125 / oceanScale), landOffset, (double)z * (0.00125 / oceanScale))  * 0.125 / (double)oceanOctaves;
    	double landmass2 = landOctave2.sample((double)x * (0.00125 / oceanScale), (double)z * (0.00125 / oceanScale)) * 0.125 / (double)oceanOctaves;
    	double mushroomChance = (mushroomOctave.sample(x * (0.00375 / biomeScale), z * (0.00375 / biomeScale)) - 8.0) * 2.0 / (double)oceanOctaves;
    	double islandChance = (islandOctave.sample(x * (0.02 / biomeScale), z * (0.02 / biomeScale)) - 3.25) / (double)oceanOctaves;

		double domLand = landmass1;
		if (domLand < landmass2)
			domLand = landmass2;
		if (domLand < islandChance)
			domLand = islandChance;
		if (domLand < mushroomChance)
			domLand = mushroomChance;
		
    	return new double[]{landmass1, landmass2, islandChance, mushroomChance, domLand};
    }
    
    public Biome[] generateLandBiome(int x, int z)
    {
        double[] temperature = getTemperature(x, z);
        double[] humidity = getHumidity(x, z);
		double[] biomeChance = getBiomeChance(x, z);

		return new Biome[] 
				{
						getLandBiome(temperature[0], humidity[0], biomeChance[0]),
						getLandBiome(temperature[1], humidity[1], biomeChance[1])
				};
    }
    
    public Biome generateLandBiome(int x, int z, boolean useNoise)
    {
        double temperature = useNoise ? getTemperature(x, z)[1] : getTemperature(x, z)[0];
        double humidity = useNoise ? getHumidity(x, z)[1] : getHumidity(x, z)[0];
		double biomeChance = useNoise ? getBiomeChance(x, z)[1] : getBiomeChance(x, z)[0];

		return getLandBiome(temperature, humidity, biomeChance);
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
    	    	landmass1 = landOctave.sample(xO * (0.00125 / oceanScale), landOffset, zO * (0.00125 / oceanScale))
    	    			* 0.125 / (double)oceanOctaves;
    	    	landmass2 = landOctave2.sample(xO * (0.00125 / oceanScale), zO * (0.00125 / oceanScale)) 
    	    			* 0.125 / (double)oceanOctaves;
    	    	
    	    	if (landmass1 < oceanThreshold - beachThreshold / (double)oceanOctaves / oceanScale && 
    	    			landmass2 < oceanThreshold - beachThreshold / (double)oceanOctaves / oceanScale)
    	    		return true;
    		}
    	}
    	
    	return false;
	}

    @Override
    public Biome getBiomeForNoiseGen(int x, int y, int z) 
    {
    	return getBiomeForNoiseGen(x << 2, y << 2, z << 2, true); 
    }
    
    public Biome getBiomeForNoiseGen(int x, int y, int z, boolean useNoise) 
    {
    	Biome biome = generateLandBiome(x, z, useNoise);
    	return setBiomebyHeight(biome, x, y, z, useNoise);
    }
    
    public Biome[] getBiomesByHeight(int x, int y, int z)
    {
        double[] temperature = getTemperature(x, z);
        double[] humidity = getHumidity(x, z);
		double[] biomeChance = getBiomeChance(x, z);
		Biome[] biomes = new Biome[2];
		
		biomes[0] = setBiomebyHeight(getLandBiome(temperature[0], humidity[0], biomeChance[0]), x, y, z, temperature[0], humidity[0], biomeChance[0]);
		biomes[1] = setBiomebyHeight(getLandBiome(temperature[1], humidity[1], biomeChance[1]), x, y, z, temperature[1], humidity[1], biomeChance[1]);
		
		return biomes;
    }
    
    public Biome setBiomebyHeight(Biome biome, int x, int y, int z, boolean useNoise)
    {
        double temperature = useNoise ? getTemperature(x, z)[1] : getTemperature(x, z)[0];
        double humidity = useNoise ? getHumidity(x, z)[1] : getHumidity(x, z)[0];
		double biomeChance = useNoise ? getBiomeChance(x, z)[1] : getBiomeChance(x, z)[0];
		
		return setBiomebyHeight(biome, x, y, z, temperature, humidity, biomeChance);
    }
    
    public Biome[] setBiomebyHeight(Biome[] biome, int x, int y, int z)
    {
        double[] temperature = getTemperature(x, z);
        double[] humidity = getHumidity(x, z);
		double[] biomeChance = getBiomeChance(x, z);
		
		biome[0] = setBiomebyHeight(biome[0], x, y, z, temperature[0], humidity[0], biomeChance[0]);
		biome[1] = setBiomebyHeight(biome[1], x, y, z, temperature[1], humidity[1], biomeChance[1]);
		
		return biome;
    }
    
    private Biome setBiomebyHeight(Biome biome, int x, int y, int z, double temperature, double humidity, double biomeChance)
    {
        int seaLevel = this.providerSettings.getSeaLevel();
   	 	double[] landmass = getLandmass(x, z);
   	 	double beachThreshold = oceanThreshold - EvoBiomeProvider.beachThreshold / (double)oceanOctaves / oceanScale;
		boolean isOcean = landmass[4] < beachThreshold;
		boolean isBeach = !isOcean && (landmass[4] < EvoBiomeProvider.oceanThreshold) && canBeBeach(x, z);
		boolean isSpecialIsland = landmass[0] < EvoBiomeProvider.oceanThreshold && landmass[1] < EvoBiomeProvider.oceanThreshold;
		
    	if (landmass[2] == landmass[4] && isSpecialIsland)
    	{
			if (temperature < EvoBiomeProvider.SNOW_TEMP)
				biome = evoBiomes.COLD_ISLANDS.getBiome(biomeChance);
			else if (temperature < EvoBiomeProvider.HOT_TEMP)
				biome = evoBiomes.ISLAND_BIOMES.getBiome(biomeChance);
			else
				biome = evoBiomes.HOT_ISLANDS.getBiome(biomeChance);
    	}
    	if (landmass[3] == landmass[4] && isSpecialIsland)
			biome = decodeBiome(BiomeList.MUSHROOM_FIELDS);
        
        if (isBeach || isOcean)
        {
        	if (y < seaLevel)
        		return getOcean(temperature, y < 40);
        	if (y < seaLevel + 3)
        	{
	    		if (!biome.equals(decodeBiome(BiomeList.BADLANDS)) && 
	    				!biome.equals(decodeBiome(BiomeList.MUSHROOM_FIELDS)) && 
	    				!biome.equals(decodeBiome(BiomeList.DESERT))
	    						)
	    			return getBeach(x, z);
        	}
        }
        
        if (isSpecialIsland && landmass[2] == landmass[4] || landmass[3] == landmass[4])
        	return biome;

        double swampChance = this.swampChance.sample((double)x * 0.0125, (double)z * 0.0125);
        swampChance = MathHelper.clamp(swampChance, 0.0, 1.0);
    	if (temperature > 0.5 && humidity > 0.675 && swampChance < 0.375 - 0.25 * ((MathHelper.clamp(temperature, 0.5, 1.0) - 0.5) * 2.0) && y <= seaLevel + 3)
    	{
            double swampType = this.swampType.sample((double)x * 0.0125, (double)z * 0.0125) * 0.125 + 0.5;
            swampType = MathHelper.clamp(swampType, 0.0, 1.0);
            Biome swamp = null;
            if (temperature < EvoBiomeProvider.WARM_TEMP)
            	swamp = evoBiomes.COLD_SWAMP.getBiome(swampType);
            else if (temperature < EvoBiomeProvider.HOT_TEMP)
              	swamp = evoBiomes.WARM_SWAMP.getBiome(swampType);
            else
            	swamp = evoBiomes.HOT_SWAMP.getBiome(swampType);
            
            if (swamp != null)
            	biome = swamp;
    	}
    	if (biome.equals(decodeBiome(BiomeList.BADLANDS)))
    	{
    		if (y >= seaLevel + 50)
        		biome = decodeBiome(BiomeList.WOODED_BADLANDS_PLATEAU);
    	}
    	/*if (getSettings().isUseBOPBiomes() && temperature < EvoBiomeProvider.SNOW_TEMP && !biome.equals(BiomeList.ICE_SPIKES))
    	{
    		if (y >= seaLevel + 65)
    			biome = BOPBiomes.alps.get();
    		else if (y >= seaLevel + 50)
    			biome = BOPBiomes.alps_foothills.get();
    	}*/
    	return biome;
    }

    public Biome decodeBiome(RegistryKey<Biome> biome) {
    	return this.lookupRegistry.getOrThrow(biome);
	}
    
    public Biome decodeBiome(Identifier biomeId) {
    	return this.lookupRegistry.get(biomeId);
    }

	public Biome getLandBiome(double temp, double humid, double chance)
    {
		EvoBiomeLookup.EvoBiome[] arr = evoBiomes.WARM_BIOMES.toArray(new EvoBiomeLookup.EvoBiome[evoBiomes.WARM_BIOMES.size()]);
    	
		if (temp < SNOW_TEMP)
			arr = evoBiomes.SNOWY_BIOMES.toArray(new EvoBiomeLookup.EvoBiome[evoBiomes.SNOWY_BIOMES.size()]);
		else if (temp < COLD_TEMP)
			arr = evoBiomes.COLD_BIOMES.toArray(new EvoBiomeLookup.EvoBiome[evoBiomes.COLD_BIOMES.size()]);
		else if (temp < WARM_TEMP)
			arr = evoBiomes.WARM_BIOMES.toArray(new EvoBiomeLookup.EvoBiome[evoBiomes.WARM_BIOMES.size()]);
		else if (temp < HOT_TEMP)
			arr = evoBiomes.HOT_BIOMES.toArray(new EvoBiomeLookup.EvoBiome[evoBiomes.HOT_BIOMES.size()]);
		else
			arr = evoBiomes.ARID_BIOMES.toArray(new EvoBiomeLookup.EvoBiome[evoBiomes.ARID_BIOMES.size()]);
		
		return arr[(int)((arr.length - 1) * humid)].getBiome(chance);
    }

	public Biome getBeach(int x, int z)
	{
		double temp = getTemperature(x, z)[1];
		double humid = getHumidity(x, z)[1];
		double chance = getBiomeChance(x, z)[1];
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
        		return decodeBiome(BiomeList.SNOWY_GRAVEL_BEACH);
    		return decodeBiome(BiomeList.SNOWY_BEACH);
    	}
    	if (noDownfall)
    	{
    		if (isGravel)
        		return decodeBiome(BiomeList.DRY_GRAVEL_BEACH);
    		return decodeBiome(BiomeList.DRY_BEACH);
    	}
		if (isGravel)
    		return decodeBiome(BiomeList.GRAVEL_BEACH);
    	return decodeBiome(BiomeList.BEACH);
    }
    
    public Biome getOcean(double temp, boolean deep)
    {
    	if (deep)
    	{		
    		if (temp < SNOW_TEMP)
    			return decodeBiome(BiomeList.DEEP_FROZEN_OCEAN);
    		else if (temp < COLD_TEMP)
    			return decodeBiome(BiomeList.DEEP_COLD_OCEAN);
    		else if (temp < WARM_TEMP)
    			return decodeBiome(BiomeList.DEEP_OCEAN);
    		else if (temp < HOT_TEMP)
    			return decodeBiome(BiomeList.DEEP_LUKEWARM_OCEAN);
    		else
    			return decodeBiome(BiomeList.DEEP_WARM_OCEAN);
    	}
		if (temp < SNOW_TEMP)
			return decodeBiome(BiomeList.FROZEN_OCEAN);
		else if (temp < COLD_TEMP)
			return decodeBiome(BiomeList.COLD_OCEAN);
		else if (temp < WARM_TEMP)
			return decodeBiome(BiomeList.OCEAN);
		else if (temp < HOT_TEMP)
			return decodeBiome(BiomeList.DEEP_LUKEWARM_OCEAN);
		else
			return decodeBiome(BiomeList.WARM_OCEAN);
    }
    
	public EvoBiomeProviderSettings getSettings() 
	{
		return this.providerSettings;
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() { return CODEC; }

	@Override
	public EvoBiomeProvider withSeed(long seed)
	{
		return new EvoBiomeProvider(providerSettings, seed, lookupRegistry);
	}

	public static void register()
	{
	    Registry.register(Registry.BIOME_SOURCE, (String)"environmental_noise", CODEC);
	}
	
	static {
		BIOMES = ImmutableList.of(BiomeKeys.OCEAN, BiomeKeys.PLAINS, BiomeKeys.DESERT, BiomeKeys.MOUNTAINS, BiomeKeys.FOREST, BiomeKeys.TAIGA, BiomeKeys.SWAMP, BiomeKeys.RIVER, BiomeKeys.FROZEN_OCEAN, BiomeKeys.FROZEN_RIVER, BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_MOUNTAINS, new RegistryKey[]{BiomeKeys.MUSHROOM_FIELDS, BiomeKeys.MUSHROOM_FIELD_SHORE, BiomeKeys.BEACH, BiomeKeys.DESERT_HILLS, BiomeKeys.WOODED_HILLS, BiomeKeys.TAIGA_HILLS, BiomeKeys.MOUNTAIN_EDGE, BiomeKeys.JUNGLE, BiomeKeys.JUNGLE_HILLS, BiomeKeys.JUNGLE_EDGE, BiomeKeys.DEEP_OCEAN, BiomeKeys.STONE_SHORE, BiomeKeys.SNOWY_BEACH, BiomeKeys.BIRCH_FOREST, BiomeKeys.BIRCH_FOREST_HILLS, BiomeKeys.DARK_FOREST, BiomeKeys.SNOWY_TAIGA, BiomeKeys.SNOWY_TAIGA_HILLS, BiomeKeys.GIANT_TREE_TAIGA, BiomeKeys.GIANT_TREE_TAIGA_HILLS, BiomeKeys.WOODED_MOUNTAINS, BiomeKeys.SAVANNA, BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.BADLANDS, BiomeKeys.WOODED_BADLANDS_PLATEAU, BiomeKeys.BADLANDS_PLATEAU, BiomeKeys.WARM_OCEAN, BiomeKeys.LUKEWARM_OCEAN, BiomeKeys.COLD_OCEAN, BiomeKeys.DEEP_WARM_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN, BiomeKeys.DEEP_COLD_OCEAN, BiomeKeys.DEEP_FROZEN_OCEAN, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.DESERT_LAKES, BiomeKeys.GRAVELLY_MOUNTAINS, BiomeKeys.FLOWER_FOREST, BiomeKeys.TAIGA_MOUNTAINS, BiomeKeys.SWAMP_HILLS, BiomeKeys.ICE_SPIKES, BiomeKeys.MODIFIED_JUNGLE, BiomeKeys.MODIFIED_JUNGLE_EDGE, BiomeKeys.TALL_BIRCH_FOREST, BiomeKeys.TALL_BIRCH_HILLS, BiomeKeys.DARK_FOREST_HILLS, BiomeKeys.SNOWY_TAIGA_MOUNTAINS, BiomeKeys.GIANT_SPRUCE_TAIGA, BiomeKeys.GIANT_SPRUCE_TAIGA_HILLS, BiomeKeys.MODIFIED_GRAVELLY_MOUNTAINS, BiomeKeys.SHATTERED_SAVANNA, BiomeKeys.SHATTERED_SAVANNA_PLATEAU, BiomeKeys.ERODED_BADLANDS, BiomeKeys.MODIFIED_WOODED_BADLANDS_PLATEAU, BiomeKeys.MODIFIED_BADLANDS_PLATEAU});
	}

	public IndexedIterable<Biome> getRegistry() {
		return this.lookupRegistry;
	}
}
