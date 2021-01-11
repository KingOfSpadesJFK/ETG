package kos.evolutionterraingenerator.util;

import kos.evolutionterraingenerator.util.noise.OpenSimplexNoise;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.world.gen.ChunkRandom;

public class OpenSimplexNoiseOctaves implements NoiseSampler
{
    /** Collection of noise generation functions.  Output is combined to produce different octaves of noise. */
    private final OpenSimplexNoise[] octaveSamplers;

    public OpenSimplexNoiseOctaves(ChunkRandom seed, int octavesIn)
    {
        this.octaveSamplers = new OpenSimplexNoise[octavesIn];
        
        for (int i = 0; i < octavesIn; ++i)
            this.octaveSamplers[i] = new OpenSimplexNoise(seed);
    }

    public OpenSimplexNoise getOctave(int i)
    { return this.octaveSamplers[i]; }

	public double eval(double x, double y, double z, double d, double e, boolean bl) 
	{
		double f = 0.0D;
		double h = 1.0D;

        for(OpenSimplexNoise noise : octaveSamplers)
        {
            double x1 = maintainPrecision(x * h);
            double y1 = maintainPrecision(y * h);
            double z1 = maintainPrecision(z * h);
            
            f += noise.eval(x1, bl ? -noise.yCoord : y1, z1, d, e) / h;
            h /= 2.0D;
        }

        return f;
	}
	
	public double sample(double x, double y, double z)
	{
        return eval(x, y, z, 0.0, 0.0, false);
	}

	public double sample(double x, double z) 
	{
        return eval(x, z, 0.0, 0.0, 0.0, false);
	}

	public static double maintainPrecision(double p_215461_0_) 
	{
		return p_215461_0_ - (double)MathHelper.lfloor(p_215461_0_ / 3.3554432E7D + 0.5D) * 3.3554432E7D;
	}

	@Override
	public double sample(double x, double y, double idk, double idk1) 
	{
		return eval(x, y, 0.0, idk, idk1, false);
	}
}
