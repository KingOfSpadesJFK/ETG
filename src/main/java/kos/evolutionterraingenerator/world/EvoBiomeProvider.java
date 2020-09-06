package kos.evolutionterraingenerator.world;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import com.mojang.serialization.Codec;

import java.util.List;

public class EvoBiomeProvider extends BiomeSource
{
	protected EvoBiomeProvider(List<Biome> biomes) {
		super(biomes);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BiomeSource withSeed(long arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
