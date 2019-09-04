package kos.evolutionterraingenerator.world.biome;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeBeach;

public class BiomeGravelBeach extends BiomeBeach
{
    public BiomeGravelBeach(Biome.BiomeProperties properties)
    {
        super(properties);
        this.topBlock = Blocks.GRAVEL.getDefaultState();
        this.fillerBlock = Blocks.GRAVEL.getDefaultState();
    }
}