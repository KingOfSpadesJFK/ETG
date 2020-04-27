package kos.evolutionterraingenerator.world;

import java.rmi.registry.Registry;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import kos.evolutionterraingenerator.util.NoiseGeneratorOpenSimplex;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.OctavesNoiseGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.CatSpawner;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraft.world.spawner.PhantomSpawner;
import net.minecraft.world.spawner.WorldEntitySpawner;

public class EvoChunkGenerator extends OverworldChunkGenerator
{
	   private static final float[] field_222561_h = Util.make(new float[13824], (p_222557_0_) -> {
		      for(int i = 0; i < 24; ++i) {
		         for(int j = 0; j < 24; ++j) {
		            for(int k = 0; k < 24; ++k) {
		               p_222557_0_[i * 24 * 24 + j * 24 + k] = (float)func_222554_b(j - 12, k - 12, i - 12);
		            }
		         }
		      }

		   });
	   private static final float[] field_222576_h = Util.make(new float[25], (p_222575_0_) -> {
		      for(int i = -2; i <= 2; ++i) {
		         for(int j = -2; j <= 2; ++j) {
		            float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
		            p_222575_0_[i + 2 + (j + 2) * 5] = f;
		         }
		      }

		   });
    protected static final BlockState STONE = Blocks.STONE.getDefaultState();
    protected static final BlockState AIR = Blocks.AIR.getDefaultState();
    protected static final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
    protected static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    protected static final BlockState RED_SANDSTONE = Blocks.RED_SANDSTONE.getDefaultState();
    protected static final BlockState SANDSTONE = Blocks.SANDSTONE.getDefaultState();
    protected static final BlockState ICE = Blocks.ICE.getDefaultState();
    protected static final BlockState WATER = Blocks.WATER.getDefaultState();
	
	private EvoGenSettings settings;
	private final BiomeProviderEvo biomeProvider;
	private final Random rand;
    private NoiseGeneratorOpenSimplex minLimitPerlinNoise;
    private NoiseGeneratorOpenSimplex maxLimitPerlinNoise;
    private NoiseGeneratorOpenSimplex mainPerlinNoise;
    private NoiseGeneratorOpenSimplex surfaceNoise;
    public NoiseGeneratorOpenSimplex scaleNoise;
    public NoiseGeneratorOpenSimplex depthNoise;
    public NoiseGeneratorOpenSimplex swampNoise;
    private final IWorld world;
    private final double[] heightMap;
    private final float[] biomeWeights;
    private double[] swamplandChance;
    private double[] depthBuffer = new double[256];
    private Biome[] biomesForGeneration;
    double[] mainNoiseRegion;
    double[] minLimitRegion;
    double[] maxLimitRegion;
    double[] depthRegion;
    
	private final int noiseSizeZ;
	private final int noiseSizeY;
	private final int noiseSizeX;
	private final int verticalNoiseGranularity;
	private final int horizontalNoiseGranularity;
	
	public EvoChunkGenerator(IWorld worldIn, BiomeProviderEvo biomeProviderIn, EvoGenSettings settingsIn) {
		super(worldIn, biomeProviderIn, settingsIn);

		this.world = worldIn;
		this.settings = settingsIn;
		this.biomeProvider = biomeProviderIn;
		this.rand = new Random(world.getSeed());

        this.minLimitPerlinNoise = new NoiseGeneratorOpenSimplex(this.rand, 16);
        this.maxLimitPerlinNoise = new NoiseGeneratorOpenSimplex(this.rand, 16);
        this.mainPerlinNoise = new NoiseGeneratorOpenSimplex(this.rand, 8);
        
        this.surfaceNoise = new NoiseGeneratorOpenSimplex(this.rand, 4);
        this.scaleNoise = new NoiseGeneratorOpenSimplex(this.rand, 10);
        this.depthNoise = new NoiseGeneratorOpenSimplex(this.rand, 16);
        this.swampNoise = new NoiseGeneratorOpenSimplex(new Random(this.rand.nextLong()), 4);
        this.heightMap = new double[825];
        this.biomeWeights = new float[25];
        
        this.horizontalNoiseGranularity = 4;
    	this.verticalNoiseGranularity = 8;
        this.noiseSizeX = 16 / this.horizontalNoiseGranularity;
        this.noiseSizeY = 256 / this.verticalNoiseGranularity;
        this.noiseSizeZ = 16 / this.horizontalNoiseGranularity;

        for (int i = -2; i <= 2; ++i)
        {
            for (int j = -2; j <= 2; ++j)
            {
                float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
                this.biomeWeights[i + 2 + (j + 2) * 5] = f;
            }
        }
		
	}

