package kos.evolutionterraingenerator.world;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeProvider;
import com.google.common.collect.Lists;

import kos.evolutionterraingenerator.util.NoiseGeneratorOpenSimplex;
import kos.evolutionterraingenerator.world.biome.EvoBiome;
import kos.evolutionterraingenerator.world.biome.EvoBiomes;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Biomes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraft.world.storage.WorldInfo;

public class BiomeProviderEvo extends BiomeProvider
{
    private ChunkGeneratorSettings settings;
    private GenLayer genBiomes;
    /** A GenLayer containing the indices into BiomeGenBase.biomeList[] */
    private GenLayer biomeIndexLayer;
    /** The biome list. */
    private final BiomeCache biomeCache;
    /** A list of biomes that the player can spawn in. */
    private final List<Biome> biomesToSpawnIn;
    public static List<Biome> allowedBiomes = Lists.newArrayList(Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.BEACH);
    
    private NoiseGeneratorOpenSimplex tempOctave;
    private NoiseGeneratorOpenSimplex humidOctave;
    private NoiseGeneratorOpenSimplex landOctave;
    private NoiseGeneratorOpenSimplex biomeChanceOctave;
    private NoiseGeneratorOpenSimplex noiseOctave;
    private NoiseGeneratorOpenSimplex mushroomOctave;
    
	public double[] temperatures;
	public double[] humidities;
	public double[] landmasses;
	public double[] biomeChance;
	public double[] mushroomChance;
	public boolean[] isRiver;
	public double[] noise;
	
	public static final double SNOW_TEMP = 0.125;
	public static final double COLD_TEMP = 0.375;
	public static final double WARM_TEMP = 0.625;
	public static final double HOT_TEMP = 0.875;

    protected BiomeProviderEvo()
    {
        this.biomeCache = new BiomeCache(this);
        this.biomesToSpawnIn = Lists.newArrayList(allowedBiomes);
    }

