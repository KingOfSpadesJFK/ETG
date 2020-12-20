package kos.evolutionterraingenerator.world.gen;

import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import kos.evolutionterraingenerator.core.EvolutionTerrainGenerator;
import kos.evolutionterraingenerator.util.OpenSimplexNoiseOctaves;
import kos.evolutionterraingenerator.util.noise.OpenSimplexNoiseGenerator;
import kos.evolutionterraingenerator.world.biome.EvoBiomeProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DebugChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.INoiseGenerator;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.OctavesNoiseGenerator;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.SimplexNoiseGenerator;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EvoChunkGenerator extends ChunkGenerator 
{
	public static final Codec<EvoChunkGenerator> CODEC = RecordCodecBuilder.create((p_236091_0_) -> {
		return p_236091_0_.group(EvoBiomeProvider.CODEC.fieldOf("environmental_noise").forGetter((p_236096_0_) -> {
			return p_236096_0_.biomeProvider;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((p_236093_0_) -> {
			return p_236093_0_.seed;
		}), DimensionSettings.field_236098_b_.fieldOf("settings").forGetter((p_236090_0_) -> {
			return p_236090_0_.dimensionsettings;
		})).apply(p_236091_0_, p_236091_0_.stable(EvoChunkGenerator::new));
	});
	private static final float[] field_222561_h = Util.make(new float[13824], (p_236094_0_) -> {
		for(int i = 0; i < 24; ++i) {
			for(int j = 0; j < 24; ++j) {
				for(int k = 0; k < 24; ++k) {
					p_236094_0_[i * 24 * 24 + j * 24 + k] = (float)func_222554_b(j - 12, k - 12, i - 12);
				}
			}
		}
	});
	private static final float[] field_236081_j_ = Util.make(new float[25], (p_236092_0_) -> {
		for(int i = -2; i <= 2; ++i) {
			for(int j = -2; j <= 2; ++j) {
				float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
	     	p_236092_0_[i + 2 + (j + 2) * 5] = f;
			}
		}
	});
	public static final Biome[] PLAINS_BIOMES = Util.make(new Biome[1024], (arr) -> 
	{
		for(int i = 0; i < arr.length; i++)
			arr[i] = BiomeRegistry.PLAINS;
	});
			
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private final int verticalNoiseGranularity;
	private final int horizontalNoiseGranularity;
	private final int noiseSizeX;
	private final int noiseSizeY;
	private final int noiseSizeZ;
	protected final EvoBiomeProvider biomeProvider;
	protected final SharedSeedRandom randomSeed;
	private final OpenSimplexNoiseOctaves minLimitSimplexNoise;
	private final OpenSimplexNoiseOctaves maxLimitSimplexNoise;
	private final OpenSimplexNoiseOctaves mainSimplexNoise;
	private final OpenSimplexNoiseOctaves surfaceDepthNoise;
	private final OpenSimplexNoiseOctaves depthNoise;
	@Nullable
	private final SimplexNoiseGenerator field_236083_v_;
	protected final BlockState defaultBlock;
	protected final BlockState defaultFluid;
	private final long seed;
	protected final Supplier<DimensionSettings> dimensionsettings;
	private final DimensionStructuresSettings structureSettings;
	private final int maxBuildHeight;
	private final EvoGenSettings genSettings;
	private OpenSimplexNoiseOctaves variationNoise;

	public EvoChunkGenerator(EvoBiomeProvider biomeProvider, long seed, Supplier<DimensionSettings> p_i241975_4_) {
	   this(biomeProvider, new EvoGenSettings(), seed, p_i241975_4_);
	}

	private EvoChunkGenerator(EvoBiomeProvider biomeProvider, EvoGenSettings genSettings, long seed, Supplier<DimensionSettings> dimensionSettings) 
	{
		super(biomeProvider, biomeProvider, dimensionSettings.get().getStructures(), seed);
		this.seed = seed;
		this.genSettings = genSettings;
		DimensionSettings dimensionsettings = dimensionSettings.get();
		this.dimensionsettings = dimensionSettings;
	    this.structureSettings = dimensionSettings.get().getStructures();
		NoiseSettings noisesettings = dimensionsettings.getNoise();
		this.biomeProvider = biomeProvider;
		this.maxBuildHeight = noisesettings.func_236169_a_();
		this.verticalNoiseGranularity = noisesettings.func_236175_f_() * 4;
		this.horizontalNoiseGranularity = noisesettings.func_236174_e_() * 4;
		this.defaultBlock = dimensionsettings.getDefaultBlock();
		this.defaultFluid = dimensionsettings.getDefaultFluid();
		this.noiseSizeX = 16 / this.horizontalNoiseGranularity;
		this.noiseSizeY = noisesettings.func_236169_a_() / this.verticalNoiseGranularity;
		this.noiseSizeZ = 16 / this.horizontalNoiseGranularity;
		this.randomSeed = new SharedSeedRandom(seed);
		this.minLimitSimplexNoise = new OpenSimplexNoiseOctaves(this.randomSeed, 16);
		this.maxLimitSimplexNoise = new OpenSimplexNoiseOctaves(this.randomSeed, 16);
		this.mainSimplexNoise = new OpenSimplexNoiseOctaves(this.randomSeed, 8);
		this.depthNoise = new OpenSimplexNoiseOctaves(this.randomSeed, 16);
		this.surfaceDepthNoise = new OpenSimplexNoiseOctaves(this.randomSeed, 4);
		this.variationNoise = new OpenSimplexNoiseOctaves(this.randomSeed, 4);
		this.randomSeed.skip(2620);
		if (noisesettings.func_236180_k_()) {
			SharedSeedRandom sharedseedrandom = new SharedSeedRandom(seed);
			sharedseedrandom.skip(17292);
			this.field_236083_v_ = new SimplexNoiseGenerator(sharedseedrandom);
		} else {
			this.field_236083_v_ = null;
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

	public int getMaxBuildHeight() 
	{
		return this.maxBuildHeight;
	}

	public int getSeaLevel() {
	   return this.dimensionsettings.get().func_236119_g_();
	}

	public List<MobSpawnInfo.Spawners> func_230353_a_(Biome p_230353_1_, StructureManager p_230353_2_, EntityClassification p_230353_3_, BlockPos p_230353_4_) {
	   List<MobSpawnInfo.Spawners> spawns = net.minecraftforge.common.world.StructureSpawnManager.getStructureSpawns(p_230353_2_, p_230353_3_, p_230353_4_);
	   if (spawns != null) return spawns;
	   return super.func_230353_a_(p_230353_1_, p_230353_2_, p_230353_3_, p_230353_4_);
	}

	public void func_230354_a_(WorldGenRegion p_230354_1_) {
	   if (!this.dimensionsettings.get().func_242744_a(null)) {
	      int i = p_230354_1_.getMainChunkX();
	      int j = p_230354_1_.getMainChunkZ();
	      Biome biome = p_230354_1_.getBiome((new ChunkPos(i, j)).asBlockPos());
	      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
	      sharedseedrandom.setDecorationSeed(p_230354_1_.getSeed(), i << 4, j << 4);
	      WorldEntitySpawner.performWorldGenSpawning(p_230354_1_, biome, i, j, sharedseedrandom);
	   }
	}

	/**
	 * Sets the biomes in the chunk. I don't need this, so I use an array with plains biomes
	 */
	public void func_242706_a(Registry<Biome> lookupRegistry, IChunk chunkIn) 
	{
		((ChunkPrimer)chunkIn).setBiomes(new BiomeContainer(lookupRegistry, PLAINS_BIOMES));
	}

	/**
	 * Generates the biomes and surface of the chunk
	 */
	public void generateSurface(WorldGenRegion worldRegion, IChunk chunkIn) 
	{
		 ChunkPos chunkpos = chunkIn.getPos();
		 SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
		 sharedseedrandom.setBaseChunkSeed(chunkpos.x, chunkpos.z);
		 int x = chunkpos.getXStart();
		 int z = chunkpos.getZStart();
		 Biome[] abiome = new Biome[BiomeContainer.BIOMES_SIZE];
		 
		 int k = 0;
		 for(int i = 0; i < 16; ++i) 
		 {
		 	for(int j = 0; j < 16; ++j)
		     {
		 		int x1 = x + j;
		     	int z1 = z + i;
		     	int y = chunkIn.getTopBlockY(Heightmap.Type.OCEAN_FLOOR_WG, j, i) + 1;
		     	Biome[] biomes = this.biomeProvider.getBiomesByHeight(x1, y, z1);
		     	double noise = this.surfaceDepthNoise.getNoise((double)x1 * 0.05, (double)z1 * 0.05) * 0.5;
		     	if (x1 % 4 == 0 && z1 % 4 == 0)
		     	{
		     		abiome[k] = biomes[1];
		     		k++;
		     	}
		 		double humidity = this.biomeProvider.getHumidity(x, z)[1];
		 		double temperature = this.biomeProvider.getTemperature(x, z)[1];
		 		if (biomes[0] == this.biomeProvider.getBiome(Biomes.BADLANDS) || biomes[0] == this.biomeProvider.getBiome(Biomes.WOODED_BADLANDS_PLATEAU))
		     		biomes[0].buildSurface(sharedseedrandom, chunkIn, x1, z1, y, noise, this.dimensionsettings.get().getDefaultBlock(), this.dimensionsettings.get().getDefaultFluid(), this.getSeaLevel(), randomSeed.nextLong());
		 		else if ( y <= 130 + Math.rint(40.0 * humidity * temperature + (sharedseedrandom.nextInt() % 10 - 5)) )
		     		biomes[0].buildSurface(sharedseedrandom, chunkIn, x1, z1, y, noise, this.dimensionsettings.get().getDefaultBlock(), this.dimensionsettings.get().getDefaultFluid(), this.getSeaLevel(), randomSeed.nextLong());
		     }
		 }
		 while (k < BiomeContainer.BIOMES_SIZE)
		 {
	 		abiome[k] = abiome[k % 16];
	 		k++;
		 }
		 ((ChunkPrimer)chunkIn).setBiomes(new BiomeContainer(this.biomeProvider.getRegistry(), abiome));
		 this.makeBedrock(chunkIn, sharedseedrandom);
	}

	private void makeBedrock(IChunk chunkIn, Random rand) {
	   BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
	   int i = chunkIn.getPos().getXStart();
	   int j = chunkIn.getPos().getZStart();
	   DimensionSettings dimensionsettings = this.dimensionsettings.get();
	   int k = dimensionsettings.func_236118_f_();
	   int l = this.maxBuildHeight - 1 - dimensionsettings.func_236117_e_();
	   int i1 = 5;
	   boolean flag = l + 4 >= 0 && l < this.maxBuildHeight;
	   boolean flag1 = k + 4 >= 0 && k < this.maxBuildHeight;
	   if (flag || flag1) {
	      for(BlockPos blockpos : BlockPos.getAllInBoxMutable(i, 0, j, i + 15, 0, j + 15)) {
	         if (flag) {
	            for(int j1 = 0; j1 < 5; ++j1) {
	               if (j1 <= rand.nextInt(5)) {
	                  chunkIn.setBlockState(blockpos$mutable.setPos(blockpos.getX(), l - j1, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
	               }
	            }
	         }

	         if (flag1) {
	            for(int k1 = 4; k1 >= 0; --k1) {
	               if (k1 <= rand.nextInt(5)) {
	                  chunkIn.setBlockState(blockpos$mutable.setPos(blockpos.getX(), k + k1, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
	               }
	            }
	         }
	      }

	   }
	}

	@Override
	protected Codec<? extends ChunkGenerator> func_230347_a_() 
	{
	   return CODEC;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ChunkGenerator func_230349_a_(long seed) {
	   return new EvoChunkGenerator(this.biomeProvider.getBiomeProvider(seed), seed, this.dimensionsettings);
	}

	public boolean func_236088_a_(long seed, RegistryKey<DimensionSettings> dimensionSettings) 
	{
	   return this.seed == seed && this.dimensionsettings.get().func_242744_a(dimensionSettings);
	}
	
	/*
	 * WORLD CARVER
	 *  I guess I probably need this to properly carve the world according to biome
	 *  From ChunkGenerator.class
	 */
	public void func_230350_a_(long l1, BiomeManager biomeManager, IChunk chunkIn, GenerationStage.Carving genStage) 
	{
		BiomeManager biomemanager = biomeManager.copyWithProvider(this.biomeProvider);
	    SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
	    int i = 8;
	    ChunkPos chunkpos = chunkIn.getPos();
	    int j = chunkpos.x;
	    int k = chunkpos.z;
	    BiomeGenerationSettings biomegenerationsettings = this.biomeProvider.getNoiseBiome(chunkpos.x << 2, chunkIn.getTopBlockY(Heightmap.Type.OCEAN_FLOOR, chunkpos.x << 2, chunkpos.z << 2), chunkpos.z << 2).getGenerationSettings();
	    BitSet bitset = ((ChunkPrimer)chunkIn).getOrAddCarvingMask(genStage);

	    for(int l = j - 8; l <= j + 8; ++l) {
	    	for(int i1 = k - 8; i1 <= k + 8; ++i1) {
	    		List<Supplier<ConfiguredCarver<?>>> list = biomegenerationsettings.getCarvers(genStage);
	            ListIterator<Supplier<ConfiguredCarver<?>>> listiterator = list.listIterator();

	            while(listiterator.hasNext()) {
	            	int j1 = listiterator.nextIndex();
	            	ConfiguredCarver<?> configuredcarver = listiterator.next().get();
	            	sharedseedrandom.setLargeFeatureSeed(l1 + (long)j1, l, i1);
	            	if (configuredcarver.shouldCarve(sharedseedrandom, l, i1)) {
	            		configuredcarver.carveRegion(chunkIn, biomemanager::getBiome, sharedseedrandom, this.getSeaLevel(), l, i1, j, k, bitset);
	            	}
	            }
	    	}
	    }
	}

	/*
	 * DECORATION
	 *  Decorating the world with decore :D
	 *  From 1.15
	 */
	
	public void func_230351_a_(WorldGenRegion region, StructureManager structManager) 
	{
		int i = region.getMainChunkX();
		int j = region.getMainChunkZ();
		int x = i * 16;
		int z = j * 16;
		BlockPos blockpos = new BlockPos(x, 0, z);
		int y = region.getChunk(blockpos).getTopBlockY(Heightmap.Type.OCEAN_FLOOR_WG, x + 8, z + 8) + 1;
		Biome biome = this.biomeProvider.getNoiseBiome(x + 8, y, z + 8, true);
		SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
		long i1 = sharedseedrandom.setDecorationSeed(region.getSeed(), x, z);
		double temperature = this.biomeProvider.getTemperature(x + 8, z + 8)[1];
		double humidity = this.biomeProvider.getHumidity(x + 8, z + 8)[1];

		for(GenerationStage.Decoration generationstage$decoration : GenerationStage.Decoration.values()) 
		{
			try 
			{
				if (generationstage$decoration == GenerationStage.Decoration.VEGETAL_DECORATION)
				{
					if ( y <= 115 + Math.rint(30.0 * humidity * temperature + sharedseedrandom.nextInt() % 10) )
						biome.generateFeatures(structManager, this, region, i1, sharedseedrandom, blockpos);
				}
				else
					biome.generateFeatures(structManager, this, region, i1, sharedseedrandom, blockpos);
			} 
			catch (Exception exception) 
			{
		        CrashReport crashreport = CrashReport.makeCrashReport(exception, "Biome decoration");
		        crashreport.makeCategory("Generation").addDetail("CenterX", i).addDetail("CenterZ", j).addDetail("Seed", i1).addDetail("Biome", biome);
		        throw new ReportedException(crashreport);
			}
		}

	}
	
	/*
	 * STRUCTURE GENERATION
	 *  Generates the structures
	 *  Taken from ChunkGenerator.class
	 */

	public void func_242707_a(DynamicRegistries structureRegistry, StructureManager structureManager, IChunk chunkIn, TemplateManager template, long l) 
	{
		ChunkPos chunkpos = chunkIn.getPos();
	    Biome biome = this.biomeProvider.getNoiseBiome((chunkpos.x << 2) + 2, getHeight((chunkpos.x << 2)+2, (chunkpos.z << 2)+2, Heightmap.Type.OCEAN_FLOOR), (chunkpos.z << 2) + 2);
	    this.placeStructure(StructureFeatures.STRONGHOLD, structureRegistry, structureManager, chunkIn, template, l, chunkpos, biome);
	    for(Supplier<StructureFeature<?, ?>> supplier : biome.getGenerationSettings().getStructures()) {
	    	this.placeStructure(supplier.get(), structureRegistry, structureManager, chunkIn, template, l, chunkpos, biome);
	    }
	}

	private void placeStructure(StructureFeature<?, ?> structure, DynamicRegistries structureRegistry, StructureManager structureManager, IChunk chunkIn, TemplateManager templateManager, long l, ChunkPos chunkPos, Biome biome) 
	{
		StructureStart<?> structurestart = structureManager.getStructureStart(SectionPos.from(chunkIn.getPos(), 0), structure.field_236268_b_, chunkIn);
		int i = structurestart != null ? structurestart.getRefCount() : 0;
		StructureSeparationSettings structureseparationsettings = this.structureSettings.func_236197_a_(structure.field_236268_b_);
		if (structureseparationsettings != null) {
			StructureStart<?> structurestart1 = structure.func_242771_a(structureRegistry, this, this.biomeProvider, templateManager, l, chunkPos, biome, i, structureseparationsettings);
			structureManager.addStructureStart(SectionPos.from(chunkIn.getPos(), 0), structure.field_236268_b_, structurestart1, chunkIn);
		}
	}
	
	/*
	 * NOISE GENERATION SETTINGS
	 *  Generates the land
	 * 	Taken from NoiseChunkGenerator.class
	 */

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType) {
	   return this.func_236087_a_(x, z, (BlockState[])null, heightmapType.getHeightLimitPredicate());
	}

	@Override
	public IBlockReader func_230348_a_(int p_230348_1_, int p_230348_2_) {
	   BlockState[] ablockstate = new BlockState[this.noiseSizeY * this.verticalNoiseGranularity];
	   this.func_236087_a_(p_230348_1_, p_230348_2_, ablockstate, (Predicate<BlockState>)null);
	   return new Blockreader(ablockstate);
	}

	private double[] func_222547_b(int p_222547_1_, int p_222547_2_) {
	   double[] adouble = new double[this.noiseSizeY + 1];
	   this.fillNoiseColumn(adouble, p_222547_1_, p_222547_2_);
	   return adouble;
	}


    protected void fillNoiseColumn(double[] arr, int x, int z) {
       double coordScale = this.genSettings.getCoordScale();
       double heightScale = this.genSettings.getHeightScale();
       double d2 = 8.555149841308594D;
       double d3 = 4.277574920654297D;
       int i = -10;
       int j = 3;
       this.calcNoiseColumn(arr, x, z, coordScale, heightScale, d2, d3, j, i);
    }

    private void calcNoiseColumn(double[] arr, int x, int z, double coordScale, double heightScale, double d_1, double d_2, int p_222546_12_, int p_222546_13_) {
       double[] adouble = this.getBiomeNoiseColumn(x, z);
       double d0 = adouble[0];
       double d1 = adouble[1];
       double d2 = (double)(this.noiseSizeY - 4);

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

    private double func_222545_a(double p_222545_1_, double p_222545_3_, int p_222545_5_) {
       double depthBase = this.genSettings.getDepthBaseSize();
       double d1 = ((double)p_222545_5_ - (depthBase + p_222545_1_ * depthBase / 8.0D * 4.0D)) * this.genSettings.getHeightStretch() * 128.0D / 256.0D / p_222545_3_;
       if (d1 < 0.0D) {
          d1 *= 4.0D;
       }

       return d1;
    }

    private double func_222552_a(double x, double y, double z, double coordScale, double heightScale) {

       double mainCoord = this.genSettings.getMainNoiseCoordScale();
       double mainHeight = this.genSettings.getMainNoiseHeightScale();
       double d0 = this.minLimitSimplexNoise.getNoise(x * coordScale, y * heightScale, z * coordScale);
       double d1 = this.maxLimitSimplexNoise.getNoise(x * coordScale, y * heightScale, z * coordScale);
       double d2 = this.mainSimplexNoise.getNoise(x * coordScale / mainCoord, y * heightScale / mainHeight, z * coordScale / mainCoord);

       return MathHelper.clampedLerp(d0 / this.genSettings.getLowerLimitScale(), d1 / this.genSettings.getUpperLimitScale(), (d2 / 10.0D + 1.0D) / 2.0D);
    }

    protected double[] getBiomeNoiseColumn(int x, int z) 
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
    			double d4 = (this.genSettings.getBiomeDepth() 
    					+ ( (1.0 - humidity * temperature) * this.genSettings.getBiomeDepthFactor()) )
    					* this.genSettings.getBiomeDepthWeight();
    			double d5 = (this.genSettings.getBiomeScale() + (variation * variation * this.genSettings.getBiomeScaleFactor())) * this.genSettings.getBiomeScaleWeight();
             
    			boolean isRiver = this.biomeProvider.getRiver((x + j) * 4, (z + k) * 4);
    			double[] landmass = this.biomeProvider.getLandmass((x + j) * 4, (z + k) * 4);
    			boolean isOcean = landmass[4] < EvoBiomeProvider.oceanThreshold - EvoBiomeProvider.beachThreshold / (double)EvoBiomeProvider.oceanOctaves / EvoBiomeProvider.oceanScale;
    			boolean isBeach = !isOcean && (landmass[4] < EvoBiomeProvider.oceanThreshold) && this.biomeProvider.canBeBeach((x + j) * 4, (z + k) * 4);
    			
    			if (isBeach | isOcean)
    			{
    				d4 = this.genSettings.getBiomeDepthOffset() + MathHelper.clamp((landmass[4] - EvoBiomeProvider.oceanThreshold + 0.025) * 6.0, -1.9, 0.0) * this.genSettings.getBiomeDepthWeight();
    				d5 = 0.0;
    			}
             
    			if (isRiver)
    			{
    				if (isBeach | isOcean)
    				{
    					d5 = 0.0;
    					if (d4 > (this.genSettings.getBiomeDepthOffset() + this.biomeProvider.getBiome(Biomes.RIVER).getDepth()) * this.genSettings.getBiomeDepthWeight())
    						d4 = (this.genSettings.getBiomeDepthOffset() + this.biomeProvider.getBiome(Biomes.RIVER).getDepth()) * this.genSettings.getBiomeDepthWeight();
    				}
    				else
    				{
    					d4 = (this.genSettings.getBiomeDepthOffset() + this.biomeProvider.getBiome(Biomes.OCEAN).getDepth()) * this.genSettings.getBiomeDepthWeight();
    					d5 = 0.0;
    				}
    			}

    			double d6 = field_236081_j_[j + 2 + (k + 2) * 5] / (d4 + 2.0);

    			d += d5 * d6;
    			d1 += d4 * d6;
    			d2 += d6;
    		}
    	}

    	d = d / d2;
    	d1 = d1 / d2;
    	d = d * 0.9 + 0.1;
    	d1 = (d1 * 4.0 - 1.0) / 8.0;
    	adouble[0] = d1 + this.func_236095_c_(x, z);
    	adouble[1] = d;
    	return adouble;
    }

	private int func_236087_a_(int p_236087_1_, int p_236087_2_, @Nullable BlockState[] p_236087_3_, @Nullable Predicate<BlockState> p_236087_4_) {
	   int i = Math.floorDiv(p_236087_1_, this.horizontalNoiseGranularity);
	   int j = Math.floorDiv(p_236087_2_, this.horizontalNoiseGranularity);
	   int k = Math.floorMod(p_236087_1_, this.horizontalNoiseGranularity);
	   int l = Math.floorMod(p_236087_2_, this.horizontalNoiseGranularity);
	   double d0 = (double)k / (double)this.horizontalNoiseGranularity;
	   double d1 = (double)l / (double)this.horizontalNoiseGranularity;
	   double[][] adouble = new double[][]{this.func_222547_b(i, j), this.func_222547_b(i, j + 1), this.func_222547_b(i + 1, j), this.func_222547_b(i + 1, j + 1)};

	   for(int i1 = this.noiseSizeY - 1; i1 >= 0; --i1) {
	      double d2 = adouble[0][i1];
	      double d3 = adouble[1][i1];
	      double d4 = adouble[2][i1];
	      double d5 = adouble[3][i1];
	      double d6 = adouble[0][i1 + 1];
	      double d7 = adouble[1][i1 + 1];
	      double d8 = adouble[2][i1 + 1];
	      double d9 = adouble[3][i1 + 1];

	      for(int j1 = this.verticalNoiseGranularity - 1; j1 >= 0; --j1) {
	         double d10 = (double)j1 / (double)this.verticalNoiseGranularity;
	         double d11 = MathHelper.lerp3(d10, d0, d1, d2, d6, d4, d8, d3, d7, d5, d9);
	         int k1 = i1 * this.verticalNoiseGranularity + j1;
	         BlockState blockstate = this.func_236086_a_(d11, k1);
	         if (p_236087_3_ != null) {
	            p_236087_3_[k1] = blockstate;
	         }

	         if (p_236087_4_ != null && p_236087_4_.test(blockstate)) {
	            return k1 + 1;
	         }
	      }
	   }

	   return 0;
	}

	private double func_236095_c_(int x, int z) {
	   double d0 = this.depthNoise.eval((double)(x * this.genSettings.getDepthNoiseScaleX()), 10.0D, (double)(z * this.genSettings.getDepthNoiseScaleZ()), 1.0D, 0.0D, true);
	   double d1;
	   if (d0 < 0.0D) {
	      d1 = -d0 * 0.3D;
	   } else {
	      d1 = d0;
	   }

	   double d2 = d1 * 24.575625D - 2.0D;
	   return d2 < 0.0D ? d2 * 0.009486607142857142D : Math.min(d2, 1.0D) * 0.006640625D;
	}

	protected BlockState func_236086_a_(double p_236086_1_, int p_236086_3_) {
	   BlockState blockstate;
	   if (p_236086_1_ > 0.0D) {
	      blockstate = this.defaultBlock;
	   } else if (p_236086_3_ < this.getSeaLevel()) {
	      blockstate = this.defaultFluid;
	   } else {
	      blockstate = AIR;
	   }

	   return blockstate;
	}

	private static double func_222556_a(int p_222556_0_, int p_222556_1_, int p_222556_2_) {
		int i = p_222556_0_ + 12;
		int j = p_222556_1_ + 12;
		int k = p_222556_2_ + 12;
		if (i >= 0 && i < 24) {
			if (j >= 0 && j < 24) {
				return k >= 0 && k < 24 ? (double)field_222561_h[k * 24 * 24 + i * 24 + j] : 0.0D;
			} else {
				return 0.0D;
			}
		} else {
			return 0.0D;
		}
	}

	@Override
	public void func_230352_b_(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_) {
	   ObjectList<StructurePiece> objectlist = new ObjectArrayList<>(10);
	   ObjectList<JigsawJunction> objectlist1 = new ObjectArrayList<>(32);
	   ChunkPos chunkpos = p_230352_3_.getPos();
	   int i = chunkpos.x;
	   int j = chunkpos.z;
	   int k = i << 4;
	   int l = j << 4;

	   for(Structure<?> structure : Structure.field_236384_t_) {
	      p_230352_2_.func_235011_a_(SectionPos.from(chunkpos, 0), structure).forEach((p_236089_5_) -> {
	         for(StructurePiece structurepiece1 : p_236089_5_.getComponents()) {
	            if (structurepiece1.func_214810_a(chunkpos, 12)) {
	               if (structurepiece1 instanceof AbstractVillagePiece) {
	                  AbstractVillagePiece abstractvillagepiece = (AbstractVillagePiece)structurepiece1;
	                  JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour = abstractvillagepiece.getJigsawPiece().getPlacementBehaviour();
	                  if (jigsawpattern$placementbehaviour == JigsawPattern.PlacementBehaviour.RIGID) {
	                     objectlist.add(abstractvillagepiece);
	                  }

	                  for(JigsawJunction jigsawjunction1 : abstractvillagepiece.getJunctions()) {
	                     int l5 = jigsawjunction1.getSourceX();
	                     int i6 = jigsawjunction1.getSourceZ();
	                     if (l5 > k - 12 && i6 > l - 12 && l5 < k + 15 + 12 && i6 < l + 15 + 12) {
	                        objectlist1.add(jigsawjunction1);
	                     }
	                  }
	               } else {
	                  objectlist.add(structurepiece1);
	               }
	            }
	         }

	      });
	   }

	   double[][][] adouble = new double[2][this.noiseSizeZ + 1][this.noiseSizeY + 1];

	   for(int i5 = 0; i5 < this.noiseSizeZ + 1; ++i5) {
	      adouble[0][i5] = new double[this.noiseSizeY + 1];
	      this.fillNoiseColumn(adouble[0][i5], i * this.noiseSizeX, j * this.noiseSizeZ + i5);
	      adouble[1][i5] = new double[this.noiseSizeY + 1];
	   }

	   ChunkPrimer chunkprimer = (ChunkPrimer)p_230352_3_;
	   Heightmap heightmap = chunkprimer.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
	   Heightmap heightmap1 = chunkprimer.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
	   BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
	   ObjectListIterator<StructurePiece> objectlistiterator = objectlist.iterator();
	   ObjectListIterator<JigsawJunction> objectlistiterator1 = objectlist1.iterator();

	   for(int i1 = 0; i1 < this.noiseSizeX; ++i1) {
	      for(int j1 = 0; j1 < this.noiseSizeZ + 1; ++j1) {
	         this.fillNoiseColumn(adouble[1][j1], i * this.noiseSizeX + i1 + 1, j * this.noiseSizeZ + j1);
	      }

	      for(int j5 = 0; j5 < this.noiseSizeZ; ++j5) {
	         ChunkSection chunksection = chunkprimer.getSection(15);
	         chunksection.lock();

	         for(int k1 = this.noiseSizeY - 1; k1 >= 0; --k1) {
	            double d0 = adouble[0][j5][k1];
	            double d1 = adouble[0][j5 + 1][k1];
	            double d2 = adouble[1][j5][k1];
	            double d3 = adouble[1][j5 + 1][k1];
	            double d4 = adouble[0][j5][k1 + 1];
	            double d5 = adouble[0][j5 + 1][k1 + 1];
	            double d6 = adouble[1][j5][k1 + 1];
	            double d7 = adouble[1][j5 + 1][k1 + 1];

	            for(int l1 = this.verticalNoiseGranularity - 1; l1 >= 0; --l1) {
	               int i2 = k1 * this.verticalNoiseGranularity + l1;
	               int j2 = i2 & 15;
	               int k2 = i2 >> 4;
	               if (chunksection.getYLocation() >> 4 != k2) {
	                  chunksection.unlock();
	                  chunksection = chunkprimer.getSection(k2);
	                  chunksection.lock();
	               }

	               double d8 = (double)l1 / (double)this.verticalNoiseGranularity;
	               double d9 = MathHelper.lerp(d8, d0, d4);
	               double d10 = MathHelper.lerp(d8, d2, d6);
	               double d11 = MathHelper.lerp(d8, d1, d5);
	               double d12 = MathHelper.lerp(d8, d3, d7);

	               for(int l2 = 0; l2 < this.horizontalNoiseGranularity; ++l2) {
	                  int i3 = k + i1 * this.horizontalNoiseGranularity + l2;
	                  int j3 = i3 & 15;
	                  double d13 = (double)l2 / (double)this.horizontalNoiseGranularity;
	                  double d14 = MathHelper.lerp(d13, d9, d10);
	                  double d15 = MathHelper.lerp(d13, d11, d12);

	                  for(int k3 = 0; k3 < this.horizontalNoiseGranularity; ++k3) {
	                     int l3 = l + j5 * this.horizontalNoiseGranularity + k3;
	                     int i4 = l3 & 15;
	                     double d16 = (double)k3 / (double)this.horizontalNoiseGranularity;
	                     double d17 = MathHelper.lerp(d16, d14, d15);
	                     double d18 = MathHelper.clamp(d17 / 200.0D, -1.0D, 1.0D);

	                     int j4;
	                     int k4;
	                     int l4;
	                     for(d18 = d18 / 2.0D - d18 * d18 * d18 / 24.0D; objectlistiterator.hasNext(); d18 += func_222556_a(j4, k4, l4) * 0.8D) {
	                        StructurePiece structurepiece = objectlistiterator.next();
	                        MutableBoundingBox mutableboundingbox = structurepiece.getBoundingBox();
	                        j4 = Math.max(0, Math.max(mutableboundingbox.minX - i3, i3 - mutableboundingbox.maxX));
	                        k4 = i2 - (mutableboundingbox.minY + (structurepiece instanceof AbstractVillagePiece ? ((AbstractVillagePiece)structurepiece).getGroundLevelDelta() : 0));
	                        l4 = Math.max(0, Math.max(mutableboundingbox.minZ - l3, l3 - mutableboundingbox.maxZ));
	                     }

	                     objectlistiterator.back(objectlist.size());

	                     while(objectlistiterator1.hasNext()) {
	                        JigsawJunction jigsawjunction = objectlistiterator1.next();
	                        int k5 = i3 - jigsawjunction.getSourceX();
	                        j4 = i2 - jigsawjunction.getSourceGroundY();
	                        k4 = l3 - jigsawjunction.getSourceZ();
	                        d18 += func_222556_a(k5, j4, k4) * 0.4D;
	                     }

	                     objectlistiterator1.back(objectlist1.size());
	                     BlockState blockstate = this.func_236086_a_(d18, i2);
	                     if (blockstate != AIR) {
	                        blockpos$mutable.setPos(i3, i2, l3);
	                        if (blockstate.getLightValue(chunkprimer, blockpos$mutable) != 0) {
	                           chunkprimer.addLightPosition(blockpos$mutable);
	                        }

	                        chunksection.setBlockState(j3, j2, i4, blockstate, false);
	                        heightmap.update(j3, i2, i4, blockstate);
	                        heightmap1.update(j3, i2, i4, blockstate);
	                     }
	                  }
	               }
	            }
	         }

	         chunksection.unlock();
	      }

	      double[][] adouble1 = adouble[0];
	      adouble[0] = adouble[1];
	      adouble[1] = adouble1;
	   }

	}
	
	static
	{
		Registry.register(Registry.CHUNK_GENERATOR_CODEC, new ResourceLocation(EvolutionTerrainGenerator.MODID, "evolution"), CODEC);
	}
}
