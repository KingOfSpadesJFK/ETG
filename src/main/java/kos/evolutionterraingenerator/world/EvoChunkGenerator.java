package kos.evolutionterraingenerator.world;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import kos.evolutionterraingenerator.util.NoiseGeneratorOpenSimplex;
import kos.evolutionterraingenerator.world.gen.GenerateMansion;
import kos.evolutionterraingenerator.world.gen.GenerateMonument;
import kos.evolutionterraingenerator.world.gen.GenerateVillage;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraft.world.gen.structure.WoodlandMansion;

public class EvoChunkGenerator implements IChunkGenerator
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
    private NoiseGeneratorPerlin surfaceNoise;
    public NoiseGeneratorOpenSimplex scaleNoise;
    public NoiseGeneratorOpenSimplex depthNoise;
    public NoiseGeneratorOctaves gravelNoise;
    public NoiseGeneratorOctaves swampNoise;
    //public NoiseGeneratorOctaves riverNoise;
    //public NoiseGeneratorOctaves forestNoise;
    private final World world;
    private final boolean mapFeaturesEnabled;
    private final WorldType terrainType;
    private final double[] heightMap;
    private final float[] biomeWeights;
    private double[] gravelBeach;
    private double[] swamplandChance;
    private ChunkGeneratorSettings settings;
    private IBlockState oceanBlock = Blocks.WATER.getDefaultState();
    private double[] depthBuffer = new double[256];
    private MapGenBase caveGenerator = new MapGenCaves();
    private MapGenStronghold strongholdGenerator = new MapGenStronghold();
    private MapGenVillage villageGenerator = new MapGenVillage();
    private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
    private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();
    private MapGenBase ravineGenerator = new MapGenRavine();
    private StructureOceanMonument oceanMonumentGenerator = new StructureOceanMonument();
    private GenerateMansion woodlandMansionGenerator;
    private Biome[] biomesForGeneration;
    double[] mainNoiseRegion;
    double[] minLimitRegion;
    double[] maxLimitRegion;
    double[] depthRegion;

    public EvoChunkGenerator(World worldIn, long seed, boolean mapFeaturesEnabledIn, String generatorOptions)
    {
    	System.out.println("The smegma under your foreskin has been generated");
        this.world = worldIn;
        this.mapFeaturesEnabled = mapFeaturesEnabledIn;
        this.terrainType = worldIn.getWorldInfo().getTerrainType();
        this.rand = new Random(seed);
        this.minLimitPerlinNoise = new NoiseGeneratorOpenSimplex(this.rand, 16);
        this.maxLimitPerlinNoise = new NoiseGeneratorOpenSimplex(this.rand, 16);
        this.mainPerlinNoise = new NoiseGeneratorOpenSimplex(this.rand, 8);
        this.surfaceNoise = new NoiseGeneratorPerlin(this.rand, 4);
        this.scaleNoise = new NoiseGeneratorOpenSimplex(this.rand, 10);
        this.depthNoise = new NoiseGeneratorOpenSimplex(this.rand, 16);
        //this.forestNoise = new NoiseGeneratorOctaves(this.rand, 8);
        this.gravelNoise = new NoiseGeneratorOctaves(this.rand, 4);
        this.swampNoise = new NoiseGeneratorOctaves(this.rand, 4);
        this.heightMap = new double[825];
        this.biomeWeights = new float[25];
        this.biomeProvider = new BiomeProviderEvo(this.world);

        {
            caveGenerator = net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(caveGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.CAVE);
            strongholdGenerator = (MapGenStronghold)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(strongholdGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.STRONGHOLD);
            villageGenerator = (MapGenVillage)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(villageGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.VILLAGE);
            mineshaftGenerator = (MapGenMineshaft)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(mineshaftGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.MINESHAFT);
            scatteredFeatureGenerator = (MapGenScatteredFeature)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(scatteredFeatureGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.SCATTERED_FEATURE);
            ravineGenerator = net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(ravineGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.RAVINE);
            oceanMonumentGenerator = (StructureOceanMonument)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(oceanMonumentGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.OCEAN_MONUMENT);
            //woodlandMansionGenerator = (WoodlandMansion)net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(woodlandMansionGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.WOODLAND_MANSION);
        }

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
        /*
        net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextOverworld ctx =
                new net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextOverworld(minLimitPerlinNoise, maxLimitPerlinNoise, mainPerlinNoise, surfaceNoise, scaleNoise, depthNoise, forestNoise);
        ctx = net.minecraftforge.event.terraingen.TerrainGen.getModdedNoiseGenerators(worldIn, this.rand, ctx);
        this.minLimitPerlinNoise = ctx.getLPerlin1();
        this.maxLimitPerlinNoise = ctx.getLPerlin2();
        this.mainPerlinNoise = ctx.getPerlin();
        this.surfaceNoise = ctx.getHeight();
        this.scaleNoise = ctx.getScale();
        this.depthNoise = ctx.getDepth();
        this.forestNoise = ctx.getForest();
        */
    }

    public void setBlocksInChunk(int x, int z, ChunkPrimer primer)
    {
        this.biomesForGeneration = this.biomeProvider.getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10, 4, 4, true);
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
        this.gravelBeach = gravelNoise.generateNoiseOctaves(null, x * 16, z * 16, 16, 16, 0.0125, 0.0125, 0.5);
        this.swamplandChance = swampNoise.generateNoiseOctaves(null, x * 16, 16, z * 16, 16, 1, 16, 0.00625, 1.0, 0.00625);
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
        double d0 = 0.03125D;
        this.depthBuffer = this.surfaceNoise.getRegion(this.depthBuffer, (double)(x * 16), (double)(z * 16), 16, 16, 0.0625D, 0.0625D, 1.0D);

        for (int i = 0; i < 16; ++i)
        {
            for (int j = 0; j < 16; ++j)
            {
            	Biome biome = biomesIn[j + i * 16];
            	double noiseVal = this.depthBuffer[j + i * 16];
            	if (biome.getBiomeName().contains("Roofed Forest") || biome.getBiomeName().contains("Jungle") || biome.getBiomeName().contains("Beach") || biome.getBiomeName().contains("Mesa"))
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
        int k = (int)(noiseVal / 2.5D + 3.0D + rand.nextDouble() * 0.25D);
        int l = x & 15;
        int i1 = z & 15;
        double grav = gravelBeach[i1 * 16 + l] + rand.nextDouble() * 0.5D;
        grav = MathHelper.clamp(grav, 0.0, 1.0);
        double swamp = swamplandChance[i1 * 16 + l];
        swamp = MathHelper.clamp(swamp, 0.0, 1.0);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

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
                    	//Weird how the if-statement for Beta beaches is still here...... 
                        else if (j1 >= i - 4 && j1 <= i + 1 )
                        {
                            iblockstate = biome.topBlock;
                            iblockstate1 = biome.fillerBlock;
                        }
                    	if ((biome.getBiomeName().contains("Roofed Forest") || biome.getBiomeName().contains("Jungle"))&& j1 <= i + 3 && swamp < 0.3125)
                    	{
                    		this.biomesForGeneration[l * 16 + i1] = Biomes.SWAMPLAND;
                    		biome = Biomes.SWAMPLAND;
                            double randVal = rand.nextDouble() * 0.25D;
                            if (swamp + randVal > 0.175 && swamp + randVal < 0.275 && j1 == i - 1)
                            {
                                iblockstate = AIR;
                                iblockstate1 = biome.fillerBlock;
                            }
                    	}
                    	if (biome.getBiomeName().contains("Mesa"))
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
                    	if (grav < 0.275 && biome.getBiomeName().contains("Beach"))
                    	{
                            iblockstate = GRAVEL;
                            iblockstate1 = GRAVEL;
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
        this.rand.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
        ChunkPrimer chunkprimer = new ChunkPrimer();
        this.setBlocksInChunk(x, z, chunkprimer);
        this.biomesForGeneration = this.biomeProvider.getBiomes(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        this.replaceBiomeBlocks(x, z, chunkprimer, this.biomesForGeneration);

        if (this.settings.useCaves)
        {
            this.caveGenerator.generate(this.world, x, z, chunkprimer);
        }

        if (this.settings.useRavines)
        {
            this.ravineGenerator.generate(this.world, x, z, chunkprimer);
        }

        if (this.mapFeaturesEnabled)
        {
            if (this.settings.useMineShafts)
            {
                this.mineshaftGenerator.generate(this.world, x, z, chunkprimer);
            }

            if (this.settings.useVillages)
            {
                this.villageGenerator.generate(this.world, x, z, chunkprimer);
            }

            if (this.settings.useStrongholds)
            {
                this.strongholdGenerator.generate(this.world, x, z, chunkprimer);
            }

            if (this.settings.useTemples)
            {
                this.scatteredFeatureGenerator.generate(this.world, x, z, chunkprimer);
            }

            if (this.settings.useMonuments)
            {
                this.oceanMonumentGenerator.generate(this.world, x, z, chunkprimer);
            }

            if (this.settings.useMansions)
            {
                //this.woodlandMansionGenerator.generate(this.world, x, z, chunkprimer);
            }
        }

        Chunk chunk = new Chunk(this.world, chunkprimer, x, z);
        byte[] abyte = chunk.getBiomeArray();

        for (int i = 0; i < abyte.length; ++i)
        {
            abyte[i] = (byte)Biome.getIdForBiome(this.biomesForGeneration[i]);
        }

        chunk.generateSkylightMap();
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
                float f2 = 0.0F;
                float f3 = 0.0F;
                float f4 = 0.0F;
                int i1 = 2;
                //Biome biome = this.biomesForGeneration[k + 2 + (l + 2) * 10];

                for (int k1 = -2; k1 <= 2; ++k1)
                {
                    for (int l1 = -2; l1 <= 2; ++l1)
                    {
                        Biome biome1 = this.biomesForGeneration[k + k1 + 2 + (l + l1 + 2) * 10];
                        float tempVal = (float) temps[k + k1 + 2 + (l + l1 + 2) * 10];
                        float humidVal = (float) humidities[k + k1 + 2 + (l + l1 + 2) * 10];
                        //float f5 = this.settings.biomeDepthOffSet + biome1.getBaseHeight() * this.settings.biomeDepthWeight;
                        //float f6 = this.settings.biomeScaleOffset + biome1.getHeightVariation() * this.settings.biomeScaleWeight;
                    	float f5 = 0.75F * this.settings.biomeDepthWeight;
                    	float f6 = 1.125F * this.settings.biomeScaleWeight;

                        float f7 = (this.biomeWeights[k1 + 2 + (l1 + 2) * 5] - (humidVal * tempVal) )/ (f5 + 2.0F);
                        
                    	if (biome1.getBiomeName().contains("Ocean") | biome1.getBiomeName().contains("Beach"))
                    	{
                    		if (biome1.getBiomeName().contains("Beach"))
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
                    	//System.out.println("River chance: " + valueOfRivia);
                    	//System.out.println("Clamped from: " + this.riverChance[k + k1 + 2 + (l + l1 + 2) * 10]);
                    	//System.out.println("this.depthRegion: " + this.depthRegion[k1 + 2 + (l1 + 2) * 5]);
                    	int riverIndex = (k + k1 + 2) + (l + l1 + 2) * 10;
                    	if (rivers[riverIndex] && !biome1.getBiomeName().contains("Ocean"))
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
        BlockFalling.fallInstantly = true;
        int i = x * 16;
        int j = z * 16;
        BlockPos blockpos = new BlockPos(i, 0, j);
        Biome biome = this.world.getBiome(blockpos.add(16, 0, 16));
        this.rand.setSeed(this.world.getSeed());
        long k = this.rand.nextLong() / 2L * 2L + 1L;
        long l = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long)x * k + (long)z * l ^ this.world.getSeed());
        boolean flag = false;
        ChunkPos chunkpos = new ChunkPos(x, z);

        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.world, this.rand, x, z, flag);

        if (this.mapFeaturesEnabled)
        {
            if (this.settings.useMineShafts)
            {
                this.mineshaftGenerator.generateStructure(this.world, this.rand, chunkpos);
            }

            if (this.settings.useVillages)
            {
                flag = this.villageGenerator.generateStructure(this.world, this.rand, chunkpos);
            }

            if (this.settings.useStrongholds)
            {
                this.strongholdGenerator.generateStructure(this.world, this.rand, chunkpos);
            }

            if (this.settings.useTemples)
            {
                this.scatteredFeatureGenerator.generateStructure(this.world, this.rand, chunkpos);
            }

            if (this.settings.useMonuments)
            {
                this.oceanMonumentGenerator.generateStructure(this.world, this.rand, chunkpos);
            }

            if (this.settings.useMansions)
            {
                //this.woodlandMansionGenerator.generateStructure(this.world, this.rand, chunkpos);
            }
        }

        if (biome != Biomes.DESERT && biome != Biomes.DESERT_HILLS && this.settings.useWaterLakes && !flag && this.rand.nextInt(this.settings.waterLakeChance) == 0)
        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAKE))
        {
            int i1 = this.rand.nextInt(16) + 8;
            int j1 = this.rand.nextInt(256);
            int k1 = this.rand.nextInt(16) + 8;
            (new WorldGenLakes(Blocks.WATER)).generate(this.world, this.rand, blockpos.add(i1, j1, k1));
        }

        if (!flag && this.rand.nextInt(this.settings.lavaLakeChance / 10) == 0 && this.settings.useLavaLakes)
        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAVA))
        {
            int i2 = this.rand.nextInt(16) + 8;
            int l2 = this.rand.nextInt(this.rand.nextInt(248) + 8);
            int k3 = this.rand.nextInt(16) + 8;

            if (l2 < this.world.getSeaLevel() || this.rand.nextInt(this.settings.lavaLakeChance / 8) == 0)
            {
                (new WorldGenLakes(Blocks.LAVA)).generate(this.world, this.rand, blockpos.add(i2, l2, k3));
            }
        }

        if (this.settings.useDungeons)
        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.DUNGEON))
        {
            for (int j2 = 0; j2 < this.settings.dungeonChance; ++j2)
            {
                int i3 = this.rand.nextInt(16) + 8;
                int l3 = this.rand.nextInt(256);
                int l1 = this.rand.nextInt(16) + 8;
                (new WorldGenDungeons()).generate(this.world, this.rand, blockpos.add(i3, l3, l1));
            }
        }

        biome.decorate(this.world, this.rand, new BlockPos(i, 0, j));
        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ANIMALS))
        WorldEntitySpawner.performWorldGenSpawning(this.world, biome, i + 8, j + 8, 16, 16, this.rand);
        blockpos = blockpos.add(8, 0, 8);

        if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ICE))
        {
        for (int k2 = 0; k2 < 16; ++k2)
        {
            for (int j3 = 0; j3 < 16; ++j3)
            {
                BlockPos blockpos1 = this.world.getPrecipitationHeight(blockpos.add(k2, 0, j3));
                BlockPos blockpos2 = blockpos1.down();

                if (this.world.canBlockFreezeWater(blockpos2))
                {
                    this.world.setBlockState(blockpos2, Blocks.ICE.getDefaultState(), 2);
                }

                if (this.world.canSnowAt(blockpos1, true))
                {
                    this.world.setBlockState(blockpos1, Blocks.SNOW_LAYER.getDefaultState(), 2);
                }
            }
        }
        }//Forge: End ICE

        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.world, this.rand, x, z, flag);

        BlockFalling.fallInstantly = false;
    }

    /**
     * Called to generate additional structures after initial worldgen, used by ocean monuments
     */
    public boolean generateStructures(Chunk chunkIn, int x, int z)
    {
        boolean flag = false;

        if (this.settings.useMonuments && this.mapFeaturesEnabled && chunkIn.getInhabitedTime() < 3600L)
        {
            flag |= this.oceanMonumentGenerator.generateStructure(this.world, this.rand, new ChunkPos(x, z));
        }

        return flag;
    }

    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        Biome biome = this.world.getBiome(pos);

        if (this.mapFeaturesEnabled)
        {
            if (creatureType == EnumCreatureType.MONSTER && this.scatteredFeatureGenerator.isSwampHut(pos))
            {
                return this.scatteredFeatureGenerator.getMonsters();
            }

            if (creatureType == EnumCreatureType.MONSTER && this.settings.useMonuments && this.oceanMonumentGenerator.isPositionInStructure(this.world, pos))
            {
                return this.oceanMonumentGenerator.getMonsters();
            }
        }

        return biome.getSpawnableList(creatureType);
    }

    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos)
    {
        if (!this.mapFeaturesEnabled)
        {
            return false;
        }
        else if ("Stronghold".equals(structureName) && this.strongholdGenerator != null)
        {
            return this.strongholdGenerator.isInsideStructure(pos);
        }
        //else if ("Mansion".equals(structureName) && this.woodlandMansionGenerator != null)
        //{
            //return this.woodlandMansionGenerator.isInsideStructure(pos);
        //}
        else if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null)
        {
            return this.oceanMonumentGenerator.isInsideStructure(pos);
        }
        else if ("Village".equals(structureName) && this.villageGenerator != null)
        {
            return this.villageGenerator.isInsideStructure(pos);
        }
        else if ("Mineshaft".equals(structureName) && this.mineshaftGenerator != null)
        {
            return this.mineshaftGenerator.isInsideStructure(pos);
        }
        else
        {
            return "Temple".equals(structureName) && this.scatteredFeatureGenerator != null ? this.scatteredFeatureGenerator.isInsideStructure(pos) : false;
        }
    }

    @Nullable
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored)
    {
        if (!this.mapFeaturesEnabled)
        {
            return null;
        }
        else if ("Stronghold".equals(structureName) && this.strongholdGenerator != null)
        {
            return this.strongholdGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
        }
        //else if ("Mansion".equals(structureName) && this.woodlandMansionGenerator != null)
        //{
            //return this.woodlandMansionGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
        //}
        else if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null)
        {
            return this.oceanMonumentGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
        }
        else if ("Village".equals(structureName) && this.villageGenerator != null)
        {
            return this.villageGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
        }
        else if ("Mineshaft".equals(structureName) && this.mineshaftGenerator != null)
        {
            return this.mineshaftGenerator.getNearestStructurePos(worldIn, position, findUnexplored);
        }
        else
        {
            return "Temple".equals(structureName) && this.scatteredFeatureGenerator != null ? this.scatteredFeatureGenerator.getNearestStructurePos(worldIn, position, findUnexplored) : null;
        }
    }

    /**
     * Recreates data about structures intersecting given chunk (used for example by getPossibleCreatures), without
     * placing any blocks. When called for the first time before any chunk is generated - also initializes the internal
     * state needed by getPossibleCreatures.
     */
    public void recreateStructures(Chunk chunkIn, int x, int z)
    {
        if (this.mapFeaturesEnabled)
        {
            if (this.settings.useMineShafts)
            {
                this.mineshaftGenerator.generate(this.world, x, z, (ChunkPrimer)null);
            }

            if (this.settings.useVillages)
            {
                this.villageGenerator.generate(this.world, x, z, (ChunkPrimer)null);
            }

            if (this.settings.useStrongholds)
            {
                this.strongholdGenerator.generate(this.world, x, z, (ChunkPrimer)null);
            }

            if (this.settings.useTemples)
            {
                this.scatteredFeatureGenerator.generate(this.world, x, z, (ChunkPrimer)null);
            }

            if (this.settings.useMonuments)
            {
                this.oceanMonumentGenerator.generate(this.world, x, z, (ChunkPrimer)null);
            }

            if (this.settings.useMansions)
            {
                //this.woodlandMansionGenerator.generate(this.world, x, z, (ChunkPrimer)null);
            }
        }
    }

}
