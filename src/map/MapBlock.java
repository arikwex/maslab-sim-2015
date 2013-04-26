package map;

import core.Config.BlockColor;

public class MapBlock extends Point {

    private BlockColor color;

    public MapBlock(Point p, BlockColor color) {
        this(p.x, p.y, color);
    }

    public MapBlock(double x, double y, BlockColor color) {
        super(x, y);
        this.color = color;
    }

    public BlockColor getColor() {
        return this.color;
    }

}
