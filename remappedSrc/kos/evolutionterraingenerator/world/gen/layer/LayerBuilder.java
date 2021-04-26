package kos.evolutionterraingenerator.world.gen.layer;

import java.util.function.LongFunction;

import net.minecraft.world.biome.layer.ContinentLayer;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.SmoothenShorelineLayer;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.layer.util.NorthWestCoordinateTransformer;

public enum LayerBuilder {

	TERRAIN_TYPE, 
	PLATEAU_STEPS,
	CLIMATE,
	SWAMP;
	
	public static TerrainLayerSampler build(long seed,LayerBuilder type, int... info)
	{
		switch (type)
		{
			case TERRAIN_TYPE:
				return new TerrainLayerSampler(LayerBuilder.build(info[0], info[1], (salt) -> {
					return new CachingLayerContext(25, seed, salt);
					}));
			
			case PLATEAU_STEPS:
				return new TerrainLayerSampler(LayerBuilder.buildPlateau(info[0], (salt) -> {
					return new CachingLayerContext(25, seed, salt);
					}));
				
			case CLIMATE:
				return new ClimateLayerSampler(info[1], LayerBuilder.buildClimate(info[0], info[1], (salt) -> {
					return new CachingLayerContext(25, seed, salt);
					}));
				
			case SWAMP:
				return new TerrainLayerSampler(LayerBuilder.buildSwamp( (salt) -> {
					return new CachingLayerContext(25, seed, salt);
					}));
				
			default: throw new IllegalArgumentException();
			
		}
	}
	
	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> buildPlateau(int stepCount, LongFunction<C> contextProvider)
	{
		LayerFactory<T> plateauLayer = (new SimpleLayer(stepCount, 0, true)).create((LayerSampleContext<T>)contextProvider.apply(1L));
		plateauLayer = stack(2001L, ScaleLayer.NORMAL, plateauLayer, 7, contextProvider);
		plateauLayer = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(1000L), plateauLayer);
		return plateauLayer;
	}
	
	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> buildClimate(int size, int percision, LongFunction<C> contextProvider)
	{
		LayerFactory<T> climateLayer = (new SimpleLayer((int)(Math.pow(10.0, percision)), 0, true)).create((LayerSampleContext<T>)contextProvider.apply(1L));
		for (int i = 0; i < 2; i++) {
			climateLayer = GradLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(i + 10L), climateLayer);
			climateLayer = ScaleLayer.NORMAL.create((LayerSampleContext<T>)contextProvider.apply(i + 2000L), climateLayer);
		}
		climateLayer = stack(1000L, ScaleLayer.NORMAL, climateLayer, size, contextProvider);
		climateLayer = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(1000L), climateLayer);
		return climateLayer;
	}
	
	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> buildSwamp(LongFunction<C> contextProvider)
	{
		LayerFactory<T> swampLayer = ContinentLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(1L));
		swampLayer = PlateauLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(2L), swampLayer);
		swampLayer = ScaleLayer.FUZZY.create((LayerSampleContext<T>)contextProvider.apply(2000L), swampLayer);
		swampLayer = stack(2001L, ScaleLayer.NORMAL, swampLayer, 7, contextProvider);
		return swampLayer;
	}
	
	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(int terrainSize, int riverSize, LongFunction<C> contextProvider)
	{
		LayerFactory<T> terrainLayer = ContinentLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(1L));
		terrainLayer = PlateauLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(2L), terrainLayer);
		terrainLayer = ScaleLayer.FUZZY.create((LayerSampleContext<T>)contextProvider.apply(2000L), terrainLayer);
		terrainLayer = ScaleLayer.NORMAL.create((LayerSampleContext<T>)contextProvider.apply(2001L), terrainLayer);
		terrainLayer = ScaleLayer.NORMAL.create((LayerSampleContext<T>)contextProvider.apply(2002L), terrainLayer);
		terrainLayer = ScaleLayer.NORMAL.create((LayerSampleContext<T>)contextProvider.apply(2003L), terrainLayer);
		LayerFactory<T> riverLayer = (new SimpleParentedLayer(299999 + 2, 2, true)).create((LayerSampleContext<T>)contextProvider.apply(2000L), terrainLayer);
		riverLayer = stack(1000L, ScaleLayer.NORMAL, riverLayer, 2, contextProvider);
		riverLayer = stack(1000L, ScaleLayer.NORMAL, riverLayer, 6, contextProvider);
		riverLayer = EvoNoiseToRiverLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(1L), riverLayer);
		riverLayer = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(1000L), riverLayer);
		riverLayer = stack(1000L, ScaleLayer.NORMAL, riverLayer, riverSize, contextProvider);
		
		for (int i = 0; i < terrainSize; i++)
			terrainLayer = ScaleLayer.NORMAL.create((LayerSampleContext<T>)contextProvider.apply(1000L + (long)i), terrainLayer);
		
		terrainLayer = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(1000L), terrainLayer);
		terrainLayer = EvoAddRiverLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(1000L), terrainLayer, riverLayer);
		return terrainLayer;
	}

	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(long seed, ParentedLayer layer, LayerFactory<T> parent, int count, LongFunction<C> contextProvider) 
	{
		LayerFactory<T> layerFactory = parent;
		for(int i = 0; i < count; ++i) {
			layerFactory = layer.create((LayerSampleContext<T>)contextProvider.apply(seed + (long)i), layerFactory);
		}
		return layerFactory;
	}
	
	public enum PlateauLayer implements ParentedLayer, IdentityCoordinateTransformer
	{
		INSTANCE;

		@Override
		public int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) 
		{
			int i = parent.sample(x, z);
			int j = context.nextInt(10);
			return i == TerrainLayerSampler.MOUNTAINS_LAYER && j == 0 ? TerrainLayerSampler.PLATEAU_LAYER : i;
		}
	}
	
	public enum GradLayer implements ParentedLayer, NorthWestCoordinateTransformer {
		INSTANCE;

		@Override
		public int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
			int nw = parent.sample(x - 1, z - 1);
			int ne = parent.sample(x + 1, z - 1);
			int sw = parent.sample(x - 1, z + 1);
			int se = parent.sample(x + 1, z + 1);
			return (nw + ne + sw + se) / 4;
		}
		
		
	}

	public enum EvoNoiseToRiverLayer implements CrossSamplingLayer {
		INSTANCE;
		
		@Override
		public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
			int i = isValidForRiver(center);
			return i == isValidForRiver(w) && i == isValidForRiver(n) && i == isValidForRiver(e) && i == isValidForRiver(s) ? -1 : TerrainLayerSampler.RIVER_LAYER;
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
			if (j == TerrainLayerSampler.RIVER_LAYER) {
				return TerrainLayerSampler.RIVER_LAYER;
			} else {
				return i;
			}
		}
	}

}
