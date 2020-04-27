package kos.evolutionterraingenerator.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.layer.Layer;
import net.minecraft.world.gen.layer.LayerUtil;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import kos.evolutionterraingenerator.util.NoiseGeneratorOpenSimplex;
import kos.evolutionterraingenerator.world.biome.EvoBiome;
import kos.evolutionterraingenerator.world.biome.EvoBiomes;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.world.biome.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;

public class BiomeProviderEvo extends OverworldBiomeProvider
{
	private final Layer genBiomes;
	private final Layer biomeFactoryLayer;
    private NoiseGeneratorOpenSimplex tempOctave;
    private NoiseGeneratorOpenSimplex humidOctave;
    private NoiseGeneratorOpenSimplex landOctave;
	private NoiseGeneratorOpenSimplex landOctave2;
    private NoiseGeneratorOpenSimplex biomeChanceOctave;
    private NoiseGeneratorOpenSimplex noiseOctave;
    private NoiseGeneratorOpenSimplex mushroomOctave;
    /** A list of biomes that the player can spawn in. */
    private final List<Biome> biomesToSpawnIn = Lists.newArrayList(Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.BEACH);
    
    private double[] temperatures;
	private double[] humidities;
	private double[] landmasses;
	private double[] landmasses2;
	private double[] biomeChance;
	private double[] mushroomChance;
	private boolean[] isRiver;
	private double[] noise;
	
	public static final double SNOW_TEMP = 0.125;
	public static final double COLD_TEMP = 0.375;
	public static final double WARM_TEMP = 0.625;
	public static final double HOT_TEMP = 0.875;

