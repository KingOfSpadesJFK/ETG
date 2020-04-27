package kos.evolutionterraingenerator.util;

import java.util.Random;

import kos.evolutionterraingenerator.util.noise.OpenSimplexNoise;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.INoiseGenerator;
import net.minecraft.world.gen.ImprovedNoiseGenerator;
import net.minecraft.world.gen.OctavesNoiseGenerator;

public class NoiseGeneratorOpenSimplex extends OctavesNoiseGenerator
{
    /** Collection of noise generation functions.  Output is combined to produce different octaves of noise. */
    private final OpenSimplexNoise[] generatorCollection;
    private final Point3D[] vertexCollection;
    private final int octaves;

    public NoiseGeneratorOpenSimplex(Random seed, int octavesIn)
    {
    	super(seed, octavesIn);
        this.octaves = octavesIn;
        this.generatorCollection = new OpenSimplexNoise[octavesIn];
        this.vertexCollection = new Point3D[octavesIn];

        for (int i = 0; i < octavesIn; ++i)
        {
            this.generatorCollection[i] = new OpenSimplexNoise(seed.nextLong());
            this.vertexCollection[i] = new Point3D();
            this.vertexCollection[i].x = seed.nextDouble() * 256.0D;
            this.vertexCollection[i].y = seed.nextDouble() * 256.0D;
            this.vertexCollection[i].z = seed.nextDouble() * 256.0D;
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
            populateNoiseArray(noiseArray, d0, d1, d2, xSize, ySize, zSize, xScale * d3, yScale * d3, zScale * d3, d3, generatorCollection[j], vertexCollection[j]);
            d3 /= 2.0D;
        }

        return noiseArray;
    }

    private void populateNoiseArray(double[] noiseArray, double xOffset, double yOffset, double zOffset, int xSize, int ySize,
			int zSize, double xScale, double yScale, double zScale, double noiseScale, OpenSimplexNoise noise, Point3D vertex)
    {
    	if (ySize == 1)
    	{
        	int c = 0;
        	for (int i = 0; i < xSize; i++)
        	{
            	double x = xOffset + (double)i * xScale + vertex.x;
        		for (int j = 0; j < zSize; j++)
        		{
        			double z = zOffset + (double)j * zScale + vertex.z;

        			noiseArray[c] += noise.eval(x, z) / noiseScale;
        			c++;
        		}
        	}
    	}
    	else
    	{
        	int c = 0;
        	for (int i = 0; i < xSize; i++)
        	{
            	double x = xOffset + (double)i * xScale + vertex.x;
        		for (int j = 0; j < zSize; j++)
        		{
            		double z = zOffset + (double)j * zScale + vertex.z;
        			for (int k = 0; k < ySize; k++)
        			{
            			double y = yOffset + (double)k * yScale + vertex.y;
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

    public OpenSimplexNoise getOpenSimplexOctave(int p_215463_1_) {
       return this.generatorCollection[p_215463_1_];
    }
    
    private class Point3D
    {
    	public double x, y, z;
    }

	public double func_215460_a(double p_215462_1_, double p_215462_3_, double p_215462_5_, double p_215462_7_, double p_215462_9_, boolean p_215462_11_) {
        double d0 = 0.0D;
        double d1 = 1.0D;

        for(int i = 0; i < octaves; i++)
        {
            d0 += this.generatorCollection[i].eval(this.generatorCollection[i].eval(maintainPrecision(p_215462_1_ * d1), p_215462_11_ ? -vertexCollection[i].y : maintainPrecision(p_215462_3_ * d1), maintainPrecision(p_215462_5_ * d1)), p_215462_7_ * d1, p_215462_9_ * d1) / d1;
            d1 /= 2.0D;
        }

        return d0;
	}
	
	public double getNoise(double x, double y, double z)
	{
        double d0 = 0.0D;
        double d1 = 1.0D;

        for(int i = 0; i < octaves; i++)
        {
            d0 += this.generatorCollection[i].eval(x * d1, y * d1, z * d1) / d1;
            d1 /= 2.0D;
        }
        return d0;
	}

	public double getNoise(double x, double z) {
		return getNoise(x, 10, z);
	}
}
