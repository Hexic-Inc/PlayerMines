package org.hexic.playermines.data.yml.refactored;

import org.hexic.playermines.data.manager.annotations.Comment;
import org.hexic.playermines.data.manager.annotations.Filename;
import org.hexic.playermines.data.manager.annotations.Section;

import java.util.HashMap;
import java.util.Map;

public class GUI {

    @Filename
    public final String fileName = "Gui";

    public GUI(){

    }

    @Section(section = "Blocks-Gui")
    @Comment(comment = "test")
    public Map<String,String> getBlocksGui(){
        Map<String, String> contents = new HashMap<>();
        contents.put("Display_Name", "&8Mine Contents");
        contents.put("Size", "54");
        contents.put("Contents", "[{1,5;$mine-blocks}, {1,x;$blank}, {x,9;$blank}, {6,x;$blank}, {x,1;$blank}");
        return contents;
    }



}
