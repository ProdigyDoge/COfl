package de.torui.coflsky;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.torui.coflsky.commands.Command;
import de.torui.coflsky.commands.CommandType;
import de.torui.coflsky.commands.JsonStringCommand;
import de.torui.coflsky.commands.RawCommand;
import de.torui.coflsky.commands.models.*;
import de.torui.coflsky.configuration.ConfigurationManager;
import de.torui.coflsky.handlers.EventRegistry;
import de.torui.coflsky.proxy.ProxyManager;
import de.torui.coflsky.utils.FileUtils;
import de.torui.coflsky.commands.models.TimerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.File;

import java.util.Arrays;
import java.util.stream.Stream;

public class WSCommandHandler {

    public static transient String lastOnClickEvent;
    public static FlipHandler flipHandler = new FlipHandler();
    private static final ModListData modListData = new ModListData();
    private static final Gson gson = new Gson();
    private static final ProxyManager proxyManager = new ProxyManager();

    public static boolean HandleCommand(JsonStringCommand cmd, Entity sender) {
        System.out.println("Handling Command=" + cmd.toString());

        switch (cmd.getType()) {
            case WriteToChat:
                WriteToChat(cmd.GetAs(new TypeToken<ChatMessageData>() {
                }));
                break;
            case Execute:
                Execute(cmd.GetAs(new TypeToken<String>() {
                }), sender);
                break;
            case PlaySound:
                SoundData sc = cmd.GetAs(new TypeToken<SoundData>() {
                }).getData();
                PlaySound(sc.Name, sc.Pitch);
                break;
            case ChatMessage:
                ChatMessage(cmd.GetAs(new TypeToken<ChatMessageData[]>() {
                }));
                break;
            case Flip:
                Flip(cmd.GetAs(new TypeToken<FlipData>() {
                }));
                break;
            case PrivacySettings:
                new ConfigurationManager().UpdateConfiguration(cmd.getData());
            case Countdown:
                StartTimer(cmd.GetAs(new TypeToken<TimerData>() {
                }));
                break;
            case GetMods:
                getMods();
                break;
            case ProxyRequest:
                handleProxyRequest(cmd.GetAs(new TypeToken<ProxyRequest[]>() {
                }).getData());
                break;
            default:
                break;
        }

        return true;
    }

    private static void Flip(Command<FlipData> cmd) {
        //handle chat message
        ChatMessageData[] messages = cmd.getData().Messages;
        SoundData sound = cmd.getData().Sound;
        if (sound != null && sound.Name != null) {
            PlaySound(sound.Name, sound.Pitch);
        }
        Command<ChatMessageData[]> showCmd = new Command<ChatMessageData[]>(CommandType.ChatMessage, messages);
        ChatMessage(showCmd);
        flipHandler.fds.Insert(cmd.getData());
        // trigger the onAfterHotkeyPressed function to open the flip if the correct hotkey is currently still pressed
        EventRegistry.onAfterKeyPressed();
    }

    private static void handleProxyRequest(ProxyRequest[] request) {
        for (ProxyRequest req : request) {
            proxyManager.handleRequestAsync(req);
        }
    }

    public static void cacheMods() {
        File modFolder = new File(Minecraft.getMinecraft().mcDataDir, "mods");
        for (File mods : modFolder.listFiles()) {
            modListData.addFilename(mods.getName());
            try {
                modListData.addFileHashes(FileUtils.getMD5Checksum(mods));
            } catch (Exception exception) {
                // Highly less likely to happen unless something goes wrong
                exception.printStackTrace();
            }
        }

        for (ModContainer mod : Loader.instance().getModList()) {
            modListData.addModname(mod.getName());
            modListData.addModname(mod.getModId());
        }
    }

    private static void getMods() {
        // the Cofl server has asked for an mod list now let's respond with all the info
        CoflSky.Wrapper.SendMessage(new RawCommand("foundMods", gson.toJson(modListData)));
    }

    private static void PlaySound(String soundName, float pitch) {
        SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();

        // random.explode
        PositionedSoundRecord psr = PositionedSoundRecord
                .create(new ResourceLocation(soundName), pitch);

        handler.playSound(psr);
    }

    private static void Execute(Command<String> cmd, Entity sender) {
        System.out.println("Execute: " + cmd.getData() + " sender:" + sender);
        //String dummy = WSClient.gson.fromJson(cmd.getData(), String.class);
        Execute(cmd.getData(), sender);
    }

    /**
     * Starts a countdown
     */
    private static void StartTimer(Command<TimerData> cmd) {
        de.torui.coflsky.CountdownTimer.startCountdown(cmd.getData());
    }

    public static void Execute(String cmd, Entity sender) {
        if (cmd.startsWith("/cofl") || cmd.startsWith("http")) {
            ClientCommandHandler.instance.executeCommand(sender, cmd);
        } else {
            Minecraft.getMinecraft().thePlayer.sendChatMessage(cmd);
        }
    }


