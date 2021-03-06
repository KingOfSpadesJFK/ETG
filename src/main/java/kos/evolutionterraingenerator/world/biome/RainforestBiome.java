package kos.evolutionterraingenerator.world.biome;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.MultipleRandomFeatureConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public class RainforestBiome extends Biome {
	   public RainforestBiome() {
		      super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG).precipitation(Biome.RainType.RAIN).category(Biome.Category.FOREST).depth(0.1F).scale(0.2F).temperature(0.9F).downfall(0.85F).waterColor(0x00c677).waterFogColor(0x13531e).parent((String)null));
		      this.addStructure(Feature.MINESHAFT.withConfiguration(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)));
		      this.addStructure(Feature.STRONGHOLD.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
		      this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
		    		  Feature.RANDOM_SELECTOR.withConfiguration
		    		  (
		    				  new MultipleRandomFeatureConfig
		    				  (
		    						  ImmutableList.of
		    						  (
		    								  Feature.FANCY_TREE.withConfiguration
		    								  (DefaultBiomeFeatures.FANCY_TREE_CONFIG)
		    								  .withChance(0.2F),
		    								  Feature.JUNGLE_GROUND_BUSH.withConfiguration
		    								  (DefaultBiomeFeatures.OAK_TREE_CONFIG)
		    								  .withChance(0.2F)
		    								  ), 
		    						  Feature.NORMAL_TREE.withConfiguration
		    						  (DefaultBiomeFeatures.OAK_TREE_CONFIG)
		    						  )
		    				  )
		    		  .withPlacement
		    		  (
		    				  Placement.COUNT_EXTRA_HEIGHTMAP
		    				  .configure(new AtSurfaceWithExtraConfig(10, 0.1F, 1))
		    				  )
		    		  );

		      addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Feature.LAKE.withConfiguration(new BlockStateFeatureConfig(Blocks.WATER.getDefaultState())).withPlacement(Placement.WATER_LAKE.configure(new ChanceConfig(2))));
		      addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Feature.LAKE.withConfiguration(new BlockStateFeatureConfig(Blocks.LAVA.getDefaultState())).withPlacement(Placement.LAVA_LAKE.configure(new ChanceConfig(80))));
		      DefaultBiomeFeatures.addCarvers(this);
		      DefaultBiomeFeatures.addStructures(this);
		      DefaultBiomeFeatures.addMonsterRooms(this);
		      DefaultBiomeFeatures.addDoubleFlowers(this);
		      DefaultBiomeFeatures.addStoneVariants(this);
		      DefaultBiomeFeatures.addOres(this);
		      DefaultBiomeFeatures.addSedimentDisks(this);
		      DefaultBiomeFeatures.addDefaultFlowers(this);
		      DefaultBiomeFeatures.addVeryDenseGrass(this);
		      DefaultBiomeFeatures.addMushrooms(this);
		      DefaultBiomeFeatures.addReedsAndPumpkins(this);
		      DefaultBiomeFeatures.addSprings(this);
		      DefaultBiomeFeatures.addFreezeTopLayer(this);
		      this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.SHEEP, 12, 4, 4));
		      this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.PIG, 10, 4, 4));
		      this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.CHICKEN, 10, 4, 4));
		      this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.COW, 8, 4, 4));
		      this.addSpawn(EntityClassification.AMBIENT, new Biome.SpawnListEntry(EntityType.BAT, 10, 8, 8));
		      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SPIDER, 100, 4, 4));
		      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE, 95, 4, 4));
		      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
		      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SKELETON, 100, 4, 4));
		      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.CREEPER, 100, 4, 4));
		      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.SLIME, 100, 4, 4));
		      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 10, 1, 4));
		      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.WITCH, 5, 1, 1));
		   }
}
