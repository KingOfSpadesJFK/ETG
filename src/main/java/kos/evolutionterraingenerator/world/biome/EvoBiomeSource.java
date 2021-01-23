package kos.evolutionterraingenerator.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import kos.evolutionterraingenerator.util.PositionalCache;
import kos.evolutionterraingenerator.util.noise.OctaveOpenSimplexSampler;
import kos.evolutionterraingenerator.world.biome.container.BiomeContainer;
import kos.evolutionterraingenerator.world.biome.selector.BiomeSelector;
import kos.evolutionterraingenerator.world.gen.layer.LayerBuilder;
import kos.evolutionterraingenerator.world.gen.layer.TerrainLayerSampler;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

public class EvoBiomeSource extends BiomeSource
{
    public static final Codec<EvoBiomeSource> CODEC = RecordCodecBuilder.create((builder) ->
    {
        return builder.group(
            Codec.LONG.fieldOf("seed").stable().forGetter((biomeProvider) -> biomeProvider.seed),
            RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter((biomeProvider) -> biomeProvider.lookupRegistry)
        ).apply(builder, builder.stable(EvoBiomeSource::new));
    });
    
    private OctaveOpenSimplexSampler tempOctave;
    private OctaveOpenSimplexSampler humidOctave;
    private OctaveOpenSimplexSampler landOctave;
	private OctaveOpenSimplexSampler landOctave2;
    private OctaveOpenSimplexSampler weirdnessOctave;
    private OctaveOpenSimplexSampler noiseOctave;
    private OctaveOpenSimplexSampler mushroomOctave;
    private OctaveOpenSimplexSampler islandOctave;
    private ChunkGenerator chunkGen;
    private double landOffset;
    private final long seed;
	private TerrainLayerSampler swampLayer;
    
	public static final double SNOW_TEMP = 0.125;
	public static final double COLD_TEMP = 0.375;
	public static final double WARM_TEMP = 0.625;
	public static final double HOT_TEMP = 0.875;
	
	private double humidityScale = 1.0;
	private double chanceScale = 1.0;
	
	private EvoBiomeSourceSettings providerSettings;
	private final Registry<Biome> lookupRegistry;
	private static final List<Identifier> BIOMES;
	private BiomeSelector landBiomeSelector;
	private BiomeSelector swampSelector;
	private BiomeSelector islandSelector;
	
	private PositionalCache<Biome[]> biomeCache;
	private PositionalCache<ClimateData> climateCache;
	
	public EvoBiomeSource(long seed, Registry<Biome> lookupRegistry)
	{
		this(new EvoBiomeSourceSettings(), seed, lookupRegistry);
	}
    
