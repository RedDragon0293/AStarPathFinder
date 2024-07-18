package cn.asone.endless.utils

import net.minecraft.block.*
import net.minecraft.client.Minecraft
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3i
import kotlin.math.abs

object BlockUtils {
    @JvmStatic
    fun isBlockSolid(x: Int, y: Int, z: Int): Boolean {
        val world = mc.theWorld
        return world.getBlock(x, y, z).isSolidFullCube ||
                (world.getBlock(x, y, z) is BlockSlab) ||
                (world.getBlock(x, y, z) is BlockStairs) ||
                (world.getBlock(x, y, z) is BlockCactus) ||
                (world.getBlock(x, y, z) is BlockChest) ||
                (world.getBlock(x, y, z) is BlockEnderChest) ||
                (world.getBlock(x, y, z) is BlockSkull) ||
                (world.getBlock(x, y, z) is BlockPane) ||
                (world.getBlock(x, y, z) is BlockFence) ||
                (world.getBlock(x, y, z) is BlockWall) ||
                (world.getBlock(x, y, z) is BlockGlass) ||
                (world.getBlock(x, y, z) is BlockPistonBase) ||
                (world.getBlock(x, y, z) is BlockPistonExtension) ||
                (world.getBlock(x, y, z) is BlockPistonMoving) ||
                (world.getBlock(x, y, z) is BlockStainedGlass) ||
                (world.getBlock(x, y, z) is BlockTrapDoor) ||
                (world.getBlock(x, y, z) is BlockCarpet)
    }
}