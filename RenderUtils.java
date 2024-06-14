package de.torui.coflsky.gui.bingui.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.awt.*;


/**
 * Created by ForBai
 */
public class RenderUtils {
    public static Minecraft mc = Minecraft.getMinecraft();

    //draw a rectangle
    public static void drawRect(float x, float y, float width, float height, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y + height);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x + width, y);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }


    //draws an outlined rectangle with a given color and size and a given line width
    public static void drawRectOutline(int x, int y, int width, int height, float lineWidth, Color color) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        setColor(color);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + width, y);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x, y + height);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    //draws a circle with a given radius and thickness
    public static void drawCircle(int x, int y, int radius, Color color) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        setColor(color);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2d(x, y);
        for (int i = 0; i <= 360; i++) {
            GL11.glVertex2d(x + Math.sin(i * Math.PI / 180) * radius, y + Math.cos(i * Math.PI / 180) * radius);
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    //draws a circle outline with a given radius and thickness
    public static void drawCircleOutline(int x, int y, float radius, float thickness, Color color) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        setColor(color);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i <= 360; i++) {
            GL11.glVertex2d(x + Math.sin(i * Math.PI / 180) * radius, y + Math.cos(i * Math.PI / 180) * radius);
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    //draws line from x1,y1 to x2,y2 with a given color and thickness
    public static void drawLine(float x1, float y1, float x2, float y2, float thickness, Color color) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        setColor(color);
        GL11.glLineWidth(thickness);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    //draws a triangle from x1,y1 to x2,y2 to x3,y3
    public static void drawTriangle(int x, int y, int x2, int y2, int x3, int y3, Color color) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        setColor(color);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x3, y3);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    //draws a triangle outline from x1,y1 to x2,y2 to x3,y3
    public static void drawTriangleOutline(int x, int y, int x2, int y2, int x3, int y3, Color color) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        setColor(color);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x3, y3);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    //draws an arc with a given radius, start angle, and end angle
    public static void drawArc(int x, int y, int radius, int startAngle, int endAngle, Color color) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        setColor(color);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2d(x, y);
        for (int i = startAngle; i <= endAngle; i++) {
            GL11.glVertex2d(x + Math.sin(i * Math.PI / 180) * radius, y + Math.cos(i * Math.PI / 180) * radius);
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }


    //draw a loading circle with a given radius, thickness, and speed
    public static void drawLoadingCircle(float x, float y, float radius, float thickness, float speed, Color color) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        setColor(color);
        GL11.glLineWidth(thickness);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= 360; i++) {
            GL11.glVertex2d(x + Math.sin((i + speed) * Math.PI / 180) * radius, y + Math.cos((i + speed) * Math.PI / 180) * radius);
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    //draws a rounded rectangle with a given radius and color and size
    public static void drawRoundedRect(int x, int y, int width, int height, int radius, @NotNull Color color) {

        //draw the two rectangles
        drawRect(x + radius, y, width - radius * 2, height, color.getRGB());
        drawRect(x, y + radius, radius, height - radius * 2, color.getRGB());
        drawRect(x + width - radius, y + radius, radius, height - radius * 2, color.getRGB());

        //drawRect(x + radius, y, width - radius - radius, height, color.getRGB());
        //drawRect(x, y + radius, width, height - radius - radius, color.getRGB());
        //draw the circles
        drawArc(x + radius, y + radius, radius, 180, 270, color);
        drawArc(x + width - radius, y + radius, radius, 90, 180, color);
        drawArc(x + radius, y + height - radius, radius, 270, 360, color);
        drawArc(x + width - radius, y + height - radius, radius, 0, 90, color);

        //drawCircle(x + radius, y + radius, radius, color);
        //drawCircle(x + width - radius, y + radius, radius, color);
        //drawCircle(x + radius, y + height - radius, radius, color);
        //drawCircle(x + width - radius, y + height - radius, radius, color);

        //drawRectOutline(x, y, width, height, 1, Color.GREEN);
    }

    //draws a gradient rectangle with a given color and size
    public static void drawGradientRect(int x, int y, int width, int height, Color color1, Color color2) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);
        setColor(color1);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + width, y);
        setColor(color2);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x, y + height);
        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }


    public static void drawString(String text, int x, int y, Color color) {
        setColor(color);
        mc.fontRendererObj.drawString(text, x, y, color.getRGB());
    }

    public static void drawStringWithShadow(String text, int x, int y, Color color) {
        setColor(color);
        mc.fontRendererObj.drawStringWithShadow(text, x, y, color.getRGB());
    }

    public static void drawCenteredString(String text, int x, int y, Color color) {
        setColor(color);
        mc.fontRendererObj.drawString(text, x - mc.fontRendererObj.getStringWidth(text) / 2, y, color.getRGB());
    }

    public static void drawCenteredStringWithShadow(String text, int x, int y, Color color) {
        setColor(color);
        mc.fontRendererObj.drawStringWithShadow(text, x - mc.fontRendererObj.getStringWidth(text) / 2, y, color.getRGB());
    }

    //draws a string with custom scale
    public static void drawString(String text, int x, int y, Color color, int scale) {
        setColor(color);
        FontRenderer fr = mc.fontRendererObj;
        fr.drawString(text, x, y, color.getRGB());
    }

    //draws a string with custom scale and shadow
    public static void drawStringWithShadow(String text, int x, int y, Color color, int scale) {
        setColor(color);
        FontRenderer fr = mc.fontRendererObj;
        fr.drawStringWithShadow(text, x, y, color.getRGB());
    }

    public static void drawCenteredString(String text, int x, int y, Color color, int scale) {
        setColor(color);
        FontRenderer fr = mc.fontRendererObj;
        fr.drawString(text, x - fr.getStringWidth(text) / 2, y, color.getRGB());
    }

    public static void drawCenteredStringWithShadow(String text, int x, int y, Color color, int scale) {
        setColor(color);
        FontRenderer fr = mc.fontRendererObj;
        fr.drawStringWithShadow(text, x - fr.getStringWidth(text) / 2, y, color.getRGB());
    }

    public static void drawCenteredStringWithShadow(String text, int x, int y, Color color, int scale, boolean centered) {
        setColor(color);
        FontRenderer fr = mc.fontRendererObj;
        if (centered) {
            fr.drawStringWithShadow(text, x - fr.getStringWidth(text) / 2, y, color.getRGB());
        } else {
            fr.drawStringWithShadow(text, x, y, color.getRGB());
        }
    }

    //draws an ItemStack at a given position with a given scale
    public static void drawItemStack(ItemStack itemStack, int x, int y, float scale) {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);
        mc.getRenderItem().renderItemIntoGUI(itemStack, x, y);
        GL11.glPopMatrix();
    }

    public static void drawItemStackWithText(ItemStack stack, int x, int y, String text) {
        if (stack == null) return;
        RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
        setColor(Color.WHITE);
        RenderHelper.enableGUIStandardItemLighting();
        itemRender.zLevel = -145;
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, stack, x, y, text);
        itemRender.zLevel = 0;
        RenderHelper.disableStandardItemLighting();
    }

    public static void drawItemStack(ItemStack stack, int x, int y) {
        drawItemStackWithText(stack, x, y, null);
    }

    public static void drawItemStack(ItemStack itemStack, int x, int y, float scaleX, float scaleY) {
        GL11.glPushMatrix();
        GL11.glScalef(scaleX, scaleY, 0);
        drawItemStack(itemStack, x, y);
        GL11.glPopMatrix();
    }

    //draw centered ItemStack at a given position with a given scale
    public static void drawCenteredItemStack(ItemStack itemStack, int x, int y, float scale) {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);
        drawItemStack(itemStack, (int) (x - (scale / 2)), (int) (y - (scale / 2)));
        GL11.glPopMatrix();
    }

    //draw a check mar with a given color and size using lines
    public static void drawCheckMark(int x, int y, int size, Color color) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2);
        setColor(color);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(x, y + size / 2);
        GL11.glVertex2d(x + size / 2, y + size);
        GL11.glVertex2d(x + size / 2, y + size);
        GL11.glVertex2d(x + size, y);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    //draw a cross mark with a given color and start and end points
    public static void drawCrossMark(int x, int y, int x2, int y2, Color color) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2);
        setColor(color);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y2);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }


    //set alpha of color
    public static Color setAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    //set color
    public static void setColor(int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void setColor(Color color) {
        setColor(color.getRGB());
    }

    //rotate
    public static void rotate(float angle) {
        GL11.glRotatef(angle, 0.0F, 0.0F, 1.0F);
    }
}

