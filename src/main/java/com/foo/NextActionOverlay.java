package com.foo;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
public class NextActionOverlay extends Overlay {
    private final Client client;
    private final EasyArceuusRunecraftingPlugin plugin;
    private final EasyArceuusRunecraftingConfig config;

    @Inject
    private NextActionOverlay(
            Client client, EasyArceuusRunecraftingPlugin plugin, EasyArceuusRunecraftingConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isPlayerInArea()) {return null;}
        switch (plugin.getNextOtherClick()) {
            case 1:
                highlightWorldPoints(graphics, 1720, 3854, 5, 5); // between dark altar and blood altar
                break;
            case 2:
                highlightWorldPoints(graphics, 1751, 3873, 5, 5); // between dark altar and essence
                break;
            case 3:
                Widget runOrb = client.getWidget(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB);
                if (runOrb != null) {
                    OverlayUtil.renderPolygon(graphics, runOrb.getBounds(), plugin.currentHighlightColor(), plugin.currentHighlightColor(), new BasicStroke());
                }
                break;
            case 4: // first segment from dark altar to soul altar
                highlightWorldPoints(graphics, 1764, 3895, 4, 4);
                break;
            case 5: // second segment from dark altar to soul altar
                highlightWorldPoints(graphics, 1808, 3890, 4, 4);
                break;
            case 6: // third segment from dark altar to soul altar
                highlightWorldPoints(graphics, 1826, 3874, 4, 4);
                break;
            default:
                renderNextAction(graphics, plugin.getNextActionClick());
                break;
        }
        return null;
    }

    private void highlightWorldPoints(Graphics2D graphics, int SWX, int SWY, int w, int h) {
        WorldPoint ploc = client.getLocalPlayer().getWorldLocation(); // How else can I make these?
        WorldPoint origin = ploc.dx(-1 * ploc.getX()).dy(-1 * ploc.getY()); // How else can I make these?
        WorldPoint SWP = origin.dx(SWX).dy(SWY); // How else can I make these?

        int dx = 0;
        while (dx < w) {
            int dy = 0;
            while (dy < h) {
                highlightWorldPoint(graphics, SWP.dx(dx).dy(dy));
                dy+=1;
            }
            dx+=1;
        }
    }

    private void highlightWorldPoint(Graphics2D graphics, WorldPoint wp) {
        final LocalPoint lp = LocalPoint.fromWorld(client, wp);
        if (lp == null) {return;}
        final Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null) {return;}
        Color color = plugin.currentHighlightColor();
        OverlayUtil.renderPolygon(graphics, poly, color, color, new BasicStroke((float) 1));
    }

    private void renderNextAction(Graphics2D graphics, TileObject tileObject)
    {
        if (tileObject != null) {
            if (config.actionEnabled()) {
                Shape clickbox = tileObject.getClickbox();
                Point mousePosition = client.getMouseCanvasPosition();
                Color color = plugin.currentHighlightColor();
                OverlayUtil.renderHoverableArea(graphics, clickbox, mousePosition, color, color, color);
            }
        }
    }
}
