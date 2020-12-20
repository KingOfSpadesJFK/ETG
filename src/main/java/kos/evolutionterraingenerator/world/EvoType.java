package kos.evolutionterraingenerator.world;

import kos.evolutionterraingenerator.world.biome.EvoBiomeProvider;
import kos.evolutionterraingenerator.world.biome.EvoBiomeProviderSettings;
import kos.evolutionterraingenerator.world.gen.EvoChunkGenerator;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.common.world.ForgeWorldType;

public class EvoType extends ForgeWorldType
{	
	public EvoType()
	{
		super(null);
	}
	
	@Override
	public ChunkGenerator createChunkGenerator(Registry<Biome> biomeRegistry, Registry<DimensionSettings> dimensionSettingsRegistry, long seed, String generatorSettings)
	{
        return new EvoChunkGenerator(new EvoBiomeProvider(new EvoBiomeProviderSettings(generatorSettings), seed, biomeRegistry), seed, () -> dimensionSettingsRegistry.getOrThrow(DimensionSettings.field_242734_c));
	}

    @Override
    public DimensionGeneratorSettings createSettings(DynamicRegistries dynamicRegistries, long seed, boolean generateStructures, boolean bonusChest, String generatorSettings)
    {
        Registry<Biome> biomeRegistry = dynamicRegistries.getRegistry(Registry.BIOME_KEY);
        Registry<DimensionType> dimensionTypeRegistry = dynamicRegistries.getRegistry(Registry.DIMENSION_TYPE_KEY);
        Registry<DimensionSettings> dimensionSettingsRegistry = dynamicRegistries.getRegistry(Registry.NOISE_SETTINGS_KEY);
        return new DimensionGeneratorSettings(seed, generateStructures, bonusChest,
                DimensionGeneratorSettings.func_242749_a(dimensionTypeRegistry,
                        DimensionType.getDefaultSimpleRegistry(dimensionTypeRegistry, biomeRegistry, dimensionSettingsRegistry, seed),
                        createChunkGenerator(biomeRegistry, dimensionSettingsRegistry, seed, generatorSettings)));
    }
}
