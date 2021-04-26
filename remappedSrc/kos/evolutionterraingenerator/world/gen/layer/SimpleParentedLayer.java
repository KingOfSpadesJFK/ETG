package kos.evolutionterraingenerator.world.gen.layer;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.layer.type.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public class SimpleParentedLayer implements IdentitySamplingLayer {
	private final int maxValue;
	private final int minValue;
	private final boolean absolute;
	
	public SimpleParentedLayer(int maxValue, int minValue, boolean absolute)
	{
		this.maxValue = maxValue;
		this.minValue = minValue;
		this.absolute = absolute;
	}
	@Override
	public int sample(LayerRandomnessSource context, int value) {
		if (absolute)
			return MathHelper.clamp(Math.abs(context.nextInt(maxValue + 1)), minValue, maxValue);
		return MathHelper.clamp(context.nextInt(maxValue + 1), minValue, maxValue);
	}

}
