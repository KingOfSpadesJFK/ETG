package kos.evolutionterraingenerator.world.biome;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BlockBlobConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.BuriedTreasureConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidWithNoiseConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public class GravelBeachBiome extends Biome {
	   public GravelBeachBiome() {
		      super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRAVEL_CONFIG).precipitation(Biome.RainType.RAIN).category(Biome.Category.BEACH).depth(0.0F).scale(0.025F).temperature(0.8F).downfall(0.4F).waterColor(4159204).waterFogColor(329011).parent("minecraft:beach"));
		      this.addStructure(Feature.MINESHAFT.withConfiguration(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)));
		      this.addStructure(Feature.BURIED_TREASURE.withConfiguration( new BuriedTreasureConfig(0.01F)));
		      this.addStructure(Feature.SHIPWRECK.withConfiguration(new ShipwreckConfig(true)));
		      this.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, 
		    		  Feature.FOREST_ROCK
		    		  .withConfiguration(new BlockBlobConfig(Blocks.COBBLESTONE.getDefaultState(), 0))
		    		  		.withPlacement(Placement.TOP_SOLID_HEIGHTMAP_NOISE_BIASED
		    		  				.configure(new TopSolidWithNoiseConfig(20, 80.0D, 0.0D, Heightmap.Type.OCEAN_FLOOR_WG))));
		      DefaultBiomeFeatures.addCarvers(this);
		      DefaultBiomeFeatures.addStructures(this);
		      DefaultBiomeFeatures.addLakes(this);
		      DefaultBiomeFeatures.addMonsterRooms(this);
		      DefaultBiomeFeatures.addStoneVariants(this);
		      DefaultBiomeFeatures.addOres(this);
		      DefaultBiomeFeatures.addSedimentDisks(this);
		      DefaultBiomeFeatures.addDefaultFlowers(this);
		      DefaultBiomeFeatures.addSparseGrass(this);
		      DefaultBiomeFeatures.addMushrooms(this);
		      DefaultBiomeFeatures.addSprings(this);
		      DefaultBiomeFeatures.addDesertFeatures(this);
		      DefaultBiomeFeatures.addFreezeTopLayer(this);
		      this.addSpawn(EntityClassification.CREATURE, new Biome.SpawnListEntry(EntityType.TURTLE, 5, 2, 5));
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
