package yarden_zamir.column_quick_swap

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation

class DrawableNineSliceTexture(
    private val texture: ResourceLocation,
    private val textureWidth: Int,
    private val textureHeight: Int,
    private val u: Int,
    private val v: Int,
    private val width: Int,
    private val height: Int,
    private val sliceLeft: Int,
    private val sliceRight: Int,
    private val sliceTop: Int,
    private val sliceBottom: Int
) {
    fun draw(guiGraphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int) {
        val leftWidth = sliceLeft
        val rightWidth = sliceRight
        val topHeight = sliceTop
        val bottomHeight = sliceBottom
        val middleWidth = this.width - leftWidth - rightWidth
        val middleHeight = this.height - topHeight - bottomHeight

        val tiledMiddleWidth = width - leftWidth - rightWidth
        val tiledMiddleHeight = height - topHeight - bottomHeight

        // Top-left corner
        guiGraphics.blit(texture, x, y, u.toFloat(), v.toFloat(), leftWidth, topHeight, textureWidth, textureHeight)
        // Top-right corner
        guiGraphics.blit(texture, x + width - rightWidth, y, (u + this.width - rightWidth).toFloat(), v.toFloat(), rightWidth, topHeight, textureWidth, textureHeight)
        // Bottom-left corner
        guiGraphics.blit(texture, x, y + height - bottomHeight, u.toFloat(), (v + this.height - bottomHeight).toFloat(), leftWidth, bottomHeight, textureWidth, textureHeight)
        // Bottom-right corner
        guiGraphics.blit(texture, x + width - rightWidth, y + height - bottomHeight, (u + this.width - rightWidth).toFloat(), (v + this.height - bottomHeight).toFloat(), rightWidth, bottomHeight, textureWidth, textureHeight)

        // Top edge
        drawTiledHorizontal(guiGraphics, x + leftWidth, y, tiledMiddleWidth, topHeight, u + leftWidth, v, middleWidth, topHeight)
        // Bottom edge
        drawTiledHorizontal(guiGraphics, x + leftWidth, y + height - bottomHeight, tiledMiddleWidth, bottomHeight, u + leftWidth, v + this.height - bottomHeight, middleWidth, bottomHeight)
        // Left edge
        drawTiledVertical(guiGraphics, x, y + topHeight, leftWidth, tiledMiddleHeight, u, v + topHeight, leftWidth, middleHeight)
        // Right edge
        drawTiledVertical(guiGraphics, x + width - rightWidth, y + topHeight, rightWidth, tiledMiddleHeight, u + this.width - rightWidth, v + topHeight, rightWidth, middleHeight)

        // Middle
        drawTiledArea(guiGraphics, x + leftWidth, y + topHeight, tiledMiddleWidth, tiledMiddleHeight, u + leftWidth, v + topHeight, middleWidth, middleHeight)
    }

    private fun drawTiledHorizontal(guiGraphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int, u: Int, v: Int, tileWidth: Int, tileHeight: Int) {
        var drawn = 0
        while (drawn < width) {
            val drawWidth = minOf(tileWidth, width - drawn)
            guiGraphics.blit(texture, x + drawn, y, u.toFloat(), v.toFloat(), drawWidth, height, textureWidth, textureHeight)
            drawn += drawWidth
        }
    }

    private fun drawTiledVertical(guiGraphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int, u: Int, v: Int, tileWidth: Int, tileHeight: Int) {
        var drawn = 0
        while (drawn < height) {
            val drawHeight = minOf(tileHeight, height - drawn)
            guiGraphics.blit(texture, x, y + drawn, u.toFloat(), v.toFloat(), width, drawHeight, textureWidth, textureHeight)
            drawn += drawHeight
        }
    }

    private fun drawTiledArea(guiGraphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int, u: Int, v: Int, tileWidth: Int, tileHeight: Int) {
        var drawnY = 0
        while (drawnY < height) {
            val drawHeight = minOf(tileHeight, height - drawnY)
            var drawnX = 0
            while (drawnX < width) {
                val drawWidth = minOf(tileWidth, width - drawnX)
                guiGraphics.blit(texture, x + drawnX, y + drawnY, u.toFloat(), v.toFloat(), drawWidth, drawHeight, textureWidth, textureHeight)
                drawnX += drawWidth
            }
            drawnY += drawHeight
        }
    }
}
