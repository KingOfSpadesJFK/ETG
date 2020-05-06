package kos.evolutionterraingenerator.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.gen.OverworldGenSettings;

public class EvoGenSettings extends OverworldGenSettings
{
	private final int seaLevel = 63;
	
	private static final BlockState oceanBlock = Blocks.WATER.getDefaultState();
	
	private double mainNoiseCoordScale = 175.0;
	private double mainNoiseHeightScale = 75.0;
	private double depthNoiseScaleX = 200.0;
	private double depthNoiseScaleZ = 200.0;
	private double coordScale = 160.0;
	private double heightScale = 60.0;
	private double depthBaseSize = 8.5;
	private double heightStretch = 12.0;
	private double lowerLimitScale = 512.0;
	private double upperLimitScale = 512.0;
	
	private float biomeScaleWeight = 1.0F;
	private float biomeScaleOffset = 0.0F;
	private float biomeDepthWeight = 1.0F;
	private float biomeDepthOffset = 0.0F;
	
	private boolean useBOP = false;
	
	public EvoGenSettings(CompoundNBT nbtSettings)
	{
		useBOP = nbtSettings.getBoolean(EvoType.USE_BOP_TAG);
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

	public boolean isUseBOP() {
		return useBOP;
	}
}
