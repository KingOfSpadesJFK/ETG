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

public class BiomeSelector
{
	private KdTree<BiomeContainer> tree;
	
	public BiomeSelector() {
		this.tree = new KdTree<BiomeContainer>();
	}
	
	public void addAllBiomesInRegistry(Registry<Biome> registry)
	{
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
			this.add(new BiomeContainer(id, temp, humid, 0.0));
			
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
	}
	
	public void addDefaultBiomes()
	{
		DefaultBiomeContainers.createContainers();
		for (BiomeContainer bc : DefaultBiomeContainers.containers)
			add(bc);
	}
	
	private void add(BiomeContainer bc) {
		tree.insert(bc.temperature, bc.humidity, bc.weirdness, bc);
	}
	
	public BiomeContainer pick(double temp, double humid, double weird) {
		return tree.nearest(temp, humid, weird);
	}
}
