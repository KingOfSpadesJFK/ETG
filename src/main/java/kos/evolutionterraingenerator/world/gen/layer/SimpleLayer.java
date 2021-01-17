package kos.evolutionterraingenerator.world.gen.layer;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public class SimpleLayer implements InitLayer 
{
	private final int maxValue;
	private final int minValue;
	private final boolean absolute;
	
	public SimpleLayer(int maxValue, int minValue, boolean absolute)
	{
		this.maxValue = maxValue;
		this.minValue = minValue;
		this.absolute = absolute;
	}

	@Override
	public int sample(LayerRandomnessSource context, int x, int y) {
		if (x == 0 && y == 0) {
			return 0;
		} else {
			if (absolute)
				return MathHelper.clamp(Math.abs(context.nextInt(maxValue + 1)), minValue, maxValue);
			return MathHelper.clamp(context.nextInt(maxValue + 1), minValue, maxValue);
		}
	}
}
