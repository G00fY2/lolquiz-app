package com.g00fy2.lolquiz.riotapi.staticdata;

import java.util.List;

public class ChampionSpellDto {
    //This object contains champion spell data.

    public List<ImageDto> altimages;
    public List<Double> cooldown;
    public String cooldownBurn;
    public List<Integer> cost;
    public String costBurn;
    public String costType;
    public String Description;
    public List<Object> effect; //This field is a List of List of Double.
    public List<String> effectBurn;
    public ImageDto	image;
    public String key;
    public LevelTipDto leveltip;
    public int maxrank;
    public String name;
    public List<Object>	range;	//This field is either a List of Integer or the String 'self' for spells that target one's own champion.
    public String rangeBurn;
    public String resource;
    public String sanitizedDescription;
    public String sanitizedTooltip;
    public String tooltip;
    public List<SpellVarsDto> vars;

}
