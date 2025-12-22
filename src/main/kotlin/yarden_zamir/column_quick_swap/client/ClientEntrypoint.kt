package yarden_zamir.column_quick_swap.client

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.common.Mod
import yarden_zamir.column_quick_swap.ClientConfig
import yarden_zamir.column_quick_swap.ColumnQuickSwap
import yarden_zamir.column_quick_swap.networking.Networking

@Mod.EventBusSubscriber(modid = ColumnQuickSwap.ID, value = [Dist.CLIENT], bus = Mod.EventBusSubscriber.Bus.MOD)
object ClientEntrypoint {
    init {
        ClientConfig.reload()
        Networking
        SlotInteractManager
    }
}
