package kos.evolutionterraingenerator.util;

import java.util.Random;

import kos.evolutionterraingenerator.util.noise.OpenSimplexNoise;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.NoiseGenerator;

public class NoiseGeneratorOpenSimplex extends NoiseGenerator
{
    /** Collection of noise generation functions.  Output is combined to produce different octaves of noise. */
    private final OpenSimplexNoise[] generatorCollection;
    private final int octaves;
    public double xCoord;
    public double yCoord;
    public double zCoord;

    public NoiseGeneratorOpenSimplex(Random seed, int octavesIn)
    {
        this.octaves = octavesIn;
        this.generatorCollection = new OpenSimplexNoise[octavesIn];
        this.xCoord = seed.nextDouble() * 256.0D;
        this.yCoord = seed.nextDouble() * 256.0D;
        this.zCoord = seed.nextDouble() * 256.0D;


        for (int i = 0; i < octavesIn; ++i)
        {
            this.generatorCollection[i] = new OpenSimplexNoise(seed.nextLong());
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

        for (int j = 0; j < this.octaves; ++j)
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
            populateNoiseArray(noiseArray, d0, d1, d2, xSize, ySize, zSize, xScale * d3, yScale * d3, zScale * d3, d3, generatorCollection[j]);
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
            	double x = xOffset + (double)i * xScale + this.xCoord;
        		for (int j = 0; j < zSize; j++)
        		{
        			double z = zOffset + (double)j * zScale + this.zCoord;

        			noiseArray[c] += noise.eval(x, z) * (1.0 / noiseScale);
        			c++;
        		}
        	}
    	}
    	else
    	{
        	int c = 0;
        	for (int i = 0; i < xSize; i++)
        	{
            	double x = xOffset + (double)i * xScale + this.xCoord;
        		for (int j = 0; j < zSize; j++)
        		{
            		double z = zOffset + (double)j * zScale + this.zCoord;
        			for (int k = 0; k < ySize; k++)
        			{
            			double y = yOffset + (double)k * yScale + this.yCoord;
            			noiseArray[c] += noise.eval(x, y, z) * (1.0 / noiseScale);
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
}
