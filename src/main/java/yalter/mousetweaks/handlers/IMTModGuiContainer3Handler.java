package yalter.mousetweaks.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import yalter.mousetweaks.IGuiScreenHandler;
import yalter.mousetweaks.MouseButton;
import yalter.mousetweaks.Reflection;
import yalter.mousetweaks.api.IMTModGuiContainer3;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class IMTModGuiContainer3Handler implements IGuiScreenHandler {
	private Minecraft mc;
	private IMTModGuiContainer3 modGuiContainer;
	private Method handleMouseClick;

	public IMTModGuiContainer3Handler(IMTModGuiContainer3 modGuiContainer) {
		this.mc = Minecraft.getInstance();
		this.modGuiContainer = modGuiContainer;
		this.handleMouseClick = Reflection.getHMCMethod(modGuiContainer);
	}

	@Override
	public boolean isMouseTweaksDisabled() {
		return modGuiContainer.MT_isMouseTweaksDisabled();
	}

	@Override
	public boolean isWheelTweakDisabled() {
		return modGuiContainer.MT_isWheelTweakDisabled();
	}

	@Override
	public List<Slot> getSlots() {
		return modGuiContainer.MT_getContainer().inventorySlots;
	}

	@Override
	public Slot getSlotUnderMouse(double mouseX, double mouseY) {
		return modGuiContainer.MT_getSlotUnderMouse(mouseX, mouseY);
	}

	@Override
	public boolean disableRMBDraggingFunctionality() {
		return modGuiContainer.MT_disableRMBDraggingFunctionality();
	}

	@Override
	public void clickSlot(Slot slot, MouseButton mouseButton, boolean shiftPressed) {
		if (handleMouseClick != null) {
			try {
				handleMouseClick.invoke(modGuiContainer,
				                        slot,
				                        slot.slotNumber,
				                        mouseButton.getValue(),
				                        shiftPressed ? ClickType.QUICK_MOVE : ClickType.PICKUP);
			} catch (InvocationTargetException e) {
				CrashReport crashreport = CrashReport.makeCrashReport(e,
				                                                      "handleMouseClick() threw an exception when "
				                                                      + "called from MouseTweaks.");
				throw new ReportedException(crashreport);
			} catch (IllegalAccessException e) {
				CrashReport crashreport = CrashReport.makeCrashReport(e,
				                                                      "Calling handleMouseClick() from MouseTweaks.");
				throw new ReportedException(crashreport);
			}
		} else {
			mc.playerController.windowClick(modGuiContainer.MT_getContainer().windowId,
			                                slot.slotNumber,
			                                mouseButton.getValue(),
			                                shiftPressed ? ClickType.QUICK_MOVE : ClickType.PICKUP,
			                                mc.player);
		}
	}

	@Override
	public boolean isCraftingOutput(Slot slot) {
		return modGuiContainer.MT_isCraftingOutput(slot);
	}

	@Override
	public boolean isIgnored(Slot slot) {
		return modGuiContainer.MT_isIgnored(slot);
	}
}
