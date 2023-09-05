package com.foo;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
public class TintOverlay extends OverlayPanel {
    private final Client client;
    private final EasyArceuusRunecraftingPlugin plugin;
    private final EasyArceuusRunecraftingConfig config;


    @Inject
    private TintOverlay(
            Client client, EasyArceuusRunecraftingPlugin plugin, EasyArceuusRunecraftingConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.MANUAL);
        drawAfterLayer(WidgetInfo.INVENTORY);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isPlayerInArea()) {return null;}
        if (plugin.isIdle()) {
            if (config.idleTint()) {
                tintWithColor(graphics, plugin.currentidleTintColor());
            }
        } else {
            if (config.activeTint()) {
                tintWithColor(graphics, config.activeTintColor());
            }
        }
        return null;
    }

    private void tintWithColor(Graphics2D graphics, Color color) {
        if (plugin.isDisableTintForLevel()) {return;}
        graphics.setColor(color);
        graphics.fillRect(0, 0, client.getCanvasWidth(), client.getCanvasHeight());
    }

}
