package net.kryos.feature.impl.render;

import net.kryos.Kryos;
import net.kryos.event.impl.GuiExtractionEvent;
import net.kryos.event.listener.impl.GuiExtractionListener;
import net.kryos.feature.Feature;
import net.kryos.feature.FeatureCategory;
import net.kryos.feature.setting.NumberSetting;
import net.kryos.feature.setting.NumberSettingBuilder;
import net.kryos.gui.MainTheme;
import net.kryos.notification.Notification;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class NotificationFeature extends Feature implements GuiExtractionListener {
	public NumberSetting<Long> lifetime = new NumberSettingBuilder<Long>()
			.name("Lifetime")
			.value(3000L)
			.min(1L)
			.max(10000L)
			.step(100L)
			.build();
	
	public NumberSetting<Integer> max = new NumberSettingBuilder<Integer>()
			.name("Max Notis")
			.value(3)
			.min(1)
			.max(10)
			.step(1)
			.build();
	
	public NotificationFeature() {
		super("Notifications", FeatureCategory.RENDER);
		addSettings(lifetime, max);
	}

	@Override
	protected void onEnable() {
		Kryos.eventBus.subscribe(this);
	}

	@Override
	protected void onDisable() {
		Kryos.eventBus.unsubscribe(this);
	}
	
	@Override
	public void onExtraction(GuiExtractionEvent event) {
		GuiGraphicsExtractor extractor = event.getGraphics();
		int height = 40;
		int yPos = mc.getWindow().getGuiScaledHeight() - mc.font.lineHeight - height - 10;
		
		Kryos.notificationBus.update();
		for(Notification notification : Kryos.notificationBus.getActive()) {
			int x = mc.getWindow().getGuiScaledWidth() - 150;
			extractor.fill(x, yPos, mc.getWindow().getGuiScaledWidth(), yPos + height, MainTheme.PRIMARY.getRGB());
			extractor.fill(x, yPos, x + 5, yPos + height, notification.getType().getColor().getRGB());
			extractor.text(mc.font, notification.getTitle(), x + 8, yPos + 2, -1);
			extractor.text(mc.font, notification.getText(), x + 8, yPos + 12, -1);
			yPos -= mc.font.lineHeight + 40;
		}
	}
}