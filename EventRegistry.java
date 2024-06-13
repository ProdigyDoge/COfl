package de.torui.coflsky.handlers;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.realmsclient.util.Pair;
import de.torui.coflsky.CoflSky;
import de.torui.coflsky.WSCommandHandler;
import de.torui.coflsky.commands.Command;
import de.torui.coflsky.commands.CommandType;
import de.torui.coflsky.commands.JsonStringCommand;
import de.torui.coflsky.commands.models.AuctionData;
import de.torui.coflsky.commands.models.FlipData;
import de.torui.coflsky.configuration.Configuration;
import de.torui.coflsky.network.WSClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static de.torui.coflsky.CoflSky.config;
import static de.torui.coflsky.handlers.DescriptionHandler.*;
import static de.torui.coflsky.handlers.EventHandler.*;

public class EventRegistry {
    public static Pattern chatpattern = Pattern.compile("a^", Pattern.CASE_INSENSITIVE);
    public final ExecutorService chatThreadPool = Executors.newFixedThreadPool(2);
    public final ExecutorService tickThreadPool = Executors.newFixedThreadPool(2);

    @SubscribeEvent
    public void onDisconnectedFromServerEvent(ClientDisconnectionFromServerEvent event) {
        if (CoflSky.Wrapper.isRunning) {
            System.out.println("Disconnected from server");
            CoflSky.Wrapper.stop();
            System.out.println("CoflSky stopped");
        }
    }

    public static long LastClick = System.currentTimeMillis();
    public static Boolean LastHotkeyState;
    private DescriptionHandler descriptionHandler;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyEvent(KeyInputEvent event) {

        if (LastHotkeyState != null && Keyboard.getEventKeyState() == LastHotkeyState) {
            return;
        }
        LastHotkeyState = Keyboard.getEventKeyState();
        onAfterKeyPressed();
    }

