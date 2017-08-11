package org.jj.simpleweb;

import org.springframework.data.annotation.Id;

public class Data {
    @Id
    public String key;

    public String value;
}
