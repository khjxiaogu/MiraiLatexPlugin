package com.khjxiaogu.LatexPlugin;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

import javax.imageio.ImageIO;

import kotlin.text.Charsets;


public class LatexRenderer {
	public static File temp_dir=new File("./temp/");
	public static long transferTo(InputStream in,OutputStream out) throws IOException {
        Objects.requireNonNull(out, "out");
        long transferred = 0;
        byte[] buffer = new byte[2048];
        int read;
        while ((read = in.read(buffer, 0, 2048)) >= 0) {
            out.write(buffer, 0, read);
            transferred += read;
        }
        return transferred;
    }
	public static void main(String[] args) {
		render("啊啊啊");
	}
	private static boolean deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
        for (File file : allContents) {
            file.delete();
        }
	    return directoryToBeDeleted.delete();
	}
	public static BufferedImage render(String latex) {
		return render(latex,null);
	}
	public static BufferedImage render(String latex,String pack) {
		String fn = "lx"+new Date().getTime(); // for New22.tex
		File cfn=new File(temp_dir,fn);
		cfn.mkdirs();
		File all=new File(cfn,fn);
		// 1. Prepare the .tex file
		String newLineWithSeparation ="\r\n";
		String math = "";
		math += "\\documentclass[border=0.50001bp,varwidth]{standalone}"+ newLineWithSeparation;
		math += "\\usepackage[english]{babel}\r\n" + 
				"\\usepackage[UTF8]{ctex}\r\n" +
				"\\usepackage{amsmath,amssymb,amsfonts}\r\n" + 
				"\\usepackage{multicol}\r\n" + 
				"\\usepackage{float}\r\n" + 
				"\\usepackage{color,xcolor}\r\n" + 
				"\\usepackage{cite}\r\n" + 
				"\\usepackage{url}\r\n" + 
				"\\usepackage{ifthen}\r\n" + 
				"\\usepackage{multicol}\r\n" + 
				"\\usepackage{float}\r\n" + 
				"\\usepackage{color,xcolor}\r\n" + 
				"\\usepackage{algorithm}\r\n" + 
				"\\usepackage{algorithmic}\r\n" + 
				"\\usepackage{graphicx}\r\n" + 
				"\\usepackage{subfigure}\r\n" + 
				"\\usepackage{circuitikz}\r\n" +
				"\\usepackage{mathtools}\r\n" + 
				"\\usepackage{etoolbox}\r\n";
		if(pack!=null) {
			math+="\\usepackage{"+pack+"}\r\n";
		}
		math += "\\begin{document}" + newLineWithSeparation;
		math +=latex+ newLineWithSeparation;
		math += "\\end{document}";
		// 2. Create the .tex file
		try(FileOutputStream writer= new FileOutputStream(all.getAbsoluteFile()+".tex")){
			writer.write(math.getBytes(StandardCharsets.UTF_16));
			writer.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// 3. Execute LaTeX from command line to generate picture
		ProcessBuilder pb = new ProcessBuilder("xelatex","-halt-on-error","-shell-escape", all.getAbsolutePath()+ ".tex");
		//magick -antialias  D:\_tmp\New22.pdf   D:\_tmp\New22.png
		pb.directory(new File(cfn.getAbsolutePath()+"\\"));
		ByteArrayOutputStream err=new ByteArrayOutputStream();

		try {
			Process p = pb.start();
			transferTo(p.getInputStream(),err);
			transferTo(p.getErrorStream(),err);
			p.waitFor();
			if(!new File(all.getAbsolutePath()+".pdf").exists()) {
				String[] lines=err.toString().split("\n");
				StringBuilder errb=new StringBuilder();
				boolean errc=false;
				for(String line:lines) {
					if(errc||line.trim().startsWith("!")) {
						errc=true;
						errb.append(line);
					}
				}
				throw new IllegalArgumentException(errb.toString());
			}
			Process p2 =Runtime.getRuntime().exec("magick -density 300 \""+all.getAbsolutePath()+".pdf\" -fill white -opaque none -colorspace RGB \""+all.getAbsolutePath()+".jpg\"");
			transferTo(p2.getInputStream(),System.out);
			transferTo(p2.getErrorStream(),System.out);
			p2.waitFor();
			try {
				BufferedImage bi=ImageIO.read(new File(all.getAbsoluteFile()+".jpg"));
				return bi;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} catch (IOException | InterruptedException ex) {
			ex.printStackTrace();
			
		}finally {
			deleteDirectory(cfn);
		}
		return null;
		

		

	}
}
