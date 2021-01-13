package kos.evolutionterraingenerator.util;

import java.util.stream.IntStream;

import org.jetbrains.annotations.Nullable;

import kos.evolutionterraingenerator.util.noise.OpenSimplexNoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.gen.ChunkRandom;

public class OctaveOpenSimplexSampler extends OctavePerlinNoiseSampler
{
	/** Collection of noise generation functions.  Output is combined to produce different octaves of noise. */
	private final OpenSimplexNoiseSampler[] octavesOpenSimplex;

	public OctaveOpenSimplexSampler(ChunkRandom seed, int octavesIn)
	{
		super(seed, IntStream.rangeClosed(-1 * octavesIn + 1, 0));
		this.octavesOpenSimplex = new OpenSimplexNoiseSampler[octavesIn];
		for (int i = 0; i < octavesIn; ++i)
			this.octavesOpenSimplex[i] = new OpenSimplexNoiseSampler(seed);
		
		this.octaveSamplers = this.octavesOpenSimplex;
	}

	public OpenSimplexNoiseSampler getOpenSimplexOctave(int octave) {
		return this.octavesOpenSimplex[this.octavesOpenSimplex.length - 1 - octave]; 
	}

	@Nullable
	@Override
	public PerlinNoiseSampler getOctave(int octave) {
	   return this.octavesOpenSimplex[this.octavesOpenSimplex.length - 1 - octave];
	}

	@Override
	public double sample(double x, double y, double z, double d, double e, boolean bl) 
	{
		double f = 0.0D;
		double h = 1.0D;

		for(OpenSimplexNoiseSampler noise : this.octavesOpenSimplex)
		{
			double x1 = maintainPrecision(x * h);
			double y1 = maintainPrecision(y * h);
			double z1 = maintainPrecision(z * h);
			
			f += noise.sample(x1, bl ? -noise.originX : y1, z1, d * h, e * h) / h;
			h /= 2.0D;
		}

		return f;
	}

	public double sample(double x, double y, double d, double e, boolean bl) 
	{
		double f = 0.0D;
		double h = 1.0D;

		for(OpenSimplexNoiseSampler noise : this.octavesOpenSimplex)
		{
			double x1 = maintainPrecision(x * h);
			double y1 = maintainPrecision(y * h);
			
			f += noise.sample(x1, bl ? -noise.originX : y1, d * h, e * h) / h;
			h /= 2.0D;
		}

		return f;
	}

	@Override
	public double sample(double x, double y, double z)
	{
		return sample(x, y, z, 0.0, 0.0, false);
	}

	public double sample(double x, double z) 
	{
		return sample(x, z, 0.0, 0.0, false);
	}

	@Override
	public double sample(double x, double y, double d, double e) 
	{
		return sample(x, y, d, e, false);
	}
}
