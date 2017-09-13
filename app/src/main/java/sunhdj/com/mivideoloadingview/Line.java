package sunhdj.com.mivideoloadingview;

/**
 * Created by huangdaju on 2017/9/13.
 */

public class Line {
    int width;
    public int startX,startY;
    public int endX,endY;
    String color;

    public Line(int width, int startX, int startY, int endX, int endY, String color) {
        this.width = width;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.color = color;
    }

}
