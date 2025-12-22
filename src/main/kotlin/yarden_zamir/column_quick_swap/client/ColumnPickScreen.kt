package yarden_zamir.column_quick_swap.client

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.ScreenEvent
import net.minecraftforge.common.MinecraftForge
import yarden_zamir.column_quick_swap.ClientConfig
import yarden_zamir.column_quick_swap.ColumnQuickSwap
import yarden_zamir.column_quick_swap.DrawableNineSliceTexture
import yarden_zamir.column_quick_swap.networking.C2SSwapColumnSlotPacket
import yarden_zamir.column_quick_swap.networking.Networking

@OnlyIn(Dist.CLIENT)
class ColumnPickScreen(
    private val hotbarSlot: Int,
    private val slotScreenX: Int,
    private val slotScreenY: Int
) : Screen(Component.translatable("gui.${ColumnQuickSwap.ID}.column_pick")) {
    companion object {
        private val TEXTURE = ColumnQuickSwap.id("textures/gui/window.png")
        private const val TEXTURE_WIDTH = 128
        private const val TEXTURE_HEIGHT = 128

        private const val WIDTH = 102
        private const val HEIGHT = 30

        private const val SLOT_SIZE = 18
        private const val BORDER = 6
        private const val COLUMN_SLOTS = 3

        val texture = DrawableNineSliceTexture(
            TEXTURE,
            TEXTURE_WIDTH,
            TEXTURE_HEIGHT,
            0,
            0,
            WIDTH,
            HEIGHT,
            BORDER,
            BORDER,
            BORDER,
            BORDER
        )
    }

    private var x = 0
    private var y = 0
    private val windowWidth = SLOT_SIZE + BORDER * 2
    private val windowHeight = SLOT_SIZE * COLUMN_SLOTS + BORDER * 2

    override fun init() {
        val inventory = minecraft?.player?.inventory ?: run {
            onClose()
            return
        }

        // Center horizontally on hotbar slot
        x = slotScreenX + (SLOT_SIZE / 2) - (windowWidth / 2)
        x = x.coerceIn(0, super.width - windowWidth)

        // Position above the hotbar slot
        val margin = 4
        y = slotScreenY - windowHeight - margin
        y = y.coerceIn(0, super.height - windowHeight)

        // Create buttons for each column slot (top to bottom: row 2, 1, 0)
        // Visual order: top=row2 (slots 9-17), middle=row1 (18-26), bottom=row0 (27-35)
        for (i in 0 until COLUMN_SLOTS) {
            val columnRow = 2 - i  // 2, 1, 0 from top to bottom
            val inventorySlot = when (columnRow) {
                0 -> 27 + hotbarSlot
                1 -> 18 + hotbarSlot
                2 -> 9 + hotbarSlot
                else -> continue
            }
            val item = inventory.getItem(inventorySlot)
            val buttonX = x + BORDER
            val buttonY = y + BORDER + SLOT_SIZE * i

            addRenderableWidget(
                ColumnSlotButton(
                    item = item,
                    x = buttonX,
                    y = buttonY,
                    width = SLOT_SIZE,
                    height = SLOT_SIZE,
                    keyNumber = i + 1  // 1, 2, 3 from top to bottom
                ) {
                    selectSlot(columnRow)
                }
            )
        }
    }

    private fun selectSlot(columnRow: Int) {
        val cfg = ClientConfig.config
        if (cfg.playSound) {
            minecraft?.soundManager?.play(
                SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), cfg.soundPitch, cfg.soundVolume)
            )
        }
        Networking.channel.sendToServer(C2SSwapColumnSlotPacket(hotbarSlot, columnRow))
        onClose()
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        // Check if key released - select hovered item (if closeOnRelease enabled)
        if (ClientConfig.config.closeOnRelease && !SlotInteractManager.columnPicking) {
            renderables.asSequence()
                .filterIsInstance<ColumnSlotButton>()
                .firstOrNull { it.isMouseOver(mouseX.toDouble(), mouseY.toDouble()) }
                ?.onPress()
            onClose()
            return
        }
        renderBackground(guiGraphics)
        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    override fun renderBackground(guiGraphics: GuiGraphics) {
        texture.draw(guiGraphics, x, y, windowWidth, windowHeight)
        MinecraftForge.EVENT_BUS.post(ScreenEvent.BackgroundRendered(this, guiGraphics))
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        // Handle number keys 1-3
        when (keyCode) {
            InputConstants.KEY_1 -> {
                selectSlot(2)  // Top row
                return true
            }
            InputConstants.KEY_2 -> {
                selectSlot(1)  // Middle row
                return true
            }
            InputConstants.KEY_3 -> {
                selectSlot(0)  // Bottom row (closest to hotbar)
                return true
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun onClose() {
        super.onClose()
        SlotInteractManager.columnPickPressedTicks = 0
        SlotInteractManager.columnPicking = false
    }

    override fun isPauseScreen(): Boolean = false

    override fun mouseScrolled(mouseX: Double, mouseY: Double, delta: Double): Boolean {
        if (!ClientConfig.config.allowScroll) return false

        // Allow scrolling to change hotbar selection
        val player = minecraft?.player ?: return false
        val inventory = player.inventory
        inventory.selected = (inventory.selected - delta.toInt()).mod(9)

        // Reopen for new slot
        val keyDown = InputConstants.isKeyDown(
            minecraft!!.window.window,
            SlotInteractManager.COLUMN_PICK_KEY.key.value
        )
        if (keyDown) {
            val screenWidth = minecraft!!.window.guiScaledWidth
            val screenHeight = minecraft!!.window.guiScaledHeight
            val newSlotScreenX = screenWidth / 2 - 91 + inventory.selected * 20 + 3
            val newSlotScreenY = screenHeight - 22 + 3
            minecraft!!.setScreen(ColumnPickScreen(inventory.selected, newSlotScreenX, newSlotScreenY))
            SlotInteractManager.columnPicking = true
        } else {
            onClose()
        }
        return true
    }
}

@OnlyIn(Dist.CLIENT)
private class ColumnSlotButton(
    private val item: ItemStack,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private val keyNumber: Int,
    onPress: OnPress
) : Button(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION) {

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val font = Minecraft.getInstance().font

        // Draw number label outside the window, to the left
        val numberStr = keyNumber.toString()
        val numberX = x - 6 - font.width(numberStr) - 2  // 6 = border width
        val numberY = y + (height - font.lineHeight) / 2 + 1
        guiGraphics.drawString(font, numberStr, numberX, numberY, 0xFFFFFF, true)

        // Draw item (works for empty stacks too - just shows nothing)
        if (!item.isEmpty) {
            guiGraphics.renderItem(item, x + 1, y + 1)
        }

        // Hover highlight
        if (isMouseOver(mouseX.toDouble(), mouseY.toDouble())) {
            guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, ClientConfig.config.highlightColor)
            if (!item.isEmpty && ClientConfig.config.showTooltips) {
                renderTooltip(guiGraphics, mouseX, mouseY)
            }
        }
    }

    private fun renderTooltip(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        val tooltipLines = Screen.getTooltipFromItem(Minecraft.getInstance(), item)
        guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltipLines, item.tooltipImage, mouseX, mouseY)
    }
}
