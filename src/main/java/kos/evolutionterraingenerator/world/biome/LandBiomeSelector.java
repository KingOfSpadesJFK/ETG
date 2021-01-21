package kos.evolutionterraingenerator.world.biome;

import java.util.Random;

import kos.evolutionterraingenerator.core.EvolutionTerrainGenerator;
import kos.evolutionterraingenerator.util.KdTree;
import kos.evolutionterraingenerator.world.biome.container.BiomeContainer;
import kos.evolutionterraingenerator.world.biome.container.DefaultBiomeContainers;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

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
	
	public static LandBiomeSelector createWithAllBiomesInRegistry(Registry<Biome> registry)
	{
		LandBiomeSelector bs = new LandBiomeSelector();
		Random random = new Random(1000L);
		double minTemp = Double.MAX_VALUE;
		double minHumid = Double.MAX_VALUE;
		double maxTemp = -Double.MAX_VALUE;
		double maxHumid = -Double.MAX_VALUE;
		for(Biome b : registry)
		{
			Biome.Category category = b.getCategory();
			if (category == Biome.Category.NETHER ||
					category == Biome.Category.THEEND ||
					category == Biome.Category.BEACH ||
					category == Biome.Category.OCEAN ||
					category == Biome.Category.SWAMP ||
					category == Biome.Category.RIVER ||
					b.equals(registry.get(BiomeKeys.THE_VOID)))
				continue;
			double temp = MathHelper.clamp(b.getTemperature() + (random.nextDouble() * 0.1), 0.0, 1.0);
			double humid = MathHelper.clamp(b.getDownfall() + (random.nextDouble() * 0.1), 0.0, 1.0);
			Identifier id = registry.getKey(b).get().getValue();
			bs.add(new BiomeContainer(id, temp, humid, 0.0));
			
			if (temp > maxTemp)
				maxTemp = temp;
			if (temp < minTemp)
				minTemp = temp;
			if (humid > maxHumid)
				maxHumid = humid;
			if (humid < minHumid)
				minHumid = humid;
		}
		
		EvolutionTerrainGenerator.logger.debug("BiomeSelector created!");
		EvolutionTerrainGenerator.logger.debug("Temperature range: ["+minTemp+", "+maxTemp+"]");
		EvolutionTerrainGenerator.logger.debug("Humidity range: ["+minHumid+", "+maxHumid+"]");
		return bs;
	}
	
	public static LandBiomeSelector createDefaultSelector() {
		LandBiomeSelector bs = new LandBiomeSelector();
		DefaultBiomeContainers.createContainers();
		for (BiomeContainer bc : DefaultBiomeContainers.containers)
			bs.add(bc);
		return bs;
	}
	
	public static LandBiomeSelector createSwampSelector() {
		LandBiomeSelector bs = new LandBiomeSelector();
		BiomeContainer swamp = new BiomeContainer(BiomeList.SWAMP, 0.5, 0.5, 0.0);
		bs.add(swamp);
		bs.temperateBiomes.insert(0.0, 0.0, 0.0, null);
		bs.temperateBiomes.insert(1.0, 0.0, 0.0, null);
		return bs;
	}
}