	   private static double func_222554_b(int p_222554_0_, int p_222554_1_, int p_222554_2_) {
	      double d0 = (double)(p_222554_0_ * p_222554_0_ + p_222554_2_ * p_222554_2_);
	      double d1 = (double)p_222554_1_ + 0.5D;
	      double d2 = d1 * d1;
	      double d3 = Math.pow(Math.E, -(d2 / 16.0D + d0 / 16.0D));
	      double d4 = -d1 * MathHelper.fastInvSqrt(d2 / 2.0D + d0 / 2.0D) / 2.0D;
	      return d4 * d3;
	   }

	public void makeBase(IWorld worldIn, IChunk chunkIn)
	{
		super.makeBase(worldIn, chunkIn);
		int x = chunkIn.getPos().x;
		int z = chunkIn.getPos().z;
		//setBlocksInChunk(chunkIn);
		ChunkPrimer primer = (ChunkPrimer) chunkIn;
        //this.biomesForGeneration = this.biomeProvider.getBiomesForGeneration(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        this.biomesForGeneration = new Biome[16*16];
		replaceBiomeBlocks(primer);
	}
	
	public EvoGenSettings getSettings()
	{
		return this.settings;
	}

    public void replaceBiomeBlocks(ChunkPrimer primer)
    {
    	int x = primer.getPos().x;
    	int z = primer.getPos().z;
    	Biome[] aBiome = new Biome[16*16];
        this.swamplandChance = swampNoise.generateNoiseOctaves(null, x * 16, z * 16, 16, 16, 0.0125, 0.0125);
        this.depthBuffer = this.surfaceNoise.generateNoiseOctaves(this.depthBuffer,x * 16, z * 16, 16, 16, 0.0875D, 0.0875D);

        for (int i = 0; i < 16; ++i)
        {
            for (int j = 0; j < 16; ++j)
            {
            	Biome biome = this.biomeProvider.getBiome(x * 16 + i, z * 16 + j);
    			this.biomesForGeneration[i * 16 + j] = biome;
            	double noiseVal = this.depthBuffer[i * 16 + j];
            	ResourceLocation biomeid = biome.getRegistryName();
            	
                boolean isBeach = biomeid.equals(Biomes.BEACH.getRegistryName()) | biomeid.equals(Biomes.SNOWY_BEACH.getRegistryName());
                boolean isOcean = 
                		biomeid.equals(Biomes.FROZEN_OCEAN.getRegistryName()) |
                		biomeid.equals(Biomes.DEEP_FROZEN_OCEAN.getRegistryName()) |
                		biomeid.equals(Biomes.COLD_OCEAN.getRegistryName()) |
                		biomeid.equals(Biomes.DEEP_COLD_OCEAN.getRegistryName()) |
                		biomeid.equals(Biomes.LUKEWARM_OCEAN.getRegistryName()) |
                		biomeid.equals(Biomes.DEEP_LUKEWARM_OCEAN.getRegistryName()) |
                		biomeid.equals(Biomes.WARM_OCEAN.getRegistryName()) |
                		biomeid.equals(Biomes.DEEP_WARM_OCEAN.getRegistryName()) |
                		biomeid.equals(Biomes.OCEAN.getRegistryName()) |
                		biomeid.equals(Biomes.DEEP_OCEAN.getRegistryName());
                
                if (!isOcean && !isBeach)
                {
                	setBiome(biome, this.rand, primer, x * 16 + i, z * 16 + j, noiseVal);
                }
            	aBiome[j * 16 + i] = this.biomesForGeneration[i * 16 + j];
            }
        }
        primer.setBiomes(aBiome);
    }
    
