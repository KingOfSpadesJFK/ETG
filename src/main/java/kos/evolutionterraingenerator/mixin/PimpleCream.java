package kos.evolutionterraingenerator.mixin;

import java.util.Random;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.WaterLakeDecorator;

@Mixin(WaterLakeDecorator.class)
public class PimpleCream {
		
	//Makes water lakes less common
	@Inject(at = @At("HEAD"), method = "getPositions", cancellable = true)
	public void rubCream(DecoratorContext decoratorContext, Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos, 
			CallbackInfoReturnable<Stream<BlockPos>> cbt) 
	{
		if (random.nextInt(chanceDecoratorConfig.chance * 10) == 0) {
			int i = random.nextInt(16) + blockPos.getX();
			int j = random.nextInt(16) + blockPos.getZ();
			int k = random.nextInt(decoratorContext.getMaxY());
			cbt.setReturnValue(Stream.of(new BlockPos(i, k, j)));
		} else {
			cbt.setReturnValue(Stream.empty());
		}
	}
}
