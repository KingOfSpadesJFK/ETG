package kos.evolutionterraingenerator.world;

import kos.evolutionterraingenerator.world.biome.EvoBiomeProvider;
import kos.evolutionterraingenerator.world.gen.EvoChunkGenerator;
import kos.evolutionterraingenerator.world.gen.EvoGenSettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

@Environment(EnvType.CLIENT)
public class EvoGeneratorType extends GeneratorType 
{
	public static final EvoGeneratorType INSTANCE = new EvoGeneratorType();
	
    private EvoGeneratorType() {
    	super("evolution");
    }
    
    public static void register() {
    	GeneratorType.VALUES.add(INSTANCE);
    }

	@Override
	protected ChunkGenerator getChunkGenerator(Registry<Biome> biomeRegistry,
			Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry, long seed) {
        return new EvoChunkGenerator(new EvoBiomeProvider(seed, biomeRegistry), seed, () -> {
            return chunkGeneratorSettingsRegistry.getOrThrow(EvoGenSettings.SETTINGS);
         });
	}
}
