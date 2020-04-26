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
    
	public double[] temperatures;
	public double[] humidities;
	public double[] landmasses;
	public double[] landmasses2;
	public double[] biomeChance;
	public double[] mushroomChance;
	public boolean[] isRiver;
	public double[] noise;
	
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
    	return getBiomesForGeneration(biomes, x, z, width, height, 1, 1, true);
    }

    public Biome getBiome(int x, int y) {
    	return getBiomesForGeneration(null, x, y, 1, 1, 1, 1, true)[0];
    }
    
    public Biome func_222366_b(int x, int z) {
    	return getBiomesForGeneration(null, x, z, 1, 1, 1, 1, true)[0];
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
    
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height, int xScale, int zScale, boolean findOceans)
    {
        if (biomes == null || biomes.length < width * height)
            biomes = new Biome[width * height];

        if (temperatures == null || temperatures.length < width * height)
        	temperatures = new double[width * height];
        if (humidities == null || humidities.length < width * height)
        	humidities = new double[width * height];
        if (landmasses == null || landmasses.length < width * height)
        	landmasses = new double[width * height];
        if (landmasses2 == null || landmasses2.length < width * height)
        	landmasses2 = new double[width * height];
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
    		landmasses = landOctave.generateNoiseOctaves(landmasses, x, z, width, height, (0.00125 / oceanScale) * xScale,( 0.00125 / oceanScale) * zScale);
    		landmasses2 = landOctave2.generateNoiseOctaves(landmasses2, x, z, width, height, (0.00125 / oceanScale) * xScale,( 0.00125 / oceanScale) * zScale);
    		biomeChance = biomeChanceOctave.generateNoiseOctaves(biomeChance, x, z, width, height, 0.0025 * xScale, 0.0025 * zScale);
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
    				double landVal = (landmasses[m]  * 0.25 + 0.65) * 0.997 + noiseVal * 0.003;
    				double landVal2 = (landmasses2[m]  * 0.25 + 0.75) * 0.997 + noiseVal * 0.003;
    				double chanceVal = (biomeChance[m]  * 0.25 + 0.5) * 0.99 + noiseVal * 0.01;
    				double mushroomVal = (mushroomChance[m]  * 0.25 + 0.5) * 0.999 + noiseVal * 0.001;
    				temperatureVal = MathHelper.clamp(temperatureVal, 0.0, 1.0);
    				humidityVal = MathHelper.clamp(humidityVal, 0.0, 1.0);
    				landVal = MathHelper.clamp(landVal, 0.0, 1.0);
    				landVal2 = MathHelper.clamp(landVal2, 0.0, 1.0);
    				chanceVal = MathHelper.clamp(chanceVal, 0.0, 1.0);
    				mushroomVal = MathHelper.clamp(mushroomVal, 0.0, 1.0);
    				temperatures[m] = temperatureVal;
    				humidities[m] = humidityVal;
    				landmasses[m] = landVal;
    				landmasses2[m] = landVal2;
    				mushroomChance[m] = mushroomVal;
					biomes[k] = getLandBiome(temperatures[m], humidities[m], chanceVal);
    				if (findOceans && landmasses[m] < oceanThreshold && landmasses2[m] < oceanThreshold)
    				{
    					if (mushroomChance[m] > 0.99975)
    					{
    						if (mushroomChance[m] >= 1.0)
    							biomes[k] = Biomes.MUSHROOM_FIELDS;
    						else
        						biomes[k] = getOcean(temperatures[m], humidities[m], false);
    					}
    					else
    					{
        					if (landmasses[m] > oceanThreshold - 0.025 || landmasses2[m] > oceanThreshold - 0.025)
        					{
        						if (!(biomes[k].equals(Biomes.BADLANDS) | biomes[k].equals(Biomes.DESERT)))
        							biomes[k] = getBeach(temperatures[m], humidities[m], landmasses[m] <= oceanThreshold - 0.025);
        					}
        					else
        						biomes[k] = getOcean(temperatures[m], humidities[m], landmasses[m] < oceanThreshold * 0.75 && landmasses2[m] < oceanThreshold * 0.75);
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
        return this.getBiomes(oldBiomeList, x, z, width, depth, true);
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

	public Biome[] getBiomesForGeneration(Biome[] biomesForGeneration, int i, int j, int k, int l, int m, int n) {
		// TODO Auto-generated method stub
		return getBiomesForGeneration(biomesForGeneration, i, j, k, l, m, n, true);
	}
}
