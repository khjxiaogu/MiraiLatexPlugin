package com.khjxiaogu.LatexPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import net.mamoe.mirai.console.MiraiConsole;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.event.events.StrangerMessageEvent;

public class CommandUtils {
	public static Map<String, BiConsumer<MessageEvent, String[]>> commands = new ConcurrentHashMap<>();
	public final static String spliter = " ";
	public static void register(){
		GlobalEventChannel.INSTANCE.registerListenerHost(new SimpleListenerHost(MiraiConsole.INSTANCE.getCoroutineContext()) {
			@EventHandler
			public void onGroup(GroupMessageEvent event) {
				String[] args = Utils.getPlainText(event.getMessage()).split(spliter);
				BiConsumer<MessageEvent, String[]> exec = commands.get(args[0]);
				if (exec != null)
					exec.accept(event, args);
			}

			@EventHandler
			public void onFriend(FriendMessageEvent event) {
				String[] args = Utils.getPlainText(event.getMessage()).split(spliter);
				BiConsumer<MessageEvent, String[]> exec = commands.get(args[0]);
				if (exec != null)
					exec.accept(event, args);
			}

			@EventHandler
			public void onTemp(StrangerMessageEvent event) {
				String[] args = Utils.getPlainText(event.getMessage()).split(spliter);
				BiConsumer<MessageEvent, String[]> exec = commands.get(args[0]);
				if (exec != null)
					exec.accept(event, args);
			}
		});
	}
}
