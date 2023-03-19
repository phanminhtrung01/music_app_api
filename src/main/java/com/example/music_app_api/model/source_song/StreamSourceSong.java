package com.example.music_app_api.model.source_song;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import org.eclipse.persistence.oxm.annotations.XmlCDATA;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class StreamSourceSong {
    @XmlCDATA
    @XmlElement(name = "location", required = true)
    @JsonProperty(value = "128", required = true)
    private String uri128;
    @XmlCDATA
    @XmlElement(name = "locationHQ")
    @JsonProperty(value = "320")
    private String uri320;
}
