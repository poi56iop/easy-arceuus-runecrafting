package com.foo;


import com.google.inject.Inject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import java.awt.*;

public class NextItemOverlay extends WidgetItemOverlay {
    private final EasyArceuusRunecraftingPlugin plugin;
    private final EasyArceuusRunecraftingConfig config;

    @Inject
    NextItemOverlay(EasyArceuusRunecraftingPlugin plugin, EasyArceuusRunecraftingConfig config)
    {
        this.config = config;
        this.plugin = plugin;
        showOnInventory();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
    {
        if (!plugin.isPlayerInArea()) {return;}
        if (itemId == plugin.getNextItemClick()) {
            highlightItem(graphics, widgetItem);
        }
    }

    private void highlightItem(Graphics2D graphics, WidgetItem widgetItem)
    {
        Rectangle bounds = widgetItem.getCanvasBounds();
        graphics.setColor(plugin.currentHighlightColor());
        graphics.fillRect((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());
    }
}
