package cn.asone.endless.utils.pathfinding

import cn.asone.endless.utils.BlockUtils
import net.minecraft.block.BlockLiquid
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import net.minecraft.util.Vec3i
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

class AStarPathFinder(val startPos: BlockPos, val endPos: BlockPos, val maxDistance: Double) {
    constructor(startPos: Vec3, endPos: Vec3, maxDistance: Double) : this(
        BlockPos(
            floor(startPos.xCoord),
            ceil(startPos.yCoord) /* 此处向上取整的原因是我们假设玩家脚下的方块永远是完整的, 以方便之后的处理 */,
            floor(startPos.zCoord)
        ),
        BlockPos(floor(endPos.xCoord), ceil(endPos.yCoord), floor(endPos.zCoord)),
        maxDistance
    )

    private val comparator: Comparator<Node> = Comparator { o1: Node, o2: Node ->
        val d1 = o1.getHeuristicCost(endPos)
        val d2 = o2.getHeuristicCost(endPos)
        if (d1 == d2)
            return@Comparator 0
        if (d1 > d2)
            return@Comparator 1
        return@Comparator -1
    }
    private val queue = PriorityQueue(comparator)
    private val visited = hashSetOf<BlockPos>()
    val nodes = arrayListOf<Node>()
    private val directions = arrayOf(
        Vec3i(1, 0, 0),
        Vec3i(1, 0, 1),
        Vec3i(0, 0, 1),
        Vec3i(-1, 0, 1),
        Vec3i(-1, 0, 0),
        Vec3i(-1, 0, -0),
        Vec3i(0, 0, -1),
        Vec3i(1, 0, -1)
    )
    var avoidLiquid = true
    var maxFallDistance = 1

    fun calculatePath(): ArrayList<BlockPos> {
        queue.add(Node(startPos, 0.0, null))
        visited.add(startPos)
        while (queue.isNotEmpty()) {
            val currentNode = queue.poll()
            nodes.add(currentNode)
            if (currentNode.pos == endPos) {
                break
            }
            if (currentNode.pos.distanceSq(startPos) > maxDistance * maxDistance) {
                continue
            }
            // 往周围八个方向探索
            for ((index, it) in directions.withIndex()) {
                var targetPos = currentNode.pos.add(it)
                if (index and 1 == 1) { // 斜着走
                    // 只有两边没有阻挡才有斜着走的必要
                    if (!checkPositionValidity(currentNode.pos.add(it.x, 0, 0), false)
                        || !checkPositionValidity(currentNode.pos.add(0, 0, it.z), false)
                    ) {
                        continue
                    }
                }
                if (!checkPositionValidity(targetPos, true)) {
                    // 能否高一格
                    targetPos = targetPos.add(0, 1, 0)
                    if (!checkPositionValidity(targetPos, true)) {
                        targetPos = targetPos.add(0, -1, 0)
                        var flag = true
                        // 能否安全坠落
                        label@ for (distance in 1..maxFallDistance) {
                            targetPos = targetPos.add(0, -1, 0)
                            if (checkPositionValidity(targetPos, true)) {
                                // 坠落地点到上面是不是空的
                                for (i in 1 until distance) {
                                    if (BlockUtils.isBlockSolid(targetPos.x, targetPos.y + i, targetPos.z))
                                        break@label
                                }
                                flag = false
                                break
                            }
                        }
                        // 不能安全坠落
                        if (flag) {
                            continue
                        }
                    }
                }
                // 探索过了
                if (visited.contains(targetPos)) {
                    continue
                }
                // 目标位置脚下的方块
                val targetBlock = mc.theWorld.getBlockState(targetPos.add(0, -1, 0)).block
                // 当前位置脚下的方块
                val currentBlock = mc.theWorld.getBlockState(currentNode.pos.add(0, -1, 0)).block
                // 计算高度差
                val diffY =
                    targetBlock.blockBoundsMaxY - currentBlock.blockBoundsMaxY
                // 能不能跳上去
                if (diffY >= 1.1) {
                    continue
                }
                val targetNode =
                    Node(targetPos, sqrt(currentNode.pos.distanceSq(targetPos)) + currentNode.currentCost, currentNode)
                queue.add(targetNode)
                visited.add(targetPos)
            }
        }
        val results = arrayListOf<BlockPos>()
        // 从终点到起点生成路径
        var currentNode: Node? = nodes.last()
        while (currentNode != null) {
            results.add(currentNode.pos)
            currentNode = currentNode.cameFrom
        }
        results.reverse()
        nodes.clear()
        queue.clear()
        visited.clear()
        return results
    }

    private fun checkPositionValidity(pos: BlockPos, checkGround: Boolean): Boolean {
        return checkPositionValidity(pos.x, pos.y, pos.z, checkGround)
    }

    private fun checkPositionValidity(x: Int, y: Int, z: Int, checkGround: Boolean): Boolean {
        if (avoidLiquid
            && (mc.theWorld.getBlock(x, y, z) is BlockLiquid
                    || mc.theWorld.getBlock(x, y + 1, z) is BlockLiquid)
        )
            return false

        return !BlockUtils.isBlockSolid(x, y, z)
                && (!BlockUtils.isBlockSolid(x, y + 1, z)
                || (mc.theWorld.getBlock(x, y + 1, z).blockBoundsMinY - mc.theWorld.getBlock(x,y - 1,z).blockBoundsMaxY) > mc.thePlayer.height //头顶的方块和脚下的方块之间的距离够玩家通过
                ) //上面两格能走过去
                && (BlockUtils.isBlockSolid(x, y - 1, z) || !checkGround)
                && BlockUtils.isSafeToWalkOn(x, y - 1, z)
    }
}