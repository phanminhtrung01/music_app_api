package com.example.music_app_api.model.source_song;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import org.eclipse.persistence.oxm.annotations.XmlCDATA;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class StreamSourceSong {
    @XmlCDATA
    @XmlElement(name = "location", required = true)
    @JsonAlias({"128", "source128"})
    private String uri128;
    @XmlCDATA
    @XmlElement(name = "locationHQ")
    @JsonAlias({"320", "source320"})
    private String uri320;
    @JsonAlias({"lossless", "sourceLossless"})
    private String uriLossless;
}