    //Sets biomes according to the conditions of the land
    private void setBiome(Biome biome, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal)
    {
        int seaLevel = this.settings.getSeaLevel();
        int xInChunk = x & 15;
        int zInChunk = z & 15;
        double swamp = swamplandChance[xInChunk * 16 + zInChunk] * 0.5 + 0.5;
        swamp = MathHelper.clamp(swamp, 0.0, 1.0);
        double temperature = this.biomeProvider.getTemperature(x, z);
        double humidity = this.biomeProvider.getHumidity(x, z);

        ResourceLocation biomeid = biome.getRegistryName();
        
        for (int i = 255; i >= 0; i--)
        {
            BlockState block = chunkPrimerIn.getBlockState(new BlockPos(xInChunk, i, zInChunk));
        	if (block.getBlock() == Blocks.STONE)
            {
            	if (temperature > 0.5 && humidity > 0.675 && swamp < 0.375 && i <= seaLevel + 3)
            	{
            		biome = Biomes.SWAMP;
                    double randVal = rand.nextDouble() * 0.25D;
                    if (swamp + randVal > 0.175 && swamp + randVal < 0.275 && i == seaLevel - 1)
                    	chunkPrimerIn.setBlockState(new BlockPos(xInChunk, i, zInChunk), WATER, false);
            	}
            	else if (biomeid.equals(Biomes.BADLANDS.getRegistryName()))
            	{
            		if (i <= seaLevel + 50)
            		{
                		biome = Biomes.BADLANDS;
            		}
            		else
            		{
                		biome = Biomes.WOODED_BADLANDS_PLATEAU;
            		}
            	}
    			this.biomesForGeneration[xInChunk * 16 + zInChunk] = biome;
        		return;
            }
        }
    }
    
    protected Biome getBiome(WorldGenRegion worldRegionIn, BlockPos pos) {
    	return worldRegionIn.getBiome(pos);
     }
	
	/* 1.14 GENERATION METHODS */

    protected void func_222548_a(double[] p_222548_1_, int x, int z) {
       double coordScale = this.settings.getCoordScale();
       double heightScale = this.settings.getHeightScale();
       double depthBase = this.settings.getDepthBaseSize();
       double d3 = 4.277574920654297D;
       int i = -10;
       int j = 3;
       this.func_222546_a(p_222548_1_, x, z, coordScale, heightScale, depthBase, d3, j, i);
    }

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
       double d0 = 0.0D;
       double d1 = 0.0D;
       double d2 = 0.0D;
       double d3 = 1.0D;
       double coord = this.settings.getMainNoiseCoordScale();
       double height = this.settings.getMainNoiseHeightScale();

       for(int i = 0; i < 16; ++i) {
          double d4 = (double)x * coord * d3;
          double d5 = (double)y * height * d3;
          double d6 = (double)z * coord * d3;
          
          long k = MathHelper.lfloor(d4);
          long l = MathHelper.lfloor(d6);
          d4 = d4 - (double)k;
          d6 = d6 - (double)l;
          k = k % 16777216L;
          l = l % 16777216L;
          d4 = d4 + (double)k;
          d6 = d6 + (double)l;
          
          d0 += this.minLimitPerlinNoise.getOpenSimplexOctave(i).eval(d4, d5, d6) / d3;
          d1 += this.maxLimitPerlinNoise.getOpenSimplexOctave(i).eval(d4, d5, d6) / d3;
          if (i < 8) 
          {
              d4 = (double)x * coord / coordScale * d3;
              d5 = (double)y * height / heightScale * d3;
              d6 = (double)z * coord / coordScale * d3;
              
              k = MathHelper.lfloor(d4);
              l = MathHelper.lfloor(d6);
              d4 = d4 - (double)k;
              d6 = d6 - (double)l;
              k = k % 16777216L;
              l = l % 16777216L;
              d4 = d4 + (double)k;
              d6 = d6 + (double)l;
              d2 += this.mainPerlinNoise.getOpenSimplexOctave(i).eval(d4, d5, d6) / d3;
          }

          d3 /= 2.0D;
       }

