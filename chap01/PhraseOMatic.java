package chap01;

public class PhraseOMatic {
    public static void main (String[] args) {
        String[][] wordMatrix = {
            {
                "24/7", "multi-Tier", "30,000 foot", "B-to-B",
                "win-win", "frontend", "web-based", "pervasive", "smart",
                "six-sigma", "critical-path", "dynamic"
            },
            {
                "empowered", "sticky", "value-added", "oriented",
                "centric", "distributed", "clustered", "branded", "outside-the-box",
                "positioned", "networked", "focused", "leveraged", "aligned", "targeted",
                "shared", "accelerated"
            },
            {
                "process", "tipping-point", "solution", "architecture",
                "core competency", "strategy", "mindshare", "portal", "space", "vision",
                "paradigm", "mission"
            }};

        String phrase = "";

        for (int n = 0; n < wordMatrix.length; ++n) 
            phrase = String.format("%s %s", 
                    phrase, wordMatrix[n][(int) (Math.random() * wordMatrix[n].length)]);

        System.out.format("What we need is a %s.\n", phrase);
    }
}
