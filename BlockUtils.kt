package cn.asone.endless.utils

import net.minecraft.block.*
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3i
import kotlin.math.abs

object BlockUtils {
    @JvmStatic
    fun isBlockSolid(x: Int, y: Int, z: Int): Boolean = isBlockSolid(mc.theWorld.getBlock(x, y, z))

    @JvmStatic
    fun isBlockSolid(block: Block): Boolean {
        return block.isSolidFullCube ||
                block is BlockSlab ||
                block is BlockStairs ||
                block is BlockCactus ||
                block is BlockChest ||
                block is BlockEnderChest ||
                block is BlockSkull ||
                block is BlockPane ||
                block is BlockFence ||
                block is BlockWall ||
                block is BlockGlass ||
                block is BlockPistonBase ||
                block is BlockPistonExtension ||
                block is BlockPistonMoving ||
                block is BlockStainedGlass ||
                block is BlockTrapDoor ||
                block is BlockCarpet
    }
    
    @JvmStatic
    fun isSafeToWalkOn(block: Block): Boolean {
        return block !is BlockFence &&
                block !is BlockWall
    }
}