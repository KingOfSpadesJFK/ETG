package kos.evolutionterraingenerator.client;

import kos.evolutionterraingenerator.world.EvoType;
import kos.evolutionterraingenerator.world.biome.support.BOPSupport;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;

@OnlyIn(Dist.CLIENT)
public class CreateETGWorldScreen extends Screen
{
	private CreateWorldScreen parent;
	private CompoundNBT settings;
	
	private boolean bopLoaded;
	
	private Button doneButton;
	private Button cancelButton;
	private Button useBOPButton;
	
	private boolean useBOPBiomes;
	
	public CreateETGWorldScreen(CreateWorldScreen gui, CompoundNBT chunkProviderSettingsJson) 
	{
		super(new TranslationTextComponent("createworld.customize.etg_settings.title"));
		this.parent = gui;
		this.settings = chunkProviderSettingsJson;
		bopLoaded = ModList.get().isLoaded(BOPSupport.BOP_MODID);
		if (bopLoaded)
			useBOPBiomes = this.settings.getBoolean(EvoType.USE_BOP_TAG);
		else
		{
			useBOPBiomes = false;
			this.settings.putBoolean(EvoType.USE_BOP_TAG, false);
		}
	}

	@Override
	protected void init()
	{
		doneButton = this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done"), savePress ->
		{
			this.parent.chunkProviderSettingsJson = this.serialize();
			this.minecraft.displayGuiScreen(this.parent);
		}
		));
		cancelButton = this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel"), exitPress -> 
		{
			this.minecraft.displayGuiScreen(this.parent);
		}));

		if (bopLoaded)
		{
			useBOPButton = this.addButton(new Button(this.width / 2 - 75, 115, 150, 20, I18n.format("gui.etg_settings.use_bop_biomes"), bopPress ->
			{
				useBOPBiomes = !useBOPBiomes;
				updateDisplay();
			}
			));
		}
		else
		{
			useBOPButton = this.addButton(new Button(this.width / 2 - 75, 115, 150, 20, I18n.format("gui.etg_settings.bop_unavailable"), bopPress ->{}));
			useBOPButton.active = false;
		}
		updateDisplay();
	}
	
	@Override
	public void render(int p_render_1_, int p_render_2_, float p_render_3_)
	{
		this.renderBackground();
		super.render(p_render_1_, p_render_2_, p_render_3_);
	}
	
	private void updateDisplay()
	{
		if (bopLoaded)
			useBOPButton.setMessage(getFormattedToggle(I18n.format("gui.etg_settings.use_bop_biomes"), useBOPBiomes));
		else
			useBOPButton.setMessage(I18n.format("gui.etg_settings.bop_unavailable"));
	}

	private CompoundNBT serialize()
	{
		CompoundNBT generatorNBT = new CompoundNBT();
		generatorNBT.putBoolean(EvoType.USE_BOP_TAG, useBOPBiomes);
		return generatorNBT;
	}

	private String getFormattedToggle(String prefix, boolean value)
	{
		if (value)
			return prefix + ": " + I18n.format("options.on");

		return prefix + ": " + I18n.format("options.off");
	}

}
