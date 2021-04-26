package kos.evolutionterraingenerator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.base.MoreObjects;

import kos.evolutionterraingenerator.world.biome.EvoBiomeSource;
import kos.evolutionterraingenerator.world.gen.EvoChunkGenerator;
import kos.evolutionterraingenerator.world.gen.EvoGenSettings;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

import java.util.Properties;
import java.util.Random;

@Mixin(GeneratorOptions.class)
public class MixinGeneratorOptions 
{
    @Inject(method = "fromProperties", at = @At("HEAD"), cancellable = true)
    private static void injectEvolutionType(DynamicRegistryManager dynamicRegistryManager, Properties properties, CallbackInfoReturnable<GeneratorOptions> cbt)
    {
        if (properties.get("level-type") == null) {
            return;
        }
        
        if (properties.get("level-type").toString().trim().toLowerCase().equals("evolution")) 
        {
            String seedField = (String) MoreObjects.firstNonNull(properties.get("level-seed"), "");
            long seed = new Random().nextLong();
            if (!seedField.isEmpty()) {
                try {
                    long parsedSeed = Long.parseLong(seedField);
                    if (parsedSeed != 0L)
                        seed = parsedSeed;
                } catch (NumberFormatException var14) {
                    seed = seedField.hashCode();
                }
            }

            Registry<DimensionType> dimensionReg = dynamicRegistryManager.get(Registry.DIMENSION_TYPE_KEY);
            Registry<Biome> biomeReg = dynamicRegistryManager.get(Registry.BIOME_KEY);
            Registry<ChunkGeneratorSettings> chunkGenReg = dynamicRegistryManager.get(Registry.NOISE_SETTINGS_WORLDGEN);
            SimpleRegistry<DimensionOptions> dimensionOptions = DimensionType.createDefaultDimensionOptions(dimensionReg, biomeReg, chunkGenReg, seed);

            String structuresField = (String)properties.get("generate-structures");
            boolean generateStructures = structuresField == null || Boolean.parseBoolean(structuresField);

            cbt.setReturnValue(
            		new GeneratorOptions(
            				seed, generateStructures, false, GeneratorOptions.method_28608(
            						dimensionReg, dimensionOptions, new EvoChunkGenerator(
            								new EvoBiomeSource(seed, biomeReg), seed, 
            								() -> { return chunkGenReg.getOrThrow(EvoGenSettings.SETTINGS); } 
            								)
            						)
            				)
            		);
        }
    }
}
