package com.aldawoudy.fallingwords;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Ismail on 3/12/16.
 */
public class Word {

    @JsonProperty("text_eng")
    private String mEnglish;

    @JsonProperty("text_spa")
    private String mSpanish;

    public String getEnglish() {
        return mEnglish;
    }

    public String getSpanish() {
        return mSpanish;
    }
}
