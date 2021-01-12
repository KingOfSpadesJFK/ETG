package kos.evolutionterraingenerator.util;

import java.util.stream.IntStream;

import kos.evolutionterraingenerator.util.noise.OpenSimplexNoise;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.gen.ChunkRandom;

public class OpenSimplexNoiseOctaves extends OctavePerlinNoiseSampler
{
    /** Collection of noise generation functions.  Output is combined to produce different octaves of noise. */

    public OpenSimplexNoiseOctaves(ChunkRandom seed, int octavesIn)
    {
    	super(seed, IntStream.rangeClosed(-1 * octavesIn + 1, 0));
        this.octaveSamplers = new OpenSimplexNoise[octavesIn];
        
        for (int i = 0; i < octavesIn; ++i)
            this.octaveSamplers[i] = new OpenSimplexNoise(seed);
    }

    public PerlinNoiseSampler getOctave2(int i)
    { return this.octaveSamplers[i]; }

	public double sample(double x, double y, double z, double d, double e, boolean bl) 
	{
		double f = 0.0D;
		double h = 1.0D;

        for(PerlinNoiseSampler noise : octaveSamplers)
        {
            double x1 = maintainPrecision(x * h);
            double y1 = maintainPrecision(y * h);
            double z1 = maintainPrecision(z * h);
            
            f += noise.sample(x1, bl ? -noise.originX : y1, z1, d, e) / h;
            h /= 2.0D;
        }

        return f;
	}
	
	public double sample(double x, double y, double z)
	{
        return sample(x, y, z, 0.0, 0.0, false);
	}

	public double sample(double x, double z) 
	{
        return sample(x, z, 0.0, 0.0, 0.0, false);
	}

	@Override
	public double sample(double x, double y, double d, double e) 
	{
		return sample(x, y, 0.0, d, e, false);
	}

	public static double maintainPrecision(double p_215461_0_) 
	{
		return p_215461_0_ - (double)MathHelper.lfloor(p_215461_0_ / 3.3554432E7D + 0.5D) * 3.3554432E7D;
	}
}
