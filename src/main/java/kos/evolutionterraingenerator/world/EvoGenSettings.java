package kos.evolutionterraingenerator.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.gen.OverworldGenSettings;

public class EvoGenSettings extends OverworldGenSettings
{
	private final int seaLevel = 63;
	private final float biomeScale = 1.0f;
	private final float humidScale = 1.0f;
	private final float tempScale = 1.0f;
	private final float oceanScale = 1.0f;
	
	private static final BlockState oceanBlock = Blocks.WATER.getDefaultState();
	
	private final double mainNoiseCoordScale = 175.0;
	private final double mainNoiseHeightScale = 75.0;
	private final double depthNoiseScaleX = 200.0;
	private final double depthNoiseScaleZ = 200.0;
	private final double coordScale = 160.0;
	private final double heightScale = 60.0;
	private final double depthBaseSize = 8.5;
	private final double heightStretch = 12.0;
	private final double lowerLimitScale = 512.0;
	private final double upperLimitScale = 512.0;
	
	private final float biomeScaleWeight = 1.0F;
	private final float biomeScaleOffset = 0.0F;
	private final float biomeDepthWeight = 1.0F;
	private final float biomeDepthOffset = 0.0F;

	public int getSeaLevel()
	{
		return seaLevel;
	}

	public float getBiomeScale()
	{
		return biomeScale;
	}

	public float getHumidityScale()
	{
		return humidScale;
	}

	public float getTemperatureScale()
	{
		return tempScale;
	}

	public float getOceanScale()
	{
		return oceanScale;
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

	public static EvoGenSettings createSettings()
	{
		return new EvoGenSettings();
	}

	public float getBiomeDepthWeight() {
		return biomeDepthWeight;
	}

	public float getBiomeScaleWeight() {
		return biomeScaleWeight;
	}

	public float getBiomeDepthOffset() {
		return biomeDepthOffset;
	}

	public float getBiomeScaleOffset() {
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
}
