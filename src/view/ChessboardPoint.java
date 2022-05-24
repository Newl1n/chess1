package view;

/**
 * 这个类表示棋盘上的位置，如(0, 0), (0, 7)等等
 * 其中，左上角是(0, 0)，左下角是(7, 0)，右上角是(0, 7)，右下角是(7, 7)
 */
public class ChessboardPoint {
    private int x, y;

    public ChessboardPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "("+x + ","+y+") " ;
    }


    //该方法用于判断棋子是否越界棋盘
    public ChessboardPoint offset(int dx, int dy) {
        if (this.x + dx <= 7 && this.x + dx >= 0 && this.y + dy <= 7 && this.y + dy >= 0) {
            return new ChessboardPoint(x + dx, y + dy);
        } else {
            return null;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessboardPoint point = (ChessboardPoint) o;
        return x == point.x && y == point.y;
    }

    //克隆位置
    public ChessboardPoint getCopy(){
        return new ChessboardPoint(this.x, this.y);
    }

}
