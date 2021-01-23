package kos.evolutionterraingenerator.world.gen;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import kos.evolutionterraingenerator.util.noise.OctaveOpenSimplexSampler;
import kos.evolutionterraingenerator.world.biome.BiomeList;
import kos.evolutionterraingenerator.world.biome.EvoBiomeSource;
import kos.evolutionterraingenerator.world.gen.layer.LayerBuilder;
import kos.evolutionterraingenerator.world.gen.layer.TerrainLayerSampler;
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
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public final class EvoChunkGenerator extends NoiseChunkGenerator
{
	public static final Codec<EvoChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(EvoBiomeSource.CODEC.fieldOf("biome_source").forGetter((noiseChunkGenerator) -> {
			return noiseChunkGenerator.biomeSource;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((noiseChunkGenerator) -> {
			return noiseChunkGenerator.seed;
		}), ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter((noiseChunkGenerator) -> {
			return () -> noiseChunkGenerator.settings;
		}) ).apply(instance, instance.stable(EvoChunkGenerator::new));
	});
	public static final Biome[] PLAINS_BIOMES = Util.make(new Biome[1024], (arr) -> 
	{
		for(int i = 0; i < arr.length; i++)
			arr[i] = BuiltinBiomes.PLAINS;
	});
			
	private final int verticalNoiseGranularity;
	private final int noiseSizeY;
	private final EvoBiomeSource biomeSource;
	private final long seed;
	private final EvoGenSettings settings;
	private final StructuresConfig structureSettings;
	private final int maxBuildHeight;
	
	private final OctaveOpenSimplexSampler lowerInterpolatedNoise;
	private final OctaveOpenSimplexSampler upperInterpolatedNoise;
	private final OctaveOpenSimplexSampler interpolationNoise;
	private final TerrainLayerSampler terrainLayer;
	private final TerrainLayerSampler plateauSteps;

	public EvoChunkGenerator(EvoBiomeSource biomeSource, long seed, Supplier<ChunkGeneratorSettings> settings) 
	{
		super(biomeSource, seed, settings);
		this.seed = seed;
		this.settings = (EvoGenSettings)(settings.get());
		this.structureSettings = settings.get().getStructuresConfig();
		GenerationShapeConfig noisesettings = settings.get().getGenerationShapeConfig();
		this.biomeSource = biomeSource;
		this.biomeSource.setChunkGenerator(this);
		this.maxBuildHeight = noisesettings.getHeight();
		this.verticalNoiseGranularity = noisesettings.getSizeVertical() * 4;
		this.noiseSizeY = noisesettings.getHeight() / this.verticalNoiseGranularity;
		ChunkRandom random = new ChunkRandom(seed);
		this.lowerInterpolatedNoise = new OctaveOpenSimplexSampler(random, 16);
		this.upperInterpolatedNoise = new OctaveOpenSimplexSampler(random, 16);
		this.interpolationNoise = new OctaveOpenSimplexSampler(random, 8);
		this.terrainLayer = LayerBuilder.build(seed, LayerBuilder.TERRAIN_TYPE, 5, 0);
		this.plateauSteps = LayerBuilder.build(seed, LayerBuilder.PLATEAU_STEPS, TerrainLayerSampler.PLATEAU_STEPPE_COUNT);
	}

	@Override
	public int getWorldHeight() 
	{
		return this.maxBuildHeight;
	}

	@Override
	public int getSeaLevel() {
		return this.settings.getSeaLevel();
	}

	/*
	 * ENTITY SPAWINNG
	 */
	@Override
	public void populateEntities(ChunkRegion region) {
		int x = region.getCenterChunkX();
		int z = region.getCenterChunkZ();
		int y = region.getChunk(x, z).sampleHeightmap(Heightmap.Type.OCEAN_FLOOR_WG, x << 4, z << 4);
		Biome biome = region.getBiome(new BlockPos(x << 4, y, z << 4));
		ChunkRandom sharedseedrandom = new ChunkRandom();
		sharedseedrandom.setPopulationSeed(region.getSeed(), x << 4, z << 4);
		SpawnHelper.populateEntities(region, biome, x, z, sharedseedrandom);
	}

	/**
	 * Sets the biomes in the chunk. Since biomes are height 
	 * dependent, I just use an array with plains biomes and 
	 * set the biomes in the surface building 
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
	public void carve(long l1, BiomeAccess biomeAccess, Chunk chunkIn, GenerationStep.Carver genStage) 
	{
		BiomeAccess biomemanager = biomeAccess.withSource(this.biomeSource);
		ChunkRandom sharedseedrandom = new ChunkRandom();
		ChunkPos chunkpos = chunkIn.getPos();
		int j = chunkpos.x;
		int k = chunkpos.z;
		int x = chunkpos.x << 4;
		int z = chunkpos.z << 4;
		int y = chunkIn.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR_WG, x, z);
		GenerationSettings biomegenerationsettings = this.biomeSource.getBiomeForNoiseGen(x, y, z, false).getGenerationSettings();
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
			  	Biome[] biomes = this.biomeSource.getBiomesByHeight(x1, y, z1);
			  	double noise = this.surfaceDepthNoise.sample((double)x1 * 0.0625D, (double)z1 * 0.0625D, 0.0625D, (double)i * 0.0625D) * 15.0D;
			  	if (x1 % 4 == 0 && z1 % 4 == 0)
			  	{
			  		abiome[k] = biomes[0];
			  		k++;
			  	}
		 		double humidity = this.biomeSource.getHumidity(x, z)[1];
		 		double temperature = this.biomeSource.getTemperature(x, z)[1];
		 		if (y <= 130 + Math.rint(40.0 * humidity * temperature + ((double)(sharedseedrandom.nextInt() % 30 - 15) * (0.125 + humidity * temperature * 0.875))) ) {
		 			if (y < this.getSeaLevel()) {
			 			biomes[0].buildSurface(sharedseedrandom, chunkIn, x1, z1, y, noise, this.settings.getDefaultBlock(), this.settings.getDefaultFluid(), this.getSeaLevel(), worldRegion.getSeed());
			 			continue;
		 			}
		 			int heightDiff = 0;
		 			int samples = 0;
		 			for (int a = -2; a <= 2; a++) {
		 				for (int b = -2; b <= 2; b++) {
		 					if (b + j >= 0 && b + j < 16 && a + i >= 0 && a + i < 16) {
		 						heightDiff += Math.abs(y - chunkIn.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR_WG, x1 + b, z1 + a));
		 						samples++;
		 					}
		 				}
		 			}
		 			if ((double)heightDiff / (double)samples >= 1.75) {
		 				BlockState top = biomes[0].getGenerationSettings().getSurfaceConfig().getTopMaterial();
		 				BlockState stone = Blocks.STONE.getDefaultState();
		 				if ((double)heightDiff / (double)samples >= 2.75) {
		 					top = stone;
		 				}
		 				
		 				if (top == Blocks.SAND.getDefaultState())
		 					top = Blocks.SANDSTONE.getDefaultState();
		 				if (top == Blocks.RED_SAND.getDefaultState())
		 					top = Blocks.RED_TERRACOTTA.getDefaultState();
		 				
		 				ConfiguredSurfaceBuilder<TernarySurfaceConfig> sb = SurfaceBuilder.DEFAULT
		 					    .withConfig(new TernarySurfaceConfig(top, stone, stone));
		 				sb.initSeed(worldRegion.getSeed());
		 				sb.generate(sharedseedrandom, chunkIn, biomes[0], x1, z1, y, noise, this.settings.getDefaultBlock(), this.settings.getDefaultFluid(), this.getSeaLevel(), worldRegion.getSeed());
		 				continue;
	 				}
		 			biomes[0].buildSurface(sharedseedrandom, chunkIn, x1, z1, y, noise, this.settings.getDefaultBlock(), this.settings.getDefaultFluid(), this.getSeaLevel(), worldRegion.getSeed());
				}
			}
		}
		while (k < BiomeArray.DEFAULT_LENGTH)
		{
			abiome[k] = abiome[k % 16];
	 		k++;
		}
		((ProtoChunk)chunkIn).setBiomes(new BiomeArray(this.biomeSource.getRegistry(), abiome));
		this.makeBedrock(chunkIn, sharedseedrandom);
	}
	
	private void makeBedrock(Chunk chunk, Random random)
	{
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = chunk.getPos().getStartX();
		int j = chunk.getPos().getStartZ();
		ChunkGeneratorSettings settings = this.settings;
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
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public ChunkGenerator withSeed(long seed) {
		return new EvoChunkGenerator(this.biomeSource.withSeed(seed), seed, () -> this.settings);
	}
	
	private boolean isOcean(Biome b) {
		return b == this.biomeSource.decodeBiome(BiomeList.OCEAN)
				|| b == this.biomeSource.decodeBiome(BiomeList.DEEP_OCEAN)
				|| b == this.biomeSource.decodeBiome(BiomeList.COLD_OCEAN)
				|| b == this.biomeSource.decodeBiome(BiomeList.DEEP_COLD_OCEAN)
				|| b == this.biomeSource.decodeBiome(BiomeList.FROZEN_OCEAN)
				|| b == this.biomeSource.decodeBiome(BiomeList.DEEP_FROZEN_OCEAN)
				|| b == this.biomeSource.decodeBiome(BiomeList.LUKEWARM_OCEAN)
				|| b == this.biomeSource.decodeBiome(BiomeList.DEEP_LUKEWARM_OCEAN)
				|| b == this.biomeSource.decodeBiome(BiomeList.WARM_OCEAN)
				|| b == this.biomeSource.decodeBiome(BiomeList.DEEP_WARM_OCEAN);
	}

	public boolean matchesSettings(long seed, RegistryKey<ChunkGeneratorSettings> settings) {
		return this.seed == seed && ((ChunkGeneratorSettings)this.settings).equals(settings);
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
		Biome biome = region.getBiome(new BlockPos(x, y, z));
		ChunkRandom sharedseedrandom = new ChunkRandom();
		long i1 = sharedseedrandom.setPopulationSeed(region.getSeed(), x, z);
		
		try 
		{
			double[] landmass = this.biomeSource.getLandmass(x, z);
			boolean isOcean = landmass[4] < EvoBiomeSource.oceanThreshold - EvoBiomeSource.beachThreshold / (double)EvoBiomeSource.oceanOctaves / EvoBiomeSource.oceanScale;
			boolean isBeach = !isOcean && (landmass[4] < EvoBiomeSource.oceanThreshold) && this.biomeSource.canBeBeach(x, z);
			if (!isBeach && !isOcean && this.terrainLayer.sample(x, z) == TerrainLayerSampler.RIVER_LAYER && y < this.getSeaLevel())
				this.biomeSource.decodeBiome(BiomeList.RIVER).generateFeatureStep(structManager, this, region, i1, sharedseedrandom, blockpos);
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
		Biome biome = this.biomeSource.getBiomeForNoiseGen(x, y, z, false);
		int terrain = this.terrainLayer.sample(x, z);
		if (isOcean(biome))
			terrain = 0;
		this.setStructureStart(ConfiguredStructureFeatures.STRONGHOLD, structureRegistry, structureManager, chunkIn, template, l, chunkpos, biome);
		
		for (Supplier<ConfiguredStructureFeature<?, ?>> supplier : biome.getGenerationSettings().getStructureFeatures()) 
		{
			if (terrain == TerrainLayerSampler.PLAINS_LAYER) {
				if (supplier.get() == ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN && this.biomeSource.hasStructureFeature(StructureFeature.RUINED_PORTAL)) {
					this.setStructureStart(ConfiguredStructureFeatures.RUINED_PORTAL, structureRegistry, structureManager, chunkIn, template, l, chunkpos, biome);
					continue;
				}
			}
			if (terrain == TerrainLayerSampler.MOUNTAINS_LAYER || terrain == TerrainLayerSampler.PLATEAU_LAYER) {
				if (supplier.get().feature == StructureFeature.VILLAGE)
					continue;
				if (supplier.get() == ConfiguredStructureFeatures.RUINED_PORTAL && this.biomeSource.hasStructureFeature(StructureFeature.RUINED_PORTAL)) {
					this.setStructureStart(ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN, structureRegistry, structureManager, chunkIn, template, l, chunkpos, biome);
					continue;
				}
			}
			if (this.biomeSource.hasStructureFeature(supplier.get().feature))
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
	
	private double calculateNoiseDepth(int x, int z)
	{
		double temperature = this.biomeSource.getTemperature(x, z)[1];
		double humidity =  this.biomeSource.getTemperature(x, z)[1];
		int terrain = this.terrainLayer.sample(x, z);
		double noiseDepth = this.settings.getNoiseDepth() 
				+ ( (0.175 - humidity * temperature * 0.175) * this.settings.getNoiseDepthFactor());
		if (terrain == TerrainLayerSampler.PLATEAU_LAYER)
			noiseDepth += 0.9 * (double)(this.plateauSteps.sample(x, z) + 1);
			
		boolean isRiver = terrain == TerrainLayerSampler.RIVER_LAYER;
		double[] landmass = this.biomeSource.getLandmass(x, z);
		boolean isOcean = landmass[4] < EvoBiomeSource.oceanThreshold - EvoBiomeSource.beachThreshold / (double)EvoBiomeSource.oceanOctaves / EvoBiomeSource.oceanScale;
		boolean isBeach = !isOcean && (landmass[4] < EvoBiomeSource.oceanThreshold) && this.biomeSource.canBeBeach(x, z);
		
		if (isBeach | isOcean) {
			noiseDepth = MathHelper.clamp((landmass[4] - EvoBiomeSource.oceanThreshold + 0.025) * 6.0, -1.9, 0.0);
		}
     
		if (isRiver) {
			if (isBeach | isOcean)
				if (noiseDepth > -0.5)
					noiseDepth = -0.5;
			else
				noiseDepth = -1.0;
		}
		
		return noiseDepth;
	}
	
	private double calculateNoiseScale(int x, int z)
	{
		int terrain = this.terrainLayer.sample(x, z);
		double noiseScale = (this.settings.getNoiseScale() + (terrain == 1 ? this.settings.getNoiseScaleFactor() : 0.075D));
		if (terrain == TerrainLayerSampler.PLATEAU_LAYER)
			noiseScale = 0.0D;
         
		boolean isRiver = terrain == TerrainLayerSampler.RIVER_LAYER;
		double[] landmass = this.biomeSource.getLandmass(x, z);
		boolean isOcean = landmass[4] < EvoBiomeSource.oceanThreshold - EvoBiomeSource.beachThreshold / (double)EvoBiomeSource.oceanOctaves / EvoBiomeSource.oceanScale;
		boolean isBeach = !isOcean && (landmass[4] < EvoBiomeSource.oceanThreshold) && this.biomeSource.canBeBeach(x, z);
		
		if (isBeach | isOcean)  {
			noiseScale = 0.0;
			if ((landmass[4] - EvoBiomeSource.oceanThreshold + 0.025) * 6.0 < -1.9)
				noiseScale = 0.275;
		}
     
		if (isRiver) {
			if (isBeach | isOcean) {
				noiseScale = 0.0;
			}
			else {
				noiseScale = 0.0;
			}
		}
		
		return noiseScale;
	}

	@Override
	protected void sampleNoiseColumn(double[] buffer, int x, int z) 
	{
		GenerationShapeConfig generationShapeConfig = this.settings.getGenerationShapeConfig();
		double ai;
		double aj;
		
		double g = 0.0;
		double h = 0.0;
		double i = 0.0;
		double l = calculateNoiseDepth(x << 2, z << 2) * this.settings.getNoiseDepthWeight() + this.settings.getNoiseDepthOffset();

		for(int m = -2; m <= 2; ++m) 
		{
			for(int n = -2; n <= 2; ++n)
			{
    			double noiseDepth = calculateNoiseDepth((x + m) << 2, (z + n) << 2) * this.settings.getNoiseDepthWeight() + this.settings.getNoiseDepthOffset();
    			double noiseScale = calculateNoiseScale((x + m) << 2, (z + n) << 2) * this.settings.getNoiseScaleWeight() + this.settings.getNoiseScaleOffset();
    			
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

		double[] landmass = this.biomeSource.getLandmass(x * 4, z * 4);
		if ((landmass[4] - EvoBiomeSource.oceanThreshold + 0.025) * 6.0 < -1.9) {
			xzFactor *= 5.0;
			yFactor *= 5.0;
		}
		
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

	/*
	 * Based off SuperCoder79's noise sampling code from JellySquid's Lithium mod
	 * 		You can thank me for screwing up and trying to find out  
	 * 		why this didn't work with Lithium initially
	 * 		Tho I feel like I could've just looked at
	 * 		MathHelper.clampedLerp and figured it out myself...
	 */
	private double sampleNoise(int x, int y, int z, double horizontalScale, double verticalScale, double horizontalStretch, double verticalStretch) 
	{
		double x1, y1, z1;
		x1 = (double)x * horizontalStretch;
		y1 = (double)y * verticalStretch;
		z1 = (double)z * horizontalStretch;
		double f = this.interpolationNoise.sample(x1, y1, z1);
		f = (f / 10.0D + 1.0D) / 2.0D;

		x1 = (double)x * horizontalScale;
		y1 = (double)y * verticalScale;
		z1 = (double)z * horizontalScale;
		if (f >= 1.0) {
			return this.upperInterpolatedNoise.sample(x1, z1) / 512.0;
		}
		else if (f <= 0.0) {
			return this.lowerInterpolatedNoise.sample(x1, z1) / 512.0;
		}
		
		double d = this.lowerInterpolatedNoise.sample(x1, z1);
		double e = this.upperInterpolatedNoise.sample(x1, z1);
		return MathHelper.lerp(f, d / 512.0D, e / 512.0D);
	}
	
	public static void register() {
		Registry.register(Registry.CHUNK_GENERATOR, (String)"evo", EvoChunkGenerator.CODEC);
	}
}
