package kos.evolutionterraingenerator.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EvoType extends WorldType
{    
	public static void register()
	{
		System.out.println("Registering New World Type....");
		new EvoType();
		System.out.println("Evolution Grips is online");
	}
	
	private EvoType()
	{
		super("EVOLUTION");
	}
	
	public IChunkGenerator getChunkGenerator(World world, String generatorOptions)
	{
		System.out.println(generatorOptions);
		return new EvoChunkGenerator(world, world.getSeed(), true, generatorOptions);
	}

	protected EvoType(String name)
	{
		super(name);
	}
	
	@Override
	public BiomeProvider getBiomeProvider(World world)
	{
		System.out.println("Here's that BiomeProvider provider you ordered.");
		return new BiomeProviderEvo(world);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasInfoNotice()
	{
		return false;
	}

	@Override
	public boolean isCustomizable()
	{
		return false;
	}
}
