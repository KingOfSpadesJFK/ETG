package kos.evolutionterraingenerator.world.biome;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.LakesConfig;
import net.minecraft.world.gen.feature.MultipleRandomFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.ShrubFeature;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.LakeChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public class RainforestBiome extends Biome {
	   public RainforestBiome() {
		      super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG).precipitation(Biome.RainType.RAIN).category(Biome.Category.FOREST).depth(0.1F).scale(0.2F).temperature(0.9F).downfall(0.85F).waterColor(0x2dbd46).waterFogColor(0x13531e).parent((String)null));
		      this.addStructure(Feature.MINESHAFT, new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL));
		      this.addStructure(Feature.STRONGHOLD, IFeatureConfig.NO_FEATURE_CONFIG);
		      addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, 
		    		  Biome.createDecoratedFeature(
		    				  Feature.RANDOM_SELECTOR, 
		    				  new MultipleRandomFeatureConfig(
		    						  new Feature[]{
		    								  Feature.NORMAL_TREE, 
		    								  new ShrubFeature(
		    										  NoFeatureConfig::deserialize, 
		    										  Blocks.OAK_LOG.getDefaultState(), 
		    										  Blocks.OAK_LEAVES.getDefaultState())
		    						  }, 
		    						  new IFeatureConfig[]{
		    								  IFeatureConfig.NO_FEATURE_CONFIG, 
		    								  IFeatureConfig.NO_FEATURE_CONFIG
		    								  }, 
		    						  new float[]{
		    								  0.2F, 
		    								  0.3F
		    								  }, 
		    						  Feature.FANCY_TREE, 
		    						  IFeatureConfig.NO_FEATURE_CONFIG), 
		    				  Placement.COUNT_EXTRA_HEIGHTMAP, 
		    				  new AtSurfaceWithExtraConfig(10, 0.1F, 1)
		    				  )
		    		  );
		      addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Biome.createDecoratedFeature(Feature.LAKE, new LakesConfig(Blocks.WATER.getDefaultState()), Placement.WATER_LAKE, new LakeChanceConfig(2)));
		      addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Biome.createDecoratedFeature(Feature.LAKE, new LakesConfig(Blocks.LAVA.getDefaultState()), Placement.LAVA_LAKE, new LakeChanceConfig(80)));
		      DefaultBiomeFeatures.addCarvers(this);
		      DefaultBiomeFeatures.addStructures(this);
		      DefaultBiomeFeatures.addMonsterRooms(this);
		      DefaultBiomeFeatures.addDoubleFlowers(this);
		      DefaultBiomeFeatures.addStoneVariants(this);
		      DefaultBiomeFeatures.addOres(this);
		      DefaultBiomeFeatures.addSedimentDisks(this);
		      DefaultBiomeFeatures.addDefaultFlowers(this);
		      DefaultBiomeFeatures.func_222339_L(this);
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
