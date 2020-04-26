package kos.evolutionterraingenerator.world;

import java.rmi.registry.Registry;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

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
import net.minecraft.village.VillageSiege;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.CatSpawner;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraft.world.spawner.PhantomSpawner;
import net.minecraft.world.spawner.WorldEntitySpawner;

public class EvoChunkGenerator extends OverworldChunkGenerator
{
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

        for (int i = -2; i <= 2; ++i)
        {
            for (int j = -2; j <= 2; ++j)
            {
                float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
                this.biomeWeights[i + 2 + (j + 2) * 5] = f;
            }
        }
		
	}

	public void makeBase(IWorld worldIn, IChunk chunkIn)
	{
		int x = chunkIn.getPos().x;
		int z = chunkIn.getPos().z;
		setBlocksInChunk(chunkIn);
		ChunkPrimer primer = (ChunkPrimer) chunkIn;
        this.biomesForGeneration = this.biomeProvider.getBiomesForGeneration(this.biomesForGeneration, x * 16, z * 16, 16, 16);
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
            	Biome biome = this.biomesForGeneration[j + i * 16];
            	double noiseVal = this.depthBuffer[i + j * 16];
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
                	setBiome(biome, this.rand, primer, x * 16 + i, z * 16 + j, noiseVal);
                aBiome[j + i * 16] = this.biomesForGeneration[j + i * 16];
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
        double swamp = swamplandChance[zInChunk * 16 + xInChunk] * 0.5 + 0.5;
        swamp = MathHelper.clamp(swamp, 0.0, 1.0);
        double temperature = this.biomeProvider.temperatures[zInChunk * 16 + xInChunk];
        double humidity = this.biomeProvider.humidities[zInChunk * 16 + xInChunk];

        ResourceLocation biomeid = biome.getRegistryName();
        
        for (int i = 255; i >= 0; i--)
        {
            BlockState block = chunkPrimerIn.getBlockState(new BlockPos(zInChunk, i, xInChunk));
        	if (block.getBlock() == Blocks.STONE)
            {
            	if (temperature > 0.5 && humidity > 0.675 && swamp < 0.375 && i <= seaLevel + 3)
            	{
            		this.biomesForGeneration[xInChunk * 16 + zInChunk] = Biomes.SWAMP;
            		biomeid = biome.getRegistryName();
                    double randVal = rand.nextDouble() * 0.25D;
                    if (swamp + randVal > 0.175 && swamp + randVal < 0.275 && i == seaLevel - 1)
                    	chunkPrimerIn.setBlockState(new BlockPos(zInChunk, i, xInChunk), WATER, false);
            	}
            	else if (biomeid.equals(Biomes.BADLANDS.getRegistryName()))
            	{
            		if (i <= seaLevel + 50)
            		{
            			this.biomesForGeneration[xInChunk * 16 + zInChunk] = Biomes.BADLANDS;
                		biome = Biomes.BADLANDS;
            		}
            		else
            		{
            			this.biomesForGeneration[zInChunk + xInChunk * 16] = Biomes.WOODED_BADLANDS_PLATEAU;
                		biome = Biomes.WOODED_BADLANDS_PLATEAU;
            		}
            	}
        		return;
            }
        }
    }
    
    protected Biome getBiome(WorldGenRegion worldRegionIn, BlockPos pos) {
    	return worldRegionIn.getBiome(pos);
     }
	
	//GENERATION METHODS
	//Modified version of the generator from 1.12
	
	public void setBlocksInChunk(IChunk chunkIn)
    {
		int x = chunkIn.getPos().x;
		int z = chunkIn.getPos().z;
		this.biomesForGeneration = this.biomeProvider.getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10, 4, 4);
        //this.biomeProvider.getBiomesForGeneration(null, x * 4 - 2, z * 4 - 2, 10, 10);
        //this.biomeProvider.getRiver(x * 4 - 2, z * 4 - 2, 10, 10);
        //this.riverChance = this.riverNoise.generateNoiseOctaves(this.riverChance, x * 4 - 2, z * 4 - 2, 10, 10, 300.0, 300.0, 0.5);
        this.generateHeightmap(x * 4, 0, z * 4);

        for (int i = 0; i < 4; ++i)
        {
            int j = i * 5;
            int k = (i + 1) * 5;

            for (int l = 0; l < 4; ++l)
            {
                int i1 = (j + l) * 33;
                int j1 = (j + l + 1) * 33;
                int k1 = (k + l) * 33;
                int l1 = (k + l + 1) * 33;

                for (int i2 = 0; i2 < 32; ++i2)
                {
                    double d0 = 0.125D;
                    double d1 = this.heightMap[i1 + i2];
                    double d2 = this.heightMap[j1 + i2];
                    double d3 = this.heightMap[k1 + i2];
                    double d4 = this.heightMap[l1 + i2];
                    double d5 = (this.heightMap[i1 + i2 + 1] - d1) * 0.125D;
                    double d6 = (this.heightMap[j1 + i2 + 1] - d2) * 0.125D;
                    double d7 = (this.heightMap[k1 + i2 + 1] - d3) * 0.125D;
                    double d8 = (this.heightMap[l1 + i2 + 1] - d4) * 0.125D;

                    for (int j2 = 0; j2 < 8; ++j2)
                    {
                        double d9 = 0.25D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * 0.25D;
                        double d13 = (d4 - d2) * 0.25D;

                        for (int k2 = 0; k2 < 4; ++k2)
                        {
                            double d14 = 0.25D;
                            double d16 = (d11 - d10) * 0.25D;
                            double lvt_45_1_ = d10 - d16;

                            for (int l2 = 0; l2 < 4; ++l2)
                            {
                                if ((lvt_45_1_ += d16) > 0.0D)
                                {
                                	chunkIn.setBlockState(new BlockPos(i * 4 + k2, i2 * 8 + j2, l * 4 + l2), STONE, false);
                                }
                                else if (i2 * 8 + j2 < this.settings.getSeaLevel())
                                {
                                	chunkIn.setBlockState(new BlockPos(i * 4 + k2, i2 * 8 + j2, l * 4 + l2), this.settings.getOceanBlock(), false);
                                }
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }
	
	 private void generateHeightmap(int x, int y, int z)
    {
        this.depthRegion = this.depthNoise.generateNoiseOctaves(this.depthRegion, x, z, 5, 5, this.settings.getDepthNoiseScaleX(), this.settings.getDepthNoiseScaleZ());
        double coord = this.settings.getMainNoiseCoordScale();
        double height = this.settings.getMainNoiseHeightScale();
		double[] temps = biomeProvider.temperatures;
		double[] humidities = biomeProvider.humidities;
	    //boolean[] rivers = biomeProvider.isRiver;
        this.mainNoiseRegion = this.mainPerlinNoise.generateNoiseOctaves(this.mainNoiseRegion, x, y, z, 5, 33, 5, coord / this.settings.getCoordScale(), height / this.settings.getHeightScale(), coord / this.settings.getCoordScale());
        this.minLimitRegion = this.minLimitPerlinNoise.generateNoiseOctaves(this.minLimitRegion, x, y, z, 5, 33, 5, coord, height, coord);
        this.maxLimitRegion = this.maxLimitPerlinNoise.generateNoiseOctaves(this.maxLimitRegion, x, y, z, 5, 33, 5, coord, height, coord);
        
        int i = 0;
        int j = 0;
        for (int k = 0; k < 5; ++k)
        {
            for (int l = 0; l < 5; ++l)
            {
                float tempVal = (float) temps[k + 2 + (l + 2) * 10];
                float humidVal = (float) humidities[k + 2 + (l + 2) * 10];
                
                float f2 = tempVal * 0.25F;
                float f3 = -humidVal;
                float f4 = tempVal * humidVal;
                
                //Biome biome = this.biomesForGeneration[k + 2 + (l + 2) * 10];
                for (int k1 = -2; k1 <= 2; ++k1)
                {
                    for (int l1 = -2; l1 <= 2; ++l1)
                    {
                        Biome biome1 = this.biomesForGeneration[k + k1 + 2 + (l + l1 + 2) * 10];
                        ResourceLocation biomeid = biome1.getRegistryName();
                        float tempVal1 = (float) temps[k + k1 + 2 + (l + l1 + 2) * 10];
                        float humidVal1 =(float) humidities[k + k1 + 2 + (l + l1 + 2) * 10];
                        //float f5 = this.settings.biomeDepthOffSet + biome1.getBaseHeight() * this.settings.biomeDepthWeight;
                        //float f6 = this.settings.biomeScaleOffset + biome1.getHeightVariation() * this.settings.biomeScaleWeight;
                    	float f5 = 0.75F * this.settings.getBiomeDepthWeight();
                    	float f6 = 1.125F * this.settings.getBiomeScaleWeight();
	                        float f7 = this.biomeWeights[k1 + 2 + (l1 + 2) * 5] / (f5 + 2.0F);
                        
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
                                f5 = this.settings.getBiomeDepthOffset() + Biomes.BEACH.getDepth() * this.settings.getBiomeDepthWeight();
                                f6 = this.settings.getBiomeScaleOffset() + Biomes.BEACH.getScale() * this.settings.getBiomeScaleWeight();
                    		}
                    		else 
                    		{
                                f5 = this.settings.getBiomeDepthOffset() + biome1.getDepth() * this.settings.getBiomeDepthWeight();
                                f6 = this.settings.getBiomeScaleOffset() + biome1.getScale() * this.settings.getBiomeScaleWeight();
                    		}
	                            f7 = this.biomeWeights[k1 + 2 + (l1 + 2) * 5] / (f5 + 2.0F);
                            
                            if (biome1.getDepth() > 0.125F)
                            {
                                f7 /= 2.0F;
                            }
                    	}
                    	
                    	//int riverIndex = (k + k1 + 2) + (l + l1 + 2) * 10;
                    	/*
                    	if (rivers[riverIndex] && !isOcean)
                    	{
                        	//f5 = -0.75F;
                        	//f6 = 0.0F;
                            //f7 = this.biomeWeights[k1 + 2 + (l1 + 2) * 5] / (f5 + 2.0F) / 2.0F;
                    	}
                    	*/
	                        f2 += f6 * f7;
                        f3 += f5 * f7;
                        f4 += f7;
                    }
                }
                f2 = f2 / f4;
                f3 = f3 / f4;
                f2 = f2 * 0.9F + 0.1F;
                f3 = (f3 * 4.0F - 1.0F) / 8.0F;
                double d7 = this.depthRegion[j] / 8000.0D;
                if (d7 < 0.0D)
                {
                    d7 = -d7 * 0.3D;
                }
                d7 = d7 * 3.0D - 2.0D;
                if (d7 < 0.0D)
                {
                    d7 = d7 / 2.0D;
                    if (d7 < -1.0D)
                    {
                        d7 = -1.0D;
                    }
                    d7 = d7 / 1.4D;
                    d7 = d7 / 2.0D;
                }
                else
                {
                    if (d7 > 1.0D)
                    {
                        d7 = 1.0D;
                    }
                    d7 = d7 / 8.0D;
                }
                ++j;
                double d8 = (double)f3;
                double d9 = (double)f2;
                d8 = d8 + d7 * 0.2D;
                d8 = d8 * (double)this.settings.getDepthBaseSize() / 8.0D;
                double d0 = (double)this.settings.getDepthBaseSize() + d8 * 4.0D;
                for (int l1 = 0; l1 < 33; ++l1)
                {
                    double d1 = ((double)l1 - d0) * (double)this.settings.getHeightStretch() * 128.0D / 256.0D / d9;
                    if (d1 < 0.0D)
                    {
                        d1 *= 4.0D;
                    }
                    double d2 = this.minLimitRegion[i] / (double)this.settings.getLowerLimitScale();
                    double d3 = this.maxLimitRegion[i] / (double)this.settings.getUpperLimitScale();
                    double d4 = (this.mainNoiseRegion[i] / 10.0D + 1.0D) / 2.0D;
                    double d5 = MathHelper.clampedLerp(d2, d3, d4) - d1;
                    if (l1 > 29)
                    {
                        double d6 = (double)((float)(l1 - 29) / 3.0F);
                        d5 = d5 * (1.0D - d6) + -10.0D * d6;
                    }
                    this.heightMap[i] = d5;
                    ++i;
                }
            }
        }
    }
}