       return MathHelper.clampedLerp(d0 / this.settings.getLowerLimitScale(), d1 / this.settings.getUpperLimitScale(), (d2 / 10.0D + 1.0D) / 2.0D);
    }

    protected double[] func_222549_a(int x, int z) {
       double[] adouble = new double[2];
       float f = 0.0F;
       float f1 = 0.0F;
       float f2 = 0.0F;
       //float f3 = this.biomeProvider.func_222366_b(p_222549_1_, p_222549_2_).getDepth();

       for(int j = -2; j <= 2; ++j) {
          for(int k = -2; k <= 2; ++k) {
             Biome biome = this.biomeProvider.getBiome((x + j) * 4, (z + k) * 4);
             float temperature =  (float) this.biomeProvider.getTemperature((x + j) * 4, (z + k) * 4);
             float humidity =  (float) this.biomeProvider.getTemperature((x + j) * 4, (z + k) * 4);
             ResourceLocation biomeid = biome.getRegistryName();
             float f4 = 0.75F * this.settings.getBiomeDepthWeight() + (0.0025F - temperature * 0.0025F);
             float f5 = 0.75F * this.settings.getBiomeScaleWeight() + humidity * 0.0025F;

             float f6 = field_222576_h[j + 2 + (k + 2) * 5] / (f4 + 2.0F);
             
             boolean isBeach = biomeid.equals(Biomes.BEACH.getRegistryName()) | biomeid.equals(Biomes.SNOWY_BEACH.getRegistryName());
             
             boolean isOcean = 
             		biomeid.equals(Biomes.FROZEN_OCEAN.getRegistryName()) |
             		biomeid.equals(Biomes.DEEP_FROZEN_OCEAN.getRegistryName()) |
             		biomeid.equals(Biomes.COLD_OCEAN.getRegistryName()) |
             		biomeid.equals(Biomes.DEEP_COLD_OCEAN.getRegistryName()) |
             		biomeid.equals(Biomes.LUKEWARM_OCEAN.getRegistryName()) |
             		biomeid.equals(Biomes.DEEP_LUKEWARM_OCEAN.getRegistryName()) |
             		biomeid.equals(Biomes.WARM_OCEAN.getRegistryName()) |
             		biomeid.equals(Biomes.DEEP_WARM_OCEAN.getRegistryName()) |
             		biomeid.equals(Biomes.OCEAN.getRegistryName()) |
             		biomeid.equals(Biomes.DEEP_OCEAN.getRegistryName());
             
         	if (isBeach | isOcean)
         	{
         		if (isBeach)
         		{
         			f4 = this.settings.getBiomeDepthOffset() + Biomes.BEACH.getDepth() * this.settings.getBiomeDepthWeight();
                    f5 = this.settings.getBiomeScaleOffset() + Biomes.BEACH.getScale() * this.settings.getBiomeScaleWeight();
         		}
         		else 
         		{
                    f4 = this.settings.getBiomeDepthOffset() + biome.getDepth() * this.settings.getBiomeDepthWeight();
                    f5 = this.settings.getBiomeScaleOffset() + biome.getScale() * this.settings.getBiomeScaleWeight();
         		}
                f6 = field_222576_h[j + 2 + (k + 2) * 5] / (f4 + 2.0F);
                 
                if (biome.getDepth() > 0.125F)
                {
                    f6 /= 2.0F;
                }
         	}

             f += f5 * f6;
             f1 += f4 * f6;
             f2 += f6;
          }
       }

       f = f / f2;
       f1 = f1 / f2;
       f = f * 0.9F + 0.1F;
       f1 = (f1 * 4.0F - 1.0F) / 8.0F;
       adouble[0] = (double)f1 + this.func_222574_c(x, z);
       adouble[1] = (double)f;
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
