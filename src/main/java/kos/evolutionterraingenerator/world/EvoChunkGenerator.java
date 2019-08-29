package kos.evolutionterraingenerator.world;

import java.rmi.registry.Registry;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import kos.evolutionterraingenerator.util.NoiseGeneratorOpenSimplex;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class EvoChunkGenerator extends ChunkGeneratorOverworld
{
	private final BiomeProviderEvo biomeProvider;
    protected static final IBlockState STONE = Blocks.STONE.getDefaultState();
    protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
    protected static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
    protected static final IBlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    protected static final IBlockState RED_SANDSTONE = Blocks.RED_SANDSTONE.getDefaultState();
    protected static final IBlockState SANDSTONE = Blocks.SANDSTONE.getDefaultState();
    protected static final IBlockState ICE = Blocks.ICE.getDefaultState();
    protected static final IBlockState WATER = Blocks.WATER.getDefaultState();
    private final Random rand;
    private NoiseGeneratorOpenSimplex minLimitPerlinNoise;
    private NoiseGeneratorOpenSimplex maxLimitPerlinNoise;
    private NoiseGeneratorOpenSimplex mainPerlinNoise;
    private NoiseGeneratorOpenSimplex surfaceNoise;
    public NoiseGeneratorOpenSimplex scaleNoise;
    public NoiseGeneratorOpenSimplex depthNoise;
    public NoiseGeneratorOpenSimplex gravelNoise;
    public NoiseGeneratorOpenSimplex swampNoise;
    public NoiseGeneratorOpenSimplex lakeBeachNoise;
    public NoiseGeneratorOpenSimplex gravelLakeNoise;
    //public NoiseGeneratorOctaves riverNoise;
    //public NoiseGeneratorOctaves forestNoise;
    private final World world;
    private final boolean mapFeaturesEnabled;
    private final double[] heightMap;
    private final float[] biomeWeights;
    private double[] gravelBeach;
    private double[] swamplandChance;
    private double[] lakeBeachChance;
    private double[] gravelLakeChance;
    private ChunkGeneratorSettings settings;
    private IBlockState oceanBlock = Blocks.WATER.getDefaultState();
    private double[] depthBuffer = new double[256];
    private Biome[] biomesForGeneration;
    double[] mainNoiseRegion;
    double[] minLimitRegion;
    double[] maxLimitRegion;
    double[] depthRegion;

    public EvoChunkGenerator(World worldIn, long seed, boolean mapFeaturesEnabledIn, String generatorOptions)
    {
    	super(worldIn, seed, mapFeaturesEnabledIn, generatorOptions);
    	System.out.println("The smegma under your foreskin has been generated");
        this.world = worldIn;
        this.mapFeaturesEnabled = mapFeaturesEnabledIn;
        this.rand = new Random(seed);
        this.minLimitPerlinNoise = new NoiseGeneratorOpenSimplex(this.rand, 16);
        this.maxLimitPerlinNoise = new NoiseGeneratorOpenSimplex(this.rand, 16);
        this.mainPerlinNoise = new NoiseGeneratorOpenSimplex(this.rand, 8);
        this.surfaceNoise = new NoiseGeneratorOpenSimplex(this.rand, 4);
        this.scaleNoise = new NoiseGeneratorOpenSimplex(this.rand, 10);
        this.depthNoise = new NoiseGeneratorOpenSimplex(this.rand, 16);
        this.gravelNoise = new NoiseGeneratorOpenSimplex(new Random(this.rand.nextLong()), 4);
        this.swampNoise = new NoiseGeneratorOpenSimplex(new Random(this.rand.nextLong()), 4);
        this.lakeBeachNoise = new NoiseGeneratorOpenSimplex(new Random(this.rand.nextLong()), 4);
        this.gravelLakeNoise = new NoiseGeneratorOpenSimplex(new Random(this.rand.nextLong()), 4);
        this.heightMap = new double[825];
        this.biomeWeights = new float[25];
        this.biomeProvider = new BiomeProviderEvo(this.world);

        for (int i = -2; i <= 2; ++i)
        {
            for (int j = -2; j <= 2; ++j)
            {
                float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
                this.biomeWeights[i + 2 + (j + 2) * 5] = f;
            }
        }

        if (generatorOptions != null)
        {
            this.settings = ChunkGeneratorSettings.Factory.jsonToFactory(generatorOptions).build();
            this.oceanBlock = this.settings.useLavaOceans ? Blocks.LAVA.getDefaultState() : Blocks.WATER.getDefaultState();
            worldIn.setSeaLevel(this.settings.seaLevel);
        }
    }

    public void setBlocksInChunk(int x, int z, ChunkPrimer primer)
    {
        this.biomesForGeneration = this.biomeProvider.getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10, 4, 4, true);
        //this.biomeProvider.getBiomesForGeneration(null, x * 4 - 2, z * 4 - 2, 10, 10);
        this.biomeProvider.getRiver(x * 4 - 2, z * 4 - 2, 10, 10);
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
                                    primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, STONE);
                                }
                                else if (i2 * 8 + j2 < this.settings.seaLevel)
                                {
                                    primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, this.oceanBlock);
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

    public void replaceBiomeBlocks(int x, int z, ChunkPrimer primer, Biome[] biomesIn)
    {
        this.biomesForGeneration = this.biomeProvider.getBiomes(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        this.gravelBeach = gravelNoise.generateNoiseOctaves(null, x * 16, z * 16, 16, 16, 0.025, 0.025);
        this.swamplandChance = swampNoise.generateNoiseOctaves(null, x * 16, z * 16, 16, 16, 0.0125, 0.0125);
        this.lakeBeachChance = lakeBeachNoise.generateNoiseOctaves(null, x * 16, z * 16, 16, 16, 0.05, 0.05);
        this.gravelLakeChance = gravelLakeNoise.generateNoiseOctaves(null, x * 16, z * 16, 16, 16, 0.0875, 0.0875);
        //System.out.println("This: " + this);
        //System.out.println("Primer: " + primer);
        //System.out.println("World: " + this.world);
        try
        {
            if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, x, z, primer, this.world)) return;
        } catch (Throwable throwable)
        {
        	System.out.println("Aww shit, here we go again");
            System.out.println("This: " + this);
            System.out.println("Chunk Position: (" + x + ", " + z + ")");
            System.out.println("Primer: " + primer);
            System.out.println("World: " + this.world);
            throw throwable;
        }
        this.depthBuffer = this.surfaceNoise.generateNoiseOctaves(this.depthBuffer,x * 16, z * 16, 16, 16, 0.0875D, 0.0875D);

        for (int i = 0; i < 16; ++i)
        {
            for (int j = 0; j < 16; ++j)
            {
            	Biome biome = biomesIn[j + i * 16];
            	double noiseVal = this.depthBuffer[i + j * 16];
            	int biomeid = Biome.getIdForBiome(biome);
                boolean isBeach = biomeid == Biome.getIdForBiome(Biomes.BEACH) |
            			biomeid == Biome.getIdForBiome(Biomes.STONE_BEACH) |
            			biomeid == Biome.getIdForBiome(Biomes.COLD_BEACH);
                boolean isOcean = biomeid == Biome.getIdForBiome(Biomes.OCEAN) |
            			biomeid == Biome.getIdForBiome(Biomes.DEEP_OCEAN);
                if (!isOcean)
                	generateBiomeTerrain(biomesIn[j + i * 16], this.rand, primer, x * 16 + i, z * 16 + j, noiseVal);
                else
            		biome.genTerrainBlocks(this.world, rand, primer, x * 16 + i, z * 16 + j, noiseVal);
            }
        }
    }
    
    /* Taken from net.minecraft.world.biome.Biome */
    public final void generateBiomeTerrain(Biome biome, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal)
    {
        int i = this.settings.seaLevel;
        IBlockState iblockstate = biome.topBlock;
        IBlockState iblockstate1 = biome.fillerBlock;
        int j = -1;
        int k = (int)((noiseVal * 0.5D) / 2.5D + 3.0D + rand.nextDouble() * 0.25D);
        int l = x & 15;
        int i1 = z & 15;
        double grav = gravelBeach[i1 * 16 + l] + rand.nextDouble() * 0.5D;
        grav = MathHelper.clamp(grav, 0.0, 1.0);
        double swamp = swamplandChance[i1 * 16 + l] * 0.5 + 0.5;
        swamp = MathHelper.clamp(swamp, 0.0, 1.0);
        double lakeBeach = lakeBeachChance[i1 * 16 + l] * 0.25 + 0.5;
        lakeBeach = MathHelper.clamp(lakeBeach, 0.0, 1.0) * 0.99 + noiseVal * 0.01;
        double gravelLake = gravelLakeChance[i1 * 16 + l] * 0.25 + 0.5;
        gravelLake = MathHelper.clamp(gravelLake, 0.0, 1.0) * 0.99 + noiseVal * 0.01;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int biomeid = Biome.getIdForBiome(biome);
        boolean isBeach = biomeid == Biome.getIdForBiome(Biomes.BEACH) |
    			biomeid == Biome.getIdForBiome(Biomes.STONE_BEACH) |
    			biomeid == Biome.getIdForBiome(Biomes.COLD_BEACH);
        double temperature = this.biomeProvider.temperatures[i1 * 16 + l];
        double humidity = this.biomeProvider.humidities[i1 * 16 + l];

        for (int j1 = 255; j1 >= 0; --j1)
        {
            if (j1 <= rand.nextInt(5))
            {
                chunkPrimerIn.setBlockState(i1, j1, l, BEDROCK);
            }
            else
            {
                IBlockState iblockstate2 = chunkPrimerIn.getBlockState(i1, j1, l);

                if (iblockstate2.getMaterial() == Material.AIR)
                {
                    j = -1;
                }
                else if (iblockstate2.getBlock() == Blocks.STONE)
                {
                    if (j == -1)
                    {
                        if (k <= 0)
                        {
                            iblockstate = AIR;
                            iblockstate1 = STONE;
                        }
                    	if (temperature > 0.5 && humidity > 0.675 && swamp < 0.375 && j1 <= i + 3)
                    	{
                    		this.biomesForGeneration[l * 16 + i1] = Biomes.SWAMPLAND;
                    		biome = Biomes.SWAMPLAND;
                    		biomeid = Biome.getIdForBiome(Biomes.SWAMPLAND);
                            double randVal = rand.nextDouble() * 0.25D;
                            if (swamp + randVal > 0.175 && swamp + randVal < 0.275 && j1 == i - 1)
                            {
                                iblockstate = AIR;
                                iblockstate1 = biome.fillerBlock;
                            }
                    	}
                    	else if (biomeid == Biome.getIdForBiome(Biomes.MESA))
                    	{
                    		if ( j1 <= i + 50)
                    		{
                    			this.biomesForGeneration[i1 * 16 + l] = Biomes.MESA_ROCK;
                        		biome = Biomes.MESA_ROCK;
                    		}
                    		else
                    		{
                    			this.biomesForGeneration[i1 * 16 + l] = Biomes.MESA;
                        		biome = Biomes.MESA;
                    		}
                    		biome.genTerrainBlocks(this.world, rand, chunkPrimerIn, x, z, noiseVal);
                    		return;
                    	}
                        if (j1 >= i - 4 && j1 <= i + 1)
                        {
                        	if (biomeid != Biome.getIdForBiome(Biomes.SWAMPLAND) & !isBeach & biomeid != Biome.getIdForBiome(Biomes.DESERT))
                        	{
                        		if (gravelLake < 0.25)
                        		{
                                    iblockstate = GRAVEL;
                                    iblockstate1 = GRAVEL;
                        		}
                        		if (lakeBeach > 0.5)
                        		{
                                    iblockstate = Blocks.SAND.getDefaultState();
                                    iblockstate1 = Blocks.SAND.getDefaultState();
                        		}
                        	}
                        }
                        if (isBeach)
                        {
                        	if (grav < 0.275)
                        	{
                                iblockstate = GRAVEL;
                                iblockstate1 = GRAVEL;
                        	}
                        }

                        if (j1 < i && (iblockstate == null || iblockstate.getMaterial() == Material.AIR))
                        {
                            if (biome.getTemperature(blockpos$mutableblockpos.setPos(x, j1, z)) < 0.15F)
                            {
                                iblockstate = ICE;
                            }
                            else
                            {
                                iblockstate = WATER;
                            }
                        }

                        j = k;

                        if (j1 >= i - 1)
                        {
                            chunkPrimerIn.setBlockState(i1, j1, l, iblockstate);
                        }
                        else if (j1 < i - 7 - k)
                        {
                            iblockstate = AIR;
                            iblockstate1 = STONE;
                            chunkPrimerIn.setBlockState(i1, j1, l, GRAVEL);
                        }
                        else
                        {
                            chunkPrimerIn.setBlockState(i1, j1, l, iblockstate1);
                        }
                    }
                    else if (j > 0)
                    {
                        --j;
                        chunkPrimerIn.setBlockState(i1, j1, l, iblockstate1);

                        if (j == 0 && iblockstate1.getBlock() == Blocks.SAND && k > 1)
                        {
                            j = rand.nextInt(4) + Math.max(0, j1 - 63);
                            iblockstate1 = iblockstate1.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND ? RED_SANDSTONE : SANDSTONE;
                        }
                    }
                }
            }
        }
    }

    /**
     * Generates the chunk at the specified position, from scratch
     */
    public Chunk generateChunk(int x, int z)
    {
        Chunk chunk = super.generateChunk(x, z);
        byte[] abyte = chunk.getBiomeArray();

        for (int i = 0; i < abyte.length; ++i)
        {
            abyte[i] = (byte)Biome.getIdForBiome(this.biomesForGeneration[i]);
        }
        return chunk;
    }

    private void generateHeightmap(int x, int y, int z)
    {
        this.depthRegion = this.depthNoise.generateNoiseOctaves(this.depthRegion, x, z, 5, 5, (double)this.settings.depthNoiseScaleX, (double)this.settings.depthNoiseScaleZ);
        float f = 175.0F;
        float f1 = 75.0F;
		double[] temps = biomeProvider.temperatures;
		double[] humidities = biomeProvider.humidities;
	    boolean[] rivers = biomeProvider.isRiver;
        this.mainNoiseRegion = this.mainPerlinNoise.generateNoiseOctaves(this.mainNoiseRegion, x, y, z, 5, 33, 5, (double)(f / 160.0), (double)(f1 / 60.0), (double)(f / 160.0));
        this.minLimitRegion = this.minLimitPerlinNoise.generateNoiseOctaves(this.minLimitRegion, x, y, z, 5, 33, 5, (double)f, (double)f1, (double)f);
        this.maxLimitRegion = this.maxLimitPerlinNoise.generateNoiseOctaves(this.maxLimitRegion, x, y, z, 5, 33, 5, (double)f, (double)f1, (double)f);
        
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
                        int biomeid = Biome.getIdForBiome(biome1);
                        float tempVal1 = (float) temps[k + k1 + 2 + (l + l1 + 2) * 10];
                        float humidVal1 = (float) humidities[k + k1 + 2 + (l + l1 + 2) * 10];
                        //float f5 = this.settings.biomeDepthOffSet + biome1.getBaseHeight() * this.settings.biomeDepthWeight;
                        //float f6 = this.settings.biomeScaleOffset + biome1.getHeightVariation() * this.settings.biomeScaleWeight;
                    	float f5 = 0.75F * this.settings.biomeDepthWeight;
                    	float f6 = 1.125F * this.settings.biomeScaleWeight;

                        float f7 = this.biomeWeights[k1 + 2 + (l1 + 2) * 5] / (f5 + 2.0F);
                        
                        boolean isBeach = biomeid == Biome.getIdForBiome(Biomes.BEACH) |
                    			biomeid == Biome.getIdForBiome(Biomes.STONE_BEACH) |
                    			biomeid == Biome.getIdForBiome(Biomes.COLD_BEACH);
                        boolean isOcean = biomeid == Biome.getIdForBiome(Biomes.OCEAN) |
                    			biomeid == Biome.getIdForBiome(Biomes.DEEP_OCEAN);
                        
                    	if (isBeach | isOcean)
                    	{
                    		if (isBeach)
                    		{
                                f5 = this.settings.biomeDepthOffSet + Biomes.BEACH.getBaseHeight() * this.settings.biomeDepthWeight;
                                f6 = this.settings.biomeScaleOffset + Biomes.BEACH.getHeightVariation() * this.settings.biomeScaleWeight;
                    		}
                    		else 
                    		{
                                f5 = this.settings.biomeDepthOffSet + biome1.getBaseHeight() * this.settings.biomeDepthWeight;
                                f6 = this.settings.biomeScaleOffset + biome1.getHeightVariation() * this.settings.biomeScaleWeight;
                    		}
                            f7 = this.biomeWeights[k1 + 2 + (l1 + 2) * 5] / (f5 + 2.0F);
                            
                            if (biome1.getBaseHeight() > 0.125F)
                            {
                                f7 /= 2.0F;
                            }
                    	}
                    	
                    	int riverIndex = (k + k1 + 2) + (l + l1 + 2) * 10;
                    	if (rivers[riverIndex] && !isOcean)
                    	{
                        	//f5 = -0.75F;
                        	//f6 = 0.0F;
                            //f7 = this.biomeWeights[k1 + 2 + (l1 + 2) * 5] / (f5 + 2.0F) / 2.0F;
                    	}

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
                d8 = d8 * (double)this.settings.baseSize / 8.0D;
                double d0 = (double)this.settings.baseSize + d8 * 4.0D;

                for (int l1 = 0; l1 < 33; ++l1)
                {
                    double d1 = ((double)l1 - d0) * (double)this.settings.stretchY * 128.0D / 256.0D / d9;

                    if (d1 < 0.0D)
                    {
                        d1 *= 4.0D;
                    }

                    double d2 = this.minLimitRegion[i] / (double)this.settings.lowerLimitScale;
                    double d3 = this.maxLimitRegion[i] / (double)this.settings.upperLimitScale;
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

    /**
     * Generate initial structures in this chunk, e.g. mineshafts, temples, lakes, and dungeons
     */
    public void populate(int x, int z)
    {
    	super.populate(x, z);
    }

    /**
     * Called to generate additional structures after initial worldgen, used by ocean monuments
     */
    public boolean generateStructures(Chunk chunkIn, int x, int z)
    {
    	return super.generateStructures(chunkIn, x, z);
    }

    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
    	return super.getPossibleCreatures(creatureType, pos);
    }

    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos)
    {
    	return super.isInsideStructure(worldIn, structureName, pos);
    }

    @Nullable
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored)
    {
    	return super.getNearestStructurePos(worldIn, structureName, position, findUnexplored);
    }

    /**
     * Recreates data about structures intersecting given chunk (used for example by getPossibleCreatures), without
     * placing any blocks. When called for the first time before any chunk is generated - also initializes the internal
     * state needed by getPossibleCreatures.
     */
    public void recreateStructures(Chunk chunkIn, int x, int z)
    {
    	super.recreateStructures(chunkIn, x, z);
    }

}
