package com.moses.miiread;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moses.miiread.utils.MD5Utils;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 * <p>
 * See [testing documentation](http://d.android.com/tools/testing).
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void fixReleaseInfos(){
        getFileMD5();
        copyAllLastedReleasedApk();
        deleteAllReleaseFiles();
    }

    @Test
    public void getFileMD5() {
        String name = "MiiRead";
        String version = "1.02.01";

        ArrayList<File> releaseApks = new ArrayList<>();
        String outputParent = "/users/donmoses/documents/project/shumi/project/android/MiiReader/app";
        String[] channelFolders = new String[]{
                "/anzhi",
                "/baidu",
                "/coolapk",
                "/google_play",
                "/huawei",
                "/internal",
                "/official",
                "/oppo",
                "/ppzhushou",
                "/safe360",
                "/vivo",
                "/xiaomi",
                "/yingyongbao"
        };
        String releaseFolder = "/release";

        for (String channelFolder : channelFolders) {
            File rF = new File(outputParent + channelFolder + releaseFolder);
            if(!rF.exists())
                continue;
            for (File file : rF.listFiles()) {
                String fileName = file.getName();
                if (fileName.endsWith("release.apk") && fileName.contains(name) && fileName.contains(version)) {
                    releaseApks.add(file);
                    break;
                }
            }
        }

        for (File releaseApk : releaseApks) {
            String md5 = MD5Utils.getFileMD5(releaseApk);
            System.out.println(releaseApk.getName() + ":: " + md5);
        }

    }

    @Test
    public void joinBookSource() {
        File sourceFolder = new File("/Users/donmoses/Documents/project/shumi/releaseRes/booksource");
        File source_275 = new File(sourceFolder, "source_275.txt");
        File source_joined = new File(sourceFolder, "source_joined.txt");
        try {
            JsonArray jsonArray = new JsonArray();
            {
                //使用275源作为基础源
                String source275JsonS = getFileS(source_275);
                JsonArray source275Array = new JsonParser().parse(source275JsonS).getAsJsonArray();
                for (JsonElement element : source275Array) {
                    if (!isSourceExist(jsonArray, element.getAsJsonObject()))
                        jsonArray.add(element);
                }
            }
            //将其他源添加到基础源中
            File[] files = sourceFolder.listFiles();
            for (File file : files) {
                if (!file.getName().endsWith(".txt"))
                    continue;
                if (file.compareTo(source_275) == 0 || file.compareTo(source_joined) == 0)
                    continue;
                String sourceJsonS = getFileS(file);
                JsonArray sourceArray = new JsonParser().parse(sourceJsonS).getAsJsonArray();
                for (JsonElement element : sourceArray) {
                    if (!isSourceExist(jsonArray, element.getAsJsonObject()))
                        jsonArray.add(element);
                }
            }
            writeJsonIntoFile(source_joined, jsonArray.toString());

        } catch (Exception ignore) {

        }

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void writeJsonIntoFile(File file, String json) {
        try {
            if (file.exists())
                file.delete();
            file.createNewFile();
            try (FileWriter writer = new FileWriter(file);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(json);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isSourceExist(JsonArray array, JsonObject compObj) {
        for (JsonElement jsonElement : array) {
            JsonObject obj = jsonElement.getAsJsonObject();
            if (obj.get("bookSourceUrl").getAsString().compareTo(compObj.get("bookSourceUrl").getAsString()) == 0)
                return true;
        }
        return false;
    }

    private String getFileS(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    @Test
    public void deleteAllReleaseFiles() {
        String outputParent = "/users/donmoses/documents/project/shumi/project/android/MiiReader/app";
        String[] channelFolders = new String[]{
                "/anzhi",
                "/baidu",
                "/coolapk",
                "/google_play",
                "/huawei",
                "/internal",
                "/official",
                "/oppo",
                "/ppzhushou",
                "/safe360",
                "/vivo",
                "/xiaomi",
                "/yingyongbao"
        };
        for (String channel : channelFolders) {
            File file = new File(outputParent + channel);
            if (!deleteFile(file)) {
                System.out.println("删除 " + channel + "   !! 失败");
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean deleteFile(File file){
        if(!file.exists())
            return true;
        if(!file.isDirectory())
        {
            return file.delete();
        }
        File[] files = file.listFiles();
        for (File file1 : files) {
            if(!file1.isDirectory())
            {
                file1.delete();
                continue;
            }
            deleteFile(file1);
        }
        return file.delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void copyAllLastedReleasedApk() {
        String name = "MiiRead";
        String version = "1.02.01";

        String outputParent = "/users/donmoses/documents/project/shumi/project/android/MiiReader/app";
        String allReleaseFolder = "/users/donmoses/documents/project/shumi/allReleaseApks";
        File allReleaseFile = new File(allReleaseFolder);
        if (allReleaseFile.exists())
            deleteFile(allReleaseFile);
        allReleaseFile.mkdirs();
        String[] channelFolders = new String[]{
                "/anzhi",
                "/baidu",
                "/coolapk",
                "/google_play",
                "/huawei",
                "/internal",
                "/official",
                "/oppo",
                "/ppzhushou",
                "/safe360",
                "/vivo",
                "/xiaomi",
                "/yingyongbao"
        };
        String releaseFolder = "/release";

        for (String channelFolder : channelFolders) {
            File rF = new File(outputParent + channelFolder + releaseFolder);
            if(!rF.exists())
                continue;
            for (File file : rF.listFiles()) {
                String fileName = file.getName();
                if (fileName.endsWith("release.apk") && fileName.contains(name) && fileName.contains(version)) {
                    try {
                        copyFile(file, new File(allReleaseFile, fileName));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    private void copyFile(File sourceFile, File targetFile)
            throws IOException {
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        //关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }
}