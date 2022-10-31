package com.example.myapplication;

public class AppCarbonInfo {
    double youtubeCarbonEmit;
    double netflixCarbonEmit;
    double googleMeetCarbonEmit;
    double zoomCarbonEmit;
    double skypeCarbonEmit;
    double tiKTokCarbonEmit;
    double facebookCarbonEmit;
    double internetCarbonEmit;
    double instagramCarbonEmit;
    double kakaoTalkCarbonEmit;

    int youtubeTimeMinute;
    int netflixTimeMinute;
    int googleMeetTimeMinute;
    int zoomTimeMinute;
    int skypeTimeMinute;
    int tiKTokTimeMinute;
    int facebookTimeMinute;
    int internetTimeMinute;
    int instagramTimeMinute;
    int kakaoTalkTimeMinute;

    public AppCarbonInfo()
    {
        this.youtubeCarbonEmit = 0;
        this.netflixCarbonEmit = 0;
        this.googleMeetCarbonEmit = 0;
        this.zoomCarbonEmit = 0;
        this.skypeCarbonEmit = 0;
        this.tiKTokCarbonEmit = 0;
        this.facebookCarbonEmit = 0;
        this.internetCarbonEmit = 0;
        this.instagramCarbonEmit = 0;
        this.kakaoTalkCarbonEmit = 0;

        this.youtubeTimeMinute = 0;
        this.netflixTimeMinute = 0;
        this.googleMeetTimeMinute = 0;
        this.zoomTimeMinute = 0;
        this.skypeTimeMinute = 0;
        this.tiKTokTimeMinute = 0;
        this.facebookTimeMinute = 0;
        this.internetTimeMinute = 0;
        this.instagramTimeMinute = 0;
        this.kakaoTalkTimeMinute = 0;
    }

    public double getTotalCarbonEmit()
    {
        return this.youtubeCarbonEmit + this.netflixCarbonEmit + this.googleMeetCarbonEmit + this.zoomCarbonEmit + this.skypeCarbonEmit + this.tiKTokCarbonEmit + this.facebookCarbonEmit + this.internetCarbonEmit + this.instagramCarbonEmit + this.kakaoTalkCarbonEmit;
    }

    public int getTotalTimeMinute()
    {
        return this.youtubeTimeMinute + this.netflixTimeMinute + this.googleMeetTimeMinute + this.zoomTimeMinute + this.skypeTimeMinute + this.tiKTokTimeMinute + this.facebookTimeMinute + this.internetTimeMinute + this.instagramTimeMinute + this.kakaoTalkTimeMinute;
    }
}