	public EvoBiomeSource(EvoBiomeSourceSettings settingsProvider, long seed, Registry<Biome> lookupRegistry)
	{
		super(BIOMES.stream().map((registryKey) -> {
			return () -> {
				return (Biome)lookupRegistry.get(registryKey);
			};
		}));
		
		this.biomeCache = new PositionalCache<Biome[]>();
		this.climateCache = new PositionalCache<ClimateData>();
		this.landBiomeSelector = BiomeSelector.createLandSelector();
		this.swampSelector = BiomeSelector.createSwampSelector();
		this.islandSelector = BiomeSelector.createLandSelector();
		this.lookupRegistry = lookupRegistry;
		this.seed = seed;
        ChunkRandom rand = new ChunkRandom(seed);
        this.landOctave = new OctaveOpenSimplexSampler(rand, oceanOctaves);
		this.landOctave2 = new OctaveOpenSimplexSampler(rand, oceanOctaves);
		this.tempOctave = new OctaveOpenSimplexSampler(rand, 8);
        this.humidOctave = new OctaveOpenSimplexSampler(rand, 8);
		this.weirdnessOctave = new OctaveOpenSimplexSampler(rand, 4);
		this.mushroomOctave = new OctaveOpenSimplexSampler(rand, 4);
		this.islandOctave = new OctaveOpenSimplexSampler(rand, 4);
		this.noiseOctave = new OctaveOpenSimplexSampler(rand, 2);
		this.swampLayer = LayerBuilder.build(seed, LayerBuilder.SWAMP);
		this.providerSettings = settingsProvider;
		this.landOffset = 0.0;
		if (landOctave2.sample(0.0, 0.0) * 0.125 / (double)oceanOctaves < oceanThreshold)
		{
			while (landOctave.sample(0.0, landOffset, 0.0) * 0.125 / (double)oceanOctaves < oceanThreshold)
				landOffset += 1.0;
		}
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
    	if (climateCache.containsKey(x, z)) {
    		return climateCache.getOrThrow(x, z).temperature;
    	}
    	double noise = noiseOctave.sample((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	double d0 = (tempOctave.sample((double)x * (0.035 / biomeScale), (double)z * (0.0875 / biomeScale)) * 0.00625 + 0.5) * 0.99;
    	double[] arr =
    		{
    				MathHelper.clamp(d0 + 0.01, 0.0, 1.0),
    				MathHelper.clamp(d0 + noise * 0.01, 0.0, 1.0),
    		};
    	return arr;
    }
    
    public double[] getHumidity(int x, int z)
    {
    	if (climateCache.containsKey(x, z)) {
    		return climateCache.getOrThrow(x, z).humidity;
    	}
    	double noise = noiseOctave.sample((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	double d0 = (humidOctave.sample((double)x * (0.3 / biomeScale / humidityScale), (double)z * (0.3 / biomeScale / humidityScale)) * 0.0075 + 0.5) * 0.95;
    	double[] arr =
    		{
				MathHelper.clamp(d0 + 0.05, 0.0, 1.0),
    			MathHelper.clamp(d0 + noise * 0.05, 0.0, 1.0),
    		};
    	return arr;
    }
    
    protected double[] getWeirdness(int x, int z)
    {
    	if (climateCache.containsKey(x, z)) {
    		return climateCache.getOrThrow(x, z).weirdness;
    	}
    	double noise = noiseOctave.sample((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	double d0 = (weirdnessOctave.sample((double)x * 0.005 / chanceScale, (double)z * 0.005 / chanceScale) * 0.05 + 0.5) * 0.99;
    	double[] arr =
    		{
    				MathHelper.clamp(d0 + 0.01, 0.0, 1.0),
    				MathHelper.clamp(d0 + noise * 0.01, 0.0, 1.0),
    		};
    	return arr;
    }
    
    public double[] getLandmass(int x, int z)
    {
    	if (climateCache.containsKey(x, z)) {
    		return climateCache.getOrThrow(x, z).landmass;
    	}
    	double landmass1 = landOctave.sample((double)x * (0.00125 / oceanScale), landOffset, (double)z * (0.00125 / oceanScale))  * 0.125 / (double)oceanOctaves;
    	double landmass2 = landOctave2.sample((double)x * (0.00125 / oceanScale), (double)z * (0.00125 / oceanScale)) * 0.125 / (double)oceanOctaves;
    	double mushroomChance = (mushroomOctave.sample((double)x * (0.00375 / biomeScale), (double)z * (0.00375 / biomeScale)) - 8.0) * 2.0 / (double)oceanOctaves;
    	double islandChance = (islandOctave.sample((double)x * (0.02 / biomeScale), (double)z * (0.02 / biomeScale)) - 3.25) / (double)oceanOctaves;

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
		double[] biomeChance = getWeirdness(x, z);

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
		double biomeChance = useNoise ? getWeirdness(x, z)[1] : getWeirdness(x, z)[0];

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
    public Biome getBiomeForNoiseGen(int x, int y, int z) {
    	return getBiome(x << 2, y << 2, z << 2, true); 
    }
    
    public Biome[] getBiome(int x, int y, int z) 
    {
    	Biome[] biome = getBiomesByHeight(x, y, z);
    	return biome;
    }
    
    public Biome getBiome(int x, int y, int z, boolean useNoise) {
    	return useNoise ? getBiome(x, y, z)[1] : getBiome(x, y, z)[0];
    }
    
    public Biome[] getBiomesByHeight(int x, int y, int z)
    {
        double[] temperature = getTemperature(x, z);
        double[] humidity = getHumidity(x, z);
		double[] biomeChance = getWeirdness(x, z);
   	 	double[] landmass = getLandmass(x, z);
		Biome[] biomes;
		
		if (biomeCache.containsKey(x, y, z)) {
			biomes = biomeCache.getOrThrow(x, y, z);
		} else {
			biomes = new Biome[2];
			biomes[0] = setBiomebyHeight(getLandBiome(temperature[0], humidity[0], biomeChance[0]), x, y, z, temperature[0], humidity[0], biomeChance[0], landmass);
			biomes[1] = setBiomebyHeight(getLandBiome(temperature[1], humidity[1], biomeChance[1]), x, y, z, temperature[1], humidity[1], biomeChance[1], landmass);
			biomeCache.add(x, y, z, biomes);
		}

		if (!climateCache.containsKey(x, z)) {
			ClimateData cd = 
	    			new ClimateData(temperature,
	    			humidity,
	    			biomeChance,
	    			landmass);
			climateCache.add(x, z, 0, cd);
		}
		return biomes;
    }
    
    private Biome setBiomebyHeight(Biome biome, int x, int y, int z, double temperature, double humidity, double biomeChance, double[] landmass)
    {
        int seaLevel = this.providerSettings.getSeaLevel();
   	 	double beachThreshold = oceanThreshold - EvoBiomeSource.beachThreshold / (double)oceanOctaves / oceanScale;
		boolean isOcean = landmass[4] < beachThreshold;
		boolean isBeach = !isOcean && (landmass[4] < EvoBiomeSource.oceanThreshold) && canBeBeach(x, z);
		boolean isSpecialIsland = landmass[0] < EvoBiomeSource.oceanThreshold && landmass[1] < EvoBiomeSource.oceanThreshold;
		
    	if (landmass[2] == landmass[4] && isSpecialIsland) {
    		biome = decodeBiome(this.islandSelector.pick(temperature, humidity, biomeChance).getID());
    	}
    	if (landmass[3] == landmass[4] && isSpecialIsland)
			biome = decodeBiome(BiomeList.MUSHROOM_FIELDS);
        
        if (isBeach || isOcean)
        {
        	if (y < seaLevel - 5)
        		return getOcean(temperature, y < 40);
        	if (y < seaLevel + 3) {
    			return getBeach(x, z);
        	}
        }
        
        if (isSpecialIsland && landmass[2] == landmass[4] || landmass[3] == landmass[4])
        	return biome;
        
    	if (humidity > 0.675 && this.swampLayer.sample(x, z) == 1 && y <= seaLevel + 6)
    	{
            Biome swamp = this.decodeBiome(this.swampSelector.pick(temperature, humidity, biomeChance).getID());
            if (swamp != null)
            	biome = swamp;
    	}
    	if (biome.equals(decodeBiome(BiomeList.BADLANDS)))
    	{
    		if (y >= seaLevel + 50)
        		biome = decodeBiome(BiomeList.WOODED_BADLANDS_PLATEAU);
    	}
    	return biome;
    }

    public Biome decodeBiome(RegistryKey<Biome> biome) {
    	return this.lookupRegistry.getOrThrow(biome);
	}
    
    public Biome decodeBiome(Identifier biomeId) {
    	if (biomeId == null)
    		return null;
    	return this.lookupRegistry.get(biomeId);
    }

	public Biome getLandBiome(double temp, double humid, double chance) {
		return this.decodeBiome(landBiomeSelector.pick(temp, humid, chance).getID());
    }

	public Biome getBeach(int x, int z)
	{
		double temp = getTemperature(x, z)[1];
		double humid = getHumidity(x, z)[1];
		double chance = getWeirdness(x, z)[1];
		double[] landmass = getLandmass(x, z);
		BiomeContainer bc = this.landBiomeSelector.pick(temp, humid, chance);
		return decodeBiome(landmass[0] < landmass[1] ? bc.getSecondaryBeach() : bc.getPrimaryBeach());
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
    
	public EvoBiomeSourceSettings getSettings() {
		return this.providerSettings;
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() { return CODEC; }

	@Override
	public EvoBiomeSource withSeed(long seed) {
		return new EvoBiomeSource(providerSettings, seed, lookupRegistry);
	}

	public static void register() {
	    Registry.register(Registry.BIOME_SOURCE, (String)"environmental_noise", CODEC);
	}
	
	public void setChunkGenerator(ChunkGenerator chunkGen) {
		this.chunkGen = chunkGen;
	}

	public Set<Biome> getBiomesInArea(int x, int y, int z, int radius) {
		return getBiomesInArea(x, y, z, radius, true);
	}
	
	private Set<Biome> getBiomesInArea(int x, int y, int z, int radius, boolean heightDependent)
	{
		int i = x - radius >> 2;
		int height = heightDependent ? this.chunkGen.getHeight(x, z, Heightmap.Type.OCEAN_FLOOR_WG) : y;
		int j = height - radius >> 2;
		int k = z - radius >> 2;
		int l = x + radius >> 2;
		int m = height + radius >> 2;
		int n = z + radius >> 2;
		int o = l - i + 1;
		int p = m - j + 1;
		int q = n - k + 1;
		Set<Biome> set = Sets.newHashSet();

		for(int r = 0; r < q; ++r) {
			for(int s = 0; s < o; ++s) {
				for(int t = 0; t < p; ++t) {
					int u = i + s;
					int v = j + t;
					int w = k + r;
					set.add(this.getBiomeForNoiseGen(u, v, w));
				}
			}
		}

		return set;
	}

	@Nullable
	public BlockPos locateBiome(int x, int y, int z, int radius, Predicate<Biome> predicate, Random random) {
		return this.locateBiome(x, y, z, radius, 1, predicate, random, false);
	}

	@Nullable
	public BlockPos locateBiome(int x, int y, int z, int radius, int i, Predicate<Biome> predicate, Random random, boolean bl) {
		return locateBiome(x, y, z, radius, i, predicate, random, bl, true);
	}
	
	private BlockPos locateBiome(int x, int y, int z, int radius, int i, Predicate<Biome> predicate, Random random, boolean bl, boolean heightDependent)
	{
		int j = x >> 2;
		int k = z >> 2;
		int l = radius >> 2;
		int m = (heightDependent ? this.chunkGen.getHeight(x, z, Heightmap.Type.OCEAN_FLOOR_WG) : y) >> 2;
		BlockPos blockPos = null;
		int n = 0;
		int o = bl ? 0 : l;

		for(int p = o; p <= l; p += i) {
			for(int q = -p; q <= p; q += i) {
				boolean bl2 = Math.abs(q) == p;

				for(int r = -p; r <= p; r += i) {
					if (bl) {
						boolean bl3 = Math.abs(r) == p;
						if (!bl3 && !bl2) {
							continue;
						}
					}

					int s = j + r;
					int t = k + q;
					if (predicate.test(this.getBiomeForNoiseGen(s, m, t))) {
						if (blockPos == null || random.nextInt(n + 1) == 0) {
							blockPos = new BlockPos(s << 2, heightDependent ? m << 2 : y, t << 2);
							if (bl) {
								return blockPos;
							}
						}

						++n;
					}
				}
			}
		}
		return blockPos;
	}
	
	static {
		BIOMES = ImmutableList.of(
				BiomeList.OCEAN,
				BiomeList.PLAINS, 
				BiomeList.DESERT, 
				BiomeList.MOUNTAINS, 
				BiomeList.FOREST, 
				BiomeList.TAIGA, 
				BiomeList.SWAMP, 
				BiomeList.RIVER,
				BiomeList.FROZEN_OCEAN,
				BiomeList.SNOWY_TUNDRA, 
				BiomeList.MUSHROOM_FIELDS, 
				BiomeList.BEACH, 
				BiomeList.JUNGLE, 
				BiomeList.DEEP_OCEAN, 
				BiomeList.STONE_SHORE, 
				BiomeList.SNOWY_BEACH, 
				BiomeList.BIRCH_FOREST, 
				BiomeList.DARK_FOREST, 
				BiomeList.SNOWY_TAIGA, 
				BiomeList.GIANT_TREE_TAIGA, 
				BiomeList.WOODED_MOUNTAINS, 
				BiomeList.SAVANNA, 
				BiomeList.BADLANDS, 
				BiomeList.WOODED_BADLANDS_PLATEAU,
				BiomeList.WARM_OCEAN, 
				BiomeList.LUKEWARM_OCEAN, 
				BiomeList.COLD_OCEAN, 
				BiomeList.DEEP_WARM_OCEAN, 
				BiomeList.DEEP_LUKEWARM_OCEAN, 
				BiomeList.DEEP_COLD_OCEAN, 
				BiomeList.DEEP_FROZEN_OCEAN, 
				BiomeList.SUNFLOWER_PLAINS, 
				BiomeList.GRAVELLY_MOUNTAINS, 
				BiomeList.FLOWER_FOREST, 
				BiomeList.ICE_SPIKES,
				BiomeList.TALL_BIRCH_FOREST, 
				BiomeList.GIANT_SPRUCE_TAIGA, 
				BiomeList.SHATTERED_SAVANNA 
				);
	}

	public IndexedIterable<Biome> getRegistry() {
		return this.lookupRegistry;
	}

	public class ClimateData
	{
		final double[] temperature;
		final double[] humidity;
		final double[] weirdness;
		final double[] landmass;
		
		public ClimateData(double[] temp, double[] humid, double[] weird, double[] land) {
			this.temperature = temp;
			this.humidity = humid;
			this.weirdness = weird;
			this.landmass = land;
		}
	}
}
