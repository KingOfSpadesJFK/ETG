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
import net.minecraft.world.gen.feature.StructureFeature;

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
	
	private final OctaveOpenSimplexSampler lowerInterpolatedNoise;
	private final OctaveOpenSimplexSampler upperInterpolatedNoise;
	private final OctaveOpenSimplexSampler interpolationNoise;
	private final TerrainLayerSampler terrainLayer;

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
		this.terrainLayer = TerrainLayerSampler.TerrainLayerBuilder.build(seed, 5);
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
			  		biomes[0].buildSurface(sharedseedrandom, chunkIn, x1, z1, y, noise, this.settings.getGeneratorSettings().get().getDefaultBlock(), this.settings.getGeneratorSettings().get().getDefaultFluid(), this.getSeaLevel(), worldRegion.getSeed());
		 		else if ( y <= 130 + Math.rint(40.0 * humidity * temperature + ((double)(sharedseedrandom.nextInt() % 30 - 15) * (0.125 + humidity * temperature * 0.875))) )
			  		biomes[0].buildSurface(sharedseedrandom, chunkIn, x1, z1, y, noise, this.settings.getGeneratorSettings().get().getDefaultBlock(), this.settings.getGeneratorSettings().get().getDefaultFluid(), this.getSeaLevel(), worldRegion.getSeed());
		 		/*
		 		if (this.terrainLayer.sample(x1, z1) == 1)
		 			this.biomeProvider.decodeBiome(BiomeKeys.DESERT).buildSurface(sharedseedrandom, chunkIn, x1, z1, y, noise, this.settings.getGeneratorSettings().get().getDefaultBlock(), this.settings.getGeneratorSettings().get().getDefaultFluid(), this.getSeaLevel(), worldRegion.getSeed());
		 		if (this.terrainLayer.sample(x1, z1) >= 2)
		 			this.biomeProvider.decodeBiome(BiomeKeys.BADLANDS).buildSurface(sharedseedrandom, chunkIn, x1, z1, y, noise, this.settings.getGeneratorSettings().get().getDefaultBlock(), this.settings.getGeneratorSettings().get().getDefaultFluid(), this.getSeaLevel(), worldRegion.getSeed());
			  	*/
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
		long i1 = sharedseedrandom.setPopulationSeed(region.getSeed(), x, z);
		
		try 
		{
			double[] landmass = this.biomeProvider.getLandmass(x, z);
			boolean isOcean = landmass[4] < EvoBiomeProvider.oceanThreshold - EvoBiomeProvider.beachThreshold / (double)EvoBiomeProvider.oceanOctaves / EvoBiomeProvider.oceanScale;
			boolean isBeach = !isOcean && (landmass[4] < EvoBiomeProvider.oceanThreshold) && this.biomeProvider.canBeBeach(x, z);
			if (!isBeach && !isOcean && this.terrainLayer.sample(x, z) == TerrainLayerSampler.RIVER_LAYER && y < this.getSeaLevel())
				this.biomeProvider.decodeBiome(BiomeKeys.RIVER).generateFeatureStep(structManager, this, region, i1, sharedseedrandom, blockpos);
			else
				biome.generateFeatureStep(structManager, this, region, i1, sharedseedrandom, blockpos);
		} 
		catch (Exception exception) 
		{
			CrashReport crashReport = CrashReport.create(exception, "Biome decoration");
			crashReport.addElement("Generation").add("CenterX", (Object)i).add("CenterZ", (Object)j).add("Seed", (Object)i1).add("Biome", (Object)biome);
			throw new CrashException(crashReport);
		}
	}
	
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
		int terrain = this.terrainLayer.sample(x, z);
		this.setStructureStart(ConfiguredStructureFeatures.STRONGHOLD, structureRegistry, structureManager, chunkIn, template, l, chunkpos, biome);
		
		for (Supplier<ConfiguredStructureFeature<?, ?>> supplier : biome.getGenerationSettings().getStructureFeatures()) 
		{
			if (terrain > 0 && supplier.get().feature == StructureFeature.VILLAGE)
				continue;
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
		double ai;
		double aj;
		
		double g = 0.0F;
		double h = 0.0F;
		double i = 0.0F;
		double l = 0.0D;
		{
 			double temperature = this.biomeProvider.getTemperature(x * 4, z * 4)[1];
 			double humidity =  this.biomeProvider.getTemperature(x * 4, z * 4)[1];
 			int terrain = this.terrainLayer.sample(x * 4, z * 4);
 			l = (this.settings.getBiomeDepth() 
 					+ ( (1.0 - humidity * temperature) * this.settings.getBiomeDepthFactor()) )
 					* this.settings.getBiomeDepthWeight();
 			if (terrain >= TerrainLayerSampler.PLATEAU_LAYER)
 				l += 0.6D * (double)(terrain - TerrainLayerSampler.PLATEAU_LAYER);
 			
			boolean isRiver = terrain == TerrainLayerSampler.RIVER_LAYER;
			double[] landmass = this.biomeProvider.getLandmass(x * 4, z * 4);
			boolean isOcean = landmass[4] < EvoBiomeProvider.oceanThreshold - EvoBiomeProvider.beachThreshold / (double)EvoBiomeProvider.oceanOctaves / EvoBiomeProvider.oceanScale;
			boolean isBeach = !isOcean && (landmass[4] < EvoBiomeProvider.oceanThreshold) && this.biomeProvider.canBeBeach(x * 4, z * 4);
			
			if (isBeach | isOcean)
			{
				l = this.settings.getBiomeDepthOffset() + MathHelper.clamp((landmass[4] - EvoBiomeProvider.oceanThreshold + 0.025) * 6.0, -1.9, 0.0) * this.settings.getBiomeDepthWeight();
			}
         
			if (isRiver)
			{
				if (isBeach | isOcean)
				{
					if (l > (this.settings.getBiomeDepthOffset() + this.biomeProvider.decodeBiome(BiomeKeys.RIVER).getDepth()) * this.settings.getBiomeDepthWeight())
						l = (this.settings.getBiomeDepthOffset() + this.biomeProvider.decodeBiome(BiomeKeys.RIVER).getDepth()) * this.settings.getBiomeDepthWeight();
				}
				else
				{
					l = (this.settings.getBiomeDepthOffset() + this.biomeProvider.decodeBiome(BiomeKeys.OCEAN).getDepth()) * this.settings.getBiomeDepthWeight();
				}
			}
		}

		for(int m = -2; m <= 2; ++m) 
		{
			for(int n = -2; n <= 2; ++n)
			{
	 			double temperature = this.biomeProvider.getTemperature((x + m) * 4, (z + n) * 4)[1];
	 			double humidity =  this.biomeProvider.getTemperature((x + m) * 4, (z + n) * 4)[1];
	 			int terrain = this.terrainLayer.sample((x + m) * 4, (z + n) * 4);
	 			double noiseDepth = (0.4//this.settings.getBiomeDepth() 
	 					+ ( (0.175 - humidity * temperature * 0.175) * this.settings.getBiomeDepthFactor()) )
	 					* this.settings.getBiomeDepthWeight();
	 			if (terrain >= TerrainLayerSampler.PLATEAU_LAYER)
	 				noiseDepth += 0.45D * (double)terrain;
				double noiseScale = (this.settings.getBiomeScale() + (terrain == 1 ? this.settings.getBiomeScaleFactor() : 0.075D)) * this.settings.getBiomeScaleWeight();
				if (terrain >= 2)
					noiseScale = 0.0D * this.settings.getBiomeScaleWeight();
	             
    			boolean isRiver = terrain == TerrainLayerSampler.RIVER_LAYER;
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
    			
				if (generationShapeConfig.isAmplified() && noiseDepth > 0.0D) {
					noiseDepth = 1.0D + noiseDepth * 2.0D;
					noiseScale = 1.0D + noiseScale * 4.0D;
				}

				double u = noiseDepth > l ? 0.5D : 1.0D;
				double v = u * BIOME_WEIGHT_TABLE[m + 2 + (n + 2) * 5] / (noiseDepth + 2.0D);
				g += noiseScale * v;
				h += noiseDepth * v;
				i += v;
			}
		}

		double w = h / i;
		double y = g / i;
        ai = w * 0.5D - 0.125D;
        aj = y * 0.9D + 0.1D;
        double finalDepth = ai * 0.265625D;
        double finalScale = 96.0D / aj;

		double xzScale = 684.412D * generationShapeConfig.getSampling().getXZScale();
		double yScale = 684.412D * generationShapeConfig.getSampling().getYScale();
		double xzFactor = xzScale / generationShapeConfig.getSampling().getXZFactor();
		double yFactor = yScale / generationShapeConfig.getSampling().getYFactor();
		double topTarget = (double)generationShapeConfig.getTopSlide().getTarget();
		double topSize = (double)generationShapeConfig.getTopSlide().getSize();
		double topOffset = (double)generationShapeConfig.getTopSlide().getOffset();
		double bottomTarget = (double)generationShapeConfig.getBottomSlide().getTarget();
		double bottomSize = (double)generationShapeConfig.getBottomSlide().getSize();
		double bottomOffset = (double)generationShapeConfig.getBottomSlide().getOffset();
		double density = generationShapeConfig.hasRandomDensityOffset() ? this.getRandomDensityAt(x, z) : 0.0D;
		double densityFactor = generationShapeConfig.getDensityFactor();
		double densityOffset = generationShapeConfig.getDensityOffset();

		double[] landmass = this.biomeProvider.getLandmass(x * 4, z * 4);
		boolean isBeach = (landmass[4] < EvoBiomeProvider.oceanThreshold) && this.biomeProvider.canBeBeach(x * 4, z * 4);
		int terrainType = this.terrainLayer.sample(x * 4, z * 4);
		if (terrainType != 0 && !isBeach)			density *= 2.75;

		for(int ar = 0; ar <= this.noiseSizeY; ++ar) {
			double noiseSample = this.sampleNoise(x, ar, z, xzScale, yScale, xzFactor, yFactor);
			double densitySample = 1.0D - (double)ar * 2.0D / (double)this.noiseSizeY + density;
			densitySample = densitySample * densityFactor + densityOffset;
			double scaledSample = (densitySample + finalDepth) * finalScale;
			if (scaledSample > 0.0D) {
				noiseSample += scaledSample * 4.0D;
			} else {
				noiseSample += scaledSample;
			}

			double ax;
			if (topSize > 0.0D) {
				ax = ((double)(this.noiseSizeY - ar) - topOffset) / topSize;
				noiseSample = MathHelper.clampedLerp(topTarget, noiseSample, ax);
			}

			if (bottomSize > 0.0D) {
				ax = ((double)ar - bottomOffset) / bottomSize;
				noiseSample = MathHelper.clampedLerp(bottomTarget, noiseSample, ax);
			}

			buffer[ar] = noiseSample;
		}
	}

	@Override
	protected double sampleNoise(int x, int y, int z, double horizontalScale, double verticalScale, double horizontalStretch, double verticalStretch) 
	{
		double x1 = (double)x * horizontalScale;
		double y1 = (double)y * verticalScale;
		double z1 = (double)z * horizontalScale;
		double d = this.lowerInterpolatedNoise.sample(x1, y1, z1);
		double e = this.upperInterpolatedNoise.sample(x1, y1, z1);

		x1 = (double)x * horizontalStretch;
		y1 = (double)y * verticalStretch;
		z1 = (double)z * horizontalStretch;
		double f = this.interpolationNoise.sample(x1, y1, z1);
		
		return MathHelper.clampedLerp(d / 512.0D, e / 512.0D, (f / 10.0D + 1.0D) / 2.0D);
	}
	
	public static void register()
	{
		Registry.register(Registry.CHUNK_GENERATOR, (String)"evo", EvoChunkGenerator.CODEC);
	}
}
