package kos.evolutionterraingenerator.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.NoiseSamplingConfig;
import net.minecraft.world.gen.chunk.SlideConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;

public class EvoGenSettings extends ChunkGeneratorSettings
{	
	public static final Codec<EvoGenSettings> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(StructuresConfig.CODEC.fieldOf("structures").forGetter(EvoGenSettings::getStructuresConfig), 
				GenerationShapeConfig.CODEC.fieldOf("noise").forGetter(EvoGenSettings::getGenerationShapeConfig), 
				BlockState.CODEC.fieldOf("default_block").forGetter(EvoGenSettings::getDefaultBlock), 
				BlockState.CODEC.fieldOf("default_fluid").forGetter(EvoGenSettings::getDefaultFluid),
				Codec.intRange(-20, 276).fieldOf("bedrock_roof_position").forGetter(EvoGenSettings::getBedrockCeilingY), 
				Codec.intRange(-20, 276).fieldOf("bedrock_floor_position").forGetter(EvoGenSettings::getBedrockFloorY), 
				Codec.intRange(0, 255).fieldOf("sea_level").forGetter(EvoGenSettings::getSeaLevel), 
				Codec.BOOL.fieldOf("disable_mob_generation").forGetter(EvoGenSettings::isMobGenerationDisabled),
				Codec.DOUBLE.fieldOf("noise_depth").forGetter(EvoGenSettings::getNoiseDepth),
				Codec.DOUBLE.fieldOf("noise_scale").forGetter(EvoGenSettings::getNoiseDepth),
				Codec.DOUBLE.fieldOf("noise_depth_factor").forGetter(EvoGenSettings::getNoiseDepth),
				Codec.DOUBLE.fieldOf("noise_scale_factor").forGetter(EvoGenSettings::getNoiseDepth),
				Codec.DOUBLE.fieldOf("noise_depth_weight").forGetter(EvoGenSettings::getNoiseDepth),
				Codec.DOUBLE.fieldOf("noise_depth_offset").forGetter(EvoGenSettings::getNoiseDepth),
				Codec.DOUBLE.fieldOf("noise_scale_weight").forGetter(EvoGenSettings::getNoiseDepth),
				Codec.DOUBLE.fieldOf("noise_scale_offset").forGetter(EvoGenSettings::getNoiseDepth)
				).apply(instance, EvoGenSettings::new);
	});
	public static final RegistryKey<ChunkGeneratorSettings> SETTINGS = RegistryKey.of(Registry.NOISE_SETTINGS_WORLDGEN, new Identifier("evolution"));
	public static ChunkGeneratorSettings register()
	{
		EvoGenSettings settings = createEvoSurfaceSettings(new StructuresConfig(true), SETTINGS.getValue());
		register(SETTINGS, settings);
		return settings;
	}

	private static ChunkGeneratorSettings register(RegistryKey<ChunkGeneratorSettings> registryKey, ChunkGeneratorSettings settings) {
		BuiltinRegistries.add(BuiltinRegistries.CHUNK_GENERATOR_SETTINGS, (Identifier)registryKey.getValue(), settings);
		return settings;
	}

	private static EvoGenSettings createEvoSurfaceSettings(StructuresConfig structuresConfig, Identifier id) {
		double xzScale = 0.51138787747730898932222111827379D;
		double yScale = 0.21916623320456099542380905068877D;
	    return new EvoGenSettings(
	    		structuresConfig,
	    		new GenerationShapeConfig(
	    				256,
	    				new NoiseSamplingConfig(
	    						xzScale, yScale, 160.0D, 60.0D),
	    				new SlideConfig(-10, 3, 0),
	    				new SlideConfig(-30, 0, 0),
	    				1,
	    				2,
	    				1.0D,
	    				-0.46875D,
	    				true, true, false, false), 
	    		Blocks.STONE.getDefaultState(), 
	    		Blocks.WATER.getDefaultState(),
	    		-10, 0, 63, false, 
	    		0.4, 
	    		0.110, 
	    		0.125,
	    		1.015, 
	    		1.0, 
	    		0.0, 
	    		1.0, 
	    		0.0);
	}
	
	private final double noiseDepth;
	private final double noiseScale;
	private final double noiseDepthFactor;
	private final double noiseScaleFactor;
	
	private final double noiseDepthWeight;
	private final double noiseDepthOffset;
	private final double noiseScaleWeight;
	private final double noiseScaleOffset;
	
	public EvoGenSettings(
			StructuresConfig structuresConfig, 
			GenerationShapeConfig generationShapeConfig, 
			BlockState defaultBlock, 
			BlockState defaultFluid, 
			int bedrockCeilingY, 
			int bedrockFloorY, 
			int seaLevel,
			boolean mobGenerationDisabled,
			double noiseDepth,
			double noiseScale,
			double noiseDepthFactor,
			double noiseScaleFactor,
			double noiseDepthWeight,
			double noiseDepthOffset,
			double noiseScaleWeight,
			double noiseScaleOffset) 
	{
		super(structuresConfig, generationShapeConfig, defaultBlock, defaultFluid, bedrockCeilingY, bedrockFloorY, seaLevel, mobGenerationDisabled);
		this.noiseDepth = noiseDepth;
		this.noiseScale = noiseScale;
		this.noiseDepthFactor = noiseDepthFactor;
		this.noiseScaleFactor = noiseScaleFactor;
		this.noiseDepthWeight = noiseDepthWeight;
		this.noiseDepthOffset = noiseDepthOffset;
		this.noiseScaleWeight = noiseScaleWeight;
		this.noiseScaleOffset = noiseScaleOffset;
	}

	public double getNoiseDepthWeight() {
		return noiseDepthWeight;
	}

	public double getNoiseScaleWeight() {
		return noiseScaleWeight;
	}

	public double getNoiseDepthOffset() {
		return noiseDepthOffset;
	}

	public double getNoiseScaleOffset() {
		return noiseScaleOffset;
	}

	public double getCoordFactor() {
		return this.getGenerationShapeConfig().getSampling().getXZFactor();
	}

	public double getHeightFactor() {
		return this.getGenerationShapeConfig().getSampling().getYFactor();
	}

	public double getCoordScale() {
		return this.getGenerationShapeConfig().getSampling().getXZScale();
	}

	public double getHeightScale() {
		return this.getGenerationShapeConfig().getSampling().getYScale();
	}

	public double getNoiseScale() {
		return noiseScale;
	}

	public double getNoiseDepth() {
		return noiseDepth;
	}

	public double getNoiseScaleFactor() {
		return noiseScaleFactor;
	}

	public double getNoiseDepthFactor() {
		return noiseDepthFactor;
	}
}
