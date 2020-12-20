package kos.evolutionterraingenerator.world.biome;

import net.minecraft.util.IObjectIntIterable;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;

public class EvoBiomeContainer extends BiomeContainer
{
	private static final int WIDTH_BITS = (int)Math.round(Math.log(16.0D) / Math.log(2.0D)) - 2;
	private final Biome[] biomes;

	public EvoBiomeContainer(IObjectIntIterable<Biome> biomeRegistry, Biome[] biomesIn) 
	{
		   super(biomeRegistry, biomesIn);
		   this.biomes = biomesIn;
	}
	
	private EvoBiomeContainer(IObjectIntIterable<Biome> biomeRegistry) 
	{
		this(biomeRegistry, new Biome[BIOMES_SIZE]);
	}
	   
	public EvoBiomeContainer(IObjectIntIterable<Biome> biomeRegistry, IChunk chunkIn, EvoBiomeProvider biomeProviderIn) 
	{
		this(biomeRegistry);
		int x = chunkIn.getPos().getXStart() >> 2;
		int z = chunkIn.getPos().getZStart() >> 2;

		for(int k = 0; k < this.biomes.length; ++k) 
		{
			int l = k & HORIZONTAL_MASK;
	        int i1 = k >> WIDTH_BITS + WIDTH_BITS & VERTICAL_MASK;
	        int j1 = k >> WIDTH_BITS & HORIZONTAL_MASK;
			int y = chunkIn.getTopBlockY(Heightmap.Type.OCEAN_FLOOR_WG, (x + l) * 4, (z + j1) * 4) >> 2;
	        this.biomes[k] = biomeProviderIn.getNoiseBiome(x + l << 2, y << 2, z + j1 << 2, true);
	    }
	}
}
