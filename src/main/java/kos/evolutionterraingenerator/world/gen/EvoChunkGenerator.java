package kos.evolutionterraingenerator.world.gen;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import kos.evolutionterraingenerator.util.OpenSimplexNoiseOctaves;
import kos.evolutionterraingenerator.util.noise.OpenSimplexNoise;
import kos.evolutionterraingenerator.world.biome.EvoBiomeProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.StructureFeature;

public final class EvoChunkGenerator extends ChunkGenerator
{
	public static final Codec<EvoChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(EvoBiomeProvider.CODEC.fieldOf("environmental_noise").forGetter((noiseChunkGenerator) -> {
			return noiseChunkGenerator.biomeProvider;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((noiseChunkGenerator) -> {
			return noiseChunkGenerator.seed;
		}), ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter((noiseChunkGenerator) -> {
	         return noiseChunkGenerator.settings.getGeneratorSettings();
		})).apply(instance, instance.stable(EvoChunkGenerator::new));
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
			arr[i] = BuiltinBiomes.PLAINS;
	});
			
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private final int verticalNoiseGranularity;
	private final int horizontalNoiseGranularity;
	private final int noiseSizeX;
	private final int noiseSizeY;
	private final int noiseSizeZ;
	protected final EvoBiomeProvider biomeProvider;
	protected final ChunkRandom randomSeed;
	private final OpenSimplexNoiseOctaves minLimitSimplexNoise;
	private final OpenSimplexNoiseOctaves maxLimitSimplexNoise;
	private final OpenSimplexNoiseOctaves mainSimplexNoise;
	private final OpenSimplexNoiseOctaves surfaceDepthNoise;
	private final OpenSimplexNoiseOctaves depthNoise;
	protected final BlockState defaultBlock;
	protected final BlockState defaultFluid;
	private final long seed;
	protected final EvoGenSettings settings;
	private final StructuresConfig structureSettings;
	private final int maxBuildHeight;
	private OpenSimplexNoiseOctaves variationNoise;

	public EvoChunkGenerator(EvoBiomeProvider biomeProvider, long seed, Supplier<ChunkGeneratorSettings> settings) 
	{
		super(biomeProvider, biomeProvider, settings.get().getStructuresConfig(), seed);
		this.seed = seed;
		this.settings = new EvoGenSettings(settings);
	    this.structureSettings = settings.get().getStructuresConfig();
	    GenerationShapeConfig noisesettings = settings.get().getGenerationShapeConfig();
		this.biomeProvider = biomeProvider;
		this.maxBuildHeight = noisesettings.getHeight();
		this.verticalNoiseGranularity = noisesettings.getSizeVertical() * 4;
		this.horizontalNoiseGranularity = noisesettings.getSizeHorizontal() * 4;
		this.defaultBlock = settings.get().getDefaultBlock();
		this.defaultFluid = settings.get().getDefaultFluid();
		this.noiseSizeX = 16 / this.horizontalNoiseGranularity;
		this.noiseSizeY = noisesettings.getHeight() / this.verticalNoiseGranularity;
		this.noiseSizeZ = 16 / this.horizontalNoiseGranularity;
		this.randomSeed = new ChunkRandom(seed);
		this.minLimitSimplexNoise = new OpenSimplexNoiseOctaves(this.randomSeed, 16);
		this.maxLimitSimplexNoise = new OpenSimplexNoiseOctaves(this.randomSeed, 16);
		this.mainSimplexNoise = new OpenSimplexNoiseOctaves(this.randomSeed, 8);
		this.depthNoise = new OpenSimplexNoiseOctaves(this.randomSeed, 16);
		this.surfaceDepthNoise = new OpenSimplexNoiseOctaves(this.randomSeed, 4);
		this.variationNoise = new OpenSimplexNoiseOctaves(this.randomSeed, 4);
		this.randomSeed.consume(2620);
	}

