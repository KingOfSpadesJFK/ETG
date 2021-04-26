package kos.evolutionterraingenerator.util;

import java.util.Arrays;
import java.util.NoSuchElementException;

import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.util.math.BlockPos;

public class PositionalCache <V extends Object>
{
	private final static int CACHE_SIZE = 0x2000;
	private final static int MASK = 0x1FFF;
	
	private final long[] keys;
	private final Object[] values;
	
	public PositionalCache()
	{
		this.keys = new long[CACHE_SIZE];
		Arrays.fill(keys, Integer.MIN_VALUE);
		this.values = new Object[CACHE_SIZE];
	}
	
	public void add(BlockPos pos, V value)
	{
		long key = pos.asLong();
		int i = (int)HashCommon.mix(key) & MASK;
		
		values[i] = value;
		keys[i] = key;
	}
	
	public boolean containsKey(BlockPos pos) {
		long key = pos.asLong();
		int i = (int)HashCommon.mix(key) & MASK;
		
		return keys[i] == key;
	}
	
	@SuppressWarnings("unchecked")
	public V getOrThrow(BlockPos pos) {
		long key = pos.asLong();
		int i = (int)HashCommon.mix(key) & MASK;
		
		if (keys[i] == key) {
			return (V)values[i];
		}
		throw new NoSuchElementException();
	}

	public void add(int x, int y, int z, V value) {
		add(new BlockPos(x, y, z), value);
	}

	public void add(int x, int y, V value) {
		add(new BlockPos(x, y, 0), value);
	}

	public boolean containsKey(int x, int y, int z) {
		return containsKey(new BlockPos(x, y, z));
	}

	public boolean containsKey(int x, int y) {
		return containsKey(new BlockPos(x, y, 0));
	}

	public V getOrThrow(int x, int y, int z) {
		return getOrThrow(new BlockPos(x, y, z));
	}

	public V getOrThrow(int x, int y) {
		return getOrThrow(new BlockPos(x, y, 0));
	}
}
