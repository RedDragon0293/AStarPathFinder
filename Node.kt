package cn.asone.endless.utils.pathfinding

import net.minecraft.util.BlockPos
import kotlin.math.sqrt

data class Node(val pos: BlockPos, val currentCost: Double, val cameFrom: Node?) {
    fun getHeuristicCost(destination: BlockPos): Double {
        return currentCost + sqrt(pos.distanceSq(destination))
    }
}
