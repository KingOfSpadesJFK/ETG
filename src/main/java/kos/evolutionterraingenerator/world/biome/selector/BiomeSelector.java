package kos.evolutionterraingenerator.world.biome.selector;

import java.util.Random;

import kos.evolutionterraingenerator.world.biome.container.BiomeContainer;
import kos.evolutionterraingenerator.world.biome.container.DefaultBiomeContainers;
import kos.evolutionterraingenerator.world.biome.container.BiomeContainer.Category;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public interface BiomeSelector 
{
	public void add(BiomeContainer bc);
	public BiomeContainer pick(double temperature, double humidity, double weirdness);
	
	public static LandBiomeSelector createLandSelector() {
		LandBiomeSelector bs = new LandBiomeSelector();
		DefaultBiomeContainers.createContainers();
		for (BiomeContainer bc : DefaultBiomeContainers.containers) {
			if (bc.getCategory() == Category.LAND)
				bs.add(bc);
		}
		return bs;
	}
	
	public static SwampBiomeSelector createSwampSelector() {
		SwampBiomeSelector bs = new SwampBiomeSelector();
		for (BiomeContainer bc : DefaultBiomeContainers.containers) {
			if (bc.getCategory() == Category.SWAMP)
				bs.add(bc);
		}
		return bs;
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
