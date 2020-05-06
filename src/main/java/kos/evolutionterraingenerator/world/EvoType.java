package kos.evolutionterraingenerator.world;

import kos.evolutionterraingenerator.client.CreateETGWorldScreen;
import kos.evolutionterraingenerator.world.biome.support.BOPSupport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.api.distmarker.Dist;

public class EvoType extends WorldType
{
	public EvoType(String name)
	{
		super(name);
	}
	
	public static final String USE_BOP_TAG = "useBOPBiomes";
	
	@Override
	@SuppressWarnings("deprecation")
	public ChunkGenerator<?> createChunkGenerator(World world)
    {
		CompoundNBT nbt = world.getWorldInfo().getGeneratorOptions();
		boolean bopLoaded = ModList.get().isLoaded(BOPSupport.BOP_MODID);
		if (!bopLoaded)
			nbt.putBoolean(USE_BOP_TAG, false);
		EvoGenSettings settings = new EvoGenSettings(nbt);
		if (world.dimension.getType() == DimensionType.OVERWORLD)
		{
			EvoBiomeProviderSettings that = new EvoBiomeProviderSettings();
			that.setWorldInfo(world.getWorldInfo());
			that.setGeneratorSettings(settings);
			that.setup();
			return new EvoChunkGenerator(world, new EvoBiomeProvider(that), settings);
		}
        return world.dimension.createChunkGenerator();
    }

	@Override
	public boolean hasCustomOptions()
	{
		return true;
	}

	@Override
	public boolean hasInfoNotice()
	{
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onCustomizeButton(Minecraft mc, CreateWorldScreen gui)
	{
		mc.displayGuiScreen(new CreateETGWorldScreen(gui, gui.chunkProviderSettingsJson));
	}
}
