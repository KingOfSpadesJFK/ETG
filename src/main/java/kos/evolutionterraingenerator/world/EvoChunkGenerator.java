package kos.evolutionterraingenerator.world;

import java.util.Random;

import biomesoplenty.api.biome.BOPBiomes;
import kos.evolutionterraingenerator.util.NoiseGeneratorOpenSimplex;
import kos.evolutionterraingenerator.world.biome.EvoBiomes;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
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
    protected static final BlockState STONE = Blocks.STONE.getDefaultState();
    protected static final BlockState AIR = Blocks.AIR.getDefaultState();
    protected static final BlockState WATER = Blocks.WATER.getDefaultState();
	
	private EvoGenSettings settings;
	private final EvoBiomeProvider biomeProvider;
	private final Random rand;
    private NoiseGeneratorOpenSimplex minLimitPerlinNoise;
    private NoiseGeneratorOpenSimplex maxLimitPerlinNoise;
    private NoiseGeneratorOpenSimplex mainPerlinNoise;
    public NoiseGeneratorOpenSimplex depthNoise;
    public NoiseGeneratorOpenSimplex swampChance;
    public NoiseGeneratorOpenSimplex swampType;
	private NoiseGeneratorOpenSimplex surfaceDepthNoise;
    private final IWorld world;
    double[] mainNoiseRegion;
    double[] minLimitRegion;
    double[] maxLimitRegion;
    double[] depthRegion;
    
	private final int noiseSizeY;
	private final int verticalNoiseGranularity;
	
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
        this.swampChance = new NoiseGeneratorOpenSimplex(this.rand, 4);
        this.swampType = new NoiseGeneratorOpenSimplex(this.rand, 4);
        
    	this.verticalNoiseGranularity = 8;
        this.noiseSizeY = 256 / this.verticalNoiseGranularity;		
	}

	@Override
	public EvoGenSettings getSettings()
	{
		return this.settings;
	}

	@Override
	public void generateBiomes(IChunk chunkIn) 
	{
		ChunkPos chunkpos = chunkIn.getPos();
		int x = chunkpos.x;
		int z = chunkpos.z;
		Biome[] abiome = this.biomeProvider.getBiomesForGeneration(null, x * 16, z * 16, 16, 16, 1, 1, true, true);
		chunkIn.setBiomes(abiome);
	}

	//This is just here for a less noisy surface between deserts/mesas and other biomes
	@Override
	public void generateSurface(IChunk chunkIn) 
	{
	      ChunkPos chunkpos = chunkIn.getPos();
	      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
	      sharedseedrandom.setBaseChunkSeed(chunkpos.x, chunkpos.z);
	      int x = chunkpos.getXStart();
	      int z = chunkpos.getZStart();
	      Biome[] abiome = chunkIn.getBiomes();
	      boolean changeBiomes = false;

	      for(int i = 0; i < 16; ++i) 
	      {
	         for(int j = 0; j < 16; ++j)
	         {
	        	 int x1 = x + i;
	        	 int z1 = z + j;
	        	 int y = chunkIn.getTopBlockY(Heightmap.Type.OCEAN_FLOOR_WG, i, j) + 1;
	        	 Biome biome = this.biomeProvider.generateLandBiome(x1, z1, false);
	        	 Biome biome2 = setBiomebyHeight(biome, chunkIn, x1, z1, y, false);
	        	 if (!biome.equals(biome2))
	        	 {
	        		 changeBiomes = true;
	        		 biome = biome2;
	        		 abiome[j * 16 + i] = biome2;
	        	 }
	        	 double d1 = this.surfaceDepthNoise.getNoise((double)x1 * 0.05D, (double)z1 * 0.05D) * 0.25;
	        	 biome.buildSurface(sharedseedrandom, chunkIn, x1, z1, y, d1, this.getSettings().getDefaultBlock(), this.getSettings().getDefaultFluid(), this.getSeaLevel(), this.world.getSeed());
	         }
	      }
	      if (changeBiomes)
	    	  chunkIn.setBiomes(abiome);
	      this.makeBedrock(chunkIn, sharedseedrandom);
	}

	@Override
	public void initStructureStarts(IChunk chunkIn, ChunkGenerator<?> generator, TemplateManager templateManagerIn) 
	{
		for(Structure<?> structure : Feature.STRUCTURES.values()) 
		{
	        if (generator.getBiomeProvider().hasStructure(structure))
	        {
	            ChunkPos chunkpos = chunkIn.getPos();
	            int x = chunkpos.getXStart() + 9;
	            int z = chunkpos.getZStart() + 9;
	            int y = chunkIn.getTopBlockY(Heightmap.Type.OCEAN_FLOOR_WG, x, z) + 1;
	        	Biome biome = setBiomebyHeight(this.biomeProvider.generateLandBiome(x, z, true), chunkIn, x, z, y, true);
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
	protected Biome getBiome(WorldGenRegion worldRegionIn, BlockPos pos) 
    {
		IChunk chunk = worldRegionIn.getChunk(pos);
        int x = pos.getX();
        int y = chunk.getTopBlockY(Heightmap.Type.OCEAN_FLOOR_WG, pos.getX(), pos.getZ()) + 1;
        int z = pos.getZ();
        Biome biome = setBiomebyHeight(this.biomeProvider.generateLandBiome(x, z, true), chunk, x, z, y, true);
        return biome;
	}

    //Sets biomes according to the conditions of the land
    private Biome setBiomebyHeight(Biome biome, IChunk chunkPrimerIn, int x, int z, int y, boolean useNoise)
    {
        int seaLevel = this.settings.getSeaLevel();
        double temperature = this.biomeProvider.getTemperature(x, z);
        double humidity = this.biomeProvider.getHumidity(x, z);
   	 	double[] landmass = this.biomeProvider.getLandmass(x, z);
   	 	double beachThreshold = EvoBiomeProvider.oceanThreshold - EvoBiomeProvider.beachThreshold / (double)EvoBiomeProvider.oceanOctaves / EvoBiomeProvider.oceanScale;
		boolean isOcean = landmass[4] < beachThreshold;
		boolean isBeach = !isOcean && (landmass[4] < EvoBiomeProvider.oceanThreshold) && this.biomeProvider.canBeBeach(x, z);
		
    	if (landmass[2] == landmass[4])
    	{
    		double biomeChance = this.biomeProvider.getBiomeChance(x, z, useNoise);
			if (temperature < EvoBiomeProvider.SNOW_TEMP)
				biome = EvoBiomes.COLD_ISLANDS.getBiome(biomeChance);
			else if (temperature < EvoBiomeProvider.HOT_TEMP)
				biome = EvoBiomes.ISLAND_BIOMES.getBiome(biomeChance);
			else
				biome = EvoBiomes.HOT_ISLANDS.getBiome(biomeChance);
    	}
    	if (landmass[3] == landmass[4])
			biome = Biomes.MUSHROOM_FIELDS;
        
        if (isBeach || isOcean)
        {
        	if (y < seaLevel)
        		return this.biomeProvider.getOcean(temperature, y < 40);
        	if (y < seaLevel + 3)
        	{
        		if (this.biomeProvider.getSettings().isUseBOPBiomes() && 
        				(landmass[0] == landmass[4] || landmass[2] == landmass[4]) && 
        				(biome.equals(Biomes.JUNGLE) ||
        						biome.equals(Biomes.BAMBOO_JUNGLE) ||
        						biome.equals(BOPBiomes.tropics.get()) ||
        						biome.equals(BOPBiomes.tropical_rainforest.get()) )
        				)
        			return BOPBiomes.white_beach.get();
        		if (this.biomeProvider.getSettings().isUseBOPBiomes() && biome.equals(BOPBiomes.volcano.get()))
        			return BOPBiomes.volcano_edge.get();
        		if (this.biomeProvider.getSettings().isUseBOPBiomes() && biome.equals(BOPBiomes.origin_hills.get()))
        			return BOPBiomes.origin_beach.get();
	    		if (!biome.equals(Biomes.BADLANDS) && 
	    				!biome.equals(Biomes.MUSHROOM_FIELDS) && 
	    				!biome.equals(Biomes.DESERT) && 
	    				!(this.biomeProvider.getSettings().isUseBOPBiomes() && 
	    						(biome.equals(BOPBiomes.outback.get()) || 
	    								biome.equals(BOPBiomes.xeric_shrubland.get()) || 
	    								biome.equals(BOPBiomes.wasteland.get()) || 
	    								biome.equals(BOPBiomes.cold_desert.get()) )
	    						)
	    				)
	    			return this.biomeProvider.getBeach(x, z);
        	}
        }
        
        if (landmass[2] == landmass[4] || landmass[3] == landmass[4])
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
    	else if (biome.equals(Biomes.BADLANDS))
    	{
    		if (y >= seaLevel + 50)
    		{
        		biome = Biomes.WOODED_BADLANDS_PLATEAU;
    		}
    	}
    	return biome;
    }
	
	/* 1.14 GENERATION METHODS */

    @Override
    protected void func_222548_a(double[] arr, int x, int z) {
       double coordScale = this.settings.getCoordScale() * (double) this.settings.getNoiseOctaves() / 8.0;
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

       double coord = this.settings.getMainNoiseCoordScale();
       double height = this.settings.getMainNoiseHeightScale();
       double d0 = this.minLimitPerlinNoise.getNoise(x * coord, y * height, z * coord);
       double d1 = this.maxLimitPerlinNoise.getNoise(x * coord, y * height, z * coord);
       double d2 = this.mainPerlinNoise.getNoise(x * coord / coordScale, y * height / heightScale, z * coord / coordScale);

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
    			double temperature = this.biomeProvider.getTemperature((x + j) * 4, (z + k) * 4);
    			double humidity =  this.biomeProvider.getTemperature((x + j) * 4, (z + k) * 4);
    			double d4 = 0.75  + (0.0275 - humidity * temperature * 0.0275) * this.settings.getBiomeDepthWeight();
    			double d5 = 0.95 + humidity * temperature * 0.3 * this.settings.getBiomeScaleWeight();
             
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
    					if (d4 > this.settings.getBiomeDepthOffset() + Biomes.RIVER.getDepth() * this.settings.getBiomeDepthWeight())
    						d4 = this.settings.getBiomeDepthOffset() + Biomes.RIVER.getDepth() * this.settings.getBiomeDepthWeight();
    				}
    				else
    				{
    					d4 = this.settings.getBiomeDepthOffset() + Biomes.OCEAN.getDepth() * this.settings.getBiomeDepthWeight();
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
