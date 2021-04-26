package kos.evolutionterraingenerator.world.biome.selector;

import kos.evolutionterraingenerator.util.KdTree;
import kos.evolutionterraingenerator.world.biome.container.BiomeContainer;

public class LandBiomeSelector implements BiomeSelector
{
	private KdTree<BiomeContainer> snowyBiomes;
	private KdTree<BiomeContainer> coldBiomes;
	private KdTree<BiomeContainer> temperateBiomes;
	private KdTree<BiomeContainer> warmBiomes;
	private KdTree<BiomeContainer> hotBiomes;
    
	public static final double SNOW_TEMP = 0.125;
	public static final double COLD_TEMP = 0.375;
	public static final double WARM_TEMP = 0.625;
	public static final double HOT_TEMP = 0.875;
	
	public LandBiomeSelector() {
		this.snowyBiomes = new KdTree<BiomeContainer>();
		this.coldBiomes = new KdTree<BiomeContainer>();
		this.temperateBiomes = new KdTree<BiomeContainer>();
		this.warmBiomes = new KdTree<BiomeContainer>();
		this.hotBiomes = new KdTree<BiomeContainer>();
	}
	
	public void add(BiomeContainer bc) {
		double temp = bc.getTemperature();
		double humid = bc.getHumidity();
		double weird = bc.getWeirdness();
		double weirdRange = bc.getWeirdness();
		if (temp < SNOW_TEMP)
			snowyBiomes.insert(humid, weird, weirdRange, bc);
		else if (temp < COLD_TEMP)
			coldBiomes.insert(humid, weird, weirdRange, bc);
		else if (temp < WARM_TEMP)
			temperateBiomes.insert(humid, weird, weirdRange, bc);
		else if (temp < HOT_TEMP)
			warmBiomes.insert(humid, weird, weirdRange, bc);
		else
			hotBiomes.insert(humid, weird, weirdRange, bc);
	}
	
	public BiomeContainer pick(double temp, double humid, double weird) {
		if (temp < SNOW_TEMP)
			return snowyBiomes.nearest(humid, weird);
		else if (temp < COLD_TEMP)
			return coldBiomes.nearest(humid, weird);
		else if (temp < WARM_TEMP)
			return temperateBiomes.nearest(humid, weird);
		else if (temp < HOT_TEMP)
			return warmBiomes.nearest(humid, weird);
		else
			return hotBiomes.nearest(humid, weird);
	}
}