	public BiomeProviderEvo(OverworldBiomeProviderSettings settingsProvider, IWorld worldIn) {
		super(settingsProvider);
		
	      WorldInfo worldinfo = worldIn.getWorldInfo();
	      OverworldGenSettings overworldgensettings = settingsProvider.getGeneratorSettings();
	      Layer[] alayer = LayerUtil.buildOverworldProcedure(worldinfo.getSeed(), worldinfo.getGenerator(), overworldgensettings);
	      this.genBiomes = alayer[0];
	      this.biomeFactoryLayer = alayer[1];
        
        Random rand = new Random(worldIn.getSeed());
        tempOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 4);
		humidOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 4);
		landOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 2);
		landOctave2 = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 2);
		biomeChanceOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 2);
		noiseOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 2);
		mushroomOctave = new NoiseGeneratorOpenSimplex(new Random(rand.nextLong()), 2);
	}
	
    public static final double biomeScale = 3.0;
    public static final double oceanScale = 1.0;
    public static final double oceanThreshold = 0.75;
    public static final double riverScale = 0.5;
    public static final int riverSamples = 2;		//An unintended method to make rivers bigger

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
    	return MathHelper.clamp((tempOctave.getNoise(x * (0.0045 / biomeScale), z * (0.0045 / biomeScale)) * 0.125 + WARM_TEMP) * 0.99 + noise * 0.01, 0.0, 1.0);
    }
    
    public double getHumidity(double x, double z)
    {
    	double noise = noiseOctave.getNoise((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	return MathHelper.clamp((humidOctave.getNoise((double)x * (0.035 / biomeScale), (double)z * (0.035 / biomeScale)) * 0.125 + 0.5) * 0.95 + noise * 0.05, 0.0, 1.0);
    }
    
    public Biome func_222366_b(int x, int z) {
    	return generateBiome(x, z);
     }
    
    public Biome generateBiome(double x, double z)
    {
    	double noise = noiseOctave.getNoise((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	double temperature = (tempOctave.getNoise((double)x * (0.0045 / biomeScale), (double)z * (0.0045 / biomeScale))* 0.125 + WARM_TEMP) * 0.99 + noise * 0.01;
    	double humidity = (humidOctave.getNoise((double)x * (0.035 / biomeScale), (double)z * (0.035 / biomeScale))* 0.125 + 0.5) * 0.95 + noise * 0.05;
    	double landmass1 = (landOctave.getNoise((double)x * (0.00125 / oceanScale), (double)z * (0.00125 / oceanScale))  * 0.25 + 0.65) * 0.997 + noise * 0.003;
    	double landmass2 = (landOctave2.getNoise((double)x * (0.00125 / oceanScale), (double)z * (0.00125 / oceanScale)) * 0.25 + 0.75) * 0.997 + noise * 0.003;
    	double biomeChance = (biomeChanceOctave.getNoise((double)x * 0.0025, (double)z * 0.0025) * 0.25 + 0.5) * 0.99 + noise * 0.01;
    	double mushroomChance = (mushroomOctave.getNoise(x * (0.00375 / biomeScale), z * (0.00375 / biomeScale)) * 0.25 + 0.5) * 0.999 + noise * 0.001;
    	
    	temperature = MathHelper.clamp(temperature, 0.0, 1.0);
		humidity = MathHelper.clamp(humidity, 0.0, 1.0);
		landmass1 = MathHelper.clamp(landmass1, 0.0, 1.0);
		landmass2 = MathHelper.clamp(landmass2, 0.0, 1.0);
		biomeChance = MathHelper.clamp(biomeChance, 0.0, 1.0);
		mushroomChance = MathHelper.clamp(mushroomChance, 0.0, 1.0);

		Biome biome = getLandBiome(temperature, humidity, biomeChance);
		if (landmass1 < oceanThreshold && landmass2 < oceanThreshold)
		{
			if (mushroomChance > 0.99975)
			{
				if (mushroomChance >= 1.0)
					biome = Biomes.MUSHROOM_FIELDS;
				else
					biome = getOcean(temperature, humidity, false);
			}
			else
			{
				if (landmass1 > oceanThreshold - 0.025 || landmass2 > oceanThreshold - 0.025)
				{
					if (!(biome.equals(Biomes.BADLANDS) | biome.equals(Biomes.DESERT)))
						biome = getBeach(temperature, humidity, landmass1 <= oceanThreshold - 0.025);
				}
				else
					biome = getOcean(temperature, humidity, landmass1 < oceanThreshold * 0.75 && landmass2 < oceanThreshold * 0.75);
			}
		}
		
    	return biome;
    }
    
    public Set<Biome> getBiomesInSquare(int centerX, int centerZ, int sideLength) {
        int i = centerX - sideLength >> 2;
        int j = centerZ - sideLength >> 2;
        int k = centerX + sideLength >> 2;
        int l = centerZ + sideLength >> 2;
        int i1 = k - i + 1;
        int j1 = l - j + 1;
        Set<Biome> set = Sets.newHashSet();
        Collections.addAll(set, getBiomesForGeneration(null, i, j, i1, j1));
        return set;
     }   
    
    public List<Biome> getBiomesToSpawnIn() {
         return BIOMES_TO_SPAWN_IN;
     }
    
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height, int xScale, int zScale)
    {
        if (biomes == null || biomes.length < width * height)
            biomes = new Biome[width * height];

        try
        {
    		for (int i = 0; i < width; i++)
    		{

    			for (int j = 0; j < height; j++)
    			{
    	    		int l = width * height - 1;
    	    		int m = i * width + j;
    	    		int k = j * height + i;
    	    		biomes[k] = generateBiome((double)x * xScale, (double)z * zScale);
    			}
    		}
    		return biomes;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("RawBiomeBlock");
            crashreportcategory.addDetail("biomes[] size", Integer.valueOf(biomes.length));
            crashreportcategory.addDetail("x", Integer.valueOf(x));
            crashreportcategory.addDetail("z", Integer.valueOf(z));
            crashreportcategory.addDetail("w", Integer.valueOf(width));
            crashreportcategory.addDetail("h", Integer.valueOf(height));
            throw new ReportedException(crashreport);
        }
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
    			return Biomes.SNOWY_BEACH;
    		return Biomes.SNOWY_BEACH;
    	}
    	if (isGravel)
    		return Biomes.BEACH;
    	return Biomes.BEACH;
    }

    //To do: Get correct ocean biome from temperature and humidity
    //	For now, regular oceans are what's available.
    //	Will add the other oceans in 1.13+
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
    
    /**
     * Gets biomes to use for the blocks and loads the other data like temperature and humidity onto the
     * WorldChunkManager.
     */
    public Biome[] getBiomes(@Nullable Biome[] oldBiomeList, int x, int z, int width, int depth)
    {
        return this.getBiomes(oldBiomeList, x, z, width, depth);
    }

    /**
     * Gets a list of biomes for the specified blocks.
     */
    public Biome[] getBiomes(@Nullable Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag)
    {
        if (listToReuse == null || listToReuse.length < width * length)
        {
            listToReuse = new Biome[width * length];
        }
        listToReuse = getBiomesForGeneration(listToReuse, x, z, width, length);
        return listToReuse;
    }
    
    /**
     * checks given Chunk's Biomes against List of allowed ones
     */
    public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed)
    {
        int i = x - radius >> 2;
        int j = z - radius >> 2;
        int k = x + radius >> 2;
        int l = z + radius >> 2;
        int i1 = k - i + 1;
        int j1 = l - j + 1;
        Biome[] arr = getBiomesForGeneration(null, i, j, i1, j1, 4, 4);

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
            crashreportcategory.addDetail("x", Integer.valueOf(x));
            crashreportcategory.addDetail("z", Integer.valueOf(z));
            crashreportcategory.addDetail("radius", Integer.valueOf(radius));
            crashreportcategory.addDetail("allowed", allowed);
            throw new ReportedException(crashreport);
        }
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
        Biome[] arr = getBiomesForGeneration(null, i, j, i1, j1, 4, 4);
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
