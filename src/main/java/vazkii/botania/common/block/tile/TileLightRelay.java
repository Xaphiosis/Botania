/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jul 15, 2015, 8:32:04 PM (GMT)]
 */
package vazkii.botania.common.block.tile;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import vazkii.botania.api.wand.IWandBindable;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.core.helper.MathHelper;
import vazkii.botania.common.core.helper.Vector3;

public class TileLightRelay extends TileMod implements IWandBindable {

	private static final int MAX_DIST = 20;
	
	private static final String TAG_BIND_X = "bindX";
	private static final String TAG_BIND_Y = "bindY";
	private static final String TAG_BIND_Z = "bindZ";
	
	int bindX, bindY = -1, bindZ;
	int ticksElapsed = 0;
	
	@Override
	public void updateEntity() {
		if(bindY > -1) {
			Block block = worldObj.getBlock(bindX, bindY, bindZ);
			if(block != ModBlocks.lightRelay) {
				bindY = -1;
				return;
			}
			
			ticksElapsed++;
			
			Vector3 vec = getMovementVector();
			
			double dist = 0.1;
			int size = (int) (vec.mag() / dist);
			int count = 10;
			int start = ticksElapsed % size;
			
			Vector3 vecMag = vec.copy().normalize().multiply(dist);
			Vector3 vecTip = vecMag.copy().multiply(start).add(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
			
			double radPer = Math.PI / 16.0;
			float mul = 0.5F;
			float mulPer = 0.4F;
			float maxMul = 2;
			for(int i = start; i < start + count; i++) { // <- Replace to i = 0; i < size
				mul = Math.min(maxMul, mul + mulPer);
				double rad = radPer * (i + ticksElapsed * 0.4);
				Vector3 vecRot = vecMag.copy().crossProduct(Vector3.one).multiply(mul).rotate(rad, vecMag).add(vecTip);
				Botania.proxy.wispFX(worldObj, vecRot.x, vecRot.y, vecRot.z, 0.4F, 0.4F, 1F, 0.1F, (float) -vecMag.x, (float) -vecMag.y, (float) -vecMag.z, 1F);
				vecTip.add(vecMag);
			}
		}
	}
	
	public Vector3 getMovementVector() {
		return new Vector3(bindX - xCoord, bindY - yCoord, bindZ - zCoord);
	}
	
	@Override
	public ChunkCoordinates getBinding() {
		return new ChunkCoordinates(bindX, bindY, bindZ);
	}

	@Override
	public boolean canSelect(EntityPlayer player, ItemStack wand, int x, int y, int z, int side) {
		return true;
	}

	@Override
	public boolean bindTo(EntityPlayer player, ItemStack wand, int x, int y, int z, int side) {
		if(player.worldObj.getBlock(x, y, z) != ModBlocks.lightRelay || MathHelper.pointDistanceSpace(x, y, z, xCoord, yCoord, zCoord) > MAX_DIST)
			return false;
		
		bindX = x;
		bindY = y;
		bindZ = z;
		return true;
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound cmp) {
		bindX = cmp.getInteger(TAG_BIND_X);
		bindY = cmp.getInteger(TAG_BIND_Y);
		bindZ = cmp.getInteger(TAG_BIND_Z);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound cmp) {
		cmp.setInteger(TAG_BIND_X, bindX);
		cmp.setInteger(TAG_BIND_Y, bindY);
		cmp.setInteger(TAG_BIND_Z, bindZ);
	}

}
