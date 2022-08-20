package cn.konfan.clearentity.task;

import cn.konfan.clearentity.ClearEntity;
import cn.konfan.clearentity.utils.Version;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class VersionScanner implements Runnable {

    @Override
    public void run() {
        try {
            URL url = new URL("https://bstats.org/api/v1/plugins/14080/charts/pluginVersion/data");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
            InputStream inputStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String versionJson = reader.readLine();
            reader.close();
            double pluginVersion = Integer.parseInt(ClearEntity.getInstance().getDescription().getVersion().replaceAll("\\.", ""));
            Type list = new TypeToken<List<Version>>() {
            }.getType();
            List<Version> versions = new Gson().fromJson(versionJson, list);


            for (Version version : versions) {
                double serverVersion = Integer.parseInt(version.getName().replaceAll("\\.", ""));
                if (serverVersion > pluginVersion) {
                    ClearEntity.getInstance().getServer().getConsoleSender().sendMessage(ClearEntity.convertColor("&b[ClearEntity] &e您的插件版本过低，请前往mcbbs更新插件！"));
                    ClearEntity.getInstance().getServer().getConsoleSender().sendMessage(ClearEntity.convertColor("&b[ClearEntity]&c https://www.mcbbs.net/thread-1283396-1-1.html"));
                    return;
                }
            }
        } catch (Exception ignore) {
            //
        }
    }
}
