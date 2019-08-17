package cn.music;

import java.io.File;

import static cn.utils.MusicPaser.parseMusic;

public class MainClass {
    public static void main(String[] args) {
        /**
         *
         */
        String inFolder="E:\\音乐相关\\云音乐缓存\\Cache";

        String outfolder="E:\\音乐相关\\云音乐缓存\\music";
        /**
         * 创建输出目录
         */
        File outf = new File(outfolder+"/noname");
        if (!outf.exists()){
            outf.mkdirs();
        }

        File folder= new File(inFolder);
        File[] files = folder.listFiles();
        for (File inFile : files) {
            String[] sps = inFile.getName().split("\\.");
            if ("uc".equals(sps[sps.length-1])){
                System.out.println(inFile.getName());
                File outFile = new File(outfolder+"/noname/"+inFile.getName().replace(".uc",".mp3"));
                String songName = parseMusic(inFile, outFile, outfolder + "/");
                if (songName!=null){
                    outFile.renameTo(new File(outfolder +"/"+ songName));
                }

            }
        }


    }
}