	private static double func_222554_b(int p_222554_0_, int p_222554_1_, int p_222554_2_) {
	   double d0 = (double)(p_222554_0_ * p_222554_0_ + p_222554_2_ * p_222554_2_);
	   double d1 = (double)p_222554_1_ + 0.5D;
	   double d2 = d1 * d1;
	   double d3 = Math.pow(Math.E, -(d2 / 16.0D + d0 / 16.0D));
	   double d4 = -d1 * MathHelper.fastInverseSqrt(d2 / 2.0D + d0 / 2.0D) / 2.0D;
	   return d4 * d3;
	}

	@Override
	public int getWorldHeight() 
	{
		return this.maxBuildHeight;
	}

	@Override
	public int getSeaLevel() {
	   return this.settings.getGeneratorSettings().get().getSeaLevel();
	}

	/*
	 * ENTITY SPAWINNG
	 */
	@Override
	public void populateEntities(ChunkRegion region) {
	      int x = region.getCenterChunkX();
	      int z = region.getCenterChunkZ();
	      int y = region.getChunk(x, z).sampleHeightmap(Heightmap.Type.OCEAN_FLOOR_WG, x << 4, z << 4);
	      Biome biome = this.biomeProvider.getBiomeForNoiseGen(x << 4, y, z << 4, false);
	      ChunkRandom sharedseedrandom = new ChunkRandom();
	      sharedseedrandom.setPopulationSeed(region.getSeed(), x << 4, z << 4);
	      SpawnHelper.populateEntities(region, biome, x, z, sharedseedrandom);
	}

	/**
	 * Sets the biomes in the chunk. I don't need this, so I use an array with plains biomes
	 */
	@Override
	public void populateBiomes(Registry<Biome> lookupRegistry, Chunk chunkIn) 
	{
		((ProtoChunk)chunkIn).setBiomes(new BiomeArray(lookupRegistry, PLAINS_BIOMES));
	}
	
	/*
	 * WORLD CARVER
	 *  I guess I probably need this to properly carve the world according to biome
	 *  From ChunkGenerator.class
	 */
	public void carve(long l1, BiomeAccess biomeManager, Chunk chunkIn, GenerationStep.Carver genStage) 
	{
		BiomeAccess biomemanager = biomeManager.withSource(this.biomeProvider);
		ChunkRandom sharedseedrandom = new ChunkRandom();
	    ChunkPos chunkpos = chunkIn.getPos();
	    int j = chunkpos.x;
	    int k = chunkpos.z;
	    int x = chunkpos.x << 4;
	    int z = chunkpos.z << 4;
	    int y = chunkIn.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR_WG, x, z);
	    GenerationSettings biomegenerationsettings = this.biomeProvider.getBiomeForNoiseGen(x, y, z, false).getGenerationSettings();
	    BitSet bitset = ((ProtoChunk)chunkIn).getOrCreateCarvingMask(genStage);

