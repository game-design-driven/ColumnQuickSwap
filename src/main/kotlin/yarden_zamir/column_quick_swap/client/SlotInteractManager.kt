package yarden_zamir.column_quick_swap.client

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.client.event.RenderGuiEvent
import net.minecraftforge.client.event.ScreenEvent
import net.minecraftforge.event.TickEvent
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import yarden_zamir.column_quick_swap.ClientConfig

@OnlyIn(Dist.CLIENT)
object SlotInteractManager {
    var columnPickPressedTicks = 0
    var columnPicking = false
    private var lastColumnPickOpenTime = 0L
    private const val OPEN_COOLDOWN_MS = 100L

    val COLUMN_PICK_KEY =
        KeyMapping("key.column_quick_swap.column_pick", InputConstants.KEY_V, "key.categories.inventory")

    init {
        MOD_BUS.addListener { event: RegisterKeyMappingsEvent ->
            event.register(COLUMN_PICK_KEY)
        }

        FORGE_BUS.addListener { event: InputEvent.Key ->
            if (event.key == COLUMN_PICK_KEY.key.value) {
                when (event.action) {
                    InputConstants.PRESS -> {
                        COLUMN_PICK_KEY.isDown = true
                        columnPickPressedTicks = 0
                    }

                    InputConstants.RELEASE -> {
                        COLUMN_PICK_KEY.isDown = false
                        columnPickPressedTicks = 0
                        columnPicking = false
                    }
                }
            }
        }

        FORGE_BUS.addListener { event: TickEvent.ClientTickEvent ->
            if (event.phase != TickEvent.Phase.END) return@addListener
            val minecraft = Minecraft.getInstance()
            if (minecraft.player == null) return@addListener
            val screen = minecraft.screen

            // Column pick - only works when no screen is open (outside inventory)
            if (COLUMN_PICK_KEY.isDown && screen == null) {
                columnPickPressedTicks++
            }
        }

        FORGE_BUS.addListener { event: ScreenEvent.Opening ->
            if (event.screen !is ColumnPickScreen) {
                columnPickPressedTicks = 0
            }
        }

        FORGE_BUS.addListener { event: ScreenEvent.Closing ->
            if (event.screen !is ColumnPickScreen) {
                columnPickPressedTicks = 0
            }
        }

        FORGE_BUS.addListener { event: RenderGuiEvent.Post ->
            val minecraft = Minecraft.getInstance()
            val screen = minecraft.screen

            // Column pick - only when no screen is open
            if (columnPickPressedTicks > ClientConfig.config.pressTicks && screen == null) {
                if (!columnPicking) {
                    val now = System.currentTimeMillis()
                    if (now - lastColumnPickOpenTime < OPEN_COOLDOWN_MS) return@addListener
                    lastColumnPickOpenTime = now

                    val inventory = minecraft.player!!.inventory
                    val screenWidth = event.guiGraphics.guiWidth()
                    val screenHeight = event.guiGraphics.guiHeight()
                    val slotScreenX = screenWidth / 2 - 91 + inventory.selected * 20 + 3
                    val slotScreenY = screenHeight - 22 + 3
                    minecraft.setScreen(ColumnPickScreen(inventory.selected, slotScreenX, slotScreenY))
                    columnPicking = true
                }
            }
        }
    }
}
