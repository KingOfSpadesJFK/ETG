package kos.evolutionterraingenerator.world.biome.selector;

import java.util.Random;

import kos.evolutionterraingenerator.world.biome.container.BiomeContainer;
import kos.evolutionterraingenerator.world.biome.container.DefaultBiomeContainers;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public interface BiomeSelector 
{
	public void add(BiomeContainer bc);
	public BiomeContainer pick(double temperature, double humidity, double weirdness);
	
	public static BiomeSelector createSelector(BiomeContainer.Category category) {
		BiomeSelector bs;
		if (category == BiomeContainer.Category.SWAMP) {
			bs = new SwampBiomeSelector();
		} else if (category == BiomeContainer.Category.OCEAN) {
			bs = new OceanBiomeSelector();
		} else {
			bs = new LandBiomeSelector();
		}
		
		if (DefaultBiomeContainers.isEmpty())
			DefaultBiomeContainers.createContainers();
		for (BiomeContainer bc : DefaultBiomeContainers.containers) {
			if (bc.getCategory() == category)
				bs.add(bc);
		}
		return bs;
	}
	
	public static BiomeSelector createLandSelector() {
		return createSelector(BiomeContainer.Category.LAND);
	}
	
	public static BiomeSelector createIslandSelector() {
		return createSelector(BiomeContainer.Category.ISLAND);
	}
	
	public static BiomeSelector createSwampSelector() {
		return createSelector(BiomeContainer.Category.SWAMP);
	}
	
	public static BiomeSelector createOceanSelector() {
		return createSelector(BiomeContainer.Category.OCEAN);
	}
	
	public static LandBiomeSelector createWithAllBiomesInRegistry(Registry<Biome> registry)
	{
		LandBiomeSelector bs = new LandBiomeSelector();
		Random random = new Random(1000L);
		for(Biome b : registry)
		{
			Biome.Category category = b.getCategory();
			if (category == Biome.Category.NETHER ||
					category == Biome.Category.THEEND ||
					category == Biome.Category.BEACH ||
					category == Biome.Category.OCEAN ||
					category == Biome.Category.RIVER ||
					b.equals(registry.get(BiomeKeys.THE_VOID)))
				continue;
			double temp = MathHelper.clamp(b.getTemperature() + (random.nextDouble() * 0.1), 0.0, 1.0);
			double humid = MathHelper.clamp(b.getDownfall() + (random.nextDouble() * 0.1), 0.0, 1.0);
			Identifier id = registry.getKey(b).get().getValue();
			bs.add(new BiomeContainer(id, temp, humid, 0.0));
		}
		return bs;
	}
}