//This Java class RenderUtils provides a collection of static methods that utilize LWJGL (Lightweight Java Game Library) and Minecraft's rendering capabilities to draw various graphical elements on the screen. Here’s an overview of its functionalities:

//Class Overview
//RenderUtils Class
//Imports:

//net.minecraft.client.Minecraft: Provides access to the Minecraft client instance.
//net.minecraft.client.gui.FontRenderer: Allows rendering text.
//net.minecraft.client.renderer.RenderItem: Renders items (like ItemStacks) in the GUI.
//net.minecraft.item.ItemStack: Represents an item stack in Minecraft.
//org.jetbrains.annotations.NotNull: Annotation indicating that a parameter, field, or method return value cannot be null.
//Methods:

//Drawing Shapes:

//drawRect(float x, float y, float width, float height, int color): Draws a filled rectangle with the specified color.
//drawRectOutline(int x, int y, int width, int height, float lineWidth, Color color): Draws an outlined rectangle with a specified line width and color.
//drawCircle(int x, int y, int radius, Color color): Draws a filled circle.
//drawCircleOutline(int x, int y, float radius, float thickness, Color color): Draws an outlined circle.
//drawLine(float x1, float y1, float x2, float y2, float thickness, Color color): Draws a line segment between two points.
//drawTriangle(int x, int y, int x2, int y2, int x3, int y3, Color color): Draws a filled triangle.
//drawTriangleOutline(int x, int y, int x2, int y2, int x3, int y3, Color color): Draws an outlined triangle.
//drawArc(int x, int y, int radius, int startAngle, int endAngle, Color color): Draws a filled arc of a circle.
//drawLoadingCircle(float x, float y, float radius, float thickness, float speed, Color color): Draws a loading circle animation.
//drawRoundedRect(int x, int y, int width, int height, int radius, @NotNull Color color): Draws a rounded rectangle with rounded corners.
//drawGradientRect(int x, int y, int width, int height, Color color1, Color color2): Draws a gradient-filled rectangle.
//Text Rendering:

