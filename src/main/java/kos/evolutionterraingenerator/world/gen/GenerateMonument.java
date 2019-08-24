package kos.evolutionterraingenerator.world.gen;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import kos.evolutionterraingenerator.world.BiomeProviderEvo;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraft.world.gen.structure.StructureStart;

public class GenerateMonument extends MapGenStructure
{
    private int spacing;
    private int separation;
    public static final List<Biome> WATER_BIOMES = Arrays.<Biome>asList(Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER);
    public static final List<Biome> SPAWN_BIOMES = Arrays.<Biome>asList(Biomes.DEEP_OCEAN);
    private static final List<Biome.SpawnListEntry> MONUMENT_ENEMIES = Lists.<Biome.SpawnListEntry>newArrayList();
    private final BiomeProviderEvo biomeProvider;
    
    public GenerateMonument(BiomeProviderEvo biomeProvider)
    {
        this.spacing = 32;
        this.separation = 5;
        this.biomeProvider = biomeProvider;
    }

    public GenerateMonument(Map<String, String> p_i45608_1_, BiomeProviderEvo biomeProvider)
    {
        this(biomeProvider);

        for (Entry<String, String> entry : p_i45608_1_.entrySet())
        {
            if (((String)entry.getKey()).equals("spacing"))
            {
                this.spacing = MathHelper.getInt(entry.getValue(), this.spacing, 1);
            }
            else if (((String)entry.getKey()).equals("separation"))
            {
                this.separation = MathHelper.getInt(entry.getValue(), this.separation, 1);
            }
        }
    }

    public String getStructureName()
    {
        return "Monument";
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= this.spacing - 1;
        }

        if (chunkZ < 0)
        {
            chunkZ -= this.spacing - 1;
        }

        int k = chunkX / this.spacing;
        int l = chunkZ / this.spacing;
        Random random = this.world.setRandomSeed(k, l, 10387313);
        k = k * this.spacing;
        l = l * this.spacing;
        k = k + (random.nextInt(this.spacing - this.separation) + random.nextInt(this.spacing - this.separation)) / 2;
        l = l + (random.nextInt(this.spacing - this.separation) + random.nextInt(this.spacing - this.separation)) / 2;

        if (i == k && j == l)
        {
            if (!this.biomeProvider.areBiomesViable(i * 16 + 8, j * 16 + 8, 16, SPAWN_BIOMES))
            {
                return false;
            }

            boolean flag = this.biomeProvider.areBiomesViable(i * 16 + 8, j * 16 + 8, 29, WATER_BIOMES);

            if (flag)
            {
                return true;
            }
        }

        return false;
    }

    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored)
    {
        this.world = worldIn;
        return findNearestStructurePosBySpacing(worldIn, this, pos, this.spacing, this.separation, 10387313, true, 100, findUnexplored);
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new StructureOceanMonument.StartMonument(this.world, this.rand, chunkX, chunkZ);
    }

    /**
     * Gets the scattered feature spawn list
     */
    public List<Biome.SpawnListEntry> getMonsters()
    {
        return MONUMENT_ENEMIES;
    }

    static
    {
        MONUMENT_ENEMIES.add(new Biome.SpawnListEntry(EntityGuardian.class, 1, 2, 4));
    }
}
