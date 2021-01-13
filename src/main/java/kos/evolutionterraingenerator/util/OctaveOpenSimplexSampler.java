package kos.evolutionterraingenerator.util;

import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import kos.evolutionterraingenerator.util.noise.OpenSimplexNoiseSampler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.gen.ChunkRandom;

public class OctaveOpenSimplexSampler implements NoiseSampler
{
	/** Collection of noise generation functions.  Output is combined to produce different octaves of noise. */
	private final OpenSimplexNoiseSampler[] octavesOpenSimplex;

	public OctaveOpenSimplexSampler(ChunkRandom seed, int octavesIn)
	{
		this(seed, IntStream.rangeClosed(-octavesIn + 1, 0));
	}
	
	private final DoubleList field_26445;

	public OctaveOpenSimplexSampler(ChunkRandom random, IntStream octaves) {
		this(random, (List<Integer>)octaves.boxed().collect(ImmutableList.toImmutableList()));
	}

	public OctaveOpenSimplexSampler(ChunkRandom random, List<Integer> octaves) {
		this(random, (IntSortedSet)(new IntRBTreeSet(octaves)));
	}

	public static OctavePerlinNoiseSampler method_30847(ChunkRandom chunkRandom, int i, DoubleList doubleList) {
		return new OctavePerlinNoiseSampler(chunkRandom, Pair.of(i, doubleList));
	}

	private static Pair<Integer, DoubleList> method_30848(IntSortedSet intSortedSet) {
		if (intSortedSet.isEmpty()) {
			throw new IllegalArgumentException("Need some octaves!");
		} else {
			int i = -intSortedSet.firstInt();
			int j = intSortedSet.lastInt();
			int k = i + j + 1;
			if (k < 1) {
				throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
			} else {
				DoubleList doubleList = new DoubleArrayList(new double[k]);
				IntBidirectionalIterator intBidirectionalIterator = intSortedSet.iterator();

				while(intBidirectionalIterator.hasNext()) {
					int l = intBidirectionalIterator.nextInt();
					doubleList.set(l + i, 1.0D);
				}

				return Pair.of(-i, doubleList);
			}
		}
	}

	private OctaveOpenSimplexSampler(ChunkRandom random, IntSortedSet octaves) {
		this(random, method_30848(octaves));
	}

	private OctaveOpenSimplexSampler(ChunkRandom chunkRandom, Pair<Integer, DoubleList> pair) {
		int i = (Integer)pair.getFirst();
		this.field_26445 = (DoubleList)pair.getSecond();
		OpenSimplexNoiseSampler perlinNoiseSampler = new OpenSimplexNoiseSampler(chunkRandom);
		int j = this.field_26445.size();
		int k = -i;
		this.octavesOpenSimplex = new OpenSimplexNoiseSampler[j];
		if (k >= 0 && k < j) {
			double d = this.field_26445.getDouble(k);
			if (d != 0.0D) {
				this.octavesOpenSimplex[k] = perlinNoiseSampler;
			}
		}

		for(int l = k - 1; l >= 0; --l) {
			if (l < j) {
				double e = this.field_26445.getDouble(l);
				if (e != 0.0D) {
					this.octavesOpenSimplex[l] = new OpenSimplexNoiseSampler(chunkRandom);
				} else {
					chunkRandom.consume(262);
				}
			} else {
				chunkRandom.consume(262);
			}
		}

		if (k < j - 1) {
			long m = (long)(perlinNoiseSampler.sample(0.0D, 0.0D, 0.0D, 0.0D, 0.0D) * 9.223372036854776E18D);
			ChunkRandom chunkRandom2 = new ChunkRandom(m);

			for(int n = k + 1; n < j; ++n) {
				if (n >= 0) {
					double f = this.field_26445.getDouble(n);
					if (f != 0.0D) {
						this.octavesOpenSimplex[n] = new OpenSimplexNoiseSampler(chunkRandom2);
					} else {
						chunkRandom2.consume(262);
					}
				} else {
					chunkRandom2.consume(262);
				}
			}
		}

		//this.field_20660 = Math.pow(2.0D, (double)(-k));
		//this.field_20659 = Math.pow(2.0D, (double)(j - 1)) / (Math.pow(2.0D, (double)j) - 1.0D);
	}

	@Nullable
	public OpenSimplexNoiseSampler getOctave(int octave) {
	   return this.octavesOpenSimplex[octave];
	}

	public double sample(double x, double y, double z, double d, double e, boolean bl) 
	{
		double f = 0.0D;
		double h = 1.0D;

		for(int i = 0; i < this.octavesOpenSimplex.length; ++i) 
		{
			OpenSimplexNoiseSampler noise = this.octavesOpenSimplex[i];
			double x1 = maintainPrecision(x * h);
			double y1 = maintainPrecision(y * h);
			double z1 = maintainPrecision(z * h);
			
			f += this.field_26445.getDouble(i) * noise.sample(x1, bl ? -noise.originX : y1, z1, d * h, e * h) / h;
			h /= 2.0D;
		}

		return f;
	}

	public double sample(double x, double y, double d, double e, boolean bl) 
	{
		double f = 0.0D;
		double h = 1.0D;

		for(int i = 0; i < this.octavesOpenSimplex.length; ++i) 
		{
			OpenSimplexNoiseSampler noise = this.octavesOpenSimplex[i];
			double x1 = maintainPrecision(x * h);
			double y1 = maintainPrecision(y * h);
			
			f += this.field_26445.getDouble(i) * noise.sample(x1, bl ? -noise.originX : y1, d * h, e * h) / h;
			h /= 2.0D;
		}

		return f;
	}

	public double sample(double x, double y, double z) {
		return sample(x, y, z, 0.0, 0.0, false);
	}

	public double sample(double x, double z) {
		return sample(x, z, 0.0, 0.0, false);
	}

	@Override
	public double sample(double x, double y, double d, double e) {
		return sample(x, y, d, e, false);
	}

	public static double maintainPrecision(double d) {
		return d - (double)MathHelper.lfloor(d / 3.3554432E7D + 0.5D) * 3.3554432E7D;
	}
}
