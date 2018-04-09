package com.bitloor.ggloor.model;

import java.util.Date;

/**
 * Created by ssaan on 22.05.2017.
 **/

public class Matches {
    public Integer id;
    public Teams team1;
    public Teams team2;
    public Date dateMatch;
    public Integer status;
    public Integer colGames;
    public boolean notify;
    public String streamUrl;
}
