package com.singularitycoder.newstime.more;

public final class MoreItem {

    private String title;
    private String subtitle;
    private int icon;
    private int titleColor;
    private int subtitleColor;
    private int iconColor;
    private String version;

    public MoreItem() {
    }

    // Header
    public MoreItem(String version) {
        this.version = version;
    }

    public MoreItem(String title, String subtitle, int icon, int titleColor, int subtitleColor, int iconColor) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
        this.titleColor = titleColor;
        this.subtitleColor = subtitleColor;
        this.iconColor = iconColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public int getSubtitleColor() {
        return subtitleColor;
    }

    public void setSubtitleColor(int subtitleColor) {
        this.subtitleColor = subtitleColor;
    }

    public int getIconColor() {
        return iconColor;
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
