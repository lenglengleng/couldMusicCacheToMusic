package cn.utils;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import java.io.*;
import java.util.HashMap;

public class MusicPaser {
    /**
     * 0->歌手+歌名     1->歌名+歌手
     */
    public static int NAME_MODE = 1;

    /**
     * 缓存转换为mp3
     * @param inFile
     * @param outFile
     * @param outfolder
     * @return
     */
    public static String parseMusic(File inFile,File outFile,String outfolder) {
        String newMusicPath=null;
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(inFile));
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(outFile));
            byte[] by = new byte[2048];
            int len;
            while ((len = dis.read(by)) != -1) {
                for (int i = 0; i < len; i++) {
                    by[i] ^= 0xa3;
                }
                dos.write(by, 0, len);
            }
            newMusicPath = getSongName(outFile);
            dos.close();
            dis.close();

        } catch (IOException ioe) {
            System.err.println("憨批!又错了吧");
            System.err.println(ioe);
        }
        return newMusicPath;
    }

    /**从源文件信息中
     * get音乐文件夹的全名
     * @param file
     * @return
     */
    private static String getSongName(File file) {

        String musicName = null;
        try {
            MP3File mp3File = (MP3File) AudioFileIO.read(file);
            ID3v24Tag id3v2Tag = mp3File.getID3v2TagAsv24();
            AbstractID3v2Tag id3v2TagAsv24 = mp3File.getID3v2Tag();

            HashMap<String, Object> tagMap = id3v2Tag != null ? id3v2Tag.frameMap : id3v2TagAsv24 != null ? id3v2TagAsv24.frameMap : null;

            if (tagMap == null) {
                return null;
            }
            String thisSongerName = getField(tagMap, "TPE1");
            String thisMusicName = getField(tagMap, "TIT2");
            if (thisMusicName==null){
                thisMusicName=getField(tagMap,"TIT2");
            }
            if (thisSongerName==null){
                thisSongerName=getField(tagMap,"TALB");
            }

            if (thisMusicName == null || thisSongerName == null) {
                if (thisMusicName == null && thisSongerName != null) {
                    musicName = thisSongerName + ".mp3";
                } else if (thisSongerName == null && thisMusicName != null) {
                    musicName = thisMusicName + ".mp3";
                } else {
                    musicName = null;
                    System.err.println(tagMap.toString());
                }
            } else {
                switch (NAME_MODE) {
                    case 1://歌名+歌手
                        musicName = thisSongerName + " - " + thisMusicName + ".mp3";
                        break;
                    default://歌手+歌名
                        musicName = thisMusicName + " - " + thisSongerName + ".mp3";
                }
            }

            //封装到music对象
//            music = new Music(songname, artist, album, duration, filePath);

        } catch (NullPointerException ie) {
            System.err.println("文件信息读取失败");
            ie.printStackTrace();
        } catch (Exception e) {
            System.err.println("文件读取失败！" + e);
            e.printStackTrace();
        } finally {
            return musicName;
        }

    }

    /**
     * 从music文件的map拿到对应字段的信息,对异常的处理,取不到返回null
     * @param tagMap
     * @param key
     * @return
     */
    private static String getField(HashMap<String, Object> tagMap, String key) {
        String filed = null;
        try {
            filed = tagMap.get(key).toString().split("\"")[1].trim();
        } catch (NullPointerException ie) {

        } finally {
            return filed;
        }
    }
}