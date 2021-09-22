package org.hexic.playermines.data.yml;

import org.hexic.playermines.PlayerMines;
import org.hexic.playermines.managers.data.Config;

public class YmlConfig {

    private static Config config;

    public YmlConfig(){
        config = PlayerMines.getDataManager().getConfig("Config.yml");
    }

    public void createConfigs(){
        createGeneralSettings();
        config.saveConfig();
    }

    private void createGeneralSettings(){
        String section = "general-settings";
        config.createSection(section);
        config.setListValue(section,"World-Name", "pmine");
        config.setListValue(section,"Area-Size","300,300");
        config.setListValue(section,"Default-Schem", "default");
        config.setListValue(section,"Mine-Coords", "{25,100,25}, {-25,50,-25}");
        config.setListValue(section,"Mine-Size","50,50,50");
        config.setListValue(section,"Mine-Contents","[{99;stone}, {1;end_stone}]");
    }

    public String getWorldName(){return config.getString("general-settings" + ".World-Name");}

    public String getPmineSize(){return config.getString("general-settings" + ".Mine-Size");}

    public String getPmineContents(){return config.getString("general-settings" + ".Mine-Contents");}

    public String getAreaSize(){return config.getString("general-settings" + ".Area-Size");}

    public String getDefaultSchem(){return config.getString("general-settings" + ".Default-Schem");}

    public String getMineCoords(){return config.getString("general-settings" + ".Mine-Coords");}


}