	    for(int l = j - 8; l <= j + 8; ++l) {
	    	for(int i1 = k - 8; i1 <= k + 8; ++i1) {
	    		List<Supplier<ConfiguredCarver<?>>> list = biomegenerationsettings.getCarversForStep(genStage);
	            ListIterator<Supplier<ConfiguredCarver<?>>> listiterator = list.listIterator();

	            while(listiterator.hasNext()) {
	            	int j1 = listiterator.nextIndex();
	            	ConfiguredCarver<?> configuredcarver = listiterator.next().get();
	            	sharedseedrandom.setCarverSeed(l1 + (long)j1, l, i1);
	            	if (configuredcarver.shouldCarve(sharedseedrandom, l, i1)) {
	            		configuredcarver.carve(chunkIn, biomemanager::getBiome, sharedseedrandom, this.getSeaLevel(), l, i1, j, k, bitset);
	            	}
	            }
	    	}
	    }
	}

	/**
	 * Generates the biomes and surface of the chunk
	 */
	@Override
	public void buildSurface(ChunkRegion worldRegion, Chunk chunkIn) 
	{
		 ChunkPos chunkpos = chunkIn.getPos();
		 ChunkRandom sharedseedrandom = new ChunkRandom();
		 sharedseedrandom.setTerrainSeed(chunkpos.x, chunkpos.z);
		 int x = chunkpos.getStartX();
		 int z = chunkpos.getStartZ();
		 Biome[] abiome = new Biome[BiomeArray.DEFAULT_LENGTH];
		 
		 int k = 0;
		 for(int i = 0; i < 16; ++i) 
		 {
		 	for(int j = 0; j < 16; ++j)
		     {
		 		int x1 = x + j;
		     	int z1 = z + i;
		     	int y = chunkIn.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR_WG, j, i) + 1;
		     	Biome[] biomes = this.biomeProvider.getBiomesByHeight(x1, y, z1);
		     	double noise = this.surfaceDepthNoise.sample((double)x1 * 0.05, (double)z1 * 0.05) * 0.5;
		     	if (x1 % 4 == 0 && z1 % 4 == 0)
		     	{
		     		abiome[k] = biomes[0];
		     		k++;
		     	}
		 		double humidity = this.biomeProvider.getHumidity(x, z)[1];
		 		double temperature = this.biomeProvider.getTemperature(x, z)[1];
		 		if (biomes[0] == this.biomeProvider.decodeBiome(BiomeKeys.BADLANDS) || biomes[0] == this.biomeProvider.decodeBiome(BiomeKeys.WOODED_BADLANDS_PLATEAU))
		     		biomes[0].buildSurface(sharedseedrandom, chunkIn, x1, z1, y, noise, this.settings.getGeneratorSettings().get().getDefaultBlock(), this.settings.getGeneratorSettings().get().getDefaultFluid(), this.getSeaLevel(), randomSeed.nextLong());
		 		else if ( y <= 130 + Math.rint(40.0 * humidity * temperature + (sharedseedrandom.nextInt() % 10 - 5)) )
		     		biomes[0].buildSurface(sharedseedrandom, chunkIn, x1, z1, y, noise, this.settings.getGeneratorSettings().get().getDefaultBlock(), this.settings.getGeneratorSettings().get().getDefaultFluid(), this.getSeaLevel(), randomSeed.nextLong());
		     }
		 }
		 while (k < BiomeArray.DEFAULT_LENGTH)
		 {
	 		abiome[k] = abiome[k % 16];
	 		k++;
		 }
		 ((ProtoChunk)chunkIn).setBiomes(new BiomeArray(this.biomeProvider.getRegistry(), abiome));
		 this.makeBedrock(chunkIn, sharedseedrandom);
	}
	
	private void makeBedrock(Chunk chunk, Random random)
	{
	      BlockPos.Mutable mutable = new BlockPos.Mutable();
	   int i = chunk.getPos().getStartX();
	   int j = chunk.getPos().getStartZ();
	   ChunkGeneratorSettings settings = this.settings.getGeneratorSettings().get();
	   int k = settings.getBedrockFloorY();
	   int l = this.maxBuildHeight - 1 - settings.getBedrockCeilingY();
	   boolean bl = l + 4 >= 0 && l < this.maxBuildHeight;
	   boolean bl2 = k + 4 >= 0 && k < this.maxBuildHeight;
	   if (bl || bl2) {
		   Iterator<BlockPos> var12 = BlockPos.iterate(i, 0, j, i + 15, 0, j + 15).iterator();

		   while(true) {
			   BlockPos blockPos;
			   int o;
			   do {
				   if (!var12.hasNext()) {
					   return;
				   }

				   blockPos = (BlockPos)var12.next();
				   if (bl) {
					   for(o = 0; o < 5; ++o) {
						   if (o <= random.nextInt(5)) {
							   chunk.setBlockState(mutable.set(blockPos.getX(), l - o, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
						   }
					   }
				   }
			   } while(!bl2);

			   for(o = 4; o >= 0; --o) {
				   if (o <= random.nextInt(5)) {
					   chunk.setBlockState(mutable.set(blockPos.getX(), k + o, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
				   }
			   }
		   }
	   }
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() 
	{
	   return CODEC;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public ChunkGenerator withSeed(long seed) {
	   return new EvoChunkGenerator(this.biomeProvider.withSeed(seed), seed, this.settings.getGeneratorSettings());
	}

	public boolean matchesSettings(long seed, RegistryKey<ChunkGeneratorSettings> settings) {
		return this.seed == seed && ((ChunkGeneratorSettings)this.settings.getGeneratorSettings().get()).equals(settings);
	}

	/*
	 * DECORATION
	 *  Decorating the world with decore :D
	 *  From 1.15
	 */
	@Override
	public void generateFeatures(ChunkRegion region, StructureAccessor structManager) 
	{
		int i = region.getCenterChunkX();
		int j = region.getCenterChunkZ();
		int x = i * 16;
		int z = j * 16;
		BlockPos blockpos = new BlockPos(x, 0, z);
		int y = region.getChunk(blockpos).sampleHeightmap(Heightmap.Type.OCEAN_FLOOR_WG, x + 8, z + 8) + 1;
		Biome biome = this.biomeProvider.getBiomeForNoiseGen(x + 8, y, z + 8, true);
		ChunkRandom sharedseedrandom = new ChunkRandom();
		long i1 = sharedseedrandom.setDecoratorSeed(region.getSeed(), x, z);
		
		try 
		{
			//generateFeatures(biome, y, structManager, this, region, i1, sharedseedrandom, blockpos);
			biome.generateFeatureStep(structManager, this, region, i1, sharedseedrandom, blockpos);
		} 
		catch (Exception exception) 
		{
	         CrashReport crashReport = CrashReport.create(exception, "Biome decoration");
	         crashReport.addElement("Generation").add("CenterX", (Object)i).add("CenterZ", (Object)j).add("Seed", (Object)i1).add("Biome", (Object)biome);
	         throw new CrashException(crashReport);
		}
	}
	
	//Taken from Biome.class so I can do the thing where the vegetation is like less frequent in higher elevations
	/*
	@SuppressWarnings("deprecation")
	private void generateFeatures(Biome biome, int y, StructureAccessor structureManager, ChunkGenerator chunkGenerator, ChunkRegion worldGenRegion, long seed, ChunkRandom rand, BlockPos pos) 
	{
		List<List<Supplier<ConfiguredFeature<?, ?>>>> list = biome.getGenerationSettings().getFeatures();
	    Map<Integer, List<Structure<?>>> biomeStructures = Registry.STRUCTURE_FEATURE.stream().collect(Collectors.groupingBy((structure) -> {
	    	return structure.getDecorationStage().ordinal();
	    }));
	    int i = GenerationStage.Decoration.values().length;
		double temperature = this.biomeProvider.getTemperature(pos.getX() + 8, pos.getZ() + 8)[1];
		double humidity = this.biomeProvider.getHumidity(pos.getX() + 8, pos.getZ() + 8)[1];

		for(int j = 0; j < i; ++j) {
			if (j == GenerationStage.Decoration.VEGETAL_DECORATION.ordinal())
				if ( y > 115 + Math.rint(30.0 * humidity * temperature + rand.nextInt() % 10) )
					continue;
			int k = 0;
			if (structureManager.canGenerateFeatures()) {
				for(Structure<?> structure : biomeStructures.getOrDefault(j, Collections.emptyList())) {
					rand.setFeatureSeed(seed, k, j);
					int l = pos.getX() >> 4;
					int i1 = pos.getZ() >> 4;
					int j1 = l << 4;
					int k1 = i1 << 4;

					try {
						structureManager.func_235011_a_(SectionPos.from(pos), structure).forEach((structureStart) -> {
							structureStart.func_230366_a_(worldGenRegion, structureManager, chunkGenerator, rand, new MutableBoundingBox(j1, k1, j1 + 15, k1 + 15), new ChunkPos(l, i1));
						});
					} catch (Exception exception) {
						CrashReport crashreport = CrashReport.makeCrashReport(exception, "Feature placement");
						crashreport.makeCategory("Feature").addDetail("Id", Registry.STRUCTURE_FEATURE.getKey(structure)).addDetail("Description", () -> {
							return structure.toString();
						});
						throw new ReportedException(crashreport);
					}

					++k;
				}
			}

			if (list.size() > j) {
				for(Supplier<ConfiguredFeature<?, ?>> supplier : list.get(j)) {
					ConfiguredFeature<?, ?> configuredfeature = supplier.get();
					rand.setFeatureSeed(seed, k, j);

					try {
						configuredfeature.generate(worldGenRegion, chunkGenerator, rand, pos);
					} catch (Exception exception1) {
						CrashReport crashreport1 = CrashReport.makeCrashReport(exception1, "Feature placement");
						crashreport1.makeCategory("Feature").addDetail("Id", Registry.FEATURE.getKey(configuredfeature.feature)).addDetail("Config", configuredfeature.config).addDetail("Description", () -> {
							return configuredfeature.feature.toString();
						});
						throw new ReportedException(crashreport1);
					}

					++k;
				}
			}
		}
	}
	*/
	
	/*
	 * STRUCTURE GENERATION
	 *  Generates the structures
	 *  Taken from ChunkGenerator.class
	 */
	@Override
	public void setStructureStarts(DynamicRegistryManager structureRegistry, StructureAccessor structureManager, Chunk chunkIn, StructureManager template, long l) 
	{
		ChunkPos chunkpos = chunkIn.getPos();
		int x = chunkpos.getStartX() + 9;
		int z = chunkpos.getStartZ() + 9;
		int y = getHeight(x, z, Heightmap.Type.OCEAN_FLOOR_WG) + 1;
		Biome biome = this.biomeProvider.getBiomeForNoiseGen(x, y, z, false);
		this.setStructureStart(ConfiguredStructureFeatures.STRONGHOLD, structureRegistry, structureManager, chunkIn, template, l, chunkpos, biome);
		
		for (Supplier<ConfiguredStructureFeature<?, ?>> supplier : biome.getGenerationSettings().getStructureFeatures()) 
		{
			if (this.biomeProvider.hasStructureFeature(supplier.get().feature))
				this.setStructureStart(supplier.get(), structureRegistry, structureManager, chunkIn, template, l, chunkpos, biome);
		}
	}

	private void setStructureStart(ConfiguredStructureFeature<?, ?> configuredStructureFeature, DynamicRegistryManager dynamicRegistryManager, StructureAccessor structureAccessor, Chunk chunk, StructureManager structureManager, long worldSeed, ChunkPos chunkPos, Biome biome) {
		StructureStart<?> structureStart = structureAccessor.getStructureStart(ChunkSectionPos.from(chunk.getPos(), 0), configuredStructureFeature.feature, chunk);
		int i = structureStart != null ? structureStart.getReferences() : 0;
		StructureConfig structureConfig = this.structureSettings.getForType(configuredStructureFeature.feature);
		if (structureConfig != null) {
			StructureStart<?> structureStart2 = configuredStructureFeature.tryPlaceStart(dynamicRegistryManager, this, this.populationSource, structureManager, worldSeed, chunkPos, biome, i, structureConfig);
			structureAccessor.setStructureStart(ChunkSectionPos.from(chunk.getPos(), 0), configuredStructureFeature.feature, structureStart2, chunk);
		}
	}
	
	/*
	 * NOISE GENERATION SETTINGS
	 *  Generates the land
	 * 	Taken from NoiseChunkGenerator.class and the old methods of 1.15
	 */
	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType) {
	   return this.func_236087_a_(x, z, (BlockState[])null, heightmapType.getBlockPredicate());
	}

	@Override
	public BlockView getColumnSample(int x, int z) {
		BlockState[] ablockstate = new BlockState[this.noiseSizeY * this.verticalNoiseGranularity];
		this.func_236087_a_(x, z, ablockstate, (Predicate<BlockState>)null);
		return new VerticalBlockSample(ablockstate);
	}

	private double[] func_222547_b(int p_222547_1_, int p_222547_2_) {
		double[] adouble = new double[this.noiseSizeY + 1];
		this.fillNoiseColumn(adouble, p_222547_1_, p_222547_2_);
		return adouble;
	}


    private void fillNoiseColumn(double[] arr, int x, int z) {
       double coordScale = 684.412 / this.settings.getCoordScale();
       double heightScale = 684.412 / this.settings.getHeightScale();
       double coordFactor = this.settings.getCoordFactor();
       double heightFactor = this.settings.getHeightFactor();
       int i = -10;
       int j = 3;
       this.calcNoiseColumn(arr, x, z, coordScale, heightScale, coordFactor, heightFactor, j, i);
    }

    private void calcNoiseColumn(double[] arr, int x, int z, double coordScale, double heightScale, double coordFactor, double heightFactor, int p_222546_12_, int p_222546_13_) {
       double[] adouble = this.getBiomeNoiseColumn(x, z);
       double d0 = adouble[0];
       double d1 = adouble[1];
       double d2 = (double)(this.noiseSizeY - 4);

       for(int i = 0; i < this.noiseSizeY + 1; ++i)
       {
          double d4 = this.sampleNoise(x, i, z, coordScale, heightScale, coordFactor, heightFactor);
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

    private double func_222545_a(double x, double y, int z) {
       double depthBase = this.settings.getDepthBaseSize();
       double d1 = ((double)z - (depthBase + x * depthBase / 8.0D * 4.0D)) * this.settings.getHeightStretch() * 128.0D / 256.0D / y;
       if (d1 < 0.0D) {
          d1 *= 4.0D;
       }

       return d1;
    }

    private double sampleNoise(double x, double y, double z, double coordScale, double heightScale, double coordFactor, double heightFactor) 
    {
       double d0 = this.minLimitSimplexNoise.sample(x * coordScale, y * heightScale, z * coordScale);
       double d1 = this.maxLimitSimplexNoise.sample(x * coordScale, y * heightScale, z * coordScale);
       double d2 = this.mainSimplexNoise.sample(x * coordScale / coordFactor, y * heightScale / heightFactor, z * coordScale / coordFactor);

       return MathHelper.clampedLerp(d0 / this.settings.getLowerLimitScale(), d1 / this.settings.getUpperLimitScale(), (d2 / 10.0D + 1.0D) / 2.0D);
    }

    private double[] getBiomeNoiseColumn(int x, int z) 
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
    			double variation = MathHelper.clamp(this.variationNoise.sample((x + j) * 0.0825, (z + k) * 0.0825) * 0.2 + 0.5, 0.0, 1.0);
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
    					if (d4 > (this.settings.getBiomeDepthOffset() + this.biomeProvider.decodeBiome(BiomeKeys.RIVER).getDepth()) * this.settings.getBiomeDepthWeight())
    						d4 = (this.settings.getBiomeDepthOffset() + this.biomeProvider.decodeBiome(BiomeKeys.RIVER).getDepth()) * this.settings.getBiomeDepthWeight();
    				}
    				else
    				{
    					d4 = (this.settings.getBiomeDepthOffset() + this.biomeProvider.decodeBiome(BiomeKeys.OCEAN).getDepth()) * this.settings.getBiomeDepthWeight();
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

	private int func_236087_a_(int x, int z, @Nullable BlockState[] arr_blockstate, @Nullable Predicate<BlockState> blockstate) {
	   int i = Math.floorDiv(x, this.horizontalNoiseGranularity);
	   int j = Math.floorDiv(z, this.horizontalNoiseGranularity);
	   int k = Math.floorMod(x, this.horizontalNoiseGranularity);
	   int l = Math.floorMod(z, this.horizontalNoiseGranularity);
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
	         BlockState blockstate1 = this.func_236086_a_(d11, k1);
	         if (arr_blockstate != null) {
	            arr_blockstate[k1] = blockstate1;
	         }

	         if (blockstate != null && blockstate.test(blockstate1)) {
	            return k1 + 1;
	         }
	      }
	   }

	   return 0;
	}

	private double func_236095_c_(int x, int z) {
	   double d0 = this.depthNoise.eval((double)(x * this.settings.getDepthNoiseScaleX()), 10.0D, (double)(z * this.settings.getDepthNoiseScaleZ()), 1.0D, 0.0D, true);
	   double d1;
	   if (d0 < 0.0D) {
	      d1 = -d0 * 0.3D;
	   } else {
	      d1 = d0;
	   }

	   double d2 = d1 * 24.575625D - 2.0D;
	   return d2 < 0.0D ? d2 * 0.009486607142857142D : Math.min(d2, 1.0D) * 0.006640625D;
	}

	private BlockState func_236086_a_(double p_236086_1_, int p_236086_3_) {
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

	private static double func_222556_a(int x, int y, int z) {
		int i = x + 12;
		int j = y + 12;
		int k = z + 12;
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
	public void populateNoise(WorldAccess world, StructureAccessor structManager, Chunk chunkIn) {
	   ObjectList<StructurePiece> objectlist = new ObjectArrayList<>(10);
	   ObjectList<JigsawJunction> objectlist1 = new ObjectArrayList<>(32);
	   ChunkPos chunkpos = chunkIn.getPos();
	   int i = chunkpos.x;
	   int j = chunkpos.z;
	   int k = i << 4;
	   int l = j << 4;

	   for(StructureFeature<?> structure : StructureFeature.JIGSAW_STRUCTURES) {
	      structManager.getStructuresWithChildren(ChunkSectionPos.from(chunkpos, 0), structure).forEach((p_236089_5_) -> {
	         for(StructurePiece structurepiece1 : p_236089_5_.getChildren()) {
	            if (structurepiece1.intersectsChunk(chunkpos, 12)) {
	               if (structurepiece1 instanceof PoolStructurePiece) {
	                  PoolStructurePiece abstractvillagepiece = (PoolStructurePiece)structurepiece1;
	                  StructurePool.Projection jigsawpattern$placementbehaviour = abstractvillagepiece.getPoolElement().getProjection();
	                  if (jigsawpattern$placementbehaviour == StructurePool.Projection.RIGID) {
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

	   ProtoChunk chunkprimer = (ProtoChunk)chunkIn;
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
	               if (chunksection.getYOffset() >> 4 != k2) {
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
	                        BlockBox mutableboundingbox = structurepiece.getBoundingBox();
	                        j4 = Math.max(0, Math.max(mutableboundingbox.minX - i3, i3 - mutableboundingbox.maxX));
	                        k4 = i2 - (mutableboundingbox.minY + (structurepiece instanceof PoolStructurePiece ? ((PoolStructurePiece)structurepiece).getGroundLevelDelta() : 0));
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
	                        blockpos$mutable.set(i3, i2, l3);
	                        if (blockstate.getLuminance() != 0) {
	                           chunkprimer.addLightSource(blockpos$mutable);
	                        }

	                        chunksection.setBlockState(j3, j2, i4, blockstate, false);
	                        heightmap.trackUpdate(j3, i2, i4, blockstate);
	                        heightmap1.trackUpdate(j3, i2, i4, blockstate);
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
	
	public static void register()
	{
	      Registry.register(Registry.CHUNK_GENERATOR, (String)"evo", EvoChunkGenerator.CODEC);
	}
}
