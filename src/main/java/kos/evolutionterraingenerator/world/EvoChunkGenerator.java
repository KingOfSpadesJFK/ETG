package kos.evolutionterraingenerator.world;

import java.util.Random;

import biomesoplenty.api.biome.BOPBiomes;
import kos.evolutionterraingenerator.util.NoiseGeneratorOpenSimplex;
import kos.evolutionterraingenerator.world.biome.EvoBiomes;
import kos.evolutionterraingenerator.world.biome.NewBiomes;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
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
        this.mainPerlinNoise = new NoiseGeneratorOpenSimplex(this.rand, 8);
        
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
		Biome[] abiome = this.biomeProvider.getBiomes(x * 16, z * 16, 16, 16, true);
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
	        	 Biome biome = this.biomeProvider.generateBiome(x1, z1, false);
	        	 Biome biome2 = setBiomebyHeight(biome, chunkIn, x1, z1, y);
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
            ChunkPos chunkpos = chunkIn.getPos();
        	BlockPos pos = new BlockPos(chunkpos.getXStart() + 9, 0, chunkpos.getZStart() + 9);
        	Biome biome = this.biomeProvider.getBiome(pos);
        	biome = setBiomebyHeight(biome, chunkIn, pos.getX(), pos.getY(), chunkIn.getTopBlockY(Heightmap.Type.OCEAN_FLOOR_WG, pos.getX(), pos.getZ()));
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

	@Override
	protected Biome getBiome(WorldGenRegion worldRegionIn, BlockPos pos) 
    {
		Biome biome = this.biomeProvider.getBiome(pos);
		IChunk chunk = worldRegionIn.getChunk(pos);
        int x = pos.getX();
        int y = chunk.getTopBlockY(Heightmap.Type.OCEAN_FLOOR_WG, pos.getX(), pos.getZ());
        int z = pos.getZ();
        biome = setBiomebyHeight(biome, chunk, x, z, y);
        return biome;
	}

    //Sets biomes according to the conditions of the land
    private Biome setBiomebyHeight(Biome biome, IChunk chunkPrimerIn, int x, int z, int y)
    {
        boolean isBeach = biome.equals(Biomes.BEACH) | 
          		 biome.equals(Biomes.SNOWY_BEACH) |
          		 biome.equals(NewBiomes.GRAVEL_BEACH) |
          		 biome.equals(NewBiomes.SNOWY_GRAVEL_BEACH) |
          		 biome.equals(NewBiomes.DRY_BEACH) |
          		 biome.equals(NewBiomes.DRY_GRAVEL_BEACH) |
          		 biome.equals(NewBiomes.RED_BEACH);
        if (this.settings.isUseBOP())
        {
       	 isBeach = isBeach | 
       	 biome.equals(BOPBiomes.white_beach.get()) |
       	 biome.equals(BOPBiomes.volcano_edge.get()) |
       	 biome.equals(BOPBiomes.origin_beach.get());
        }
        
        boolean isOcean = 
        		biome.equals(Biomes.FROZEN_OCEAN) |
        		biome.equals(Biomes.DEEP_FROZEN_OCEAN) |
        		biome.equals(Biomes.COLD_OCEAN) |
        		biome.equals(Biomes.DEEP_COLD_OCEAN) |
        		biome.equals(Biomes.LUKEWARM_OCEAN) |
        		biome.equals(Biomes.DEEP_LUKEWARM_OCEAN) |
        		biome.equals(Biomes.WARM_OCEAN) |
        		biome.equals(Biomes.DEEP_WARM_OCEAN) |
        		biome.equals(Biomes.OCEAN) |
        		biome.equals(Biomes.DEEP_OCEAN);
        
        if (isBeach)
        	return biome;
        
        int seaLevel = this.settings.getSeaLevel();
        double swampChance = this.swampChance.getNoise((double)x * 0.0125, (double)z * 0.0125);
        swampChance = MathHelper.clamp(swampChance, 0.0, 1.0);
        double temperature = this.biomeProvider.getTemperature(x, z);
        double humidity = this.biomeProvider.getHumidity(x, z);
        
        if (isOcean && y >= seaLevel)
        {
        	// /tp 2709 71 -1119
        	if (this.biomeProvider.getLandBiome(temperature, humidity, this.biomeProvider.getBiomeChance(x, z, true)).equals(Biomes.BADLANDS))
        		biome = NewBiomes.RED_BEACH;
        	else
        		biome = this.biomeProvider.getBeach(x, z);
        	return biome;
        }
    	if (!isOcean && temperature > 0.5 && humidity > 0.675 && swampChance < 0.375 - 0.25 * ((MathHelper.clamp(temperature, 0.5, 1.0) - 0.5) * 2.0) && y <= seaLevel + 3)
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
    protected void func_222548_a(double[] p_222548_1_, int x, int z) {
       double coordScale = this.settings.getCoordScale();
       double heightScale = this.settings.getHeightScale();
       double depthBase = this.settings.getDepthBaseSize();
       double d3 = 4.277574920654297D;
       int i = -10;
       int j = 3;
       this.func_222546_a(p_222548_1_, x, z, coordScale, heightScale, depthBase, d3, j, i);
    }

    
    //The only reason it's here is because of func_222552_a() being private in NoiseChunkGenerator
    @Override
    protected void func_222546_a(double[] p_222546_1_, int x, int z, double coordScale, double heightScale, double depthBase, double p_222546_10_, int p_222546_12_, int p_222546_13_) {
       double[] adouble = this.func_222549_a(x, z);
       double d0 = adouble[0];
       double d1 = adouble[1];
       double d2 = this.func_222551_g();
       double d3 = 0.0;

       for(int i = 0; i < this.noiseSizeY + 1; ++i) {
          double d4 = this.func_222552_a(x, i, z, coordScale, heightScale, depthBase, p_222546_10_);
          d4 = d4 - this.func_222545_a(d0, d1, i);
          if ((double)i > d2) {
             d4 = MathHelper.clampedLerp(d4, (double)p_222546_13_, ((double)i - d2) / (double)p_222546_12_);
          } else if ((double)i < d3) {
             d4 = MathHelper.clampedLerp(d4, -30.0D, (d3 - (double)i) / (d3 - 1.0D));
          }

          p_222546_1_[i] = d4;
       }

    }

    private double func_222552_a(int x, int y, int z, double coordScale, double heightScale, double depthBase, double p_222552_10_) {

       double coord = this.settings.getMainNoiseCoordScale();
       double height = this.settings.getMainNoiseHeightScale();
       double d0 = this.minLimitPerlinNoise.getNoise(x * coord, y * height, z * coord);
       double d1 = this.maxLimitPerlinNoise.getNoise(x * coord, y * height, z * coord);
       double d2 = this.mainPerlinNoise.getNoise(x * coord / coordScale, y * height / heightScale, z * coord / coordScale);

       return MathHelper.clampedLerp(d0 / this.settings.getLowerLimitScale(), d1 / this.settings.getUpperLimitScale(), (d2 / 10.0D + 1.0D) / 2.0D);
    }

    @Override
    protected double[] func_222549_a(int x, int z) {
       double[] adouble = new double[2];
       double d = 0.0;
       double d1 = 0.0;
       double d2 = 0.0;

       for(int j = -2; j <= 2; ++j)
       {
          for(int k = -2; k <= 2; ++k) 
          {
             Biome biome = this.biomeProvider.getBiome((x + j) * 4, (z + k) * 4);
             double temperature = this.biomeProvider.getTemperature((x + j) * 4, (z + k) * 4);
             double humidity =  this.biomeProvider.getTemperature((x + j) * 4, (z + k) * 4);
             double d4 = 0.75  + (0.0275 - humidity * temperature * 0.0275) * this.settings.getBiomeDepthWeight();
             double d5 = 0.95 + humidity * temperature * 0.3 * this.settings.getBiomeScaleWeight();
             
             boolean isBeach = biome.equals(Biomes.BEACH) | 
               		 biome.equals(Biomes.SNOWY_BEACH) |
               		 biome.equals(NewBiomes.GRAVEL_BEACH) |
               		 biome.equals(NewBiomes.SNOWY_GRAVEL_BEACH) |
               		 biome.equals(NewBiomes.DRY_BEACH) |
               		 biome.equals(NewBiomes.DRY_GRAVEL_BEACH) |
               		 biome.equals(NewBiomes.RED_BEACH);
             if (this.settings.isUseBOP())
             {
            	 isBeach = isBeach | 
            	 biome.equals(BOPBiomes.white_beach.get()) |
            	 biome.equals(BOPBiomes.volcano_edge.get()) |
            	 biome.equals(BOPBiomes.origin_beach.get());
             }
             
             boolean isOcean = 
             		biome.equals(Biomes.FROZEN_OCEAN) |
             		biome.equals(Biomes.DEEP_FROZEN_OCEAN) |
             		biome.equals(Biomes.COLD_OCEAN) |
             		biome.equals(Biomes.DEEP_COLD_OCEAN) |
             		biome.equals(Biomes.LUKEWARM_OCEAN) |
             		biome.equals(Biomes.DEEP_LUKEWARM_OCEAN) |
             		biome.equals(Biomes.WARM_OCEAN) |
             		biome.equals(Biomes.DEEP_WARM_OCEAN) |
             		biome.equals(Biomes.OCEAN) |
             		biome.equals(Biomes.DEEP_OCEAN);
             
             boolean isRiver = this.biomeProvider.getRiver((x + j) * 4, (z + k) * 4);
             
             if (isBeach | isOcean)
             {
            	 // /tp -423 66 -2038
            	 //
            	 double[] landmass = this.biomeProvider.getLandmass((x + j) * 4, (z + k) * 4);
            	 if (landmass[0] > landmass[1])
            		 d4 = this.settings.getBiomeDepthOffset() + MathHelper.clamp((landmass[0] - EvoBiomeProvider.oceanThreshold + EvoBiomeProvider.beachThreshold) * 6.0, -1.8125, 0.0);
            	 else
            		 d4 = this.settings.getBiomeDepthOffset() + MathHelper.clamp((landmass[1] - EvoBiomeProvider.oceanThreshold + EvoBiomeProvider.beachThreshold) * 6.0, -1.8125, 0.0);
            	 d5 = 0.0;
             }
             
             // /tp -1062 70 -1908
             if (isRiver)
             {
            	 if (d4 > this.settings.getBiomeDepthOffset() + Biomes.OCEAN.getDepth() * this.settings.getBiomeDepthWeight())
            		 d4 = this.settings.getBiomeDepthOffset() + Biomes.OCEAN.getDepth() * this.settings.getBiomeDepthWeight();
            	 d5 = 0.0;
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
       double d0 = this.depthNoise.getNoise((double)(x * 200), (double)(z * 200)) / 8000.0D;
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
