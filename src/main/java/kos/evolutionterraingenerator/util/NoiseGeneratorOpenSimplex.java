package kos.evolutionterraingenerator.util;

import java.util.stream.IntStream;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import kos.evolutionterraingenerator.util.noise.OpenSimplexNoise;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.INoiseGenerator;

public class NoiseGeneratorOpenSimplex implements INoiseGenerator
{
    /** Collection of noise generation functions.  Output is combined to produce different octaves of noise. */
    private final OpenSimplexNoise[] octaves;
    private final double field_227460_b_;
    private final double field_227461_c_;

    public NoiseGeneratorOpenSimplex(SharedSeedRandom seed, int max, int min) {
       this(seed, new IntRBTreeSet(IntStream.rangeClosed(-max, min).toArray()));
    }

    public NoiseGeneratorOpenSimplex(SharedSeedRandom seed, IntSortedSet octaves)
    {
        if (octaves.isEmpty()) 
        	throw new IllegalArgumentException("Need some octaves!");
        else 
        {
        	int i = -octaves.firstInt();
            int j = octaves.lastInt();
            int k = i + j + 1;
            if (k < 1)
               throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
            else 
            {
            	OpenSimplexNoise improvednoisegenerator = new OpenSimplexNoise(seed);
            	int l = j;
            	this.octaves = new OpenSimplexNoise[k];
            	if (j >= 0 && j < k && octaves.contains(0))
            		this.octaves[j] = improvednoisegenerator;

            	for(int i1 = j + 1; i1 < k; ++i1) {
            		if (i1 >= 0 && octaves.contains(l - i1)) {
            			this.octaves[i1] = new OpenSimplexNoise(seed);
            		} else {
            			seed.skip(262);
            		}
            	}

            	if (j > 0) {
            		long k1 = (long)(improvednoisegenerator.eval(0.0, 0.0, 0.0) * (double)9.223372E18F);
            		SharedSeedRandom sharedseedrandom = new SharedSeedRandom(k1);

            		for(int j1 = l - 1; j1 >= 0; --j1) {
            			if (j1 < k && octaves.contains(l - j1)) {
            				this.octaves[j1] = new OpenSimplexNoise(sharedseedrandom);
            			} else {
            				sharedseedrandom.skip(262);
            			}
            		}
            	}

            	this.field_227461_c_ = Math.pow(2.0, (double)j);
            	this.field_227460_b_ = 1.0 / (Math.pow(2.0, (double)k) - 1.0);
            }
        }
    }

    /**
     * pars:(par2,3,4=noiseOffset ; so that adjacent noise segments connect) (pars5,6,7=x,y,zArraySize),(pars8,10,12 =
     * x,y,z noiseScale)
     */
    public double[] generateNoiseOctaves(double[] noiseArray, int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale)
    {
        if (noiseArray == null)
        {
            noiseArray = new double[xSize * ySize * zSize];
        }
        else
        {
            for (int i = 0; i < noiseArray.length; ++i)
            {
                noiseArray[i] = 0.0D;
            }
        }

        double d3 = 1.0D;

        for (int j = 0; j < this.octaves.length; ++j)
        {
            double d0 = (double)xOffset * d3 * xScale;
            double d1 = (double)yOffset * d3 * yScale;
            double d2 = (double)zOffset * d3 * zScale;
            long k = MathHelper.lfloor(d0);
            long l = MathHelper.lfloor(d2);
            d0 = d0 - (double)k;
            d2 = d2 - (double)l;
            k = k % 16777216L;
            l = l % 16777216L;
            d0 = d0 + (double)k;
            d2 = d2 + (double)l;
            populateNoiseArray(noiseArray, d0, d1, d2, xSize, ySize, zSize, xScale * d3, yScale * d3, zScale * d3, d3, octaves[j]);
            d3 /= 2.0D;
        }

        return noiseArray;
    }

    private void populateNoiseArray(double[] noiseArray, double xOffset, double yOffset, double zOffset, int xSize, int ySize,
			int zSize, double xScale, double yScale, double zScale, double noiseScale, OpenSimplexNoise noise)
    {
    	if (ySize == 1)
    	{
        	int c = 0;
        	for (int i = 0; i < xSize; i++)
        	{
            	double x = xOffset + (double)i * xScale;
        		for (int j = 0; j < zSize; j++)
        		{
        			double y = yOffset * yScale;
        			double z = zOffset + (double)j * zScale;

        			noiseArray[c] += noise.eval(x, y, z) / noiseScale;
        			c++;
        		}
        	}
    	}
    	else
    	{
        	int c = 0;
        	for (int i = 0; i < xSize; i++)
        	{
            	double x = xOffset + (double)i * xScale;
        		for (int j = 0; j < zSize; j++)
        		{
            		double z = zOffset + (double)j * zScale;
        			for (int k = 0; k < ySize; k++)
        			{
            			double y = yOffset + (double)k * yScale;
            			noiseArray[c] += noise.eval(x, y, z) / noiseScale;
            			c++;
        			}
        		}
        	}
    	}
	}

	/**
     * Bouncer function to the main one with some default arguments.
     */
    public double[] generateNoiseOctaves(double[] noiseArray, int xOffset, int zOffset, int xSize, int zSize, double xScale, double zScale)
    {
        return this.generateNoiseOctaves(noiseArray, xOffset, 10, zOffset, xSize, 1, zSize, xScale, 1.0D, zScale);
    }

    public OpenSimplexNoise getOpenSimplexOctave(int i) {
       return this.octaves[i];
    }

	public double func_215460_a(double x, double y, double z, double unkown1, double unkown2, boolean two_dimensional) 
	{
		double d0 = 0.0D;
		double d1 = this.field_227461_c_;
		double d2 = this.field_227460_b_;

		for(OpenSimplexNoise noise : this.octaves) 
		{
			if (noise != null)
				d0 += noise.eval(maintainPrecision(x * d1), two_dimensional ? -noise.yCoord : maintainPrecision(y * d1), maintainPrecision(z * d1)) * d2;

			d1 /= 2.0D;
			d2 *= 2.0D;
		}

	    return d0;
	}
	
	public double getNoise(double x, double y, double z)
	{
        return func_215460_a(x, y, z, 0, 0, false);
	}

	public double getNoise(double x, double z) 
	{
        return func_215460_a(x, 0, z, 0, 0, true);
	}

	public static double maintainPrecision(double p_215461_0_) 
	{
		return p_215461_0_ - (double)MathHelper.lfloor(p_215461_0_ / 3.3554432E7D + 0.5D) * 3.3554432E7D;
	}

	@Override
	public double noiseAt(double x, double y, double z, double unkown) 
	{
		return func_215460_a(x, y, z, unkown, 0.0, false);
	}
}