    private BiomeProviderEvo(long seed, WorldType worldTypeIn, String options)
    {
        this();
        Random rand = new Random(seed);
        tempOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 4);
		humidOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 4);
		landOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 4);
		biomeChanceOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 2);
		noiseOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 2);
		mushroomOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 2);

        if (worldTypeIn == WorldType.CUSTOMIZED && !options.isEmpty())
        {
            this.settings = ChunkGeneratorSettings.Factory.jsonToFactory(options).build();
        }

        GenLayer[] agenlayer = GenLayer.initializeAllBiomeGenerators(seed, worldTypeIn, this.settings);
        agenlayer = getModdedBiomeGenerators(worldTypeIn, seed, agenlayer);
        this.genBiomes = agenlayer[0];
        this.biomeIndexLayer = agenlayer[1];
    }

    public BiomeProviderEvo(WorldInfo info)
    {
        this(info.getSeed(), info.getTerrainType(), info.getGeneratorOptions());
    }

    public BiomeProviderEvo(World world) {
        this(world.getWorldInfo());
	}

	/**
     * Gets the list of valid biomes for the player to spawn in.
     */
    public List<Biome> getBiomesToSpawnIn()
    {
        return this.biomesToSpawnIn;
    }

    /**
     * Returns the biome generator
     */
    public Biome getBiome(BlockPos pos)
    {
        return this.getBiome(pos, (Biome)null);
    }

    public Biome getBiome(BlockPos pos, Biome defaultBiome)
    {
        return this.biomeCache.getBiome(pos.getX(), pos.getZ(), defaultBiome);
    }

    /**
     * Returns an array of biomes for the location input.
     */
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height)
    {
    	return getBiomesForGeneration(biomes, x, z, width, height, 1, 1, true);
    }
    
    public static final double biomeScale = 3.0;
    public static final double oceanScale = 1.0;
    public static final double oceanThreshold = 0.4;
    public static final double riverScale = 0.5;
    public static final int riverSamples = 2;		//An unintended method to make rivers bigger
    
    public boolean[] getRiver(int x, int z, int width, int height)
    {
    	x = (int)(x * riverScale);
    	z = (int)(z * riverScale);
        if (isRiver == null || isRiver.length < width * height)
        	isRiver = new boolean[width * height];

		int[] arr = genBiomes.getInts(x - riverSamples, z - riverSamples, width + riverSamples, height + riverSamples);
    	double[] riverAvgs = new double[width * height];
		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
	    		int m = i * width + j;
	    		int k = j * height + i;
				//double noiseVal = noise[m] * 1.1 + 0.5;
	    		double valueOfRivia = 0.0;
				for (int i1 = 0 - riverSamples; i1 <= riverSamples; i1++) 
				{
					for (int j1 = 0 - riverSamples; j1 <= riverSamples; j1++)
					{
						int i2 = i + i1 + riverSamples;
						int j2 = j + j1 + riverSamples;
						int id = arr[(int)(j2  * riverScale) * (height + riverSamples) + (int)(i2 * riverScale)];
						if (id == 7 || id == 11)
							valueOfRivia = valueOfRivia + 1.0;
						else
							valueOfRivia += 0.0;
					}
				}
				double avg = (double)((riverSamples + riverSamples + 1) * (riverSamples + riverSamples + 1));
				valueOfRivia /= avg;
				isRiver[k] = valueOfRivia > 0.0;
			}
		}
		return isRiver;
    }
    
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height, int xScale, int zScale, boolean findOceans)
    {
        IntCache.resetIntCache();

        if (biomes == null || biomes.length < width * height)
            biomes = new Biome[width * height];

        if (temperatures == null || temperatures.length < width * height)
        	temperatures = new double[width * height];
        if (humidities == null || humidities.length < width * height)
        	humidities = new double[width * height];
        if (landmasses == null || landmasses.length < width * height)
        	landmasses = new double[width * height];
        if (biomeChance == null || biomeChance.length < width * height)
        	biomeChance = new double[width * height];
        if (mushroomChance == null || mushroomChance.length < width * height)
        	mushroomChance = new double[width * height];

        if (noise == null || noise.length < width * height)
        	noise = new double[width * height];

        try
        {
    		temperatures = tempOctave.generateNoiseOctaves(temperatures, x, z, width, height, (0.0045 / biomeScale) * xScale, (0.0045 / biomeScale) * zScale);
    		humidities = humidOctave.generateNoiseOctaves(humidities, x, z, width, height, (0.035 / biomeScale) * xScale, (0.035 / biomeScale) * zScale);
    		landmasses = landOctave.generateNoiseOctaves(landmasses, x, z, width, height, (0.0025 / oceanScale) * xScale,( 0.0025 / oceanScale) * zScale);
    		biomeChance = biomeChanceOctave.generateNoiseOctaves(biomeChance, x, z, width, height, 0.00075 * xScale, 0.00075 * zScale);
    		mushroomChance = mushroomOctave.generateNoiseOctaves(mushroomChance, x, z, width, height, (0.00375 / biomeScale) * xScale, (0.00375 / biomeScale) * zScale);
    		//double[] riverChance = riverOctave.generateNoiseOctaves(null, x, z, width, height, 0.0125 * xScale, 0.0125 * zScale, 0.25);
    		noise = noiseOctave.generateNoiseOctaves(noise, x, z, width, height, 0.25 * xScale, 0.25 * zScale);

    		for (int i = 0; i < width; i++)
    		{

    			for (int j = 0; j < height; j++)
    			{
    	    		int l = width * height - 1;
    	    		int m = i * width + j;
    	    		int k = j * height + i;
    				double noiseVal = noise[m] * 1.1 + 0.5;
    				double temperatureVal = (temperatures[m] * 0.125 + WARM_TEMP) * 0.99 + noiseVal * 0.01;
    				double humidityVal = (humidities[m] * 0.125 + 0.5) * 0.95 + noiseVal * 0.05;
    				double landVal = (landmasses[m]  * 0.125 + 0.625) * 0.997 + noiseVal * 0.003;
    				double chanceVal = (biomeChance[m]  * 0.25 + 0.5) * 0.9999 + noiseVal * 0.0001;
    				double mushroomVal = (mushroomChance[m]  * 0.25 + 0.5) * 0.999 + noiseVal * 0.001;
    				temperatureVal = MathHelper.clamp(temperatureVal, 0.0, 1.0);
    				humidityVal = MathHelper.clamp(humidityVal, 0.0, 1.0);
    				landVal = MathHelper.clamp(landVal, 0.0, 1.0);
    				chanceVal = MathHelper.clamp(chanceVal, 0.0, 1.0);
    				mushroomVal = MathHelper.clamp(mushroomVal, 0.0, 1.0);
    				temperatures[m] = temperatureVal;
    				humidities[m] = humidityVal;
    				landmasses[m] = landVal;
    				mushroomChance[m] = mushroomVal;
					biomes[k] = getLandBiome(temperatures[m], humidities[m], chanceVal);
    				if (findOceans && landmasses[m] < oceanThreshold)
    				{
    					if (mushroomChance[m] > 0.999125)
    					{
    						if (mushroomChance[m] > 0.99975)
    							biomes[k] = Biomes.MUSHROOM_ISLAND;
    						else
        						biomes[k] = getOcean(temperatures[m], humidities[m], false);
    					}
    					else
    					{
        					if (landmasses[m] > oceanThreshold - 0.025)
        					{
        						if (!(biomes[k].equals(Biomes.MESA) | biomes[k].equals(Biomes.DESERT)))
        							biomes[k] = getBeach(temperatures[m], humidities[m]);
        					}
        					else
        						biomes[k] = getOcean(temperatures[m], humidities[m], landmasses[m] < oceanThreshold * 0.25);
    					}
    				}
    				//isRiver[k] = valueOfRivia >= 0.25F && valueOfRivia <= 0.3F && !biomes[k].getBiomeName().contains("Ocean");
    			}
    		}
    		return biomes;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("RawBiomeBlock");
            crashreportcategory.addCrashSection("biomes[] size", Integer.valueOf(biomes.length));
            crashreportcategory.addCrashSection("x", Integer.valueOf(x));
            crashreportcategory.addCrashSection("z", Integer.valueOf(z));
            crashreportcategory.addCrashSection("w", Integer.valueOf(width));
            crashreportcategory.addCrashSection("h", Integer.valueOf(height));
            throw new ReportedException(crashreport);
        }
    }

    private Biome getLandBiome(double temp, double humid, double chance)
    {
    	EvoBiome[] arr = EvoBiomes.WARM_BIOMES;
    	
		if (temp < SNOW_TEMP)
		{
			arr = EvoBiomes.SNOWY_BIOMES;
		}
		else if (temp < COLD_TEMP)
		{
			arr = EvoBiomes.COLD_BIOMES;
		}
		else if (temp < WARM_TEMP)
		{
			arr = EvoBiomes.WARM_BIOMES;
			
		}
		else if (temp < HOT_TEMP)
		{
			arr = EvoBiomes.HOT_BIOMES;
		}
		else
		{
			arr = EvoBiomes.ARID_BIOMES;
		}
		
		return arr[(int)((arr.length - 1) * humid)].getBiome(chance);
    }


    private Biome getBeach(double temp, double humid)
    {
    	if (temp < SNOW_TEMP)
    		return Biomes.COLD_BEACH;
    	if (Biome.getIdForBiome(getLandBiome(temp, humid, 0.0)) == Biome.getIdForBiome(Biomes.EXTREME_HILLS))
    		return Biomes.STONE_BEACH;
    	return Biomes.BEACH;
    }

    //To do: Get correct ocean biome from temperature and humidity
    //	For now, regular oceans are what's available.
    //	Will add the other oceans in 1.13+
    private Biome getOcean(double temp, double humid, boolean deep)
    {
    	if (deep)
    		return Biomes.DEEP_OCEAN;
    	return Biomes.OCEAN;
    }
    
    /**
     * Gets biomes to use for the blocks and loads the other data like temperature and humidity onto the
     * WorldChunkManager.
     */
    public Biome[] getBiomes(@Nullable Biome[] oldBiomeList, int x, int z, int width, int depth)
    {
        return this.getBiomes(oldBiomeList, x, z, width, depth, true);
    }

    /**
     * Gets a list of biomes for the specified blocks.
     */
    public Biome[] getBiomes(@Nullable Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag)
    {
        IntCache.resetIntCache();

        if (listToReuse == null || listToReuse.length < width * length)
        {
            listToReuse = new Biome[width * length];
        }
        listToReuse = getBiomesForGeneration(listToReuse, x, z, width, length);
        return listToReuse;
        /*
        if (cacheFlag && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0)
        {
            Biome[] abiome = this.biomeCache.getCachedBiomes(x, z);
            System.arraycopy(abiome, 0, listToReuse, 0, width * length);
            return listToReuse;
        }
        else
        {
            int[] aint = this.biomeIndexLayer.getInts(x, z, width, length);

            for (int i = 0; i < width * length; ++i)
            {
                listToReuse[i] = Biome.getBiome(aint[i], Biomes.DEFAULT);
            }

            return listToReuse;
        }
        */
    }

    /**
     * checks given Chunk's Biomes against List of allowed ones
     */
    public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed)
    {
        IntCache.resetIntCache();
        int i = x - radius >> 2;
        int j = z - radius >> 2;
        int k = x + radius >> 2;
        int l = z + radius >> 2;
        int i1 = k - i + 1;
        int j1 = l - j + 1;
        Biome[] arr = getBiomesForGeneration(null, i, j, i1, j1, 4, 4, true);

        try
        {
            for (int k1 = 0; k1 < i1 * j1; ++k1)
            {
                Biome biome = arr[k1];

                if (!allowed.contains(biome))
                {
                    return false;
                }
            }
            return true;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Layer");
            crashreportcategory.addCrashSection("Layer", this.genBiomes.toString());
            crashreportcategory.addCrashSection("x", Integer.valueOf(x));
            crashreportcategory.addCrashSection("z", Integer.valueOf(z));
            crashreportcategory.addCrashSection("radius", Integer.valueOf(radius));
            crashreportcategory.addCrashSection("allowed", allowed);
            throw new ReportedException(crashreport);
        }
    }

    @Nullable
    public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random)
    {
        IntCache.resetIntCache();
        int i = x - range >> 2;
        int j = z - range >> 2;
        int k = x + range >> 2;
        int l = z + range >> 2;
        int i1 = k - i + 1;
        int j1 = l - j + 1;
        Biome[] arr = getBiomesForGeneration(null, i, j, i1, j1, 4, 4, false);
        BlockPos blockpos = null;
        int k1 = 0;

        for (int l1 = 0; l1 < i1 * j1; ++l1)
        {
            int i2 = i + l1 % i1 << 2;
            int j2 = j + l1 / i1 << 2;
            Biome biome = arr[l1];

            if (biomes.contains(biome) && (blockpos == null || random.nextInt(k1 + 1) == 0))
            {
                blockpos = new BlockPos(i2, 0, j2);
                ++k1;
            }
        }

        return blockpos;
    }
}
