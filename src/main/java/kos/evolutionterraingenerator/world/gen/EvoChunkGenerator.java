package kos.evolutionterraingenerator.world.gen;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import kos.evolutionterraingenerator.util.OctaveOpenSimplexSampler;
import kos.evolutionterraingenerator.util.noise.OpenSimplexNoiseSampler;
import kos.evolutionterraingenerator.world.biome.EvoBiomeProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;

public final class EvoChunkGenerator extends NoiseChunkGenerator
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
	public static final Biome[] PLAINS_BIOMES = Util.make(new Biome[1024], (arr) -> 
	{
		for(int i = 0; i < arr.length; i++)
			arr[i] = BuiltinBiomes.PLAINS;
	});
			
	private final int verticalNoiseGranularity;
	private final int noiseSizeY;
	protected final EvoBiomeProvider biomeProvider;
	protected final ChunkRandom randomSeed;
	protected final BlockState defaultBlock;
	protected final BlockState defaultFluid;
	private final long seed;
	protected final EvoGenSettings settings;
	private final StructuresConfig structureSettings;
	private final int maxBuildHeight;
	private OctaveOpenSimplexSampler variationNoise;
	
	private final OctaveOpenSimplexSampler lowerInterpolatedNoise;
	private final OctaveOpenSimplexSampler upperInterpolatedNoise;
	private final OctaveOpenSimplexSampler interpolationNoise;

	public EvoChunkGenerator(EvoBiomeProvider biomeProvider, long seed, Supplier<ChunkGeneratorSettings> settings) 
	{
		super(biomeProvider, seed, settings);
		this.seed = seed;
		this.settings = new EvoGenSettings(settings);
		 this.structureSettings = settings.get().getStructuresConfig();
		 GenerationShapeConfig noisesettings = settings.get().getGenerationShapeConfig();
		this.biomeProvider = biomeProvider;
		this.maxBuildHeight = noisesettings.getHeight();
		this.verticalNoiseGranularity = noisesettings.getSizeVertical() * 4;
		this.defaultBlock = settings.get().getDefaultBlock();
		this.defaultFluid = settings.get().getDefaultFluid();
		this.noiseSizeY = noisesettings.getHeight() / this.verticalNoiseGranularity;
		this.randomSeed = new ChunkRandom(seed);
		this.lowerInterpolatedNoise = new OctaveOpenSimplexSampler(this.randomSeed, 16);
		this.upperInterpolatedNoise = new OctaveOpenSimplexSampler(this.randomSeed, 16);
		this.interpolationNoise = new OctaveOpenSimplexSampler(this.randomSeed, 8);
		this.variationNoise = new OctaveOpenSimplexSampler(this.randomSeed, 4);
		this.densityNoise = new OctaveOpenSimplexSampler(this.randomSeed, 16);
		this.randomSeed.consume(2620);
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
			  	double noise = this.surfaceDepthNoise.sample((double)x1 * 0.0625D, (double)z1 * 0.0625D, 0.0625D, (double)i * 0.0625D) * 15.0D;
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
	 * 	Taken from NoiseChunkGenerator.class
	 */

	@Override
	protected double[] sampleNoiseColumn(int x, int z) 
	{
		double[] ds = new double[this.noiseSizeY + 1];
		this.sampleNoiseColumn(ds, x, z);
		return ds;
	}

	@Override
	protected void sampleNoiseColumn(double[] buffer, int x, int z) 
	{
		GenerationShapeConfig generationShapeConfig = this.settings.getGeneratorSettings().get().getGenerationShapeConfig();
		double ac;
		double ad;
		double ai;
		double aj;
		
		double g = 0.0F;
		double h = 0.0F;
		double i = 0.0F;
		double l = 0.0D;
		{
 			double temperature = this.biomeProvider.getTemperature(x * 4, z * 4)[1];
 			double humidity =  this.biomeProvider.getTemperature(x * 4, z * 4)[1];
 			l = (this.settings.getBiomeDepth() 
 					+ ( (1.0 - humidity * temperature) * this.settings.getBiomeDepthFactor()) )
 					* this.settings.getBiomeDepthWeight();
		}

		for(int m = -2; m <= 2; ++m) 
		{
			for(int n = -2; n <= 2; ++n)
			{
	 			double temperature = this.biomeProvider.getTemperature((x + m) * 4, (z + n) * 4)[1];
	 			double humidity =  this.biomeProvider.getTemperature((x + m) * 4, (z + n) * 4)[1];
	 			double variation = MathHelper.clamp(this.variationNoise.sample((x + m) * 0.0825, (z + n) * 0.0825) * 0.2 + 0.5, 0.0, 1.0);
	 			double noiseDepth = (this.settings.getBiomeDepth() 
	 					+ ( (1.0 - humidity * temperature) * this.settings.getBiomeDepthFactor()) )
	 					* this.settings.getBiomeDepthWeight();
				double noiseScale = (this.settings.getBiomeScale() + (variation * variation * this.settings.getBiomeScaleFactor())) * this.settings.getBiomeScaleWeight();

	             
    			boolean isRiver = this.biomeProvider.getRiver((x + m) * 4, (z + n) * 4);
    			double[] landmass = this.biomeProvider.getLandmass((x + m) * 4, (z + n) * 4);
    			boolean isOcean = landmass[4] < EvoBiomeProvider.oceanThreshold - EvoBiomeProvider.beachThreshold / (double)EvoBiomeProvider.oceanOctaves / EvoBiomeProvider.oceanScale;
    			boolean isBeach = !isOcean && (landmass[4] < EvoBiomeProvider.oceanThreshold) && this.biomeProvider.canBeBeach((x + m) * 4, (z + n) * 4);
    			
    			if (isBeach | isOcean)
    			{
    				noiseDepth = this.settings.getBiomeDepthOffset() + MathHelper.clamp((landmass[4] - EvoBiomeProvider.oceanThreshold + 0.025) * 6.0, -1.9, 0.0) * this.settings.getBiomeDepthWeight();
    				noiseScale = 0.0;
    			}
             
    			if (isRiver)
    			{
    				if (isBeach | isOcean)
    				{
    					noiseScale = 0.0;
    					if (noiseDepth > (this.settings.getBiomeDepthOffset() + this.biomeProvider.decodeBiome(BiomeKeys.RIVER).getDepth()) * this.settings.getBiomeDepthWeight())
    						noiseDepth = (this.settings.getBiomeDepthOffset() + this.biomeProvider.decodeBiome(BiomeKeys.RIVER).getDepth()) * this.settings.getBiomeDepthWeight();
    				}
    				else
    				{
    					noiseDepth = (this.settings.getBiomeDepthOffset() + this.biomeProvider.decodeBiome(BiomeKeys.OCEAN).getDepth()) * this.settings.getBiomeDepthWeight();
    					noiseScale = 0.0;
    				}
    			}
    			
				if (generationShapeConfig.isAmplified() && noiseDepth > 0.0F) {
					noiseDepth = 1.0F + noiseDepth * 2.0F;
					noiseScale = 1.0F + noiseScale * 4.0F;
				}

				double u = noiseDepth > l ? 0.5F : 1.0F;
				double v = u * BIOME_WEIGHT_TABLE[m + 2 + (n + 2) * 5] / (noiseDepth + 2.0F);
				g += noiseScale * v;
				h += noiseDepth * v;
				i += v;
			}
		}

		double w = h / i;
		double y = g / i;
        ai = w * 4.0D - 1.0D / 8.0D;
        aj = y * 0.9D + 0.1D;
        ac = ai;
        ad = aj;

		double ae = 684.412D * generationShapeConfig.getSampling().getXZScale();
		double af = 684.412D * generationShapeConfig.getSampling().getYScale();
		double ag = ae / generationShapeConfig.getSampling().getXZFactor();
		double ah = af / generationShapeConfig.getSampling().getYFactor();
		ai = (double)generationShapeConfig.getTopSlide().getTarget();
		aj = (double)generationShapeConfig.getTopSlide().getSize();
		double ak = (double)generationShapeConfig.getTopSlide().getOffset();
		double al = (double)generationShapeConfig.getBottomSlide().getTarget();
		double am = (double)generationShapeConfig.getBottomSlide().getSize();
		double an = (double)generationShapeConfig.getBottomSlide().getOffset();
		double ao = generationShapeConfig.hasRandomDensityOffset() ? this.getRandomDensityAt(x, z) : 0.0D;
		double ap = generationShapeConfig.getDensityFactor();
		double aq = generationShapeConfig.getDensityOffset();

		for(int ar = 0; ar <= this.noiseSizeY; ++ar) {
			double as = this.sampleNoise(x, ar, z, ae, af, ag, ah);
			double at = 1.0D - (double)ar * 2.0D / (double)this.noiseSizeY + ao;
			double au = at * ap + aq;
			double av = (au + ac) * ad;
			if (av > 0.0D) {
				as += av * 4.0D;
			} else {
				as += av;
			}

			double ax;
			if (aj > 0.0D) {
				ax = ((double)(this.noiseSizeY - ar) - ak) / aj;
				as = MathHelper.clampedLerp(ai, as, ax);
			}

			if (am > 0.0D) {
				ax = ((double)ar - an) / am;
				as = MathHelper.clampedLerp(al, as, ax);
			}

			buffer[ar] = as;
		}

	}

    private double func_222545_a(double p_222545_1_, double p_222545_3_, int p_222545_5_) {
       double depthBase = this.settings.getDepthBaseSize();
       double d1 = ((double)p_222545_5_ - (depthBase + p_222545_1_ * depthBase / 8.0D * 4.0D)) * this.settings.getHeightStretch() * 128.0D / 256.0D / p_222545_3_;
       if (d1 < 0.0D) {
          d1 *= 4.0D;
       }

       return d1;
    }


	@Override
	protected double sampleNoise(int x, int y, int z, double horizontalScale, double verticalScale, double horizontalStretch, double verticalStretch) {
	      double d = this.lowerInterpolatedNoise.sample(
	    		  x * horizontalScale, 
	    		  y * verticalScale, 
	    		  z * horizontalScale, 
	    		  verticalScale, y * verticalScale, true);
	      double e = this.upperInterpolatedNoise.sample(
	    		  x * horizontalScale, 
	    		  y * verticalScale, 
	    		  z * horizontalScale, 
	    		  verticalScale, y * verticalScale, true);
	      double f = this.interpolationNoise.sample(
	    		  x * horizontalStretch, 
	    		  y * verticalStretch, 
	    		  z * horizontalStretch, 
	    		  verticalStretch, y * verticalStretch, true);

	      return MathHelper.clampedLerp(d / 512.0D, e / 512.0D, (f / 10.0D + 1.0D) / 2.0D);
	   }
	
	public static void register()
	{
		Registry.register(Registry.CHUNK_GENERATOR, (String)"evo", EvoChunkGenerator.CODEC);
	}
}
