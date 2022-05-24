package org.hexic.playermines.data.yml;

import me.lucko.helper.menu.Gui;
import org.hexic.playermines.Main;

public class Data {

    private final GuiConfig guiConfig;

    public Data(){
        this.guiConfig = new GuiConfig();
    }

    public GuiConfig getGuiConfig(){
        return guiConfig;
    }
}
