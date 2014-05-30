package com.lion328.knot.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil
{
  private static final int  BUFFER_SIZE = 4096;

  private static void extractFile(ZipInputStream in, File outdir, String name) throws IOException
  {
    byte[] buffer = new byte[BUFFER_SIZE];
    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outdir,name)));
    int count = -1;
    while ((count = in.read(buffer)) != -1)
      out.write(buffer, 0, count);
    out.close();
  }

  private static void mkdirs(File outdir,String path)
  {
    File d = new File(outdir, path);
    if( !d.exists() )
      d.mkdirs();
  }

  private static String dirpart(String name)
  {
    int s = name.lastIndexOf( File.separatorChar );
    return s == -1 ? null : name.substring( 0, s );
  }

  /***
   * Extract zipfile to outdir with complete directory structure
   * @param zipfile Input .zip file
   * @param outdir Output directory
   */
  public static void extract(File zipfile, File outdir)
  {
    try
    {
      ZipInputStream zin = new ZipInputStream(new FileInputStream(zipfile));
      ZipEntry entry;
      String name, dir;
      while ((entry = zin.getNextEntry()) != null)
      {
        name = entry.getName();
        if( entry.isDirectory() )
        {
          mkdirs(outdir,name);
          continue;
        }
        /* this part is necessary because file entry can come before
         * directory entry where is file located
         * i.e.:
         *   /foo/foo.txt
         *   /foo/
         */
        dir = dirpart(name);
        if( dir != null )
          mkdirs(outdir,dir);

        extractFile(zin, outdir, name);
      }
      zin.close();
    } 
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void createZipFile(File to, File basepath, File[] src) throws IOException {
	  ZipOutputStream out = new ZipOutputStream(new FileOutputStream(to));
	  for(int i = 0; i < src.length; i++) {
		  if(src[i].isDirectory()) continue;
		  String filename = basepath.toPath().relativize(src[i].toPath()).toString().replace("\\", "/");
		  FileInputStream in = new FileInputStream(src[i]);
		  out.putNextEntry(new ZipEntry(filename));
		  byte[] buffer = new byte[1024];
		  int count;
		  while((count = in.read(buffer)) > 0) {
			  out.write(buffer, 0, count);
		  }
		  out.closeEntry();
		  in.close();
	  }
	  out.close();
  }
  
  public static void createJarFile(File to, File basepath, File[] src) throws IOException {
	  JarOutputStream out = new JarOutputStream(new FileOutputStream(to));
	  for(int i = 0; i < src.length; i++) {
		  if(src[i].isDirectory()) continue;
		  String filename = basepath.toPath().relativize(src[i].toPath()).toString().replace("\\", "/");
		  FileInputStream in = new FileInputStream(src[i]);
		  out.putNextEntry(new JarEntry(filename));
		  byte[] buffer = new byte[1024];
		  int count;
		  while((count = in.read(buffer)) > 0) {
			  out.write(buffer, 0, count);
		  }
		  out.closeEntry();
		  in.close();
	  }
	  out.close();
  }
  
}