package kos.evolutionterraingenerator.world.biome;

import java.awt.geom.Point2D;

import kos.evolutionterraingenerator.util.KdTree;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class BiomeMap
{
	private KdTree<Biome> tree;
	
	public BiomeMap(Registry<Biome> registry)
	{
		this.tree = new KdTree<Biome>();
		for(Biome b : registry)
		{
			Biome.Category category = b.getCategory();
			if (category == Biome.Category.NETHER ||
					category == Biome.Category.THEEND ||
					category == Biome.Category.BEACH ||
					category == Biome.Category.OCEAN ||
					category == Biome.Category.SWAMP ||
					category == Biome.Category.RIVER)
				continue;
			tree.insert(new Point2D.Double(b.getTemperature(), b.getDownfall()), b);
		}
	}
	
	public Biome pick(double temp, double humid) {
		return tree.nearest(temp, humid);
	}
}
