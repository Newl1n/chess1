package model;

import java.awt.*;

import activity.Settings;

/**
 * 这个类主要用于包装Color对象，用于Chess游戏使用。
 */
public enum ChessColor {
    BLACK("Black", Color.BLACK), WHITE("White", Color.WHITE), NONE("No Player", Color.WHITE);

    private final String name;
    private final Color color;


    ChessColor(String name, Color color) {
        this.name = name;
        if(color==Color.BLACK&&Settings.gameGraphKind==1){
             this.color=Color.BLUE;
        }else if(color==Color.WHITE&&Settings.gameGraphKind==1){
            this.color=Color.YELLOW;
        }else{
            this.color=color;
        }
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

}