    public static void onAfterKeyPressed() {
        if (CoflSky.keyBindings[0].isPressed()) {
            if (WSCommandHandler.lastOnClickEvent != null) {
                FlipData f = WSCommandHandler.flipHandler.fds.GetLastFlip();
                if (f != null) {
                    WSCommandHandler.Execute("/cofl openauctiongui " + f.Id + " false",
                            Minecraft.getMinecraft().thePlayer);
                }
            }

        }
        if (CoflSky.keyBindings[1].isKeyDown()) {
            if ((System.currentTimeMillis() - LastClick) >= 300) {

                FlipData f = WSCommandHandler.flipHandler.fds.GetHighestFlip();

                if (f != null) {
                    WSCommandHandler.Execute("/cofl openauctiongui " + f.Id + " true",
                            Minecraft.getMinecraft().thePlayer);
                    EventRegistry.LastViewAuctionUUID = f.Id;
                    EventRegistry.LastViewAuctionInvocation = System.currentTimeMillis();
                    LastClick = System.currentTimeMillis();
                    String command = WSClient.gson.toJson("/viewauction " + f.Id);

                    CoflSky.Wrapper.SendMessage(new JsonStringCommand(CommandType.Clicked, command));
                    WSCommandHandler.Execute("/cofl track besthotkey " + f.Id, Minecraft.getMinecraft().thePlayer);
                } else {
                    // only display message once (if this is the key down event)
                    if (CoflSky.keyBindings[1].isPressed())
                        WSCommandHandler.Execute("/cofl dialog nobestflip", Minecraft.getMinecraft().thePlayer);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    // @SubscribeEvent
    public void DrawOntoGUI(RenderGameOverlayEvent rgoe) {

        if (rgoe.type == ElementType.CROSSHAIRS) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.ingameGUI.drawString(Minecraft.getMinecraft().fontRendererObj,
                    "Flips in Pipeline:" + WSCommandHandler.flipHandler.fds.CurrentFlips(), 0, 0, Integer.MAX_VALUE);
        }
    }

    public static String ExtractUuidFromInventory(IInventory inventory) {

        ItemStack stack = inventory.getStackInSlot(13);
        if (stack != null) {
            try {
                String uuid = stack.serializeNBT().getCompoundTag("tag").getCompoundTag("ExtraAttributes")
                        .getString("uuid");
                if (uuid.length() == 0) {
                    throw new Exception();
                }
                System.out.println("Item has the UUID: " + uuid);
                return uuid;
            } catch (Exception e) {
                System.out.println(
                        "Clicked item " + stack.getDisplayName() + " has the following meta: " + stack.serializeNBT());
            }
        }
        return "";
    }

    public static ItemStack GOLD_NUGGET = new ItemStack(
            Item.itemRegistry.getObject(new ResourceLocation("minecraft:gold_nugget")));

    public static final Pair<String, Pair<String, LocalDateTime>> EMPTY = Pair.of(null, Pair.of("", LocalDateTime.MIN));
    public static Pair<String, Pair<String, LocalDateTime>> last = EMPTY;
    private LocalDateTime lastBatchStart = LocalDateTime.now();
    private LinkedBlockingQueue<String> chatBatch = new LinkedBlockingQueue<String>();

    @SubscribeEvent
    public void HandleChatEvent(ClientChatReceivedEvent sce) {
        if (!CoflSky.Wrapper.isRunning || !Configuration.getInstance().collectChat)
            return;
        chatThreadPool.submit(() -> {
            try {

                String msg = sce.message.getUnformattedText();
                Matcher matcher = chatpattern.matcher(msg);
                boolean matchFound = matcher.find();
                if (!matchFound)
                    return;

                chatBatch.add(msg);
                // add 500ms to the last batch start time
                long nanoSeconds = 500_000_000;
                if (!lastBatchStart.plusNanos(nanoSeconds).isBefore(LocalDateTime.now())) {
                    System.out.println(msg + " was not sent because it was too soon");
                    return;
                }
                lastBatchStart = LocalDateTime.now();

                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Sending batch of " + chatBatch.size() + " messages");
                        Command<String[]> data = new Command<>(CommandType.chatBatch, chatBatch.toArray(new String[0]));
                        chatBatch.clear();
                        CoflSky.Wrapper.SendMessage(data);
                    }
                }, 500);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static long lastStartTime = Long.MIN_VALUE;

    public static long LastViewAuctionInvocation = Long.MIN_VALUE;
    public static String LastViewAuctionUUID = null;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void OnGuiClick(GuiScreenEvent.MouseInputEvent mie) {
        if (!CoflSky.Wrapper.isRunning)
            return;
        if (!(mie.gui instanceof GuiChest))
            return; // verify that it's really a chest
        if (!(((GuiChest) mie.gui).inventorySlots instanceof ContainerChest))
            return;
        ContainerChest chest = (ContainerChest) ((GuiChest) mie.gui).inventorySlots;
        IInventory inv = chest.getLowerChestInventory();
        if (inv.hasCustomName()) { // verify that the chest actually has a custom name
            String chestName = inv.getName();
            if (chestName.equalsIgnoreCase("BIN Auction View")) {

                ItemStack heldItem = Minecraft.getMinecraft().thePlayer.inventory.getItemStack();

                if (heldItem != null) {
                    System.out.println("Clicked on: " + heldItem.getItem().getRegistryName());

                    String itemUUID = ExtractUuidFromInventory(inv);

                    if (System.currentTimeMillis() > lastStartTime) {

                        if (heldItem.isItemEqual(GOLD_NUGGET)) {
                            AuctionData ad = new AuctionData();
                            ad.setItemId(itemUUID);

                            if ((LastViewAuctionInvocation + 60 * 1000) >= System.currentTimeMillis()) {
                                ad.setAuctionId(LastViewAuctionUUID);
                            } else {
                                ad.setAuctionId("");
                            }

                            Command<AuctionData> data = new Command<>(CommandType.PurchaseStart, ad);
                            CoflSky.Wrapper.SendMessage(data);
                            System.out.println("PurchaseStart");
                            last = Pair.of("You claimed ", Pair.of(itemUUID, LocalDateTime.now()));
                            lastStartTime = System.currentTimeMillis() + 200 /* ensure a small debounce */;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void OnRenderTick(TickEvent.RenderTickEvent event) {
        de.torui.coflsky.CountdownTimer.onRenderTick(event);
    }

    long UpdateThisTick = 0;

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onTick(TickEvent.ClientTickEvent event) {
        UpdateThisTick++;
        if (UpdateThisTick % 200 == 0) {
            tickThreadPool.submit(() -> {
                try {
                    ScoreboardData();
                    TabMenuData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiOpen(GuiOpenEvent event) {

        // if gui is null, a gui was closed
        // therefore clear the lastClickFlipMessage, so it doesn't show on other
        // auctions
        if (event.gui == null) {
            WSCommandHandler.flipHandler.lastClickedFlipMessage = "";
        }

        if (!config.extendedtooltips)
            return;
        if (descriptionHandler != null)
            descriptionHandler.Close();
        if (event.gui == null)
            emptyTooltipData();

        if (!(event.gui instanceof GuiContainer))
            return;
        new Thread(() -> {
            try {
                descriptionHandler = new DescriptionHandler();
                descriptionHandler.loadDescriptionAndListenForChanges(event);
            } catch (Exception e) {
                System.out.println("failed to update description " + e);
            }
        }).start();
    }

    @SubscribeEvent
    public void onBackgroundRenderDone(GuiScreenEvent.BackgroundDrawnEvent event) {
        if(descriptionHandler != null)
            descriptionHandler.highlightSlots(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemTooltipEvent(ItemTooltipEvent event) {
        if (!config.extendedtooltips)
            return;
        if (descriptionHandler == null)
            return;
        descriptionHandler.setTooltips(event);
    }
}

// The EventRegistry class in this Java code is designed to handle various Minecraft Forge events, specifically tailored for a mod named "CoflSky." This class handles chat events, key events, GUI interactions, and more. Here's a detailed breakdown of what each part of the code does:

// Fields
// chatpattern: A regex pattern, currently set to match nothing ("a^").
// chatThreadPool and tickThreadPool: Thread pools for handling chat and tick events concurrently.
// LastClick: Stores the timestamp of the last click event.
// LastHotkeyState: Tracks the state of the last hotkey event.
// descriptionHandler: An instance of DescriptionHandler, used for managing item descriptions.
// GOLD_NUGGET: Represents an item stack of a gold nugget.
// EMPTY: A constant representing an empty state for tracking the last action.
// last, lastBatchStart, chatBatch: Used for managing batches of chat messages.
// lastStartTime, LastViewAuctionInvocation, LastViewAuctionUUID: Track the timing and UUIDs of the last viewed auctions.
// UpdateThisTick: Used to control the frequency of tick updates.
// Methods and Event Handlers
// onDisconnectedFromServerEvent(ClientDisconnectionFromServerEvent event):

// Handles disconnection from the server by stopping the CoflSky wrapper if it is running.
// onKeyEvent(KeyInputEvent event):

// Handles key events, ensuring no repeated actions if the state hasn't changed.
// Calls onAfterKeyPressed() for further processing.
// onAfterKeyPressed():

// Handles specific key bindings for opening auction GUIs based on the last or highest flip data.
// Sends commands to the server to open auction GUIs and track actions.
// DrawOntoGUI(RenderGameOverlayEvent rgoe):

// Draws text onto the GUI overlay, specifically displaying the number of flips in the pipeline if the crosshairs are being rendered.
// ExtractUuidFromInventory(IInventory inventory):

// Extracts a UUID from the inventory item in slot 13, used for identifying items in auctions.
// HandleChatEvent(ClientChatReceivedEvent sce):

// Handles chat messages, batching them and sending them to the server if certain conditions are met.
// OnGuiClick(GuiScreenEvent.MouseInputEvent mie):

// Handles clicks within GUIs, specifically interacting with the "BIN Auction View" chest to initiate purchases or other actions.
// OnRenderTick(TickEvent.RenderTickEvent event):

// Calls a method to handle render tick events.
// onTick(TickEvent.ClientTickEvent event):

// Periodically (every 200 ticks) submits tasks to update scoreboard and tab menu data.
// onGuiOpen(GuiOpenEvent event):

// Clears the last clicked flip message when a GUI is closed.
// Manages extended tooltips and loads descriptions for items when a GUI is opened.
// onBackgroundRenderDone(GuiScreenEvent.BackgroundDrawnEvent event):

// Highlights slots in the GUI based on the description handler.
// onItemTooltipEvent(ItemTooltipEvent event):

// Sets tooltips for items based on the description handler if extended tooltips are enabled.
// Functionality Overview
// Threading: Uses thread pools to handle chat and tick events concurrently, improving performance and responsiveness.
// Event Handling: Subscribes to various Minecraft Forge events to respond to chat messages, GUI interactions, key presses, and render ticks.
// Auction Interaction: Provides functionality to interact with auctions, including opening auction GUIs and tracking the last viewed auction.
// GUI Management: Handles drawing additional information onto the GUI, such as the number of flips in the pipeline and item descriptions.
// Configuration: Integrates with the mod's configuration settings to enable or disable features like extended tooltips.
// This class is central to managing the mod's interaction with the Minecraft client, providing a robust framework for handling events and performing actions based on those events.