    private static IChatComponent CommandToChatComponent(ChatMessageData wcmd, String fullMessage) {
        if (wcmd.OnClick != null) {
            if (wcmd.Text != null && wcmd.OnClick.startsWith("/viewauction")) {
                lastOnClickEvent = "/cofl openauctiongui " + wcmd.OnClick.split(" ")[1] + " false";
            } else {
                lastOnClickEvent = "/cofl callback " + wcmd.OnClick;
            }
        }
        if (wcmd.Text != null) {
            IChatComponent comp = new ChatComponentText(wcmd.Text);

            ChatStyle style;
            if (wcmd.OnClick != null) {
                if (wcmd.OnClick.startsWith("http")) {
                    style = new ChatStyle().setChatClickEvent(new ClickEvent(Action.OPEN_URL, wcmd.OnClick));
                } else {
                    style = new ChatStyle()
                            .setChatClickEvent(new ClickEvent(Action.RUN_COMMAND, lastOnClickEvent));
                }
                comp.setChatStyle(style);
            }

            if (wcmd.Hover != null && !wcmd.Hover.isEmpty()) {
                if (comp.getChatStyle() == null)
                    comp.setChatStyle(new ChatStyle());
                comp.getChatStyle().setChatHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(wcmd.Hover)));
            }
            return comp;
        }
        return null;
    }

    public static void sendChatMessage(IChatComponent message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(message);
    }

    public static IChatComponent ChatMessage(Command<ChatMessageData[]> cmd) {
        ChatMessageData[] list = cmd.getData();

        IChatComponent master = new ChatComponentText("");
        String fullMessage = ChatMessageDataToString(list);

        for (ChatMessageData wcmd : list) {
            IChatComponent comp = CommandToChatComponent(wcmd, fullMessage);
            if (comp != null)
                master.appendSibling(comp);
        }
        Minecraft.getMinecraft().thePlayer.addChatMessage(master);
        return master;
    }


    private static void WriteToChat(Command<ChatMessageData> cmd) {
        ChatMessageData wcmd = cmd.getData();

        IChatComponent comp = CommandToChatComponent(wcmd, wcmd.Text);
        if (comp != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(comp);
        }

    }

    public static String ChatMessageDataToString(ChatMessageData[] messages) {
        Stream<String> stream = Arrays.stream(messages).map(message -> message.Text);
        String s = String.join(",", stream.toArray(String[]::new));
        stream.close();
        return s;
    }

}

//This Java code appears to be part of a Minecraft mod or plugin, likely handling various commands and interactions within the game. Let's break down its functionality:

//Package and Imports
//The code is contained within the de.torui.coflsky package.
//Imports include several Gson-related classes, Minecraft client classes (Minecraft, Entity, SoundHandler, etc.), and utilities from the mod's own classes (Command, JsonStringCommand, RawCommand, etc.).
//Class WSCommandHandler
//Static Fields and Objects
//lastOnClickEvent: Stores the last click event processed.
//flipHandler: An instance of FlipHandler used for handling flip-related commands.
//modListData, gson, proxyManager: Objects for handling mod list data, JSON serialization, and proxy management respectively.
//HandleCommand Method
//Purpose: Executes different commands based on their type (JsonStringCommand).
//Switch Cases:
//WriteToChat: Writes a chat message.
//Execute: Executes a command.
//PlaySound: Plays a sound in the game.
//ChatMessage: Handles multiple chat messages.
//Flip: Initiates a flip action which involves showing chat messages and playing sounds.
//PrivacySettings: Updates configuration settings.
//Countdown: Starts a timer countdown.
//GetMods: Retrieves a list of mods.
//ProxyRequest: Handles proxy requests asynchronously.
//Other Methods
//Flip: Handles the specific actions required when flipping, including showing chat messages, playing sounds, and triggering events.
//handleProxyRequest: Processes proxy requests asynchronously.
//cacheMods: Caches information about installed mods.
//getMods: Sends information about installed mods to the server.
//PlaySound: Plays a specified sound with given pitch.
//Execute: Executes a command string, either as a client command or chat message.
//StartTimer: Starts a countdown timer.
//CommandToChatComponent: Converts a command data object into a Minecraft chat component with specified click and hover actions.
//sendChatMessage: Sends a chat message to the Minecraft client.
//ChatMessage: Constructs and sends chat messages based on provided command data.
//WriteToChat: Writes a single chat message to the client.
//Overall Functionality
//Command Handling: Processes various commands received from external sources or user input.
//Chat and Sound Handling: Manages sending chat messages, playing sounds, and interacting with chat components.
//Mod Management: Deals with retrieving and caching information about installed mods.
//Proxy Management: Handles proxy requests asynchronously.
//Event Handling: Triggers specific actions based on commands, such as opening GUIs or starting timers.
//Conclusion
//This code defines a comprehensive set of functionalities for handling commands and interactions within the Minecraft game environment, likely as part of a mod or plugin that extends the game's capabilities for server communication, sound management, mod information retrieval, and more. It integrates with Minecraft's client-side API to achieve these functionalities effectively.
