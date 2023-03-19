package com.example.music_app_api.model.multi_search;

import com.example.music_app_api.model.source_song.StreamSourceSong;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "tracklist")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class TrackSong {
    @XmlElement(name = "track")
    private StreamSourceSong sourceSong;
}