//Methods for rendering text:
//drawString, drawStringWithShadow, drawCenteredString, drawCenteredStringWithShadow: Render text at specified positions with different styles and scales.
//Item Rendering:

//drawItemStack(ItemStack itemStack, int x, int y, float scale): Renders an ItemStack (an item icon) at a given position with scaling.
//drawItemStackWithText(ItemStack stack, int x, int y, String text): Renders an ItemStack with an optional overlay text.
//drawCenteredItemStack(ItemStack itemStack, int x, int y, float scale): Renders an ItemStack centered at a given position with scaling.
//Miscellaneous:

//drawCheckMark(int x, int y, int size, Color color): Draws a check mark shape.
//drawCrossMark(int x, int y, int x2, int y2, Color color): Draws a cross mark shape.
//setAlpha(Color color, int alpha): Sets the alpha transparency of a Color object.
//setColor(int color) and setColor(Color color): Set the current drawing color using RGBA values.
//rotate(float angle): Rotates subsequent drawing operations by a specified angle.
//Usage
//This class is typically used in Minecraft mods or plugins (like Forge mods) that require custom graphical user interfaces (GUIs) or visual elements.
//It leverages LWJGL for OpenGL-based rendering directly onto the Minecraft game screen.
//Methods like drawString, drawRect, drawItemStack are particularly useful for customizing UI elements or displaying information in-game.

//Summary
//RenderUtils encapsulates various OpenGL-based rendering methods tailored for Minecraft's GUI system. It provides convenient static methods to draw shapes, text, items, and more, making it easier to create custom UI elements and visual effects within the game environment.
