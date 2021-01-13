package kos.evolutionterraingenerator.world.gen;

import java.util.function.Supplier;

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

public class EvoGenSettings
{	
	private static final BlockState oceanBlock = Blocks.WATER.getDefaultState();
	
	public static final RegistryKey<ChunkGeneratorSettings> SETTINGS = RegistryKey.of(Registry.NOISE_SETTINGS_WORLDGEN, new Identifier("evolution"));
	public static ChunkGeneratorSettings register()
	{
		ChunkGeneratorSettings settings = createEvoSurfaceSettings(new StructuresConfig(true), SETTINGS.getValue());
		register(SETTINGS, settings);
		return settings;
	}

	private static ChunkGeneratorSettings register(RegistryKey<ChunkGeneratorSettings> registryKey, ChunkGeneratorSettings settings) {
		BuiltinRegistries.add(BuiltinRegistries.CHUNK_GENERATOR_SETTINGS, (Identifier)registryKey.getValue(), settings);
		return settings;
	}

	private static ChunkGeneratorSettings createEvoSurfaceSettings(StructuresConfig structuresConfig, Identifier id) {
		double xzScale = 0.51138787747730898932222111827379D;
		double yScale = 0.21916623320456099542380905068877D;
	    return new ChunkGeneratorSettings(
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
	    		-10, 0, 63, false);
	}

	private int noiseOctaves = 8;
	private double depthNoiseScaleX = 200.0;
	private double depthNoiseScaleZ = 200.0;
	private double depthBaseSize = 8.5;
	private double heightStretch = 12.0;
	private double lowerLimitScale = 512.0;
	private double upperLimitScale = 512.0;
	
	private double biomeDepth = 0.205;
	private double biomeScale = 0.110;
	private double biomeDepthFactor = 0.30;
	private double biomeScaleFactor = 1.015;
	
	private double biomeDepthWeight = 1.0;
	private double biomeDepthOffset = 0.0;
	private double biomeScaleWeight = 1.0;
	private double biomeScaleOffset = 0.0;
	private final Supplier<ChunkGeneratorSettings> genSettings;
	
	public EvoGenSettings(Supplier<ChunkGeneratorSettings> settings)
	{
		this.genSettings = settings;
	}
	
	public Supplier<ChunkGeneratorSettings> getGeneratorSettings()
	{
		return genSettings;		
	}

	public int getNoiseOctaves() 
	{
		return noiseOctaves;
	}
	
	public BlockState getOceanBlock()
	{
		return oceanBlock;
	}
	
	public double getDepthNoiseScaleX() {
		return depthNoiseScaleX;
	}

	public double getDepthNoiseScaleZ() {
		return depthNoiseScaleZ;
	}

	public double getBiomeDepthWeight() {
		return biomeDepthWeight;
	}

	public double getBiomeScaleWeight() {
		return biomeScaleWeight;
	}

	public double getBiomeDepthOffset() {
		return biomeDepthOffset;
	}

	public double getBiomeScaleOffset() {
		return biomeScaleOffset;
	}

	public double getDepthBaseSize() {
		return depthBaseSize;
	}

	public double getHeightStretch() {
		return heightStretch;
	}

	public double getLowerLimitScale() {
		return lowerLimitScale;
	}

	public double getUpperLimitScale() {
		return upperLimitScale;
	}

	public double getCoordFactor() {
		return this.genSettings.get().getGenerationShapeConfig().getSampling().getXZFactor();
	}

	public double getHeightFactor() {
		return this.genSettings.get().getGenerationShapeConfig().getSampling().getYFactor();
	}

	public double getCoordScale() {
		return this.genSettings.get().getGenerationShapeConfig().getSampling().getXZScale();
	}

	public double getHeightScale() {
		return this.genSettings.get().getGenerationShapeConfig().getSampling().getYScale();
	}

	public double getBiomeScale() {
		return biomeScale;
	}

	public double getBiomeDepth() {
		return biomeDepth;
	}

	public double getBiomeScaleFactor() {
		return biomeScaleFactor;
	}

	public double getBiomeDepthFactor() {
		return biomeDepthFactor;
	}
}
