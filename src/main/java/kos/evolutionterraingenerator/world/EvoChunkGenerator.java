package kos.evolutionterraingenerator.world;

import java.util.Random;

import kos.evolutionterraingenerator.util.NoiseGeneratorOpenSimplex;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class EvoChunkGenerator extends OverworldChunkGenerator
{
	   private static final double[] field_222576_h = Util.make(new double[25], (p_222575_0_) -> {
		      for(int i = -2; i <= 2; ++i) {
		         for(int j = -2; j <= 2; ++j) {
		            double f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
		            p_222575_0_[i + 2 + (j + 2) * 5] = f;
		         }
		      }

		   });
		public static final Biome[] PLAINS_BIOMES = Util.make(new Biome[256], (arr) -> 
		{
			for(int i = 0; i < arr.length; i++)
				arr[i] = Biomes.PLAINS;
		});
	
	private EvoGenSettings settings;
	private final EvoBiomeProvider biomeProvider;
	private final Random rand;
    private NoiseGeneratorOpenSimplex minLimitPerlinNoise;
    private NoiseGeneratorOpenSimplex maxLimitPerlinNoise;
    private NoiseGeneratorOpenSimplex mainPerlinNoise;
    public NoiseGeneratorOpenSimplex depthNoise;
	private NoiseGeneratorOpenSimplex surfaceDepthNoise;
	private NoiseGeneratorOpenSimplex variationNoise;
    private final IWorld world;
    
	private final int verticalNoiseGranularity;
	private final int noiseSizeY;
	
	public EvoChunkGenerator(IWorld worldIn, EvoBiomeProvider biomeProviderIn, EvoGenSettings settingsIn) {
		super(worldIn, biomeProviderIn, settingsIn);

		this.world = worldIn;
		this.settings = settingsIn;
		this.biomeProvider = biomeProviderIn;
		this.rand = new Random(world.getSeed());

        this.minLimitPerlinNoise = new NoiseGeneratorOpenSimplex(this.rand, 16);
        this.maxLimitPerlinNoise = new NoiseGeneratorOpenSimplex(this.rand, 16);
        this.mainPerlinNoise = new NoiseGeneratorOpenSimplex(this.rand, this.settings.getNoiseOctaves());
        
        this.depthNoise = new NoiseGeneratorOpenSimplex(this.rand, 16);
        this.surfaceDepthNoise = new NoiseGeneratorOpenSimplex(this.rand, 4);
        this.variationNoise = new NoiseGeneratorOpenSimplex(this.rand, 4);
        
    	this.verticalNoiseGranularity = 8;
        this.noiseSizeY = 256 / this.verticalNoiseGranularity;
	}

	@Override
	public EvoGenSettings getSettings()
	{
		return this.settings;
	}
	
	@Override
	public int getSeaLevel()
	{
		return this.settings.getSeaLevel();
	}

	//Just temporarily use an array of plains biomes
	@Override
	public void generateBiomes(IChunk chunkIn)
	{
		chunkIn.setBiomes(PLAINS_BIOMES);
	}

	//This is where the biomes are set
	@Override
	public void generateSurface(IChunk chunkIn) 
	{
		ChunkPos chunkpos = chunkIn.getPos();
	    SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
	    sharedseedrandom.setBaseChunkSeed(chunkpos.x, chunkpos.z);
	    int x = chunkpos.getXStart();
	    int z = chunkpos.getZStart();
	    Biome[] abiome = new Biome[256];

	    for(int i = 0; i < 16; ++i) 
	    {
	    	for(int j = 0; j < 16; ++j)
	        {
	    		int x1 = x + i;
	        	int z1 = z + j;
	        	int y = chunkIn.getTopBlockY(Heightmap.Type.OCEAN_FLOOR_WG, i, j) + 1;
        		Biome[] biomes = this.biomeProvider.generateLandBiome(x1, z1);
	        	biomes = this.biomeProvider.setBiomebyHeight(biomes, x1, z1, y);
	        	double noise = this.surfaceDepthNoise.getNoise((double)x1 * 0.05D, (double)z1 * 0.05D) * 0.5;
	    		double humidity = this.biomeProvider.getHumidity(x, z)[1];
	    		if (biomes[0] == Biomes.BADLANDS || biomes[0] == Biomes.WOODED_BADLANDS_PLATEAU)
	        		biomes[0].buildSurface(sharedseedrandom, chunkIn, x1, z1, y, noise, this.getSettings().getDefaultBlock(), this.getSettings().getDefaultFluid(), this.getSeaLevel(), this.world.getSeed());
	    		else if ( y <= 150 + Math.rint(20.0 * humidity + MathHelper.clamp(noise * 2.0, -2, 2)) )
	        		biomes[0].buildSurface(sharedseedrandom, chunkIn, x1, z1, y, noise, this.getSettings().getDefaultBlock(), this.getSettings().getDefaultFluid(), this.getSeaLevel(), this.world.getSeed());
        		abiome[j * 16 + i] = biomes[1];
	        }
	    }
	    chunkIn.setBiomes(abiome);
	    this.makeBedrock(chunkIn, sharedseedrandom);
	}

	@Override
	public void initStructureStarts(IChunk chunkIn, ChunkGenerator<?> generator, TemplateManager templateManagerIn) 
	{
		ChunkPos chunkpos = chunkIn.getPos();
        int x = chunkpos.getXStart() + 9;
        int z = chunkpos.getZStart() + 9;
        int y = func_222529_a(x, z, Heightmap.Type.OCEAN_FLOOR_WG) + 1;
		Biome biome = this.biomeProvider.setBiomebyHeight(this.biomeProvider.generateLandBiome(x, z, true), x, z, y, true);
		for(Structure<?> structure : Feature.STRUCTURES.values()) 
		{
	        if (generator.getBiomeProvider().hasStructure(structure))
	        {
				if (biome.hasStructure(structure)) 
				{
					SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
		            StructureStart structurestart = StructureStart.DUMMY;
		            if (structure.hasStartAt(generator, sharedseedrandom, chunkpos.x, chunkpos.z)) 
		            {
		            	StructureStart structurestart1 = structure.getStartFactory().create(structure, chunkpos.x, chunkpos.z, biome, MutableBoundingBox.getNewBoundingBox(), 0, generator.getSeed());
		            	structurestart1.init(this, templateManagerIn, chunkpos.x, chunkpos.z, biome);
		            	structurestart = structurestart1.isValid() ? structurestart1 : StructureStart.DUMMY;
		            }
		            chunkIn.putStructureStart(structure.getStructureName(), structurestart);
				}
	        }
		}
	}
	
	@Override
	public void decorate(WorldGenRegion region) 
	{
		int i = region.getMainChunkX();
		int j = region.getMainChunkZ();
		int x = i * 16;
		int z = j * 16;
		BlockPos blockpos = new BlockPos(x, 0, z);
		int y = region.getChunk(blockpos).getTopBlockY(Heightmap.Type.OCEAN_FLOOR_WG, x + 8, z + 8) + 1;
		Biome biome = this.getBiome(region, blockpos.add(8, y, 8));
		SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
		long i1 = sharedseedrandom.setDecorationSeed(region.getSeed(), x, z);
		double temperature = this.biomeProvider.getTemperature(x + 8, z + 8)[1];
		double humidity = this.biomeProvider.getHumidity(x + 8, z + 8)[1];
    	double noise = this.surfaceDepthNoise.getNoise((double)(x + 8) * 0.5, (double)(z + 8) * 0.5);

		for(GenerationStage.Decoration generationstage$decoration : GenerationStage.Decoration.values()) 
		{
			try 
			{
				if (generationstage$decoration == GenerationStage.Decoration.VEGETAL_DECORATION)
				{
					if ( y <= 140 + Math.rint(10.0 * ((0.5 - Math.abs(temperature - 0.5)) * 2.0) * humidity + MathHelper.clamp(noise, -3.125, 3.125)) )
						biome.decorate(generationstage$decoration, this, region, i1, sharedseedrandom, blockpos);
				}
				else
					biome.decorate(generationstage$decoration, this, region, i1, sharedseedrandom, blockpos);
			} 
			catch (Exception exception) 
			{
				CrashReport crashreport = CrashReport.makeCrashReport(exception, "Biome decoration");
				crashreport.makeCategory("Generation").addDetail("CenterX", i).addDetail("CenterZ", j).addDetail("Step", generationstage$decoration).addDetail("Seed", i1).addDetail("Biome", Registry.BIOME.getKey(biome));
		        throw new ReportedException(crashreport);
			}
		}
	}

	@Override
	protected Biome getBiome(WorldGenRegion worldRegionIn, BlockPos pos) 
    {
		IChunk chunk = worldRegionIn.getChunk(pos);
        int x = pos.getX();
        int y = chunk.getTopBlockY(Heightmap.Type.OCEAN_FLOOR_WG, pos.getX(), pos.getZ()) + 1;
        int z = pos.getZ();
        Biome biome = this.biomeProvider.setBiomebyHeight(this.biomeProvider.generateLandBiome(x, z, true), x, z, y, true);
        return biome;
	}

	/* 1.14 GENERATION METHODS */
    @Override
    protected void func_222548_a(double[] arr, int x, int z) {
       double coordScale = this.settings.getCoordScale();
       double heightScale = this.settings.getHeightScale();
       double d2 = 8.555149841308594D;
       double d3 = 4.277574920654297D;
       int i = -10;
       int j = 3;
       this.func_222546_a(arr, x, z, coordScale, heightScale, d2, d3, j, i);
    }

    
    //The only reason it's here is because of func_222552_a() being private in NoiseChunkGenerator
    @Override
    protected void func_222546_a(double[] arr, int x, int z, double coordScale, double heightScale, double d_1, double d_2, int p_222546_12_, int p_222546_13_) {
       double[] adouble = this.func_222549_a(x, z);
       double d0 = adouble[0];
       double d1 = adouble[1];
       double d2 = this.func_222551_g();

       for(int i = 0; i < this.noiseSizeY + 1; ++i)
       {
          double d4 = this.func_222552_a(x, i, z, coordScale, heightScale);
          d4 = d4 - this.func_222545_a(d0, d1, i);
          if ((double)i > d2) 
          {
             d4 = MathHelper.clampedLerp(d4, (double)p_222546_13_, ((double)i - d2) / (double)p_222546_12_);
          } 
          else if ((double)i < 0.0)
          {
             d4 = MathHelper.clampedLerp(d4, -30.0D, (0.0 - (double)i) / -1.0);
          }

          arr[i] = d4;
       }

    }

    @Override
    protected double func_222545_a(double p_222545_1_, double p_222545_3_, int p_222545_5_) {
       double depthBase = this.settings.getDepthBaseSize();
       double d1 = ((double)p_222545_5_ - (depthBase + p_222545_1_ * depthBase / 8.0D * 4.0D)) * this.settings.getHeightStretch() * 128.0D / 256.0D / p_222545_3_;
       if (d1 < 0.0D) {
          d1 *= 4.0D;
       }

       return d1;
    }

    private double func_222552_a(double x, double y, double z, double coordScale, double heightScale) {

       double mainCoord = this.settings.getMainNoiseCoordScale();
       double mainHeight = this.settings.getMainNoiseHeightScale();
       double d0 = this.minLimitPerlinNoise.getNoise(x * coordScale, y * heightScale, z * coordScale);
       double d1 = this.maxLimitPerlinNoise.getNoise(x * coordScale, y * heightScale, z * coordScale);
       double d2 = this.mainPerlinNoise.getNoise(x * coordScale / mainCoord, y * heightScale / mainHeight, z * coordScale / mainCoord);

       return MathHelper.clampedLerp(d0 / this.settings.getLowerLimitScale(), d1 / this.settings.getUpperLimitScale(), (d2 / 10.0D + 1.0D) / 2.0D);
    }

    @Override
    protected double[] func_222549_a(int x, int z) 
    {
    	double[] adouble = new double[2];
    	double d = 0.0;
    	double d1 = 0.0;
    	double d2 = 0.0;

    	for(int j = -2; j <= 2; ++j)
    	{
    		for(int k = -2; k <= 2; ++k) 
    		{
    			double temperature = this.biomeProvider.getTemperature((x + j) * 4, (z + k) * 4)[1];
    			double humidity =  this.biomeProvider.getTemperature((x + j) * 4, (z + k) * 4)[1];
    			double variation = MathHelper.clamp(this.variationNoise.getNoise((x + j) * 0.0825, (z + k) * 0.0825) * 0.2 + 0.5, 0.0, 1.0);
    			double d4 = (this.settings.getBiomeDepth() 
    					+ ( (1.0 - humidity * temperature) * this.settings.getBiomeDepthFactor()) )
    					* this.settings.getBiomeDepthWeight();
    			double d5 = (this.settings.getBiomeScale() + (variation * variation * this.settings.getBiomeScaleFactor())) * this.settings.getBiomeScaleWeight();
             
    			boolean isRiver = this.biomeProvider.getRiver((x + j) * 4, (z + k) * 4);
    			double[] landmass = this.biomeProvider.getLandmass((x + j) * 4, (z + k) * 4);
    			boolean isOcean = landmass[4] < EvoBiomeProvider.oceanThreshold - EvoBiomeProvider.beachThreshold / (double)EvoBiomeProvider.oceanOctaves / EvoBiomeProvider.oceanScale;
    			boolean isBeach = !isOcean && (landmass[4] < EvoBiomeProvider.oceanThreshold) && this.biomeProvider.canBeBeach((x + j) * 4, (z + k) * 4);
    			
    			if (isBeach | isOcean)
    			{
    				d4 = this.settings.getBiomeDepthOffset() + MathHelper.clamp((landmass[4] - EvoBiomeProvider.oceanThreshold + 0.025) * 6.0, -1.9, 0.0) * this.settings.getBiomeDepthWeight();
    				d5 = 0.0;
    			}
             
    			if (isRiver)
    			{
    				if (isBeach | isOcean)
    				{
    					d5 = 0.0;
    					if (d4 > (this.settings.getBiomeDepthOffset() + Biomes.RIVER.getDepth()) * this.settings.getBiomeDepthWeight())
    						d4 = (this.settings.getBiomeDepthOffset() + Biomes.RIVER.getDepth()) * this.settings.getBiomeDepthWeight();
    				}
    				else
    				{
    					d4 = (this.settings.getBiomeDepthOffset() + Biomes.OCEAN.getDepth()) * this.settings.getBiomeDepthWeight();
    					d5 = 0.0;
    				}
    			}

    			double d6 = field_222576_h[j + 2 + (k + 2) * 5] / (d4 + 2.0);

    			d += d5 * d6;
    			d1 += d4 * d6;
    			d2 += d6;
    		}
    	}

    	d = d / d2;
    	d1 = d1 / d2;
    	d = d * 0.9 + 0.1;
    	d1 = (d1 * 4.0 - 1.0) / 8.0;
    	adouble[0] = d1 + this.func_222574_c(x, z);
    	adouble[1] = d;
    	return adouble;
    }

    private double func_222574_c(int x, int z) {
       double d0 = this.depthNoise.getNoise((double)(x * this.settings.getDepthNoiseScaleX()), (double)(z * this.settings.getDepthNoiseScaleZ())) / 8000.0D;
       if (d0 < 0.0D) {
          d0 = -d0 * 0.3D;
       }

       d0 = d0 * 3.0D - 2.0D;
       if (d0 < 0.0D) {
          d0 = d0 / 28.0D;
       } else {
          if (d0 > 1.0D) {
             d0 = 1.0D;
          }

          d0 = d0 / 40.0D;
       }

       return d0;
    }
}
