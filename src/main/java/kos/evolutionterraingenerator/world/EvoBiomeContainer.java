package kos.evolutionterraingenerator.world;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;

public class EvoBiomeContainer extends BiomeContainer
{
	private static final int WIDTH_BITS = (int)Math.round(Math.log(16.0D) / Math.log(2.0D)) - 2;
	private final Biome[] biomes;

	public EvoBiomeContainer(Biome[] biomesIn) 
	{
		   super(biomesIn);
		   this.biomes = biomesIn;
	}
	
	private EvoBiomeContainer() 
	{
		this(new Biome[BIOMES_SIZE]);
	}
	   
	public EvoBiomeContainer(IChunk chunkIn, EvoBiomeProvider biomeProviderIn) 
	{
		this();
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
	
	//Since the biomes array in the super class is private smfh
	   public int[] getBiomeIds() {
	      int[] aint = new int[this.biomes.length];

	      for(int i = 0; i < this.biomes.length; ++i) {
	         aint[i] = Registry.BIOME.getId(this.biomes[i]);
	      }

	      return aint;
	   }

	   public void writeToBuf(PacketBuffer buf) {
	      for(Biome biome : this.biomes) {
	         buf.writeInt(Registry.BIOME.getId(biome));
	      }

	   }

	   public BiomeContainer clone() {
	      return new EvoBiomeContainer((Biome[])this.biomes.clone());
	   }

	   public Biome getNoiseBiome(int x, int y, int z) {
	      int i = x & HORIZONTAL_MASK;
	      int j = MathHelper.clamp(y, 0, VERTICAL_MASK);
	      int k = z & HORIZONTAL_MASK;
	      return this.biomes[j << WIDTH_BITS + WIDTH_BITS | k << WIDTH_BITS | i];
	   }

}
