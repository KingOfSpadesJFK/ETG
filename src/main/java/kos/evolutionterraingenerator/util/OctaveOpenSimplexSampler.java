package kos.evolutionterraingenerator.util;

import org.jetbrains.annotations.Nullable;
import kos.evolutionterraingenerator.util.noise.OpenSimplexNoiseSampler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.world.gen.ChunkRandom;

public class OctaveOpenSimplexSampler implements NoiseSampler
{
	/** Collection of noise generation functions.  Output is combined to produce different octaves of noise. */
	private final OpenSimplexNoiseSampler[] octaveSamplers;

	public OctaveOpenSimplexSampler(ChunkRandom random, int octaves)
	{
         if (octaves < 1)
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
		this.octaveSamplers = new OpenSimplexNoiseSampler[octaves];
		for (int i = 0; i < octaves; i++)
			this.octaveSamplers[i] = new OpenSimplexNoiseSampler(random);
	}

	@Nullable
	public OpenSimplexNoiseSampler getOctave(int octave) {
	   return this.octaveSamplers[octave];
	}

	public double sample(double x, double y, double z) 
	{
		double f = 0.0D;
		double h = 1.0D;

		for(int i = 0; i < this.octaveSamplers.length; ++i) 
		{
			OpenSimplexNoiseSampler noise = this.octaveSamplers[i];
			double x1 = maintainPrecision(x * h);
			double y1 = maintainPrecision(y * h);
			double z1 = maintainPrecision(z * h);
			
			f += noise.sample(x1, y1, z1) / h;
			h /= 2.0D;
		}

		return f;
	}

	public double sample(double x, double y) 
	{
		double f = 0.0D;
		double h = 1.0D;

		for(int i = 0; i < this.octaveSamplers.length; ++i) 
		{
			OpenSimplexNoiseSampler noise = this.octaveSamplers[i];
			double x1 = maintainPrecision(x * h);
			double y1 = maintainPrecision(y * h);
			
			f += noise.sample(x1, y1) / h;
			h /= 2.0D;
		}

		return f;
	}

	@Override
	public double sample(double x, double y, double d, double e) {
		return sample(x, y);
	}

	public static double maintainPrecision(double d) {
		return d - (double)MathHelper.lfloor(d / 3.3554432E7D + 0.5D) * 3.3554432E7D;
	}
}
