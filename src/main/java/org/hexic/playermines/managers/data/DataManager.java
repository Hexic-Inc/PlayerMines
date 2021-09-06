/*     */
package org.hexic.playermines.managers.data;
/*     */

import java.io.*;
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ import java.nio.charset.Charset;
/*     */ import org.bukkit.plugin.java.JavaPlugin;

/*     */
/*     */ public class DataManager {
    /*     */
    public DataManager(JavaPlugin pl) {
        /*  16 */
        this.pl = pl;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */   private JavaPlugin pl;

    /*     */
    /*     */
    /*     */
    /*     */
    public Config getNewConfig(String filePath, String[] header) {
        /*  26 */
        File file = getConfigFile(filePath);
        /*     */
        /*  28 */
        if (!file.exists()) {
            /*  29 */
            prepareFile(filePath);
            /*     */
            /*  31 */
            if (header != null && header.length != 0) {
                /*  32 */
                setHeader(file, header);
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /*  37 */
        return new Config(file, getCommentsNum(file), this.pl);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean getFile(String filePath) {
        /*     */
        try {
            /*  43 */
            if (!this.pl.getDataFolder().exists()) {
                /*  44 */
                return false;
                /*     */
            }
            /*  46 */
            return true;
            /*     */
        }
        /*  48 */ catch (NullPointerException e) {
            /*  49 */
            return false;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public Config getNewConfig(String filePath) {
        /*  65 */
        return getNewConfig(filePath, null);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public File getConfigFile(String file) {
        /*     */
        File configFile;
        /*  75 */
        if (file.isEmpty() || file == null) {
            /*  76 */
            return null;
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /*  81 */
        if (file.contains("/")) {
            /*     */
            /*  83 */
            if (file.startsWith("/")) {
                /*  84 */
                configFile = new File(this.pl.getDataFolder() + file.replace("/", File.separator));
                /*     */
            } else {
                /*  86 */
                configFile = new File(this.pl.getDataFolder() + File.separator + file.replace("/", File.separator));
                /*     */
            }
            /*     */
        } else {
            /*     */
            /*  90 */
            configFile = new File(this.pl.getDataFolder(), file);
            /*     */
        }
        /*     */
        /*  93 */
        return configFile;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void prepareFile(String filePath, String resource) {
        /* 104 */
        File file = getConfigFile(filePath);
        /*     */
        /* 106 */
        if (file.exists()) {
            /*     */
            return;
            /*     */
        }
        /*     */
        /*     */
        try {
            /* 111 */
            file.getParentFile().mkdirs();
            /* 112 */
            file.createNewFile();
            /*     */
            /* 114 */
            if (resource != null && !resource.isEmpty()) {
                /* 115 */
                copyResource(this.pl.getResource(resource), file);
                /*     */
            }
            /*     */
        }
        /* 118 */ catch (IOException e) {
            /* 119 */
            e.printStackTrace();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void prepareFile(String filePath) {
        /* 129 */
        prepareFile(filePath, null);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void setHeader(File file, String[] header) {
        /* 139 */
        if (!file.exists()) {
            /*     */
            return;
            /*     */
        }
        /*     */
        /*     */
        /*     */
        try {
            /* 145 */
            StringBuilder config = new StringBuilder("");
            /* 146 */
            BufferedReader reader = new BufferedReader(new FileReader(file));
            /*     */
            String currentLine;
            /* 148 */
            while ((currentLine = reader.readLine()) != null) {
                /* 149 */
                config.append(currentLine + "\n");
                /*     */
            }
            /*     */
            /* 152 */
            reader.close();
            /* 153 */
            config.append("# +----------------------------------------------------+ #\n");
            /*     */
            /* 155 */
            for (String line : header) {
                /*     */
                /* 157 */
                if (line.length() <= 50) {
                    /*     */
                    /*     */
                    /*     */
                    /* 161 */
                    int lenght = (50 - line.length()) / 2;
                    /* 162 */
                    StringBuilder finalLine = new StringBuilder(line);
                    /*     */
                    /* 164 */
                    for (int i = 0; i < lenght; i++) {
                        /* 165 */
                        finalLine.append(" ");
                        /* 166 */
                        finalLine.reverse();
                        /* 167 */
                        finalLine.append(" ");
                        /* 168 */
                        finalLine.reverse();
                        /*     */
                    }
                    /*     */
                    /* 171 */
                    if (line.length() % 2 != 0) {
                        /* 172 */
                        finalLine.append(" ");
                        /*     */
                    }
                    /*     */
                    /* 175 */
                    config.append("# < " + finalLine.toString() + " > #\n");
                    /*     */
                }
                /*     */
            }
            /*     */
            /* 179 */
            config.append("# +----------------------------------------------------+ #");
            /*     */
            /* 181 */
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            /* 182 */
            writer.write(prepareConfigString(config.toString()));
            /* 183 */
            writer.flush();
            /* 184 */
            writer.close();
            /*     */
        }
        /* 186 */ catch (IOException e) {
            /* 187 */
            e.printStackTrace();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public InputStream getConfigContent(File file) {
        /* 199 */
        if (!file.exists()) {
            /* 200 */
            return null;
            /*     */
        }
        /*     */
        /*     */
        try {
            /* 204 */
            int commentNum = 0;
            /*     */
            /*     */
            /*     */
            /* 208 */
            String pluginName = getPluginName();
            /*     */
            /* 210 */
            StringBuilder whole = new StringBuilder("");
            /* 211 */
            BufferedReader reader = new BufferedReader(new FileReader(file));
            /*     */
            String currentLine;
            /* 213 */
            while ((currentLine = reader.readLine()) != null) {
                /*     */
                /* 215 */
                if (currentLine.startsWith("#")) {
                    /* 216 */
                    String addLine = currentLine.replaceFirst("#", pluginName + "_COMMENT_" + commentNum + ":");
                    /* 217 */
                    whole.append(addLine + "\n");
                    /* 218 */
                    commentNum++;
                    /*     */
                    continue;
                    /*     */
                }
                /* 221 */
                whole.append(currentLine + "\n");
                /*     */
            }
            /*     */
            /*     */
            /*     */
            /* 226 */
            String config = whole.toString();
            /* 227 */
            InputStream configStream = new ByteArrayInputStream(config.getBytes(Charset.forName("UTF-8")));
            /*     */
            /* 229 */
            reader.close();
            /* 230 */
            return configStream;
            /*     */
        }
        /* 232 */ catch (IOException e) {
            /* 233 */
            e.printStackTrace();
            /* 234 */
            return null;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    private int getCommentsNum(File file) {
        /* 246 */
        if (!file.exists()) {
            /* 247 */
            return 0;
            /*     */
        }
        /*     */
        /*     */
        try {
            /* 251 */
            int comments = 0;
            /*     */
            /*     */
            /* 254 */
            BufferedReader reader = new BufferedReader(new FileReader(file));
            /*     */
            String currentLine;
            /* 256 */
            while ((currentLine = reader.readLine()) != null) {
                /*     */
                /* 258 */
                if (currentLine.startsWith("#")) {
                    /* 259 */
                    comments++;
                    /*     */
                }
                /*     */
            }
            /*     */
            /*     */
            /* 264 */
            reader.close();
            /* 265 */
            return comments;
            /*     */
        }
        /* 267 */ catch (IOException e) {
            /* 268 */
            e.printStackTrace();
            /* 269 */
            return 0;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public InputStream getConfigContent(String filePath) {
        /* 280 */
        return getConfigContent(getConfigFile(filePath));
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    private String prepareConfigString(String configString) {
        /* 286 */
        int lastLine = 0;
        /* 287 */
        int headerLine = 0;
        /*     */
        /* 289 */
        String[] lines = configString.split("\n");
        /* 290 */
        StringBuilder config = new StringBuilder("");
        /*     */
        /* 292 */
        for (String line : lines) {
            /*     */
            /* 294 */
            if (line.startsWith(getPluginName() + "_COMMENT")) {
                /* 295 */
                String comment = "#" + line.trim().substring(line.indexOf(":") + 1);
                /*     */
                /* 297 */
                if (comment.startsWith("# +-")) {
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /* 305 */
                    if (headerLine == 0) {
                        /* 306 */
                        config.append(comment + "\n");
                        /*     */
                        /* 308 */
                        lastLine = 0;
                        /* 309 */
                        headerLine = 1;
                        /*     */
                    }
                    /* 311 */
                    else if (headerLine == 1) {
                        /* 312 */
                        config.append(comment + "\n\n");
                        /*     */
                        /* 314 */
                        lastLine = 0;
                        /* 315 */
                        headerLine = 0;
                        /*     */
                    }
                    /*     */
                } else {
                    /*     */
                    String normalComment;
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /* 328 */
                    if (comment.startsWith("# ' ")) {
                        /* 329 */
                        normalComment = comment.substring(0, comment.length() - 1).replaceFirst("# ' ", "# ");
                        /*     */
                    } else {
                        /* 331 */
                        normalComment = comment;
                        /*     */
                    }
                    /*     */
                    /* 334 */
                    if (lastLine == 0) {
                        /* 335 */
                        config.append(normalComment + "\n");
                        /* 336 */
                    } else if (lastLine == 1) {
                        /* 337 */
                        config.append("\n" + normalComment + "\n");
                        /*     */
                    }
                    /*     */
                    /* 340 */
                    lastLine = 0;
                    /*     */
                }
                /*     */
                /*     */
            } else {
                /*     */
                /* 345 */
                config.append(line + "\n");
                /* 346 */
                lastLine = 1;
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 351 */
        return config.toString();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void saveConfig(String configString, File file) {
        /* 362 */
        String configuration = prepareConfigString(configString);
        /*     */
        /*     */
        try {
            /* 365 */
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            /* 366 */
            writer.write(configuration);
            /* 367 */
            writer.flush();
            /* 368 */
            writer.close();
            /*     */
        }
        /* 370 */ catch (IOException e) {
            /* 371 */
            e.printStackTrace();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public String getPluginName() {
        /* 377 */
        return this.pl.getDescription().getName();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    private void copyResource(InputStream resource, File file) {
        /*     */
        try {
            /* 388 */
            OutputStream out = new FileOutputStream(file);
            /*     */
            /*     */
            /* 391 */
            byte[] buf = new byte[1024];
            /*     */
            int lenght;
            /* 393 */
            while ((lenght = resource.read(buf)) > 0) {
                /* 394 */
                out.write(buf, 0, lenght);
                /*     */
            }
            /*     */
            /* 397 */
            out.close();
            /* 398 */
            resource.close();
            /*     */
        }
        /* 400 */ catch (Exception e) {
            /* 401 */
            e.printStackTrace();
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              /Users/siiant/Desktop/PickaxeCheckRework.jar!/US/Siiant/PickaxeCheck/datamanager/dataManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */