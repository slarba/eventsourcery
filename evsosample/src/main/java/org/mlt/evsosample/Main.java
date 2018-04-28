package org.mlt.evsosample;

import static spark.Spark.get;

public class Main {
    public static void main(String[] args) {
        get("/hello", (req,resp) -> "Hello");
    }
}
