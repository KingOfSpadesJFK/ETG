package kos.evolutionterraingenerator.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.gen.OverworldGenSettings;

public class EvoGenSettings extends OverworldGenSettings
{
	private final int seaLevel = 63;
	
	private static final BlockState oceanBlock = Blocks.WATER.getDefaultState();

	private int noiseOctaves = 8;
	private double mainNoiseCoordScale = 160.0;
	private double mainNoiseHeightScale = 60.0;
	private double depthNoiseScaleX = 200.0;
	private double depthNoiseScaleZ = 200.0;
	private double coordScale = 175.0;
	private double heightScale = 75.0;
	private double depthBaseSize = 8.5;
	private double heightStretch = 12.0;
	private double lowerLimitScale = 512.0;
	private double upperLimitScale = 512.0;
	
	private double biomeDepth = 0.75;
	private double biomeScale = 0.95;
	private double biomeDepthFactor = 0.0275;
	private double biomeScaleFactor = 0.3;
	
	private double biomeDepthWeight = 1.0;
	private double biomeDepthOffset = 0.0;
	private double biomeScaleWeight = 1.0;
	private double biomeScaleOffset = 0.0;
	
	public EvoGenSettings(CompoundNBT nbtSettings)
	{
		
	}

	public int getNoiseOctaves() 
	{
		return noiseOctaves;
	}

	public int getSeaLevel()
	{
		return seaLevel;
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

	public double getMainNoiseCoordScale() {
		return mainNoiseCoordScale;
	}

	public double getMainNoiseHeightScale() {
		return mainNoiseHeightScale;
	}

	public double getCoordScale() {
		return coordScale;
	}

	public double getHeightScale() {
		return heightScale;
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
