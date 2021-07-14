package com.khjxiaogu.LatexPlugin;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.khjxiaogu.LatexPlugin.PluginData;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

// TODO: Auto-generated Javadoc
/**
 * 插件主类
 * @author khjxiaogu
 * file: MiraiSongPlugin.java
 * time: 2020年8月26日
 */
public class Main extends JavaPlugin{
	public Main() {
		super(new JvmPluginDescriptionBuilder(PluginData.id,PluginData.version).name(PluginData.name).author(PluginData.author).info(PluginData.info).build());
	}
	public static void TimedExecute(Runnable r) throws Exception{
		TimedExecute(r,10);
	}
	public static void TimedExecute(Runnable r,int sec) throws Exception{
		Future<?> obj = null;
		ExecutorService sexec = Executors.newSingleThreadExecutor();
		try {
			obj=sexec.submit(r);
			obj.get(sec,TimeUnit.SECONDS);
		}catch (TimeoutException e) {
			obj.cancel(true);
			
			throw e;
		}
	}
	@Override
	public void onEnable() {
		CommandUtils.commands.put("#latex",(event,args)->{
			String toexec=String.join(CommandUtils.spliter, Arrays.copyOfRange(args, 1, args.length));
				try {
					TimedExecute(()->{
						try {
							Utils.getRealSender(event).sendMessage(Utils.getRealSender(event).uploadImage(Utils.ImageResource(LatexRenderer.render("$"+toexec+"$"))));
						}catch(IllegalArgumentException err) {
							Utils.getRealSender(event).sendMessage(err.getMessage());
						}
					},60);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		});
		CommandUtils.commands.put("#tex",(event,args)->{
			String toexec=String.join(CommandUtils.spliter, Arrays.copyOfRange(args, 1, args.length));
				try {
					TimedExecute(()->{
						try {
							Utils.getRealSender(event).sendMessage(Utils.getRealSender(event).uploadImage(Utils.ImageResource(LatexRenderer.render(toexec))));
						}catch(IllegalArgumentException err) {
							Utils.getRealSender(event).sendMessage(err.getMessage());
						}
					},60);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		});
		CommandUtils.commands.put("#texp",(event,args)->{
			String toexec=String.join(CommandUtils.spliter, Arrays.copyOfRange(args, 2, args.length));
				try {
					TimedExecute(()->{
						try {
							Utils.getRealSender(event).sendMessage(Utils.getRealSender(event).uploadImage(Utils.ImageResource(LatexRenderer.render(toexec,args[1]))));
						}catch(IllegalArgumentException err) {
							Utils.getRealSender(event).sendMessage(err.getMessage());
						}
					},60);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		});
		CommandUtils.register();
		getLogger().info("插件加载完毕!");
	}

}
