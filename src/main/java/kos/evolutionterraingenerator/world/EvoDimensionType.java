package kos.evolutionterraingenerator.world;

import java.util.OptionalLong;

import com.mojang.serialization.Lifecycle;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.FuzzedBiomeMagnifier;
import net.minecraft.world.biome.IBiomeMagnifier;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;

public class EvoDimensionType extends DimensionType
{

	protected EvoDimensionType(OptionalLong fixedTime, boolean hasSkyLight, boolean hasCeiling, boolean ultrawarm,
			boolean natural, double coordinateScale, boolean hasDragonFight, boolean piglinSafe, boolean bedWorks,
			boolean respawnAnchorWorks, boolean hasRaids, int logicalHeight, IBiomeMagnifier magnifier,
			ResourceLocation infiniburn, ResourceLocation effects, float ambientLight) {
		super(fixedTime, hasSkyLight, hasCeiling, ultrawarm, natural, coordinateScale, hasDragonFight, piglinSafe, bedWorks,
				respawnAnchorWorks, hasRaids, logicalHeight, magnifier, infiniburn, effects, ambientLight);

	}


	protected EvoDimensionType(OptionalLong fixedTime, boolean hasSkyLight, boolean hasCeiling, boolean ultrawarm, boolean natural, double coordinateScale, boolean piglinSafe, boolean bedWorks, boolean respawnAnchorWorks, boolean hasRaids, int logicalHeight, ResourceLocation infiniburn, ResourceLocation effects, float ambientLight) {
		this(fixedTime, hasSkyLight, hasCeiling, ultrawarm, natural, coordinateScale, false, piglinSafe, bedWorks, respawnAnchorWorks, hasRaids, logicalHeight, FuzzedBiomeMagnifier.INSTANCE, infiniburn, effects, ambientLight);
	}

	private static ChunkGenerator evoEndChunkGenerator(Registry<Biome> lookUpRegistryBiome, Registry<DimensionSettings> settingsRegistry, long seed) 
	{
		return new NoiseChunkGenerator(new EndBiomeProvider(lookUpRegistryBiome, seed), seed, () -> {
			return settingsRegistry.getOrThrow(DimensionSettings.field_242737_f);
		});
	}

	private static ChunkGenerator evoNetherChunkGenerator(Registry<Biome> lookUpRegistryBiome, Registry<DimensionSettings> lookUpRegistryDimensionType, long seed) 
	{
		return new NoiseChunkGenerator(NetherBiomeProvider.Preset.DEFAULT_NETHER_PROVIDER_PRESET.build(lookUpRegistryBiome, seed), seed, () -> {
			return lookUpRegistryDimensionType.getOrThrow(DimensionSettings.field_242736_e);
		});
	}

	public static SimpleRegistry<Dimension> getDefaultSimpleRegistry(Registry<DimensionType> lookUpRegistryDimensionType, Registry<Biome> lookUpRegistryBiome, Registry<DimensionSettings> lookUpRegistryDimensionSettings, long seed) 
	{
		SimpleRegistry<Dimension> simpleregistry = new SimpleRegistry<>(Registry.DIMENSION_KEY, Lifecycle.experimental());
		simpleregistry.register(Dimension.THE_NETHER, new Dimension(() -> {
				return lookUpRegistryDimensionType.getOrThrow(THE_NETHER);
		    }, evoNetherChunkGenerator(lookUpRegistryBiome, lookUpRegistryDimensionSettings, seed)), Lifecycle.stable());
		simpleregistry.register(Dimension.THE_END, new Dimension(() -> {
				return lookUpRegistryDimensionType.getOrThrow(THE_END);
		    }, evoEndChunkGenerator(lookUpRegistryBiome, lookUpRegistryDimensionSettings, seed)), Lifecycle.stable());
		return simpleregistry;
	}
}
