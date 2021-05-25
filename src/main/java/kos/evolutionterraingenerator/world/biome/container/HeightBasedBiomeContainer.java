package kos.evolutionterraingenerator.world.biome.container;

import net.minecraft.util.Identifier;

public class HeightBasedBiomeContainer extends BiomeContainer
{
	private class Node {
		int key;
		Identifier value;
		Node left;
		Node right;
	}
	
	private Node root;
	private final int length;

	public HeightBasedBiomeContainer(Identifier mainBiome, double temperature, double humidity, double weirdness, Identifier[] biomes, int[] heights) {
		super(mainBiome, temperature, humidity, weirdness);
		if (biomes.length != heights.length)
			throw new IllegalArgumentException();
		else
			this.length = biomes.length;
		
		this.root = new Node();
		this.root.key = heights[0];
		this.root.value = biomes[0];
		int i = 1;
		while (i < this.length) {
			Node n = this.root;
			while (n != null) {
				if (heights[i] < n.key) {
					if (n.left == null) {
						n.left = new Node();
						n.left.key = heights[i];
						n.left.value = biomes[i];
						break;
					} else {
						n = n.left;
					}
				} else if (heights[i] > n.key) {
					if (n.right == null) {
						n.right = new Node();
						n.right.key = heights[i];
						n.right.value = biomes[i];
						break;
					} else {
						n = n.right;
					}
				} else {
					break;
				}
			}
			i++;
		}
	}
	
	public Identifier getIDByHeight(int y) {
		Node n = this.root;
		
		while (true) {
			if (n.key == y || (n.left == null && n.right == null))
				return n.value;
			
			if (n.right == null) {
				if (y >= n.left.key)
					return n.left.value;
				n = n.left;
				continue;
			}
			
			if (n.left == null) {
				if (y <= n.right.key) {
					return n.value;
				}
				n = n.right;
				continue;
			}
			
			if (n.left != null && n.right != null) {
				if (y >= n.left.key && y <= n.right.key) {
					return n.left.value;
				}
				if (y <= n.left.key) {
					n = n.left;
				}
				if (y >= n.right.key) {
					n = n.right;
				}
			}
		}
	}
}
