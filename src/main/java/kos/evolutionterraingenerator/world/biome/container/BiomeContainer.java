package kos.evolutionterraingenerator.world.biome.container;

import kos.evolutionterraingenerator.world.biome.BiomeList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class BiomeContainer
{
	private Identifier mainBiome;
	private Identifier primaryBeach;
	private Identifier secondaryBeach;
	private double temperature;
	private double humidity;
	private double weirdness;
	private double weirdnessRange;
	private Category category;
	private boolean useDefaultSurfaceBuilder;
	
	public BiomeContainer(Identifier mainBiome, double temperature, double humidity, double weirdness) {
		this.mainBiome = mainBiome;
		this.temperature = temperature;
		this.humidity = humidity;
		this.weirdness = weirdness;
		
		this.primaryBeach = BiomeList.BEACH;
		this.secondaryBeach = BiomeList.GRAVEL_BEACH;
		this.weirdnessRange = Double.NaN;
		this.setCategory(Category.LAND);
	}
	
	public Identifier getID() {
		return mainBiome;
	}
	
	public final Biome getBiome(Registry<Biome> registry) {
		return registry.get(getID());
	}

	public double getTemperature() {
		return temperature;
	}

	public double getWeirdness() {
		return weirdness;
	}

	public double getHumidity() {
		return humidity;
	}

	public Identifier getPrimaryBeach() {
		return primaryBeach;
	}

	public BiomeContainer setPrimaryBeach(Identifier primaryBeach) {
		this.primaryBeach = primaryBeach;
		return this;
	}

	public Identifier getSecondaryBeach() {
		return secondaryBeach;
	}

	public BiomeContainer setSecondaryBeach(Identifier secondaryBeach) {
		this.secondaryBeach = secondaryBeach;
		return this;
	}

	public double getWeirdnessRange() {
		return weirdnessRange;
	}

	public BiomeContainer setWeirdnessRange(double weirdnessRange) {
		this.weirdnessRange = weirdnessRange;
		return this;
	}
	
	public String toString() {
		return "(Main Biome: " + mainBiome.toString() + ")";
	}
	
	public Category getCategory() {
		return category;
	}

	public BiomeContainer setCategory(Category category) {
		this.category = category;
		return this;
	}

	public boolean usesDefaultSurfaceBuilder() {
		return useDefaultSurfaceBuilder;
	}

	public BiomeContainer useDefaultSurfaceBuilder(boolean useDefaultSurfaceBuilder) {
		this.useDefaultSurfaceBuilder = useDefaultSurfaceBuilder;
		return this;
	}

	public enum Category {
		LAND,
		ISLAND,
		SWAMP,
		OCEAN;
	}
}