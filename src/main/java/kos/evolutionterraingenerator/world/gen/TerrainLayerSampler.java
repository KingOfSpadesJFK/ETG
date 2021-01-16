package kos.evolutionterraingenerator.world.gen;

import java.util.function.LongFunction;

import kos.evolutionterraingenerator.world.gen.layer.SimpleLayer;
import net.minecraft.world.biome.layer.ContinentLayer;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.SmoothLayer;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.layer.util.NorthWestCoordinateTransformer;

public class TerrainLayerSampler
{
	private final CachingLayerSampler sampler;

	public TerrainLayerSampler(LayerFactory<CachingLayerSampler> layerFactory) {
		this.sampler = (CachingLayerSampler)layerFactory.make();
	}
	
	public int sample(int x, int z)	{
		int i = this.sampler.sample(x, z);
		return i;
	}
	
	public final static class TerrainLayerBuilder
	{
		public static TerrainLayerSampler build(long seed, int terrainSize)
		{
			LayerFactory<CachingLayerSampler> layerFactory = TerrainLayerBuilder.build(terrainSize, (salt) -> {
				return new CachingLayerContext(25, seed, salt);
			});
			return new TerrainLayerSampler(layerFactory);
		}
		
		private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(int terrainSize, LongFunction<C> contextProvider)
		{
			LayerFactory<T> layerFactory = ContinentLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(1L));
			layerFactory = (new SimpleLayer(3, 0, true)).create((LayerSampleContext<T>)contextProvider.apply(2000L), layerFactory);
			layerFactory = ScaleLayer.FUZZY.create((LayerSampleContext<T>)contextProvider.apply(2000L), layerFactory);
			layerFactory = ScaleLayer.NORMAL.create((LayerSampleContext<T>)contextProvider.apply(2001L), layerFactory);
			layerFactory = ScaleLayer.NORMAL.create((LayerSampleContext<T>)contextProvider.apply(2002L), layerFactory);
			layerFactory = ScaleLayer.NORMAL.create((LayerSampleContext<T>)contextProvider.apply(2003L), layerFactory);
			LayerFactory<T> layerFactory2 = (new SimpleLayer(PLATEAU_STEPPE_COUNT + 1, 0, true)).create((LayerSampleContext<T>)contextProvider.apply(2000L), layerFactory);
			layerFactory2 = stack(2001L, ScaleLayer.NORMAL, layerFactory2, 6, contextProvider);
			LayerFactory<T> layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory, 0, contextProvider); 
			layerFactory3 = (new SimpleLayer(299999 + 2, 2, true)).create((LayerSampleContext<T>)contextProvider.apply(2000L), layerFactory);
			layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, 2, contextProvider);
			layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, 6, contextProvider);
			layerFactory3 = EvoNoiseToRiverLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(1L), layerFactory3);
			layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, 4, contextProvider);
			layerFactory3 = SmoothLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(1000L), layerFactory3);
			
			for (int i = 0; i < terrainSize; i++)
				layerFactory = ScaleLayer.NORMAL.create((LayerSampleContext<T>)contextProvider.apply(1000L + (long)i), layerFactory);

			layerFactory2 = AddPlateauLayers.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(1000L), layerFactory2, layerFactory);
			layerFactory2 = SmoothLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(1000L), layerFactory2);
			layerFactory2 = EvoAddRiverLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(1000L), layerFactory2, layerFactory3);
			return layerFactory2;
		}

		private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(long seed, ParentedLayer layer, LayerFactory<T> parent, int count, LongFunction<C> contextProvider) 
		{
			LayerFactory<T> layerFactory = parent;
			for(int i = 0; i < count; ++i) {
				layerFactory = layer.create((LayerSampleContext<T>)contextProvider.apply(seed + (long)i), layerFactory);
			}
			return layerFactory;
		}
	}
	
	public static final int PLAINS_LAYER = 0;
	public static final int MOUNTAINS_LAYER = 1;
	public static final int RIVER_LAYER = 2;
	public static final int PLATEAU_LAYER = 3;
	public static final int PLATEAU_STEPPE_COUNT = 6;
	
	public enum AddPlateauLayers implements MergingLayer, NorthWestCoordinateTransformer
	{
		INSTANCE;

		@Override
		public int transformX(int x) {
			return x;
		}

		@Override
		public int transformZ(int y) {
			return y;
		}

		@Override
		public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
			int i = sampler2.sample(this.transformX(x), this.transformZ(z));
			if (i >= 2)
				return PLATEAU_LAYER + sampler1.sample(this.transformX(x), this.transformZ(z));
			return i;
		}
	}

	public enum EvoNoiseToRiverLayer implements CrossSamplingLayer {
		INSTANCE;
		
		@Override
		public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
			int i = isValidForRiver(center);
			return i == isValidForRiver(w) && i == isValidForRiver(n) && i == isValidForRiver(e) && i == isValidForRiver(s) ? -1 : RIVER_LAYER;
		}
		
		private static int isValidForRiver(int value) {
			return value >= 2 ? 2 + (value & 1) : value;
		}
	}

	public enum OverwriteLayers implements MergingLayer, IdentityCoordinateTransformer {
		INSTANCE;

		@Override
		public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
			int i = sampler2.sample(this.transformX(x), this.transformZ(z));
			if (i != -1)
				return i;
			return sampler1.sample(this.transformX(x), this.transformZ(z));
		}
	}
	
	public enum EvoAddRiverLayer implements MergingLayer, IdentityCoordinateTransformer
	{
		INSTANCE;

		@Override
		public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z)
		{
			int i = sampler1.sample(this.transformX(x), this.transformZ(z));
			int j = sampler2.sample(this.transformX(x), this.transformZ(z));
			if (j == RIVER_LAYER) {
				return RIVER_LAYER;
			} else {
				return i;
			}
		}
	}
}
