package com.thatmg393.esmanager.data;

public class ModProperties {
    public String modName;
    public String modDesc;
    public String modAuthor;
    public String modVersion;
    public String modPreviewPath;
    public boolean noMods;
    
    public ModProperties(String name, String desc, String author, String version, String previewPath, boolean noMods) {
        this.modName = name;
        this.modDesc = desc;
        this.modAuthor = author;
        this.modVersion = version;
        this.modPreviewPath = previewPath;
        this.noMods = noMods;
    }
    
    public String getName() {
        return modName;
    }
    
    public String getDesc() {
        return modDesc;
    }
    
    public String getAuthor() {
        return modAuthor;
    }
    
    public String getVersion() {
        return modVersion;
    }
    
    public String getPreviewImgPath() {
        return modPreviewPath;
    }
    
    public boolean getNoMods() {
        return noMods;
    }
}
