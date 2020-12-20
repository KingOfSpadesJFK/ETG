package kos.evolutionterraingenerator.util;

import kos.evolutionterraingenerator.util.noise.OpenSimplexNoiseGenerator;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.INoiseGenerator;

public class OpenSimplexNoiseOctaves implements INoiseGenerator
{
    /** Collection of noise generation functions.  Output is combined to produce different octaves of noise. */
    private final OpenSimplexNoiseGenerator[] octaves;

    public OpenSimplexNoiseOctaves(SharedSeedRandom seed, int octavesIn)
    {
        this.octaves = new OpenSimplexNoiseGenerator[octavesIn];
        
        for (int i = 0; i < octavesIn; ++i)
            this.octaves[i] = new OpenSimplexNoiseGenerator(seed);
    }

    public OpenSimplexNoiseGenerator getOpenSimplexOctave(int i)
    { return this.octaves[i]; }

	public double eval(double x, double y, double z, double p_215462_7_, double p_215462_9_, boolean two_dimensional) 
	{
        double d0 = 1.0D;
        double d1 = 0.0D;

        for(OpenSimplexNoiseGenerator noise : octaves)
        {
            double x1 = maintainPrecision(x * d0);
            double y1 = maintainPrecision(y * d0);
            double z1 = maintainPrecision(z * d0);
            
            d1 += noise.eval(x1, two_dimensional ? -noise.yCoord : y1, z1) / d0;
            d0 /= 2.0D;
        }

        return d1;
	}
	
	public double getNoise(double x, double y, double z)
	{
        return eval(x, y, z, 0, 0, false);
	}

	public double getNoise(double x, double z) 
	{
        return eval(x, 0, z, 0, 0, true);
	}

	public static double maintainPrecision(double p_215461_0_) 
	{
		return p_215461_0_ - (double)MathHelper.lfloor(p_215461_0_ / 3.3554432E7D + 0.5D) * 3.3554432E7D;
	}

	@Override
	public double noiseAt(double x, double y, double idk, double idk1) 
	{
		return eval(x, y, 0.0, idk, idk1, false);
	}

	public OpenSimplexNoiseGenerator getOctave(int i) 
	{
		return octaves[i];
	}
}
