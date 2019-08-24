package kos.evolutionterraingenerator.world.gen;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import kos.evolutionterraingenerator.world.BiomeProviderEvo;

import java.util.Map.Entry;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureStart;

public class GenerateVillage extends MapGenStructure
{
    /** A list of all the biomes villages can spawn in. */
    public static List<Biome> VILLAGE_SPAWN_BIOMES = Arrays.<Biome>asList(Biomes.PLAINS, Biomes.DESERT, Biomes.SAVANNA, Biomes.TAIGA, Biomes.ICE_PLAINS);
    /** None */
    private int size;
    private int distance;
    private final int minTownSeparation;
    private final BiomeProviderEvo biomeProvider;

    public GenerateVillage(BiomeProviderEvo biomeProvider)
    {
        this.distance = 32;
        this.minTownSeparation = 8;
        this.biomeProvider = biomeProvider;
    }

    public GenerateVillage(Map<String, String> map, BiomeProviderEvo biomeProvider)
    {
        this(biomeProvider);

        for (Entry<String, String> entry : map.entrySet())
        {
            if (((String)entry.getKey()).equals("size"))
            {
                this.size = MathHelper.getInt(entry.getValue(), this.size, 0);
            }
            else if (((String)entry.getKey()).equals("distance"))
            {
                this.distance = MathHelper.getInt(entry.getValue(), this.distance, 9);
            }
        }
    }

    public String getStructureName()
    {
        return "Village";
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= this.distance - 1;
        }

        if (chunkZ < 0)
        {
            chunkZ -= this.distance - 1;
        }

        int k = chunkX / this.distance;
        int l = chunkZ / this.distance;
        Random random = this.world.setRandomSeed(k, l, 10387312);
        k = k * this.distance;
        l = l * this.distance;
        k = k + random.nextInt(this.distance - 8);
        l = l + random.nextInt(this.distance - 8);

        if (i == k && j == l)
        {
            boolean flag = this.biomeProvider.areBiomesViable(i * 16 + 8, j * 16 + 8, 0, VILLAGE_SPAWN_BIOMES);

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
        return findNearestStructurePosBySpacing(worldIn, this, pos, this.distance, 8, 10387312, false, 100, findUnexplored);
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new MapGenVillage.Start(this.world, this.rand, chunkX, chunkZ, this.size);
    }
}
